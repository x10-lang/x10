package com.ibm.torolab;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
public @interface TRJITOption {
   OptLevel optLevel() default OptLevel.WARM;;
   int      count() default -2;
   InlinePriority      inlinePriority() default InlinePriority.DEFAULT;
}
