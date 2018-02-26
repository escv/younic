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
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;

import net.younic.content.IAggregatedResourceContentProvider;
import net.younic.content.IContextPostProcessor;
import net.younic.content.IResourceConverter;
import net.younic.core.api.IRankable;
import net.younic.core.api.IResourceProvider;
import net.younic.core.api.Resource;

@Component(service=IAggregatedResourceContentProvider.class)
public class WhiteboardAggregatedResourceContentProvider implements IAggregatedResourceContentProvider {

	@Reference(
			target="(type=cache)"
			)
	private IResourceProvider resourceProvider;
	
	@Reference(policy=ReferencePolicy.DYNAMIC)
	final List<IResourceConverter> converters = new CopyOnWriteArrayList<>();
	
	@Reference(policy=ReferencePolicy.DYNAMIC)
	final List<IContextPostProcessor> processors = new CopyOnWriteArrayList<>();
	
	private Comparator<IRankable> rankComparator;

	@Override
	public Map<String, Object> provideContents(Resource resource) throws IOException {
		if (rankComparator == null) {
			rankComparator = new RankResourceConverterComparator();
		}
		converters.sort(rankComparator);
		
		long lastModified = 0L;
		Map<String, Object> result = new TreeMap<>();
		String[] elems = resource.qualifiedName().split("/");
		for (int i = 0; i<elems.length; i++) {
			Collection<Resource> entries = resourceProvider.list(Arrays.copyOfRange(elems, 0, (i+1)));
			for (Resource entry : entries) {
				if (!entry.isContainer()) {
					Optional<IResourceConverter> converter = converters.stream().filter(e->e.handles(entry)).findFirst();
					if (converter.isPresent()) {
						result.put(entry.getName(), converter.get().convert(entry));
						if (entry.getLastModified() > lastModified) {
							lastModified = entry.getLastModified();
						}
					}
				}
			}			
		}
		
		// allow to hook in post processor for result modification
		Map<String, Object> context = prepareContext(result);
		processors.sort(rankComparator);
		processors.stream().forEach(p -> p.processContext(context));
		
		context.put("net.younic.content.lastModified", lastModified);
		context.put("currentPage", resource);
		
		return context;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map<String, Object> prepareContext(Map<String, Object> contents){
		Map<String, Object> result = new HashMap<>();
		for (Entry<String, Object> e : contents.entrySet()) {
			Object val = e.getValue();
			int fileExtSepPos = e.getKey().lastIndexOf('.');
			
			String entryName = e.getKey();
			if (fileExtSepPos > 0) {
				entryName = entryName.substring(0, fileExtSepPos);
			}
			int orderSepPos = e.getKey().lastIndexOf('$');
			if (orderSepPos > 0) {
				entryName = entryName.substring(0, orderSepPos);
				try {
					//int order = Integer.parseInt(entryName.substring(orderSepPos));
					List<Object> values = null;
					Object currentVal = result.get(entryName);
					if (currentVal == null) {
						values = new LinkedList<>();
					} else if (currentVal instanceof List<?>) {
						values = (List) currentVal;
					} else {
						values = new LinkedList<>();
						values.add(currentVal);
					}
					values.add(val);
					val = (Serializable) values;
				} catch (NumberFormatException ex) {
					//silent here
				}
			}
			result.put(entryName, val);
		}
		return result;
	}

}
