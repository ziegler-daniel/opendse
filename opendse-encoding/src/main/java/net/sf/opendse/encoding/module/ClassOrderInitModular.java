package net.sf.opendse.encoding.module;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Singleton;

import net.sf.opendse.encoding.variables.CLRR;
import net.sf.opendse.encoding.variables.CR;
import net.sf.opendse.encoding.variables.L;
import net.sf.opendse.encoding.variables.M;
import net.sf.opendse.encoding.variables.R;
import net.sf.opendse.optimization.ClassOrderInit;
import net.sf.opendse.optimization.encoding.variables.EAVI;

/**
 * The {@link ClassOrderInitModular} is used to set the order for
 * routing/mapping explorations which are based on a modular constraint
 * encoding and the corresponding variable types.
 * 
 * @author Fedor Smirnov
 *
 */
@Singleton
public class ClassOrderInitModular implements ClassOrderInit{

	@Override
	public List<Class<?>> getClassList() {
		List<Class<?>> result = new ArrayList<>();
		result.add(R.class);
		result.add(L.class);
		result.add(M.class);
		result.add(CR.class);
		result.add(CLRR.class);
		result.add(EAVI.class);
		return result;
	}

}
