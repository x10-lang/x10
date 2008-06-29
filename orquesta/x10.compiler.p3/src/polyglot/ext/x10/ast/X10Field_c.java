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

import polyglot.ast.Expr;
import polyglot.ast.Field_c;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.NullableType;
import polyglot.ext.x10.types.X10ArraysMixin;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10FieldInstance;
import polyglot.ext.x10.types.X10NamedType;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.ConstructorDef;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
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
		assert tType != null : "target " + target + " type is null";
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
			if (!(tType instanceof StructType))
				throw new NoMemberException(NoMemberException.FIELD,
						"Field \"" + name + "\" not found in type \"" +
						tType + "\".");

			FieldInstance fi = ts.findField((StructType) tType, name.id(), c.currentClassDef());
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
			// FIXME: [IP] HACK!
			if (xts.typeEquals(fi.container(), xts.distribution()) && fi.name().equals("UNIQUE")) {
				Type ud = fi.type();
				ud = X10ArraysMixin.setUniqueDist(ud);
				fi = fi.type(ud);
				retType = fi.type();
			}
			result = (X10Field_c)fieldInstance(fi).type(retType);  
			result.checkConsistency(c);
			
			checkFieldAccessesInDepClausesAreFinal(result, tc);
			
			result = checkArrayFields(result);
			//Report.report(1, "X10Field_c: typeCheck " + result+ " has type " + result.type());
			return result;
        } catch (NoMemberException e) {
            if (e.getKind() != NoMemberException.FIELD || this.target == null)
                throw e;
// ### [NN] should be handled by the new 1.7 runtime
//            if (xts.isX10Array(tType)) {
//            	// Special fields on arrays
//            	if (name().equals(X10TypeSystem.DIST_FIELD) || name().equals(X10TypeSystem.REGION_FIELD)) {
//            		Type array = xts.array();
//            		TypeNode typenode = xnf.CanonicalTypeNode(position(), array);
//            		return this.target(xnf.Cast(position(), typenode, (Expr) target).type(array)).del().typeCheck(tc);
//            	}
//            } else if (xts.isValueType(tType)) {
//            	 if (name().equals(X10TypeSystem.LOCATION_FIELD)) {
//            		 // TODO
////            		 return xnf.Here(position()).typeCheck(tc);
//            		 return xnf.Cast(position, xnf.CanonicalTypeNode(position(),
//            				 xts.boxOf(position(), Types.ref((X10NamedType)xts.place()))),
//            				 (Expr) xnf.NullLit(position()).typeCheck(tc)).typeCheck(tc);
//            	 }
//            }
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
	protected X10Field_c checkArrayFields(X10Field_c result) {
		Type aType = result.target.type();
		X10TypeSystem xts = (X10TypeSystem) aType.typeSystem();
		if (result.nameString().equals("distribution") && xts.isX10Array(aType)) {
			Type aType1 = aType;
			Type type = result.type();
			//Report.report(1, "X10Field_c aType1=" + aType1 + " " + aType1.getClass());
			XTerm rank = X10ArraysMixin.rank(aType1);
			if (rank != null) {
				type = X10ArraysMixin.setRank(type, rank);
				//Report.report(1, "X10Field_c: set rank of .distribution to " + rank);
			}
			if (X10ArraysMixin.isRect(aType1)) type = X10ArraysMixin.setRect(type);
			if (X10ArraysMixin.isZeroBased(aType1)) type = X10ArraysMixin.setZeroBased(type);
			XTerm place = X10ArraysMixin.onePlace(aType1);
			if (place != null)
				type = X10ArraysMixin.setOnePlace(type, place);

			// Add the constraint on the target into the type.
	    		XConstraint c = X10TypeMixin.xclause(aType1);
			if (c != null) {
				try {
					XConstraint myC = X10TypeMixin.xclause(type).copy();
					XVar outer = myC.genEQV();
					myC.addIn(c.copy().substitute(outer, XSelf.Self));
					XVar f = (XVar) xts.xtypeTranslator().trans(outer, result.fieldInstance());
					myC.addSelfBinding(f);
					type = X10TypeMixin.xclause(type, myC);
				}
				catch (SemanticException e) {
				}
				catch (XFailure e) {
				}
			}
			
			result = (X10Field_c) result.fieldInstance(result.fieldInstance().type(type)).type(type);
			return result;
		}
		if (nameString().equals(X10TypeSystem.REGION_FIELD) && (xts.isX10Array(aType) || xts.isDistribution(aType)) ) {
			Type aType1 = (aType instanceof NullableType ? 
					((NullableType) aType).base() : aType);
			Type type = result.type();
			XTerm aRank = X10ArraysMixin.rank(aType1);
			if (aRank !=null) type = X10ArraysMixin.setRank(type, aRank);
			if (X10ArraysMixin.isRect(aType1)) type = X10ArraysMixin.setRect(type);
			if (X10ArraysMixin.isZeroBased(aType1)) type = X10ArraysMixin.setZeroBased(type);
			
			// Add the constraint on the target into the type.
	    		XConstraint c = X10TypeMixin.xclause(aType1);
			if (c != null) {
				try {
					XConstraint myC = X10TypeMixin.xclause(type).copy();
					XVar outer = myC.genEQV();
					myC.addIn(c.copy().substitute(outer, XSelf.Self));
					XVar f = (XVar) xts.xtypeTranslator().trans(outer, result.fieldInstance());
					myC.addSelfBinding(f);
					type = X10TypeMixin.xclause(type, myC);
				}
				catch (SemanticException e) {
				}
				catch (XFailure e) {
				}
			}
			
			result = (X10Field_c) result.fieldInstance(result.fieldInstance().type(type)).type(type);
			return result;
		}
		return result;
	}
//
//	public boolean equals(Object o) {
//		if (!(o instanceof Field_c)) return false;
//		Field_c other = (Field_c) o;
//		return target.equals(other.target()) && name().equals(other.name());
//	}
}
