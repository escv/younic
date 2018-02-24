package net.younic.core.api;

import java.io.Serializable;

public class Resource implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * name of the node element or null if it is a folder (container = true)
	 */
	private String name;
	private String path;
	private boolean container;
	private boolean hidden;
	private long lastModified;
	
	public Resource() {
		super();
	}

	public Resource(String path, String name, boolean container) {
		super();
		this.name = name;
		this.path = path;
		this.container = container;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isContainer() {
		return container;
	}

	public void setContainer(boolean container) {
		this.container = container;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	
	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	@Override
	public String toString() {
		return "" + path+"/"+name;
	}
	
}
