package net.sf.opendse.encoding.routing;

import static org.junit.Assert.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;

import com.google.common.collect.ImmutableSet;

import net.sf.opendse.encoding.preprocessing.PreprocessedRoutings;
import net.sf.opendse.encoding.routing.RoutingEncodingPP.RoutingDescription;
import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.M;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.RoutingGraphVariable;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Task;
import verification.ConstraintVerifier;

public class RoutingEncodingPPTest {

	@Test
	public void testToConstraints() {
		CommunicationRoutingManager mockManager = mock(CommunicationRoutingManager.class);
		PreprocessedRoutings mockPP = mock(PreprocessedRoutings.class);
		RoutingEncodingPP encoding = new RoutingEncodingPP(mockManager, mockPP);
		@SuppressWarnings("unchecked")
		Routings<Task, Resource, Link> mockRoutings = mock(Routings.class);

		Resource res0 = new Resource("r0");
		Resource res1 = new Resource("r1");
		Resource res2 = new Resource("r2");
		Resource res3 = new Resource("r3");
		Resource res4 = new Resource("r4");
		Resource res5 = new Resource("r5");

		Resource res6 = new Resource("r6");
		Resource res7 = new Resource("r7");
		Resource res8 = new Resource("r8");
		Resource res9 = new Resource("r9");
		Resource res10 = new Resource("r10");
		Resource res11 = new Resource("r11");
		Resource res12 = new Resource("r12");
		Resource res13 = new Resource("r13");
		Resource res14 = new Resource("r14");
		Architecture<Resource, Link> routing0 = new Architecture<Resource, Link>();
		routing0.addVertex(res6);
		Architecture<Resource, Link> routing1 = new Architecture<Resource, Link>();
		routing1.addVertex(res7);
		Architecture<Resource, Link> routing2 = new Architecture<Resource, Link>();
		routing2.addVertex(res8);
		Architecture<Resource, Link> routing3 = new Architecture<Resource, Link>();
		routing3.addVertex(res9);
		Architecture<Resource, Link> routing4 = new Architecture<Resource, Link>();
		routing4.addVertex(res10);
		Architecture<Resource, Link> routing5 = new Architecture<Resource, Link>();
		routing5.addVertex(res11);
		Architecture<Resource, Link> routing6 = new Architecture<Resource, Link>();
		routing6.addVertex(res12);
		Architecture<Resource, Link> routing7 = new Architecture<Resource, Link>();
		routing7.addVertex(res13);
		Architecture<Resource, Link> routing8 = new Architecture<Resource, Link>();
		routing8.addVertex(res14);
		when(mockPP.getAllRoutings(res0, ImmutableSet.of(res2, res3))).thenReturn(ImmutableSet.of(routing0, routing2));
		when(mockPP.getAllRoutings(res0, ImmutableSet.of(res2, res4))).thenReturn(ImmutableSet.of(routing1, routing3));
		when(mockPP.getAllRoutings(res0, ImmutableSet.of(res2, res5))).thenReturn(ImmutableSet.of(routing4, routing5));
		when(mockPP.getAllRoutings(res1, ImmutableSet.of(res2, res3))).thenReturn(ImmutableSet.of(routing6));
		when(mockPP.getAllRoutings(res1, ImmutableSet.of(res2, res4))).thenReturn(ImmutableSet.of(routing7));
		when(mockPP.getAllRoutings(res1, ImmutableSet.of(res2, res5))).thenReturn(ImmutableSet.of(routing8));
		Task t0 = new Task("t0");
		Task t1 = new Task("t1");
		Task t2 = new Task("t2");
		Communication c0 = new Communication("c0");
		Dependency d0 = new Dependency("d0");
		Dependency d1 = new Dependency("d1");
		Dependency d2 = new Dependency("d2");
		T commVar = Variables.varT(c0);
		DTT dVar0 = Variables.varDTT(d0, t0, c0);
		DTT dVar1 = Variables.varDTT(d1, c0, t1);
		DTT dVar2 = Variables.varDTT(d2, c0, t2);
		Mapping<Task, Resource> m0 = new Mapping<Task, Resource>("m0", t0, res0);
		M mv0 = Variables.varM(m0);
		Mapping<Task, Resource> m1 = new Mapping<Task, Resource>("m1", t0, res1);
		M mv1 = Variables.varM(m1);
		Mapping<Task, Resource> m2 = new Mapping<Task, Resource>("m2", t1, res2);
		M mv2 = Variables.varM(m2);
		Mapping<Task, Resource> m3 = new Mapping<Task, Resource>("m3", t2, res3);
		M mv3 = Variables.varM(m3);
		Mapping<Task, Resource> m4 = new Mapping<Task, Resource>("m4", t2, res4);
		M mv4 = Variables.varM(m4);
		Mapping<Task, Resource> m5 = new Mapping<Task, Resource>("m5", t2, res5);
		M mv5 = Variables.varM(m5);
		Set<MappingVariable> mappingVariables = new HashSet<MappingVariable>();
		mappingVariables.add(mv0);
		mappingVariables.add(mv1);
		mappingVariables.add(mv2);
		mappingVariables.add(mv3);
		mappingVariables.add(mv4);
		mappingVariables.add(mv5);
		Set<ApplicationVariable> applicationVariables = new HashSet<ApplicationVariable>();
		applicationVariables.add(Variables.varT(t0));
		applicationVariables.add(Variables.varT(t1));
		applicationVariables.add(Variables.varT(t2));
		applicationVariables.add(commVar);
		applicationVariables.add(dVar0);
		applicationVariables.add(dVar1);
		applicationVariables.add(dVar2);

		Set<Constraint> cs = encoding.toConstraints(applicationVariables, mappingVariables, mockRoutings);
		ConstraintVerifier verifier = new ConstraintVerifier(cs);
		for (ApplicationVariable applVar : applicationVariables) {
			verifier.activateVariable(applVar);
		}
		verifier.activateVariable(mv0);
		verifier.activateVariable(mv2);
		verifier.activateVariable(mv3);
		verifier.deactivateVariable(mv1);
		verifier.deactivateVariable(mv4);
		verifier.deactivateVariable(mv5);
		RoutingGraphVariable routingVar0 = new RoutingGraphVariable(c0,
				new HashSet<Models.DirectedLink>(Models.getLinks(routing0)));
		RoutingGraphVariable routingVar1 = new RoutingGraphVariable(c0,
				new HashSet<Models.DirectedLink>(Models.getLinks(routing1)));
		RoutingGraphVariable routingVar2 = new RoutingGraphVariable(c0,
				new HashSet<Models.DirectedLink>(Models.getLinks(routing2)));
		RoutingGraphVariable routingVar3 = new RoutingGraphVariable(c0,
				new HashSet<Models.DirectedLink>(Models.getLinks(routing3)));
		RoutingGraphVariable routingVar4 = new RoutingGraphVariable(c0,
				new HashSet<Models.DirectedLink>(Models.getLinks(routing4)));
		RoutingGraphVariable routingVar5 = new RoutingGraphVariable(c0,
				new HashSet<Models.DirectedLink>(Models.getLinks(routing5)));
		RoutingGraphVariable routingVar6 = new RoutingGraphVariable(c0,
				new HashSet<Models.DirectedLink>(Models.getLinks(routing6)));
		RoutingGraphVariable routingVar7 = new RoutingGraphVariable(c0,
				new HashSet<Models.DirectedLink>(Models.getLinks(routing7)));
		RoutingGraphVariable routingVar8 = new RoutingGraphVariable(c0,
				new HashSet<Models.DirectedLink>(Models.getLinks(routing8)));
		verifier.deactivateVariable(routingVar2);

		verifier.verifyVariableActivated(routingVar0);
		verifier.verifyVariableDeactivated(routingVar1);
		verifier.verifyVariableDeactivated(routingVar3);
		verifier.verifyVariableDeactivated(routingVar4);
		verifier.verifyVariableDeactivated(routingVar5);
		verifier.verifyVariableDeactivated(routingVar6);
		verifier.verifyVariableDeactivated(routingVar7);
		verifier.verifyVariableDeactivated(routingVar8);
	}

