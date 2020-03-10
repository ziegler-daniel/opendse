package net.sf.opendse.model.generator;

import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

/**
 * Interface for the classes used to generate the mappings between the
 * {@link Application} and the {@link Architecture} created by the corresponding
 * generator objects.
 * 
 * @author Fedor Smirnov
 */
public interface MappingGenerator {

	/**
	 * Creates the {@link Mappings} between the provided {@link Application} and the
	 * provided {@link Architecture}.
	 * 
	 * @param appl the provided {@link Application}
	 * @param arch the provided {@link Architecture}
	 * @return the {@link Mappings} between the provided {@link Application} and the
	 *         provided {@link Architecture}
	 */
	public Mappings<Task, Resource> generateMappings(Application<Task, Dependency> appl,
			Architecture<Resource, Link> arch);

}
