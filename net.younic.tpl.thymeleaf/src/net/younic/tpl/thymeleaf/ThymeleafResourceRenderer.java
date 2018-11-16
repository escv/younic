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

import java.io.Writer;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.framework.BundleException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import net.younic.core.api.IResourceProvider;
import net.younic.core.api.IResourceRenderer;
import net.younic.core.api.ITemplatePreProcessor;
import net.younic.core.api.ResourceRenderingFailedException;
import net.younic.core.api.YounicEventsConstants;

@Component(
	property= {
			EventConstants.EVENT_TOPIC + "=" + YounicEventsConstants.RESOURCE_MODIFIED
	}
)
public class ThymeleafResourceRenderer implements IResourceRenderer, EventHandler {

	@Reference
	private IResourceProvider resourceProvider;
	
	@Reference(policy=ReferencePolicy.DYNAMIC)
	final List<ITemplatePreProcessor> preProcessors = new CopyOnWriteArrayList<>();
	
	private String docroot;
	private String tplroot;

	private boolean devMode;
	private transient TemplateEngine templateEngine;
	
	@Activate
	public void activate(ComponentContext context) throws BundleException {
		this.devMode = "true".equals(context.getBundleContext().getProperty("net.younic.devmode"));
		this.docroot = context.getBundleContext().getProperty("net.younic.cms.root");
		this.tplroot = this.docroot + "/template/";
		if (this.docroot == null) {
			throw new BundleException("Missing Property \"net.younic.cms.root\"");
		}
	}
	
	@Override
	public void render(String tpl, Map<String, Object> context, Writer out) throws ResourceRenderingFailedException {
		TemplateEngine engine = obtainTemplateEngine();
		
		Context tlContext = new Context(Locale.US, context);
		tlContext.setVariable("FS", resourceProvider);

		engine.process(tpl, tlContext, out);
	}

	private TemplateEngine obtainTemplateEngine() {
		TemplateEngine engine;
		if (!devMode) {
			if (templateEngine == null) {
				this.templateEngine = new TemplateEngine();
				FileTemplateResolver templateResolver = new MergeThemeFileTemplateResolver(this.preProcessors);
				templateResolver.setCacheable(true);
				templateResolver.setPrefix(this.tplroot);
				templateResolver.setSuffix(".html");
				this.templateEngine.setTemplateResolver(templateResolver);
			}
			engine = this.templateEngine;
		} else {
			engine = new TemplateEngine();			
			FileTemplateResolver templateResolver = new MergeThemeFileTemplateResolver(this.preProcessors);
			templateResolver.setPrefix(this.tplroot);
			templateResolver.setCacheable(false);
			templateResolver.setSuffix(".html");
			engine.setTemplateResolver(templateResolver);
		}
		return engine;
	}
	
	/* (non-Javadoc)
	 * @see org.osgi.service.event.EventHandler#handleEvent(org.osgi.service.event.Event)
	 */
	@Override
	public void handleEvent(Event event) {
		if (this.templateEngine != null) {
			this.templateEngine.clearTemplateCache();
		}
		
	}
}
