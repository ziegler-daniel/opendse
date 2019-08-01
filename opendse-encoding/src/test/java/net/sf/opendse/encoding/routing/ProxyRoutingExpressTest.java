package net.sf.opendse.encoding.routing;

import java.util.Set;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.RoutingEncoding;
import net.sf.opendse.encoding.routing.res.ProxyRoutingExpressTestRes;
import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.CLRR;
import net.sf.opendse.encoding.variables.CR;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import verification.ConstraintVerifier;

public class ProxyRoutingExpressTest {

	protected static RoutingEncoding getRoutingEncoding(boolean redundant, boolean lazy) {
		CommunicationFlowRoutingManager communicationFlowManager = new CommunicationFlowRoutingManagerDefault(
				new ActivationEncoderDefault(), new EndNodeEncoderMapping(), new RoutingResourceEncoderDefault(),
				redundant ? new RoutingEdgeEncoderRedundant() : new RoutingEdgeEncoderNonRedundant());
		CommunicationRoutingManagerDefault routingEncoderManager = new CommunicationRoutingManagerDefault(
				new OneDirectionEncoderDefault(),
				redundant ? new CycleBreakEncoderOrder() : new CycleBreakEncoderColor(),
				new CommunicationHierarchyEncoderDefault(), communicationFlowManager,
				lazy ? new ProxyEncoderLazy() : new ProxyEncoderCompact(),
				new AdditionalRoutingConstraintsEncoderMulti());

		return new RoutingEncodingFlexible(routingEncoderManager);
	}

