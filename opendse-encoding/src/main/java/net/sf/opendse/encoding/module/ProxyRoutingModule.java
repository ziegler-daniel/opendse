package net.sf.opendse.encoding.module;

import org.opt4j.core.config.Icons;
import org.opt4j.core.config.annotations.Icon;
import org.opt4j.core.config.annotations.Name;
import org.opt4j.core.config.annotations.Parent;
import net.sf.opendse.encoding.interpreter.SpecificationPostProcessorProxy;
import net.sf.opendse.encoding.preprocessing.ProxySearch;
import net.sf.opendse.encoding.preprocessing.ProxySearchReduction;
import net.sf.opendse.encoding.routing.ProxyEncoder;
import net.sf.opendse.encoding.routing.ProxyEncoderLazy;
import net.sf.opendse.optimization.DesignSpaceExplorationModule;

/**
 * The {@link ProxyRoutingModule} binds the classes necessary for a proxy-based
 * routing encoding.
 * 
 * @author Fedor Smirnov
 *
 */
@Parent(DesignSpaceExplorationModule.class)
@Icon(Icons.PROBLEM)
public class ProxyRoutingModule extends AbstractPreprocessorModule {

	@Name("activate the elements in the proxy areas")
	protected boolean activateProxyAreas = true;

	public boolean isActivateProxyAreas() {
		return activateProxyAreas;
	}

	public void setActivateProxyAreas(boolean activateProxyAreas) {
		this.activateProxyAreas = activateProxyAreas;
	}

	@Override
	protected void config() {
		if (!activateProxyAreas) {
			bind(ProxyEncoder.class).to(ProxyEncoderLazy.class);
			addPreprocessor(ProxySearchReduction.class);
			bind(SpecificationPostProcessorProxy.class).asEagerSingleton();
		} else {
			addPreprocessor(ProxySearch.class);
		}
	}
}
