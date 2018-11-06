package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Constraint.Operator;

import net.sf.opendse.encoding.constraints.Constraints;
import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.M;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.Variable;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Models.DirectedLink;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.properties.ArchitectureElementPropertyService;

/**
 * Encodes the activation of the preprocessed routes based on the chosen
 * mapping, that is, encodes the activation of all links that do not offer any
 * routing variety.
 * 
 * @author Fedor Smirnov
 *
 */
public class ProxyEncoderCompact implements ProxyEncoder {

	@Override
	public Set<Constraint> toConstraints(Task communication, Architecture<Resource, Link> routing,
			Set<MappingVariable> mappingVariables, Set<ApplicationVariable> applicationVariables) {
		Set<Constraint> result = new HashSet<Constraint>();
		// iterate all resources inside proxy areas
		for (Resource res : routing) {
			Set<Variable> srcMappings = new HashSet<Variable>();
			Set<Variable> destMappings = new HashSet<Variable>();
			getSrcDestMappings(mappingVariables, applicationVariables, communication, res, result, srcMappings,
					destMappings);
			if (insideProxyArea(res, routing)) {
				result.add(formulateSrcInLinkCondition(communication, srcMappings, destMappings, res, routing));
				result.add(formulateDestOutLinkCondition(communication, srcMappings, destMappings, res, routing));
			}
		}
		return result;
	}

	protected Constraint formulateSrcInLinkCondition(Task communication, Set<Variable> srcMappings,
			Set<Variable> destMappings, Resource res, Architecture<Resource, Link> routing) {
		return formulateEdgeConstraint(communication, srcMappings, destMappings, res, routing, true);
	}

	protected Constraint formulateDestOutLinkCondition(Task communication, Set<Variable> srcMappings,
			Set<Variable> destMappings, Resource res, Architecture<Resource, Link> routing) {
		return formulateEdgeConstraint(communication, srcMappings, destMappings, res, routing, false);
	}

	/**
	 * Formulates the constraint stating that a resource may only have in- (out-)
	 * -edges or be a src if it has at least one out- (in-)edge or is also the
	 * destination (src).
	 * 
	 * @param communication
	 *            the communication that is being routed
	 * @param srcMappings
	 *            the set of mapping variables of the src tasks
	 * @param destMappings
	 *            the set of mapping variables of the dest tasks
	 * @param res
	 *            the current resource
	 * @param routing
	 *            the routing graph
	 * @param srcIn
	 *            {@code true} if encoding conditions for being src or having
	 *            in-edges, {@code false} otherwise
	 * @return the constraint stating that a resource may only have in- (out-)
	 *         -edges or be a src if it has at least one out- (in-)edge or is also
	 *         the destination (src)
	 */
	protected Constraint formulateEdgeConstraint(Task communication, Set<Variable> srcMappings,
			Set<Variable> destMappings, Resource res, Architecture<Resource, Link> routing, boolean srcIn) {
		Constraint result = new Constraint(Operator.LE, 0);
		Set<Variable> consequenceMappings = srcIn ? srcMappings : destMappings;
		List<DirectedLink> consequenceEdges = srcIn ? Models.getInLinks(routing, res)
				: Models.getOutLinks(routing, res);
		Set<Variable> conditionMappings = srcIn ? destMappings : srcMappings;
		List<DirectedLink> conditionEdges = srcIn ? Models.getOutLinks(routing, res) : Models.getInLinks(routing, res);
		int constantFactor = consequenceMappings.size() + conditionEdges.size();
		for (Variable consequenceMapping : consequenceMappings) {
			result.add(Variables.p(consequenceMapping));
		}
		for (DirectedLink consequenceEdge : consequenceEdges) {
			result.add(Variables.p(Variables.varCLRR(communication, consequenceEdge)));
		}
		for (Variable conditionMapping : conditionMappings) {
			result.add(-constantFactor, Variables.p(conditionMapping));
		}
		for (DirectedLink conditionEdge : conditionEdges) {
			result.add(-constantFactor, Variables.p(Variables.varCLRR(communication, conditionEdge)));
		}
		return result;
	}

	/**
	 * Fills the sets of {@link Variable}s that encode the source and the
	 * destination mappings relevant for the given communication that are mapped on
	 * the given resource.
	 * 
	 * @param mappingVariables
	 *            the set of variables encoding the mappings of all tasks
	 * @param applicationVariables
	 *            the set of variables encoding the activation of the application
	 *            elements
	 * @param communication
	 *            the communication that is bein routed
	 * @param res
	 *            the given resource
	 * @param constraints
	 *            the set of constraints (it will be extended by this method)
	 */
	protected void getSrcDestMappings(Set<MappingVariable> mappingVariables,
			Set<ApplicationVariable> applicationVariables, Task communication, Resource res,
			Set<Constraint> constraints, Set<Variable> srcMappings, Set<Variable> destMappings) {
		for (ApplicationVariable applVar : applicationVariables) {
			if (applVar instanceof DTT) {
				DTT dttVar = (DTT) applVar;
				if (dttVar.getDestinationTask().equals(communication)) {
					// edge to the communication
					Task srcTask = dttVar.getSourceTask();
					for (MappingVariable mapVar : mappingVariables) {
						if (mapVar instanceof M) {
							M mVar = (M) mapVar;
							Mapping<Task, Resource> mapping = mVar.getMapping();
							if (mapping.getSource().equals(srcTask) && mapping.getTarget().equals(res)) {
								Set<Variable> args = new HashSet<Variable>();
								args.add(mVar);
								args.add(dttVar);
								Variable srcVar = Constraints.generateAndVariable(args, constraints);
								srcMappings.add(srcVar);
							}
						}
					}
				} else if (dttVar.getSourceTask().equals(communication)) {
					// edge from the communication
					Task destTask = dttVar.getDestinationTask();
					for (MappingVariable mapVar : mappingVariables) {
						if (mapVar instanceof M) {
							M mVar = (M) mapVar;
							Mapping<Task, Resource> mapping = mVar.getMapping();
							if (mapping.getSource().equals(destTask) && mapping.getTarget().equals(res)) {
								Set<Variable> args = new HashSet<Variable>();
								args.add(mVar);
								args.add(dttVar);
								Variable destVar = Constraints.generateAndVariable(args, constraints);
								destMappings.add(destVar);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Returns {@code true} if the resource is inside a proxy area, returns
	 * {@code false} otherwise.
	 * 
	 * @param res
	 *            the resource in question
	 * @param routing
	 *            the routing graph of the communication
	 * @return {@code true} if the resource is inside a proxy area, returns
	 *         {@code false} otherwise
	 */
	protected boolean insideProxyArea(Resource res, Architecture<Resource, Link> routing) {
		boolean result = false;
		for (Link link : routing.getIncidentEdges(res)) {
			result |= !ArchitectureElementPropertyService.getOffersRoutingVariety(link);
		}
		return result;
	}
}