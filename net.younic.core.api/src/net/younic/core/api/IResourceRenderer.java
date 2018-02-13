package net.younic.core.api;

import java.io.Writer;
import java.util.Map;

import org.osgi.annotation.versioning.ConsumerType;

@ConsumerType
public interface IResourceRenderer {

	void render (String tpl, Map<String, Object> context, Writer out) throws ResourceRenderingFailedException;
}
