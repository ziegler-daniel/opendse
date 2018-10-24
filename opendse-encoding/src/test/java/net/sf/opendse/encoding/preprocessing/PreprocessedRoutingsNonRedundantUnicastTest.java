package net.sf.opendse.encoding.preprocessing;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Specification;
import net.sf.opendse.optimization.SpecificationWrapper;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PreprocessedRoutingsNonRedundantUnicastTest {

	protected static Architecture<Resource, Link> makeArch(){
		Architecture<Resource, Link> result = new Architecture<Resource, Link>();
		Resource r0 = new Resource("r0");
		Resource r1 = new Resource("r1");
		Resource r2 = new Resource("r2");
		Link l0 = new Link("l0");
		Link l1 = new Link("l1");
		Link l2 = new Link("l2");
		Link l3 = new Link("l3");
		result.addEdge(l0, r0, r2, EdgeType.UNDIRECTED);
		result.addEdge(l1, r0, r2, EdgeType.UNDIRECTED);
		result.addEdge(l2, r0, r1, EdgeType.UNDIRECTED);
		result.addEdge(l3, r1, r2, EdgeType.UNDIRECTED);
		return result;
	}
	
	@Test
	public void testSameSrcAndDest() {
		Architecture<Resource, Link> arch = makeArch();
		Specification spec = mock(Specification.class);
		when(spec.getArchitecture()).thenReturn(arch);
		SpecificationWrapper wrapper = mock(SpecificationWrapper.class);
		when(wrapper.getSpecification()).thenReturn(spec);
		PreprocessedRoutingsNonRedundantUnicast routingPP = new PreprocessedRoutingsNonRedundantUnicast(wrapper);
		Resource src = arch.getVertex("r0");
		Set<Resource> destinations = new HashSet<Resource>(Arrays.asList(src));
		assertEquals(1, routingPP.getAllRoutings(src, destinations).size());
	}
	
	@Test
	public void testDifferentSrcAndDest() {
		Architecture<Resource, Link> arch = makeArch();
		Specification spec = mock(Specification.class);
		when(spec.getArchitecture()).thenReturn(arch);
		SpecificationWrapper wrapper = mock(SpecificationWrapper.class);
		when(wrapper.getSpecification()).thenReturn(spec);
		PreprocessedRoutingsNonRedundantUnicast routingPP = new PreprocessedRoutingsNonRedundantUnicast(wrapper);
		Resource src = arch.getVertex("r0");
		Resource dest = arch.getVertex("r2");
		Set<Resource> destinations = new HashSet<Resource>(Arrays.asList(dest));
		assertEquals(3, routingPP.getAllRoutings(src, destinations).size());
	}

}
