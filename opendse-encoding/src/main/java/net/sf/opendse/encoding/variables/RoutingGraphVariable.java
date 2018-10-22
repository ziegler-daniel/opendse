package net.sf.opendse.encoding.variables;

import java.util.List;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.Models.DirectedLink;

public class RoutingGraphVariable extends Variable implements RoutingVariable {
	public RoutingGraphVariable(Task communication, Architecture<Resource, Link> routing) {
		super(communication, routing);
	}
	
	public Task getCommunication() {
		return get(0);
	}
	
	public Architecture<Resource, Link> getRouting(){
		return get(1);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof RoutingGraphVariable)){
			return false;
		}
		RoutingGraphVariable other = (RoutingGraphVariable) obj;
		List<DirectedLink> theseLinks = Models.getLinks(this.getRouting());
		List<DirectedLink> otherLinks = Models.getLinks(other.getRouting());
		return ((theseLinks.containsAll(otherLinks)) && otherLinks.containsAll(theseLinks));
	}
	
	@Override
	public int hashCode() {
		int result = 1;
		for (DirectedLink dLink : Models.getLinks(getRouting())){
			result *= dLink.hashCode();
		}
		return result;
	}
}
