package net.sf.opendse.encoding.allocation;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Test;

import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.RoutingVariable;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;

public class AllocationEncodingNoneTest {

	@Test
	public void test() {
		AllocationEncodingNone encoding = new AllocationEncodingNone();
		assertTrue(encoding.toConstraints(new HashSet<MappingVariable>(), new HashSet<RoutingVariable>(),
				new Architecture<Resource, Link>()).isEmpty());
	}
}
