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

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import net.youni.content.IResourceConverter;

/**
 * @author Andre Albert
 *
 */
public class RankResourceConverterComparatorTest {

	private RankResourceConverterComparator comparator;
	
	@Before
	public void setup() {
		comparator = new RankResourceConverterComparator();
	}

	@Test
	public void testSortDescending() {
		List<IResourceConverter> converters = new LinkedList<>();
		converters.add(new MockResourceConverter(5));
		converters.add(new MockResourceConverter(34));
		converters.add(new MockResourceConverter(1));
		converters.add(new MockResourceConverter(600));
		converters.add(new MockResourceConverter(-1));
		
		converters.sort(comparator);
		int i=0;
		assertEquals(600, converters.get(i++).rank());
		assertEquals(34, converters.get(i++).rank());
		assertEquals(5, converters.get(i++).rank());
		assertEquals(1, converters.get(i++).rank());
		assertEquals(-1, converters.get(i++).rank());
		
	}
	
}