	@Test
	public void testGetAllRoutingDescriptionsTest() {
		CommunicationRoutingManager mockManager = mock(CommunicationRoutingManager.class);
		PreprocessedRoutings mockPP = mock(PreprocessedRoutings.class);
		RoutingEncodingPP encoding = new RoutingEncodingPP(mockManager, mockPP);
		Resource res0 = new Resource("r0");
		Resource res1 = new Resource("r1");
		Resource res2 = new Resource("r2");
		Resource res3 = new Resource("r3");
		Resource res4 = new Resource("r4");
		Resource res5 = new Resource("r5");
		Task t0 = new Task("t0");
		Task t1 = new Task("t1");
		Task t2 = new Task("t2");
		Mapping<Task, Resource> m0 = new Mapping<Task, Resource>("m0", t0, res0);
		M mv0 = Variables.varM(m0);
		Mapping<Task, Resource> m1 = new Mapping<Task, Resource>("m1", t0, res1);
		M mv1 = Variables.varM(m1);
		Mapping<Task, Resource> m2 = new Mapping<Task, Resource>("m2", t1, res2);
		M mv2 = Variables.varM(m2);
		Mapping<Task, Resource> m3 = new Mapping<Task, Resource>("m3", t2, res3);
		M mv3 = Variables.varM(m3);
		Mapping<Task, Resource> m4 = new Mapping<Task, Resource>("m4", t2, res4);
		M mv4 = Variables.varM(m4);
		Mapping<Task, Resource> m5 = new Mapping<Task, Resource>("m5", t2, res5);
		M mv5 = Variables.varM(m5);
		List<MappingVariable> sourceMappings = new ArrayList<MappingVariable>();
		sourceMappings.add(mv0);
		sourceMappings.add(mv1);
		List<List<MappingVariable>> destMappings = new ArrayList<List<MappingVariable>>();
		List<MappingVariable> destMappingT1 = new ArrayList<MappingVariable>();
		List<MappingVariable> destMappingT2 = new ArrayList<MappingVariable>();
		destMappingT1.add(mv2);
		destMappingT2.add(mv3);
		destMappingT2.add(mv4);
		destMappingT2.add(mv5);
		destMappings.add(destMappingT1);
		destMappings.add(destMappingT2);
		Set<RoutingDescription> result = encoding.getAllRoutingDescriptions(sourceMappings, destMappings);
		assertEquals(6, result.size());
	}

