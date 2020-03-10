package net.sf.opendse.model.generator;

import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;

/**
 * Interface for the creation of an {@link Architecture}.
 * 
 * @author Fedor Smirnov
 *
 */
public interface ArchitectureGenerator {

	/**
	 * Generates an {@link Architecture}.
	 * 
	 * @return the created {@link Architecture}.
	 */
	public Architecture<Resource, Link> generateArchitecture();
	
}
