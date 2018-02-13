package net.younic.core.fs;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.LinkedList;

import org.osgi.framework.BundleException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import net.younic.core.api.IResourceProvider;
import net.younic.core.api.Resource;

@Component(
	service=IResourceProvider.class
)
public class FileSystemResourceProvider implements IResourceProvider {

	private File docroot;
	private FilenameFilter fileFilter;
	
	@Activate
	public void activate(ComponentContext context) throws BundleException {
		this.docroot = new File(context.getBundleContext().getProperty("net.younic.cms.root"));
		this.fileFilter = new NonTechnicalFilter();
		if (this.docroot == null) {
			throw new BundleException("Missing Property \"net.younic.cms.root\"");
		}
	}
	
	@Override
	public Collection<Resource> list(String pathSpec) {
		Collection<Resource> result = new LinkedList<>();
		File folder = new File(docroot, pathSpec);
		
		if (folder.isDirectory()) {
			for (File elem : folder.listFiles(this.fileFilter)) {
				Resource r = new Resource();
				r.setContainer(elem.isDirectory());
				r.setName(elem.getName());
				r.setPath(pathSpec);
				result.add(r);
			}
		}
		
		return result;
	}
	
	@Override
	public Collection<Resource> list(String... pathSpec) {
		final String pathSpecSerial = String.join(File.separator, pathSpec);
		return list(pathSpecSerial);
	}

}
