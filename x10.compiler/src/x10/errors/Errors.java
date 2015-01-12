/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.errors;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;

import polyglot.ast.Binary;
import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.ast.Binary.Operator;
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.ConstructorInstance;
import polyglot.types.ContainerType;
import polyglot.types.Context;
import polyglot.types.Def;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.FunctionDef;
import polyglot.types.MethodDef;
import polyglot.types.Name;
import polyglot.types.ProcedureDef;
import polyglot.types.ProcedureInstance;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.VarInstance;
import polyglot.types.MemberDef;
import polyglot.types.TypeObject;
import polyglot.types.ClassType;
import polyglot.types.TypeSystem_c.ConstructorMatcher;
import polyglot.types.TypeSystem_c.MethodMatcher;
import polyglot.util.CodedErrorInfo;
import polyglot.util.ErrorInfo;
import polyglot.util.Position;
import polyglot.util.CollectionUtil; import polyglot.visit.CFGBuildError;
import polyglot.visit.ContextVisitor;
import x10.util.CollectionFactory;
import x10.ExtensionInfo;
import x10.ExtensionInfo.X10Scheduler.X10Job;
import x10.ast.DepParameterExpr;
import x10.ast.SettableAssign;
import x10.ast.X10ClassDecl;
import x10.ast.X10FieldDecl;
import x10.constraint.XConstraint;
import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.types.ConstrainedType;
import x10.types.MacroType;
import x10.types.ParameterType;
import x10.types.TypeDef;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10ConstructorDef;
import x10.types.X10FieldInstance;
import x10.types.MethodInstance;
import x10.types.X10MethodDef;
import x10.types.X10ProcedureInstance;
import x10.types.checker.Converter;
import x10.types.checker.Converter.ConversionType;
import x10.types.constraints.CConstraint;
import x10.types.constraints.TypeConstraint;

/**
 * Start at centralizing Error messages. Goal is to support standardization of error messages for
 * internationalization, to make unit tests more accurate, and to support better error-handling
 * inside compiler.
 *
 * @author vj 2010/02/06
 *
 */
public class Errors {

	public static void issue(Job job, SemanticException e) {
		issue(job, e, null);
	}
	public static void issue(Job job, SemanticException e, Node n) {
		ExtensionInfo ei = (ExtensionInfo) job.extensionInfo();
		issue(ei, e, getPosition(n));
		((X10Job) job).setReportedErrors(true);
	}

	public static void issue(polyglot.frontend.ExtensionInfo extInfo, SemanticException e, Position p) {
        issue((ExtensionInfo)extInfo,e,p);
    }
	public static void issue(ExtensionInfo extInfo, SemanticException e, Position p) {
		if (e.getCause() == null && e.position() == null && p != null)
			e = new SemanticException(e.getMessage(), p);
		boolean newP = extInfo.errorSet().add(e);
		if (newP && e.getMessage() != null) {

			Position position = e.position();

			if (position == null && p != null) {
				position = p;
			}
			
			ErrorInfo errorInfo = new CodedErrorInfo(ErrorInfo.SEMANTIC_ERROR,
			 					e.getMessage(), position, e.attributes());
			extInfo.compiler().errorQueue().enqueue(errorInfo);
		}
	}

	private static Position getPosition(Node n) {
		return n == null ? null : n.position();
	}

	public static interface DepTypeException {}
	public static interface ConversionException {}
    public static class EqualByTypeAndPosException extends SemanticException {
        private static final long serialVersionUID = -5707886878408735177L;
        public EqualByTypeAndPosException(String m, Position position) {
            super(m,position);
        }
		public final boolean equals(Object o) {
			if (o==null || o.getClass()!=this.getClass() )
				return false;
			return posEquals(((EqualByTypeAndPosException)o).position(),position());
		}
        public final void issue(Job job) {
            Errors.issue(job, this);
        }
    }

