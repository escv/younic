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
package net.younic.core.dispatcher;


import java.io.File;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.younic.core.dispatcher.api.YounicHttpContext;

/**
 * @author Andre Albert
 */
public class Activator implements BundleActivator {

	private static final Logger LOG = LoggerFactory.getLogger(Activator.class);
	
	private ServiceTracker<HttpService, HttpService> httpServiceTracker;

	private File docroot;
    
	/* (non-Javadoc).
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		this.docroot = new File(context.getProperty("net.younic.cms.root"),"resource/");
		LOG.info("Starting dispatch for cms-root: "+this.docroot.getParentFile().getAbsolutePath());

		setupFileInstallDeployFolders(context);
		
		if (!docroot.exists()) {
			LOG.info("Disabling docroot due to missing net.younic.cms.root Property");
			return;
		}

		httpServiceTracker = new ServiceTracker<HttpService, HttpService>(context, HttpService.class.getName(), null){

			@Override
			public HttpService addingService(ServiceReference<HttpService> reference) {
				LOG.debug("Looking for HttpService"); 
		        
		        if (reference != null) {
		        		final HttpService httpService = context.getService(reference);
		        		if(httpService!=null) { 
		                LOG.debug("Found HttpSerice!"); 
		                try {
							httpService.registerResources("/resource", docroot.getAbsolutePath(), new YounicHttpContext());
							LOG.info("ResourceServlet "+docroot.getAbsolutePath()+" launched");
						} catch (NamespaceException e) {
							LOG.error("Error starting Resoruce Service for /resource", e);
						}
		            }else{ 
		                LOG.warn("httpServiceTracker.getService() returned null"); 
		            }
		        } else {
		        		LOG.warn("No HttpService Reference found");
		        }
				return super.addingService(reference);
			}
		};
        httpServiceTracker.open();        
	}

	/**
	 * @param context
	 */
	private void setupFileInstallDeployFolders(BundleContext context) {
		File extBundleDir = new File(context.getProperty("net.younic.cms.root"),"bundles/");
		addFileInstallDeployFolder(extBundleDir);
		String runAdmin = System.getenv("YOUNIC_RUN_ADMIN");
		LOG.info("Check if run in Admin Mode: "+runAdmin);
		if (runAdmin.equals("true")) {
	        File admBundleDir = new File("bundle-adm");
			addFileInstallDeployFolder(admBundleDir);
		}
	}

	/**
	 * @param extBundleDir
	 * @return
	 */
	private void addFileInstallDeployFolder(File extBundleDir) {
		String fileinstallDir = System.getProperty("felix.fileinstall.dir", "").trim();
		if (extBundleDir.exists() && extBundleDir.isDirectory()) {
			if (!fileinstallDir.isEmpty()) {
				fileinstallDir += ",";
			}
			fileinstallDir += extBundleDir.getAbsolutePath();
			System.setProperty("felix.fileinstall.dir", fileinstallDir);
			LOG.info("New Felix.Fileinstall.Dir: "+System.getProperty("felix.fileinstall.dir"));
		}
	}

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		httpServiceTracker.close();
	}

}
