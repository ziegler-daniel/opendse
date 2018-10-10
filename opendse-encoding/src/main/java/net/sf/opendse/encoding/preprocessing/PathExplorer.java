package net.sf.opendse.encoding.preprocessing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Specification;
import net.sf.opendse.optimization.SpecificationWrapper;

/**
 * {@code AllPathDirectFinder} uses the knowledge of existing graph to find all
 * possible paths between given two nodes
 * 
 * @author Zhao Han
 *
 */
public class PathExplorer implements IPathPreprocessing {

	public final static boolean DEBUG = false;

	private Architecture<Resource, Link> architecture = new Architecture<Resource, Link>();

	@Inject
	public PathExplorer(SpecificationWrapper wrapper) throws IllegalArgumentException {
		Specification S = wrapper.getSpecification();
		if (S == null)
			throw new IllegalArgumentException("The given specification is invalid.");
		architecture = S.getArchitecture();
	}

	@Override
	public Set<Architecture<Resource, Link>> getRoutingPossibilities(Resource source, Resource destination) {
		Set<Architecture<Resource, Link>> routings = new HashSet<Architecture<Resource, Link>>();
		routings.addAll(findAllPathsUtil(source, destination));
		return PathFinders.copyClone(routings);
	}

	@Override
	public Set<Architecture<Resource, Link>> getRoutingPossibilities(Resource source, Resource destinationA,
			Resource destinationB) {
		Set<Architecture<Resource, Link>> routings = new HashSet<Architecture<Resource, Link>>();
		if (destinationA.equals(destinationB))
			return getRoutingPossibilities(source, destinationA);
		else {
			Architecture<Resource, Link> routing = new Architecture<Resource, Link>();
			routing.addVertex(source);
			DFStraversalMulticast(source, destinationA, destinationB, routing, routings);
		}
		return PathFinders.copyClone(routings);
	}

	@Override
	public Set<Architecture<Resource, Link>> getRoutingPossibilities(Resource source, Set<Resource> destinations) {
		Set<Architecture<Resource, Link>> routings = new HashSet<Architecture<Resource, Link>>();
		Architecture<Resource, Link> routing = new Architecture<Resource, Link>();
		routing.addVertex(source);
		DFStraversalMulticast(source, destinations, routing, routings);
		return PathFinders.copyClone(routings);
	}

	/**
	 * This function intializes the visited list for {@link Depth-First Search},
	 * and explores all simple paths between {@code src} and {@code dest}
	 * 
	 * @param src
	 * @param dest
	 * @return
	 */
	private Set<Architecture<Resource, Link>> findAllPathsUtil(Resource src, Resource dest) {
		Set<Architecture<Resource, Link>> routings = new HashSet<Architecture<Resource, Link>>();
		Architecture<Resource, Link> routing = new Architecture<Resource, Link>();
		routing.addVertex(src);
		DFStraversalUnicast(src, dest, routing, routings);
		return routings;
	}

