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
package net.younic.core.dispatcher.api;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.http.HttpContext;

/**
 * @author Andre Albert
 *
 */
public class YounicHttpContext implements HttpContext {
	
	/* (non-Javadoc)
	 * @see org.osgi.service.http.HttpContext#handleSecurity(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) throws IOException {
		return true;
	}

	/* (non-Javadoc)d
	 * @see org.osgi.service.http.HttpContext#getResource(java.lang.String)
	 */
	@Override
	public URL getResource(String name) {
		try {
			File file = new File(name);
			if (file.isDirectory()) {
				file = new File(file, "index.html");
			}
			if (!file.exists()) {
				return null;
			}
			return file.toURI().toURL();
		} catch (MalformedURLException e) {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.osgi.service.http.HttpContext#getMimeType(java.lang.String)
	 */
	@Override
	public String getMimeType(String name) {
		String file = name.toLowerCase();
		if (file.endsWith(".css")) {
			return "text/css";
		} else if (file.endsWith(".js")) {
			return "text/javascript";
		} else if (file.endsWith(".html")) {
			return "text/html";
		} else if (file.endsWith(".txt")) {
			return "text/plain";
		} else if (file.endsWith(".jpg") || name.endsWith(".jpeg")) {
			return "image/jpeg";
		} else if (file.endsWith(".png")) {
			return "image/png";
		} else if (file.endsWith(".giv")) {
			return "image/gif";
		}
		
		return "application/octet-stream";
	}

}
