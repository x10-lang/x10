/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on May 23, 2005
 *
 * 
 */
package x10.ast;

import java.util.Collections;

import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Field_c;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.ast.Special;
import polyglot.ast.TypeNode;
import polyglot.types.Context;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.Name;
import polyglot.types.NoMemberException;
import polyglot.types.SemanticException;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.UnknownType;
import polyglot.util.ErrorInfo;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XRoot;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.types.ParameterType;
import x10.types.Subst;
import x10.types.X10ClassType;
import x10.types.X10Context;
import x10.types.X10FieldInstance;
import x10.types.X10Flags;
import x10.types.X10MemberDef;
import x10.types.X10MethodInstance;
import x10.types.X10Type;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;


/**
 * An immutable representation of an X10 Field access. It is the same as a Java
 * field access except for accesses of the field "location" for value types.
 * In this implementation such field accesses are implemented by the method call
 * x10.lang.Runtime.here().
 * 
 * @author vj May 23, 2005
 */
public class X10Field_c extends Field_c {

	/**
	 * @param pos
	 * @param target
	 * @param name
	 */
	public X10Field_c(Position pos, Receiver target, Id name) {
		super(pos, target, name);
	}

	public static class ProtoFieldAccessException extends SemanticException{
		public ProtoFieldAccessException(String s) {
			super(s);
		}
	}
	public Node typeCheck(ContextVisitor tc) throws SemanticException {
		Node n = typeCheck1(tc);
		// Keep this at the very end. This is caught by 
		// handle proto.
		if (! ((X10Context) tc.context()).inAssignment()) {
			if (n instanceof X10Field_c) {
				
			X10Type xtType = (X10Type) ((X10Field_c) n).target().type();
			if (xtType.isProto()) {
				throw new SemanticException("Cannot read fields of the proto value " 
						+ ((X10Field_c) n).target());
			}
			}
		}
		return n;
	}
	public Node typeCheck1(ContextVisitor tc) throws SemanticException {
		X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
		X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
		X10Context c = (X10Context) tc.context();
		
		Type tType = target.type();
		

		if (target instanceof TypeNode) {
			Type t = ((TypeNode) target).type();
			t = X10TypeMixin.baseType(t);
			if (t instanceof ParameterType) {
				throw new SemanticException("Cannot access a static field of a type parameter.", position());
			}
		}
		
		if (c.inSuperTypeDeclaration()) {
			Type tBase = X10TypeMixin.baseType(tType);
			if (tBase instanceof X10ClassType) {
				X10ClassType tCt = (X10ClassType) tBase;
				if (tCt.def() == c.supertypeDeclarationType()) {
					// The only fields in scope here are the ones explicitly declared here.
					for (FieldDef fd : tCt.x10Def().properties()) {
						if (fd.name().equals(name.id())) {
							X10FieldInstance fi = (X10FieldInstance) fd.asInstance();
							fi = (X10FieldInstance) ts.FieldMatcher(tType, name.id(), c).instantiate(fi);
							if (fi != null) {
								// Found!
								X10Field_c result = this;
								Type t = rightType(fi.rightType(), fi.x10Def(), target, c);
								result = (X10Field_c) result.fieldInstance(fi).type(t);
								return result;
							}
						}
					}

					throw new SemanticException("Cannot access field " + name + " of " + tCt + " in class declaration header; the field may be a member of a superclass.",
							position());
				}
			}
		}

		try {
			X10FieldInstance fi = (X10FieldInstance) ts.findField(tType, ts.FieldMatcher(tType, name.id(), c));
			if (fi == null) {
				throw new InternalCompilerError("Cannot access field " + name +
						" on node of type " + target.getClass().getName() + ".",
						position());
			}
			X10Field_c result = this;
			Type type = rightType(fi.rightType(), fi.x10Def(), target, c);
			if (type instanceof UnknownType) {
				throw new SemanticException();
			}

			Type retType = type;

			// Substitute in the actual target for this.  This is done by findField, now.
			//			Type thisType = tType;
			//			XConstraint rc = X10TypeMixin.realX(retType);
			//			if (rc != null) {
			//				XVar var= X10TypeMixin.selfVar(thisType);
			//				if (var == null) 
			//					var = ts.xtypeTranslator().genEQV(rc, thisType);
			//				XConstraint newRC = rc.substitute(var, ts.xtypeTranslator().transThis(thisType));
			//				retType = X10TypeMixin.xclause(retType, newRC);
			//				fi = fi.type(retType);
			//			}
			result = (X10Field_c)fieldInstance(fi).type(retType);
			result.checkConsistency(c);

			// Check the guard
			XConstraint guard = ((X10FieldInstance) result.fieldInstance()).guard();
			if (guard != null && ! new XConstraint_c().entails(guard, c.constraintProjection(guard))) {
				throw new SemanticException("Cannot access field.  Field guard not satisfied.", position());
			}

			checkFieldAccessesInDepClausesAreFinal(result, tc);


			if (ENABLE_PLACE_TYPES)
				result.checkFieldPlaceType(tc);

			//Report.report(1, "X10Field_c: typeCheck " + result+ " has type " + result.type());
			return result;
		} catch (NoMemberException e) {
			if (target instanceof Expr) {
				Position pos = position();

				// Now try 0-ary property methods.
				try {
					X10MethodInstance mi = ts.findMethod(target.type(), ts.MethodMatcher(target.type(), name.id(), Collections.EMPTY_LIST, c));
					if (X10Flags.toX10Flags(mi.flags()).isProperty()) {
						Call call = nf.Call(pos, target, this.name);
						call = call.methodInstance(mi);
						call = (Call) call.type(rightType(mi.rightType(), mi.x10Def(), target, c));
						return call;
					}
				}
				catch (SemanticException ex) {
				}
			}
			throw e;
		}
		catch (XFailure e) {
			throw new SemanticException(e.getMessage(), position());
		}

	}

