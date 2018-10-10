package net.sf.opendse.encoding.preprocessing;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;

public final class PathFinders {

	private PathFinders() { }
	
	/**
	 * Check if a {@code routing} has been already contained in {@code routings}
	 * 
	 * @param routings
	 * @param routing
	 * @return
	 */
	public static boolean contains(Set<Architecture<Resource, Link>> routings, Architecture<Resource, Link> routing) {
		for (Architecture<Resource, Link> rout : routings) {
			if (equal(rout, routing))
				return true;
		}
		return false;
	}
	
	/**
	 * This functions checks if the {@code child} is a part of {@code parent}.
	 * 
	 * @param parent
	 * @param child
	 * @return If {@code child} is a part of {@code parent} then return {@code ture}, otherwise {@code false}. <br>
	 *				 If two architecure are same, this function will also return {@code true}.
	 */
	public static boolean contains(Architecture<Resource, Link> parent, Architecture<Resource, Link> child){
	
		for (Resource resourceChild : child.getVertices()) {
			boolean flag = false;
			for (Resource resourceParent :	parent.getVertices()) {
				if (resourceParent.equals(resourceChild))
					flag = true;
			}
			if (!flag)
				return false;
		}
		
		for (Link linkChild : child.getEdges()) {
			Resource source = child.getSource(linkChild);
			Resource destination = child.getDest(linkChild);
			
			Link linkParent = parent.findEdge(parent.getVertex(source.getId()), parent.getVertex(destination.getId()));
			if ( linkParent == null)
				return false;
		}
		
		return true;		
	}
	
	public static boolean equal(Architecture<Resource, Link> a, Architecture<Resource, Link> b){
		Collection<Resource> aVertices = a.getVertices();
		Collection<Resource> bVertices = b.getVertices();
		
		if (aVertices.size() != bVertices.size())
			return false;
		for (Resource resourceB : bVertices) {
			boolean flag = false;
			for (Resource resourceA :	aVertices) {
				if (resourceA.equals(resourceB))
					flag = true;
			}
			if (!flag)
				return false;
		}
		
		Collection<Link> aLinks = a.getEdges();
		Collection<Link> bLinks = b.getEdges();
		if (aLinks.size() != bLinks.size())
			return false;
		for (Link link : bLinks) {
			Resource source = b.getSource(link);
			Resource destination = b.getDest(link);
			
			Link aLink = a.findEdge(a.getVertex(source.getId()), a.getVertex(destination.getId()));
			if ( aLink == null)
				return false;
		}
		
		return true;		
	}
		
	/**
	 * This function checks if loops are existing in the given {@code routing} or not.
	 * 
	 * @param routing
	 * @return
	 */	
	public static boolean hasLoop(Architecture<Resource, Link> routing) {		
		assert routing != null: "Given routing is null.";
		HashSet<Resource> visited = new HashSet<Resource>();
		Resource sender = null;
		
		// Find the sender
		for (Resource resource : routing) {
			if (routing.getPredecessorCount(resource) == 0){
				assert sender == null : "There are two senders in routing " + routing.toString();
				sender = resource;					
			}
		}
		
		// Can not find the source vertex, the routing contains loop
		if (sender == null)
			return true;
		boolean hasLoop = findLoopUtil(sender, routing, visited);	
		return hasLoop;	
	} 
	
	/**
	 * This function walks through the {@code routing}, if a vertex is visited twice, 
	 * then there is a loop existing in the {@code routing}.
	 * 
	 * @param thisNode
	 * @param routing
	 * @param visited
	 * @return
	 */
	private static boolean findLoopUtil(Resource thisNode, Architecture<Resource, Link> routing, HashSet<Resource> visited) {
		visited.add(thisNode);
		
		for (Resource nextNode : routing.getSuccessors(thisNode)) {
			if (visited.contains(nextNode))
				return true;
			if (findLoopUtil(nextNode, routing, visited) ) 
				return true;
		}
		
		visited.remove(thisNode);
		return false;
	}
		
	/**
	 * Merge two routings together, that means all resources and links will be merged into one routing.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static Architecture<Resource, Link> pathMergingUtil(Set<Architecture<Resource, Link>> paths) {	
		Architecture<Resource, Link> merged = new Architecture<Resource, Link>();
		assert paths.size() > 0 : "The given paths contains zero elements.";
		
		for (Architecture<Resource,Link> simplePath : paths) {
			for (Resource resource : simplePath) {
				if ( !merged.containsVertex(resource) )
					merged.addVertex(resource);	
			}
			
			for (Link linkToMerge : simplePath.getEdges()) {
				Resource source = simplePath.getSource(linkToMerge);
				Resource destination = simplePath.getDest(linkToMerge);
				
				// Check if the reverse link is already added to the routing.
				// Without pre-check, the OpenDSE framework will throw IllegalArgumentException
				Link reverseLink = merged.findEdge(destination, source);
				Link normalLink = merged.findEdge(source, destination);
				if (normalLink != null)
					continue;
				if (reverseLink == null)
					merged.addEdge(linkToMerge, source, destination, EdgeType.DIRECTED);
				else {
					return null;
				}
			}
		}		
		
		return merged;
	}
	
	public static Architecture<Resource, Link> simpleClone(Architecture<Resource, Link> arch) {
		Architecture<Resource, Link> clone = new Architecture<Resource,Link>();
		for (Resource resource : arch) {
			clone.addVertex(resource);
		}
		for(Link link : arch.getEdges()){
			Resource source = arch.getSource(link);
			Resource destination = arch.getDest(link);
			clone.addEdge(link, source, destination, EdgeType.DIRECTED);
		}		
		return clone;		
	}	

	public static Architecture<Resource, Link> copyClone(Architecture<Resource, Link> arch) {
		Architecture<Resource, Link> clone = new Architecture<Resource, Link>();
		for (Resource resource : arch) {
			clone.addVertex(new Resource(resource));
		}
		for (Link link : arch.getEdges()) {
			Resource source = arch.getSource(link);
			Resource destination = arch.getDest(link);
			clone.addEdge(new Link(link), clone.getVertex(source.getId()), clone.getVertex(destination.getId()),
					EdgeType.DIRECTED);
		}
		return clone;
	}

	public static Set<Architecture<Resource, Link>> copyClone(Set<Architecture<Resource, Link>> architectures) {
		Set<Architecture<Resource, Link>> clone = new HashSet<Architecture<Resource, Link>>();
		for (Architecture<Resource, Link> architecture : architectures) {
			clone.add(copyClone(architecture));
		}
		return clone;
	}
}
