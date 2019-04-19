package net.younic.core.api;

import java.io.Writer;
import java.util.Map;

import org.osgi.annotation.versioning.ConsumerType;

@ConsumerType
public interface IResourceRenderer {

	/**
	 * @param tpl The template to use for rendering
	 * @param plain if templates should not be merged with main template
	 * @param context
	 * @param out
	 * @throws ResourceRenderingFailedException
	 */
	void render(String tpl, boolean plain, Map<String, Object> context, Writer out) throws ResourceRenderingFailedException;
}
