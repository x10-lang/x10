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
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.NullableType;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10FieldInstance;
import polyglot.ext.x10.types.X10NamedType;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.constr.C_Field;
import polyglot.ext.x10.types.constr.C_Field_c;
import polyglot.ext.x10.types.constr.C_Special;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.NoMemberException;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;


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
			if (!tType.isReference())
				throw new NoMemberException(NoMemberException.FIELD,
						"Field \"" + name + "\" not found in type \"" +
						tType + "\".");

			FieldInstance fi = ts.findField(tType.toReference(), name.id(), c.currentClassScope());
			if (fi == null) {
				throw new InternalCompilerError("Cannot access field " + name +
						" on node of type " + target.getClass().getName() + ".",
						position());
			}
			boolean inTypeElaboration = false; // tc instanceof TypeElaborator;
			X10Field_c result = this;
			Type type = fi.type();
			if (type instanceof UnknownType) {
			    throw new SemanticException();
			}
			X10Type retType = (X10Type) type;
			if (! inTypeElaboration) {
				// Fix up the type of the field instance with the name of the field.
				X10Type thisType = (X10Type) tType;
				Constraint rc = retType.realClause();
				if (rc != null ) {
					C_Var var= X10TypeMixin.selfVar(thisType);
					if (var == null) 
						var = rc.genEQV(thisType, true);
					Constraint newRC = rc.substitute(var, C_Special.This);
					retType = X10TypeMixin.makeVariant(retType, newRC, null);
					fi = fi.type(retType);
				}
				// FIXME: [IP] HACK!
				if (xts.typeEquals(fi.container(), xts.distribution()) && fi.name().equals("UNIQUE")) {
					X10ParsedClassType ud = (X10ParsedClassType) fi.type();
					ud = ud.setUniqueDist();
					fi = fi.type(ud);
					retType = (X10Type) fi.type();
				}
			}
			result = (X10Field_c)fieldInstance(fi).type(retType);  
			result.checkConsistency(c);
			
			if (! inTypeElaboration)
				checkFieldAccessesInDepClausesAreFinal(result, tc);
			
			result = checkArrayFields(result);
			//Report.report(1, "X10Field_c: typeCheck " + result+ " has type " + result.type());
			return result;
        } catch (NoMemberException e) {
            if (e.getKind() != NoMemberException.FIELD || this.target == null)
                throw e;
            if (xts.isX10Array(tType)) {
            	// Special fields on arrays
            	if (name().equals("distribution") || name().equals("region")) {
            		Type array = xts.array();
            		TypeNode typenode = xnf.CanonicalTypeNode(position(), array);
            		return this.target(xnf.Cast(position(), typenode, (Expr) target).type(array)).del().typeCheck(tc);
            	}
            } else if (xts.isValueType(tType)) {
            	 if (name().equals("location")) {
            		 // TODO
//            		 return xnf.Here(position()).typeCheck(tc);
            		 return xnf.Cast(position, xnf.CanonicalTypeNode(position(),
            				 xts.createNullableType(position(), Types.ref((X10NamedType)xts.place()))),
            				 (Expr) xnf.NullLit(position()).typeCheck(tc)).typeCheck(tc);
            	 }
            }
            throw e;
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
						+ " is not final. Only final fields are permitted in a depclause.", 
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
							+ "Only properties may appear unqualified or prefixed with self in a depclause."
							);
			}
		}
	}
	protected X10Field_c checkArrayFields(X10Field_c result) {
		X10Type aType = (X10Type) result.target.type();
		X10TypeSystem xts = (X10TypeSystem) aType.typeSystem();
		if (result.name().equals("distribution") && xts.isX10Array(aType)) {
			X10ParsedClassType aType1 = (X10ParsedClassType) aType;
			X10ParsedClassType type = ((X10ParsedClassType) result.type());
			//Report.report(1, "X10Field_c aType1=" + aType1 + " " + aType1.getClass());
			C_Var rank = aType1.rank();
			if (rank != null) {
				type = type.setRank(rank);
				//Report.report(1, "X10Field_c: set rank of .distribution to " + rank);
			}
			if (aType1.isRect()) type = type.setRect();
			if (aType1.isZeroBased()) type = type.setZeroBased();
			C_Var place = aType1.onePlace();
			if (place != null)
				type = type.setOnePlace(place);
			Constraint c = aType1.depClause(); 
			if (c != null) {
				C_Var me = c.selfVar();
				if (me !=null) {
					C_Field f = new C_Field_c(result.fieldInstance(), me);
					Constraint myC = type.depClause().copy();
					myC.setSelfVar(f);
					type = X10TypeMixin.depClauseDeref(type, myC);
				}
			}
			result = (X10Field_c) result.fieldInstance(result.fieldInstance().type(type)).type(type);
			return result;
		}
		if (name().equals("region") && (xts.isX10Array(aType) || xts.isDistribution(aType)) ) {
			X10ParsedClassType aType1 = (X10ParsedClassType) (aType instanceof NullableType ? 
					((NullableType) aType).base() : aType);
			X10ParsedClassType type = ((X10ParsedClassType) result.type());
			C_Var aRank = aType1.rank();
			if (aRank !=null) type = type.setRank(aRank);
			if (aType1.isRect()) type = type.setRect();
			if (aType1.isZeroBased()) type = type.setZeroBased();
			
			Constraint c = aType1.depClause(); 
			if (c != null) {
				C_Var me = c.selfVar();
				if (me !=null) {
					C_Field f = new C_Field_c(result.fieldInstance(), me);
					Constraint myC = type.depClause();
					myC.setSelfVar(f);
				}
			}
			result = (X10Field_c) result.fieldInstance(result.fieldInstance().type(type)).type(type);
			return result;
		}
		return result;
	}

	public boolean equals(Object o) {
		if (!(o instanceof Field_c)) return false;
		Field_c other = (Field_c) o;
		return target.equals(other.target()) && name().equals(other.name());
	}
}
