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
import java.util.OptionalLong;
import java.util.Map.Entry;
import java.util.TreeMap;
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
import net.younic.core.api.WhiteboardUtil;

@Component(service=IAggregatedResourceContentProvider.class)
public class WhiteboardAggregatedResourceContentProvider implements IAggregatedResourceContentProvider {

	@Reference
	private IResourceProvider resourceProvider;
	
	@Reference(policy=ReferencePolicy.DYNAMIC)
	final List<IResourceConverter> converters = new CopyOnWriteArrayList<>();
	
	@Reference(policy=ReferencePolicy.DYNAMIC)
	final List<IContextPostProcessor> processors = new CopyOnWriteArrayList<>();
	
	private Comparator<IRankable> rankComparator;

	@Override
	public Map<String, Object> provideContents(Resource resource) throws IOException {
		
		Map<String, Object> result = new TreeMap<>();
		
		long lastModified = 0L;
		
		if (resource.isComponent()) {
			lastModified =  fetchSubContents(resource, result);
		} else {
			lastModified =  fetchHierarchyContents(resource, result);
		}
		
		// allow to hook in post processor for result modification
		Map<String, Object> context = prepareContext(result);
		processors.sort(rankComparator);
		processors.stream().forEach(p -> p.processContext(context));
		
		context.put("net.younic.content.lastModified", lastModified);
		context.put("currentPage", resource);
		
		return context;
	}

	private long fetchSubContents(Resource resource, Map<String, Object> result) {
		long lastModified = 0L;
		Collection<Resource> entries = resourceProvider.list(resource.qualifiedName());
		
		entries.stream().filter(e->!e.isComponent() && !e.isContainer()).forEach(e->{
			IResourceConverter converter = WhiteboardUtil.findHandler(IResourceConverter.class, converters, e);
			if (converter != null) {
				try {
					result.put(e.getName(), converter.convert(e));
				} catch(IOException ioe) {
					
				}
			}
		});
		
		return lastModified;
	}

	private long fetchHierarchyContents(Resource resource, Map<String, Object> result) {
		long lastModified = 0L;
		String[] elems = resource.qualifiedName().split("/");
		for (int i = 0; i<elems.length; i++) {
			Collection<Resource> entries = resourceProvider.list(Arrays.copyOfRange(elems, 0, (i+1)));
			
			OptionalLong currentMax = entries.stream().filter(e->e.isComponent() || !e.isContainer()).mapToLong(e->{
				IResourceConverter converter = WhiteboardUtil.findHandler(IResourceConverter.class, converters, e);
				if (converter != null) {
					try {
						result.put(e.getName(), converter.convert(e));
						return e.getLastModified();
					} catch(IOException ioe) {
						
					}
				}
				return 0L;
			}).max();
			if (currentMax.isPresent() && currentMax.getAsLong()>lastModified) {
				lastModified = currentMax.getAsLong();
			}
		}
		return lastModified;
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
