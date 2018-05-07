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

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

import net.younic.core.api.WhiteboardUtil;
import net.younic.core.dispatcher.api.IResourceAPIService;

/**
 * @author Andre Albert
 *
 */
@Component(
		property = {
			HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN + "=/api/*"
		})
public class APIServlet extends HttpServlet implements Servlet {

	private static final long serialVersionUID = 1L;
	
	@Reference(policy=ReferencePolicy.DYNAMIC)
	final List<IResourceAPIService> apiServices = new CopyOnWriteArrayList<>();
	
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 */
	@Override
	public void service(ServletRequest req, ServletResponse resp) throws ServletException, IOException {
		HttpServletRequest hreq = (HttpServletRequest)req;
		HttpServletResponse hres = (HttpServletResponse) resp;
		
		if (apiServices == null || apiServices.isEmpty()) {
			hres.sendError(404);
		}
		String pathInfo = hreq.getPathInfo();
		if (pathInfo.charAt(0) == '/') {
			pathInfo = pathInfo.substring(1);
		}
		int firstSep = pathInfo.indexOf('/');
		String matcher = "";
		String path = "";
		if (firstSep >= 0) {
			matcher = pathInfo.substring(0, firstSep);
			path = pathInfo.substring(firstSep+1);
		}
		IResourceAPIService handler = WhiteboardUtil.findHandler(IResourceAPIService.class, apiServices, matcher);
		
		resp.setContentType(handler.contentType());
		hres.addHeader("Access-Control-Allow-Origin", "*");
		hres.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
		ServletOutputStream out = resp.getOutputStream();
		
		if ("POST".equalsIgnoreCase(hreq.getMethod()) || "PUT".equalsIgnoreCase(hreq.getMethod())) {
			ServletInputStream in = hreq.getInputStream();
			handler.write(path, in, out);
		} else if ("DELETE".equalsIgnoreCase(hreq.getMethod())) {
			handler.delete(path);
		} else {
			handler.read(path, out);
		}
		out.flush();
		out.close();
	}

}
