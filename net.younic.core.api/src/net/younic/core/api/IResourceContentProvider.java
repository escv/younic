package net.younic.core.api;

import java.io.File;
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
	
	/**
	 * Fetches up the resource and returns a file (if any). This operation can return null
	 * if the requested resource is not file based.
	 * @param resource the abstract resource
	 *
	 * @return a File handle
	 * @throws IOException in case of errors creating a file
	 */
			
	File fetchContentFile(Resource resource) throws IOException;
}
