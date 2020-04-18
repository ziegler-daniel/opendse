package net.sf.opendse.encoding.preprocessing;

import java.util.Set;
import java.util.TreeSet;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.sf.opendse.encoding.SpecificationPreprocessor;
import net.sf.opendse.model.Specification;

/**
 * The {@link SpecificationPreprocessorMulti} is used to use multiple
 * {@link SpecificationPreprocessor}s during one exploration. It also allows to
 * establish an order of the preprocessing.
 * 
 * @author Fedor Smirnov
 *
 */
@Singleton
public class SpecificationPreprocessorMulti implements SpecificationPreprocessor {

	protected final Set<SpecificationPreprocessorComposable> preprocessors = new TreeSet<>();

	@Inject
	public SpecificationPreprocessorMulti(Set<SpecificationPreprocessorComposable> preprocessors) {
		this.preprocessors.addAll(preprocessors);
	}

	@Override
	public void preprocessSpecification(Specification userSpecification) {
		for (SpecificationPreprocessorComposable preprocessor : preprocessors) {
			preprocessor.preprocessSpecification(userSpecification);
		}
	}

	/**
	 * Adds the given {@link SpecificationPreprocessorComposable} to the list while
	 * respecting its priority.
	 * 
	 * @param preprocessor the preprocessor to add to the list
	 */
	public void addPreprocessor(SpecificationPreprocessorComposable preprocessor) {
		preprocessors.add(preprocessor);
	}
}
