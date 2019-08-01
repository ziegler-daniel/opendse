package net.sf.opendse.encoding.routing.res;

import java.util.HashSet;
import java.util.Set;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.Variables;
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
import net.sf.opendse.model.properties.ArchitectureElementPropertyService;
import net.sf.opendse.model.properties.ResourcePropertyService;
import net.sf.opendse.visualization.SpecificationViewer;

public class ProxyRoutingExpressTestRes {

	private ProxyRoutingExpressTestRes() {
	}
	
	public static void main(String[] args) {
		SpecificationViewer.view(makeSpec());
	}
	
	public static Specification makeSpec() {
		// application
		Application<Task, Dependency> appl = new Application<Task, Dependency>();
		Task t0 = new Task("t0");
		Task t1 = new Task("t1");
		Communication comm = new Communication("comm");
		Dependency d0 = new Dependency("d0");
		Dependency d1 = new Dependency("d1");
		appl.addEdge(d0, t0, comm, EdgeType.DIRECTED);
		appl.addEdge(d1, comm, t1, EdgeType.DIRECTED);
		
		// architecture
		Architecture<Resource, Link> arch = new Architecture<Resource, Link>();
		
		Resource r0 = new Resource("r0");
		Resource r1 = new Resource("r1");
		Resource r2 = new Resource("r2");
		Resource r3 = new Resource("r3");
		Resource r4 = new Resource("r4");
		Resource r5 = new Resource("r5");
		Resource r6 = new Resource("r6");
		Resource r7 = new Resource("r7");
		Resource r8 = new Resource("r8");
		Resource r9 = new Resource("r9");
		Resource r10 = new Resource("r10");
		Resource r11 = new Resource("r11");
		Resource r12 = new Resource("r12");
		
		Resource r13 = new Resource("r13");
		Resource r14 = new Resource("r14");
		Resource r15 = new Resource("r15");
		Resource r16 = new Resource("r16");
		Resource r17 = new Resource("r17");
		Resource r18 = new Resource("r18");
		
		ResourcePropertyService.setProxyId(r0, r2);
		ResourcePropertyService.setProxyId(r1, r2);
		ResourcePropertyService.setProxyId(r11, r10);
		ResourcePropertyService.setProxyId(r12, r10);
		ResourcePropertyService.makeExpress(r5);
		ResourcePropertyService.makeExpress(r7);
		
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
		Link l12 = new Link("l12");
		Link l13 = new Link("l13");
		
		Link l14 = new Link("l14");
		Link l15 = new Link("l15");
		Link l16 = new Link("l16");
		Link l17 = new Link("l17");
		Link l18 = new Link("l18");
		Link l19 = new Link("l19");
		
		ArchitectureElementPropertyService.setOfferRoutingVariety(l0, false);
		ArchitectureElementPropertyService.setOfferRoutingVariety(l1, false);
		ArchitectureElementPropertyService.setOfferRoutingVariety(l6, false);
		ArchitectureElementPropertyService.setOfferRoutingVariety(l7, false);
		ArchitectureElementPropertyService.setOfferRoutingVariety(l12, false);
		ArchitectureElementPropertyService.setOfferRoutingVariety(l13, false);
		
		arch.addEdge(l0, r0, r2, EdgeType.UNDIRECTED);
		arch.addEdge(l1, r1, r2, EdgeType.UNDIRECTED);
		arch.addEdge(l2, r2, r3, EdgeType.UNDIRECTED);
		arch.addEdge(l3, r2, r4, EdgeType.UNDIRECTED);
		arch.addEdge(l4, r3, r5, EdgeType.UNDIRECTED);
		arch.addEdge(l5, r4, r5, EdgeType.UNDIRECTED);
		arch.addEdge(l6, r5, r6, EdgeType.UNDIRECTED);
		arch.addEdge(l7, r6, r7, EdgeType.UNDIRECTED);
		arch.addEdge(l8, r7, r8, EdgeType.UNDIRECTED);
		arch.addEdge(l9, r7, r9, EdgeType.UNDIRECTED);
		arch.addEdge(l10, r8, r10, EdgeType.UNDIRECTED);
		arch.addEdge(l11, r9, r10, EdgeType.UNDIRECTED);
		arch.addEdge(l12, r10, r11, EdgeType.UNDIRECTED);
		arch.addEdge(l13, r10, r12, EdgeType.UNDIRECTED);
		
		arch.addEdge(l14, r6, r13, EdgeType.UNDIRECTED);
		arch.addEdge(l15, r6, r14, EdgeType.UNDIRECTED);
		arch.addEdge(l16, r13, r15, EdgeType.UNDIRECTED);
		arch.addEdge(l17, r13, r16, EdgeType.UNDIRECTED);
		arch.addEdge(l18, r14, r17, EdgeType.UNDIRECTED);
		arch.addEdge(l19, r14, r18, EdgeType.UNDIRECTED);
		
		// mapping
		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		Mapping<Task, Resource> m0 = new Mapping<Task, Resource>("m0", t0, r0);
		Mapping<Task, Resource> m1 = new Mapping<Task, Resource>("m1", t1, r11);
		mappings.add(m0);
		mappings.add(m1);
	
		return new Specification(appl, arch, mappings);
	}

	public static Set<MappingVariable> getMappingVariables(Specification spec){
		Set<MappingVariable> mappingVars = new HashSet<MappingVariable>();
		for (Mapping<Task, Resource> m : spec.getMappings()) {
			mappingVars.add(Variables.varM(m));
		}
		return mappingVars;
	}
	
	public static Set<ApplicationVariable> getApplicationVars(Specification spec){
		Set<ApplicationVariable> applVars = new HashSet<ApplicationVariable>();
		for (Task task : spec.getApplication()) {
			applVars.add(Variables.varT(task));
		}
		for (Dependency d : spec.getApplication().getEdges()) {
			applVars.add(Variables.varDTT(d, spec.getApplication().getSource(d), spec.getApplication().getDest(d)));
		}
		return applVars;
	}
}
