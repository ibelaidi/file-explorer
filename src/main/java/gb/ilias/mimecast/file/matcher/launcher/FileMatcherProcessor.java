/**
 *
 */
package gb.ilias.mimecast.file.matcher.launcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author ilias
 * @since Oct 12, 2016
 */
@Component
public class FileMatcherProcessor {

	private final Logger			LOG	= LoggerFactory.getLogger(FileMatcherProcessor.class);

	@Autowired
	private JobLauncherTestUtils	jobLauncherTestUtils;

	/**
	 * Input Folder
	 */
	private String					inputFolder;

	/**
	 * Output folder
	 */
	private String					outputFolder;

	/**
	 * Regular Expression
	 */
	private String					regularExpression;

	/**
	 * Default constructor. Does nothing.
	 */
	public FileMatcherProcessor() {
	}

	/**
	 * Constructor with predefined values.
	 *
	 * @param input
	 *            Input Directory
	 * @param output
	 *            Output Directory
	 * @param regExp
	 *            Regular Expression being matched.
	 */
	public FileMatcherProcessor(String input, String output, String regExp) {
		this.inputFolder = input;
		this.outputFolder = output;
		this.regularExpression = regExp;
	}

	/**
	 * Main method to launch the complete process
	 *
	 * @throws Exception
	 */
	public void launchJob() throws Exception {
		// Job parameters
		final List<String> directories = this.walkinDirectories(new File(this.inputFolder));
		for (final String directory : directories) {
			final Map<String, JobParameter> jobParametersMap = new HashMap<String, JobParameter>();
			jobParametersMap.put("time", new JobParameter(System.currentTimeMillis()));
			jobParametersMap.put("input.file.pattern", new JobParameter("file:" + directory + "/*"));
			jobParametersMap.put("output.dir", new JobParameter("file:" + this.outputFolder));
			jobParametersMap.put("str.regexp", new JobParameter(this.regularExpression));

			// launch the job
			final JobExecution jobExecution = this.jobLauncherTestUtils.launchJob(new JobParameters(
					jobParametersMap));
		}
	}

	/**
	 * Lists all directories within the given path
	 *
	 * @param dir
	 *            A file object defining the top directory
	 **/
	private List<String> walkinDirectories(File dir) {
		final List<String> directories = new ArrayList<String>();
		directories.add(dir.getAbsolutePath());
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir.toPath())) {
			for (final Path entry : stream) {
				if (entry.toFile().isDirectory()) {
					directories.add(entry.toString());
				}
			}
		} catch (final IOException e) {
			this.LOG.error(e.getMessage(), e);
		}
		return directories;
	}

	/**
	 * @return the inputFolder
	 */
	public String getInputFolder() {
		return this.inputFolder;
	}

	/**
	 * @param inputFolder
	 *            the inputFolder to set
	 */
	public void setInputFolder(String inputFolder) {
		this.inputFolder = inputFolder;
	}

	/**
	 * @return the outputFolder
	 */
	public String getOutputFolder() {
		return this.outputFolder;
	}

	/**
	 * @param outputFolder
	 *            the outputFolder to set
	 */
	public void setOutputFolder(String outputFolder) {
		this.outputFolder = outputFolder;
	}

	/**
	 * @return the regularExpression
	 */
	public String getRegularExpression() {
		return this.regularExpression;
	}

	/**
	 * @param regularExpression
	 *            the regularExpression to set
	 */
	public void setRegularExpression(String regularExpression) {
		this.regularExpression = regularExpression;
	}
}
