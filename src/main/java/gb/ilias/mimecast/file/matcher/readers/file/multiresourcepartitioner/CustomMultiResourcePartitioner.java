package gb.ilias.mimecast.file.matcher.readers.file.multiresourcepartitioner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * Slightly changed MultiResourcePartitioner, does create output file name too.
 *
 * @author ilias
 * @since Oct 12, 2016
 */
public class CustomMultiResourcePartitioner implements Partitioner {

	private final Logger		LOG				= LoggerFactory.getLogger(this.getClass());
	private static final String	PARTITION_KEY	= "partition";
	private Resource[]			resources		= new Resource[0];
	private final String		inputKeyName	= "inputFilePath";
	private final String		outputKeyName	= "outputFileName";

	/**
	 * Assign the filename of each of the injected resources to an
	 * {@link ExecutionContext}.
	 *
	 * @see Partitioner#partition(int)
	 */
	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		final Map<String, ExecutionContext> map = new HashMap<String, ExecutionContext>(gridSize);
		int i = 0;
		for (final Resource resource : this.resources) {
			final ExecutionContext context = new ExecutionContext();
			Assert.state(resource.exists(), "Resource does not exist: " + resource);
			try {
				context.putString(this.inputKeyName, resource.getURL().toExternalForm());
				context.put(this.outputKeyName, this.createOutputFilename(i, resource));
			} catch (final IOException e) {
				throw new IllegalArgumentException("File could not be located for: " + resource, e);
			}
			map.put(PARTITION_KEY + i, context);
			i++;
		}
		return map;
	}

	/**
	 * Creates distinct output file name per partition.
	 *
	 * @param partitionId
	 * @param context
	 * @param resource
	 * @return
	 */
	private String createOutputFilename(int partitionId, Resource resource) {
		final String outputFileName = resource.getFilename();
		this.LOG.info("for inputfile:'" + resource.getFilename() + "' outputfilename:'" + outputFileName
				+ "' was created");

		return outputFileName;
	}

	/**
	 * The resources to assign to each partition. In Spring configuration you
	 * can use a pattern to select multiple resources.
	 *
	 * @param resources
	 *            the resources to use
	 */
	public void setResources(Resource[] resources) {
		this.resources = resources;
	}
}
