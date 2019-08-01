package net.sf.opendse.encoding.preprocessing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.opendse.model.Models.DirectedLink;
import net.sf.opendse.encoding.preprocessing.ProxyRoutingsShortestPath.Connection;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.properties.ResourcePropertyService;

/**
 * Preprocesses the architecture and finds the set of express links between each
 * pair of non-express resources.
 * 
 * @author Fedor Smirnov
 *
 */
public class ExpressRoutingsShortestPath implements ExpressRoutings {

	protected final Map<Connection, Set<DirectedLink>> connections;

	public ExpressRoutingsShortestPath(Architecture<Resource, Link> arch) {
		this.connections = generateConnectionMap(arch);
	}

	/**
	 * Generates the connection map based on the given architecture.
	 * 
	 * @param arch
	 *            the given architecture
	 * @return the connection map based on the given architecture
	 */
	protected Map<Connection, Set<DirectedLink>> generateConnectionMap(Architecture<Resource, Link> arch) {
		Map<Connection, Set<DirectedLink>> result = new HashMap<ProxyRoutingsShortestPath.Connection, Set<DirectedLink>>();
		// find all non-express resources
		Set<Resource> nonExpress = new HashSet<Resource>();
		for (Resource res : arch) {
			if (!ResourcePropertyService.isExpress(res)) {
				nonExpress.add(res);
			}
		}
		// iterate each pair
		for (Resource src : nonExpress) {
			for (Resource dest : nonExpress) {
				Connection connection = new Connection(src, dest);
				if (src.equals(dest)) {
					// same src and dest => empty set
					result.put(connection, new HashSet<DirectedLink>());
					continue;
				}
				// find the shortest path in the routing
				Architecture<Resource, Link> shortestPath = RoutingSearch.findShortestPath(src, dest, arch);
				// collect the express links on the path
				result.put(connection, findExpressLinksInShortestPath(shortestPath, src));
			}
		}
		return result;
	}

	/**
	 * Returns the set of directed express links between the src and the dest in the
	 * given shortest path
	 * 
	 * @param shortestPath
	 *            the arch with the shortest path
	 * @param src
	 *            the src
	 * @return the set of directed express links between the src and the dest in the
	 *         given shortest path
	 */
	protected Set<DirectedLink> findExpressLinksInShortestPath(Architecture<Resource, Link> shortestPath,
			Resource src) {
		Set<DirectedLink> result = new HashSet<DirectedLink>();
		Resource cur = src;
		while (true) {
			Set<Link> outLinks = new HashSet<Link>(shortestPath.getOutEdges(cur));
			if (outLinks.size() > 1) {
				throw new IllegalArgumentException("The shortest path must not have more than one out edge.");
			}
			if (outLinks.isEmpty())
				break;
			Link outLink = outLinks.iterator().next();
			Resource next = shortestPath.getOpposite(cur, outLink);
			DirectedLink dirLink = new DirectedLink(outLink, cur, next);
			if (ResourcePropertyService.isExpress(cur) && ResourcePropertyService.isExpress(next)) {
				result.add(dirLink);
			}
			cur = next;
		}
		return result;
	}

	@Override
	public Set<DirectedLink> getLinksBetweenExpressResources(Resource src, Resource dest) {
		return connections.get(new Connection(src, dest));
	}
}
