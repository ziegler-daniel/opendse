package net.sf.opendse.encoding.preprocessing;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Models.DirectedLink;

import static org.mockito.Mockito.mock;

import java.util.Set;

public class ExpressRoutingsShortestPathTest {

	@Test
	public void test() {

		Architecture<Resource, Link> arch = ExpressSearchTestRes.getArch();
		ExpressSearch search = new ExpressSearch(mock(SpecificationPreprocessorMulti.class));
		search.searchForExpressAreas(arch);

		ExpressRoutingsShortestPath expressRoutings = new ExpressRoutingsShortestPath(arch);
		Resource r0 = arch.getVertex("res0");
		Resource r1 = arch.getVertex("res1");
		Resource res12 = arch.getVertex("res12");
		Set<DirectedLink> dLinks = expressRoutings.getLinksBetweenExpressResources(r0, res12);
		assertEquals(2, dLinks.size());
		Link link = dLinks.iterator().next().getLink();
		assertTrue(link.getId().equals("l6") || link.getId().equals("l7"));
		Set<DirectedLink> dLinks2 = expressRoutings.getLinksBetweenExpressResources(r0, r1);
		assertTrue(dLinks2.isEmpty());
	}
}
