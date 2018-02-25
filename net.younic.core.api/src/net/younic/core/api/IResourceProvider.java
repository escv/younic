package net.younic.core.api;

import java.util.Collection;

import org.osgi.annotation.versioning.ConsumerType;

@ConsumerType
public interface IResourceProvider {

	Collection<Resource> list(String... pathSpec);
	
	Collection<Resource> list(String pathSpec);
	
	Resource fetchResource(String pathSpec);
}
