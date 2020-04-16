package net.sf.opendse.optimization;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Singleton;

/**
 * 
 * When using the {@link ClassOrderInitObject}, the {@link VariableClassOrder}
 * array contains only the object class, so that every variable is part of the
 * genotype.
 * 
 * @author Fedor Smirnov
 *
 */
@Singleton
public class ClassOrderInitObject implements ClassOrderInit {

	@Override
	public List<Class<?>> getClassList() {
		List<Class<?>> result = new ArrayList<>();
		result.add(Object.class);
		return result;
	}
}
