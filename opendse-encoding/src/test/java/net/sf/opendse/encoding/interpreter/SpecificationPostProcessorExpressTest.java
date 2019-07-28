package net.sf.opendse.encoding.interpreter;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.encoding.interpreter.res.SpecificationPostProcessorExpressTestRes;
import net.sf.opendse.encoding.preprocessing.ExpressSearch;
import net.sf.opendse.encoding.preprocessing.SpecificationPreprocessorMulti;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.optimization.SpecificationWrapper;
import static org.mockito.Mockito.mock;

import static org.mockito.Mockito.when;

public class SpecificationPostProcessorExpressTest {

	@Test
	public void test() {
		Specification spec = SpecificationPostProcessorExpressTestRes.getSpec();
		SpecificationWrapper wrapper = mock(SpecificationWrapper.class);
		when(wrapper.getSpecification()).thenReturn(spec);
		SpecificationPostProcessorMulti postProcessorMulti = mock(SpecificationPostProcessorMulti.class);
		ExpressSearch search = new ExpressSearch(mock(SpecificationPreprocessorMulti.class));
		search.preprocessSpecification(spec);
		SpecificationPostProcessorExpress postProcessor = new SpecificationPostProcessorExpress(wrapper, postProcessorMulti);
		Specification impl = SpecificationPostProcessorExpressTestRes.getImpl();
		//SpecificationViewer.view(impl);
		postProcessor.postProcessImplementation(impl);
		Task c =  impl.getApplication().getVertex("c");
		assertTrue(impl.getRoutings().get(c).getEdge("l5") != null);
		assertTrue(impl.getRoutings().get(c).getEdge("l2") != null);
	}
}
