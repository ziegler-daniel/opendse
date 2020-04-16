package net.sf.opendse.optimization;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * The {@link ClassOrderInit} initializes the class order by adding class types
 * to the array of {@link VariableClassOrder}.
 * 
 * @author fedor
 *
 */
@ImplementedBy(ClassOrderInitObject.class)
public interface ClassOrderInit {

	/**
	 * Returns the list of the classes which are to be added to the order.
	 * 
	 * @return
	 */
	List<Class<?>> getClassList();

}
