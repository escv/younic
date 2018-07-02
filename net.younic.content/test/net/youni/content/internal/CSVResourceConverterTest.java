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
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.younic.content.internal.CSVResourceConverter;
import net.younic.content.internal.XMLResourceConverter;
import net.younic.core.api.IResourceContentProvider;
import net.younic.core.api.Resource;

/**
 * @author Andre Albert
 *
 */
public class CSVResourceConverterTest {

	private CSVResourceConverter conv;
	private File docRoot;
	private Mockery m = new JUnit4Mockery();
	
	@Before
	public void setup() throws Exception {
		this.docRoot = new File(this.getClass().getClassLoader().getResource("resources").getFile());
		this.conv = new CSVResourceConverter();
		final IResourceContentProvider rcp = m.mock(IResourceContentProvider.class);
		m.checking(new Expectations() {{
			allowing(rcp).fetchContentStream(with(any(Resource.class)));
			will(returnValue(new ByteArrayInputStream(Files.readAllBytes(Paths.get(docRoot.getAbsolutePath()+"/content/table.csv")))));			
		}});
		Field field = CSVResourceConverter.class.getDeclaredField("contentProvider");
		field.setAccessible(true);
		field.set(conv, rcp);
		field.setAccessible(false);
	}

	@Test
	public void testStructureContentXML() throws Exception {
		Resource r = new Resource(docRoot.getAbsolutePath()+"/content/", "table.csv", false);
		@SuppressWarnings("unchecked")
		List<String[]> result = (List<String[]>) this.conv.convert(r);
		Assert.assertFalse(result.isEmpty());
		for (int i=1; i<=6; i++) {
			Assert.assertArrayEquals(new String[] {"A"+i, "B"+i, "C"+i, "D"+i}, result.get(i-1));
		}
	}
}
