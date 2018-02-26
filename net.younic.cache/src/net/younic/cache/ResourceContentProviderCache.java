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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import net.younic.core.api.IResourceContentProvider;
import net.younic.core.api.IResourceProvider;
import net.younic.core.api.Resource;

/**
 * @author Andre Albert
 *
 */
@Component(property="type=cache")
public class ResourceContentProviderCache implements IResourceContentProvider {

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
		if (!resource.isContainer() && resource.getSize()<MAX_CACHE_ENTRY_SIZE) {
			String fqn = resource.qualifiedName();
			String hit = cache.get(fqn);
			
			if (hit != null) {
				return hit;
			} else {
				String content = target.readContent(resource);
				this.cache.put(fqn, content);
			}
		}
		return target.readContent(resource);
	}

	/* (non-Javadoc)
	 * @see net.younic.core.api.IResourceContentProvider#readContent(java.lang.String)
	 */
	@Override
	public String readContent(String resourceFQName) throws IOException {
		Resource resource = resourceProvider.fetchResource(resourceFQName);
		if (resource!=null && !resource.isContainer() && resource.getSize()<MAX_CACHE_ENTRY_SIZE) {
			String hit = cache.get(resourceFQName);
			
			if (hit != null) {
				return hit;
			} else {
				String content = target.readContent(resource);
				this.cache.put(resourceFQName, content);
			}
		}
		return target.readContent(resourceFQName);
	}

	/* (non-Javadoc)
	 * @see net.younic.core.api.IResourceContentProvider#fetchContentStream(net.younic.core.api.Resource)
	 */
	@Override
	public InputStream fetchContentStream(Resource resource) throws IOException {
		// Streams are not cached
		return target.fetchContentStream(resource);
	}

	/* (non-Javadoc)
	 * @see net.younic.core.api.IResourceContentProvider#fetchContentStream(java.lang.String)
	 */
	@Override
	public InputStream fetchContentStream(String resourceFQName) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
