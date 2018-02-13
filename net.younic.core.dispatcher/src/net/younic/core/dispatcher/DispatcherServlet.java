package net.younic.core.dispatcher;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

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
	
	@Reference
	private IResourceContentProvider resourceContentProvider;
	
	@Reference
	private IResourceRenderer render;
	
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		
		String[] interpretPath = interpretPath(pathInfo);
		Resource contentFolder = new Resource();
		contentFolder.setContainer(true);
		contentFolder.setPath(interpretPath[0]);
		
		Map<String, Serializable> contents = resourceContentProvider.readContents(contentFolder);	
		
		// read the template.ref
		String tmplRef = fetchTemplateRef(interpretPath[0]);
		
		response.setContentType("application/xhtml+xml");
		
		PrintWriter writer = response.getWriter();
		try {
			render.render(tmplRef == null ? "index" : tmplRef , prepareContext(contents), writer);
		} catch (ResourceRenderingFailedException e) {
			writer.write(e.getMessage());
		}

		writer.flush();
		writer.close();
	}
	
	private String fetchTemplateRef(String contentFolder) {
		
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
	
	// This code should be extracted to a TPL bundle as a service
	@SuppressWarnings("unchecked")
	private Map<String, Object> prepareContext(Map<String, Serializable> contents){
		Map<String, Object> result = new HashMap<>();
		for (Entry<String, Serializable> e : contents.entrySet()) {
			Object val = e.getValue();
			int fileExtSepPos = e.getKey().lastIndexOf('.');
			
			String entryName = e.getKey();
			if (fileExtSepPos > 0) {
				entryName = entryName.substring(0, fileExtSepPos);
			}
			int orderSepPos = e.getKey().lastIndexOf('$');
			if (orderSepPos > 0) {
				entryName = entryName.substring(0, orderSepPos);
				try {
					//int order = Integer.parseInt(entryName.substring(orderSepPos));
					List<Object> values = null;
					Object currentVal = result.get(entryName);
					if (currentVal == null) {
						values = new LinkedList<>();
					} else if (currentVal instanceof List<?>) {
						values = (List) currentVal;
					} else {
						values = new LinkedList<>();
						values.add(currentVal);
					}
					values.add(val);
					val = (Serializable) values;
				} catch (NumberFormatException ex) {
					//silent here
				}
			}
			result.put(entryName, val);
		}
		return result;
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