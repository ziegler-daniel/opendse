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
			if (insideProxyArea(res, routing)) {
				Set<Variable> srcMappings = new HashSet<Variable>();
				Set<Variable> destMappings = new HashSet<Variable>();
				getSrcDestMappings(mappingVariables, applicationVariables, communication, res, result, srcMappings,
						destMappings);
				List<DirectedLink> inLinks = Models.getInLinks(routing, res);
				List<DirectedLink> outLinks = Models.getOutLinks(routing, res);
				// formulate the constraints for the mapping variables
				for (Variable srcMapping : srcMappings) {
					result.add(makeMappingConstraint(communication, srcMapping, destMappings, outLinks));
				}
				for (Variable destMapping : destMappings) {
					result.add(makeMappingConstraint(communication, destMapping, srcMappings, inLinks));
				}
				// formulate the constraints for each in-Link
				for (DirectedLink inLink : inLinks) {
					result.add(makeLinkConstraint(communication, inLink, destMappings, outLinks));
				}
				for (DirectedLink outLink : outLinks) {
					result.add(makeLinkConstraint(communication, outLink, srcMappings, inLinks));
				}
			}
		}
		return result;
	}

	/**
	 * Generates the constraint stating that the given link may only be active if at
	 * least one of the enablers is active.
	 * 
	 * L_set - (sum(L_enable) + sum(M_enable)) <= 0
	 * 
	 * @param communication
	 *            the routed comm
	 * @param mappingToSet
	 *            the link to set
	 * @param enablingMappings
	 *            the enabling mappings
	 * @param enablingLinks
	 *            the enabling links
	 * @return the constraint stating that the given link may only be active if at
	 *         least one of the enablers is active
	 */
	protected Constraint makeLinkConstraint(Task communication, DirectedLink linkToSet, Set<Variable> enablingMappings,
			List<DirectedLink> oppositeLinks) {
		Constraint result = new Constraint(Operator.LE, 0);
		result.add(Variables.p(Variables.varCLRR(communication, linkToSet)));
		for (Variable enablingMapping : enablingMappings) {
			result.add(-1, Variables.p(enablingMapping));
		}
		for (DirectedLink oppositeLink : oppositeLinks) {
			if (!oppositeLink.getLink().getId().equals(linkToSet.getLink().getId())) {
				result.add(-1, Variables.p(Variables.varCLRR(communication, oppositeLink)));
			}
		}
		return result;
	}

	/**
	 * Generates the constraint stating that the given mapping may only be active if
	 * at least one of the enablers is active.
	 * 
	 * M_set - (sum(L_enable) + sum(M_enable)) <= 0
	 * 
	 * @param communication
	 *            the routed comm
	 * @param mappingToSet
	 *            the mapping to set
	 * @param enablingMappings
	 *            the enabling mappings
	 * @param enablingLinks
	 *            the enabling links
	 * @return the constraint stating that the given mapping may only be active if
	 *         at least one of the enablers is active
	 */
	protected Constraint makeMappingConstraint(Task communication, Variable mappingToSet,
			Set<Variable> enablingMappings, List<DirectedLink> enablingLinks) {
		Constraint result = new Constraint(Operator.LE, 0);
		result.add(Variables.p(mappingToSet));
		for (DirectedLink enableLink : enablingLinks) {
			result.add(-1, Variables.p(Variables.varCLRR(communication, enableLink)));
		}
		for (Variable enableMapping : enablingMappings) {
			result.add(-1, Variables.p(enableMapping));
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