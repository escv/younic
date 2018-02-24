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

	private final String[] homePage = new String[] {"/", "html"};
	
	private static final Logger LOG = LoggerFactory.getLogger(DispatcherServlet.class);
	
	@Reference
	private IResourceContentProvider resourceContentProvider;
	
	@Reference
	private IAggregatedResourceContentProvider aggregatedResourceContentProvider;
	
	@Reference
	private IResourceRenderer renderer;
	
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		LOG.trace("Dispatch "+pathInfo);
		
		String[] interpretPath = interpretPath(pathInfo);
		Resource contentFolder = new Resource();
		contentFolder.setContainer(true);
		contentFolder.setPath(interpretPath[0]);
		
		Map<String, Object> contents = aggregatedResourceContentProvider.provideContents(contentFolder);

		long modifiedSRV = (Long)contents.get("net.younic.content.lastModified");
		long modidiedCLI = request.getDateHeader("If-Modified-Since");
		response.addDateHeader("Last-Modified", modifiedSRV);
		if (modidiedCLI != -1 && modifiedSRV <= modidiedCLI) {
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
	        return;
		}

		// read the template.ref
		String tmplRef = fetchTemplateRef(interpretPath[0]);
		
		response.setContentType("application/xhtml+xml");
		
		PrintWriter writer = response.getWriter();
		try {
			renderer.render(tmplRef == null ? "index" : tmplRef , contents, writer);
		} catch (ResourceRenderingFailedException e) {
			writer.write(e.getMessage());
		}

		writer.flush();
		writer.close();
	}
	
	private String fetchTemplateRef(String contentFolder) throws IOException {
		
		LinkedList<String> parts = new LinkedList<String>(Arrays.asList(contentFolder.split("/")));
		do {
			String path = parts.stream().collect(Collectors.joining("/"));
			String content = resourceContentProvider.readContent(path+"/template.ref");
			if (content != null) {
				return content;
			}
		} while (parts.pollLast() != null);
		return null;
	}
	
	private String[] interpretPath(String pathInfo) {
		String[] result =  new String[2];
		if (pathInfo == null) {
			return homePage;
		}
		
		pathInfo = pathInfo.replace("..", "");
		
		int typeSepPos = pathInfo.lastIndexOf('.');
		if (typeSepPos>0) {
			result[0] = pathInfo.substring(0, typeSepPos);
			result[1] = pathInfo.substring(typeSepPos);
		}
		
		return result;
		
	}
}