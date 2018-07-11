/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The younic team (https://github.com/escv/younic)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package net.younic.content.internal;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Andre Albert
 *
 */
public class DOCx2HTLMHandler extends DefaultHandler {


	private StringBuilder data;
	private LinkedList<PrElement> rStack = new LinkedList<PrElement>();
	private final Set<String> inlineStyles = new HashSet<String>();
	private final Set<String> tableStyles = new HashSet<String>();
	private final Set<String> stackables = new HashSet<String>();
	
	public DOCx2HTLMHandler() {
		data = new StringBuilder();
		inlineStyles.add("b");
		inlineStyles.add("i");
		inlineStyles.add("u");
		
		tableStyles.add("tbl");
		tableStyles.add("tr");
		tableStyles.add("tc");
		
		stackables.add("p");
		stackables.add("tbl");
		stackables.add("tr");
		stackables.add("tc");
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("document".equals(localName)) {
			data.append("<div class=\"document\">");
		} else if (stackables.contains(localName)) {
			rStack.add(new PrElement(mapStyle(localName)));
		} else if ("r".equals(localName)) {
			rStack.getLast().push(new PrElement(mapStyle(localName)));
		} else if ("br".equals(localName)) {
			rStack.getLast().appendText("<br/>");
		} else if ("pStyle".equals(localName)) {
			String style = mapStyle(attributes.getValue("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "val"));
			rStack.getLast().addStyle(style);
		} else if (inlineStyles.contains(localName)) {
			rStack.getLast().peek().addStyle(localName);
		} else if (tableStyles.contains(localName)) {
			rStack.getLast().appendText("<"+mapStyle(localName)+">");
		}
	}
	
	private String mapStyle(final String style) {
		switch (style) {
		case "Heading1":
			return "h1";
		case "Heading2":
			return "h2";
		case "Heading3":
			return "h3";
		case "Heading4":
			return "h5";
		case "ListParagraph":
			return "li";
		case "tbl":
			return "table";
		case "tr":
			return "tr";
		case "tc":
			return "td";
		default:
			return style;
		}
	}
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("document".equals(localName)) {
			data.append("</div>");
		} else if (stackables.contains(localName) && rStack.size()==1) {
			data.append(rStack.removeLast().render()+"\r\n");
		} else if (stackables.contains(localName)) {
			String finalized = rStack.removeLast().render();
			rStack.getLast().appendText(finalized);
		}
		
	}

	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
        String chars = new String(ch, start, length);
        if (chars.length() > 0)
        {
        	this.rStack.getLast().appendText(chars);
        }
	}

	public String getMarkup() {
		String html = data.toString();
		
		// do some post processing here
		html = html.replace("<p><h1>", "<h1>");
		html = html.replace("</h1></p>", "</h1>");
		
		html = html.replace("<p><h2>", "<h2>");
		html = html.replace("</h2></p>", "</h2>");
		
		html = html.replace("<p><h3>", "<h3>");
		html = html.replace("</h3></p>", "</h3>");
		
		html = html.replace("<p><h4>", "<h4>");
		html = html.replace("</h4></p>", "</h4>");

		html = html.replace("<p><li>", "<li>");
		html = html.replace("</li></p>", "</li>");
		
		return html;
	}

	private static class PrElement {
		private String elem;
		private List<String> styles;
		private StringBuilder text;
		private LinkedList<PrElement> innerStack;
 
		private PrElement(String elem) {
			super();
			this.elem = elem;
			this.styles = new LinkedList<String>();
			this.innerStack = new LinkedList<PrElement>();
			this.text = new StringBuilder();
		}

		public void addStyle(String style) {
			this.styles.add(style);
		}
		public void appendText(String text) {
			if (innerStack.isEmpty()) {
				this.text.append(text);
			} else {
				this.innerStack.getLast().appendText(text);
			}
		}
		public void push(PrElement docElem) {
			this.innerStack.addLast(docElem);
		}
		public PrElement pop() {
			return this.innerStack.removeLast();
		}
		public PrElement peek() {
			if (innerStack.isEmpty()) {
				return new PrElement("nullobject");
			}
			return this.innerStack.getLast();
		}
		public String render() {
			StringBuilder r = new StringBuilder();
			if (elem!=null && !"r".equals(elem)) {
				r.append("<"+elem+">");
			}
			for (String style : styles) {
				r.append("<"+style+">");
			}
			for (PrElement docElement : innerStack) {
				r.append(docElement.render());
			}
			if (text.length() > 0) {
				r.append(this.text);
			}
			for (String style : styles) {
				r.append("</"+style+">");
			}
			if (elem!=null && !"r".equals(elem)) {
				r.append("</"+elem+">");
			}
			return r.toString();
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return this.elem;
		}
	}
}
