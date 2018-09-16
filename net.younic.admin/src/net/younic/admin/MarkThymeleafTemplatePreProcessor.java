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
package net.younic.admin;

import org.osgi.service.component.annotations.Component;

import net.younic.core.api.ITemplatePreProcessor;

/**
 * @author Andre Albert
 *
 */
@Component(service=ITemplatePreProcessor.class)
public class MarkThymeleafTemplatePreProcessor implements ITemplatePreProcessor {

	private static final String SHOW_TH_CSS = "<style type=\"text/css\">[data-tpl-text=\"true\"], [data-tpl-utext=\"true\"] {border:2px solid blue;} \n [data-tpl-each=\"true\"] {border:2px solid red !important;}</style>";
	/* (non-Javadoc)
	 * @see net.younic.core.api.ITemplatePreProcessor#process(java.lang.String)
	 */
	@Override
	public String process(String content) {
		
		String processed = content.replaceAll(" th:([a-z]+)=", " data-tpl-$1=\"true\" th:$1=");
		processed = processed.replaceAll("</head>", SHOW_TH_CSS+"</head>");
		return processed;
	}

}
