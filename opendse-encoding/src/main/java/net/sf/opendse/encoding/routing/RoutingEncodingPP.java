package net.sf.opendse.encoding.routing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Constraint.Operator;
import org.opt4j.satdecoding.Literal;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

import net.sf.opendse.encoding.preprocessing.PreprocessedRoutings;
import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.M;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.RoutingGraphVariable;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Task;

/**
 * Encodes the routing based on preprocessed routes. CANNOT handle dynamic
 * application elements in its current state.
 * 
 * @author Fedor Smirnov
 *
 */
public class RoutingEncodingPP extends RoutingEncodingFlexible {

	protected class RoutingDescription {
		protected final MappingVariable srcMappingVar;
		protected final List<MappingVariable> destinationMappingVars;

		protected RoutingDescription(MappingVariable src, List<MappingVariable> destinations) {
			this.srcMappingVar = src;
			this.destinationMappingVars = destinations;
		}
	}

	protected final PreprocessedRoutings preprocessedRoutings;
	protected final Map<Literal, Literal> pVarCache;

	@Inject
	public RoutingEncodingPP(CommunicationRoutingManager routingEncoderManager,
			PreprocessedRoutings preprocessedRoutings) {
		super(routingEncoderManager);
		this.preprocessedRoutings = preprocessedRoutings;
		this.pVarCache = new HashMap<Literal, Literal>();
	}

	@Override
	public Set<Constraint> toConstraints(Set<ApplicationVariable> applicationVariables,
			Set<MappingVariable> mappingVariables, Routings<Task, Resource, Link> routings) {
		Set<Constraint> result = new HashSet<Constraint>();
		Map<T, Set<DTT>> applVarMap = makeDependencyMap(applicationVariables);
		// iterates the comm vars and encodes the constraints for each comm (we assume
		// that each comm and all dependencies are active)
		for (Entry<T, Set<DTT>> e : applVarMap.entrySet()) {
			T commVar = e.getKey();
			Task comm = commVar.getTask();
			Set<Architecture<Resource, Link>> relevantRoutings = new HashSet<Architecture<Resource, Link>>();
			Set<DTT> depVars = e.getValue();
			Task srcTask = null;
			Set<Task> destTasks = new HashSet<Task>();
			for (DTT depVar : depVars) {
				Task src = depVar.getSourceTask();
				Task dest = depVar.getDestinationTask();
				if (src.equals(comm)) {
					destTasks.add(dest);
				} else if (dest.equals(comm)) {
					srcTask = src;
				} else {
					throw new IllegalArgumentException("Should not happen");
				}
			}
			List<MappingVariable> srcMappings = getMappingVarsForTask(srcTask, mappingVariables);
			List<List<MappingVariable>> destMappings = new ArrayList<List<MappingVariable>>();
			for (Task destTask : destTasks) {
				destMappings.add(getMappingVarsForTask(destTask, mappingVariables));
			}
			// iterate all combinations of a source resource and destination resource
			for (RoutingDescription routingDesc : getAllRoutingDescriptions(srcMappings, destMappings)) {
				Resource srcRes = ((M) routingDesc.srcMappingVar).getMapping().getTarget();
				Set<Resource> destRess = new HashSet<Resource>();
				for (MappingVariable destMapVar : routingDesc.destinationMappingVars) {
					destRess.add(((M) destMapVar).getMapping().getTarget());
				}
				// get all routings for this combination
				Set<Architecture<Resource, Link>> allRoutings = preprocessedRoutings.getAllRoutings(srcRes, destRess);
				// say that each of the routings may only be active if the mapping vars are set
				// accordingly
				// sum(mVar) - N * routingVar >= 0
				for (Architecture<Resource, Link> routing : allRoutings) {
					Constraint mappingConstraint = new Constraint(Operator.GE, 0);
					mappingConstraint.add(Variables.p((M) (routingDesc.srcMappingVar)));
					for (MappingVariable destMapvar : routingDesc.destinationMappingVars) {
						mappingConstraint.add(Variables.p((M) (destMapvar)));
					}
					int coefficient = 1 + destMappings.size();
					mappingConstraint.add(-coefficient, getRoutingLiteral(comm, routing));
					result.add(mappingConstraint);
				}
				// add them to the set of all relevant routings
				relevantRoutings.addAll(allRoutings);
			}
			// state that exactly one of the set of all possible routings has to be chosen
			Constraint chooseOneRoutingConstraint = new Constraint(Operator.EQ, 1);
			for (Architecture<Resource, Link> routing : relevantRoutings) {
				chooseOneRoutingConstraint.add(getRoutingLiteral(comm, routing));
			}
			result.add(chooseOneRoutingConstraint);
		}
		return result;
	}

	protected Literal getRoutingLiteral(Task communication, Architecture<Resource, Link> routing) {
		RoutingGraphVariable var = new RoutingGraphVariable(communication, routing);
		Literal lit = new Literal(var, true);
		if (!pVarCache.containsKey(lit)) {
			pVarCache.put(lit, lit);
		}
		return pVarCache.get(lit);
	}

	/**
	 * Returns the descriptions of all combinations of src and destination mappings
	 * forming routings.
	 * 
	 * @param sourceMappings
	 *            the possible mappings for the source task
	 * @param destMappings
	 *            a list with the possible mappings for all destinations
	 * @return the descriptions of all combinations of src and destination mappings
	 *         forming routings
	 */
	protected Set<RoutingDescription> getAllRoutingDescriptions(List<MappingVariable> sourceMappings,
			List<List<MappingVariable>> destMappings) {
		Set<RoutingDescription> result = new HashSet<RoutingEncodingPP.RoutingDescription>();
		List<List<MappingVariable>> cartesianInput = new ArrayList<List<MappingVariable>>();
		cartesianInput.add(sourceMappings);
		cartesianInput.addAll(destMappings);
		for (List<MappingVariable> cartProd : Lists.cartesianProduct(cartesianInput)) {
			List<MappingVariable> copy = new ArrayList<MappingVariable>(cartProd);
			MappingVariable srcMapping = copy.get(0);
			copy.remove(0);
			result.add(new RoutingDescription(srcMapping, copy));
		}
		return result;
	}

	/**
	 * Returns a list of all mapping variables that have the given task as src
	 * 
	 * @param t
	 *            the given task
	 * @param allMappingVariables
	 *            the set of variables to consider
	 * @return the list of all variables with the given task as source
	 */
	protected List<MappingVariable> getMappingVarsForTask(Task t, Set<MappingVariable> allMappingVariables) {
		List<MappingVariable> result = new ArrayList<MappingVariable>();
		for (MappingVariable mVar : allMappingVariables) {
			M mmVar = (M) mVar;
			if (mmVar.getMapping().getSource().equals(t)) {
				result.add(mmVar);
			}
		}
		return result;
	}
}
