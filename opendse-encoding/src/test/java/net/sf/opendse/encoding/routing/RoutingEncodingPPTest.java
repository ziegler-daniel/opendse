package net.sf.opendse.encoding.routing;

import static org.junit.Assert.*;

import static org.mockito.Mockito.mock;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import net.sf.opendse.encoding.preprocessing.PreprocessedRoutings;
import net.sf.opendse.encoding.routing.RoutingEncodingPP.RoutingDescription;
import net.sf.opendse.encoding.variables.M;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

public class RoutingEncodingPPTest {

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