	/**
	 * Explore the graph directly for redundant routings between {@code src} and
	 * {@code dest}.
	 * 
	 * @param src
	 * @param dest
	 * @param routing
	 * @param routings
	 */
	private void DFStraversalUnicast(Resource src, final Resource dest, Architecture<Resource, Link> routing,
			Set<Architecture<Resource, Link>> routings) {

		Set<Resource> neighbors = new HashSet<Resource>(architecture.getNeighbors(src));
		// remove the vertices that has already been connected to source vertex
		// from the list
		for (Iterator<Resource> iterator = neighbors.iterator(); iterator.hasNext();) {
			Resource resource = iterator.next();
			Link normalLink = routing.findEdge(src, resource);
			Link reverseLink = routing.findEdge(resource, src);
			if (normalLink != null || reverseLink != null)
				iterator.remove();
		}

		// power-set-based exploration of all forwarding possibilities
		for (Set<Resource> nextLevel : Sets.powerSet(neighbors)) {
			// remove the empty set
			if (nextLevel.size() == 0)
				continue;

			Set<Link> addedLinks = buildNextLevel(nextLevel, src, routing);

			ArrayList<Resource> leaves = getAllLeaves(routing);
			// Remove invalid or loops contained routings
			if (leaves.size() == 0 || PathFinders.hasLoop(routing)) {
			}
			// Add valid routings to the list
			else if (leaves.size() == 1 && leaves.contains(dest)) {
				if (!PathFinders.contains(routings, routing)) {
					Architecture<Resource, Link> routingClone = PathFinders.simpleClone(routing);
					if (DEBUG)
						System.out.print("Adding a new unicast routing... \r");
					routings.add(routingClone);
				}
			} else {
				if (DEBUG)
					System.out.print("Processing... \r");
				leaves.removeIf(new Predicate<Resource>() {
					@Override
					public boolean test(Resource r) {
						return r.equals(dest);
					}
				});
				for (Resource resource : leaves)
					DFStraversalUnicast(resource, dest, routing, routings);
			}

			// Reset the routing to the status before buildNextLevel()
			for (Link link : addedLinks) {
				boolean isRemoved = routing.removeEdge(link);
				assert isRemoved : "Link " + link.getId() + " cannot be reomved from " + routing.toString();
			}
			ArrayList<Resource> toRemove = new ArrayList<Resource>();
			for (Resource resource : routing) {
				if (routing.getIncidentEdges(resource).size() == 0) // Find the
																	// isolated
																	// vertices
					toRemove.add(resource);
			}
			for (Resource resource : toRemove) {
				boolean isRemoved = routing.removeVertex(resource);
				assert isRemoved : "Resource " + resource.getId() + " cannot be reomved from " + routing.toString();
			}
		}
	}

	private void DFStraversalMulticast(Resource src, Resource destA, Resource destB,
			Architecture<Resource, Link> routing, Set<Architecture<Resource, Link>> routings) {

		Set<Resource> neighbors = new HashSet<Resource>(architecture.getNeighbors(src));
		// remove the vertices that has already been connected to source vertex
		// from the list
		for (Iterator<Resource> iterator = neighbors.iterator(); iterator.hasNext();) {
			Resource resource = iterator.next();
			Link normalLink = routing.findEdge(src, resource);
			Link reverseLink = routing.findEdge(resource, src);
			if (normalLink != null || reverseLink != null)
				iterator.remove();
		}

		// power-set-based exploration of all forwarding possibilities
		for (Set<Resource> nextLevel : Sets.powerSet(neighbors)) {
			// remove the empty set
			if (nextLevel.size() == 0)
				continue;

			Set<Link> addedLinks = buildNextLevel(nextLevel, src, routing);

			ArrayList<Resource> leaves = getAllLeaves(routing);
			// Remove invalid or loops contained routings
			if (leaves.size() == 0 || PathFinders.hasLoop(routing)) {
			}
			// Add valid routings to the list
			else if (leaves.size() == 2 && leaves.contains(destA) && leaves.contains(destB)) {
				if (!PathFinders.contains(routings, routing)) {
					Architecture<Resource, Link> routingClone = PathFinders.simpleClone(routing);
					if (DEBUG)
						System.out.print("Adding a new multicast routing... \r");
					routings.add(routingClone);
				}
			} else if (leaves.size() == 1 && ((leaves.contains(destA) && routing.containsVertex(destB))
					|| (leaves.contains(destB) && routing.containsVertex(destA)))) {
				if (!PathFinders.contains(routings, routing)) {
					Architecture<Resource, Link> routingClone = PathFinders.simpleClone(routing);
					if (DEBUG)
						System.out.print("Adding a new multicast routing... \r");
					routings.add(routingClone);
				}
			} else {
				if (DEBUG)
					System.out.print("Processing... \r");
				for (Resource resource : leaves)
					DFStraversalMulticast(resource, destA, destB, routing, routings);
			}

			// Reset the routing to the status before buildNextLevel()
			for (Link link : addedLinks) {
				boolean isRemoved = routing.removeEdge(link);
				assert isRemoved : "Link " + link.getId() + " cannot be reomved from " + routing.toString();
			}
			ArrayList<Resource> toRemove = new ArrayList<Resource>();
			for (Resource resource : routing) {
				if (routing.getIncidentEdges(resource).size() == 0) // Find the
																	// isolated
																	// vertices
					toRemove.add(resource);
			}
			for (Resource resource : toRemove) {
				boolean isRemoved = routing.removeVertex(resource);
				assert isRemoved : "Resource " + resource.getId() + " cannot be reomved from " + routing.toString();
			}
		}
	}

