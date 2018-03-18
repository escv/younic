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
package net.younic.admin;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import net.younic.core.api.IResourceContentProvider;
import net.younic.core.api.IResourcePersistence;
import net.younic.core.api.IResourceProvider;
import net.younic.core.api.Resource;
import net.younic.core.dispatcher.api.IDeleteAPIService;
import net.younic.core.dispatcher.api.IReadAPIService;
import net.younic.core.dispatcher.api.IWriteAPIService;

/**
 * @author Andre Albert
 *
 */
@Component(service=IReadAPIService.class)
public class ResourceTextRestService implements IReadAPIService, IWriteAPIService, IDeleteAPIService {

	@Reference(target="(type=impl)")
	private IResourceContentProvider resourceContentProvider;
	
	@Reference(target="(type=impl)")
	private IResourceProvider resourceProvider;
	
	@Reference
	private IResourcePersistence resourcePersistence;
	
	/* (non-Javadoc)
	 * @see net.younic.core.api.IHandleable#handles(java.lang.Object)
	 */
	@Override
	public boolean handles(Object resource) {
		return "txt".equals(resource);
	}

	/* (non-Javadoc)
	 * @see net.younic.core.api.IRankable#rank()
	 */
	@Override
	public int rank() {
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see net.younic.core.dispatcher.IDispatchAPIService#contentType()
	 */
	@Override
	public String contentType() {
		return "text/plain";
	}
	
	/* (non-Javadoc)
	 * @see net.younic.core.dispatcher.IDispatchAPIService#dispatchServices(java.lang.String, java.io.OutputStream)
	 */
	@Override
	public void read(String path, OutputStream out) throws IOException {
		out.write(resourceContentProvider.readContent(path).getBytes());
	}

	/* (non-Javadoc)
	 * @see net.younic.core.dispatcher.api.IWriteAPIService#dispatchWriteService(java.lang.String, java.io.InputStream, java.io.OutputStream)
	 */
	@Override
	public void write(String path, InputStream in, OutputStream out) throws IOException {
		Resource resource = resourceProvider.fetchResource(path);
		resourcePersistence.persist(resource, in);
	}

	/* (non-Javadoc)
	 * @see net.younic.core.dispatcher.api.IDeleteAPIService#write(java.lang.String)
	 */
	@Override
	public void delete(String path) throws IOException {
		Resource resource = resourceProvider.fetchResource(path);
		resourcePersistence.delete(resource);
		
	}
	
}
