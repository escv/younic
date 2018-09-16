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
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;

import net.younic.core.api.YounicEventsConstants;

/**
 * Allows to clear all caches (template, resource ...) to force a clean re-rendering afterwards.
 * @author Andre Albert
 */
@Component(service=CacheRestService.class)
@JaxrsResource
@JaxrsApplicationSelect("(osgi.jaxrs.name=api)")
@Path("cache")
public class CacheRestService {

	@Reference
    private EventAdmin eventAdmin;

	@GET
	@Path("clear")
	@Produces(MediaType.TEXT_PLAIN)
	public String clear() throws IOException {
		Map<String, String> props = new HashMap<String, String>();
		eventAdmin.postEvent(new Event(YounicEventsConstants.RESOURCE_MODIFIED, props));
		return "OK";
	}

}
