package x10.generics;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a class's interfaces are generic interfaces that should be
 * instantiated.  The value is a list of actual instantiation parameters for
 * each interface (in order of reference).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface InstantiateInterfaces {
	InstantiateClass[] value();
}

