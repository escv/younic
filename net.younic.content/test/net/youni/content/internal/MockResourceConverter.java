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
package net.youni.content.internal;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import net.younic.content.IResourceConverter;
import net.younic.core.api.Resource;

/**
 * @author Andre Albert
 *
 */
class MockResourceConverter implements IResourceConverter {

	private int rank;
	private File docRoot;

	public MockResourceConverter(int rank) {
		super();
		this.rank = rank;
	}

	public MockResourceConverter(int rank, File docRoot) {
		super();
		this.rank = rank;
		this.docRoot = docRoot;
	}

	@Override
	public Object convert(Resource resource) throws IOException {
		File dest = new File (new File(docRoot, resource.getPath()), resource.getName());
		return new String(Files.readAllBytes(Paths.get(dest.getPath())));
	}

	@Override
	public boolean handles(Resource resource) {
		return docRoot!=null;
	}

	@Override
	public int rank() {
		return rank;
	}
	
}
