package net.sf.opendse.encoding.module;

import java.util.List;

import com.google.inject.Singleton;

import net.sf.opendse.encoding.variables.RoutingGraphVariable;

/**
 * The {@link ClassOrderInitModularPreprocess} extends the
 * {@link ClassOrderInitModular} by adding an additional variable type to the
 * order.
 * 
 * @author Fedor Smirnov
 *
 */
@Singleton
public class ClassOrderInitModularPreprocess extends ClassOrderInitModular {

	@Override
	public List<Class<?>> getClassList() {
		List<Class<?>> result = super.getClassList();
		result.add(RoutingGraphVariable.class);
		return result;
	}
	
}
