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
package net.youni.content.internal;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import net.younic.content.internal.WhiteboardAggregatedResourceContentProvider;
import net.younic.core.api.IResourceProvider;
import net.younic.core.api.Resource;

/**
 * @author Andre Albert
 *
 */
public class WhiteboardAggregatedResourceTest {

	private WhiteboardAggregatedResourceContentProvider wbarcp;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Before
	public void setup() throws Exception {
		File docRoot = new File(this.getClass().getClassLoader().getResource("resources").getFile());
		wbarcp = new WhiteboardAggregatedResourceContentProvider();
		Field field = WhiteboardAggregatedResourceContentProvider.class.getDeclaredField("resourceProvider");
		field.setAccessible(true);
		field.set(wbarcp, new MockResourceProvider(docRoot));
		field.setAccessible(false);
		
		field = WhiteboardAggregatedResourceContentProvider.class.getDeclaredField("converters");
		field.setAccessible(true);
		List converters = (List<?>) field.get(wbarcp);
		converters.add(new MockResourceConverter(3, docRoot));
		field.setAccessible(false);
	}

	
	@Test
	public void testProvideContents() throws Exception {
		Resource r = new Resource("content/de", null, true);
		
		Map<String, Object> contents = wbarcp.provideContents(r);
		assertEquals("A in de", contents.get("A"));
		assertEquals("B äüö <test>ÄÜÖ</test>", contents.get("B"));
		@SuppressWarnings("rawtypes")
		List list = (List)contents.get("news");
		assertEquals(2, list.size());
		assertEquals("News 1", list.get(0));
		assertEquals("News 2", list.get(1));
	}
	
	private static class MockResourceProvider implements IResourceProvider {

		private File docRoot;

		public MockResourceProvider(File docRoot) {
			this.docRoot = docRoot;
		}
		
		@Override
		public Collection<Resource> list(String... pathSpec) {
			final String pathSpecSerial = String.join(File.separator, pathSpec);
			Collection<Resource> result = new LinkedList<>();
			File folder = new File(docRoot, pathSpecSerial);
			
			if (folder.isDirectory()) {
				for (File elem : folder.listFiles()) {
					Resource r = new Resource();
					r.setContainer(elem.isDirectory());
					r.setName(elem.getName());
					r.setPath(pathSpecSerial);
					result.add(r);
				}
			}
			
			return result;
		}

		@Override
		public Collection<Resource> list(String pathSpec) {
			throw new UnsupportedOperationException();
		}
		
	}
}
