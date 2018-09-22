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

	//private boolean devMode;
    
	/* (non-Javadoc).
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		//this.devMode = "true".equals(context.getProperty("net.younic.devmode"));
		this.docroot = new File(context.getProperty("net.younic.cms.root"),"resource/");

		File extBundleDir = new File(context.getProperty("net.younic.cms.root"),"bundles/");
		if (extBundleDir.exists() && extBundleDir.isDirectory()) {
			System.setProperty("felix.fileinstall.dir", extBundleDir.getAbsolutePath());
		}
		
		LOG.info("Starting resource Servlet for "+docroot);
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
							LOG.info("/resource launched");
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

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		httpServiceTracker.close();
	}

}
