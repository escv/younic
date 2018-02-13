package net.younic.tpl.thymeleaf;

import java.io.Writer;
import java.util.Locale;
import java.util.Map;

import org.osgi.framework.BundleException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import net.younic.core.api.IResourceProvider;
import net.younic.core.api.IResourceRenderer;
import net.younic.core.api.ResourceRenderingFailedException;

@Component(
	service=IResourceRenderer.class
)
public class ThymeleafResourceRenderer implements IResourceRenderer {

	@Reference
	private IResourceProvider resourceProvider;
	
	private String docroot;
	
	@Activate
	public void activate(ComponentContext context) throws BundleException {
		this.docroot = context.getBundleContext().getProperty("net.younic.cms.root");
		if (this.docroot == null) {
			throw new BundleException("Missing Property \"net.younic.cms.root\"");
		}
	}
	
	@Override
	public void render(String tpl, Map<String, Object> context, Writer out) throws ResourceRenderingFailedException {
		TemplateEngine templateEngine = new TemplateEngine();
		Context tlContext = new Context(Locale.US, context);
		tlContext.setVariable("FS", resourceProvider);

		FileTemplateResolver templateResolver = new MergeThemeFileTemplateResolver();
		templateResolver.setPrefix(this.docroot + "/template/");
		templateResolver.setSuffix(".html");
		templateEngine.setTemplateResolver(templateResolver);
		templateEngine.process(tpl, tlContext, out);
	}

}
