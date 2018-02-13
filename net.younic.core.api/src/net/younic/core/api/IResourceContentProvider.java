package net.younic.core.api;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import org.osgi.annotation.versioning.ConsumerType;

@ConsumerType
public interface IResourceContentProvider {

	String readContent(Resource resource) throws IOException;
	
	Map<String, Serializable> readContents(Resource resource) throws IOException;

	
	/**
	 * @param resourceFQName
	 * @return the resource content or <code>null</code>
	 */
	String readContent(String resourceFQName);
}
