package main;
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

public class ViewerClass {

	public static void main(String[] args) {
		
		Application<Task, Dependency> appl = new Application<Task, Dependency>();
		Task t0 = new Task("t0");
		Task t1 = new Task("t1");
		Communication c = new Communication("c");
		appl.addEdge(new Dependency("d0"), t0, c, EdgeType.DIRECTED);
		appl.addEdge(new Dependency("d1"), c, t1, EdgeType.DIRECTED);
		
		Architecture<Resource, Link> arch = new Architecture<Resource, Link>();
		Resource r0 = new Resource("r0");
		Resource r1 = new Resource("r1");
		Link l = new Link("l");
		arch.addEdge(l, r0, r1, EdgeType.UNDIRECTED);
		
		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		mappings.add(new Mapping<Task, Resource>("m0", t0, r0));
		mappings.add(new Mapping<Task, Resource>("m1", t1, r1));
		
		Specification spec = new Specification(appl, arch, mappings);
		SpecificationViewer.view(spec);
	}
}
