package net.sf.opendse.encoding.interpreter.res;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.visualization.SpecificationViewer;

public class SpecificationPostProcessorExpressTestRes {

	public static void main(String[] args) {
		SpecificationViewer.view(getSpec());
	}

	public static Specification getImpl() {
		Specification spec = getSpec();
		Architecture<Resource, Link> routing = spec.getRoutings().get(spec.getApplication().getVertex("c"));
		routing.removeEdge(spec.getArchitecture().getEdge("l5"));
		routing.removeEdge(spec.getArchitecture().getEdge("l2"));
		return spec;
	}
	
	public static Specification getSpec() {
		// appl
		Application<Task, Dependency> appl = new Application<Task, Dependency>();
		Task t0 = new Task("t0");
		Task t1 = new Task("t1");
		Communication c = new Communication("c");
		Dependency d0 = new Dependency("d0");
		Dependency d1 = new Dependency("d1");
		appl.addEdge(d0, t0, c, EdgeType.DIRECTED);
		appl.addEdge(d1, c, t1, EdgeType.DIRECTED);

		// arch
		Architecture<Resource, Link> arch = new Architecture<Resource, Link>();
		Resource r0 = new Resource("r0");
		Resource r1 = new Resource("r1");
		Resource r3 = new Resource("r3");
		Resource r5 = new Resource("r5");
		Resource r6 = new Resource("r6");

		Link l0 = new Link("l0");
		Link l1 = new Link("l1");
		Link l2 = new Link("l2");
		Link l5 = new Link("l5");
		Link l6 = new Link("l6");
		Link l7 = new Link("l7");

		arch.addEdge(l0, r0, r1, EdgeType.UNDIRECTED);
		arch.addEdge(l1, r0, r1, EdgeType.UNDIRECTED);
		arch.addEdge(l2, r1, r3, EdgeType.UNDIRECTED);
		arch.addEdge(l5, r3, r5, EdgeType.UNDIRECTED);
		arch.addEdge(l6, r5, r6, EdgeType.UNDIRECTED);
		arch.addEdge(l7, r5, r6, EdgeType.UNDIRECTED);

		// mappings
		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		Mapping<Task, Resource> m0 = new Mapping<Task, Resource>("m0", t0, r0);
		Mapping<Task, Resource> m1 = new Mapping<Task, Resource>("m1", t1, r6);
		mappings.add(m0);
		mappings.add(m1);
		
		return new Specification(appl, arch, mappings);
	}
}
