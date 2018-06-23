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

import java.io.IOException;
import java.util.TimerTask;

/**
 * @author Andre Albert
 *
 */
public abstract class SyncTimerTask extends TimerTask {

	private IPostResourceModified eventPoster;
	
	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run() {
		//throw events here
		try {
			if (sync()) {
				//throw modified event here
				eventPoster.postModified();
			}
		} catch (IOException e) {
			// TODO: handle exception
		}
		// throw done event here

	}

	/**
	 * @return true if something was changed on sync execution
	 */
	protected abstract boolean sync() throws IOException;
}
