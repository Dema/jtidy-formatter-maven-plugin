package org.dema;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.plexus.util.DirectoryScanner;
import org.w3c.tidy.Tidy;

/**
 * Goal which touches a timestamp file.
 *
 * @goal tidy
 *
 * @phase process-sources
 */
public class JTidyFormatter
				extends AbstractMojo {

	private Tidy tidy;
	/**
	 * JTidy properties
	 *
	 * @parameter
	 */
	private Properties jtidyConfiguration;
	/**
	 * @parameter default-value="${basedir}/src/main/webapp/"
	 * @required
	 */
	private File sourceDir;
	/**
	 * @parameter
	 */
	private String[] includes;
	/**
	 * @parameter
	 */
	private String[] excludes;

	public void execute()
					throws MojoExecutionException {
		tidy = new Tidy();




		if (jtidyConfiguration == null) {

			jtidyConfiguration = new Properties();
		}
		Properties config = new Properties();

		config.put("input-encoding", "UTF-8");
		config.put("output-encoding", "UTF-8");
		config.put("input-xml", "true");
		config.put("output-xml", "true");
		config.put("indent", "true");
		config.put("wrap", "120");
		config.put("write-back", "true");

		config.putAll(jtidyConfiguration);

		tidy.setConfigurationFromProps(config);
		if (includes == null) {
			includes = new String[]{"**/*.xhtml"};
		}

		final DirectoryScanner directoryScanner = new DirectoryScanner();
		directoryScanner.setIncludes(includes);
		if (excludes != null) {
			directoryScanner.setExcludes(excludes);
		}
		directoryScanner.setBasedir(sourceDir);
		directoryScanner.scan();


		for (String fileName : directoryScanner.getIncludedFiles()) {
			try {
				File dest = new File(sourceDir, fileName + ".tmp");
				final File file = new File(sourceDir, fileName);

				tidy.parse(new FileInputStream(file), new FileOutputStream(dest));

				dest.renameTo(file);

			} catch (FileNotFoundException ex) {
				Logger.getLogger(JTidyFormatter.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

	}
}
