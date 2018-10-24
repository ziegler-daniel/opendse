package net.sf.opendse.encoding.preprocessing;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Models.DirectedLink;
import net.sf.opendse.model.Resource;
import net.sf.opendse.optimization.SpecificationWrapper;

/**
 * The {@link PreprocessedRoutingsConstructive} is used to find all the routes
 * between a given source and a set of provided destinations.
 * 
 * @author Fedor Smirnov
 *
 */
@Singleton
public class PreprocessedRoutingsConstructive implements PreprocessedRoutings {

	protected class RoutingDescription {
		protected final Resource src;
		protected final Set<Resource> destinations;
		public RoutingDescription(Resource src, Set<Resource> destinations) {
			this.src = src;
			this.destinations = destinations;
		}
	}

	protected final Architecture<Resource, Link> specArch;
	protected Architecture<Resource, Link> preprocessedArch;
	protected final Map<RoutingDescription, Set<Architecture<Resource, Link>>> routingCache;

	@Inject
	public PreprocessedRoutingsConstructive(SpecificationWrapper wrapper) {
		this.specArch = wrapper.getSpecification().getArchitecture();
		this.routingCache = new HashMap<PreprocessedRoutingsConstructive.RoutingDescription, Set<Architecture<Resource, Link>>>();
	}

	@Override
	public Set<Architecture<Resource, Link>> getAllRoutings(Resource source, Set<Resource> destinations) {
		RoutingDescription desc = new RoutingDescription(source, destinations);
		preprocessArch(source, destinations);
		if (!routingCache.containsKey(desc)) {
			routingCache.put(desc, findRoutings(source, destinations));
		}
		return routingCache.get(desc);
	}
	
	/**
	 * Throws out all elements that can not be part of the routing.
	 * 
	 * @param src
	 * @param destinations
	 */
	protected void preprocessArch(Resource src, Set<Resource> destinations){
		// copy the spec arch
		preprocessedArch = new Architecture<Resource, Link>();
		for (Link l : specArch.getEdges()){
			Resource first = specArch.getEndpoints(l).getFirst();
			Resource second = specArch.getEndpoints(l).getSecond();
			preprocessedArch.addEdge(l, first, second, EdgeType.UNDIRECTED);
		}
		for (Resource res : specArch){
			if (!preprocessedArch.containsVertex(res)){
				preprocessedArch.addVertex(res);
			}
		}
		Set<Resource> endPoints = new HashSet<Resource>(destinations);
		endPoints.add(src);
		Set<Resource> toRemove = new HashSet<Resource>();
		do{
			preprocessedArch.removeVertices(toRemove);
			toRemove.clear();
			for (Resource res : preprocessedArch){
				if (Models.getInLinks(preprocessedArch, res).size() == 1 && !endPoints.contains(res)){
					toRemove.add(res);
				}
			}
		}while(!toRemove.isEmpty());
	}

	/**
	 * Returns all routings connecting the given source to each of the
	 * destinations.
	 * 
	 * @param src
	 *            the src
	 * @param destinations
	 *            the destinations
	 * @return all routings connecting the given source to each of the
	 *         destinations
	 */
	protected Set<Architecture<Resource, Link>> findRoutings(Resource src, Set<Resource> destinations) {
		Set<Architecture<Resource, Link>> foundRoutings = new HashSet<Architecture<Resource, Link>>();
		Architecture<Resource, Link> curRouting = new Architecture<Resource, Link>();
		curRouting.addVertex(src);
		Map<Resource, Integer> orderMap = new HashMap<Resource, Integer>();
		orderMap.put(src, 0);
		for (DirectedLink dLink : findConstructiveLinks(curRouting)) {
			addLinkRecursively(dLink, curRouting, destinations, foundRoutings, orderMap);
		}
		return foundRoutings;
	}

	/**
	 * Processes the current link. Checks whether the link is constructive.
	 * Creates an updated current routing and updates the order map. Triggers
	 * next link iteration.
	 * 
	 * @param dLink
	 *            the link that is currently added
	 * @param curRouting
	 *            the current state of the currently processed routing
	 * @param destinations
	 *            the set of the destination resources
	 * @param orderMap
	 *            map mapping the resources onto their order
	 * @param foundRoutings
	 */
	protected void addLinkRecursively(DirectedLink dLink, Architecture<Resource, Link> curRouting,
			Set<Resource> destinations, Set<Architecture<Resource, Link>> foundRoutings,
			Map<Resource, Integer> orderMap) {
		// check if adding the new link violates the order
		Resource candidate = dLink.getDest();
		Resource linkSrc = dLink.getSource();
		if (doesViolateOrder(linkSrc, candidate, orderMap)) {
			return;
		}
		// add the link
		Map<Resource, Integer> nextOrderMap = new HashMap<Resource, Integer>(orderMap);
		Architecture<Resource, Link> nextRouting = addLinkToRouting(dLink, curRouting, nextOrderMap);
		// check if ready
		if (routingFinished(nextRouting, destinations)) {
			if (!isRoutingKnown(nextRouting, foundRoutings)) {
				foundRoutings.add(nextRouting);
			}
		}
		// next recursion level
		for (DirectedLink nextLink : findConstructiveLinks(nextRouting)) {
			addLinkRecursively(nextLink, nextRouting, destinations, foundRoutings, nextOrderMap);
		}
	}

