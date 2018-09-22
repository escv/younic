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
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsExtension;

/**
 * @author Andre Albert
 *
 */
@Produces ( MediaType.APPLICATION_JSON )
@Consumes ( MediaType.APPLICATION_JSON )
@Provider
@JaxrsExtension
@Component(scope=ServiceScope.PROTOTYPE)
public class IterableJsonProvider implements MessageBodyWriter<Iterable<?>> {


	@Context
    private Providers providers;
	
	@Override
	public boolean isWriteable(Class<?> arg0, Type arg1, Annotation[] arg2, MediaType arg3) {
		return Iterable.class.isAssignableFrom(arg0) && arg3.equals(MediaType.APPLICATION_JSON_TYPE);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void writeTo(Iterable<?> arg0, Class<?> arg1, Type arg2, Annotation[] arg3, MediaType arg4,
			MultivaluedMap<String, Object> arg5, OutputStream arg6) throws IOException, WebApplicationException {
		arg6.write("[".getBytes("utf-8"));

		boolean isFirst = true;
		MessageBodyWriter<Object> rWriter = null;
		for (Object r : arg0) {
			if (rWriter == null) {
				rWriter = (MessageBodyWriter<Object>) providers.getMessageBodyWriter(r.getClass(), null, null, MediaType.APPLICATION_JSON_TYPE);
			}
			if (!isFirst) {
				arg6.write(",".getBytes("utf-8"));
			} else {
				isFirst = false;
			}
			rWriter.writeTo(r, arg1, arg2, arg3, arg4, arg5, arg6);
		}
		arg6.write("]".getBytes("utf-8"));
	}


}
