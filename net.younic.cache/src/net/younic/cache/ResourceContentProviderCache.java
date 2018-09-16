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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceRanking;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import net.younic.core.api.IResourceContentProvider;
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
public class ResourceContentProviderCache implements IResourceContentProvider, EventHandler {

	private static final int MAX_CACHE_ENTRY_SIZE = 50000000; // 50K
	transient private Map<String, String> cache;
	
	@Reference(target="(type=impl)")
	private IResourceContentProvider target;
	
	@Reference(target="(type=impl)")
	private IResourceProvider resourceProvider;
	
	@Activate
	public void init() {
		this.cache = new HashMap<String, String>();
	}
	
	/* (non-Javadoc)
	 * @see net.younic.core.api.IResourceContentProvider#readContent(net.younic.core.api.Resource)
	 */
	@Override
	public String readContent(Resource resource) throws IOException {
		String content = null;
		if (!resource.isContainer() && resource.getSize()<MAX_CACHE_ENTRY_SIZE) {
			String fqn = resource.qualifiedName();
			String hit = cache.get(fqn);
			
			if (hit != null) {
				return hit;
			} else {
				content = target.readContent(resource);
				this.cache.put(fqn, content);
			}
		}
		return content;
	}

	/* (non-Javadoc)
	 * @see net.younic.core.api.IResourceContentProvider#readContent(java.lang.String)
	 */
	@Override
	public String readContent(String resourceFQName) throws IOException {
		Resource resource = resourceProvider.fetchResource(resourceFQName);
		return this.readContent(resource);
	}

	/* (non-Javadoc)
	 * @see net.younic.core.api.IResourceContentProvider#fetchContentStream(net.younic.core.api.Resource)
	 */
	@Override
	public InputStream fetchContentStream(Resource resource) throws IOException {
		InputStream result = null;
		if (!resource.isContainer() && resource.getSize()<MAX_CACHE_ENTRY_SIZE) {
			String fqn = resource.qualifiedName();
			String hit = cache.get(fqn);
			
			if (hit != null) {
				result = new ByteArrayInputStream(hit.getBytes("UTF-8"));
			} else {
				result = target.fetchContentStream(resource);
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see net.younic.core.api.IResourceContentProvider#fetchContentStream(java.lang.String)
	 */
	@Override
	public InputStream fetchContentStream(String resourceFQName) throws IOException {
		Resource resource = resourceProvider.fetchResource(resourceFQName);
		return this.fetchContentStream(resource);
	}
	
	/* (non-Javadoc)
	 * @see net.younic.core.api.IResourceContentProvider#fetchContentFile(net.younic.core.api.Resource)
	 */
	@Override
	public File fetchContentFile(Resource resource) throws IOException {
		return target.fetchContentFile(resource);
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
