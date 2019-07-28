package net.sf.opendse.encoding.preprocessing;

import java.util.Set;

import net.sf.opendse.model.Models.DirectedLink;
import net.sf.opendse.model.Resource;

/**
 * Interface that documents the requirements of an express routing preprocessor
 * 
 * @author Fedor Smirnov
 *
 */
public interface ExpressRoutings {

	/**
	 * Returns the set of directed express links used when routing between the two
	 * given non-express resources.
	 * 
	 * @param src
	 *            the express resource used as src
	 * @param dest
	 *            the express resource used as dest
	 * @return the set of directed express links used when routing between the two
	 *         given non-express resources
	 */
	public Set<DirectedLink> getLinksBetweenExpressResources(Resource src, Resource dest);

}
