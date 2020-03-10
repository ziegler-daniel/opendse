package net.sf.opendse.model.generator;

import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;

/**
 * The {@link SpecificationGenerator} creates a specification using its
 * generator objects.
 * 
 * 
 * @author Fedor Smirnov
 *
 * @param <A> the {@link ApplicationGenerator}
 * @param <R> the {@link ArchitectureGenerator}
 * @param <M> the {@link MappingGenerator}
 */
public abstract class SpecificationGenerator<A extends ApplicationGenerator, R extends ArchitectureGenerator, M extends MappingGenerator> {

	protected final A applGenerator;
	protected final R archGenerator;
	protected final M mappGenerator;

	public SpecificationGenerator(A applGenerator, R archGenerator, M mappingGenerator) {
		this.applGenerator = applGenerator;
		this.archGenerator = archGenerator;
		this.mappGenerator = mappingGenerator;
	}

	/**
	 * Generates the specification using the underlying generator objects.
	 * 
	 * @return the specification created using the underlying generator objects
	 */
	public Specification generateSpec() {
		Application<Task, Dependency> appl = applGenerator.generateApplication();
		Architecture<Resource, Link> arch = archGenerator.generateArchitecture();
		Mappings<Task, Resource> mappings = mappGenerator.generateMappings(appl, arch);
		return new Specification(appl, arch, mappings);
	}
}
