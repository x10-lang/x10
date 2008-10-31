/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.compiler;

import x10.lang.annotations.ClassAnnotation;

/**
 * Annotation to mark classes as having a particular native representation.
 * lang is the name of the language, typically "java" or "c".
 * type is the class type of the native representation.  Type parameters are not supported.
 */ 
public interface NativeRep(lang: String, type: String) extends ClassAnnotation { }
