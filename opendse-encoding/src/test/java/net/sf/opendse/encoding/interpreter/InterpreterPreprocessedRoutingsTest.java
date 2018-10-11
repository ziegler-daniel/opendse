package net.sf.opendse.encoding.interpreter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.opt4j.satdecoding.Model;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.encoding.ImplementationEncodingModular;
import net.sf.opendse.encoding.variables.RoutingGraphVariable;
import net.sf.opendse.encoding.variables.RoutingVariable;
import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.optimization.SpecificationWrapper;
import net.sf.opendse.optimization.constraints.SpecificationConstraints;

public class InterpreterPreprocessedRoutingsTest {

	@Test
	public void test() {
		// create the impl
		Application<Task, Dependency> appl = new Application<Task, Dependency>();
		Task t0 = new Task("t0");
		Task t1 = new Task("t1");
		Task t2 = new Task("t2");
		Task t3 = new Task("t3");
		Task t4 = new Task("t4");
		Communication c0 = new Communication("c0");
		Communication c1 = new Communication("c1");
		Dependency dep0 = new Dependency("d0");
		Dependency dep1 = new Dependency("d1");
		Dependency dep2 = new Dependency("d2");
		Dependency dep3 = new Dependency("d3");
		Dependency dep4 = new Dependency("d4");
		appl.addEdge(dep0, t0, c0, EdgeType.DIRECTED);
		appl.addEdge(dep1, t1, c1, EdgeType.DIRECTED);
		appl.addEdge(dep2, c0, t2, EdgeType.DIRECTED);
		appl.addEdge(dep3, c1, t3, EdgeType.DIRECTED);
		appl.addEdge(dep4, c1, t4, EdgeType.DIRECTED);

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
		arch.addEdge(l1, r0, r2, EdgeType.UNDIRECTED);
		arch.addEdge(l2, r1, r3, EdgeType.UNDIRECTED);
		arch.addEdge(l3, r2, r3, EdgeType.UNDIRECTED);
		arch.addEdge(l4, r2, r4, EdgeType.UNDIRECTED);

		Architecture<Resource, Link> c0Routing1 = new Architecture<Resource, Link>();
		c0Routing1.addEdge(l0, r0, r1, EdgeType.DIRECTED);
		c0Routing1.addEdge(l2, r1, r3, EdgeType.DIRECTED);
		Architecture<Resource, Link> c0Routing2 = new Architecture<Resource, Link>();
		c0Routing2.addEdge(l1, r0, r2, EdgeType.DIRECTED);
		c0Routing2.addEdge(l3, r2, r3, EdgeType.DIRECTED);
		Architecture<Resource, Link> c1Routing1 = new Architecture<Resource, Link>();
		c1Routing1.addEdge(l0, r0, r1, EdgeType.DIRECTED);
		c1Routing1.addEdge(l2, r1, r3, EdgeType.DIRECTED);
		c1Routing1.addEdge(l1, r0, r2, EdgeType.DIRECTED);
		c1Routing1.addEdge(l4, r2, r4, EdgeType.DIRECTED);
		Architecture<Resource, Link> c1Routing2 = new Architecture<Resource, Link>();
		c1Routing2.addEdge(l3, r2, r3, EdgeType.DIRECTED);
		c1Routing2.addEdge(l1, r0, r2, EdgeType.DIRECTED);
		c1Routing2.addEdge(l4, r2, r4, EdgeType.DIRECTED);

		RoutingGraphVariable routingGraphVarc01 = new RoutingGraphVariable(c0, c0Routing1);
		RoutingGraphVariable routingGraphVarc02 = new RoutingGraphVariable(c0, c0Routing2);
		RoutingGraphVariable routingGraphVarc11 = new RoutingGraphVariable(c1, c1Routing1);
		RoutingGraphVariable routingGraphVarc12 = new RoutingGraphVariable(c1, c1Routing2);
		Set<RoutingVariable> routingVariables = new HashSet<RoutingVariable>();
		routingVariables.add(routingGraphVarc01);
		routingVariables.add(routingGraphVarc02);
		routingVariables.add(routingGraphVarc11);
		routingVariables.add(routingGraphVarc12);
		Model model = new Model();
		model.set(routingGraphVarc01, true);
		model.set(routingGraphVarc02, false);
		model.set(routingGraphVarc11, false);
		model.set(routingGraphVarc12, true);

		// create the interpreter
		SpecificationPostProcessor mockPostProcessor = mock(SpecificationPostProcessor.class);
		ImplementationEncodingModular mockImplEncoding = mock(ImplementationEncodingModular.class);
		SpecificationConstraints mockSpecConstraints = mock(SpecificationConstraints.class);
		Specification mockSpec = mock(Specification.class);
		when(mockSpec.getArchitecture()).thenReturn(arch);
		SpecificationWrapper mockWrapper = mock(SpecificationWrapper.class);
		when(mockWrapper.getSpecification()).thenReturn(mockSpec);
		InterpreterPreprocessedRoutings interpreter = new InterpreterPreprocessedRoutings(mockPostProcessor,
				mockImplEncoding, mockSpecConstraints, mockWrapper);

		Routings<Task, Resource, Link> result = interpreter.decodeRoutings(routingVariables, model, appl, arch);
		Architecture<Resource, Link> routingComm0 = result.get(c0);
		assertEquals(2, routingComm0.getEdgeCount());
		assertTrue(routingComm0.getVertex("r0") != null);
		assertTrue(routingComm0.getVertex("r1") != null);
		assertTrue(routingComm0.getVertex("r3") != null);
		assertFalse(routingComm0.getVertex("r2") != null);
		assertFalse(routingComm0.getVertex("r4") != null);
		Architecture<Resource, Link> routingComm1 = result.get(c1);
		assertEquals(3, routingComm1.getEdgeCount());
		assertTrue(routingComm1.getVertex("r0") != null);
		assertTrue(routingComm1.getVertex("r2") != null);
		assertTrue(routingComm1.getVertex("r3") != null);
		assertTrue(routingComm1.getVertex("r4") != null);
		assertFalse(routingComm1.getVertex("r1") != null);
	}

}
