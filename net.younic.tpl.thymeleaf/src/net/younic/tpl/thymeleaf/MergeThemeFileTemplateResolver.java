package net.younic.tpl.thymeleaf;

import java.util.Map;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;

public class MergeThemeFileTemplateResolver extends FileTemplateResolver {

	@Override
    protected ITemplateResource computeTemplateResource(
            final IEngineConfiguration configuration, final String ownerTemplate, final String template, final String resourceName, final String characterEncoding, final Map<String, Object> templateResolutionAttributes) {
	    	return new MergedTemplateResource(resourceName, characterEncoding, this.getPrefix() + "index"+this.getSuffix());
    }
}