	public static Type rightType(Type t, X10MemberDef fi, Receiver target, Context c) throws SemanticException {
		XConstraint x = X10TypeMixin.xclause(t);
		if (x != null && fi.thisVar() != null) {
			if (target instanceof Expr) {
				XVar receiver = null;
				
					X10TypeSystem ts = (X10TypeSystem) t.typeSystem();
					XTerm r = ts.xtypeTranslator().trans((XConstraint) null, target, (X10Context) c);
					if (r instanceof XVar) {
						receiver = (XVar) r;
					}
				
				
				if (receiver == null)
					receiver = XConstraint_c.genEQV();
				t = Subst.subst(t, (new XVar[] { receiver }), (new XRoot[] { fi.thisVar() }), new Type[] { }, new ParameterType[] { });
			}
		}
		return t;
	}

	private static final boolean ENABLE_PLACE_TYPES = true;

	public void checkFieldPlaceType( ContextVisitor tc) 
	throws SemanticException {
		X10Flags xFlags = X10Flags.toX10Flags(fieldInstance().flags());
		// A global field can be accessed from anywhere.
		if (xFlags.isGlobal())
			return;
		
		// A static field can be accessed from anywhere.
		if (xFlags.isStatic())
			return;

		X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
		X10Context xc = (X10Context) tc.context();
		
		Receiver target = target();
		if (! ts.isSubtype(target.type(), ts.Ref(), xc))
			return;

		if (ts.isHere(target, xc))
			return;

		throw new SemanticError("Place type error: " +
				"either field target " 
				+ target + " of type " + target.type() + " should be local "
				+ " to place " + ((X10Context) tc.context()).currentPlaceTerm()
				+ " or field " + name() + " should be global.",
				position());
	}

	protected void checkFieldAccessesInDepClausesAreFinal(X10Field_c result, ContextVisitor tc) 
	throws SemanticException {
		//		 Check that field accesses in dep clauses refer to final fields.
		X10Context xtc = (X10Context) tc.context();
		if (xtc.inDepType()) {
			FieldInstance fi = result.fieldInstance();
			if (! fi.flags().contains(Flags.FINAL))
				throw 
				new SemanticException("Field " + fi.name() 
						+ " is not final. Only final fields are permitted in a dependent clause.", 
						position());
			if ((target instanceof X10Special) &&
					((X10Special)target).kind()==X10Special.SELF) {
				// The fieldInstance must be a property.
				//Report.report(1, "X10Field_c checking " + fi  + " is a property. ");
				// The following is going to look for property propertyNames$
				// and may throw a MissingDependencyException asking for the field to be set.
				if (! (fi instanceof X10FieldInstance && ((X10FieldInstance) fi).isProperty()))
					throw new SemanticException("Field \"" + fi.name() 
							+  "\" is not a property of " + fi.container() + ". "
							+ "Only properties may appear unqualified or prefixed with self in a dependent clause."
					);
			}
		}
	}
}
