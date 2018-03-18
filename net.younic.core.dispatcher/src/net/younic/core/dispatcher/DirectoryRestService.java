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
package net.younic.core.dispatcher;

import java.io.IOException;
import java.io.OutputStream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import net.younic.core.api.IResourceProvider;
import net.younic.core.api.Resource;
import net.younic.core.dispatcher.api.IReadAPIService;

/**
 * @author Andre Albert
 *
 */
@Component(service=IReadAPIService.class)
public class DirectoryRestService implements IReadAPIService {

	@Reference(target="(type=impl)")
	private IResourceProvider resourceProvider;
	
	/* (non-Javadoc)
	 * @see net.younic.core.api.IHandleable#handles(java.lang.Object)
	 */
	@Override
	public boolean handles(Object resource) {
		return "dir".equals(resource);
	}

	/* (non-Javadoc)
	 * @see net.younic.core.api.IRankable#rank()
	 */
	@Override
	public int rank() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see net.younic.core.dispatcher.IDispatchAPIService#dispatchServices(java.lang.String, java.io.OutputStream)
	 */
	@Override
	public void read(String path, OutputStream out) throws IOException {
		out.write("[".getBytes());
		boolean first = true;
		for (Resource r : resourceProvider.list(path)) {
			if (!first) {
				out.write(",".getBytes());
			} else {
				first = false;
			}
			out.write(("{"
					+ "\"name\":\""+r.getName()+"\","
					+ "\"fqn\":\""+r.qualifiedName()+"\","
					+ "\"container\":"+r.isContainer()
							+ "}").getBytes());
		}
		
		out.write("]".getBytes("UTF-8"));

	}

	/* (non-Javadoc)
	 * @see net.younic.core.dispatcher.IDispatchAPIService#contentType()
	 */
	@Override
	public String contentType() {
		return "application/json";
	}

}
