package net.sf.opendse.encoding.module;

import net.sf.opendse.encoding.ImplementationEncodingModularDefault;
import net.sf.opendse.encoding.allocation.AllocationEncodingNone;
import net.sf.opendse.encoding.allocation.AllocationEncodingUtilization;
import net.sf.opendse.encoding.AllocationEncoding;
import net.sf.opendse.encoding.ImplementationEncodingModular;
import net.sf.opendse.encoding.interpreter.InterpreterVariable;
import net.sf.opendse.encoding.interpreter.SpecificationPostProcessorCycleRemover;
import net.sf.opendse.encoding.routing.CycleBreakEncoder;
import net.sf.opendse.encoding.routing.CycleBreakEncoderNone;
import net.sf.opendse.encoding.routing.RoutingEncodingFlexible;
import net.sf.opendse.encoding.routing.RoutingEncodingNone;
import net.sf.opendse.optimization.DesignSpaceExplorationCreator;
import net.sf.opendse.optimization.DesignSpaceExplorationDecoder;
import net.sf.opendse.optimization.DesignSpaceExplorationEvaluator;
import net.sf.opendse.optimization.DesignSpaceExplorationModule;
import net.sf.opendse.optimization.ImplementationEvaluator;
import net.sf.opendse.optimization.ImplementationWidgetService;
import net.sf.opendse.optimization.RoutingVariableClassOrder;
import net.sf.opendse.optimization.SATConstraints;
import net.sf.opendse.optimization.SATCreatorDecoder;
import net.sf.opendse.optimization.SpecificationToolBarService;
import net.sf.opendse.optimization.StagnationRestart;
import net.sf.opendse.optimization.constraints.SpecificationCapacityConstraints;
import net.sf.opendse.optimization.constraints.SpecificationConnectConstraints;
import net.sf.opendse.optimization.constraints.SpecificationConstraints;
import net.sf.opendse.optimization.constraints.SpecificationConstraintsMulti;
import net.sf.opendse.optimization.constraints.SpecificationElementsConstraints;
import net.sf.opendse.optimization.constraints.SpecificationRouterConstraints;
import net.sf.opendse.optimization.encoding.Encoding;
import net.sf.opendse.optimization.encoding.Encoding.RoutingEncoding;
import net.sf.opendse.optimization.encoding.ImplementationEncoding;
import net.sf.opendse.optimization.encoding.Interpreter;
import net.sf.opendse.optimization.encoding.InterpreterSpecification;

import org.opt4j.core.config.annotations.Parent;
import org.opt4j.core.config.annotations.Required;
import org.opt4j.core.problem.ProblemModule;
import org.opt4j.core.start.Constant;
import org.opt4j.viewer.VisualizationModule;

import com.google.inject.multibindings.Multibinder;

@Parent(DesignSpaceExplorationModule.class)
public class OptimizationNewModule extends ProblemModule {

	/**
	 * Different allocation encodings: NONE: Allocation not encoded, see
	 * {@link AllocationEncodingNone} UTILIZATION: Everything that is used for
	 * either tasks or messages allocated, see {@link AllocationEncodingUtilization}
	 * CUSTOM: Allocation encoding chosen in a different module (e.g. outside the
	 * opendse projects)
	 * 
	 * @author Fedor Smirnov
	 *
	 */
	protected enum ChosenAllocationEncoding {
		NONE, UTILIZATION, CUSTOM
	}

	/**
	 * Different routing encodings: NONE: Routing not encoded, see
	 * {@link RoutingEncodingNone} Flexible: Rather complex scheme, see
	 * {@link AllocationEncodingUtilization} CUSTOM: Routing encoding chosen in a
	 * different module (e.g. outside the opendse projects)
	 * 
	 * @author Fedor Smirnov
	 *
	 */
	protected enum ChosenRoutingEncoding {
		NONE, FLEXIBLE, CUSTOM
	}

	protected ChosenAllocationEncoding allocationEncoding = ChosenAllocationEncoding.UTILIZATION;
	
	protected ChosenRoutingEncoding routingEncodingType = ChosenRoutingEncoding.FLEXIBLE;

	protected RoutingEncoding routingEncoding = RoutingEncoding.FLOW;

	@Constant(value = "preprocessing", namespace = SATConstraints.class)
	protected boolean usePreprocessing = true;

	protected boolean stagnationRestartEnabled = true;

	protected boolean useModularEncoding = false;

	protected boolean removeCyclesManually = false;

	@Required(property = "stagnationRestartEnabled", elements = { "TRUE" })
	@Constant(value = "maximalNumberStagnatingGenerations", namespace = StagnationRestart.class)
	protected int maximalNumberStagnatingGenerations = 20;

	@Constant(value = "variableorder", namespace = SATCreatorDecoder.class)
	protected boolean useVariableOrder = true;

