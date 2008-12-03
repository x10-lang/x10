package x10.generics;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a type (a class's superclass, a field type,
 * a method return type, or a method parameter type) is instantiated.
 * The value is a list of actual instantiation parameters.
 * Also used as a sub-annotation of @link{InstantiateInterfaces}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface InstantiateClass {
	String[] value();
}

