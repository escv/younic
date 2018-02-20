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
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.Collection;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

import net.younic.core.api.Resource;

public class FileSystemResourceProviderTest {

	private FileSystemResourceProvider fsrp;
	private Mockery m = new JUnit4Mockery();
	
	@Before
	public void setup() throws Exception {
		fsrp = new FileSystemResourceProvider();
		
		final ComponentContext context = m.mock(ComponentContext.class);
		final BundleContext bndCtx = m.mock(BundleContext.class);
		URL url = this.getClass().getClassLoader().getResource("resources");
		m.checking(new Expectations() {{
			allowing(bndCtx).getProperty("net.younic.cms.root");
			will(returnValue(url.getFile()));
			
			allowing(context).getBundleContext();
			will(returnValue(bndCtx));
		}});
		fsrp.activate(context);
	}
	
	@Test
	public void testListContent() throws Exception {
		Collection<Resource> result = fsrp.list("content");
		assertEquals(3, result.size());
		assertTrue(result.stream().anyMatch(r->"B.txt".equals(r.getName())));
		assertTrue(result.stream().anyMatch(r->"de".equals(r.getName())));
		assertTrue(result.stream().anyMatch(r->"en".equals(r.getName())));
		assertTrue(result.stream().filter(r->"de".equals(r.getName())).allMatch(r->r.isContainer()));
		assertTrue(result.stream().filter(r->"en".equals(r.getName())).allMatch(r->r.isContainer()));
		assertTrue(result.stream().filter(r->"B.txt".equals(r.getName())).noneMatch(r->r.isContainer()));
	}
	
	@Test
	public void testHandlesPathSep() throws Exception {
		Collection<Resource> result = fsrp.list("/content/");
		assertEquals(3, result.size());
		assertTrue(result.stream().anyMatch(r->"B.txt".equals(r.getName())));
		assertTrue(result.stream().anyMatch(r->"de".equals(r.getName())));
		assertTrue(result.stream().anyMatch(r->"en".equals(r.getName())));
		assertTrue(result.stream().filter(r->"de".equals(r.getName())).allMatch(r->r.isContainer()));
		assertTrue(result.stream().filter(r->"en".equals(r.getName())).allMatch(r->r.isContainer()));
		assertTrue(result.stream().filter(r->"B.txt".equals(r.getName())).noneMatch(r->r.isContainer()));
	}
	
	@Test
	public void testVarArgsPath() throws Exception {
		Collection<Resource> result = fsrp.list("content", "de");
		assertEquals(1, result.size());
		assertTrue(result.stream().anyMatch(r->"A.txt".equals(r.getName())));
		assertTrue(result.stream().filter(r->"A.txt".equals(r.getName())).noneMatch(r->r.isContainer()));
	}
	
	@Test
	public void testPaths() throws Exception {
		Collection<Resource> result = fsrp.list("content/en");
		assertEquals(1, result.size());
		assertTrue(result.stream().anyMatch(r->"A.txt".equals(r.getName())));
		assertTrue(result.stream().filter(r->"A.txt".equals(r.getName())).noneMatch(r->r.isContainer()));
	}
	
	@Test
	public void testEmptyOnNotExist() throws Exception {
		Collection<Resource> result = fsrp.list("ceontenret/en");
		assertEquals(0, result.size());
	}
	
	@Test
	public void testIgnoresTemplateRefs() throws Exception {
		Collection<Resource> result = fsrp.list("content/de");
		assertTrue(result.stream().noneMatch(r->"TEMPLATE.REF".equals(r.getName())));
		
		result = fsrp.list("content/");
		assertTrue(result.stream().noneMatch(r->"template.ref".equals(r.getName())));
	}


}
