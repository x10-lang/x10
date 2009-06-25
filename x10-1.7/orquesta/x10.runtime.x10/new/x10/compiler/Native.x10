package x10.compiler;

import x10.lang.annotations.MethodAnnotation;
import x10.lang.annotations.FieldAnnotation;

/**
 * Annotation to mark methods and fields as having a particular native implementation.
 * lang is the name of the language, typically "java" or "c".
 * code is the code to insert for a call to the method or an access to the field.
 *
 * For "java" annotations:
 *
 * Given a method with signature:
 *     def m[X, Y](x, y);
 * and a call
 *     o.m[A, B](a, b);
 * #0 = o
 * #1 = A
 * #2 = B
 * #3 = a
 * #4 = b
 * #5 = boxed representation of A
 * #6 = boxed representation of B
 * #7 = run-time Type object for A
 * #8 = run-time Type object for B
 */
public interface Native(lang: String, code: String) extends MethodAnnotation, FieldAnnotation { }
