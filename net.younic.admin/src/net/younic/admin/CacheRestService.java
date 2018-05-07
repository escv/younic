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
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import net.younic.core.api.YounicEventsConstants;
import net.younic.core.dispatcher.api.IResourceAPIService;

/**
 * @author Andre Albert
 *
 */
@Component(service=IResourceAPIService.class)
public class CacheRestService implements IResourceAPIService {

	@Reference
    private EventAdmin eventAdmin;
	
	/* (non-Javadoc)
	 * @see net.younic.core.api.IHandleable#handles(java.lang.Object)
	 */
	@Override
	public boolean handles(Object resource) {
		return "cache".equals(resource);
	}

	/* (non-Javadoc)
	 * @see net.younic.core.api.IRankable#rank()
	 */
	@Override
	public int rank() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see net.younic.core.dispatcher.api.IResourceAPIService#read(java.lang.String, java.io.OutputStream)
	 */
	@Override
	public void read(String path, OutputStream out) throws IOException {
		if ("clear".equals(path)) {
			Map<String, String> props = new HashMap<String, String>();
			eventAdmin.postEvent(new Event(YounicEventsConstants.RESOURCE_DELETED, props));
			out.write("OK".getBytes("UTF-8"));
		}

	}

	/* (non-Javadoc)
	 * @see net.younic.core.dispatcher.api.IResourceAPIService#delete(java.lang.String)
	 */
	@Override
	public void delete(String path) throws IOException {

	}

	/* (non-Javadoc)
	 * @see net.younic.core.dispatcher.api.IResourceAPIService#write(java.lang.String, java.io.InputStream, java.io.OutputStream)
	 */
	@Override
	public void write(String path, InputStream in, OutputStream out) throws IOException {

	}

	/* (non-Javadoc)
	 * @see net.younic.core.dispatcher.api.IResourceAPIService#contentType()
	 */
	@Override
	public String contentType() {
		// TODO Auto-generated method stub
		return "text/plain";
	}

}
