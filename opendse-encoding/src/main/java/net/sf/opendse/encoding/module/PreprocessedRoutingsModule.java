package net.sf.opendse.encoding.module;

import net.sf.opendse.encoding.interpreter.InterpreterPreprocessedRoutings;
import net.sf.opendse.encoding.interpreter.InterpreterVariable;
import net.sf.opendse.encoding.routing.RoutingEncodingFlexible;
import net.sf.opendse.encoding.routing.RoutingEncodingPP;
import net.sf.opendse.optimization.DesignSpaceExplorationModule;

public class PreprocessedRoutingsModule extends DesignSpaceExplorationModule {

	@Override
	protected void config() {
		bind(InterpreterVariable.class).to(InterpreterPreprocessedRoutings.class);
		bind(RoutingEncodingFlexible.class).to(RoutingEncodingPP.class);
		bind(VariableClassOrderPreprocessedRoutings.class).asEagerSingleton();
	}

}
