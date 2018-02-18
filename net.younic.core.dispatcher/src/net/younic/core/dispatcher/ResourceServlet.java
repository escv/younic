/* Copyright 2007 Alin Dreghiciu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.younic.core.dispatcher;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.HttpOutput;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.resource.FileResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.osgi.framework.BundleException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// This source code was copied and modified from https://github.com/ops4j/org.ops4j.pax.web/blob/master/pax-web-jetty/src/main/java/org/ops4j/pax/web/service/jetty/internal/ResourceServlet.java


@Component(
		property = {
			HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN + "=/resource/*"
		})
public class ResourceServlet extends HttpServlet implements Servlet, ResourceFactory {

	private static final int SECOND = 1000;

	private boolean devMode = false;

	private static final long serialVersionUID = 1L;

	// header constants
	// CHECKSTYLE:OFF
	private static final String IF_NONE_MATCH = "If-None-Match";
	@SuppressWarnings("unused")
	private static final String IF_MATCH = "If-Match";
	private static final String IF_MODIFIED_SINCE = "If-Modified-Since";
	@SuppressWarnings("unused")
	private static final String IF_RANGE = "If-Range";
	private static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
	@SuppressWarnings("unused")
	private static final String KEEP_ALIVE = "Keep-Alive";
	private static final String ETAG = "ETag";
	// CHECKSTYLE:ON

	private String docroot = "/Users/aalbert/Development/cynic/root/resource";
	
	private static final Logger LOG = LoggerFactory
			.getLogger(ResourceServlet.class);

	//private final HttpContext httpContext;
	//private final String contextName;
	//private final String alias;
//	private final String name;
	private final MimeTypes mimeTypes = new MimeTypes();

	
	@Activate
	public void activate(ComponentContext context) throws BundleException {
		this.devMode = "true".equals(context.getBundleContext().getProperty("net.younic.devmode"));
	}

	/**
	 * Compute the field _contextHandler.<br/>
	 * In the case where the DefaultServlet is deployed on the HttpService it is
	 * likely that this method needs to be overwritten to unwrap the
	 * ServletContext facade until we reach the original jetty's ContextHandler.
	 *
	 * @param servletContext The servletContext of this servlet.
	 * @return the jetty's ContextHandler for this servletContext.
	 */
	protected ContextHandler initContextHandler(ServletContext servletContext) {
		ContextHandler.Context scontext = ContextHandler.getCurrentContext();
		if (scontext == null) {
			if (servletContext instanceof ContextHandler.Context) {
				return ((ContextHandler.Context) servletContext)
						.getContextHandler();
			} else {
				throw new IllegalArgumentException("The servletContext "
						+ servletContext + " "
						+ servletContext.getClass().getName() + " is not "
						+ ContextHandler.Context.class.getName());
			}
		} else {
			return ContextHandler.getCurrentContext().getContextHandler();
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void service(final HttpServletRequest request,
						 final HttpServletResponse response) throws ServletException,
			IOException {
		if (response.isCommitted()) {
			return;
		}

		Boolean included = request
				.getAttribute(RequestDispatcher.INCLUDE_REQUEST_URI) != null;
		String pathInfo = null;

		if (included != null && included) {
			String servletPath = (String) request
					.getAttribute(RequestDispatcher.INCLUDE_SERVLET_PATH);
			pathInfo = (String) request
					.getAttribute(RequestDispatcher.INCLUDE_PATH_INFO);
			if (servletPath == null) {
				servletPath = request.getServletPath();
				pathInfo = request.getPathInfo();
			}
		} else {
			included = Boolean.FALSE;
			pathInfo = request.getPathInfo();
		}
		LOG.trace("Requesting resource "+pathInfo);
		
		Resource resource = getResource(pathInfo);
		resource.setAssociate(response);

		try {

			if (resource == null || !resource.exists()) {
				if (!response.isCommitted()) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
				}
				return;
			}


			if (resource != null && resource.isDirectory()) {
				// directory listing
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}

			// if the request contains an etag and its the same for the
			// resource, we deliver a NOT MODIFIED response
			String eTag = String.valueOf(resource.lastModified());
			if ((request.getHeader(IF_NONE_MATCH) != null)
					&& (eTag.equals(request.getHeader(IF_NONE_MATCH)))) {
				response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
				return;
			} else if (request.getHeader(IF_MODIFIED_SINCE) != null) {
				long ifModifiedSince = request.getDateHeader(IF_MODIFIED_SINCE);
				if (resource.lastModified() != -1) {
					// resource.lastModified()/1000 <= ifmsl/1000
					if (resource.lastModified() / SECOND <= ifModifiedSince / SECOND) {
						response.reset();
						response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
						response.flushBuffer();
						return;
					}
				}
			} else if (request.getHeader(IF_UNMODIFIED_SINCE) != null) {
				long modifiedSince = request.getDateHeader(IF_UNMODIFIED_SINCE);

				if (modifiedSince != -1) {
					if (resource.lastModified() / SECOND > modifiedSince / SECOND) {
						response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
						return;
					}
				}
			}

			// set the etag
			//response.setHeader(ETAG, eTag);
			String extension = resource.getName().substring(resource.getName().lastIndexOf('.'));
			String mimeType = mimeTypes.getMimeByExtension(extension);

			if (mimeType != null) {
				response.setContentType(mimeType);
			}
			if (!devMode) {
				addCacheControl(response);
			}

			OutputStream out = response.getOutputStream();
			if (out != null) { // null should be just in unit testing
				if (out instanceof HttpOutput) {
					((HttpOutput) out).sendContent(resource.getInputStream());
				} else {
					// Write content normally
					resource.writeTo(out, 0, resource.length());
				}
			}
			response.setStatus(HttpServletResponse.SC_OK);
		} finally {
			resource.close();
		}
	}

	private void addCacheControl(HttpServletResponse response) {
		int cacheAge = 5*60; // 5 minutes
		long expiry = System.currentTimeMillis() + cacheAge *1000;
		response.setDateHeader("Expires", expiry);
	    response.setHeader("Cache-Control", "max-age="+ cacheAge);
		
	}
	@Override
	public Resource getResource(String path) {
		File resFile = new File (docroot, path);
		if (resFile.exists() && resFile.canRead()) {
			return new FileResource(resFile.toURI());
		}
		return null;
	}

}