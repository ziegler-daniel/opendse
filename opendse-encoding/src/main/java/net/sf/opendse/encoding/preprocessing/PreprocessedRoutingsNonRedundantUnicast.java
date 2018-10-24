package net.sf.opendse.encoding.preprocessing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.inject.Inject;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Models.DirectedLink;
import net.sf.opendse.optimization.SpecificationWrapper;

/**
 * The {@link PreprocessedRoutingsNonRedundantUnicast} is to be used for unicast
 * messages only and provides all non-redundant routes between a source and a
 * destination node.
 * 
 * @author Fedor Smirnov
 *
 */
public class PreprocessedRoutingsNonRedundantUnicast implements PreprocessedRoutings {

	protected class UniCastRouteDescription {

		protected final Resource src;
		protected final Resource dest;

		protected UniCastRouteDescription(Resource src, Resource dest) {
			this.src = src;
			this.dest = dest;
		}

	}

	protected final Architecture<Resource, Link> arch;
	protected final Map<UniCastRouteDescription, Set<Architecture<Resource, Link>>> routingCache;

	@Inject
	public PreprocessedRoutingsNonRedundantUnicast(SpecificationWrapper wrapper) {
		this.arch = wrapper.getSpecification().getArchitecture();
		this.routingCache = new HashMap<PreprocessedRoutingsNonRedundantUnicast.UniCastRouteDescription, Set<Architecture<Resource, Link>>>();
	}

	@Override
	public Set<Architecture<Resource, Link>> getAllRoutings(Resource source, Set<Resource> destinations) {
		if (destinations.size() != 1) {
			throw new IllegalArgumentException("Intended for the use with unicast messages only!!!");
		}
		UniCastRouteDescription desc = new UniCastRouteDescription(source, destinations.iterator().next());
		if (!routingCache.containsKey(desc)) {
			routingCache.put(desc, findAllRoutes(desc.src, desc.dest));
		}
		return routingCache.get(desc);
	}

	/**
	 * Returns all non-redundant unicast routes connecting the src to the dest.
	 * 
	 * @param src
	 *            the src resource
	 * @param dest
	 *            the dest resource
	 * @return all non-redundant unicast routes connecting the src to the dest
	 */
	protected Set<Architecture<Resource, Link>> findAllRoutes(Resource src, Resource dest) {
		Set<Architecture<Resource, Link>> foundRoutings = new HashSet<Architecture<Resource, Link>>();
		Architecture<Resource, Link> currentRouting = new Architecture<Resource, Link>();
		currentRouting.addVertex(src);
		recDFS(src, currentRouting, foundRoutings, dest);
		return foundRoutings;
	}

	/**
	 * A recursive step of the dfs search to find all the routings.
	 * 
	 * @param curRes
	 * @param curRouting
	 * @param foundRoutings
	 * @param dest
	 */
	protected void recDFS(Resource curRes, Architecture<Resource, Link> curRouting,
			Set<Architecture<Resource, Link>> foundRoutings, Resource dest) {
		if (curRes.equals(dest)) {
			// CASE WHERE THE SEARCH YIELDS A CORRECT ROUTING
			foundRoutings.add(curRouting);
			return;
		}
		// get all the neighbors
		for (DirectedLink dLink : Models.getOutLinks(arch, curRes)) {
			if (!curRouting.containsVertex(dLink.getDest())) {
				// add the link to the routing and go to the next recursion step
				// CASE WHERE RECURSION CONTINUES
				Architecture<Resource, Link> nextRouting = copyArchitecture(curRouting);
				Resource nextRes = dLink.getDest();
				nextRouting.addEdge(dLink.getLink(), curRes, nextRes, EdgeType.DIRECTED);
				recDFS(nextRes, nextRouting, foundRoutings, dest);
			}
		}
	}

	protected Architecture<Resource, Link> copyArchitecture(Architecture<Resource, Link> original) {
		Architecture<Resource, Link> result = new Architecture<Resource, Link>();
		for (Link l : original.getEdges()) {
			Resource src = original.getSource(l);
			Resource dest = original.getDest(l);
			result.addEdge(l, src, dest, EdgeType.DIRECTED);
		}
		for (Resource res : original) {
			if (!result.containsVertex(res)) {
				result.addVertex(res);
			}
		}
		return result;
	}
}
