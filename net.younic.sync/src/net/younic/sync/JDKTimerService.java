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
package net.younic.sync;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import net.younic.core.api.YounicEventsConstants;

/**
 * @author Andre Albert
 *
 */
@Component(service=ISyncTimerService.class)
public class JDKTimerService implements ISyncTimerService, IPostResourceModified {

	private Timer timer;

	@Reference
    private EventAdmin eventAdmin;
	
	@Deactivate
	void deactivate() {
		timer = null;
	}

	@Activate
	void activate() {
		timer = new Timer("YOUNIC :: timer", false);
	}
	
	/* (non-Javadoc)
	 * @see net.younic.sync.internal.IPostResourceModified#postModified()
	 */
	@Override
	public void postModified() {
		if (eventAdmin != null) {
			Map<String, String> props = new HashMap<String, String>();
			eventAdmin.postEvent(new Event(YounicEventsConstants.RESOURCE_MODIFIED, props));
		}
	}

	/* (non-Javadoc)
	 * @see net.younic.sync.ISyncTimerService#registerSyncTimerTask(net.younic.sync.SyncTimerTask)
	 */
	@Override
	public void registerSyncTimerTask(SyncTimerTask task) {
		timer.schedule(task, 1000, 10000);
		
	}

	/* (non-Javadoc)
	 * @see net.younic.sync.ISyncTimerService#removeSyncTimerTask(net.younic.sync.SyncTimerTask)
	 */
	@Override
	public void cancel() {
		timer.cancel();
	}

}
