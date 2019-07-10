package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.properties.ArchitectureElementPropertyService;
import net.sf.opendse.model.properties.ResourcePropertyService;

/**
 * The express search is used to find the express nodes in a given architecture,
 * mark them as such, and annotate that links within express areas do not offer
 * routing variety.
 * 
 * @author Fedor Smirnov
 *
 */
public class ExpressSearch {

	/**
	 * Searches for the express resources in the architecture, marks them, and
	 * annotates that the links within the express area do not offer routing
	 * variety.
	 * 
	 * @param specArch
	 *            the specification architecture
	 */
	public void searchForExpressAreas(Architecture<Resource, Link> specArch) {
		Set<Link> expressLinks = searchForExpressLinks(specArch);
		// annotate the express nodes
		annotateExpressNodes(specArch, expressLinks);
		// annotate the express links
		for (Link l : expressLinks) {
			ArchitectureElementPropertyService.setOfferRoutingVariety(l, false);
		}
	}
	
	/**
	 * Annotates the nodes that form the entrances to the express areas.
	 * 
	 * @param arch the architecture graph
	 * @param expressLinks the express links
	 */
	protected void annotateExpressNodes(Architecture<Resource, Link> arch, Set<Link> expressLinks) {
		Set<Resource> expressNodes = new HashSet<Resource>();
		for (Resource res : arch) {
			boolean expressLink = false;
			boolean nonExpressLink = false;
			for (Link l : arch.getIncidentEdges(res)) {
				if (expressLinks.contains(l)) {
					expressLink = true;
				}else {
					nonExpressLink = true;
				}
			}
			if (expressLink && nonExpressLink) {
				expressNodes.add(res);
			}
		}
		for (Resource res : expressNodes) {
			ResourcePropertyService.makeExpress(res);
		}
	}

	/**
	 * Searches for express links in the given architecture.
	 * 
	 * @param arch
	 *            the given architecture
	 * @return express links in the given architecture
	 */
	protected Set<Link> searchForExpressLinks(Architecture<Resource, Link> arch) {
		Set<Link> result = new HashSet<Link>();
		// remember what is already marked as proxy link
		Set<Link> proxyLinks = new HashSet<Link>();
		for (Link l : arch.getEdges()) {
			if (!ArchitectureElementPropertyService.getOffersRoutingVariety(l)) {
				proxyLinks.add(l);
			}
		}
		// check all non-proxy links
		for (Link l : arch.getEdges()) {
			if (proxyLinks.contains(l)) {
				continue;
			}
			if (checkIfExpress(l, arch, proxyLinks)) {
				result.add(l);
			}
		}
		return result;
	}

	/**
	 * Returns {@code true} iff the current link is an express link, i.e., if
	 * cutting the link separates the graph into two unconnected graphs.
	 * 
	 * @param l
	 *            the given link
	 * @param arch
	 *            the architecture
	 * @param proxy
	 *            the proxy links
	 * @return {@code true} iff the current link is an express link, i.e., if
	 *         cutting the link separates the graph into two unconnected graphs
	 */
	protected boolean checkIfExpress(Link l, Architecture<Resource, Link> arch, Set<Link> proxy) {
		Resource src = arch.getEndpoints(l).getFirst();
		Resource dest = arch.getEndpoints(l).getSecond();
		Set<Link> visited = new HashSet<Link>(proxy);
		visited.add(l);
		Set<Resource> reachable = getReachableResources(src, arch, visited);
		return !reachable.contains(dest);
	}

	/**
	 * Gets the resources that can be reached starting from the given node, without
	 * using the given links.
	 * 
	 * @param src
	 *            the given node
	 * @param arch
	 *            the resource graph
	 * @param visited
	 *            a list of links that is considered as visited
	 * @return the resources that can be reached starting from the given node,
	 *         without using the given links
	 */
	protected Set<Resource> getReachableResources(Resource src, Architecture<Resource, Link> arch, Set<Link> visited) {
		Set<Resource> reachable = new HashSet<Resource>();
		reachable.add(src);
		Set<Resource> foundNow = new HashSet<Resource>();
		do {
			foundNow.clear();
			for (Resource res : reachable) {
				// check all the edges
				for (Link l : arch.getIncidentEdges(res)) {
					if (!visited.contains(l)) {
						Resource other = arch.getOpposite(res, l);
						foundNow.add(other);
						visited.add(l);
					}
				}
			}
			reachable.addAll(foundNow);
		} while (!foundNow.isEmpty());
		return reachable;
	}
}
