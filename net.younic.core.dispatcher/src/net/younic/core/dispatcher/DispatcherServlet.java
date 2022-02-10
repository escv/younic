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
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.younic.content.IAggregatedResourceContentProvider;
import net.younic.core.api.IResourceContentProvider;
import net.younic.core.api.IResourceRenderer;
import net.younic.core.api.Resource;
import net.younic.core.api.ResourceRenderingFailedException;

@Component(
	property = {
		HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN + "=/*"
	})
public class DispatcherServlet extends HttpServlet implements Servlet {

	private static final long serialVersionUID = 1L;
	
	private static final Logger LOG = LoggerFactory.getLogger(DispatcherServlet.class);
		
	@Reference
	private IResourceContentProvider resourceContentProvider;
	
	@Reference
	private IAggregatedResourceContentProvider aggregatedResourceContentProvider;
	
	@Reference
	private IResourceRenderer renderer;
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String[] interpretPath = interpretPath(request.getPathInfo());
		Resource contentFolder = new Resource();
		contentFolder.setContainer(true);
		contentFolder.setPath(interpretPath[0]);
		contentFolder.setName(interpretPath[1]);
		
		Map<String, Object> contents = aggregatedResourceContentProvider.provideContents(contentFolder);

		long modifiedSRV = (Long)contents.get("net.younic.content.lastModified");
		long modidiedCLI = request.getDateHeader("If-Modified-Since");

		// cache control
		if (!Activator.DEV_MODE) {
			response.addHeader("Cache-Control", "must-revalidate, max-age=120");
			response.addDateHeader("Last-Modified", modifiedSRV);
			Calendar c = Calendar.getInstance();
			c.add(Calendar.MINUTE, 1);
			response.addDateHeader("Expires", c.getTimeInMillis());
			if (modidiedCLI != -1 && modifiedSRV <= modidiedCLI) {
				response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
		        return;
			}
		} else {
			response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			response.addDateHeader("Expires", 0L);
			response.addHeader("Pragma", "no-cache");		
		}
		
		if (".json".equalsIgnoreCase(interpretPath[2])) {
			// render to (x)html
			response.setContentType("application/json; charset=utf-8");
			IResourceRenderer jsonRenderer = new JsonResourceRenderer();
			try (PrintWriter writer = response.getWriter()){
				jsonRenderer.render(null, true, contents, writer);			
				writer.flush();
			} catch (ResourceRenderingFailedException e) {
				LOG.error("error rendering json", e);
			}
		} else {
			// render to (x)html
			response.setContentType("text/html; charset=utf-8");
	
			// read the template.ref
			String tmplRef = fetchTemplateRef(contentFolder);		
			
			try (PrintWriter writer = response.getWriter()){
				renderer.render(tmplRef == null ? "index" : tmplRef, false, contents, writer);			
				writer.flush();
			} catch (ResourceRenderingFailedException e) {
				LOG.error("error rendering "+tmplRef, e);
			}
		}
	}
	
	/**
	 * Looks up for a template reference with a bottom-up strategy starting form "contentFolder" param.
	 * @param contentFolder
	 * @return the name of the template file to use
	 * @throws IOException
	 */
	private String fetchTemplateRef(Resource contentFolder) throws IOException {
		String contentFolderPath = contentFolder.qualifiedName();
		LinkedList<String> parts = new LinkedList<String>(Arrays.asList(contentFolderPath.split("/")));
		do {
			String path = parts.stream().collect(Collectors.joining("/"));
			String content = resourceContentProvider.readContent(path+"/template.ref");
			if (content != null) {
				return content.trim();
			}
		} while (parts.pollLast() != null);
		return null;
	}
	
	/**
	 * Interpretes the Request path to match it with the filesystem resources.
	 * Direct access to /template and /bundles is prohibited.
	 * If not already in path, the content folder will be prefixed.
	 * Could later by a own OSGi component to allow Vanity URLs and other matchers.
	 * 
	 * @param pathInfo as obtained by servlet request, might be null
	 * @return an 3-elem. Array with 0: the folder 1: the filename 2: the file extension
	 * @throws ServletException on not allowed path access
	 */
	private String[] interpretPath(String pathInfo) throws ServletException {
		String[] result =  new String[3];
		if (pathInfo == null) {
			pathInfo = "/content.html";
		}
		
		if (pathInfo.startsWith("/template") || pathInfo.startsWith("/bundles")) {
			LOG.warn("Illegal Request to either template or bundles forlder was blocked");
			throw new ServletException("Illegal Request");
		}
		
		if (!pathInfo.startsWith(Resource.RESOURCE_RESOURCE_FOLDER) && !pathInfo.startsWith(Resource.RESOURCE_CONTENT_FOLDER)) {
			pathInfo = Resource.RESOURCE_CONTENT_FOLDER+pathInfo;
		}
		
		pathInfo = pathInfo.replace("..", "");
		if (pathInfo.charAt(0) != '/') {
			pathInfo = "/"+pathInfo;
		}
		
		int typeSepPos = pathInfo.lastIndexOf('.');
		if (typeSepPos>0) {
			String path = pathInfo.substring(0, typeSepPos);
			int lastPathSep = path.lastIndexOf('/');
			if (lastPathSep>=0) {
				result[0] = path.substring(0, lastPathSep);
				result[1] = path.substring(lastPathSep+1);
			} else {
				result[0] = "/";
				result[1] = path;
			}			
			result[2] = pathInfo.substring(typeSepPos);
		}
		
		return result;
	}
}