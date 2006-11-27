/*
 * Created by vj on May 23, 2005
 *
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.ast.Field_c;
import polyglot.ext.x10.types.NullableType;
import polyglot.ext.x10.types.X10FieldInstance;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.constr.C_Field;
import polyglot.ext.x10.types.constr.C_Field_c;
import polyglot.ext.x10.types.constr.C_Root;
import polyglot.ext.x10.types.constr.C_Special;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Promise;
import polyglot.main.Report;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.NoMemberException;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
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
	public X10Field_c(Position pos, Receiver target, String name) {
		super(pos, target, name);
	}

	public Node typeCheck(TypeChecker tc) throws SemanticException {
		X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
		X10NodeFactory xnf = (X10NodeFactory) tc.nodeFactory();
		/*
		if (name.equals("location") && xts.isValueType(target.type())) {
			return xnf.Here(position()).typeCheck(tc);
		}
		*/
		try {
			//Report.report(1, "X10Field_c.tpeCheck: context" + tc.context());
			Context c = tc.context();
			TypeSystem ts = tc.typeSystem();
			
			if (! target.type().isReference()) 
				throw new SemanticException("Cannot access field \"" + name +
						"\" " + (target instanceof Expr
								? "on an expression "
										: "") +
										"of non-reference type \"" +
										target.type() + "\".", target.position());
			
			FieldInstance fi = ts.findField(target.type().toReference(), name, c.currentClass());
			if (fi == null) {
				throw new InternalCompilerError("Cannot access field on node of type " +
						target.getClass().getName() + ".");
			}
			
			X10Type type = (X10Type) fi.type();
			X10Type thisType = (X10Type) target.type();
			X10Type retType = type;
			Constraint rc = type.realClause();
			if (rc != null ) {
				C_Var var=thisType.selfVar();
				if (var == null) var = rc.genEQV(thisType);
				Constraint newRC = rc.substitute(var, C_Special.This);
				retType = type.makeVariant(newRC, null);
			}
			fi = fi.type(retType);
			
			X10Field_c result = (X10Field_c)fieldInstance(fi).type(retType);  
			result.checkConsistency(c);
			
			if (! result.isTypeChecked()) return result;
			checkFieldAccessesInDepClausesAreFinal(result, tc);
			
			result = checkArrayFields(result);
			//Report.report(1, "X10Field_c: typeCheck " + result+ " has type " + result.type());
			return result;
        } catch (NoMemberException e) {
            if (e.getKind() != NoMemberException.FIELD || this.target == null)
                throw e;
            Type type = target.type();
            if (!xts.isX10Array(type))
                throw e;
            // Special fields on arrays
            if (name.equals("distribution") || name.equals("region")) {
            	Type array = xts.array();
				TypeNode typenode = xnf.CanonicalTypeNode(position(), array);
            	return this.target(xnf.Cast(position(), typenode, (Expr) target).type(array)).typeCheck(tc);
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
		if (result.name.equals("distribution") && xts.isX10Array(aType)) {
			X10ParsedClassType aType1 = (X10ParsedClassType) aType;
			X10ParsedClassType type = ((X10ParsedClassType) result.type()).makeVariant();
			//Report.report(1, "X10Field_c aType1=" + aType1 + " " + aType1.getClass());
			C_Term rank = aType1.rank();
			if (rank != null) {
				type.setRank(rank);
				//Report.report(1, "X10Field_c: set rank of .distribution to " + rank);
			}
			if (aType1.isRect()) type.setRect();
			if (aType1.isZeroBased()) type.setZeroBased();
			type.setOnePlace(aType1.onePlace());
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
		if (name.equals("region") && (xts.isX10Array(aType) || xts.isDistribution(aType)) ) {
			X10ParsedClassType aType1 = (X10ParsedClassType) (aType instanceof NullableType ? 
					((NullableType) aType).base() : aType);
			X10ParsedClassType type = ((X10ParsedClassType) result.type()).makeVariant();
			C_Term aRank = aType1.rank();
			if (aRank !=null) type.setRank(aRank);
			if (aType1.isRect()) type.setRect();
			if (aType1.isZeroBased()) type.setZeroBased();
			
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
		return target.equals(other.target()) && name.equals(other.name());
	}
}
