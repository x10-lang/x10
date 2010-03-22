package clocked;

import x10.lang.annotations.*;

public interface Clocked[T](c:Clock, op:(T,T)=>T) extends TypeAnnotation {}, ExpressionAnnotation {}

