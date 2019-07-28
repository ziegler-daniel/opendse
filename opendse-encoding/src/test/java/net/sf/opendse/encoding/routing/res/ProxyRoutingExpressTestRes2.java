package net.sf.opendse.encoding.routing.res;

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

public class ProxyRoutingExpressTestRes2 {

	private ProxyRoutingExpressTestRes2() {
	}

	public static void main(String[] args) {
		SpecificationViewer.view(makeSpec());
	}

	public static Specification makeSpec() {
		// application
		Application<Task, Dependency> appl = new Application<Task, Dependency>();
		Task t0 = new Task("t0");
		Task t1 = new Task("t1");
		Task t2 = new Task("t2");
		Task t3 = new Task("t3");
		Communication c0 = new Communication("c0");
		Communication c1 = new Communication("c1");

		Dependency d0 = new Dependency("d0");
		Dependency d1 = new Dependency("d1");
		Dependency d2 = new Dependency("d2");
		Dependency d3 = new Dependency("d3");

		appl.addEdge(d0, t0, c0, EdgeType.DIRECTED);
		appl.addEdge(d1, c0, t1, EdgeType.DIRECTED);
		appl.addEdge(d2, t2, c1, EdgeType.DIRECTED);
		appl.addEdge(d3, c1, t3, EdgeType.DIRECTED);

		// architecture
		Architecture<Resource, Link> arch = new Architecture<Resource, Link>();
		Resource res0 = new Resource("r0");
		Resource res1 = new Resource("r1");
		Resource res2 = new Resource("r2");
		Resource res3 = new Resource("r3");
		Resource res4 = new Resource("r4");
		Resource res5 = new Resource("r5");
		Resource res6 = new Resource("r6");
		Resource res7 = new Resource("r7");
		Resource res8 = new Resource("r8");

		Link l0 = new Link("l0");
		Link l1 = new Link("l1");
		Link l2 = new Link("l2");
		Link l3 = new Link("l3");
		Link l4 = new Link("l4");
		Link l5 = new Link("l5");
		Link l6 = new Link("l6");
		Link l7 = new Link("l7");
		Link l8 = new Link("l8");
		Link l9 = new Link("l9");
		Link l10 = new Link("l10");
		Link l11 = new Link("l11");

		arch.addEdge(l0, res0, res1, EdgeType.UNDIRECTED);
		arch.addEdge(l1, res0, res1, EdgeType.UNDIRECTED);
		arch.addEdge(l2, res1, res8, EdgeType.UNDIRECTED);
		arch.addEdge(l3, res2, res3, EdgeType.UNDIRECTED);
		arch.addEdge(l4, res2, res3, EdgeType.UNDIRECTED);
		arch.addEdge(l5, res3, res8, EdgeType.UNDIRECTED);
		arch.addEdge(l6, res5, res4, EdgeType.UNDIRECTED);
		arch.addEdge(l7, res5, res4, EdgeType.UNDIRECTED);
		arch.addEdge(l8, res5, res8, EdgeType.UNDIRECTED);
		arch.addEdge(l9, res7, res6, EdgeType.UNDIRECTED);
		arch.addEdge(l10, res7, res6, EdgeType.UNDIRECTED);
		arch.addEdge(l11, res7, res8, EdgeType.UNDIRECTED);

		// mappings
		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		Mapping<Task, Resource> m0 = new Mapping<Task, Resource>("m0", t0, res0);
		Mapping<Task, Resource> m1 = new Mapping<Task, Resource>("m1", t1, res2);
		Mapping<Task, Resource> m2 = new Mapping<Task, Resource>("m2", t2, res4);
		Mapping<Task, Resource> m3 = new Mapping<Task, Resource>("m3", t3, res6);
		mappings.add(m0);
		mappings.add(m1);
		mappings.add(m2);
		mappings.add(m3);

		return new Specification(appl, arch, mappings);
	}
}
