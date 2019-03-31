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
package net.younic.tpl.thymeleaf;

import java.util.List;
import java.util.Map;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;

import net.younic.core.api.ITemplatePreProcessor;

/**
 * Hooks a mergeable template that only replaces rendered markup inside a parent host template.
 * @author Andre Albert
 *
 */
public class MergeThemeFileTemplateResolver extends FileTemplateResolver {

	private List<ITemplatePreProcessor> preProcessors;
	private boolean plain = false;
	
	public MergeThemeFileTemplateResolver(List<ITemplatePreProcessor> preProcessors) {
		super();
		this.preProcessors = preProcessors;
	}

	@Override
    protected ITemplateResource computeTemplateResource(
            final IEngineConfiguration configuration, final String ownerTemplate, final String template, final String resourceName, final String characterEncoding, final Map<String, Object> templateResolutionAttributes) {

		return new MergedTemplateResource(resourceName, this.plain, characterEncoding == null ? "utf-8" : characterEncoding, this.getPrefix() + "index"+this.getSuffix(), preProcessors);
    }

	public boolean isPlain() {
		return plain;
	}

	public void setPlain(boolean plain) {
		this.plain = plain;
	}
	
}
