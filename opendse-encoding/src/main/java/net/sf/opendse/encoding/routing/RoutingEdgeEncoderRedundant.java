package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Constraint.Operator;

import net.sf.opendse.encoding.variables.DDdR;
import net.sf.opendse.encoding.variables.DDsR;
import net.sf.opendse.encoding.variables.Variable;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.properties.ResourcePropertyService;
import net.sf.opendse.model.Models.DirectedLink;

public class RoutingEdgeEncoderRedundant implements RoutingEdgeEncoder {

	@Override
	public Set<Constraint> toConstraints(CommunicationFlow communicationFlow, Architecture<Resource, Link> routing) {
		Set<Constraint> result = new HashSet<Constraint>();
		for (Resource res : routing) {
			// ignoring express resources
			if (!ResourcePropertyService.isExpress(res) && !ResourcePropertyService.hasProxy(res)) {
				result.addAll(generateSrcConstraints(communicationFlow, res, routing));
				result.addAll(generateDestConstraints(communicationFlow, res, routing));
				result.addAll(generateLinkBalanceConstraints(communicationFlow, res, routing));
			}
		}
		return result;
	}

	/**
	 * Generates the constraints stating that a resource either does not have any
	 * activated edges or that it has both in- and out- edges that are active.
	 * 
	 * src + sum(outLink) - CR >= 0 dest + sum(inLink) - CR >= 0
	 * 
	 * @param flow
	 *            the {@link CommunicationFlow} that is being routed
	 * @param res
	 *            the {@link Resource} that is being considered
	 * @param routing
	 *            the routing graph
	 * @return the constraints stating that a resource either does not have any
	 *         activated edges or that it has both in- and out- edges that are
	 *         active
	 */
	protected Set<Constraint> generateLinkBalanceConstraints(CommunicationFlow flow, Resource res,
			Architecture<Resource, Link> routing) {
		Set<Constraint> result = new HashSet<Constraint>();
		Set<DirectedLink> inLinks = new HashSet<Models.DirectedLink>(Models.getInLinks(routing, res));
		Set<DirectedLink> outLinks = new HashSet<Models.DirectedLink>(Models.getOutLinks(routing, res));
		DDsR srcVariable = Variables.varDDsR(flow, res);
		DDdR destVariable = Variables.varDDdR(flow, res);
		// 1) an in-link is only active if either destination or at least one out-link
		// DDLRR_in - DDdR - sum (DDLRR_out) <= 0
		for (DirectedLink inLink : inLinks) {
			result.add(createDirectedLinkConstraint(inLink, flow, destVariable, outLinks));
		}
		// 2) an out-link is only active it either source or at least one in-link
		for (DirectedLink outLink : outLinks) {
			result.add(createDirectedLinkConstraint(outLink, flow, srcVariable, inLinks));
		}
		return result;
	}

	/**
	 * Formulates the constraint stating that the given link is only active if at
	 * least one of the enablers is active
	 * 
	 * @param dirLink
	 *            the given link
	 * @param flow
	 *            the routed communication flow
	 * @param endPointEnabler
	 *            the end point variable
	 * @param linkEnablers
	 *            the enabling links
	 * @return the constraint stating that the given link is only active if at least
	 *         one of the enablers is active
	 */
	public Constraint createDirectedLinkConstraint(DirectedLink dirLink, CommunicationFlow flow,
			Variable endPointEnabler, Set<DirectedLink> linkEnablers) {
		Constraint result = new Constraint(Operator.LE, 0);
		result.add(Variables.p(Variables.varDDLRR(flow, dirLink)));
		result.add(-1, Variables.p(endPointEnabler));
		for (DirectedLink enablingLink : linkEnablers) {
			if (!dirLink.getLink().getId().equals(enablingLink.getLink().getId())) {
				result.add(-1, Variables.p(Variables.varDDLRR(flow, enablingLink)));
			}
		}
		return result;
	}

	/**
	 * Generates the constraints stating that the destination of the communication
	 * flow (which is not a source) has a) at least one in-edge and b) no out-edges.
	 * 
	 * @param flow
	 *            the {@link CommunicationFlow} that is being routed
	 * @param res
	 *            the {@link Resource} that is being considered
	 * @param routing
	 *            the routing graph
	 * @return the constraints stating that the destination of the communication
	 *         flow has a) at least one in-edge and b) no out-edges
	 */
	protected Set<Constraint> generateDestConstraints(CommunicationFlow flow, Resource res,
			Architecture<Resource, Link> routing) {
		return generateEndPointConstraints(flow, res, routing, false);
	}

	/**
	 * Generates the constraints stating that the source of the communication flow
	 * (which is not a destination) has a) at least one out-edge
	 * 
	 * sum(outLink) + dest - src >= 0 each inLink <= src
	 * 
	 * @param flow
	 *            the {@link CommunicationFlow} that is being routed
	 * @param res
	 *            the {@link Resource} that is being considered
	 * @param routing
	 *            the routing graph
	 * @return the constraints stating that the source of the communication flow has
	 *         a) at least one out-edge and b) no in-edges
	 */
	protected Set<Constraint> generateSrcConstraints(CommunicationFlow flow, Resource res,
			Architecture<Resource, Link> routing) {
		return generateEndPointConstraints(flow, res, routing, true);
	}

	protected Set<Constraint> generateEndPointConstraints(CommunicationFlow flow, Resource res,
			Architecture<Resource, Link> routing, boolean source) {
		Set<Constraint> result = new HashSet<Constraint>();
		// Set<DirectedLink> inLinks = source ? new
		// HashSet<Models.DirectedLink>(Models.getInLinks(routing, res))
		// : new HashSet<Models.DirectedLink>(Models.getOutLinks(routing, res));
		Set<DirectedLink> outLinks = source ? new HashSet<Models.DirectedLink>(Models.getOutLinks(routing, res))
				: new HashSet<Models.DirectedLink>(Models.getInLinks(routing, res));
		Constraint outLinkConstraint = new Constraint(Operator.GE, 0);
		Variable resourceSource = source ? Variables.varDDsR(flow, res) : Variables.varDDdR(flow, res);
		Variable resourceDest = source ? Variables.varDDdR(flow, res) : Variables.varDDsR(flow, res);
		outLinkConstraint.add(Variables.p(resourceDest));
		outLinkConstraint.add(-1, Variables.p(resourceSource));
		for (DirectedLink outLink : outLinks) {
			outLinkConstraint.add(Variables.p(Variables.varDDLRR(flow, outLink)));
		}
		result.add(outLinkConstraint);
		// for (DirectedLink inLink : inLinks) {
		// Constraint inLinkConstraint = new Constraint(Operator.LE, 0);
		// inLinkConstraint.add(Variables.p(Variables.varDDLRR(flow, inLink)));
		// inLinkConstraint.add(-1, Variables.n(resourceSource));
		// result.add(inLinkConstraint);
		// }
		return result;
	}
}
