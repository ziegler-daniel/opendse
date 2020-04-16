package net.sf.opendse.optimization;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Singleton;

import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.optimization.encoding.variables.EAVI;

/**
 * 
 * When using the {@link ClassOrderInitRouting}, the {@link VariableClassOrder}
 * is set to an order typically beneficial for constraints used during routing
 * and mapping optimization.
 * 
 * @author Fedor Smirnov
 *
 */
@Singleton
public class ClassOrderInitRouting implements ClassOrderInit {

	@Override
	public List<Class<?>> getClassList() {
		List<Class<?>> result = new ArrayList<>();
		result.add(Resource.class);
		result.add(Link.class);
		result.add(EAVI.class);
		result.add(Mapping.class);
		return result;
	}

}
