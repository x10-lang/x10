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
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.types.MethodInstance;
import polyglot.util.TypedList;
import polyglot.visit.TypeChecker;
import polyglot.ast.ClassBody_c;

public class X10ClassBody_c extends ClassBody_c {
	public X10ClassBody_c(Position pos,java.util.List members) {
		super(pos,members);
	}

	public Node typeCheck(TypeChecker tc) throws SemanticException {
		X10ClassBody_c result = (X10ClassBody_c) super.typeCheck(tc);
		X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
		
		ClassBody_c cb = (ClassBody_c) result.node();
		for (ClassMember o : cb.members()) {
			if (o instanceof MethodDecl) {
				MethodDecl_c md = (MethodDecl_c) o;
				MethodDef mi = md.methodDef();
				if(mi.flags().isNative()){
					
					if (!mi.returnType().get().isPrimitive())
						throw new SemanticException("extern return type \""+mi.returnType()+"\" is not a primitive type.",
								md.position());
					
					for (Formal parameter : md.formals()) {
						if (!parameter.declType().isPrimitive()) {
							boolean isOk = true;
							if (parameter.declType().isArray()) { 
								isOk = false;
							}
							else {
								ClassType_c ct = (ClassType_c)parameter.declType().toClass();
								isOk = xts.isX10Array(ct);
							}
							if (!isOk)
								throw new SemanticException(parameter+
										":parameters to extern calls must be either X10 arrays or primitives.",
										parameter.position());
						}
					}
				}
			}
		}
		return result;
	}
}
