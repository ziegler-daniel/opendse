package net.sf.opendse.encoding.variables;

import java.util.Set;

import net.sf.opendse.model.Task;
import net.sf.opendse.model.Models.DirectedLink;

public class RoutingGraphVariable extends Variable implements RoutingVariable {
	public RoutingGraphVariable(Task communication, Set<DirectedLink> dirLinks) {
		super(communication, dirLinks);
	}
	
	public Task getCommunication() {
		return get(0);
	}
	
	public Set<DirectedLink> getRouting(){
		return get(1);
	}
}