	protected boolean isRoutingKnown(Architecture<Resource, Link> routing,
			Set<Architecture<Resource, Link>> knownRoutings) {
		for (Architecture<Resource, Link> knownRouting : knownRoutings) {
			if (areRoutingsSame(knownRouting, routing)) {
				return true;
			}
		}
		return false;
	}

	protected boolean areRoutingsSame(Architecture<Resource, Link> routingA, Architecture<Resource, Link> routingB) {
		Collection<Resource> resA = routingA.getVertices();
		Collection<Resource> resB = routingB.getVertices();
		if (!(resA.containsAll(resB) && resB.containsAll(resA))) {
			return false;
		}
		List<DirectedLink> linksA = Models.getLinks(routingA);
		List<DirectedLink> linksB = Models.getLinks(routingB);
		if (!(linksA.containsAll(linksB) && linksB.containsAll(linksA))) {
			return false;
		}
		return true;
	}

	/**
	 * Checks whether the current routing has already reached all destinations.
	 * 
	 * @param routing
	 *            the current state of the routing
	 * @param destinations
	 *            the set of the destination resources
	 * @return {@code true} if the routing is complete, {@code false} otherwise
	 */
	protected boolean routingFinished(Architecture<Resource, Link> routing, Set<Resource> destinations) {
		boolean allDestsFound = true;
		for (Resource dest : destinations) {
			allDestsFound &= routing.containsVertex(dest);
		}
		boolean noUnconnectedResources = true;
		for (Resource res : routing) {
			if (!destinations.contains(res)) {
				if (Models.getOutLinks(routing, res).isEmpty()) {
					noUnconnectedResources = false;
				}
			}
		}
		return allDestsFound && noUnconnectedResources;
	}

	/**
	 * Returns the routing that is created by adding the given link to the
	 * current routing. Also updates the order map.
	 * 
	 * @param dLink
	 *            the directed link to add
	 * @param curRouting
	 *            the current routing (to be copied)
	 * @param orderMap
	 *            a copy of the order map (to be updated directly)
	 * @return the routing that is created by adding the given link to the
	 *         current routing
	 */
	protected Architecture<Resource, Link> addLinkToRouting(DirectedLink dLink, Architecture<Resource, Link> curRouting,
			Map<Resource, Integer> orderMap) {
		Architecture<Resource, Link> resultingRouting = new Architecture<Resource, Link>();
		// copy the current routing
		for (Link l : curRouting.getEdges()) {
			Resource src = curRouting.getSource(l);
			Resource dest = curRouting.getDest(l);
			resultingRouting.addEdge(l, src, dest, EdgeType.DIRECTED);
		}
		for (Resource res : curRouting.getVertices()) {
			if (!resultingRouting.containsVertex(res)) {
				resultingRouting.addVertex(res);
			}
		}
		// add the directed link
		Resource src = dLink.getSource();
		Resource dest = dLink.getDest();
		resultingRouting.addEdge(dLink.getLink(), src, dest, EdgeType.DIRECTED);
		// update the order map
		int newOrder = orderMap.containsKey(dest) ? Math.max(orderMap.get(dest), orderMap.get(src) + 1)
				: orderMap.get(src) + 1;
		orderMap.put(dest, newOrder);
		return resultingRouting;
	}

	/**
	 * Checks whether the given link violates the hitherto established resource
	 * order
	 * 
	 * @param linkSrc
	 *            the src of the link
	 * @param candidate
	 *            the candidate new resource (link dest)
	 * @return {@code true} in case of an order violation, {@code false}
	 *         otherwise
	 */
	protected boolean doesViolateOrder(Resource linkSrc, Resource candidate, Map<Resource, Integer> orderMap) {
		if (!orderMap.containsKey(candidate)) {
			// no order specified yet
			return false;
		}
		return orderMap.get(linkSrc) > orderMap.get(candidate);
	}

	/**
	 * Finds the set of all outgoing links in the current state of the routing
	 * that are not yet part of the routing.
	 * 
	 * @param curRouting
	 *            the currently built routing graph
	 * @return the set of all constructive links in the current state of the
	 *         routing
	 */
	protected Set<DirectedLink> findConstructiveLinks(Architecture<Resource, Link> curRouting) {
		Set<DirectedLink> result = new HashSet<DirectedLink>();
		for (Resource res : curRouting) {
			for (DirectedLink dLink : Models.getOutLinks(preprocessedArch, res)) {
				if (!curRouting.containsEdge(dLink.getLink())) {
					result.add(dLink);
				}
			}
		}
		return result;
	}
}
