package net.sf.opendse.encoding.preprocessing;

import static org.junit.Assert.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Specification;
import net.sf.opendse.optimization.SpecificationWrapper;

public class PreprocessedRoutingsBruteTest {

	@Test
	public void test() {
		Resource r0 = new Resource("r0");
		Resource r1 = new Resource("r1");
		Resource r2 = new Resource("r2");
		Resource r3 = new Resource("r3");
		Resource r4 = new Resource("r4");
		Resource r5 = new Resource("r5");

		Link l0 = new Link("l0");
		Link l1 = new Link("l1");
		Link l2 = new Link("l2");
		Link l3 = new Link("l3");
		Link l4 = new Link("l4");
		Link l5 = new Link("l5");

		Architecture<Resource, Link> specArch = new Architecture<Resource, Link>();
		specArch.addEdge(l0, r0, r1, EdgeType.UNDIRECTED);
		specArch.addEdge(l1, r0, r2, EdgeType.UNDIRECTED);
		specArch.addEdge(l2, r0, r3, EdgeType.UNDIRECTED);
		specArch.addEdge(l3, r1, r4, EdgeType.UNDIRECTED);
		specArch.addEdge(l4, r2, r4, EdgeType.UNDIRECTED);
		specArch.addEdge(l5, r3, r5, EdgeType.UNDIRECTED);
		Specification mockSpec = mock(Specification.class);
		when(mockSpec.getArchitecture()).thenReturn(specArch);
		SpecificationWrapper mockWrapper = mock(SpecificationWrapper.class);
		when(mockWrapper.getSpecification()).thenReturn(mockSpec);

		PreprocessedRoutingsBrute ppRoutings = new PreprocessedRoutingsBrute(mockWrapper);
		assertEquals(3, ppRoutings.getAllRoutings(r0, ImmutableSet.of(r4)).size());
		assertEquals(1, ppRoutings.getAllRoutings(r0, ImmutableSet.of(r5)).size());
		assertEquals(3, ppRoutings.getAllRoutings(r0, ImmutableSet.of(r4, r5)).size());
		Architecture<Resource, Link> routing = ppRoutings.getAllRoutings(r0, ImmutableSet.of(r5)).iterator().next();
		assertTrue(routing.containsVertex(r0));
		assertTrue(routing.containsVertex(r3));
		assertTrue(routing.containsVertex(r5));
		assertFalse(routing.containsVertex(r1));
		assertFalse(routing.containsVertex(r2));
		assertFalse(routing.containsVertex(r4));
	}

}
