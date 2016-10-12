/**
 *
 */
package gb.ilias.mimecast.file.matcher.launcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Main class to launch this App.
 *
 * @author ilias
 * @since Oct 12, 2016
 */
public class MimecastLauncher {

	private static final Logger	LOG	= LoggerFactory.getLogger(MimecastLauncher.class);

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args == null || args.length != 3) {
			System.out.println("USAGE: The following parameters are mandatory ():");
			System.out.println("- input directory");
			System.out.println("- regular expression");
			System.out.println("- output directory");
			System.out.println("Leaving...");
			System.exit(0);
		}
		@SuppressWarnings("resource")
		final ApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "spring/batch/main-context.xml" });
		final FileMatcherProcessor processor = context.getBean(FileMatcherProcessor.class);
		if (processor != null) {
			processor.setInputFolder(args[0]);
			processor.setRegularExpression(args[1]);
			processor.setOutputFolder(args[2]);
			try {
				processor.launchJob();
			} catch (final Exception ex) {
				LOG.error(ex.getMessage(), ex);
			}
		}
	}
}
