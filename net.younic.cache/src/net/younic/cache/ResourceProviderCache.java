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
package net.younic.cache;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceRanking;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import net.younic.core.api.IResourceProvider;
import net.younic.core.api.Resource;
import net.younic.core.api.YounicEventsConstants;

/**
 * @author Andre Albert
 *
 */
@Component(property= {
	"type=cache",
	EventConstants.EVENT_TOPIC + "=" + YounicEventsConstants.RESOURCE_MODIFIED
})
@ServiceRanking(100)
public class ResourceProviderCache implements IResourceProvider, EventHandler {

	@Reference(target="(type=impl)")
	private IResourceProvider target;
	
	
	transient private Map<String, Collection<Resource>> cache;
	
	@Activate
	public void init() {
		cache = new HashMap<String, Collection<Resource>>();
	}
	
	/* (non-Javadoc)
	 * @see net.younic.core.api.IResourceProvider#list(java.lang.String[])
	 */
	@Override
	public Collection<Resource> list(String... pathSpec) {
		String path = String.join(File.separator, pathSpec);
		Collection<Resource> hit = cache.get(path);
		if (hit != null) {
			return hit;
		} else {
			Collection<Resource> result = target.list(pathSpec);
			if (result!=null) {
				cache.put(path, result);
			}
			return result;
		}
	}

	/* (non-Javadoc)
	 * @see net.younic.core.api.IResourceProvider#list(java.lang.String)
	 */
	@Override
	public Collection<Resource> list(String pathSpec) {
		Collection<Resource> hit = cache.get(pathSpec);
		if (hit != null) {
			return hit;
		} else {
			Collection<Resource> result = target.list(pathSpec);
			if (result!=null) {
				cache.put(pathSpec, result);
			}
			return result;
		}
	}

	/* (non-Javadoc)
	 * @see net.younic.core.api.IResourceProvider#fetchResource(java.lang.String)
	 */
	@Override
	public Resource fetchResource(String pathSpec) {
		return target.fetchResource(pathSpec);
	}

	/* (non-Javadoc)
	 * @see org.osgi.service.event.EventHandler#handleEvent(org.osgi.service.event.Event)
	 */
	@Override
	public void handleEvent(Event event) {
		// TODO aalbert : only remove relevant parts
		cache.clear();
	}
}
