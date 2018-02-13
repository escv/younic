package net.younic.core.fs;

import java.io.File;
import java.io.FilenameFilter;

final class NonTechnicalFilter implements FilenameFilter {
	@Override
	public boolean accept(File dir, String name) {
		return name.charAt(0) != '.' && !"TEMPLATE.REF".equalsIgnoreCase(name);
	}
}