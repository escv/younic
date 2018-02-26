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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.osgi.framework.BundleException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import net.younic.core.api.IResourceContentProvider;
import net.younic.core.api.IResourceProvider;
import net.younic.core.api.Resource;

@Component(
	service=IResourceContentProvider.class,
	property="type=impl"
)
public class FileSystemResourceContentProvider implements IResourceContentProvider {

	private String docroot;
	
	@Reference
	private IResourceProvider resourceProvider;
	
	@Activate
	public void activate(ComponentContext context) throws BundleException {
		this.docroot = context.getBundleContext().getProperty("net.younic.cms.root");
		if (this.docroot == null) {
			throw new BundleException("Missing Property \"net.younic.cms.root\"");
		}
	}
	
	@Override
	public String readContent(Resource resource) throws IOException {
		return readContent(resource.qualifiedName());
	}

	@Override
	public String readContent(String resourceFQName) {
		String content = null;
		try {
			content = new String(Files.readAllBytes(Paths.get(docroot, resourceFQName)));
		} catch (IOException e) {
			//TODO aalbert loggin
		}
		return content;
	}

	@Override
	public InputStream fetchContentStream(Resource resource) throws IOException {
		return fetchContentStream(resource.qualifiedName());
	}

	@Override
	public InputStream fetchContentStream(String resourceFQName) throws IOException {
		Path path = Paths.get(docroot, resourceFQName);
		
		return new FileInputStream(path.toFile());
	}

	
}
