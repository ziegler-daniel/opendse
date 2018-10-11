package net.sf.opendse.encoding.preprocessing;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.optimization.SpecificationWrapper;

@Singleton
public class PreprocessedRoutingsBrute implements PreprocessedRoutings {

	protected final Architecture<Resource, Link> specArch;
	protected final PathExplorer explorer;
	protected final Map<RoutingDescription, Set<Architecture<Resource, Link>>> routingCache;

	protected class RoutingDescription {
		protected final Resource src;
		protected final Set<Resource> destinations;

		public RoutingDescription(Resource src, Set<Resource> destinations) {
			this.src = src;
			this.destinations = destinations;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((destinations == null) ? 0 : destinations.hashCode());
			result = prime * result + ((src == null) ? 0 : src.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			RoutingDescription other = (RoutingDescription) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (destinations == null) {
				if (other.destinations != null)
					return false;
			} else if (!destinations.equals(other.destinations))
				return false;
			if (src == null) {
				if (other.src != null)
					return false;
			} else if (!src.equals(other.src))
				return false;
			return true;
		}

		private PreprocessedRoutingsBrute getOuterType() {
			return PreprocessedRoutingsBrute.this;
		}

	}

	@Inject
	public PreprocessedRoutingsBrute(SpecificationWrapper specWrapper) {
		this.specArch = specWrapper.getSpecification().getArchitecture();
		this.explorer = new PathExplorer(specWrapper);
		this.routingCache = new HashMap<PreprocessedRoutingsBrute.RoutingDescription, Set<Architecture<Resource, Link>>>();
	}

	@Override
	public Set<Architecture<Resource, Link>> getAllRoutings(Resource source, Set<Resource> destinations) {
		RoutingDescription description = new RoutingDescription(source, destinations);
		if (!routingCache.containsKey(description)) {
			routingCache.put(description, explorer.getRoutingPossibilities(description.src, description.destinations));
		}
		return routingCache.get(description);
	}
}
