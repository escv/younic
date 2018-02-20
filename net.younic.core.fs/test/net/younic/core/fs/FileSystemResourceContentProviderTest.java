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
package net.younic.core.fs;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.URL;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

import net.younic.core.api.Resource;

public class FileSystemResourceContentProviderTest {

	private FileSystemResourceContentProvider fsrcp;
	private Mockery m = new JUnit4Mockery();
	
	@Before
	public void setup() throws Exception {
		fsrcp = new FileSystemResourceContentProvider();
		
		final ComponentContext context = m.mock(ComponentContext.class);
		final BundleContext bndCtx = m.mock(BundleContext.class);
		URL url = this.getClass().getClassLoader().getResource("resources");
		m.checking(new Expectations() {{
			allowing(bndCtx).getProperty("net.younic.cms.root");
			will(returnValue(url.getFile()));
			
			allowing(context).getBundleContext();
			will(returnValue(bndCtx));
		}});
		fsrcp.activate(context);
	}
	
	@Test
	public void testReadContent() throws Exception {
		String content = fsrcp.readContent("content/B.txt");
		assertEquals("B äüö <test>ÄÜÖ</test>", content);
	}
	@Test
	public void testReadNotExistContent() throws Exception {
		String content = fsrcp.readContent("content/NotExists.txt");
		assertNull(content);
	}
	@Test
	public void testReadResourceContent() throws Exception {
		Resource r = new Resource();
		r.setContainer(false);
		r.setName("A.txt");
		r.setPath("content/de/");
		String content = fsrcp.readContent(r);
		assertEquals("A in de", content);
	}

}
