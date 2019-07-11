package net.sf.opendse.encoding.routing;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.properties.ArchitectureElementPropertyService;
import net.sf.opendse.model.properties.ResourcePropertyService;
import net.sf.opendse.visualization.SpecificationViewer;

public class ExpressSearchTestRes {

	public static void main(String[] args) {
		SpecificationViewer.view(
				new Specification(new Application<Task, Dependency>(), getArch(), new Mappings<Task, Resource>()));
	}

	public static Architecture<Resource, Link> getArch() {
		Architecture<Resource, Link> result = new Architecture<Resource, Link>();

		Resource res0 = new Resource("res0");
		Resource res1 = new Resource("res1");
		Resource res2 = new Resource("res2");
		Resource res3 = new Resource("res3");
		Resource res4 = new Resource("res4");
		Resource res5 = new Resource("res5");
		Resource res6 = new Resource("res6");
		Resource res7 = new Resource("res7");
		Resource res8 = new Resource("res8");
		Resource res9 = new Resource("res9");
		Resource res10 = new Resource("res10");
		Resource res11 = new Resource("res11");
		Resource res12 = new Resource("res12");

		Link link0 = new Link("l0");
		Link link1 = new Link("l1");
		Link link2 = new Link("l2");
		Link link3 = new Link("l3");
		Link link4 = new Link("l4");
		Link link5 = new Link("l5");
		Link link6 = new Link("l6");
		Link link7 = new Link("l7");
		Link link8 = new Link("l8");
		Link link9 = new Link("l9");
		Link link10 = new Link("l10");
		Link link11 = new Link("l11");
		Link link12 = new Link("l12");
		Link link13 = new Link("l13");

		result.addEdge(link0, res0, res2, EdgeType.UNDIRECTED);
		result.addEdge(link1, res1, res2, EdgeType.UNDIRECTED);
		result.addEdge(link2, res2, res3, EdgeType.UNDIRECTED);
		result.addEdge(link3, res2, res4, EdgeType.UNDIRECTED);
		result.addEdge(link4, res3, res5, EdgeType.UNDIRECTED);
		result.addEdge(link5, res4, res5, EdgeType.UNDIRECTED);
		result.addEdge(link6, res5, res6, EdgeType.UNDIRECTED);
		result.addEdge(link7, res6, res7, EdgeType.UNDIRECTED);
		result.addEdge(link8, res7, res8, EdgeType.UNDIRECTED);
		result.addEdge(link9, res7, res9, EdgeType.UNDIRECTED);
		result.addEdge(link10, res8, res10, EdgeType.UNDIRECTED);
		result.addEdge(link11, res9, res10, EdgeType.UNDIRECTED);
		result.addEdge(link12, res10, res11, EdgeType.UNDIRECTED);
		result.addEdge(link13, res10, res12, EdgeType.UNDIRECTED);

		ArchitectureElementPropertyService.setOfferRoutingVariety(link0, false);
		ArchitectureElementPropertyService.setOfferRoutingVariety(link1, false);
		ArchitectureElementPropertyService.setOfferRoutingVariety(link12, false);
		ArchitectureElementPropertyService.setOfferRoutingVariety(link13, false);

		ResourcePropertyService.setProxyId(res0, res2);
		ResourcePropertyService.setProxyId(res1, res2);
		ResourcePropertyService.setProxyId(res12, res10);
		ResourcePropertyService.setProxyId(res11, res10);

		return result;
	}

}
