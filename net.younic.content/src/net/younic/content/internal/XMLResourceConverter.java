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
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.xml.sax.SAXException;

import net.younic.content.IResourceConverter;
import net.younic.core.api.IResourceContentProvider;
import net.younic.core.api.Resource;

@Component(service=IResourceConverter.class)
public class XMLResourceConverter implements IResourceConverter {

	@Reference
	private IResourceContentProvider contentProvider;
	
	@Override
	public Object convert(Resource resource) throws IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser;
		MapSAXHandler handler = new MapSAXHandler();
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			saxParser = factory.newSAXParser();
			saxParser.parse(contentProvider.fetchContentStream(resource), handler);
			result.putAll(handler.getMap());
			
		} catch (ParserConfigurationException | SAXException e) {
			throw new IOException("Error reading resource "+resource);
		} finally {
			handler.cleanup();
		}
		
		return result;
	}
	
	@Override
	public boolean handles(Object resource) {
		return (resource instanceof Resource) && ((Resource)resource).getName().endsWith(".xml");
	}
	
	@Override
	public int rank() {
		return 1;
	}
}
