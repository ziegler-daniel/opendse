package net.sf.opendse.encoding.routing;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.ContradictionException;
import org.opt4j.satdecoding.DefaultSolver;
import org.opt4j.satdecoding.Solver;
import org.opt4j.satdecoding.TimeoutException;
import org.opt4j.satdecoding.VarOrder;

import net.sf.opendse.encoding.AllocationEncoding;
import net.sf.opendse.encoding.ApplicationEncoding;
import net.sf.opendse.encoding.ImplementationEncodingModularDefault;
import net.sf.opendse.encoding.MappingEncoding;
import net.sf.opendse.encoding.RoutingEncoding;
import net.sf.opendse.encoding.SpecificationPreprocessor;
import net.sf.opendse.encoding.SpecificationPreprocessorNone;
import net.sf.opendse.encoding.allocation.AllocationEncodingUtilization;
import net.sf.opendse.encoding.application.ApplicationConstraintManagerDefault;
import net.sf.opendse.encoding.application.ApplicationEncodingMode;
import net.sf.opendse.encoding.mapping.MappingConstraintManagerDefault;
import net.sf.opendse.encoding.mapping.MappingEncodingMode;
import net.sf.opendse.encoding.preprocessing.ExpressSearch;
import net.sf.opendse.encoding.preprocessing.ProxySearch;
import net.sf.opendse.encoding.preprocessing.SpecificationPreprocessorMulti;
import net.sf.opendse.encoding.routing.res.ProxyRoutingExpressTestRes2;
import net.sf.opendse.model.Specification;
import net.sf.opendse.optimization.SpecificationWrapper;
import net.sf.opendse.optimization.constraints.SpecificationConstraints;
import static org.mockito.Mockito.mock;

import java.util.Set;

import static org.mockito.Mockito.when;

public class ProxyRoutingExpress2Test {

	@Test
	public void test() {
		// make the spec
		Specification spec = ProxyRoutingExpressTestRes2.makeSpec();
		// find the proxies/express
		ProxySearch search = new ProxySearch(mock(SpecificationPreprocessorMulti.class));
		search.preprocessSpecification(spec);
		ExpressSearch expressSearch = new ExpressSearch(mock(SpecificationPreprocessorMulti.class));
		expressSearch.preprocessSpecification(spec);
		// create the wrapper
		SpecificationWrapper wrapper = mock(SpecificationWrapper.class);
		when(wrapper.getSpecification()).thenReturn(spec);
		// create the encodings
		ApplicationEncoding applicationEncoding = new ApplicationEncodingMode(
				new ApplicationConstraintManagerDefault());
		MappingEncoding mappingEncoding = new MappingEncodingMode(new MappingConstraintManagerDefault());
		RoutingEncoding routingEncoding = ProxyRoutingExpressTest.getRoutingEncoding(true, true);
		AllocationEncoding allocationEncoding = new AllocationEncodingUtilization();
		SpecificationPreprocessor preprocessor = new SpecificationPreprocessorNone();
		SpecificationConstraints mockConstraints = mock(SpecificationConstraints.class);
		ImplementationEncodingModularDefault encoding = new ImplementationEncodingModularDefault(preprocessor,
				applicationEncoding, mappingEncoding, routingEncoding, allocationEncoding, wrapper, mockConstraints);
		// create the constraints
		Set<Constraint> cs = encoding.toConstraints();
		Solver solver = new DefaultSolver();
		for (Constraint c : cs) {
			solver.addConstraint(c);
		}
		
		try {
			solver.solve(new VarOrder());
		} catch (ContradictionException e) {
			fail("contra");
		} catch (TimeoutException e) {
			fail("timeout");
		}
	}
}
