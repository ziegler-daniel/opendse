package net.sf.opendse.encoding.allocation;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.AllocationEncoding;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.RoutingVariable;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;

/**
 * The {@link AllocationEncodingNone} is used when no allocation constraints are
 * to be added. In that case, the allocation of each solution must be
 * deterministically created by the interpreter.
 * 
 * @author Fedor Smirnov
 *
 */
public class AllocationEncodingNone implements AllocationEncoding {

	@Override
	public Set<Constraint> toConstraints(Set<MappingVariable> mappingVariables, Set<RoutingVariable> routingVariables,
			Architecture<Resource, Link> architecture) {
		return new HashSet<Constraint>();
	}
}
