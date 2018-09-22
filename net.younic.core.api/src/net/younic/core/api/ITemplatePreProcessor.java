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
package net.younic.core.api;

/**
 * A Template Pre-Processor allows to hook in custom code to modify the plain
 * text (String) to be provided for template rendering. For example to remove some
 * XSS code from untrusted sources. Also, for admin purposes, it is possible to 
 * mark template code for ui presentations.
 * 
 * @author Andre Albert
 *
 */
public interface ITemplatePreProcessor {

	/**
	 * Called before Providing the template to to template engine.
	 * It should not return nothing, so that some template engines might
	 * not throw an exception.
	 * 
	 * @param content the content to process.
	 * @return the content to be proved to the template engine.
	 */
	String process(String content);
}
