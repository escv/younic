package net.younic.tpl.thymeleaf;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.templateresource.ITemplateResource;

public class MergedTemplateResource implements ITemplateResource {

	private String charachterEncoding;
	private String resourceName;
	private String indexTpl;
	private String indexTplPath;

	public MergedTemplateResource(String resourceName, String characterEncoding, String indexTplPath) {
		this.resourceName = resourceName;
		this.charachterEncoding = characterEncoding;
		this.indexTplPath = indexTplPath;
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
			indexTpl = new String(Files.readAllBytes(Paths.get(indexTplPath)));
		}
		String merged = indexTpl.replace("<!-- MAIN-TPL -->", new String(Files.readAllBytes(Paths.get(resourceName))));
		return new StringReader(merged);
	}

	@Override
	public ITemplateResource relative(String relativeLocation) {
		throw new TemplateInputException("Not supported for MergedTemplates");
	}

}
