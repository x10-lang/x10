package x10.generics;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stores the generic type parameter names for a class.
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE,ElementType.METHOD,ElementType.CONSTRUCTOR})
public @interface Synthetic { }
