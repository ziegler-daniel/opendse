package net.sf.opendse.encoding.interpreter;

import java.util.HashSet;
import java.util.Set;

import com.google.inject.Inject;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.encoding.preprocessing.ProxyRoutingsShortestPath;
import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.Models.DirectedLink;
import net.sf.opendse.model.properties.ResourcePropertyService;
import net.sf.opendse.model.properties.TaskPropertyService;
import net.sf.opendse.optimization.SpecificationWrapper;

/**
 * The {@link SpecificationPostProcessorProxy} adds the links from the proxy
 * areas to the implementation.
 * 
 * @author Fedor Smirnov
 *
 */
public class SpecificationPostProcessorProxy extends SpecificationPostProcessorComposable {

	protected ProxyRoutingsShortestPath proxyRoutings = null;
	protected final Architecture<Resource, Link> annotatedArch;

	@Inject
	public SpecificationPostProcessorProxy(SpecificationWrapper specWrapper,
			SpecificationPostProcessorMulti postProcessorMulti) {
		this.annotatedArch = specWrapper.getSpecification().getArchitecture();
		postProcessorMulti.addPostProcessor(this);
	}

	@Override
	public void postProcessImplementation(Specification implementation) {
		if (proxyRoutings == null) {
			proxyRoutings = new ProxyRoutingsShortestPath(annotatedArch);
		}
		Application<Task, Dependency> appl = implementation.getApplication();
		Architecture<Resource, Link> arch = implementation.getArchitecture();
		Mappings<Task, Resource> mappings = implementation.getMappings();
		Routings<Task, Resource, Link> routings = implementation.getRoutings();
		// iterates all communication flows
		for (Task comm : implementation.getApplication()) {
			if (TaskPropertyService.isCommunication(comm)) {
				Architecture<Resource, Link> routing = routings.get(comm);
				Set<Task> srcTasks = new HashSet<Task>(appl.getPredecessors(comm));
				Set<Task> destTasks = new HashSet<Task>(appl.getSuccessors(comm));
				for (Task srcTask : srcTasks) {
					for (Task destTask : destTasks) {
						Resource src = mappings.get(srcTask).iterator().next().getTarget();
						Resource dest = mappings.get(destTask).iterator().next().getTarget();

						String srcProxy = ResourcePropertyService.getProxyId(annotatedArch.getVertex(src));
						String destProxy = ResourcePropertyService.getProxyId(annotatedArch.getVertex(dest));

						boolean isSrcProxy = !srcProxy.equals(src.getId());
						boolean isDestProxy = !destProxy.equals(dest.getId());
						Set<DirectedLink> route = new HashSet<Models.DirectedLink>();
						if (isSrcProxy && isDestProxy && srcProxy.equals(destProxy)) {
							// Same proxy area => add the intermediate links
							route.addAll(proxyRoutings.getLinksBetweenResources(src, dest));
						} else {
							// add the routes to and from the proxy
							route.addAll(proxyRoutings.getResourceToProxyLinks(src));
							route.addAll(proxyRoutings.getProxyToResourceLinks(dest));
						}
						for (DirectedLink dLink : route) {
							addLinkToImplementation(dLink, arch, routing);
						}
					}
				}
			}
		}
		return;
	}

	/**
	 * Adds the given link to the architecture and the provided routing while
	 * upholding the parent-child relation between the two graphs.
	 * 
	 * @param dLink
	 *            the directed link
	 * @param arch
	 *            the architecture graph
	 * @param routing
	 *            the routing graph
	 */
	protected void addLinkToImplementation(DirectedLink dLink, Architecture<Resource, Link> arch,
			Architecture<Resource, Link> routing) {
		if (!arch.containsEdge(dLink.getLink())) {
			// architecture does not contain link => spec arch link has to be copied
			Link implLink = InterpreterVariable.copy(dLink.getLink());
			Resource implSrc = InterpreterVariable.copy(dLink.getSource());
			Resource implDest = InterpreterVariable.copy(dLink.getDest());
			arch.addEdge(implLink, implSrc, implDest, EdgeType.UNDIRECTED);
		}
		if (!routing.containsEdge(dLink.getLink())) {
			// routing does not contain link => has to be copied from impl architecture
			Link routingLink = InterpreterVariable.copy(arch.getEdge(dLink.getLink()));
			Resource routingSrc = InterpreterVariable.copy(arch.getVertex(dLink.getSource()));
			Resource routingDest = InterpreterVariable.copy(arch.getVertex(dLink.getDest()));
			routing.addEdge(routingLink, routingSrc, routingDest, EdgeType.DIRECTED);
		}
	}
}