	@Test
	public void testNonRedundant() {
		Specification spec = ProxyRoutingExpressTestRes.makeSpec();
		Application<Task, Dependency> appl = spec.getApplication();
		Architecture<Resource, Link> arch = spec.getArchitecture();
		Routings<Task, Resource, Link> routings = spec.getRoutings();

		Set<ApplicationVariable> applVars = ProxyRoutingExpressTestRes.getApplicationVars(spec);
		Set<MappingVariable> mappingVars = ProxyRoutingExpressTestRes.getMappingVariables(spec);
		RoutingEncoding encoderUnredundantLazy = getRoutingEncoding(false, true);
		RoutingEncoding encoderRedundantLazy = getRoutingEncoding(true, true);
		RoutingEncoding encoderUnredundantCompact = getRoutingEncoding(false, false);
		RoutingEncoding encoderRedundantCompact = getRoutingEncoding(true, false);
		Set<Constraint> csUnredundantLazy = encoderUnredundantLazy.toConstraints(applVars, mappingVars, routings);
		Set<Constraint> csRedundantLazy = encoderRedundantLazy.toConstraints(applVars, mappingVars, routings);
		Set<Constraint> csUnredundantCompact = encoderUnredundantCompact.toConstraints(applVars, mappingVars, routings);
		Set<Constraint> csRedundantCompact = encoderRedundantCompact.toConstraints(applVars, mappingVars, routings);
		ConstraintVerifier verifierUnredundantLazy = new ConstraintVerifier(csUnredundantLazy);
		ConstraintVerifier verifierRedundantLazy = new ConstraintVerifier(csRedundantLazy);
		ConstraintVerifier verifierUnredundantCompact = new ConstraintVerifier(csUnredundantCompact);
		ConstraintVerifier verifierRedundantCompact = new ConstraintVerifier(csRedundantCompact);
		for (ApplicationVariable applVar : applVars) {
			verifierUnredundantLazy.activateVariable(applVar);
			verifierRedundantLazy.activateVariable(applVar);
			verifierUnredundantCompact.activateVariable(applVar);
			verifierRedundantCompact.activateVariable(applVar);
		}
		for (MappingVariable mapVar : mappingVars) {
			verifierUnredundantLazy.activateVariable(mapVar);
			verifierRedundantLazy.activateVariable(mapVar);
			verifierUnredundantCompact.activateVariable(mapVar);
			verifierRedundantCompact.activateVariable(mapVar);
		}

		// get the encoding variables
		Resource r0 = arch.getVertex("r0");
		Resource r1 = arch.getVertex("r1");
		Resource r2 = arch.getVertex("r2");
		Resource r3 = arch.getVertex("r3");
		Resource r4 = arch.getVertex("r4");
		Resource r5 = arch.getVertex("r5");
		Resource r6 = arch.getVertex("r6");
		Resource r7 = arch.getVertex("r7");
		Resource r8 = arch.getVertex("r8");
		Resource r9 = arch.getVertex("r9");
		Resource r10 = arch.getVertex("r10");
		Resource r11 = arch.getVertex("r11");
		Resource r12 = arch.getVertex("r12");
		Resource r13 = arch.getVertex("r13");
		Resource r14 = arch.getVertex("r14");

		Link l0 = arch.getEdge("l0");
		Link l1 = arch.getEdge("l1");
		Link l2 = arch.getEdge("l2");
		Link l3 = arch.getEdge("l3");
		Link l4 = arch.getEdge("l4");
		Link l5 = arch.getEdge("l5");
		Link l6 = arch.getEdge("l6");
		Link l7 = arch.getEdge("l7");
		Link l8 = arch.getEdge("l8");
		Link l9 = arch.getEdge("l9");
		Link l10 = arch.getEdge("l10");
		Link l11 = arch.getEdge("l11");
		Link l12 = arch.getEdge("l12");
		Link l13 = arch.getEdge("l13");

		Task comm = appl.getVertex("comm");

		// CR cr0 = Variables.varCR(comm, r0);
		// CR cr1 = Variables.varCR(comm, r1);
		// CR cr2 = Variables.varCR(comm, r2);
		// CR cr3 = Variables.varCR(comm, r3);
		// CR cr4 = Variables.varCR(comm, r4);
		// CR cr5 = Variables.varCR(comm, r5);
		// CR cr6 = Variables.varCR(comm, r6);
		// CR cr7 = Variables.varCR(comm, r7);
		// CR cr8 = Variables.varCR(comm, r8);
		// CR cr9 = Variables.varCR(comm, r9);
		// CR cr10 = Variables.varCR(comm, r10);
		// CR cr11 = Variables.varCR(comm, r11);
		CR cr13 = Variables.varCR(comm, r13);
		CR cr14 = Variables.varCR(comm, r14);

		CLRR clrr0I = Variables.varCLRR(comm, l0, r0, r2);
		CLRR clrr1I = Variables.varCLRR(comm, l1, r1, r2);
		CLRR clrr2I = Variables.varCLRR(comm, l2, r2, r3);
		CLRR clrr3I = Variables.varCLRR(comm, l3, r2, r4);
		CLRR clrr4I = Variables.varCLRR(comm, l4, r3, r5);
		CLRR clrr5I = Variables.varCLRR(comm, l5, r4, r5);
		CLRR clrr6I = Variables.varCLRR(comm, l6, r5, r6);
		CLRR clrr7I = Variables.varCLRR(comm, l7, r6, r7);
		CLRR clrr8I = Variables.varCLRR(comm, l8, r7, r8);
		CLRR clrr9I = Variables.varCLRR(comm, l9, r7, r9);
		CLRR clrr10I = Variables.varCLRR(comm, l10, r8, r10);
		CLRR clrr11I = Variables.varCLRR(comm, l11, r9, r10);
		CLRR clrr12I = Variables.varCLRR(comm, l12, r10, r11);
		CLRR clrr13I = Variables.varCLRR(comm, l13, r10, r12);

		CLRR clrr0II = Variables.varCLRR(comm, l0, r2, r0);
		CLRR clrr1II = Variables.varCLRR(comm, l1, r2, r1);
		CLRR clrr2II = Variables.varCLRR(comm, l2, r3, r2);
		CLRR clrr3II = Variables.varCLRR(comm, l3, r4, r2);
		CLRR clrr4II = Variables.varCLRR(comm, l4, r5, r3);
		CLRR clrr5II = Variables.varCLRR(comm, l5, r5, r4);
		CLRR clrr6II = Variables.varCLRR(comm, l6, r6, r5);
		CLRR clrr7II = Variables.varCLRR(comm, l7, r7, r6);
		CLRR clrr8II = Variables.varCLRR(comm, l8, r8, r7);
		CLRR clrr9II = Variables.varCLRR(comm, l9, r9, r7);
		CLRR clrr10II = Variables.varCLRR(comm, l10, r10, r8);
		CLRR clrr11II = Variables.varCLRR(comm, l11, r10, r9);
		CLRR clrr12II = Variables.varCLRR(comm, l12, r11, r10);
		CLRR clrr13II = Variables.varCLRR(comm, l13, r12, r10);

		// non redundant test lazy
		// proxy areas
		verifierUnredundantLazy.verifyVariableNotFixed(clrr0I);
		verifierUnredundantLazy.verifyVariableNotFixed(clrr0II);
		verifierUnredundantLazy.verifyVariableNotFixed(clrr1I);
		verifierUnredundantLazy.verifyVariableNotFixed(clrr1II);
		verifierUnredundantLazy.verifyVariableNotFixed(clrr13I);
		verifierUnredundantLazy.verifyVariableNotFixed(clrr13II);
		verifierUnredundantLazy.verifyVariableNotFixed(clrr12I);
		verifierUnredundantLazy.verifyVariableNotFixed(clrr12II);

		// variety links
		// force a route
		verifierUnredundantLazy.activateVariable(clrr3I);
		verifierUnredundantLazy.activateVariable(clrr8I);
		// verify
		verifierUnredundantLazy.verifyVariableActivated(clrr5I);
		verifierUnredundantLazy.verifyVariableActivated(clrr10I);

		verifierUnredundantLazy.verifyVariableDeactivated(clrr2I);
		verifierUnredundantLazy.verifyVariableDeactivated(clrr4I);
		verifierUnredundantLazy.verifyVariableDeactivated(clrr9I);
		verifierUnredundantLazy.verifyVariableDeactivated(clrr11I);

		verifierUnredundantLazy.verifyVariableDeactivated(clrr2II);
		verifierUnredundantLazy.verifyVariableDeactivated(clrr3II);
		verifierUnredundantLazy.verifyVariableDeactivated(clrr4II);
		verifierUnredundantLazy.verifyVariableDeactivated(clrr5II);
		verifierUnredundantLazy.verifyVariableDeactivated(clrr8II);
		verifierUnredundantLazy.verifyVariableDeactivated(clrr9II);
		verifierUnredundantLazy.verifyVariableDeactivated(clrr10II);
		verifierUnredundantLazy.verifyVariableDeactivated(clrr11II);

		// express test
		verifierUnredundantLazy.verifyVariableNotFixed(clrr6I);
		verifierUnredundantLazy.verifyVariableNotFixed(clrr6II);
		verifierUnredundantLazy.verifyVariableNotFixed(clrr7I);
		verifierUnredundantLazy.verifyVariableNotFixed(clrr7II);

		verifierUnredundantLazy.verifyVariableDeactivated(cr13);
		verifierUnredundantLazy.verifyVariableDeactivated(cr14);

		// redundant test lazy
		// proxy areas
		verifierRedundantLazy.verifyVariableNotFixed(clrr0I);
		verifierRedundantLazy.verifyVariableNotFixed(clrr0II);
		verifierRedundantLazy.verifyVariableNotFixed(clrr1I);
		verifierRedundantLazy.verifyVariableNotFixed(clrr1II);
		verifierRedundantLazy.verifyVariableNotFixed(clrr13I);
		verifierRedundantLazy.verifyVariableNotFixed(clrr13II);
		verifierRedundantLazy.verifyVariableNotFixed(clrr12I);
		verifierRedundantLazy.verifyVariableNotFixed(clrr12II);

		// variety links
		// force redundancy
		verifierRedundantLazy.activateVariable(clrr2I);
		verifierRedundantLazy.activateVariable(clrr3I);
		verifierRedundantLazy.activateVariable(clrr8I);
		verifierRedundantLazy.activateVariable(clrr9I);
		// verify
		verifierRedundantLazy.verifyVariableActivated(clrr4I);
		verifierRedundantLazy.verifyVariableActivated(clrr5I);
		verifierRedundantLazy.verifyVariableActivated(clrr10I);
		verifierRedundantLazy.verifyVariableActivated(clrr11I);

		verifierRedundantLazy.verifyVariableDeactivated(clrr2II);
		verifierRedundantLazy.verifyVariableDeactivated(clrr3II);
		verifierRedundantLazy.verifyVariableDeactivated(clrr4II);
		verifierRedundantLazy.verifyVariableDeactivated(clrr5II);
		verifierRedundantLazy.verifyVariableDeactivated(clrr8II);
		verifierRedundantLazy.verifyVariableDeactivated(clrr9II);
		verifierRedundantLazy.verifyVariableDeactivated(clrr10II);
		verifierRedundantLazy.verifyVariableDeactivated(clrr11II);

		// express test
		verifierRedundantLazy.verifyVariableNotFixed(clrr6I);
		verifierRedundantLazy.verifyVariableNotFixed(clrr6II);
		verifierRedundantLazy.verifyVariableNotFixed(clrr7I);
		verifierRedundantLazy.verifyVariableNotFixed(clrr7II);

		verifierRedundantLazy.verifyVariableDeactivated(cr13);
		verifierRedundantLazy.verifyVariableDeactivated(cr14);

		// unredundant test compact

		// variety links
		// force a route
		verifierUnredundantCompact.activateVariable(clrr3I);
		verifierUnredundantCompact.activateVariable(clrr8I);
		// verify
		verifierUnredundantCompact.verifyVariableActivated(clrr5I);
		verifierUnredundantCompact.verifyVariableActivated(clrr10I);

		verifierUnredundantCompact.verifyVariableDeactivated(clrr2I);
		verifierUnredundantCompact.verifyVariableDeactivated(clrr4I);
		verifierUnredundantCompact.verifyVariableDeactivated(clrr9I);
		verifierUnredundantCompact.verifyVariableDeactivated(clrr11I);

		verifierUnredundantCompact.verifyVariableDeactivated(clrr2II);
		verifierUnredundantCompact.verifyVariableDeactivated(clrr3II);
		verifierUnredundantCompact.verifyVariableDeactivated(clrr4II);
		verifierUnredundantCompact.verifyVariableDeactivated(clrr5II);
		verifierUnredundantCompact.verifyVariableDeactivated(clrr8II);
		verifierUnredundantCompact.verifyVariableDeactivated(clrr9II);
		verifierUnredundantCompact.verifyVariableDeactivated(clrr10II);
		verifierUnredundantCompact.verifyVariableDeactivated(clrr11II);

		// proxy areas
		verifierUnredundantCompact.verifyVariableActivated(clrr0I);
		verifierUnredundantCompact.verifyVariableDeactivated(clrr0II);
		verifierUnredundantCompact.verifyVariableDeactivated(clrr1I);
		verifierUnredundantCompact.verifyVariableDeactivated(clrr1II);
		verifierUnredundantCompact.verifyVariableDeactivated(clrr13I);
		verifierUnredundantCompact.verifyVariableDeactivated(clrr13II);
		verifierUnredundantCompact.verifyVariableActivated(clrr12I);
		verifierUnredundantCompact.verifyVariableDeactivated(clrr12II);

		// express test
		verifierUnredundantCompact.verifyVariableActivated(clrr6I);
		verifierUnredundantCompact.verifyVariableDeactivated(clrr6II);
		verifierUnredundantCompact.verifyVariableActivated(clrr7I);
		verifierUnredundantCompact.verifyVariableDeactivated(clrr7II);

		verifierUnredundantCompact.verifyVariableDeactivated(cr13);
		verifierUnredundantCompact.verifyVariableDeactivated(cr14);

		// redundant test compact
		// force redundancy
		verifierRedundantCompact.activateVariable(clrr2I);
		verifierRedundantCompact.activateVariable(clrr3I);
		verifierRedundantCompact.activateVariable(clrr8I);
		verifierRedundantCompact.activateVariable(clrr9I);

		// verify
		// variety links
		verifierRedundantCompact.verifyVariableActivated(clrr4I);
		verifierRedundantCompact.verifyVariableActivated(clrr5I);
		verifierRedundantCompact.verifyVariableActivated(clrr10I);
		verifierRedundantCompact.verifyVariableActivated(clrr11I);

		verifierRedundantCompact.verifyVariableDeactivated(clrr2II);
		verifierRedundantCompact.verifyVariableDeactivated(clrr3II);
		verifierRedundantCompact.verifyVariableDeactivated(clrr4II);
		verifierRedundantCompact.verifyVariableDeactivated(clrr5II);
		verifierRedundantCompact.verifyVariableDeactivated(clrr8II);
		verifierRedundantCompact.verifyVariableDeactivated(clrr9II);
		verifierRedundantCompact.verifyVariableDeactivated(clrr10II);
		verifierRedundantCompact.verifyVariableDeactivated(clrr11II);

		// proxy areas
		verifierRedundantCompact.verifyVariableActivated(clrr0I);
		verifierRedundantCompact.verifyVariableDeactivated(clrr0II);
		verifierRedundantCompact.verifyVariableDeactivated(clrr1I);
		verifierRedundantCompact.verifyVariableDeactivated(clrr1II);
		verifierRedundantCompact.verifyVariableDeactivated(clrr13I);
		verifierRedundantCompact.verifyVariableDeactivated(clrr13II);
		verifierRedundantCompact.verifyVariableActivated(clrr12I);
		verifierRedundantCompact.verifyVariableDeactivated(clrr12II);

		// express test
		verifierRedundantCompact.verifyVariableActivated(clrr6I);
		verifierRedundantCompact.verifyVariableDeactivated(clrr6II);
		verifierRedundantCompact.verifyVariableActivated(clrr7I);
		verifierRedundantCompact.verifyVariableDeactivated(clrr7II);

		verifierRedundantCompact.verifyVariableDeactivated(cr13);
		verifierRedundantCompact.verifyVariableDeactivated(cr14);
	}
}
