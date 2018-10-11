package net.sf.opendse.encoding.interpreter;

import java.util.Set;

import org.opt4j.satdecoding.Model;

import com.google.inject.Inject;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.encoding.ImplementationEncodingModular;
import net.sf.opendse.encoding.variables.RoutingGraphVariable;
import net.sf.opendse.encoding.variables.RoutingVariable;
import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Task;
import net.sf.opendse.optimization.constraints.SpecificationConstraints;

public class InterpreterPreprocessedRoutings extends InterpreterVariable {

	@Inject
	public InterpreterPreprocessedRoutings(SpecificationPostProcessor postProcessor,
			ImplementationEncodingModular implementationEncoding, SpecificationConstraints specificationConstraints) {
		super(postProcessor, implementationEncoding, specificationConstraints);
	}

	@Override
	protected Routings<Task, Resource, Link> decodeRoutings(Set<RoutingVariable> routingVariables, Model model,
			Application<Task, Dependency> implementationApplication,
			Architecture<Resource, Link> implementationAllocation) {
		Routings<Task, Resource, Link> result = new Routings<Task, Resource, Link>();
		// iterates all routing vars and searches for the routing graphs vars
		for (RoutingVariable rVar : routingVariables) {
			if (rVar instanceof RoutingGraphVariable) {
				RoutingGraphVariable rGraphVar = (RoutingGraphVariable) rVar;
				if (model.get(rGraphVar)) {
					Architecture<Resource, Link> routing = rGraphVar.getRouting();
					Task comm = rGraphVar.getCommunication();
					Architecture<Resource, Link> copyRouting = getChildArchitecture(routing, implementationAllocation);
					result.set(comm, copyRouting);
				}
			}
		}
		return result;
	}

	/**
	 * Returns a routing architecture, where the elements of the routing are the
	 * children of the elements in the allocation
	 * 
	 * @param routing  the routing (captures just the structure, without respecting
	 *                 the parent relation)
	 * @param implArch the allocation chosen for the implementation (contains the
	 *                 parent elements)
	 * @return a routing architecture, where the elements of the routing are the
	 *         children of the elements in the allocation
	 */
	protected Architecture<Resource, Link> getChildArchitecture(Architecture<Resource, Link> routing,
			Architecture<Resource, Link> implArch) {
		Architecture<Resource, Link> result = new Architecture<Resource, Link>();
		for (Link l : routing.getEdges()) {
			Link allocLink = implArch.getEdge(l.getId());
			Resource src = implArch.getVertex(routing.getSource(l).getId());
			Resource dest = implArch.getVertex(routing.getDest(l).getId());
			Link linkCopy = copy(allocLink);
			Resource srcCopy = copy(src);
			Resource destCopy = copy(dest);
			result.addEdge(linkCopy, srcCopy, destCopy, EdgeType.DIRECTED);
		}
		for (Resource r : routing) {
			if (result.getVertex(r.getId()) == null) {
				Resource allocRes = implArch.getVertex(r.getId());
				result.addVertex((Resource) copy(allocRes));
			}
		}
		return result;
	}
}
