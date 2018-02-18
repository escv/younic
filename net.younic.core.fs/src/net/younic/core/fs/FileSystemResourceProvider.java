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
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.LinkedList;

import org.osgi.framework.BundleException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import net.younic.core.api.IResourceProvider;
import net.younic.core.api.Resource;

@Component(
	service=IResourceProvider.class
)
public class FileSystemResourceProvider implements IResourceProvider {

	private File docroot;
	private FilenameFilter fileFilter;
	
	@Activate
	public void activate(ComponentContext context) throws BundleException {
		this.docroot = new File(context.getBundleContext().getProperty("net.younic.cms.root"));
		this.fileFilter = new NonTechnicalFilter();
		if (this.docroot == null) {
			throw new BundleException("Missing Property \"net.younic.cms.root\"");
		}
	}
	
	@Override
	public Collection<Resource> list(String pathSpec) {
		Collection<Resource> result = new LinkedList<>();
		File folder = new File(docroot, pathSpec);
		
		if (folder.isDirectory()) {
			for (File elem : folder.listFiles(this.fileFilter)) {
				Resource r = new Resource();
				r.setContainer(elem.isDirectory());
				r.setName(elem.getName());
				r.setPath(pathSpec);
				result.add(r);
			}
		}
		
		return result;
	}
	
	@Override
	public Collection<Resource> list(String... pathSpec) {
		final String pathSpecSerial = String.join(File.separator, pathSpec);
		return list(pathSpecSerial);
	}

}