	public boolean isRemoveCyclesManually() {
		return removeCyclesManually;
	}

	public void setRemoveCyclesManually(boolean removeCyclesManually) {
		this.removeCyclesManually = removeCyclesManually;
	}

	public boolean isUseModularEncoding() {
		return useModularEncoding;
	}

	public void setUseModularEncoding(boolean useModularEncoding) {
		this.useModularEncoding = useModularEncoding;
	}

	public RoutingEncoding getRoutingEncoding() {
		return routingEncoding;
	}

	public void setRoutingEncoding(RoutingEncoding routingEncoding) {
		this.routingEncoding = routingEncoding;
	}

	public boolean isUsePreprocessing() {
		return usePreprocessing;
	}

	public void setUsePreprocessing(boolean usePreprocessing) {
		this.usePreprocessing = usePreprocessing;
	}

	public boolean isUseVariableOrder() {
		return useVariableOrder;
	}

	public void setUseVariableOrder(boolean useVariableOrder) {
		this.useVariableOrder = useVariableOrder;
	}

	public boolean isStagnationRestartEnabled() {
		return stagnationRestartEnabled;
	}

	public void setStagnationRestartEnabled(boolean stagnationRestartEnabled) {
		this.stagnationRestartEnabled = stagnationRestartEnabled;
	}

	public int getMaximalNumberStagnatingGenerations() {
		return maximalNumberStagnatingGenerations;
	}

	public void setMaximalNumberStagnatingGenerations(int maximalNumberStagnatingGenerations) {
		this.maximalNumberStagnatingGenerations = maximalNumberStagnatingGenerations;
	}

	public ChosenAllocationEncoding getAllocationEncoding() {
		return allocationEncoding;
	}

	public void setAllocationEncoding(ChosenAllocationEncoding allocationEncoding) {
		this.allocationEncoding = allocationEncoding;
	}

	@Override
	protected void config() {
		bindProblem(DesignSpaceExplorationCreator.class, DesignSpaceExplorationDecoder.class,
				DesignSpaceExplorationEvaluator.class);

		VisualizationModule.addIndividualMouseListener(binder(), ImplementationWidgetService.class);
		VisualizationModule.addToolBarService(binder(), SpecificationToolBarService.class);

		bind(SpecificationConstraints.class).to(SpecificationConstraintsMulti.class).in(SINGLETON);
		Multibinder<SpecificationConstraints> scmulti = Multibinder.newSetBinder(binder(),
				SpecificationConstraints.class);
		scmulti.addBinding().to(SpecificationCapacityConstraints.class);
		scmulti.addBinding().to(SpecificationConnectConstraints.class);
		scmulti.addBinding().to(SpecificationElementsConstraints.class);
		scmulti.addBinding().to(SpecificationRouterConstraints.class);

		Multibinder.newSetBinder(binder(), ImplementationEvaluator.class);

		if (stagnationRestartEnabled) {
			addOptimizerIterationListener(StagnationRestart.class);
		}

		if (!useModularEncoding) {
			bind(RoutingEncoding.class).toInstance(routingEncoding);
			if (useVariableOrder) {
				bind(RoutingVariableClassOrder.class).asEagerSingleton();
			}
			bind(Interpreter.class).to(InterpreterSpecification.class);
			bind(ImplementationEncoding.class).to(Encoding.class);
		} else {
			bind(Interpreter.class).to(InterpreterVariable.class);
			bind(ImplementationEncoding.class).to(ImplementationEncodingModular.class);
			bind(ImplementationEncodingModular.class).to(ImplementationEncodingModularDefault.class);
			if (useVariableOrder) {
				bind(VariableClassOrderModular.class).asEagerSingleton();
			}
		}

		if (removeCyclesManually) {
			bind(CycleBreakEncoder.class).to(CycleBreakEncoderNone.class);
			bind(SpecificationPostProcessorCycleRemover.class).asEagerSingleton();
		}

		// bind the allocation encoder
		switch (allocationEncoding) {
		case UTILIZATION:
			bind(AllocationEncoding.class).to(AllocationEncodingUtilization.class);
			break;

		case NONE:
			bind(AllocationEncoding.class).to(AllocationEncodingNone.class);

		case CUSTOM:
			// do nothing, defined elsewhere
			break;

		default:
			break;
		}

		// bind the routing encoder
		switch (routingEncodingType) {
		case NONE:
			bind(net.sf.opendse.encoding.RoutingEncoding.class).to(RoutingEncodingNone.class);
			break;

		case FLEXIBLE:
			bind(net.sf.opendse.encoding.RoutingEncoding.class).to(RoutingEncodingFlexible.class);
			
		case CUSTOM:
			// no binding, done elsewhere
			break;
		default:
			break;
		}
	}
}
