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

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;

import net.younic.core.api.IResourceContentProvider;
import net.younic.core.api.IResourcePersistence;
import net.younic.core.api.IResourceProvider;
import net.younic.core.api.Resource;

/**
 * @author Andre Albert
 *
 */
@Component(service=ResourceTextRestService.class)
@JaxrsResource
@JaxrsApplicationSelect("(osgi.jaxrs.name=api)")
@Path("txt")
public class ResourceTextRestService {

	@Reference
	private IResourceContentProvider resourceContentProvider;
	
	@Reference
	private IResourceProvider resourceProvider;
	
	@Reference
	private IResourcePersistence resourcePersistence;
	
	@GET
	@Path("{path:.*}")
	@Produces(MediaType.TEXT_PLAIN)
	public String read(@PathParam("path") String path) throws IOException {
		if (path.indexOf(0) != '/') {
			path = "/"+path;
		}
		return resourceContentProvider.readContent(path);
	}

	/* (non-Javadoc)
	 * @see net.younic.core.dispatcher.api.IWriteAPIService#dispatchWriteService(java.lang.String, java.io.InputStream, java.io.OutputStream)
	 */
	public void write(String path, InputStream in, OutputStream out) throws IOException {
		Resource resource = resourceProvider.fetchResource(path);
		resourcePersistence.persist(resource, in);
	}

	@DELETE
	@Path("{path:.*}")
	public void delete(@PathParam("path") String path) throws IOException {
		if (path.indexOf(0) != '/') {
			path = "/"+path;
		}
		Resource resource = resourceProvider.fetchResource(path);
		resourcePersistence.delete(resource);
	}
	
}
