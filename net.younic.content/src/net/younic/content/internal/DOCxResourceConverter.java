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

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.xml.sax.SAXException;

import net.younic.content.IResourceConverter;
import net.younic.core.api.IResourceContentProvider;
import net.younic.core.api.Resource;

/**
 * @author Andre Albert
 *
 */
@Component(service=IResourceConverter.class)
public class DOCxResourceConverter implements IResourceConverter {


	@Reference
	private IResourceContentProvider contentProvider;
	
	/* (non-Javadoc)
	 * @see net.younic.core.api.IHandleable#handles(java.lang.Object)
	 */
	@Override
	public boolean handles(Object resource) {
		return (resource instanceof Resource) && ((Resource)resource).getName().endsWith(".docx");
	}

	/* (non-Javadoc)
	 * @see net.younic.core.api.IRankable#rank()
	 */
	@Override
	public int rank() {
		return 1;
	}

	/* (non-Javadoc)
	 * @see net.younic.content.IResourceConverter#convert(net.younic.core.api.Resource)
	 */
	@Override
	public Object convert(Resource resource) throws IOException {//TODO

		try (ZipFile zip = new ZipFile(contentProvider.fetchContentFile(resource))) {
			Enumeration<? extends ZipEntry> entries = zip.entries();
			String markup = null;
			while ( entries.hasMoreElements()) {
			   ZipEntry entry = (ZipEntry)entries.nextElement();
	
			   if ( !entry.getName().equals("word/document.xml")) continue;
	
			   InputStream in = zip.getInputStream(entry);
			   markup = parseDocumentXML(in);
			   break;
			}
			return markup;
		}
	}

	private String parseDocumentXML(InputStream docXML) throws IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		SAXParser saxParser;
		DOCx2HTLMHandler handler = new DOCx2HTLMHandler();
		try {
			saxParser = factory.newSAXParser();
			saxParser.parse(docXML, handler);
			
		} catch (ParserConfigurationException | SAXException e) {
			throw new IOException("Error reading resource");
		}
		
		return handler.getMarkup();
	}

}
