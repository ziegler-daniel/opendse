package net.sf.opendse.encoding.module;

import net.sf.opendse.encoding.interpreter.InterpreterPreprocessedRoutings;
import net.sf.opendse.encoding.interpreter.InterpreterVariable;
import net.sf.opendse.encoding.preprocessing.PreprocessedRoutings;
import net.sf.opendse.encoding.preprocessing.PreprocessedRoutingsNonRedundantUnicast;
import net.sf.opendse.encoding.routing.RoutingEncodingFlexible;
import net.sf.opendse.encoding.routing.RoutingEncodingPP;
import net.sf.opendse.optimization.DesignSpaceExplorationModule;

public class PreprocessedRoutingsModule extends DesignSpaceExplorationModule {

	public boolean nonRedundantUnicast = false;

	public boolean isNonRedundantUnicast() {
		return nonRedundantUnicast;
	}

	public void setNonRedundantUnicast(boolean nonRedundantUnicast) {
		this.nonRedundantUnicast = nonRedundantUnicast;
	}

	@Override
	protected void config() {
		bind(InterpreterVariable.class).to(InterpreterPreprocessedRoutings.class);
		bind(RoutingEncodingFlexible.class).to(RoutingEncodingPP.class);
		bind(ClassOrderInitModular.class).to(ClassOrderInitModularPreprocess.class);

		if (nonRedundantUnicast) {
			bind(PreprocessedRoutings.class).to(PreprocessedRoutingsNonRedundantUnicast.class).in(SINGLETON);
		}
	}

}
