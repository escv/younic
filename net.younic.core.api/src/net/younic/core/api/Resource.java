package net.younic.core.api;

import java.io.Serializable;

public class Resource implements Serializable, Comparable<Resource> {

	private static final long serialVersionUID = 1L;

	private String name;
	private String displayName;
	private String path;
	private boolean container;
	private boolean hidden;
	private long lastModified;
	private long size;
	
	public Resource() {
		super();
		this.size=0L;
		this.lastModified=0L;
	}

	public Resource(String path, String name, boolean container) {
		this();
		this.name = name;
		this.path = path;
		this.displayName = fetchDisplayName(name);
		this.container = container;
		if (this.path == null || this.path.isEmpty()) {
			this.path = "/";
		} else if (this.path.charAt(0) != '/') {
			this.path = "/"+this.path;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.displayName = fetchDisplayName(name);
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

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
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

	
	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	/**
	 * Identified the resource. A Concatenation of path and name.
	 * @return a string as Resource identification
	 */
	public String qualifiedName() {
		// for root folder listing, exclusion 
		if ("/".equals(path)) {
			return "/"+name;
		}
		return "" + path+"/"+name;
	}
	@Override
	public String toString() {
		return qualifiedName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Resource) {
			return this.qualifiedName().equals(((Resource)obj).qualifiedName());
		}
		return false;
	}
	
	private String fetchDisplayName(String name) {
		if (name == null || name.length()==0) {
			return "";
		}
		if (Character.isDigit(name.charAt(0))) {
			int indexOf = name.indexOf('-');
			if (indexOf > 0) {
				name = name.substring(indexOf+1);
			}
		}
		name = name.replace("_", " ");
		name = name.substring(0, 1).toUpperCase() + name.substring(1);

		return name;
	}
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Resource o) {
		return getName().compareTo(o.getName());
	}
}
