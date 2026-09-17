package net.younic.core.dispatcher;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.EventConstants;

import net.younic.core.api.IResourceRenderer;
import net.younic.core.api.Resource;
import net.younic.core.api.ResourceRenderingFailedException;
import net.younic.core.api.YounicEventsConstants;

public class JsonResourceRenderer implements IResourceRenderer {

	@Override
	public void render(String tpl, boolean plain, Map<String, Object> context, Writer out)
			throws ResourceRenderingFailedException {
		try {
			out.append("{");
			boolean first=true;
			for (Entry<String, Object> entry : context.entrySet()) {
				if (!first) {
					out.append(",");
				} else {
					first = false;
				}
				out.append("\""+escapeForJSON(entry.getKey())+"\":");
				Object val = entry.getValue();
				jsonSerializeValue(out, val);
			}
			out.append("}");
		} catch (IOException e) {
			throw new ResourceRenderingFailedException("Error rendering context to json", e);
		}

	}

	private void jsonSerializeValue(Writer out, Object val) throws IOException {
		if (val == null) {
			out.append("null");
		} else if (val instanceof String) {
			out.append("\""+escapeForJSON((String)val)+"\"");
		} else if (val instanceof Number || val instanceof Boolean) {
			out.append(val.toString());
		} else if (val instanceof Map<?,?>) {
			out.append("{");
			boolean first=true;
			for (Entry<?,?> obj : ((Map<?,?>)val).entrySet()) {
				if (!first) {
					out.append(",");
				} else {
					first = false;
				}
				out.append("\""+escapeForJSON(String.valueOf(obj.getKey()))+"\":");
				jsonSerializeValue(out, obj.getValue());
			}
			out.append("}");
		} else if (val instanceof Resource) {
			out.append("\""+escapeForJSON(((Resource)val).qualifiedName())+"\"");
		} else if (val instanceof Iterable<?>) {
			out.append("[");
			boolean first=true;
			for (Object obj : ((Iterable<?>)val)) {
				if (!first) {
					out.append(",");
				} else {
					first = false;
				}
				jsonSerializeValue(out, obj);
			}
			out.append("]");
		} else if (val instanceof String[]) {
			out.append("[");
			boolean first=true;
			for (String obj : ((String[])val)) {
				if (!first) {
					out.append(",");
				} else {
					first = false;
				}
				jsonSerializeValue(out, obj);
			}
			out.append("]");
		}
	}

	private String escapeForJSON(String str) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			switch (c) {
			case '"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			default:
				if (c < 0x20) {
					sb.append(String.format("\\u%04x", (int) c));
				} else {
					sb.append(c);
				}
			}
		}
		return sb.toString();
	}
}
