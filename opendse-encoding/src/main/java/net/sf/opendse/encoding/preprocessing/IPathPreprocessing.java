package net.sf.opendse.encoding.preprocessing;

import java.util.Set;

import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;

/**
 * @author Fedor Smirnov
 * 
 *         Interface for the classes that deliver all possible routings between
 *         two resources.
 */
public interface IPathPreprocessing {

	public enum PreprocessingType{
		PathMerger,
		PathExplorer
	}
	
	/**
	 * Return all possible routing graphs that start at the source and go to the
	 * destination resource.
	 * 
	 * @param source
	 *            : The source resource
	 * @param destination
	 *            : The destination resource
	 * @return : A set of the routings (represented as {@link Architecture}
	 *         graphs) that contain all possibilities (redundant and
	 *         non-redundant) to route a message from source to destination.
	 */
	public Set<Architecture<Resource, Link>> getRoutingPossibilities(Resource source, Resource destination);
	
	
	/**
	 * Return all possible routing graphs that start at the source and go to the
	 * destination resource.
	 * 
	 * @param source
	 *            : The source resource
	 * @param destinationA
	 *            : The destination resource A
	 * @param destinationB
	 *            : The destination resource B
	 * @return : A set of the routings (represented as {@link Architecture}
	 *         graphs) that contain all possibilities (redundant and
	 *         non-redundant) to route a message from source to destinations.
	 */
	public Set<Architecture<Resource, Link>> getRoutingPossibilities(Resource source, Resource destinationA, Resource destinationB);
	
	/**
	 * Return all possible routing graphs that start at the source and go to the
	 * destination resource.
	 * 
	 * @param source
	 *            : The source resource
	 * @param destinations
	 *            : The destination resources
	 * @return : A set of the routings (represented as {@link Architecture}
	 *         graphs) that contain all possibilities (redundant and
	 *         non-redundant) to route a message from source to destinations.
	 */
	public Set<Architecture<Resource, Link>> getRoutingPossibilities(Resource source, Set<Resource> destinations);
}
