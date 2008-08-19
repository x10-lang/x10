/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.Iterator;

import polyglot.types.MethodDef;
import polyglot.types.SemanticException;
import polyglot.types.Type;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.List;
import polyglot.util.Position;
import polyglot.ast.ClassMember;
import polyglot.ast.Formal;
import polyglot.ast.Node;
import polyglot.ast.MethodDecl;
import polyglot.ast.MethodDecl_c;
import polyglot.ast.Formal_c;
import polyglot.types.ClassType_c;
import polyglot.ext.x10.types.X10Flags;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.types.MethodInstance;
import polyglot.util.TypedList;
import polyglot.visit.ContextVisitor;
import polyglot.ast.ClassBody_c;

public class X10ClassBody_c extends ClassBody_c {
	public X10ClassBody_c(Position pos, java.util.List<ClassMember> members) {
		super(pos,members);
	}
}
