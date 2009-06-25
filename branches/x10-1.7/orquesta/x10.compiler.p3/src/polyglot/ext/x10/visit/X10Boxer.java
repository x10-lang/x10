/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.visit;


import java.util.Collections;

import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ext.x10.extension.X10Ext;
import polyglot.ext.x10.types.X10PrimitiveType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.MethodInstance;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Name;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.NodeVisitor;

/**
 * Visitor that inserts boxing and unboxing code into the AST.
 */
public class X10Boxer extends AscriptionVisitor
{
    X10TypeSystem xts;
	public X10Boxer(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
        xts = (X10TypeSystem) ts;
	}

	/**
	 * This method rewrites an AST node. We have to be careful also to
	 * provide type information with the newly created node, because the
	 * type checker ran before this pass and the node must hence be annotated.
	 * Just calling the node factory is not sufficient.
	 */
	public Expr ascribe(Expr e, Type toType) {
		Type fromType = e.type();
		Expr ret_notype;

		if (toType == null) {
			return e;
		}

		Position p = e.position();
		
		// If can coerce from fromType to toType, but is not a subtype, then insert a coercion.
		if (ts.isImplicitCastValid(fromType, toType) && ! ts.isSubtype(fromType, toType)) {
			// Can convert if there is a static method toType.make(fromType)
		        MethodInstance mi = null;

		        try {
		            mi = ts.findMethod(toType, ts.MethodMatcher(toType, Name.make("$convert"), Collections.singletonList(fromType)), (ClassDef) null);
		            if (mi.flags().isStatic() && X10TypeMixin.baseType(mi.returnType()).isSubtype(X10TypeMixin.baseType(toType)))
		                ;
		            else
		                mi = null;
		        }
		        catch (SemanticException ex) {
		        }

		        if (mi == null) {
		            try {
		                mi = ts.findMethod(toType, ts.MethodMatcher(toType, Name.make("make"), Collections.singletonList(fromType)), (ClassDef) null);
		                if (mi.flags().isStatic() && X10TypeMixin.baseType(mi.returnType()).isSubtype(X10TypeMixin.baseType(toType)))
		                    ;
		                else
		                    mi = null;
		            }
		            catch (SemanticException ex) {
		            }
		        }
			
			if (mi != null) {
			    if (mi.flags().isStatic() && mi.returnType().isSubtype(toType)) {
				Call c = nf.Call(p, nf.CanonicalTypeNode(p, toType), nf.Id(p, mi.name()), e);
				c = c.methodInstance(mi);
				c = (Call) c.type(mi.returnType());
				return c;
			    }
			}
		}
		
		// This avoids that the int value in code like "String" + 2
		// is boxed. The toType of the IntLit node that represents "2"
		// is actually a ParsedClassType for java.lang.String.
		// While the boxing is OK in case "String" + 2 , it would break
		// the expression "String" + 2 + 3.

		// A better way to fix this problem would be to modify the type system and
		// annotation such that it understand that operator "+" for Strings
		// and primitives is fine and would not ask for a String here.
		// see line 405 in polyglot.ext.jl.as.Binary_c
		// TODO  I leave this up to whoever implements the type system/checker

		boolean is_String_type = ts.typeEquals(toType, ts.String());
		// Insert a cast.  Translation of the cast will insert the
		// correct boxing/unboxing code.

		if (!is_String_type && fromType.isPrimitive() && toType.isReference()) {
			ret_notype = nf.Cast(p, nf.CanonicalTypeNode(p, toType), e).type(toType);
			return ret_notype.type(toType);
		}
		
		return e;
	}

//	public Node override(Node parent, Node n) {
//		// FIXME: [IP] HACK: Leave printf alone
//		if (n instanceof Call) {
//			Call call_n = (Call) n;
//			Name m_name = call_n.name().id();
//			Type target_t = call_n.target().type();
//			if (m_name.equals(Name.make("printf")) && target_t.isClass() &&
//					target_t.toClass().fullName().equals(QName.make("java.io.PrintStream"))) {
//				return n;
//			}
//		}
//		return super.override(parent, n);
//	}

	public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
		n = super.leaveCall(old, n, v);

		if (n.ext() instanceof X10Ext) {
		    return ((X10Ext) n.ext()).rewrite((X10TypeSystem) typeSystem(),
		                                      nodeFactory(),
		                                      job.extensionInfo());
		}

		return n;
	}
}
