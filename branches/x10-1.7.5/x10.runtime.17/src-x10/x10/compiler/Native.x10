/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

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
 * #2 = boxed representation of A
 * #3 = run-time Type object for A
 * #4 = B
 * #5 = boxed representation of B
 * #6 = run-time Type object for B
 * #7 = a
 * #8 = b
 */
public interface Native(lang: String, code: String) extends MethodAnnotation, FieldAnnotation { }
