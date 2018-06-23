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
package net.younic.sync.git;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.younic.sync.ISyncTimerService;

/**
 * @author Andre Albert
 *
 */
public class Activator implements BundleActivator {

	private static final Logger LOG = LoggerFactory.getLogger(Activator.class);
	
	
	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		ServiceReference<ISyncTimerService> timer = context.getServiceReference(ISyncTimerService.class);
		if (timer == null) {
			LOG.warn("Sync Timer Service is not available");
			return;
		}
		ISyncTimerService service = context.getService(timer);
		String docroot = context.getProperty("net.younic.cms.root");
		if (docroot == null) {
			throw new BundleException("Missing Property \"net.younic.cms.root\"");
		}
		service.registerSyncTimerTask(new GitSyncTimerTask(docroot));

	}

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		ServiceReference<ISyncTimerService> timer = context.getServiceReference(ISyncTimerService.class);
		ISyncTimerService service = context.getService(timer);
		service.cancel();

	}

}
