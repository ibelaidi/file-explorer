package gb.ilias.mimecast.file.matcher.writers;

import java.util.Arrays;
import java.util.List;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.util.Assert;

/**
 * File Match Writer. This class handles matching the given regular expression
 * against entries read for input files.
 *
 * @author ilias
 * @since 2016
 */
public class FileMatchWriter extends FlatFileItemWriter<String> {

	/**
	 * Regular Expression
	 */
	private String	regularExpression;
	private Long	numberEntries	= 0L;

	@Override
	public void write(List<? extends String> objs) throws Exception {
		this.numberEntries += objs.stream().filter(s -> s.matches(this.regularExpression)).count();
	}

	/**
	 * Sets the regular expression to be matched against entries.
	 *
	 * @param regExp
	 *            Regular expression
	 */
	public void setRegularExpression(String regExp) {
		this.regularExpression = regExp;
	}

	@AfterStep
	public ExitStatus afterStep(StepExecution stepExecution) {
		try {
			super.write(Arrays.asList(this.numberEntries.toString()));
		} catch (final Exception e) {
			e.printStackTrace();
			return ExitStatus.FAILED;
		}
		return ExitStatus.COMPLETED;
	}

	@BeforeStep
	public ExitStatus beforeStep(StepExecution stepExecution) {
		Assert.state(this.regularExpression != null, "Regular Expression must not be NULL!");
		return ExitStatus.COMPLETED;
	}

}
