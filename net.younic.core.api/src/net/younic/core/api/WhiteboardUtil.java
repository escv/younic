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
package net.younic.core.api;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Andre Albert
 *
 */
public final class WhiteboardUtil {

	private static final RankableDescendingComparator comparator = new RankableDescendingComparator();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final <T extends IHandleable> T findHandler(Class<T> clazz, List<T> candidates, Object matcher) {
		Collections.sort(candidates, comparator);
		Optional<T> converter = candidates.stream().filter(e->e.handles(matcher)).findFirst();
		T result = null;
		if (converter.isPresent()) {
			result = (T) converter.get();
		}
		return result;
	}

}
