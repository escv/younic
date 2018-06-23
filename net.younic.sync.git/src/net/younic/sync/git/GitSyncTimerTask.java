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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import net.younic.sync.SyncTimerTask;

/**
 * @author Andre Albert
 *
 */
public class GitSyncTimerTask extends SyncTimerTask {

	private File docroot;
	
	private static final String NO_CHANGES_CONTAINS = "up to date";
	private static final String WITH_CHANGES_CONTAINS = "Updating";

	public GitSyncTimerTask(String docroot) {
		super();
		this.docroot = new File(docroot);
	}


	/* (non-Javadoc)
	 * @see net.younic.sync.SyncTimerTask#sync()
	 */
	@Override
	protected boolean sync() throws IOException {
		
		Process p = Runtime.getRuntime().exec("git pull", new String[0], this.docroot);
	    try {
			p.waitFor();
			BufferedReader reader = 
					new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			String line = "";
			StringBuilder sb = new StringBuilder();
			while ((line = reader.readLine())!= null) {
				sb.append(line + "\n");
			}
			
			String result = sb.toString();
			if (result.contains(NO_CHANGES_CONTAINS)) {
				return false;
			} else if (result.contains(WITH_CHANGES_CONTAINS)) {
				return true;
			}
		} catch (InterruptedException e) {
			throw new IOException("Error executing Process", e);
		}

		return false;
	}

}
