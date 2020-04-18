package net.sf.opendse.encoding.module;

import org.opt4j.core.config.Icons;
import org.opt4j.core.config.annotations.Category;
import org.opt4j.core.config.annotations.Icon;
import org.opt4j.core.start.Opt4JModule;

import com.google.inject.Binder;
import com.google.inject.multibindings.Multibinder;

import net.sf.opendse.encoding.SpecificationPreprocessor;
import net.sf.opendse.encoding.preprocessing.SpecificationPreprocessorComposable;
import net.sf.opendse.model.Specification;

/**
 * Parent of all modules used to bind
 * {@link SpecificationPreprocessorComposable}s.
 * 
 * @author Fedor Smirnov
 *
 */
@Icon(Icons.PROBLEM)
@Category
public abstract class AbstractPreprocessorModule extends Opt4JModule {

	/**
	 * Adds the provided {@link SpecificationPreprocessor} to the set of
	 * preprocessors which are used to process the {@link Specification} prior to
	 * the constraint formulation.
	 * 
	 * @param preprocessor the preprocessor to add
	 */
	protected void addPreprocessor(final Class<? extends SpecificationPreprocessorComposable> preprocessor) {
		addPreprocessor(binder(), preprocessor);
	}

	public static void addPreprocessor(Binder binder,
			final Class<? extends SpecificationPreprocessorComposable> preprocessor) {
		Multibinder<SpecificationPreprocessorComposable> multiBinder = Multibinder.newSetBinder(binder,
				SpecificationPreprocessorComposable.class);
		multiBinder.addBinding().to(preprocessor);
	}

}
