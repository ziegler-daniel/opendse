package net.sf.opendse.encoding.preprocessing;

import java.util.Set;

import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;

/**
 * The {@link PreprocessedRoutings} offers the possibility to get all valid
 * routings between a source and a destination.
 * 
 * @author Fedor Smirnov
 *
 */
public interface PreprocessedRoutings {

	/**
	 * Returns a set containing all valid routings between the given source and the
	 * given destination.
	 * 
	 * @param source
	 *            the source resource
	 * @param destinations
	 *            the destination resources
	 * @return set containing all valid routings between the given source and the
	 *         given destination
	 */
	public Set<Architecture<Resource, Link>> getAllRoutings(Resource source, Set<Resource> destinations);

}
