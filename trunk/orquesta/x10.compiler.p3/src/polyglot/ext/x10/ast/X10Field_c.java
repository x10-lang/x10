/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on May 23, 2005
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.Collections;

import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Field_c;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.MacroType;
import polyglot.ext.x10.types.NullableType;
import polyglot.ext.x10.types.X10ArraysMixin;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10FieldInstance;
import polyglot.ext.x10.types.X10Flags;
import polyglot.ext.x10.types.X10NamedType;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.types.ConstructorDef;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.MethodInstance;
import polyglot.types.NoMemberException;
import polyglot.types.SemanticException;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XSelf;
import x10.constraint.XTerm;
import x10.constraint.XVar;


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

	public Node typeCheck(TypeChecker tc) throws SemanticException {
		X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
		X10NodeFactory xnf = (X10NodeFactory) tc.nodeFactory();
		Type tType = target.type();
		if (tType == null)
		assert tType != null : "type of target of |" + this + "| is null";
		try {
			//Report.report(1, "X10Field_c.tpeCheck: context" + tc.context());
			Context c = tc.context();
			TypeSystem ts = tc.typeSystem();

			/*
			 * In X10, everything is an object.
			 */
			//if (! tType.isReference()) 
			//	throw new SemanticException("Cannot access field \"" + name +
			//			"\" " + (target instanceof Expr
			//					? "on an expression "
			//							: "") +
			//							"of non-reference type \"" +
			//							target.type() + "\".", target.position());
//			if (!(tType instanceof StructType))
//				throw new NoMemberException(NoMemberException.FIELD,
//						"Field \"" + name + "\" not found in type \"" +
//						tType + "\".");

			FieldInstance fi = ts.findField(tType, ts.FieldMatcher(tType, name.id()), c.currentClassDef());
			if (fi == null) {
				throw new InternalCompilerError("Cannot access field " + name +
						" on node of type " + target.getClass().getName() + ".",
						position());
			}
			X10Field_c result = this;
			Type type = fi.type();
			if (type instanceof UnknownType) {
			    throw new SemanticException();
			}
			
			Type retType = type;

			// Substitute in the actual target for this.
			Type thisType = tType;
			XConstraint rc = X10TypeMixin.realX(retType);
			if (rc != null) {
				XVar var= X10TypeMixin.selfVar(thisType);
				if (var == null) 
					var = xts.xtypeTranslator().genEQV(rc, thisType);
				XConstraint newRC = rc.substitute(var, xts.xtypeTranslator().transThis(thisType));
				retType = X10TypeMixin.xclause(retType, newRC);
				fi = fi.type(retType);
			}
			result = (X10Field_c)fieldInstance(fi).type(retType);
			result.checkConsistency(c);
			
			// Check the guard
			XConstraint where = ((X10FieldInstance) result.fieldInstance()).whereClause();
			if (where != null && ! new XConstraint_c().entails(where)) {
			    throw new SemanticException("Cannot access field.  Field guard not satisfied.", position());
			}
			
			checkFieldAccessesInDepClausesAreFinal(result, tc);
			
			//Report.report(1, "X10Field_c: typeCheck " + result+ " has type " + result.type());
			return result;
        } catch (NoMemberException e) {
            if (target instanceof Expr) {
        	Context c = tc.context();
        	NodeFactory nf = tc.nodeFactory();
        	TypeSystem ts = tc.typeSystem();
        	Position pos = position();
        	
        	// Now try 0-ary property methods.
        	try {
        	    MethodInstance mi = ts.findMethod(target.type(), ts.MethodMatcher(target.type(), name.id(), Collections.EMPTY_LIST), c.currentClassDef());
        	    if (X10Flags.toX10Flags(mi.flags()).isProperty()) {
        		Call call = nf.Call(pos, target, this.name);
        		call = call.methodInstance(mi);
        		call = (Call) call.type(mi.returnType());
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

	protected void checkFieldAccessesInDepClausesAreFinal(X10Field_c result, TypeChecker tc) 
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
