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
package net.younic.content.internal;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import net.younic.content.IAggregatedResourceContentProvider;
import net.younic.content.IResourceConverter;
import net.younic.core.api.IResourceContentProvider;
import net.younic.core.api.IResourceProvider;
import net.younic.core.api.IResourceRenderer;
import net.younic.core.api.Resource;
import net.younic.core.api.ResourceRenderingFailedException;

/**
 * @author Andre Albert
 *
 */
@Component(service=IResourceConverter.class)
public class ComponentResourceConverter implements IResourceConverter {

	@Reference
	private IResourceContentProvider contentProvider;

	@Reference
	private IResourceProvider resourceProvider;
	
	@Reference
	private IAggregatedResourceContentProvider aggregatedResourceContentProvider;
	
	@Reference
	private IResourceRenderer renderer;

	/* (non-Javadoc)
	 * @see net.younic.core.api.IHandleable#handles(java.lang.Object)
	 */
	@Override
	public boolean handles(Resource resource) {
		return resource.isComponent();
	}

	/* (non-Javadoc)
	 * @see net.younic.core.api.IRankable#rank()
	 */
	@Override
	public int rank() {
		return 1;
	}

	/* (non-Javadoc)
	 * @see net.younic.content.IResourceConverter#convert(net.younic.core.api.Resource)
	 */
	@Override
	public Object convert(Resource resource) throws IOException {
		Map<String, Object> context = aggregatedResourceContentProvider.provideContents(resource);
		String template = contentProvider.readContent(resource.qualifiedName()+"/template.ref").trim();
		
		Writer out = new StringWriter();
		try {
			renderer.render(template, true, context, out );
		} catch (ResourceRenderingFailedException e) {
			throw new IOException("Error rendering component "+resource.qualifiedName(), e);
		}
		return out.toString();
	}


}
