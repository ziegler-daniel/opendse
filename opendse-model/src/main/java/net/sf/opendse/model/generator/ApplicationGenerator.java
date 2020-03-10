package net.sf.opendse.model.generator;

import net.sf.opendse.model.Application;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Task;

/**
 * Interface for classes used to create an {@link Application}.
 * 
 * @author Fedor Smirnov
 */
public interface ApplicationGenerator {

	/**
	 * Generates an {@link Application}.
	 * 
	 * @return the {@link Application}
	 */
	public Application<Task, Dependency> generateApplication();
}