	private void DFStraversalMulticast(Resource src, final Set<Resource> dests, Architecture<Resource, Link> routing,
			Set<Architecture<Resource, Link>> routings) {

		Set<Resource> neighbors = new HashSet<Resource>(architecture.getNeighbors(src));
		// remove the vertices that has already been connected to source vertex
		// from the list
		for (Iterator<Resource> iterator = neighbors.iterator(); iterator.hasNext();) {
			Resource resource = iterator.next();
			Link normalLink = routing.findEdge(src, resource);
			Link reverseLink = routing.findEdge(resource, src);
			if (normalLink != null || reverseLink != null)
				iterator.remove();
		}

		// power-set-based exploration of all forwarding possibilities
		for (Set<Resource> nextLevel : Sets.powerSet(neighbors)) {
			// remove the empty set
			if (nextLevel.size() == 0)
				continue;

			Set<Link> addedLinks = buildNextLevel(nextLevel, src, routing);

			ArrayList<Resource> leaves = getAllLeaves(routing);
			// Remove invalid or loops contained routings
			if (leaves.size() == 0 || PathFinders.hasLoop(routing)) {
			}
			// Add valid routings to the list
			else if (leaves.size() == dests.size() && leaves.containsAll(dests)) {
				if (!PathFinders.contains(routings, routing)) {
					Architecture<Resource, Link> routingClone = PathFinders.simpleClone(routing);
					if (DEBUG)
						System.out.print("Adding a new multicast routing... \r");
					routings.add(routingClone);
				}
			} else if (leaves.size() < dests.size() && dests.containsAll(leaves)
					&& routing.getVertices().containsAll(dests)) {
				if (!PathFinders.contains(routings, routing)) {
					Architecture<Resource, Link> routingClone = PathFinders.simpleClone(routing);
					if (DEBUG)
						System.out.print("Adding a new multicast routing... \r");
					routings.add(routingClone);
				}
			} else {
				if (DEBUG)
					System.out.print("Processing... \r");
				for (Resource resource : leaves)
					DFStraversalMulticast(resource, dests, routing, routings);
			}

			// Reset the routing to the status before buildNextLevel()
			for (Link link : addedLinks) {
				boolean isRemoved = routing.removeEdge(link);
				assert isRemoved : "Link " + link.getId() + " cannot be reomved from " + routing.toString();
			}
			ArrayList<Resource> toRemove = new ArrayList<Resource>();
			for (Resource resource : routing) {
				if (routing.getIncidentEdges(resource).size() == 0) // Find the
																	// isolated
																	// vertices
					toRemove.add(resource);
			}
			for (Resource resource : toRemove) {
				boolean isRemoved = routing.removeVertex(resource);
				assert isRemoved : "Resource " + resource.getId() + " cannot be reomved from " + routing.toString();
			}
		}
	}

	/**
	 * All vertices from {@code nextLevel} will be connected to {@code src} with
	 * directed edges.
	 * 
	 * @param nextLevel
	 * @param src
	 * @param routing
	 * @return added directed edges
	 */
	private Set<Link> buildNextLevel(Set<Resource> nextLevel, Resource src, Architecture<Resource, Link> routing) {
		Set<Link> added = new HashSet<Link>();
		for (Resource resource : nextLevel) {
			if (!routing.containsVertex(resource))
				routing.addVertex(resource);
			Link link = architecture.findEdge(src, resource);
			if (routing.addEdge(link, src, resource, EdgeType.DIRECTED))
				added.add(link);
		}
		return added;
	}

	/**
	 * Find all the leaves, which are the vertices without out-edges.
	 * 
	 * @param architecture
	 * @return vertices that have no sucessors
	 */
	private ArrayList<Resource> getAllLeaves(Architecture<Resource, Link> architecture) {
		ArrayList<Resource> leaves = new ArrayList<Resource>();
		for (Resource resource : architecture) {
			if (architecture.getOutEdges(resource).size() == 0)
				leaves.add(resource);
		}
		return leaves;
	}
}
