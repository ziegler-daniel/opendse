package net.sf.opendse.encoding.routing;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Test;

import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Task;

public class RoutingEncodingNoneTest {

	@Test
	public void test() {
		RoutingEncodingNone encoding = new RoutingEncodingNone();
		assertTrue(encoding.toConstraints(new HashSet<ApplicationVariable>(), new HashSet<MappingVariable>(),
				new Routings<Task, Resource, Link>()).isEmpty());
	}
}
