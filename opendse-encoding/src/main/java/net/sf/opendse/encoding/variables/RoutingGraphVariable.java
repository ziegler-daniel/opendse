package net.sf.opendse.encoding.variables;

import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

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
}