    public static class ClassCannotHaveSuperInterface extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -807398966257098427L;
		public ClassCannotHaveSuperInterface(ClassType type, Position p) {
			super("Class cannot have a superinterface." +
					"\n\t Class: " + type, p);
		}
	}
    public static class SuperInterfaceNotInterface extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 366164325571822130L;
		public SuperInterfaceNotInterface(Type t, ClassType type, Position p) {
			super("Superinterface of class is not an interface." +
					"\n\t Superinterface: " + t +
					"\n\t Class: " + type, p);
		}
	}
    public static class CannotHaveSuperclass extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 260938668864591547L;
		public CannotHaveSuperclass(ClassType type, Position p) {
			super("Class cannot have a superclass." + 
					"\n\t Class: " + type, p);
		}
	}
    public static class ExtendedFinalClass extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 6885425460129173909L;
		public ExtendedFinalClass(ClassType type, Position p) {
			super("Cannot extend final class." +
					"\n\t Final class: " + type.superClass(), p);
		}
	}
    public static class ExtendedNonClass extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 5529507748253686721L;
		public ExtendedNonClass(ClassType type, Position p) {
			super("Cannot extend non-class." +
					"\n\t Non-class: " + type.superClass(), p);
		}
	}
    public static class InnerDeclaredStatic extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 1937596402969387888L;
		public InnerDeclaredStatic(ClassType type, ClassType container, Position p) {
			super("Inner and local classes cannot declare static member classes."
	              + "\n\t Member: " + type.toString()
	              + "\n\t Container: " + container.toString(), p);
		}
	}
    public static class InnerDeclaredInterface extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -7633854120689783816L;
		public InnerDeclaredInterface(ClassType type, ClassType container, Position p) {
			super("Inner and local classes cannot declare member interfaces."
			      + "\n\t Interface: " + type.toString()
			      + "\n\t Container: " + container.toString(), p);
		}
	}
    public static class SameNameLocal extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -8305278484313725894L;
		public SameNameLocal(ClassType type, Position p) {
			super("Cannot declare local class within the same " +
                    "method, constructor or initializer as another " +
                    "local class of the same name." +
                    "\n\t Local class: " + type, p);
		}
	}

    public static class SameNameClass extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 189016834316504798L;
		public SameNameClass(ClassType type, Position p) {
			super("Cannot declare member class inside class with the same name." +
					"\n\t Class: " + type.fullName(), p);
		}
    }
	public static class DuplicateMember extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -729466352124876376L;
		public DuplicateMember(TypeObject def) {
			super("Duplicate member." + 
					"\n\t Member: " + def, def.errorPosition());
		}
	}

	public static class CannotAssign extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -4243637083971033996L;
		CannotAssign(Expr expr, Type targetType, Position pos) {
			super("Cannot assign expression to target."
					+ "\n\t Expression: " + expr
					+ "\n\t Expected type: " + targetType
					+ "\n\t Found type: " + expr.type()
					, pos);
		}
		CannotAssign(Expr expr, Type bType, Type btargetType, Position pos) {
            super("Cannot assign expression to target; base types are incompatible."
                    + "\n\t Expression: " + expr
                    + "\n\t Expected base type: " + btargetType
                    + "\n\t Found base type: " + bType
                    , pos);
        }
		CannotAssign(Expr expr, Type type, XConstraint con, Position pos) {
            super("Cannot assign expression to target; constraints not satisfied."
                    + "\n\t Expression: " + expr
                    + "\n\t Type: " + type
                    + "\n\t Unsatisfied constraints: " + con
                    , pos);
        }
		public static CannotAssign make(Expr expr, Type targetType, ContextVisitor tc, Position pos) {
		    Type type = expr.type(), bType = Types.baseType(type), bTargetType = Types.baseType(targetType);
		    TypeSystem ts = tc.typeSystem();
		    if (! ts.isSubtype(bType, bTargetType, tc.context()))
		        return new CannotAssign(expr, bType, bTargetType, pos);
		    // base types are compatible, constraints are not.
		    CConstraint 
		    c = Types.xclause(type), 
		    d = Types.xclause(targetType);
		    XConstraint residue = c == null ? d : c.residue(d);
		    
		    
		    return new CannotAssign(expr, type, residue, pos);
		}
	}
	public static class NewIncompatibleType extends EqualByTypeAndPosException {
        private static final long serialVersionUID = 5076152155527158732L;
	    NewIncompatibleType(Expr expr, Type bType, XConstraint con, Position pos) {
            super("Return type of resolved constructor does not satisfy given constraints."
                    + "\n\t Expression: " + expr
                    + "\n\t Type: " + bType
                    + "\n\t Unsatisfied constraints: " + con
                    , pos);
        }
	    NewIncompatibleType(Expr expr, Type bType, Type target, Position pos) {
            super("Return type of resolved constructor has incompatible base"
                    + "\n\t Expression: " + expr
                    + "\n\t Type: " + bType
                    + "\n\t Expected type: " + target
                    , pos);
        }
        public static NewIncompatibleType make(Expr expr, Type targetType, ContextVisitor tc, Position pos) {
            Type type = expr.type(), bType = Types.baseType(type), bTargetType = Types.baseType(targetType);
            TypeSystem ts = tc.typeSystem();
           if (! ts.isSubtype(bType, bTargetType, tc.context()))
                return new NewIncompatibleType(expr, bType, bTargetType, pos);
            // base types are compatible, constraints are not.
            CConstraint 
            c = Types.xclause(type), 
            d = Types.xclause(targetType);
            XConstraint residue = c.residue(d);
            
            
            return new NewIncompatibleType(expr, type, residue, pos);
        }
    }
	public static class FieldInitTypeWrong extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 4778277210134359519L;
		public FieldInitTypeWrong(Expr expr, Type targetType, Position pos) {
			super("The type of the field initializer is not a subtype of the field type."
					+ "\n\t Expression: " + expr
					+ "\n\t Expected type: " + targetType
					+ "\n\t Found type: " + expr.type()
					, pos);
		}
	}
	public static class IncompatibleReturnType extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -6220163900080278288L;
		public IncompatibleReturnType(MethodInstance mi, MethodInstance mj) {
			super("Method A in container A cannot override method B in container B"
					+ "; attempting to use incompatible return type."
					+ "\n\t Method A: " + mi.signature()
					+ "\n\t Container A: " + mi.container()
					+ "\n\t Method B: " + mj.signature()
					+ "\n\t Container B: " + mj.container()
					+ "\n\t Expected Type: " + mj.returnType()
					+ "\n\t Found Type: " + mi.returnType(), mi.errorPosition());
			}
	}

	public static class InvalidParameter extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -1351185257724314440L;
		public InvalidParameter(int i, X10ProcedureInstance<?> me, Type from, Type to, Context c, Position pos) {
		    super("Parameter " + i + " does not have the right type." +
		          "\n\t Expected type: " + to + 
		          "\n\t Found type: " + from
		          +"\n\t (" + me + ")", pos);
		}


		InvalidParameter(int i, Type actual, Type formal, X10ProcedureInstance<?> me, Position pos) {
		    super("Parameter " + i + " does not have the expected base type."
		          + "\n\t Formal base type: " + formal
		          + "\n\t Actual base type: " + actual
		          +"\n\t (" + me + ")"
		          , pos);
		}
		InvalidParameter(int i, Type formal, XConstraint con, X10ProcedureInstance<?> me, Position pos) {
		    super("Parameter " + i + " does not have the expected constraints."
		          + "\n\t Formal type: " + formal
		          + "\n\t Unsatisfied constraints: " + con
		          +"\n\t (" + me + ")"
		          , pos);
		}
		public static InvalidParameter make(int i, X10ProcedureInstance<?> me, Type actual, Type formal, Context cxt, Position pos) {
		    Type actualBase = Types.baseType(actual), formalBase = Types.baseType(formal);
		    TypeSystem ts = cxt.typeSystem();
		    if (! ts.isSubtype(actualBase, formalBase,  cxt))
		        return new InvalidParameter(i, actual, formal, me, pos);
		    // base types are compatible, constraints are not.
		    CConstraint 
		    c = Types.xclause(actual), 
		    d = Types.xclause(formal);
		    c.addIn(cxt.currentConstraint());
		    XConstraint residue = c.residue(d);
		    return new InvalidParameter(i, formal, residue, me, pos);
		}

	}

	public static class NoAssignmentInDepType extends EqualByTypeAndPosException implements DepTypeException {
		private static final long serialVersionUID = 8343234065357158485L;
		public NoAssignmentInDepType(FieldAssign f, Position pos) {
			super("Assignment may not appear in a constrained type:" +
					"\n\t Error: " + f, pos);
		}
	}

	public static class PlaceTypeException extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -8998234559836889448L;

		public PlaceTypeException(String s, Position p) {
			super(s,p);
		}
	}
	public static class PlaceTypeErrorFieldShouldBeGlobal extends  PlaceTypeException {
		private static final long serialVersionUID = -7491337042919050786L;
		public PlaceTypeErrorFieldShouldBeGlobal(Field f, Position pos) {
			super("Place type error: Field should be global." +
					"\n\t Field: " + f, pos);
		}
	}
	public static class PlaceTypeErrorFieldShouldBeLocalOrGlobal extends PlaceTypeException {
		private static final long serialVersionUID = 8839433155480902083L;
		public PlaceTypeErrorFieldShouldBeLocalOrGlobal(Field f, XTerm place, XTerm targetPlace, Position pos) {
			super("Place type error: either field target should be local or field should be global."
					+ "\n\t Field: " + f.name()
					+ "\n\t Field target: " + f.target()
					+ (targetPlace != null ? "\n\t Field target place: "+ targetPlace : "" )
					+ "\n\t Current place: " + place,
					pos);
		}
	}

	public static class PlaceTypeErrorMethodShouldBeGlobal extends PlaceTypeException {
		private static final long serialVersionUID = -657551989521522263L;

		public PlaceTypeErrorMethodShouldBeGlobal(Call c, Position pos) {
			super("Place type error: Method should be global. (Called within a global method.) " +
					"\n\t Method: " + c.name(), pos);
		}
	}
	public static class PlaceTypeErrorMethodShouldBeLocalOrGlobal extends  PlaceTypeException {
		private static final long serialVersionUID = 5212483087766572622L;

		public PlaceTypeErrorMethodShouldBeLocalOrGlobal(Call c, XTerm place, XTerm targetPlace, Position pos) {
			super("Place type error: either method target should be local or method should be global."
					+ "\n\t Method target: " + c.target()
					+ "\n\t Method target place: " + targetPlace
					+ "\n\t Current place: " + place
					+ "\n\t Method: " + c.name(), pos);
		}
	}
	static boolean posEquals(Position a, Position b) {
		return a.line()==b.line() && a.column()==b.column();
	}
	public static class DependentClauseErrorFieldMustBeFinal extends EqualByTypeAndPosException implements DepTypeException {
		private static final long serialVersionUID = 8737323529719693415L;
		public DependentClauseErrorFieldMustBeFinal(X10FieldInstance fi, Position pos) {
			super("Only val fields are permitted in constraints."
					+ "\n\t Field: " + fi, pos);
		}
	}

	public static class DependentClauseErrorSelfMayAccessOnlyProperties extends EqualByTypeAndPosException implements DepTypeException {
		private static final long serialVersionUID = 8019315512496243771L;
		public DependentClauseErrorSelfMayAccessOnlyProperties(FieldInstance fi, Position pos) {
			super("Only properties may be prefixed with self in a constraint."
					+ "\n\t Field: " + fi.name()
					+ "\n\t Container: " + fi.container(), pos);
		}
	}

	public static class DependentClauseIsInconsistent extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = -737687218058693221L;
	    public DependentClauseIsInconsistent(String entity, DepParameterExpr e) {
	        super("The "+entity+"'s constraint is inconsistent.",
	              e == null ? null : e.position());
	    }
	}

	public static class CannotAccessStaticFieldOfTypeParameter extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -8016592273145691613L;
		public CannotAccessStaticFieldOfTypeParameter(Type t,Position pos) {
			super("Cannot access static field of a type parameter"
					+ "\n\t Type Parameter: " + t, pos);
		}
	}

	public static class CannotConvertToType extends EqualByTypeAndPosException implements ConversionException {
		private static final long serialVersionUID = 5580836853775144578L;

		public CannotConvertToType(Type fromType,  Type toType, Position pos) {
			super("Cannot perform type conversion."
					+ "\n\t From type: "  + fromType
					+ "\n\t To type: " + toType,
					pos);
		}
	}

	public static class CannotConvertExprToType extends EqualByTypeAndPosException implements ConversionException {
		private static final long serialVersionUID = -3353656656440601443L;
		public CannotConvertExprToType(Expr expr, Converter.ConversionType conversion,  Type toType, Position pos) {
			super("Cannot "
					+ (conversion == ConversionType.UNKNOWN_CONVERSION ? "cast" : "implicitly convert")
					+ " expression to type."
					+ "\n\t Expression: "  + expr
					+ "\n\t Expression type: "  + expr.type()
					+ "\n\t To type: " + toType,
					pos);
		}
	}

	public static class InconsistentReturnType extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 5928425853367539997L;

		public <PI extends X10ProcedureInstance<?>> InconsistentReturnType(Type t, PI me) {
			super("Inconsistent return type."
					+ "\n\t ReturnType: " + t
					+ "\n\t Invocation: " + me,
                    me.errorPosition());
		}
	}
	public static class CannotAssignToProperty extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 3461823901187721248L;
		public CannotAssignToProperty(X10FieldInstance f, Position p) {
			super("Must use property(...) to assign to a property."
					+ "\n\t Property: " + f.name(),
					p);
		}
	}
	public static class TernaryConditionalTypeUndetermined extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -3724235800269996470L;
		public TernaryConditionalTypeUndetermined(Type t1, Type t2, Position p) {
			super("Could not determine type of ternary conditional expression. "
					+ "Cannot assign expression of type T1 to T2 or vice versa."
					+ "\n\t T1: " + t1
					+ "\n\t T2: " + t2,
					p);
		}
	}
	public static class TypedefMustBeStatic extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = -1088534868188898121L;
	    public TypedefMustBeStatic(MacroType mt, Position pos) {
	        super("Member type definitions must be static.  This is a limitation of the current implementation." +
	        		"\n\t Type definition: " + mt, pos);
	    }
	}
	public static class StructMustBeStatic extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 1450037642852701286L;
		public StructMustBeStatic(X10ClassDecl cd) {
			super("Nested structs must be declared static.  This is a limitation of the current implementation."
					+ "\n\t Struct: " + cd.name(),
					cd.position());
		}
	}
	public static class InstanceofError extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -3026696944876868780L;
		public InstanceofError(Type left, Type right, Position pos) {
			super("An instance of the left type cannot possibly be an instance of the right type."
					+ "\n\t Left type: " + left
					+ "\n\t Right type: " + right,
					pos);
		}
	}
	public static class VarMustBeFinalInTypeDef extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -1828548933164244089L;
		public VarMustBeFinalInTypeDef(String name, Position pos) {
			super("Variable must be immutable (val) in type def."
					+ "\n\t Variable: " + name,
					pos);
		}
	}
	public static class VarMustBeAccessibleInTypeDef extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -1984266198367743732L;
		public VarMustBeAccessibleInTypeDef(VarInstance<?> var, Position pos) {
			super("Variable must be accessible in type."
					+ "\n\t Variable: " + var,
					pos);
		}
	}

	public static class CannotExtendTwoInstancesSameInterfaceLimitation extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -1984266198367743732L;
		public CannotExtendTwoInstancesSameInterfaceLimitation(Type t1, Type t2, Position pos) {
			super("LIMITATION: Cannot extend different instantiations of the same type."
					+ "\n\t Type 1: " + t1
					+ "\n\t Type 2: " + t2,
					pos);
		}
	}

	public static class TypeIsNotASubtypeOfTypeBound extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = 5054688602611389407L;
	    public TypeIsNotASubtypeOfTypeBound(Type type, Type hasType, Position pos) {
	        super("Computed type is not a subtype of type bound." +
	              "\n\t Computed Type: " + type +
	              "\n\t Type Bound: " + hasType, pos);
	    }
	}

	public static class TypeIsMissingParameters extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 1254563921501323608L;
		public TypeIsMissingParameters(Type t1, List<ParameterType> t2, Position pos) {
			super("Type is missing parameters."
					+ "\n\t Type: " + t1
					+ "\n\t Expected parameters: " + t2,
					pos);
		}
	}

	public static class CannotAssignToElement extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -9118489907802078734L;
		public CannotAssignToElement(String leftString, boolean arrayP, Expr right, Type t, Position pos, SemanticException cause) {
			super(toMessage(leftString, arrayP, right, t, cause), pos);
		}
		private static String toMessage(String leftString, boolean arrayP, Expr right, Type t, SemanticException cause) {
			if (cause.getMessage() == null)
				return null;
			return "Cannot assign expression to " + (arrayP ? "array " : "rail ") + "element of given type."
					+ "\n\t Expression: " + right
					+ "\n\t Type: " + right.type()
					+ "\n\t " + (arrayP ? "Array ": "Rail ") +"element: "  + leftString
					+ "\n\t Type: " + t
					+ "\n\t Cause: " + cause.getMessage();
		}
	}
	public static class CannotPerformAssignmentOperation extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = 2577635917256629928L;
	    public CannotPerformAssignmentOperation(String leftString, boolean arrayP, String op, Expr right, Type t, Position pos, SemanticException error) {
	        super("Cannot perform assignment expression to " + (arrayP ? "array " : "rail ") + "element of given type."
	              + "\n\t Expression: " + right
	              + "\n\t Operation: " + op
	              + "\n\t Type: " + right.type()
	              + "\n\t " + (arrayP ? "Array ": "Rail ") +"element: "  + leftString
	              + "\n\t Type: " + t
	              + "\n\t Because of: " + error.getMessage(),
	              pos);
	    }
	}
	public static class AssignSetMethodCantBeStatic extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 2179749179921672516L;
		public AssignSetMethodCantBeStatic(MethodInstance mi, Expr array,  Position pos) {
			super("The set method for array cannot be static."
					+ "\n\t Array: "  + array
					+ "\n\t Method: " + mi,
					pos);
		}
	}

	public static class ConstructorReturnTypeNotSubtypeOfContainer extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = 8107418837802223220L;
	    public ConstructorReturnTypeNotSubtypeOfContainer(Type retType, X10ClassType container, Position pos) {
	        super("Constructor return type is not a subtype of the containing class"
	                + "\n\t Type: " + retType
	                + "\n\t Container: " + container,
	                pos);
	    }
	}

	public static class ConstructorReturnTypeNotEntailed extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -4705861378590877043L;
		public ConstructorReturnTypeNotEntailed(CConstraint known, CConstraint ret,  Position pos) {
			super("Instances created by this constructor do not satisfy return type"
					+ "\n\t Constraint satisfied: "  + known
					+ "\n\t Constraint required: " + ret,
					pos);
		}
	}

	public static class InconsistentInvariant extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = 243905319528026232L;
	    public InconsistentInvariant(X10ClassDef cd,  Position pos) {
	        super("Class invariant is inconsistent."
	              + "\n\t Invariant: "  + cd.classInvariant()
	              + "\n\t Class: " + cd,
	              pos);
	    }
	}
	public static class ThisNotPermittedInConstructorFormals extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = -7998660806293584830L;
	    public ThisNotPermittedInConstructorFormals(List<Formal> formals, Position pos) {
	        super("This or super cannot be used (implicitly or explicitly) in a constructor formal type."
	              + "\n\t Formals: "  + formals,
	              pos);
	    }
	}
	public static class ThisNotPermittedInConstructorReturnType extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = 751592738631688909L;
	    public ThisNotPermittedInConstructorReturnType(TypeNode type, Position pos) {
	        super("This or super cannot be used (implicitly or explicitly) in a constructor return type."
	              + "\n\t Type: "  + type,
	              pos);
	    }
	}
	public static class ThisNotPermittedInPropertyInitializer extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = -7457369484612652452L;
	    public ThisNotPermittedInPropertyInitializer(Expr init, Position pos) {
	        super("This or super cannot be used (implicitly or explicitly) in a property initializer."
	              + "\n\t Expr: "  + init,
	              pos);
	    }
	}
	public static class MethodOrStaticConstructorNotFound extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = -6230289868576516608L;
	    public MethodOrStaticConstructorNotFound(MethodMatcher mm,  Position pos) {
	        super("Method or static constructor not found for given call."
	              + "\n\t Call: "  + mm,
	              pos);
	        
	        Map<String, Object> map = CollectionFactory.newHashMap();
		    map.put(CodedErrorInfo.ERROR_CODE_KEY, CodedErrorInfo.ERROR_CODE_METHOD_NOT_FOUND);
		    map.put("METHOD", mm.name().toString());
		    map.put("ARGUMENTS", mm.argumentString());
		    setAttributes(map);
	    }
	    public MethodOrStaticConstructorNotFound(ConstructorMatcher mm,  Position pos) {
	        super("Method or static constructor not found for given call"
	                + "\n\t Call: "  + mm,
	                pos);
	    }
	}
	public static class AmbiguousOperator extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = 2747145999189438964L;
	    private static String matchingMethods(List<MethodInstance> mis) {
	        StringBuilder sb = new StringBuilder();
	        for (MethodInstance mi : mis) sb.append("\t").append(mi).append("\n");
	        return sb.toString();
	    }
	    public AmbiguousOperator(Unary.Operator op, List<MethodInstance> mis,  Position pos) {
	        super("Ambiguous operator because it matches more than one operator definition." +
	        		"\n\t Operator: " + op + 
	        		"\n\t Matching definitions: " + matchingMethods(mis), pos);
	    }
	    public AmbiguousOperator(Binary.Operator op, List<MethodInstance> mis,  Position pos) {
	        super("Ambiguous operator because it matches more than one operator definition." +
	        		"\n\t Operator: " + op + 
	        		"\n\t Matching definitions: " + matchingMethods(mis), pos);
	    }
	}
	public static class OnlyValMayHaveHasType extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -4705861378590877043L;
		public OnlyValMayHaveHasType(X10FieldDecl field) {
			super("Only val fields may have a has type."
					+ "\n\t Field: "  + field.name()
					+ "\n\t Field has type: " + field.hasType(),
					field.position());
		}
	}
	public static class CannotFindIndexType extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -8300517312728182918L;
		public CannotFindIndexType(Type type, Position position) {
			super("Cannot determine index type for given type."
					+ "\n\t Type: "  + type,
					position);
		}
	}

	public static class CannotTranslateStaticField extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -950551311327307252L;
		public CannotTranslateStaticField(Type type, Position position) {
			super("Cannot translate a static field of non-class type"
					+ "\n\t Type: "  + type,
					position);
		}
	}

	public static class CannotGenerateCast extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 8124533664575933282L;
		public CannotGenerateCast(Node n, Position position) {
			super("Place type error with this expression. Cannot generate dynamic cast." +
					"\n\t Expression: " + n, position);
		}
	}

	public static class ClassMustHaveClassSupertype extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = -7826831387240378409L;
	    public ClassMustHaveClassSupertype(Ref<? extends Type> superType, ClassDef type, Position pos) {
	        super("Super cannot be the superclass for type; " + 
	        		"a class must subclass a class." +
	        		"\n\t Super: " + superType +
	        		"\n\t Type: " + type, pos);
	    }
	}
	public static class NoCollectingFinishFound extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 9107601200096892236L;
		public NoCollectingFinishFound(String offer, Position position) {
			super("Cannot find enclosing collecting finish for offer statement."
					+ "\n\t Offer: "  + offer,
					position);
		}
	}
	public static class OfferDoesNotMatchCollectingFinishType extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -4277756733682836855L;
		public OfferDoesNotMatchCollectingFinishType(Type eType, Type fType, Position position) {
			super("The type of the offer expression does not match the type of the collecting finish."
					+ "\n\t Offer expression type: "  + eType
					+ "\n\t Collecting finish type: "  + fType,
					position);
		}
	}
	public static class IsNotReducible extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 6604309927252841516L;
		public IsNotReducible(Expr expr,  Position position) {
			super("The reducer must be of type Reducible[T], for some type T."
					+ "\n\t Reducer: " + expr
					+ "\n\t Reducer type: "  + expr.type(),
					position);
		}
	}
	public static class CannotCallCodeThatOffers extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 1561991534265566375L;
		public CannotCallCodeThatOffers(X10ProcedureInstance<? extends ProcedureDef> pi,  Position position) {
			super("Code that can offer values of given type is invoked in a context which does not expect offers."
					+ "\n\t Offer type: " + Types.get(pi.offerType()),
					position);
		}
	}
	public static class OfferTypeMismatch extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 6476600577193965991L;
		public OfferTypeMismatch(Type actualType, Type expectedType,   Position position) {
			super("Offer type mismatch."
					+ "\n\t Expected offer type: " + expectedType
					+ "\n\t Found offer type: " + actualType
					, position);
		}
	}
	public static class IllegalClockedAccess extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = -5824261892277076305L;
	    public IllegalClockedAccess(X10FieldInstance fi, Position pos) {
	        super("Field must be accessed in a clocked context." +
	        		"\n\t Field: " + fi, pos);
	    }
	}
	public static class CannotReturnExpr extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = 211999857915638603L;
	    CannotReturnExpr(Expr expr, Type bType, Type btargetType, Position pos) {
            super("Cannot return expression; base type incompatible with method return type."
                    + "\n\t Expression: " + expr
                    + "\n\t Base type: " + bType
                    + "\n\t Expected base type: " + btargetType
                    , pos);
        }
	    CannotReturnExpr(Expr expr, Type type, XConstraint con, Position pos) {
            super("Cannot return expression; constraints not satisfied."
                    + "\n\t Expression: " + expr
                    + "\n\t Type: " + type
                    + "\n\t Unsatisfied constraints: " + con
                    , pos);
        }
        public static CannotReturnExpr make(Expr expr, Type targetType, ContextVisitor tc, Position pos) {
            Type type = expr.type(), bType = Types.baseType(type), bTargetType = Types.baseType(targetType);
            TypeSystem ts = tc.typeSystem();
            if (! ts.isSubtype(bType, bTargetType, tc.context()))
                return new CannotReturnExpr(expr, bType, bTargetType, pos);
            // base types are compatible, constraints are not.
            CConstraint 
            c = Types.xclause(type), 
            d = Types.xclause(targetType);
            XConstraint residue = c==null? d: c.residue(d);
            
            
            return new CannotReturnExpr(expr, type, residue, pos);
        }
    }
	
	public static class RailLiteralMustBeOfArrayType extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 3059270665285777371L;
		public RailLiteralMustBeOfArrayType(String typeName,   Position pos) {
	        super("A rail literal must start with 'new Rail...'.", pos);
	    }
	}
	public static class RailLiteralTypeMismatch extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -8344153029213631407L;
		public RailLiteralTypeMismatch(Expr e, Type itype) {
	        super("The literal is not of the given type" +
	        		"\n\t expr:" + e +
	        		"\n\t type: " + e.type() +
	        		"\n\t desired type: " + itype, e.position());
	    }
	}
	public static class AtArgMustBePlace extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -2736877234203434252L;
		public AtArgMustBePlace(Expr e, Type placeType, Position pos) {
	        super("The place argument to the at statement cannot be converted to type Place." +
	        		"\n\t expr:" + e +
	        		"\n\t type: " + e.type() +
	        		"\n\t desired type: " + placeType, pos);
	    }
	}

	public static class CannotUseHereInThisContext extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = -2736877234203434252L;
	    public CannotUseHereInThisContext(Position pos) {
	        super("Cannot use \"here\" in this context", pos);
	    }
	}

	public static class CannotAssignValueToFinalField extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 4045368984085560378L;

		public CannotAssignValueToFinalField(X10FieldInstance fd, Position p) {
			super("Cannot assign a value to final field." +
					"\n\t Final field: " + fd.name(), p);
		}
	}
	public static class CannotAssignToStaticField extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 5570402789549272603L;

		public CannotAssignToStaticField(X10FieldInstance fd, Position p) {
			super("Cannot assign to static field." +
					"\n\t Static field: " + fd.name(), p);
		}
	}
	public static class CannotDisambiguateNodeWithAmbiguousPrefix extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -6293236891329098614L;

		public CannotDisambiguateNodeWithAmbiguousPrefix( Position p) {
			super("Cannot disambiguate node with ambiguous prefix.", p);
		}
	}
	public static class PackageOrClassNameNotFound extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -611328678350338960L;

		public PackageOrClassNameNotFound(QName name, Position p) {
			super("Package or class name not found." +
					"\n\t Name: " + name, p);
		}
	}
	public static class ClassNotAccessible extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 9178778259435324030L;

		public ClassNotAccessible(ClassType ct, Position p) {
			super("Class is not accessible." +
					"\n\t Class: " + ct, p);
		}
	}	
	public static class CannotDeclareConstructorInInterface extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -7065380944831259236L;

		public CannotDeclareConstructorInInterface(Position p) {
			super("Cannot declare a constructor inside an interface.", p);
		}
	}
	public static class CannotDeclareConstructorInAnonymousClass extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -8669218725974059793L;

		public CannotDeclareConstructorInAnonymousClass(Position p) {
			super("Cannot declare a constructor inside an anonymous class.", p);
		}
	}
	public static class ConstructorNameDoesNotMatchContainingClassName extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 1150096677241770933L;

		public ConstructorNameDoesNotMatchContainingClassName(Id name, Name ctName, Position p) {
			super("Constructor name does not match name of containing class." +
					"\n\t Constructor name: " + name +
					"\n\t Containing class: " + ctName, p);
		}
	}
	public static class InterfaceMembersMustBePublic extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -538590971149553273L;

		public InterfaceMembersMustBePublic(Position p) {
			super("Interface members must be public.", p);
		}
	}
	public static class InterfaceMethodsMustBePublic extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -3459623861298751099L;

		public InterfaceMethodsMustBePublic(Position p) {
			super("Interface methods must be public.", p);
		}
	}
	public static class InterfaceMethodsCannotBeStatic extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 4347802795939587694L;

		public InterfaceMethodsCannotBeStatic(Position p) {
			super("Interface methods cannot be static.", p);
		}
	}
	public static class InnerClassCannotDeclareStaticFields extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 3312267938190028022L;
		public InnerClassCannotDeclareStaticFields(FieldDef fd, ClassType container, Position p) {
			super("Inner and local classes cannot declare static fields, unless they are compile-time constant fields."
			      + "\n\t Field: " + fd.toString()
			      + "\n\t Container: " + container.toString(), p);
		}
	}
	public static class InnerClassesCannotDeclareStaticMethods extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 7722988292280217208L;
		public InnerClassesCannotDeclareStaticMethods(MethodDef md, ClassType container, Position p) {
			super("Inner and local classes cannot declare static methods."
	              + "\n\t Method: " + md.toString()
	              + "\n\t Container: " + container.toString(), p);
		}
	}
	public static class MissingMethodBody extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 2685352756896202435L;

		public MissingMethodBody(Position p) {
			super("Missing method body.", p);
		}
	}
	public static class InterfaceMethodsCannotHaveBody extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -5941791865765677859L;

		public InterfaceMethodsCannotHaveBody(Position p) {
			super("Interface methods cannot have a body.", p);
		}
	}
	public static class AbstractMethodCannotHaveBody extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -1327030950553878150L;

		public AbstractMethodCannotHaveBody(Position p) {
			super("An abstract method cannot have a body.", p);
		}
	}
	public static class NativeMethodCannotHaveBody extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 5462359406781071209L;

		public NativeMethodCannotHaveBody(Position p) {
			super("A native method cannot have a body.", p);
		}
	}
	public static class IllegalVarianceParameter extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 1409712863344639826L;

		public IllegalVarianceParameter(ParameterType.Variance var, ParameterType.Variance variance, Position p) {
			super("Illegal variance! Type parameter has variance variable but it is used in a variance position." +
					"\n\t Variable: " + var +
					"\n\t Variance: " + variance,p);
		}
	}
	public static class FinalFieldAlreadyInitialized extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -7376028111845924644L;

		public FinalFieldAlreadyInitialized(Name name, Position p) {
			super("Final field might already have been initialized." +
					"\n\t Field: " + name, p);
		}
	}
	public static class CannotReadFromFieldBeforeDefiniteAssignment extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -4917415669609512489L;

		public CannotReadFromFieldBeforeDefiniteAssignment(Name name, Position p) {
			super("Cannot read from field before it is definitely assigned." +
					"\n\t Field: " + name, p);
		}
	}
	public static class FieldNameWasNotDefinitelyAssigned extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -6033353295414568038L;

		public FieldNameWasNotDefinitelyAssigned(Boolean property, Name name, Position p) {
			super(property ? "Property(...) might not have been called." :
                "Field was not definitely assigned." +
                "\n\t Field: " + name, p);
		}
	}
	public static class ControlFlowGraphError extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 5439909487709510158L;

		public ControlFlowGraphError(String msg, Position p) {
			super("Control flow graph had an error." + 
					"\n\t Error: " + msg, p);
		}
	}
	public static class MustReturnValueOfType extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 755005770475489356L;

		public MustReturnValueOfType(String designator, FunctionDef fd, Position p) {
			super(designator + " must return a value of type." +
					"\n\t Type: " + fd.returnType().get(), p);
		}
	}
	public static class MissingReturnStatement extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 8740091192627749311L;

		public MissingReturnStatement(Position p) {
			super("Missing return statement.",p);
		}
	}
	public static class MayNotHaveBeenInitialized extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -1027258040225227536L;

		public MayNotHaveBeenInitialized(Name n, Position p) {
			super("Local variable may not have been initialized" +
					"\n\t Local variable: " + n, p);
		}
	}
	public static class FinalLocalVariableCannotBeAssignedTo extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -3082608693293011502L;

		public FinalLocalVariableCannotBeAssignedTo(Name name, Position p) {
			super("Final local variable cannot be assigned to in an inner class." +
					"\n\t Final local variable: " + name,
                    p);
			}
	}
	public static class FinalVariableAlreadyInitialized extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -1058095715107803234L;

		public FinalVariableAlreadyInitialized(Name name, Position p) {
			super("Final variable might already have been initialized." + 
                    "\n\t Final variable: " + name,
                    p);
		}
	}
	public static class LocalVariableMustBeInitializedBeforeClassDeclaration extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 5116419341545721859L;

		public LocalVariableMustBeInitializedBeforeClassDeclaration(Name name, Position p) {
			super("Local variable must be initialized before the class declaration." +
					"\n\t Local variable: " + name,
                    p);
		}
	}
	public static class UnreachableStatement extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -6391490184776236536L;

		public UnreachableStatement(Position p) {
			super("Unreachable statement.",p);
		}
	}
	public static class InitializersMustCompleteNormally extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -7371091355744661118L;

		public InitializersMustCompleteNormally(Position p) {
			super("Initializers must be able to complete normally.", p);
		}
	}
	public static class NumberTypeArgumentsNotSameAsNumberTypeParameters extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 8325356137110702559L;

		public NumberTypeArgumentsNotSameAsNumberTypeParameters(int size, QName name, int numParams, Position p) {
			super("Number of type arguments is not the same as number of type parameters." +
					"\n\t Type: " + name +
					"\n\t Number of arguments: " + size +
					"\n\t Number of parameters: " + numParams, 
					p);
			}
	}
	public static class AnnotationMustBeInterfacetype extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -2706879539123602875L;

		public AnnotationMustBeInterfacetype(Position p) {
			super("Annotation must be an interface type.", p);
		}
	}
	public static class TypeOfPropertyIsNotSubtypeOfPropertyType extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 3524201314606970604L;

		public TypeOfPropertyIsNotSubtypeOfPropertyType(Type type, List<FieldInstance> props, int i, Position p) {
			super("Actual type of property initializer is not a subtype of declared type." +
			      "\n\t Property: " + props.get(i).name() +
			      "\n\t Actual Type: " +  type +
			      "\n\t Declared Type: " + props.get(i).type(), p);
		}
	}
	public static class PropertyStatementMayOnlyOccurInBodyOfConstuctor extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 898188824542430216L;

		public PropertyStatementMayOnlyOccurInBodyOfConstuctor(Position p) {
			super("A property statement may only occur in the body of a constructor.", p);
		}
	}
	public static class PropertyInitializerMustHaveSameNumberOfArgumentsAsPropertyForClass extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 4412871355070658372L;

		public PropertyInitializerMustHaveSameNumberOfArgumentsAsPropertyForClass(Position p) {
			super("The property initializer must have the same number of arguments as properties for the class.", p);
		}
	}
	public static class ClockedAsyncMustBeInvokedInsideAStaticallyEnclosingClockedFinish extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 8536832971895629265L;

		public ClockedAsyncMustBeInvokedInsideAStaticallyEnclosingClockedFinish(Position p) {
			super("Clocked async must be invoked inside a statically enclosing clocked finish.", p);
		}
	}
	public static class TypeMustBeX10LangClock extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -989345877838120199L;

		public TypeMustBeX10LangClock(Type t, Position p) {
			super("Type must be x10.lang.clock." +
					"\n\t Type: " + t, p);
		}
	}
	public static class CannotOccurOutsideCodeBody extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 7040386157583672803L;
		public static enum Element { Closure, At, Async};
		public CannotOccurOutsideCodeBody(Element element, Position p) {
			super(element + " cannot occur outside code body.", p);
		}
	}
	public static class TypeConstraintMustBeBoolean extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -4432551623865573205L;

		public TypeConstraintMustBeBoolean(Expr e, Type t, Position p) {
			super("The type of the constraint must be boolean." +
					"\n\t Constraint: " + e + 
					"\n\t Actual type: " + t, p);
		}
	}
	public static class DomainIteratedForLoopMustBeLocal extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -6213470792884695982L;

		public DomainIteratedForLoopMustBeLocal(Position p) {
			super("The domain of this iterated for loop must be local", p);
		}
	}
	public static class ConstraintInconsistency extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -421661579077052018L;

		public ConstraintInconsistency(XFailure e, Position p) {
			super("Constraint on here is inconsistent; " + 
					"\n\t Failure: " + e.getMessage(), p);
		}
	}
	public static class TypeDefinitionMustBeStaticClassOrInterfaceMembers extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -8425838279855302691L;

		public TypeDefinitionMustBeStaticClassOrInterfaceMembers(Position p) {
			super("Type definitions must be static class or interface members.  This is a limitation of the current implementation.", p);
		}
	}
	public static class CannotCompareUnsignedVersusSignedValues extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -6839731064051520711L;

		public CannotCompareUnsignedVersusSignedValues(Position p) {
			super("Cannot compare unsigned versus signed values.",p);
		}
	}
	public static class CannotCompareSignedVersusUnsignedValues extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 5435194048939904628L;

		public CannotCompareSignedVersusUnsignedValues(Position p) {
			super("Cannot compare signed versus unsigned values.",p);
		}
	}
	public static class OperatorMustHaveOperandsOfComparabletype extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -703338348089918290L;

		public OperatorMustHaveOperandsOfComparabletype(Type lbase, Type rbase, Position p) {
			super("Operator must have operands of comparable type; these types do not share any values." +
					"\n\t Left type: " + lbase +
					"\n\t Right type: " + rbase, 
					p);
		}
	}
	public static class NoOperationFoundForOperands extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -6078304801274669515L;

		public NoOperationFoundForOperands(Operator op, Type l, Type r, Position p) {
			super("No operation found for these operands." +
					"\n\t Left: " + l +
					"\n\t Right: " + r, p);
		}
	}
	public static class ArgumentOfWhenMustBeBoolean extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 9030879119428159594L;

		public ArgumentOfWhenMustBeBoolean(Position p) {
			super("The type of the argument of a 'when' statement must be Boolean", p);
		}
	}
	public static class InvalidType extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -6767519992326274592L;

		public InvalidType(Type t, Position p) {
			super("Invalid type; the real clause of type is inconsistent." +
					"\n\t Type: " + t, p);
		}
	}
	public static class TypeInconsistent extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 3885914351359414398L;

		public TypeInconsistent(Type t, Position p) {
			super("Type is inconsistent." +
					"\n\t Type: " + t, p);
		}
	}
	public static class CannotReferToTypeParameterFromStaticContext extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 3614407683430453528L;

		public CannotReferToTypeParameterFromStaticContext(ParameterType pt, Def def, Position p) {
			super("Cannot refer to type parameter of declaration from a static context." +
					"\n\t Parameter: " + pt.fullName() +
					"\n\t Declaration: " + def, p);
		}
	}
	public static class CannotQualifyTypeParameter extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 456124673024566425L;

		public CannotQualifyTypeParameter(ParameterType pt, Def def, Flags flags, Position p) {
			super("Cannot qualify type parameter of declaration with flags." + 
					"\n\t Parameter: " + pt.fullName() + 
					"\n\t Declaration: " + def + 
					"\n\t Flags: " + flags, p);
		}
	}
	public static class DuplicateConstructor extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -1776900732090690450L;

		public DuplicateConstructor(X10ConstructorDef cj, X10ConstructorDef ci, Position p) {
			super("Duplicate constructor." +
					"\n\t Constructor: " + cj + 
					"\n\t Previous declaration: " + ci.position(), p);
		}
	}
	public static class DuplicateMethod extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 6494663346170675010L;

		public DuplicateMethod(X10MethodDef mj, X10MethodDef mi, Position p) {
			super("Duplicate method." + 
					"\n\t Method: " + mj + 
					"\n\t Previous declaration: " + mi.position(), p);
		}
	}
	public static class PublicTypeMustBeDeclaredInX10 extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 8500972101236417810L;

		public PublicTypeMustBeDeclaredInX10(ClassDef type, Position p) {
			super("Public type must be declared in .x10 file." +
					"\n\t Type: " + type.fullName() +
					"\n\t .x10 file: " + type.name(), p);
		}
	}
	public static class InterfaceCannotHaveSuperclass extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -951041327107684237L;

		public InterfaceCannotHaveSuperclass(ClassDef type, Position p) {
			super("Interface cannot have a superclass." + 
					"\n\t Interface: " + type, p);
		}
	}
	public static class ClassCannotOverridePropertyOfSuperclass extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -9107279168454654750L;
		public ClassCannotOverridePropertyOfSuperclass(ClassDef type, FieldInstance fi, Position p) {
			super("Class cannot override property of superclass." +
					"\n\t Class: " + type +
					"\n\t Property: " + fi.name() +
					"\n\t Superclass: " + Types.get(fi.def().container()), p);
		}
	}
	public static class CanOnlyQualifySuperConstructorInvocation extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 334170215245078545L;

		public CanOnlyQualifySuperConstructorInvocation(Position p) {
			super("Can only qualify a \"super\" constructor invocation.", p);
		}
	}
	public static class ClassNotInnerClass extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 6916790928445022813L;

		public ClassNotInnerClass(Type superType, Position p) {
			super("The class is not an inner class, or was declared in a static context; a qualified constructor invocation cannot be used." +
					"\n\t Class: " + superType, p);
		}
	}
	public static class QualifierDoesNotMatchEnclosingClass extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 416764378363096062L;

		public QualifierDoesNotMatchEnclosingClass(Type qt, ContainerType container, Position p) {
			super("The type of the qualifier does not match the immediately enclosing class of the super class." +
					"\n\t Qualifier type: " + qt +
					"\n\t Enclosing class of supper class: " + container, p);
		}
	}
	public static class ConstructorsCannotHaveTypeParameters extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -4268091774047965360L;

		public ConstructorsCannotHaveTypeParameters(Position p) {
			super("Constructors cannot have type parameters.", p);
		}
	}
	public static class ReturnTypeOfConstructorMustBeFromTypeOfClass extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 7385763817970282185L;

		public ReturnTypeOfConstructorMustBeFromTypeOfClass(Type retTypeBase, Type clazz, Position p) {
			super("The return type of the constructor must be derived from the type of the class on which the constructor is defined." +
					"\n\t Constructor: " + retTypeBase +
					"\n\t Class: " + clazz, p);
		}
	}
	public static class CannotInferFieldType extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -8796440936111998457L;
		public CannotInferFieldType(Position p) {
			super("Cannot infer field type; field has no initializer.", p);
		}
	}
	public static class CannotInferNonFinalFieldType extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 5501955726545364951L;
		public CannotInferNonFinalFieldType(Position p) {
			super("Cannot infer type of non-final fields.", p);
		}
	}
	public static class CannotInferNativeFieldType extends EqualByTypeAndPosException {
        private static final long serialVersionUID = -1957463133947941268L;
        public CannotInferNativeFieldType(Position p) {
	        super("Cannot infer type of native fields.", p);
	    }
	}
	public static class CannotInferNativeMethodReturnType extends EqualByTypeAndPosException {
        private static final long serialVersionUID = 1285634999428441833L;
        public CannotInferNativeMethodReturnType(Position p) {
	        super("Cannot infer return type of native methods.", p);
	    }
	}
	public static class CannotDeclareStaticNonFinalField extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 3538350155759773020L;

		public CannotDeclareStaticNonFinalField(Position p) {
			super("Cannot declare static non-final field.", p);
		}
	}
	public static class IllegalFieldDefinition extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -50506600138976230L;

		public IllegalFieldDefinition(FieldDef fi, Position p) {
			super("Illegal field definition; Structs cannot have var fields." +
					"\n\t Filed definition: " + fi,p);
		}
	}
	public static class FieldCannotHaveType extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -9171923663121438344L;

		public FieldCannotHaveType(Type type, Position p) {
			super("Field cannot be of this type." +
					"\n\t Type: " + type, p);
		}
	}
	public static class StructMayNotHaveVarFields extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 5225038908218676956L;

		public StructMayNotHaveVarFields(Position p) {
			super("A struct may not have var fields.", p);
		}
	}
	public static class StaticFieldMustHaveInitializer extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -3124433917653058159L;

		public StaticFieldMustHaveInitializer(Id name, Position p) {
			super("Static field must have an initializer." +
					"\n\t Static field name: " + name, p);
		}
	}
	public static class TransientFieldMustHaveTypeWithDefaultValue extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -5733384133852266861L;

		public TransientFieldMustHaveTypeWithDefaultValue(Id name, Position p) {
			super("The transient field must have a type with a default value." +
					"\n\t Transient field name: " + name, p);
		}
	}
	public static class CannotInferTypeForFormalParameter extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -61727884585436624L;

		public CannotInferTypeForFormalParameter(Id name, Position p) {
			super("Could not infer type for formal parameter." + 
					"\n\t Formal parameter name: " + name, p);
		}
	}
	public static class FormalParameterCannotHaveType extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -8535626024832234896L;

		public FormalParameterCannotHaveType(Type type, Position p) {
			//super("Formal parameter cannot have type " + type + ".", p);
			super("Formal parameter cannot have type." +
					"\n\t Type: " + type, p);
		}
	}
	public static class LocalVariableAccessedFromInnerClass extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -7354209819609788973L;

		public LocalVariableAccessedFromInnerClass(Name liName, Position p) {
			super("Local variable is accessed from an inner class or a closure, and must be val." +
					"\n\t Local variable name: " + liName, p);
		}
	}
	public static class LocalVariableCannotBeCapturedInAsync extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 5334856744559947168L;

		public LocalVariableCannotBeCapturedInAsync(Name liName, Position p) {
			super("Local variable cannot be captured in an async if there is no enclosing finish in the same scoping-level." +
					"\n\t Variable name: " + liName, p);
		}
	}
	public static class LocalVariableAccessedAtDifferentPlace extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 5809300848963559701L;

		public LocalVariableAccessedAtDifferentPlace(Name liName, Position p) {
			super("Local variable is accessed at a different place, and therefore it must be an initialized val." +
					"\n\t Variable name: " + liName, p);
		}
	}
	public static class CannotInferTypeofMutalVariable extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -3533052492291300618L;

		public CannotInferTypeofMutalVariable(Position p) {
			super("Cannot infer type of a mutable (non-val) variable.", p);
		}
	}
	public static class CannotInferVariableType extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 7697089332559732619L;

		public CannotInferVariableType(Name name, Position p) {
			super("Cannot infer variable type; variable has no initializer." + 
					"\n\t Name" + name, p);
		}
	}
	public static class LocalVariableCannotHaveType extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 3830132151835663781L;

		public LocalVariableCannotHaveType(Type type, Position p) {
			super("Local variable cannot have type." +
					"\n\t Actual type: " + type, p);
		}
	}
	public static class LoopDomainIsNotOfExpectedType extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -5004964287663095964L;

		public LoopDomainIsNotOfExpectedType(ConstrainedType formalType, Type domainType, HashSet<Type> iterableIndex, Position p) {
			super("Loop domain is not of expected type." 
	                + "\n\t Expected type: Iterable[" + Types.stripConstraintsIfDynamicCalls(formalType) + "]"
	                + "\n\t Actual type: " + Types.stripConstraintsIfDynamicCalls(domainType) + " "+
                    (iterableIndex.size()==0 ? "(must implement Iterable[...])" :
                        "(implements Iterable["+Types.stripConstraintsIfDynamicCalls(iterableIndex.iterator().next())+"])")
                    , p);
		}
	}
	public static class CannotInferMethodReturnType extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -8730396959303696191L;

		public CannotInferMethodReturnType(Position p) {
			super("Cannot infer method return type; method has no body.", p);
		}
	}
	public static class MissingConstructorBody extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 862315945431530111L;

		public MissingConstructorBody(Position p) {
			super("Missing constructor body.", p);
		}
	}
	public static class NativeConstructorCannotHaveABody extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -1270399751516286108L;

		public NativeConstructorCannotHaveABody(Position p) {
			super("A native constructor cannot have a body.", p);
		}
	}
	public static class LiteralOutOfRange extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -3537322791102086322L;

		public LiteralOutOfRange(String str, long value, Position p) {
			super("Literal is out of range." +
					"\n\t Actual literal: " + str +
					"\n\t Actual value: " + value, p);
		}
	}
	public static class NonAbstractPropertyMethodMustBeFinal extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 4289076973488728361L;

		public NonAbstractPropertyMethodMustBeFinal(Position p) {
			super("A non-abstract property method must be final.", p);
		}
	}
	public static class PropertyMethodCannotBeStatic extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 7332775749673910302L;

		public PropertyMethodCannotBeStatic(Position p) {
			super("A property method cannot be static.", p);
		}
	}
	public static class PropertyMethodCannotHaveGuard extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 5402119335168435666L;

		public PropertyMethodCannotHaveGuard(DepParameterExpr guard, Position p) {
			super("A property method cannot have a guard.", guard != null ? guard.position() : p);
		}
	}
	public static class InconsistentContext extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -7025023968245938435L;
		public InconsistentContext(Type ct, Position p) {
			super("Context for type is inconsistent." +
					"\n\t Type: " + ct, p);
	    }
	    public InconsistentContext(CConstraint c, Position p) {
	    	super("Context becomes inconsistent when constraint is added." + 
	    			"\n\t Constraint: " + c, p);
	    }
	}
	public static class InconsistentType extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 3896496971389234633L;
		public InconsistentType(Type t, Position p) {
			super("Inconsistent type." +
					"\n\t Type: " + t, p);
        }
    }
	public static class SuperTypeIsNotAClass extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 3060814244759215072L;
		public SuperTypeIsNotAClass(ClassType ct, Position p) {
			super("Super type is not a class." +
					"\n\t Class type: " + ct, p);
        }
    }
	public static class ClassTypeMustHaveEnclosingInstance extends EqualByTypeAndPosException {
        
		private static final long serialVersionUID = 752361659281947668L;

		public ClassTypeMustHaveEnclosingInstance(ClassType ct, ClassType superContainer, Position p) {
			super("Class type must have an enclosing instance that is a subtype." +
					"\n\t Actual class type: " + ct +
					"\n\t Expected enclosing instance of subtype: " + superContainer, p);
        }
    }
	public static class ClassTypeMustBeSpecifiedInSuperConstructor extends EqualByTypeAndPosException {
        
		private static final long serialVersionUID = -3843224005059646945L;

		public ClassTypeMustBeSpecifiedInSuperConstructor(ClassType ct, ClassType superContainer, Position p) {
            super("An enclosing instance that is a subtype must be specified in the super constructor call." +
            		"\n\t Subtype: " + ct +
            		"\n\t Container: " + superContainer, p);
        }
    }
	public static class CannotAccessField extends EqualByTypeAndPosException {
        
		private static final long serialVersionUID = 7876206253367118844L;

		public CannotAccessField(Id name, X10ClassType tCt, Position p) {
			super("Cannot access a field in class declaration header; the field may be a member of a superclass." +
					"\n\t Field name: " + name +
					"\n\t X10 class type: " + tCt, p);
        }
    }
	public static class UnableToFindImplementingPropertyMethod extends EqualByTypeAndPosException {
        
		private static final long serialVersionUID = 2542687322009212061L;

		public UnableToFindImplementingPropertyMethod(Name name, Position p) {
            super("Unable to find the implementing property method for an interface property." + 
            		"\n\t Interface property name: " + name, p);
        }
    }
	public static class OnlyTypePointOrArrayCanBeExploded extends EqualByTypeAndPosException {
        
		private static final long serialVersionUID = 2954246035729867260L;

		public OnlyTypePointOrArrayCanBeExploded(Type myType, Position p) {
            super("Only a formal of type Point or Array can be exploded." +
            		"\n\t Formal type: " + myType, p);
        }
    }
	public static class LocalVariableNotAllowedInContainer extends EqualByTypeAndPosException {
        
		private static final long serialVersionUID = 9188013965362151860L;

		public LocalVariableNotAllowedInContainer(Name liName, Position p) {
			super("A var local variable is not allowed in a constraint." +
					"\n\t Var name: " + liName, p);
        }
    }
	public static class MethodBodyMustBeConstraintExpressiong extends EqualByTypeAndPosException {
        
		private static final long serialVersionUID = -4319645748289940419L;

		public MethodBodyMustBeConstraintExpressiong(Position p) {
            super("Property method body must be a constraint expression.", p);
        }
    }
	public static class MustHaveSameClassAsContainer extends EqualByTypeAndPosException {
        
		private static final long serialVersionUID = 8988805819997786489L;

		public MustHaveSameClassAsContainer(Position p) {
            super("The return type of an explicit or implicit operator 'as' " +
            		"must have the same class as the container.", p);
        }
    }
	public static class TypeParameterMultiplyDefined extends EqualByTypeAndPosException {
        
		private static final long serialVersionUID = 3322184703494765738L;

		public TypeParameterMultiplyDefined(Name name, Position p) {
			super("Type parameter multiply defined." +
					"\n\t Name: " + name, p);
        }
    }
	public static class LocalVariableMultiplyDefined extends EqualByTypeAndPosException {
        
		private static final long serialVersionUID = -8530740127048586198L;

		public LocalVariableMultiplyDefined(Name name, Position p) {
			super("Local variable multiply defined." +
					"\n\t Name: " + name, p);
		}
		public LocalVariableMultiplyDefined(Name name, Position outerP, Position p) {
		    super("Local variable multiply defined." +
		            "\n\t Name: " + name +
		            "\n\t Previous definition: " + outerP, p);
		}
	}
	public static class CouldNotFindNonStaticMemberClass extends EqualByTypeAndPosException {
        
		private static final long serialVersionUID = -8483611596874182943L;

		public CouldNotFindNonStaticMemberClass(Name name, Position p) {
			super("Could not find non-static member class." +
					"\n\t Name: " + name, p);
        }
    }
	public static class OnlySimplyNameMemberClassMayBeInstantiated extends EqualByTypeAndPosException {
        
		private static final long serialVersionUID = 6821123540936733094L;

		public OnlySimplyNameMemberClassMayBeInstantiated(Position p) {
            super("Only simply-named member classes may be instantiated by a qualified new expression.", p);
        }
    }
	public static class CannotInstantiateMemberClass extends EqualByTypeAndPosException {
        
		private static final long serialVersionUID = -6547512782619290457L;

		public CannotInstantiateMemberClass(Position p) {
            super("Cannot instantiate member class of non-class type.", p);
        }
    }
	public static class CannotInstantiateType extends EqualByTypeAndPosException {
        
		private static final long serialVersionUID = -2172383355204979418L;

		public CannotInstantiateType(Type ct, Position p) {
			super("Cannot instantiate type due to incorrect number of type arguments." +
					"\n\t Type: " + ct, p);
        }
    }
	public static class MustReturnValueFromNonVoidMethod extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = 8449016684539457751L;
	    public MustReturnValueFromNonVoidMethod(Position p) {
	        super("Must return value from non-void method.", p);
	    }
	}
	public static class CannotReturnValueFromVoidMethod extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = -2211763341228911088L;
	    public CannotReturnValueFromVoidMethod(Position p) {
	        super("Cannot return value from void method or closure.", p);
	    }
	}
	public static class CannotReturnValueFromConstructor extends EqualByTypeAndPosException {
        private static final long serialVersionUID = 833533689299328410L;
        public CannotReturnValueFromConstructor(Position p) {
	        super("Cannot return value from a constructor.", p);
	    }
	}
	public static class SelfMayOnlyBeUsedWithinDependentType extends EqualByTypeAndPosException {
        
		private static final long serialVersionUID = 5580046365659081850L;

		public SelfMayOnlyBeUsedWithinDependentType(Position p) {
            super("self may only be used within a dependent type", p);
        }
    }
	public static class CannotAccessNonStaticFromStaticContext extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = -6660349817801232916L;
	    public CannotAccessNonStaticFromStaticContext(Position p) {
	        super("Cannot access a non-static field or method, or refer to \"this\" or \"super\" from a static context.", p);
	    }
	    public CannotAccessNonStaticFromStaticContext(FieldInstance fi, Position p) {
	        super("Cannot access a non-static field "+fi+" from a static context.", p);
	    }
	    public CannotAccessNonStaticFromStaticContext(MethodInstance mi, Position p) {
	        super("Cannot access a non-static method "+mi+" from a static context.", p);
	    }
	}
	public static class ConstraintOnThisIsInconsistent extends EqualByTypeAndPosException {
        
		private static final long serialVersionUID = 1741711946479260959L;

		public ConstraintOnThisIsInconsistent(XFailure e, Position p) {
			super("Constraint on this is inconsistent." + 
					"\n\t Failure: " + e.getMessage(), p);
        }
    }
	public static class ConstraintOnSuperIsInconsistent extends EqualByTypeAndPosException {
        
		private static final long serialVersionUID = -7105890838233828297L;

		public ConstraintOnSuperIsInconsistent(XFailure e, Position p) {
			super("Constraint on super is inconsistent." + 
					"\n\t Failure: " + e.getMessage(), p);
        }
    }
	public static class CannotApplyToFinalVariable extends EqualByTypeAndPosException {
        
		private static final long serialVersionUID = 4142666893804299390L;

		public CannotApplyToFinalVariable(Unary.Operator op, Position p) {
			super("Cannot apply operation to a final variable." +
					"\n\t Unary operator: " + op, p);
        }
    }
	public static class CannotApplyToArbitraryMethodCall extends EqualByTypeAndPosException {
        
		private static final long serialVersionUID = 2978452858686727693L;

		public CannotApplyToArbitraryMethodCall(Unary.Operator op, Position p) {
			super("Cannot apply operation to an arbitrary method call." +
					"\n\t Unary operator: " + op, p);
        }
    }
	public static class CannotApplyToArbitraryExpression extends EqualByTypeAndPosException {
        
		private static final long serialVersionUID = 8563392445054545327L;

		public CannotApplyToArbitraryExpression(Unary.Operator op, Position p) {
			super("Cannot apply operation to an arbitrary expression." +
					"\n\t Unary operator: " + op, p);
        }
    }
	public static class NoMethodFoundInType extends EqualByTypeAndPosException {
        
		private static final long serialVersionUID = 3121851317077909122L;

		public NoMethodFoundInType(Name name, Type type, Position p) {
			super("Method name not found in type." +
					"\n\t Actual type: " + type +
					"\n\t Expected method name: " + name, p);
        }
    }
	public static class NoBinaryOperatorFoundInType extends EqualByTypeAndPosException {
        
		private static final long serialVersionUID = 1526054057818012596L;

		public NoBinaryOperatorFoundInType(Binary.Operator binaryOp, Type t, Position p) {
			super("No binary operator found in type." +
					"\n\t Actual type: " + t +
					"\n\t Expected binary operator: " + binaryOp, p);
        }
    }
	public static class IncompatibleReturnTypeOfBinaryOperator extends EqualByTypeAndPosException {
        
		private static final long serialVersionUID = 3142770652264041803L;

		public IncompatibleReturnTypeOfBinaryOperator(Binary.Operator binaryOp, Type resultType, Type et, Position p) {
			super("Incompatible return type of binary operator." + 
					"\n\t Binary operator: " + binaryOp +
					"\n\t Operator return type: " + resultType + 
					"\n\t Expression type: "+ et, p);
        }
    }
	public static class NoOperationFoundForOperand extends EqualByTypeAndPosException {
        
		private static final long serialVersionUID = -6350714420202167451L;

		public NoOperationFoundForOperand(Unary.Operator op, Type t, Position p) {
			super("No operation found for operand." + 
					"\n\t Expected operation: " + op +
					"\n\t Actual operand: " + t, p);
        }
    }
	public static class UnknownType extends EqualByTypeAndPosException {
        
		private static final long serialVersionUID = -7259950887160518778L;

		public UnknownType(Position p) {
            super("Complaining about UnknownType", p);
        }
    }
	public static class InconsistentTypeSelf extends EqualByTypeAndPosException {
        
		private static final long serialVersionUID = -2272052142358768489L;

		public InconsistentTypeSelf(Type toType, XTerm sv, Position p) {
			super("Inconsistent type." + 
					"\n\t Expected type: " + toType +
					"\n\t {self==" + sv+"}", p);
        }
    }
	public static class AnnotationMustImplementType extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 2306390786578072500L;
		public static enum Element { types, expressions, statements, method_declarations, field_declarations,
			class_declarations, package_declarations, imports;
			public String toString() {
				return name().replace('_', ' ');
			}
		};
        public AnnotationMustImplementType(X10ClassType at, Element element, Type type, Position p) {
        	super("Annotation of X10 class on an element must implement a type." +
        			"\n\t Actual X10 class: " + at +
        			"\n\t Actual element type: " + element +
        			"\n\t Expected type: " + type, p);
        }
        public AnnotationMustImplementType(X10ClassType at, Type type, Position p) {
        	super("Annotation of X10 class must implement type." + 
        			"\n\t Actual X10 class: " + at +
        			"\n\t Expected type: " + type, p);
        }
    }
	public static class GeneralError extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 2155276761588484310L;

		public GeneralError(String str, Position p) {
            super(str, p);
        }
    }
	public static class RecursiveTypeDefinition extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -842340018775625795L;

		public RecursiveTypeDefinition(Position p) {
            super("Recursive type definition; type definition depends on itself.", p);
        }
    }
	public static class MethodsOverrideWithCompatibleSignatures extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 7345146936277355750L;

		// TODO: no idea what this means, dead code
		public MethodsOverrideWithCompatibleSignatures(MethodInstance mj, MethodInstance mi, Position p) {
			super("Method " + mj.signature() + " in " + mj.container() + " and method " + mi.signature() + " in " + mi.container()
					+ " override methods with compatible signatures.", p);
        }
    }
	public static class DuplicateTypeDefinition extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 3601200386807354680L;

		public DuplicateTypeDefinition(TypeDef mj, TypeDef mi, Position p) {
			super("Duplicate type definition." + 
					"\n\t Type: " + mi +
					"\n\t Previous declaration: " + mi.position(), p);
        }
    }
	public static class TypeDefinitionSameNameAsMemberClass extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = 3624067897043533607L;
	    private static String designation(ClassType ct) {
	        Flags flags = ct.flags();
	        if (flags.isStruct()) return "struct";
	        if (flags.isInterface()) return "interface";
	        return "class";
	    }
	    public TypeDefinitionSameNameAsMemberClass(TypeDef mi, ClassType ct, Position p) {
	        super("Type definition has the same name as member " + designation(ct) + "." +
	                "\n\t Type definition: " + mi +
	                "\n\t Member " + designation(ct) + ": " + ct, p);
	    }
	}
	public static class ClockedLoopMayOnlyBeClockedOnClock extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -1335837449324934323L;

		public ClockedLoopMayOnlyBeClockedOnClock(Position p) {
            super("Clocked loop may only be clocked on a clock.", p);
        }
    }
	public static class TernaryConditionMustBeBoolean extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = -1877025742518675776L;
	    public TernaryConditionMustBeBoolean(Position p) {
	        super("Condition of ternary expression must be of type boolean.", p);
	    }
	}
	public static class ConstructorGuardNotSatisfied extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 6434865257883772504L;

		public ConstructorGuardNotSatisfied(Position p) {
            super("The constructor guard was not satisfied.", p);
        }
    }
	public static class DoStatementMustHaveBooleanType extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -9135430610071222757L;

		public DoStatementMustHaveBooleanType(Type type, Position p) {
			super("Condition of do statement must have boolean type." + 
					"\n\t Actual type :" + type, p);
        }
    }
	public static class StructsCircularity extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 9009031466278739441L;

		public StructsCircularity(Position p) {
            super("Circularity in the usage of structs will cause this field to have infinite size. Use a class instead of a struct.",p);
        }
    }
	public static class IfStatementMustHaveBooleanType extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -5594198395865204963L;

		public IfStatementMustHaveBooleanType(Type type, Position p) {
			super("Condition of if statement must have boolean type." + 
					"\n\t Type:" + type, p);
        }
    }
	public static class CannotReturnFromAsync extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -2159259054519115441L;

		public CannotReturnFromAsync(Position p) {
            super("Cannot return from an async.",p);
        }
    }
	public static class SourceContainsMoreThanOnePublicDeclaration extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 919717419009474300L;

		public SourceContainsMoreThanOnePublicDeclaration(Position p) {
            super("The source contains more than one public class declaration.", p);
        }
    }
	public static class CannotReferToSuperFromDeclarationHeader extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -8299709535638160197L;

		public CannotReferToSuperFromDeclarationHeader(Position p) {
            super("Cannot refer to \"super\" from within a class or interface declaration header.", p);
        }
    }
	public static class NestedClassMissingEclosingInstance extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = -7130506706943788378L;

		public NestedClassMissingEclosingInstance(X10ClassType c, Type ct, Position p) {
			super( "Nested class must have an enclosing instance." +
					"\n\t Actual nested Class: " + c +
					"\n\t Expected instance type: " + ct, p) ;
        }
    }
	public static class InvalidQualifierForSuper extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 4857358619197913454L;

		public InvalidQualifierForSuper(Position p) {
            super("Invalid qualifier for \"this\" or \"super\".", p);
        }
    }
	public static class WhileStatementMustHaveBooleanType extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 387625309316860312L;

		public WhileStatementMustHaveBooleanType(Type type, Position p) {
			super("Condition of while statement must have boolean type." +
					"\n\t Type: " + type, p);
        }
    }
	public static class MaxMacroExpansionDepth extends EqualByTypeAndPosException {
		
		private static final long serialVersionUID = 5186067047134099233L;

 		public MaxMacroExpansionDepth(Type t, Position p) {
 			super("Reached max macro expansion depth." +
 			"\n\t Expanded type: " + t, p);
        }
    }
	
    public static class TypeGuardNotEntailed extends SemanticException {
        
		private static final long serialVersionUID = 7217492191556883816L;

		public TypeGuardNotEntailed(TypeConstraint tb, Type container) {
			super("Cannot instantiate type container since type guard was not entailed." + 
					"\n\t Type Container: " + container + 
					"\n\t Type Guard: " + tb);
        }
    }
    
    public static class MultipleMethodDefsMatch extends SemanticException {

		private static final long serialVersionUID = -3288674805766816121L;

		private MultipleMethodDefsMatch(String str, Position p) {
            super(str, p);
        }
		public static SemanticException make(Collection<MethodInstance> mis, String name, Position p) {
		    String str = "Multiple methods match " + name + ": ";
		    for (MethodInstance mi : mis) {
		        str += "\n\t" + mi;
		    }
		    return new MultipleMethodDefsMatch(str, p);
		}
    }
    
    public static class SuperCallCannotEstablishSuperType extends SemanticException {

		private static final long serialVersionUID = 4638105732313176934L;

		public SuperCallCannotEstablishSuperType(Type returnType, Type superType, Position p) {
            super("The information from super(...) and property(...) cannot establish the supertype. " 
            		+ "\n\t Return type: " + returnType 
            		+ "\n\t Desired super type: " + superType, p);
        }
    }
    public static class InvariantNotEntailed extends SemanticException {

		private static final long serialVersionUID = 3967279435892439019L;

		public InvariantNotEntailed(CConstraint known, CConstraint inv, Position p) {
            super("With information from super(...) and property(...) cannot establish the class invariant. " 
            		+ "\n\t Known information: " + known.toString() 
            		+ "\n\t Class invariant: " + known.residue(inv), p);
        }
    }
    public static class InterfaceInvariantNotEntailed extends SemanticException {
		private static final long serialVersionUID = -3322620203926003102L;

		public InterfaceInvariantNotEntailed(CConstraint known, Type intfc, CConstraint inv, Position p) {
            super("With information from super(...) and property(...), cannot establish the given interface type. " 
            		+ "\n\t Known information: " + known 
            		+ "\n\t Interface type: " + intfc, p);
        }
    }
    
    public static class CannotOverrideGuard extends SemanticException {
		private static final long serialVersionUID = 272479475853566129L;

		public CannotOverrideGuard(MethodInstance mi, MethodInstance mj) {
            super("Method mi cannot override method mj. " 
            		+ "\n\t mi: " + mi
            		+ "\n\t mj: " + mj
            		+ "\n\t Reason: mj's guard does not entail mi's guard.",
            		mi.errorPosition());
        }
    }
    public static class IllegalConstraint  extends SemanticException {
		private static final long serialVersionUID = 4076811545544318952L;
		public IllegalConstraint(Term t) {
			super("Illegal constraint." +
					"\n\t Term: " + t, t.position());
        }
		public IllegalConstraint(Call c, XTerm t, Position p) {
			super("Illegal constraint. The nested call expands into a term that is not permitted to be nested."
					+ "\n\t Call: " + c
					+ "\n\t Expansion: " + t, p);
					
		}
    }
    public static class ArrayExplosionError extends EqualByTypeAndPosException {
        private static final long serialVersionUID = 2851042936446059831L;
        public ArrayExplosionError(int n, Position pos) {
            super("Array argument must have constraint {rank==1,size=" + n + "}.", pos);
        }
    }
    public static class ParametricClassCannotExtendCheckedThrowable extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -5078018337334696948L;

		public ParametricClassCannotExtendCheckedThrowable(X10ClassDef cd, Position pos) {
            super("A class with type parameters cannot extend x10.lang.CheckedThrowable."
            		+ "\n\t Class: " + cd, pos);
        }
    }
}
