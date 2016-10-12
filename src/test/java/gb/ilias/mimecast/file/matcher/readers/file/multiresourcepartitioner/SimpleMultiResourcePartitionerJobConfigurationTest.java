package gb.ilias.mimecast.file.matcher.readers.file.multiresourcepartitioner;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * JobConfigurationTest.
 *
 * @author ilias
 * @since Oct 12, 2016
 */
@ContextConfiguration({
		"classpath*:spring/batch/job/readers/file/file-multiresourcepartitioner-simple-job.xml",
		"classpath*:spring/batch/setup/**/*.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class SimpleMultiResourcePartitionerJobConfigurationTest {

	private static final int		READ_COUNT_PER_FILE	= 1;
	private static final int		READ_COUNT_OVERALL	= 2;
	private static final int		STEP_COUNT			= 3;

	@Autowired
	private JobLauncherTestUtils	jobLauncherTestUtils;

	/** Launch Test. */
	@Test
	public void launchJob() throws Exception {
		// Job parameters
		final Map<String, JobParameter> jobParametersMap = new HashMap<String, JobParameter>();
		jobParametersMap.put("time", new JobParameter(System.currentTimeMillis()));
		jobParametersMap.put("input.file.pattern", new JobParameter(
				"file:src/test/resources/input/file/multiresource/*.txt"));
		jobParametersMap.put("output.dir", new JobParameter(
				"file:target/test-outputs/readers/file/multiresourcepartitioner-simple/"));
		jobParametersMap.put("str.regexp", new JobParameter("(.*)Ilias(.*)"));

		// launch the job
		final JobExecution jobExecution = this.jobLauncherTestUtils.launchJob(new JobParameters(
				jobParametersMap));

		// assert step meta data
		for (final StepExecution step : jobExecution.getStepExecutions()) {
			// spring batch works with 3 "steps" here, the PartitionStep itself
			// and the created children
			if ("businessStep".equals(step.getStepName())) {
				assertTrue("Read Count, Isn't there something to read?",
						step.getReadCount() >= READ_COUNT_OVERALL);
				assertTrue("Write Count, Isn't there something to write?",
						step.getWriteCount() >= READ_COUNT_OVERALL);
			}
		}
	}
}
