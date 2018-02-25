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

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import net.younic.content.IResourceConverter;
import net.younic.core.api.IResourceContentProvider;
import net.younic.core.api.Resource;

@Component(service=IResourceConverter.class)
public class StringResourceConverter implements IResourceConverter {

	@Reference(target="(type=cache)")
	private IResourceContentProvider contentProvider;
	
	@Override
	public Object convert(Resource resource) throws IOException {
		
		return contentProvider.readContent(resource);
	}
	
	@Override
	public boolean handles(Resource resource) {
		return true;
	}
	
	@Override
	public int rank() {
		return 0;
	}
}
