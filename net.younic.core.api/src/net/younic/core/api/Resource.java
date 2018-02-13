package net.younic.core.api;

import java.io.Serializable;

public class Resource implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String path;
	private boolean container;
	private boolean hidden;
	
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
	
	@Override
	public String toString() {
		return "" + path+"/"+name;
	}
	
}
