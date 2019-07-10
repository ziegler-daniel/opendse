package net.sf.opendse.encoding.routing;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.properties.ArchitectureElementPropertyService;
import net.sf.opendse.model.properties.ResourcePropertyService;

public class ExpressSearchTest {

	public static Architecture<Resource, Link> makeTestArch() {
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
		result.addEdge(link2, res2,res3, EdgeType.UNDIRECTED);
		result.addEdge(link3, res2,res4, EdgeType.UNDIRECTED);
		result.addEdge(link4, res3,res5, EdgeType.UNDIRECTED);
		result.addEdge(link5, res4,res5, EdgeType.UNDIRECTED);
		result.addEdge(link6, res5,res6, EdgeType.UNDIRECTED);
		result.addEdge(link7, res6,res7, EdgeType.UNDIRECTED);
		result.addEdge(link8, res7,res8, EdgeType.UNDIRECTED);
		result.addEdge(link9, res7,res9, EdgeType.UNDIRECTED);
		result.addEdge(link10, res8,res10, EdgeType.UNDIRECTED);
		result.addEdge(link11, res9,res10, EdgeType.UNDIRECTED);
		result.addEdge(link12, res10,res11, EdgeType.UNDIRECTED);
		result.addEdge(link13, res10,res12, EdgeType.UNDIRECTED);

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

	@Test
	public void test() {
		Architecture<Resource, Link> arch = makeTestArch();
		ExpressSearch expressSearch = new ExpressSearch();
		expressSearch.searchForExpressAreas(arch);
		
		assertFalse(ResourcePropertyService.isExpress(arch.getVertex("res0")));
		assertFalse(ResourcePropertyService.isExpress(arch.getVertex("res1")));
		assertFalse(ResourcePropertyService.isExpress(arch.getVertex("res2")));
		assertFalse(ResourcePropertyService.isExpress(arch.getVertex("res3")));
		assertFalse(ResourcePropertyService.isExpress(arch.getVertex("res4")));
		assertTrue(ResourcePropertyService.isExpress(arch.getVertex("res5")));
		assertFalse(ResourcePropertyService.isExpress(arch.getVertex("res6")));
		assertTrue(ResourcePropertyService.isExpress(arch.getVertex("res7")));
		assertFalse(ResourcePropertyService.isExpress(arch.getVertex("res8")));
		assertFalse(ResourcePropertyService.isExpress(arch.getVertex("res9")));
		assertFalse(ResourcePropertyService.isExpress(arch.getVertex("res10")));
		assertFalse(ResourcePropertyService.isExpress(arch.getVertex("res11")));
		assertFalse(ResourcePropertyService.isExpress(arch.getVertex("res12")));
		
		assertFalse(ArchitectureElementPropertyService.getOffersRoutingVariety(arch.getEdge("l0")));
		assertFalse(ArchitectureElementPropertyService.getOffersRoutingVariety(arch.getEdge("l1")));
		assertTrue(ArchitectureElementPropertyService.getOffersRoutingVariety(arch.getEdge("l2")));
		assertTrue(ArchitectureElementPropertyService.getOffersRoutingVariety(arch.getEdge("l3")));
		assertTrue(ArchitectureElementPropertyService.getOffersRoutingVariety(arch.getEdge("l4")));
		assertTrue(ArchitectureElementPropertyService.getOffersRoutingVariety(arch.getEdge("l5")));
		assertFalse(ArchitectureElementPropertyService.getOffersRoutingVariety(arch.getEdge("l6")));
		assertFalse(ArchitectureElementPropertyService.getOffersRoutingVariety(arch.getEdge("l7")));
		assertTrue(ArchitectureElementPropertyService.getOffersRoutingVariety(arch.getEdge("l8")));
		assertTrue(ArchitectureElementPropertyService.getOffersRoutingVariety(arch.getEdge("l9")));
		assertTrue(ArchitectureElementPropertyService.getOffersRoutingVariety(arch.getEdge("l10")));
		assertTrue(ArchitectureElementPropertyService.getOffersRoutingVariety(arch.getEdge("l11")));
		assertFalse(ArchitectureElementPropertyService.getOffersRoutingVariety(arch.getEdge("l12")));
		assertFalse(ArchitectureElementPropertyService.getOffersRoutingVariety(arch.getEdge("l13")));
	}
}
