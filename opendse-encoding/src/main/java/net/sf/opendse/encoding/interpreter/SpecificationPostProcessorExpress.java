package net.sf.opendse.encoding.interpreter;

import java.util.Set;

import com.google.inject.Inject;

import net.sf.opendse.encoding.preprocessing.ExpressRoutings;
import net.sf.opendse.encoding.preprocessing.ExpressRoutingsShortestPath;
import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Models.DirectedLink;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.properties.TaskPropertyService;
import net.sf.opendse.optimization.SpecificationWrapper;

/**
 * The {@link SpecificationPostProcessorExpress} is used during the
 * implementation to add the invariant links (proxy and express) which were
 * removed before the encoding.
 * 
 * @author Fedor Smirnov
 *
 */
public class SpecificationPostProcessorExpress extends SpecificationPostProcessorProxy {

	protected ExpressRoutings expressRoutings;

	@Inject
	public SpecificationPostProcessorExpress(SpecificationWrapper specWrapper,
			SpecificationPostProcessorMulti postProcessorMulti) {
		super(specWrapper, postProcessorMulti);
	}

	@Override
	public void postProcessImplementation(Specification implementation) {
		super.postProcessImplementation(implementation);
		// add the express links
		if (expressRoutings == null) {
			expressRoutings = new ExpressRoutingsShortestPath(annotatedArch);
		}
		Application<Task, Dependency> appl = implementation.getApplication();
		Architecture<Resource, Link> arch = implementation.getArchitecture();
		Mappings<Task, Resource> mappings = implementation.getMappings();
		for (Task t : appl) {
			Architecture<Resource, Link> routing = implementation.getRoutings().get(t);
			if (TaskPropertyService.isCommunication(t)) {
				if (appl.getPredecessorCount(t) > 1) {
					throw new IllegalArgumentException("more than one predecessor for comm");
				}
				Task pred = appl.getPredecessors(t).iterator().next();
				if (mappings.getTargets(pred).size() > 1) {
					throw new IllegalArgumentException("more than one binding target");
				}
				Resource src = mappings.getTargets(pred).iterator().next();
				for (Task succ : appl.getSuccessors(t)) {
					if (mappings.getTargets(succ).size() > 1) {
						throw new IllegalArgumentException("more than one binding target");
					}
					Resource dest = mappings.getTargets(succ).iterator().next();
					Set<DirectedLink> expressLinks = expressRoutings.getLinksBetweenExpressResources(src, dest);
					for (DirectedLink dl : expressLinks) {
						addLinkToImplementation(dl, arch, routing);
					}
				}
			}
		}
	}
}
