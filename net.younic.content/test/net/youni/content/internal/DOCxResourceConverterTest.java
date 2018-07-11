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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;

import net.younic.content.internal.DOCxResourceConverter;
import net.younic.core.api.IResourceContentProvider;
import net.younic.core.api.Resource;

/**
 * @author Andre Albert
 *
 */
public class DOCxResourceConverterTest {

	private DOCxResourceConverter conv;
	private File docRoot;
	private Mockery m = new JUnit4Mockery();
	
	@Before
	public void setup() throws Exception {
		this.docRoot = new File(this.getClass().getClassLoader().getResource("resources").getFile());
		this.conv = new DOCxResourceConverter();
		final IResourceContentProvider rcp = m.mock(IResourceContentProvider.class);
		m.checking(new Expectations() {{
			allowing(rcp).fetchContentStream(with(any(Resource.class)));
			will(returnValue(new ByteArrayInputStream(Files.readAllBytes(Paths.get(docRoot.getAbsolutePath()+"/content/table.csv")))));			
		}});
		Field field = DOCxResourceConverter.class.getDeclaredField("contentProvider");
		field.setAccessible(true);
		field.set(conv, rcp);
		field.setAccessible(false);
	}

	@Test
	public void testTransformWordXML() throws Exception {
		Resource r = new Resource(docRoot.getAbsolutePath()+"/content/", "Simple.docx", false);
		String markup = (String)conv.convert(r);
		System.out.println(markup);
	}
}