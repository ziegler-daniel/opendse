package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.RoutingEncoding;
import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Task;
import net.sf.opendse.optimization.encoding.Interpreter;

/**
 * The {@link RoutingEncodingNone} is used when the routing is not explored
 * based on constraints, but instead deterministically created by the
 * {@link Interpreter}.
 * 
 * @author Fedor Smirnov
 *
 */
public class RoutingEncodingNone implements RoutingEncoding {

	@Override
	public Set<Constraint> toConstraints(Set<ApplicationVariable> applicationVariables,
			Set<MappingVariable> mappingVariables, Routings<Task, Resource, Link> routings) {
		return new HashSet<Constraint>();
	}
}
