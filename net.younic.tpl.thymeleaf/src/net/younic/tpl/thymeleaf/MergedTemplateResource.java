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
package net.younic.tpl.thymeleaf;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.templateresource.ITemplateResource;

import net.younic.core.api.ITemplatePreProcessor;

public class MergedTemplateResource implements ITemplateResource {

	private String charachterEncoding;
	private String resourceName;
	private String indexTpl;
	private String indexTplPath;
	private List<ITemplatePreProcessor> preProcessors;

	public MergedTemplateResource(String resourceName, String characterEncoding, String indexTplPath, List<ITemplatePreProcessor> preProcessors) {
		this.resourceName = resourceName;
		this.charachterEncoding = characterEncoding;
		this.indexTplPath = indexTplPath;
		this.preProcessors = preProcessors;
	}

	@Override
	public String getDescription() {
		return resourceName + " merged Template";
	}

	@Override
	public String getBaseName() {
		return null;
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public Reader reader() throws IOException {
		if (indexTpl == null) {
			indexTpl = new String(Files.readAllBytes(Paths.get(indexTplPath)), charachterEncoding);
		}
		
		String merged = indexTpl.replace("<!-- MAIN-TPL -->", resourceName.equals(indexTplPath) 
				? "" : new String(Files.readAllBytes(Paths.get(resourceName)), charachterEncoding));
		
		// hook in registered PreProcessor before providing Template code to engine
		if (preProcessors != null) {
			for (ITemplatePreProcessor processor : preProcessors) {
				merged = processor.process(merged);
			}
		}
		return new StringReader(merged);
	}

	@Override
	public ITemplateResource relative(String relativeLocation) {
		throw new TemplateInputException("Not supported for MergedTemplates");
	}

}
