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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
	private LinkedList<StackBlockElement> rStack = new LinkedList<StackBlockElement>();
	private final Set<String> tableStyles = new HashSet<String>();
	private final Set<String> stackables = new HashSet<String>();
	private boolean previousList = false;
	private boolean isOrderedList = false;
	private boolean inRProps = false;
	
	public DOCx2HTLMHandler() {
		data = new StringBuilder();
		
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
			rStack.add(new StackBlockElement(mapStyle(localName)));
		} else if ("r".equals(localName)) {
			rStack.getLast().push(new StackBlockElement(mapStyle(localName)));
		} else if ("br".equals(localName)) {
			rStack.getLast().appendText("<br/>");
		} else if ("rStyle".equals(localName)) {
			rStack.getLast().addAttr("class", attributes.getValue("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "val"));
		} else if (inRProps && !"lang".equals(localName)) {
			rStack.getLast().peek().addProp(localName, attributes.getValue("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "val"));
		} else if ("rPr".equals(localName)) {
			inRProps = true;
		} else if ("pStyle".equals(localName)) {
			String style = mapStyle(attributes.getValue("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "val"));
			rStack.getLast().setElem(style);
		} else if ("jc".equals(localName)) {
			rStack.getLast().addProp("text-align", attributes.getValue("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "val"));
		} else if (tableStyles.contains(localName)) {
			rStack.getLast().appendText("<"+mapStyle(localName)+">");
		} else if ("numId".equals(localName) && "2".equals(attributes.getValue("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "val"))) {
			isOrderedList = true;
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
			if ("li".equals(rStack.getFirst().getElem()) && !previousList) {
				data.append(isOrderedList ? "<ol>" : "<ul>");
				previousList = true;
			} else if (!"li".equals(rStack.getFirst().getElem()) && previousList) {
				data.append(isOrderedList ? "</ol>" : "</ul>");
				previousList = false;
			}
			data.append(rStack.removeLast().render()+"\r\n");
		} else if ("rPr".equals(localName)) {
			inRProps = false;
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
		return data.toString();
	}

	private static class StackBlockElement {
		private static final String[] INLINE_ELEMENTS = new String[] {"b","i","u"};
		private String elem;
		private StringBuilder text;
		private LinkedList<StackBlockElement> inlineParts;
		private Map<String, String> props;
		private Map<String, String> attrs;
 
		private StackBlockElement(String elem) {
			super();
			this.elem = elem;
			this.inlineParts = new LinkedList<StackBlockElement>();
			this.text = new StringBuilder();
			this.props = new HashMap<>();
			this.attrs = new HashMap<>();
		}
		String getElem() {
			return elem;
		}
		void setElem(String elem) {
			this.elem = elem;
		}
		void addProp(String key, String val) {
			this.props.put(key, val);
		}
		void addAttr(String key, String val) {
			this.attrs.put(key, val);
		}
		void appendText(String text) {
			if (inlineParts.isEmpty()) {
				this.text.append(text);
			} else {
				this.inlineParts.getLast().appendText(text);
			}
		}
		void push(StackBlockElement docElem) {
			this.inlineParts.addLast(docElem);
		}
		StackBlockElement peek() {
			if (inlineParts.isEmpty()) {
				return new StackBlockElement("nullobject");
			}
			return this.inlineParts.getLast();
		}
		String render() {
			StringBuilder r = new StringBuilder();
			String spanStyles = "";
			for (String prop : this.props.keySet()) {
				if (Arrays.binarySearch(INLINE_ELEMENTS, prop)<0) {
					if (!spanStyles.isEmpty()) {
						spanStyles+= ";";
					}
					spanStyles+= prop+":"+(prop.contains("color")?"#":"")+this.props.get(prop);
				}
			}
			if ("r".equals(elem) && !spanStyles.isEmpty()) {
				this.elem = "span";
			}
			if (!"r".equals(elem)) {
				r.append("<"+elem);
				for (Entry<String, String> attr : this.attrs.entrySet()) {
					r.append(" "+attr.getKey()+"=\""+attr.getValue()+"\"");
				}
				if (!spanStyles.isEmpty()) {
					r.append(" style=\""+spanStyles+"\"");
				}
				
				r.append(">");
			}
			for (String prop : this.props.keySet()) {
				if (Arrays.binarySearch(INLINE_ELEMENTS, prop)>=0) {
					r.append("<"+prop+">");
				}
			}
			for (StackBlockElement docElement : inlineParts) {
				r.append(docElement.render());
			}
			if (text.length() > 0) {
				r.append(this.text);
			} else if (inlineParts.isEmpty()) {
				r.append("&nbsp;");
			}

			for (String prop : this.props.keySet()) {
				if (Arrays.binarySearch(INLINE_ELEMENTS, prop)>=0) {
					r.append("</"+prop+">");
				}
			}
			if (elem!=null && !"r".equals(elem)) {
				r.append("</"+elem+">");
			}
			return r.toString();
		}
		@Override
		public String toString() {
			return this.elem;
		}
	}
}
