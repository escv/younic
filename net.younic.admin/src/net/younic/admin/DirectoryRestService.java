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
import java.util.Collection;

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

import net.younic.core.api.IResourcePersistence;
import net.younic.core.api.IResourceProvider;
import net.younic.core.api.Resource;

/**
 * @author Andre Albert
 *
 */
@Component(service=DirectoryRestService.class)
@JaxrsResource
@JaxrsApplicationSelect("(osgi.jaxrs.name=api)")
@Path("dir")
public class DirectoryRestService {

	@Reference
	private IResourceProvider resourceProvider;
	
	@Reference
	private IResourcePersistence resourcePersistence;
	
	@GET
	@Path("{path:.*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Resource> list(@PathParam("path") String path) {
		if (path.indexOf(0) != '/') {
			path = "/"+path;
		}
		return resourceProvider.list(path);
	}

	@DELETE
	@Path("{path:.*}")
	public void delete(String path) throws IOException {
		if (path.indexOf(0) != '/') {
			path = "/"+path;
		}
		Resource resource = resourceProvider.fetchResource(path);
		resourcePersistence.delete(resource);
		
	}

	public void write(String path, InputStream in, OutputStream out) throws IOException {
		if (path.indexOf(0) != '/') {
			path = "/"+path;
		}
		Resource resource = resourceProvider.fetchResource(path);
		resource.setContainer(true);
		resourcePersistence.persist(resource, in);
	}

}
