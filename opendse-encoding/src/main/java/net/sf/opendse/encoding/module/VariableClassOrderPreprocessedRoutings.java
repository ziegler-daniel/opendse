package net.sf.opendse.encoding.module;

import com.google.inject.Inject;

import net.sf.opendse.encoding.variables.RoutingGraphVariable;
import net.sf.opendse.optimization.VariableClassOrder;

/**
 * The {@link VariableClassOrderPreprocessedRoutings} adds the
 * {@link RoutingGraphVariable} variables to the set of variables that are to be
 * explored.
 * 
 * @author Fedor Smirnov
 *
 */
public class VariableClassOrderPreprocessedRoutings extends VariableClassOrderModular {
	@Inject
	public VariableClassOrderPreprocessedRoutings(VariableClassOrder order) {
		super(order);
		order.add(RoutingGraphVariable.class);
	}
}
