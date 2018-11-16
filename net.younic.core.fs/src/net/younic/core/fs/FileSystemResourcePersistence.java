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
package net.younic.core.fs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import net.younic.core.api.IResourcePersistence;
import net.younic.core.api.Resource;
import net.younic.core.api.YounicEventsConstants;

/**
 * @author Andre Albert
 *
 */
@Component(service=IResourcePersistence.class)
public class FileSystemResourcePersistence implements IResourcePersistence {

	private File docroot;
	
	@Reference
    private EventAdmin eventAdmin;
	
	@Activate
	public void activate(ComponentContext context) throws BundleException {
		this.docroot = new File(context.getBundleContext().getProperty("net.younic.cms.root"));
		if (this.docroot == null) {
			throw new BundleException("Missing Property \"net.younic.cms.root\"");
		}
	}
	
	/* (non-Javadoc)
	 * @see net.younic.core.api.IResourcePersistence#persist(net.younic.core.api.Resource, java.io.InputStream)
	 */
	@Override
	public void persist(Resource resource, InputStream in) throws IOException {
		File target = new File(docroot, resource.getPath()+"/"+resource.getName());
		if (resource.isContainer()) {
			target.mkdirs();
		} else {
			Files.copy(in, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
		
		//post an event
		postEvent(YounicEventsConstants.RESOURCE_MODIFIED, resource.qualifiedName());
	}
	
	/* (non-Javadoc)
	 * @see net.younic.core.api.IResourcePersistence#delete(net.younic.core.api.Resource)
	 */
	@Override
	public void delete(Resource resource) throws IOException {
		File target = new File(docroot, resource.getPath()+"/"+resource.getName());
		if (target.isDirectory()) {
			Path pathToBeDeleted = target.toPath();
			 
		    Files.walk(pathToBeDeleted)
		      .sorted(Comparator.reverseOrder())
		      .map(Path::toFile)
		      .forEach(File::delete);
		} else {
			target.delete();
		}
		//post an event
		postEvent(YounicEventsConstants.RESOURCE_MODIFIED, resource.qualifiedName());
	}

	/**
	 * @param resource
	 */
	private void postEvent(String eventTopic, String resourceFQN) {
		Map<String, String> props = new HashMap<String, String>();
		props.put(YounicEventsConstants.PROPERTY_RESOURCE, resourceFQN);
		eventAdmin.postEvent(new Event(eventTopic, props));
	}
}
