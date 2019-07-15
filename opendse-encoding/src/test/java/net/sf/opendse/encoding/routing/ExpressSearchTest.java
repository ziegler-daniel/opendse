package net.sf.opendse.encoding.routing;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.encoding.preprocessing.SpecificationPreprocessorMulti;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.properties.ArchitectureElementPropertyService;
import net.sf.opendse.model.properties.ResourcePropertyService;

import static org.mockito.Mockito.mock;

public class ExpressSearchTest {

	@Test
	public void test() {
		Architecture<Resource, Link> arch = ExpressSearchTestRes.getArch();
		ExpressSearch expressSearch = new ExpressSearch(mock(SpecificationPreprocessorMulti.class));
		expressSearch.searchForExpressAreas(arch);

		assertFalse(ResourcePropertyService.isExpress(arch.getVertex("res0")));
		assertFalse(ResourcePropertyService.isExpress(arch.getVertex("res1")));
		assertFalse(ResourcePropertyService.isExpress(arch.getVertex("res2")));
		assertFalse(ResourcePropertyService.isExpress(arch.getVertex("res3")));
		assertFalse(ResourcePropertyService.isExpress(arch.getVertex("res4")));
		assertTrue(ResourcePropertyService.isExpress(arch.getVertex("res5")));
		assertTrue(ResourcePropertyService.isExpress(arch.getVertex("res6")));
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
