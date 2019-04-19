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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.BundleException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceRanking;

import net.younic.core.api.IResourceProvider;
import net.younic.core.api.Resource;

@Component(
	service=IResourceProvider.class,
	property= {"type=impl"}
)
@ServiceRanking(1)
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
		List<Resource> result = new LinkedList<>();
		File folder = new File(docroot, pathSpec);
		
		if (folder.isDirectory()) {
			for (File elem : folder.listFiles(this.fileFilter)) {
				Resource r = new Resource(pathSpec, elem.getName(), elem.isDirectory()&&!elem.getName().startsWith("!"));
				r.setLastModified(elem.lastModified());
				r.setSize(elem.length());
				result.add(r);
			}
		}
		Collections.sort(result);
		return result;
	}
	
	/* (non-Javadoc)
	 * @see net.younic.core.api.IResourceProvider#fetchResource(java.lang.String)
	 */
	@Override
	public Resource fetchResource(String pathSpec) {
		File resource = new File(docroot, pathSpec);
		
		String[] pathName = separatePath(pathSpec);
		boolean isDir = resource.exists()&&resource.isDirectory() || pathName[1].indexOf('.')==-1;
		
		Resource result = new Resource(pathName[0], pathName[1], isDir);
		
		if (resource.exists()) {
			result.setLastModified(resource.lastModified());
			if (!resource.isDirectory()) {
				result.setSize(resource.length());
			}
		}
		
		return result;
	}
	
	@Override
	public Collection<Resource> list(String... pathSpec) {
		final String pathSpecSerial = String.join(File.separator, pathSpec);
		return list(pathSpecSerial);
	}

	private String[] separatePath(String pathSpec) {
		String path = pathSpec;
		String name = null;
		int lastSepPos = pathSpec.lastIndexOf('/');
		if (lastSepPos >= 0) {
			path = pathSpec.substring(0, lastSepPos);
			name = pathSpec.substring(lastSepPos+1);
		}
		return new String[] {path, name};
	}
}
