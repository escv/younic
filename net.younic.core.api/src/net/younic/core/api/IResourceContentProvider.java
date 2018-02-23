package net.younic.core.api;

import java.io.IOException;
import java.io.InputStream;

import org.osgi.annotation.versioning.ConsumerType;

@ConsumerType
public interface IResourceContentProvider {

	String readContent(Resource resource) throws IOException;
	
	/**
	 * @param resourceFQName
	 * @return the resource content or <code>null</code>
	 */
	String readContent(String resourceFQName) throws IOException;

	
	/**
	 * Please close result Stream after read 
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	InputStream fetchContentStream(Resource resource) throws IOException;
	InputStream fetchContentStream(String resourceFQName) throws IOException;
}
