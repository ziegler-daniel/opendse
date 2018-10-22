package net.sf.opendse.encoding.preprocessing;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Specification;
import net.sf.opendse.optimization.SpecificationWrapper;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PreprocessedRoutingsConstructiveTest {

	protected static SpecificationWrapper makeWrapperMock(Architecture<Resource, Link> arch) {
		Specification mockSpec = mock(Specification.class);
		when(mockSpec.getArchitecture()).thenReturn(arch);
		SpecificationWrapper result = mock(SpecificationWrapper.class);
		when(result.getSpecification()).thenReturn(mockSpec);
		return result;
	}

	@Test
	public void test4() {
		Architecture<Resource, Link> arch = new Architecture<Resource, Link>();
		Resource r0 = new Resource("r0");
		Resource r1 = new Resource("r1");
		Resource r2 = new Resource("r2");
		Resource r3 = new Resource("r3");
		Resource r4 = new Resource("r4");
		Link l0 = new Link("l0");
		Link l1 = new Link("l1");
		Link l2 = new Link("l2");
		Link l3 = new Link("l3");
		Link l4 = new Link("l4");
		arch.addEdge(l0, r0, r1, EdgeType.UNDIRECTED);
		arch.addEdge(l1, r1, r2, EdgeType.UNDIRECTED);
		arch.addEdge(l2, r1, r2, EdgeType.UNDIRECTED);
		arch.addEdge(l3, r1, r3, EdgeType.UNDIRECTED);
		arch.addEdge(l4, r1, r4, EdgeType.UNDIRECTED);
		PreprocessedRoutingsConstructive ppRoutings = new PreprocessedRoutingsConstructive(makeWrapperMock(arch));
		Set<Resource> destinations = new HashSet<Resource>();
		destinations.add(r2);
		destinations.add(r3);
		Set<Architecture<Resource, Link>> routings = ppRoutings.getAllRoutings(r0, destinations);
		assertEquals(3, routings.size());
	}

	@Test
	public void test3() {
		Architecture<Resource, Link> arch = new Architecture<Resource, Link>();
		Resource r0 = new Resource("r0");
		Resource r1 = new Resource("r1");
		Link l0 = new Link("l0");
		Link l1 = new Link("l1");
		arch.addEdge(l0, r0, r1, EdgeType.UNDIRECTED);
		arch.addEdge(l1, r0, r1, EdgeType.UNDIRECTED);
		PreprocessedRoutingsConstructive ppRoutings = new PreprocessedRoutingsConstructive(makeWrapperMock(arch));
		Set<Resource> destinations = new HashSet<Resource>();
		destinations.add(r1);
		Set<Architecture<Resource, Link>> routings = ppRoutings.getAllRoutings(r0, destinations);
		assertEquals(3, routings.size());
	}

	@Test
	public void test2() {
		Architecture<Resource, Link> arch = new Architecture<Resource, Link>();
		Resource r0 = new Resource("r0");
		Resource r1 = new Resource("r1");
		Resource r2 = new Resource("r2");
		Resource r3 = new Resource("r3");
		Link l0 = new Link("l0");
		Link l1 = new Link("l1");
		Link l2 = new Link("l2");
		Link l3 = new Link("l3");
		arch.addEdge(l0, r0, r1, EdgeType.UNDIRECTED);
		arch.addEdge(l1, r0, r2, EdgeType.UNDIRECTED);
		arch.addEdge(l2, r1, r3, EdgeType.UNDIRECTED);
		arch.addEdge(l3, r2, r3, EdgeType.UNDIRECTED);
		PreprocessedRoutingsConstructive ppRoutings = new PreprocessedRoutingsConstructive(makeWrapperMock(arch));
		Set<Resource> destinations = new HashSet<Resource>();
		destinations.add(r3);
		Set<Architecture<Resource, Link>> routings = ppRoutings.getAllRoutings(r0, destinations);
		assertEquals(3, routings.size());
	}

	@Test
	public void test1() {
		Architecture<Resource, Link> arch = new Architecture<Resource, Link>();
		Resource r0 = new Resource("r0");
		Resource r1 = new Resource("r1");
		Resource r2 = new Resource("r2");
		Link l0 = new Link("l0");
		Link l1 = new Link("l1");
		arch.addEdge(l0, r0, r1, EdgeType.UNDIRECTED);
		arch.addEdge(l1, r1, r2, EdgeType.UNDIRECTED);
		PreprocessedRoutingsConstructive ppRoutings = new PreprocessedRoutingsConstructive(makeWrapperMock(arch));
		Set<Resource> destinations = new HashSet<Resource>();
		destinations.add(r2);
		Set<Architecture<Resource, Link>> routings = ppRoutings.getAllRoutings(r0, destinations);
		assertEquals(1, routings.size());
	}

}