	@Test
	public void testGetMappingVars() {
		CommunicationRoutingManager mockManager = mock(CommunicationRoutingManager.class);
		PreprocessedRoutings mockPP = mock(PreprocessedRoutings.class);
		RoutingEncodingPP encoding = new RoutingEncodingPP(mockManager, mockPP);
		Task t0 = new Task("t0");
		Task t1 = new Task("t1");
		Resource r0 = new Resource("r0");
		Resource r1 = new Resource("r1");
		M mvar0 = Variables.varM(new Mapping<Task, Resource>("m0", t0, r0));
		M mvar1 = Variables.varM(new Mapping<Task, Resource>("m1", t1, r0));
		M mvar2 = Variables.varM(new Mapping<Task, Resource>("m2", t0, r1));
		M mvar3 = Variables.varM(new Mapping<Task, Resource>("m3", t1, r1));
		Set<MappingVariable> testInput = new HashSet<MappingVariable>();
		testInput.add(mvar0);
		testInput.add(mvar1);
		testInput.add(mvar2);
		testInput.add(mvar3);
		List<MappingVariable> result = encoding.getMappingVarsForTask(t0, testInput);
		assertEquals(2, result.size());
		assertTrue(result.contains(mvar0));
		assertTrue(result.contains(mvar2));
	}

}
