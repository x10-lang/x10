/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.errors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyglot.ast.Binary;
import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.ast.Binary.Operator;
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.ConstructorInstance;
import polyglot.types.ContainerType;
import polyglot.types.Def;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.FunctionDef;
import polyglot.types.Name;
import polyglot.types.ProcedureDef;
import polyglot.types.ProcedureInstance;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
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
import x10.util.CollectionFactory;
import x10.ExtensionInfo;
import x10.ExtensionInfo.X10Scheduler.X10Job;
import x10.ast.DepParameterExpr;
import x10.ast.SettableAssign;
import x10.ast.X10ClassDecl;
import x10.ast.X10FieldDecl;
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

    // todo Yoav added: I use serialVersionUID=1L like lpg parser. We should increment it if an class changes.
    
    public static class ClassCannotHaveSuperInterface extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -1L;
		public ClassCannotHaveSuperInterface(ClassType type, Position p) {
			super("Class " + type + " cannot have a superinterface.", p);
		}
	}
    public static class SuperInterfaceNotInterface extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -1L;
		public SuperInterfaceNotInterface(Type t, ClassType type, Position p) {
			super("Superinterface " + t + " of " + type + " is not an interface.", p);
		}
	}
    public static class CannotHaveSuperclass extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -1L;
		public CannotHaveSuperclass(ClassType type, Position p) {
			super("Class \"" + type + "\" cannot have a superclass.", p);
		}
	}
    public static class ExtendedFinalClass extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -1L;
		public ExtendedFinalClass(ClassType type, Position p) {
			super("Cannot extend final class \"" + type.superClass() + "\".", p);
		}
	}
    public static class ExtendedNonClass extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -1L;
		public ExtendedNonClass(ClassType type, Position p) {
			super("Cannot extend non-class \"" + type.superClass() + "\".", p);
		}
	}
    public static class InnerDeclaredStatic extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -1L;
		public InnerDeclaredStatic(ClassType type, Position p) {
			super("Inner classes cannot declare static member classes.", p);
		}
	}
    public static class InnerDeclaredInterface extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -1L;
		public InnerDeclaredInterface(ClassType type, Position p) {
			super("Inner classes cannot declare member interfaces.", p);
		}
	}
    public static class SameNameLocal extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -1L;
		public SameNameLocal(ClassType type, Position p) {
			super("Cannot declare local " +
                                    "class \"" + type + "\" within the same " +
                                    "method, constructor or initializer as another " +
                                    "local class of the same name.", p);
		}
	}

    public static class SameNameClass extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -1L;
		public SameNameClass(ClassType type, Position p) {
			super("Cannot declare member " +
                                "class \"" + type.fullName() +
                                "\" inside class with the " +
                                "same name.", p);
		}
    }
	public static class DuplicateMember extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -1L;
		public DuplicateMember(TypeObject def) {
			super("Duplicate member " + def, def.position());
		}
	}

	public static class CannotAssign extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -4243637083971033996L;
		public CannotAssign(Expr expr, Type targetType, Position pos) {
			super("Cannot assign expression to target."
					+ "\n\t Expression: " + expr
					+ "\n\t Expected type: " + targetType
					+ "\n\t Found type: " + expr.type()
					, pos);
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
			super(mi.signature() + " in " + mi.container() + " cannot override " + mj.signature() + " in " + mj.container()
			        + "; attempting to use incompatible return type."
					+ "\n\t Expected Type: " + mj.returnType()
					+ "\n\t Found Type: " + mi.returnType(), mi.position());
		}
	}

	public static class InvalidParameter extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -1351185257724314440L;
		public InvalidParameter(Type from, Type to, Position pos) {
			super("   Invalid Parameter.\n\t Expected type: " + to + "\n\t Found type: " + from, pos);
		}
	}

	public static class NoAssignmentInDepType extends EqualByTypeAndPosException implements DepTypeException {
		private static final long serialVersionUID = 8343234065357158485L;
		public NoAssignmentInDepType(FieldAssign f, Position pos) {
			super("Assignment may not appear in a constrained type: \n\t Error: " + f, pos);
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
			super("Place type error: Field should be global. \n\t Field: " + f, pos);
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
                    me.position());
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
	        super("Illegal type def " + mt + ": type-defs must be static.", pos);
	    }
	}
	public static class StructMustBeStatic extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 1450037642852701286L;
		public StructMustBeStatic(X10ClassDecl cd) {
			super("Struct must be declared static."
					+ "\n\t Struct: " + cd.name(),
					cd.position());
		}
	}
	public static class NewOfStructNotPermitted extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 2484875712265904017L;
		public NewOfStructNotPermitted(New n) {
			super("Struct constructor invocations must not use \"new\"."
					+ "\n\t Struct: " + n.toString(),
					n.position());
		}
	}
	public static class InstanceofError extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -3026696944876868780L;
		public InstanceofError(Type left, Type right, Position pos) {
			super("Left operand of instanceof must be castable to right type."
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
		    map.put(CodedErrorInfo.ERROR_CODE_KEY, 1002);
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
	public static class AmbiguousCall extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = 2449179239460432298L;
        public AmbiguousCall(ProcedureInstance<?> pi,  Expr cc, Position pos) {
            super("Ambiguous call: the given procedure and closure match."
                  + "\n\t Procedure: "  + pi
                  + "\n\t Closure: "  + cc,
                  pos);
        }
	    public AmbiguousCall(MethodInstance pi, ConstructorInstance ci, Position pos) {
	        super("Ambiguous call: cannot resolve between struct constructor and method."
	              + "\n\t Method: " + pi
	              + "\n\t Struct constructor: " + ci,
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
	        super("Ambiguous operator '" + op + "': all of\n" + matchingMethods(mis) + "match.", pos);
	    }
	    public AmbiguousOperator(Binary.Operator op, List<MethodInstance> mis,  Position pos) {
	        super("Ambiguous operator '" + op + "': all of\n" + matchingMethods(mis) + "match.", pos);
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

	public static class CannotDisambiguate extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -4594440281666152534L;
		public CannotDisambiguate(Node n, Position position) {
			super("Cannot disambiguate " + n, position);
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
	        super(superType + " cannot be the superclass for " + type +
	              "; a class must subclass a class.", pos);
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
					,
					position);
		}
	}
	public static class StructMayNotBeGlobal extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 6179492565190231102L;
		public StructMayNotBeGlobal(Position position, X10ClassDecl cd) {
			super("global modifier cannot be used for structs."
					+ "\n\t Struct declaration: " + cd.name(), 
					position);
		}
	}
	public static class GlobalClassMustHaveGlobalClassSupertype extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 2237512073167205925L;
		public GlobalClassMustHaveGlobalClassSupertype(Ref<? extends Type> superType, ClassDef type, Position pos) {
	        super(superType + " cannot be the superclass for " + type +
	              "; a global class must subclass a global class.", pos);
	    }
	}
	public static class IllegalClockedAccess extends EqualByTypeAndPosException {
	    private static final long serialVersionUID = -5824261892277076305L;
	    public IllegalClockedAccess(X10FieldInstance fi, Position pos) {
	        super(fi + " must be accessed in a clocked context.", pos);
	    }
	}
	public static class CannotReturnExpr extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 211999857915638603L;
		public CannotReturnExpr(Type type,  Type returnType, Position pos) {
	        super("Cannot return expression of given type."
	        		+ "\n\t type: " + type
	        		+ "\n\t desired Type:" + returnType, pos);
	    }
	}
	public static class ArrayLiteralMustBeOfArrayType extends EqualByTypeAndPosException {
		private static final long serialVersionUID = 3059270665285777371L;
		public ArrayLiteralMustBeOfArrayType(String typeName,   Position pos) {
	        super("An array literal must start with 'new Array...'.", pos);
	    }
	}
	public static class ArrayLiteralTypeMismatch extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -8344153029213631407L;
		public ArrayLiteralTypeMismatch(Expr e, Type itype) {
	        super("The literal is not of the given type"
	        		+ "\n\t expr:" + e
	        		+ "\n\t type: " + e.type()
	        		+ "\n\t desired type: " + itype, e.position());
	    }
	}
	public static class AtArgMustBePlace extends EqualByTypeAndPosException {
		private static final long serialVersionUID = -2736877234203434252L;
		public AtArgMustBePlace(Expr e, Type placeType, Position pos) {
	        super("The place argument to the at statement cannot be converted to type Place."
	        		+ "\n\t expr:" + e
	        		+ "\n\t type: " + e.type()
	        		+ "\n\t desired type: " + placeType, pos);
	    }
	}
	
	
	

	public static class CannotAssignValueToFinalField extends EqualByTypeAndPosException {
		public CannotAssignValueToFinalField(X10FieldInstance fd, Position p) {
			super("Cannot assign a value to final field " + fd.name(),
					p);
		}
	}
	public static class CannotAssignToStaticField extends EqualByTypeAndPosException {
		public CannotAssignToStaticField(X10FieldInstance fd, Position p) {
			super("Cannot assign to static field " + fd.name(),
					p);
		}
	}
	public static class CannotDisambiguateNodeWithAmbiguousPrefix extends EqualByTypeAndPosException {
		public CannotDisambiguateNodeWithAmbiguousPrefix( Position p) {
			super("Cannot disambiguate node with ambiguous prefix.", p);
		}
	}
	public static class PackageOrClassNameNotFound extends EqualByTypeAndPosException {
		public PackageOrClassNameNotFound(QName name, Position p) {
			super("Package or class " + name + " not found.", p);
		}
	}
	public static class ClassNotAccessible extends EqualByTypeAndPosException {
		public ClassNotAccessible(ClassType ct, Position p) {
			super("Class " + ct + " is not accessible.", p);
		}
	}	
	public static class CannotDeclareConstructorInInterface extends EqualByTypeAndPosException {
		public CannotDeclareConstructorInInterface(Position p) {
			super("Cannot declare a constructor inside an interface.", p);
		}
	}
	public static class CannotDeclareConstructorInAnonymousClass extends EqualByTypeAndPosException {
		public CannotDeclareConstructorInAnonymousClass(Position p) {
			super("Cannot declare a constructor inside an anonymous class.", p);
		}
	}
	public static class ConstructorNameDoesNotMatchContainingClassName extends EqualByTypeAndPosException {
		public ConstructorNameDoesNotMatchContainingClassName(Id name, polyglot.types.Name ctName, Position p) {
			super("Constructor name \"" + name +"\" does not match name of containing class \"" + ctName + "\".", p);
		}
	}
	public static class InterfaceMembersMustBePublic extends EqualByTypeAndPosException {
		public InterfaceMembersMustBePublic(Position p) {
			super("Interface members must be public.", p);
		}
	}
	public static class InterfaceMethodsMustBePublic extends EqualByTypeAndPosException {
		public InterfaceMethodsMustBePublic(Position p) {
			super("Interface methods must be public.", p);
		}
	}
	public static class InterfaceMethodsCannobBeStatic extends EqualByTypeAndPosException {
		public InterfaceMethodsCannobBeStatic(Position p) {
			super("Interface methods cannot be static.", p);
		}
	}
	public static class InnerClassCannotDeclareStaticFields extends EqualByTypeAndPosException {
		public InnerClassCannotDeclareStaticFields(Position p) {
			super("Inner classes cannot declare static fields, unless they are compile-time constant fields.", p);
		}
	}
	public static class InnerClassesCannotDeclareStaticMethod extends EqualByTypeAndPosException {
		public InnerClassesCannotDeclareStaticMethod(Position p) {
			super("Inner classes cannot declare static methods.", p);
		}
	}
	public static class MissingMethodBody extends EqualByTypeAndPosException {
		public MissingMethodBody(Position p) {
			super("Missing method body.", p);
		}
	}
	public static class InterfaceMethodsCannotHaveBody extends EqualByTypeAndPosException {
		public InterfaceMethodsCannotHaveBody(Position p) {
			super("Interface methods cannot have a body.", p);
		}
	}
	public static class AbstractMethodCannotHaveBody extends EqualByTypeAndPosException {
		public AbstractMethodCannotHaveBody(Position p) {
			super("An abstract method cannot have a body.", p);
		}
	}
	public static class NativeMethodCannotHaveBody extends EqualByTypeAndPosException {
		public NativeMethodCannotHaveBody(Position p) {
			super("A native method cannot have a body.", p);
		}
	}
	public static class IllegalVarianceParameter extends EqualByTypeAndPosException {
		public IllegalVarianceParameter(ParameterType.Variance var, ParameterType.Variance variance, Position p) {
			super("Illegal variance! Type parameter has variance "+var+" but it is used in a "+variance+" position.",p);
		}
	}
	public static class FinalFieldAlreadyInitialized extends EqualByTypeAndPosException {
		public FinalFieldAlreadyInitialized(polyglot.types.Name name, Position p) {
			super("Final field '"+name+"' might already have been initialized.",p);
		}
	}
	public static class CannotReadFromFieldBeforeDefiniteAssignment extends EqualByTypeAndPosException {
		public CannotReadFromFieldBeforeDefiniteAssignment(polyglot.types.Name name, Position p) {
			super("Cannot read from field '"+name+"' before it is definitely assigned.",p);
		}
	}
	public static class FieldNameWasNotDefinitelyAssigned extends EqualByTypeAndPosException {
		public FieldNameWasNotDefinitelyAssigned(Boolean property, polyglot.types.Name name, Position p) {
			super(property ? "property(...) might not have been called" :
                "Field '"+name+"' was not definitely assigned.",p);
		}
	}
	public static class ControlFlowGraphError extends EqualByTypeAndPosException {
		public ControlFlowGraphError(String msg, Position p) {
			super("Control flow graph had an error: "+msg, p);
		}
	}
	public static class MustReturnValueOfType extends EqualByTypeAndPosException {
		public MustReturnValueOfType(String designator, FunctionDef fd, Position p) {
			super(designator + " must return a value of type "+fd.returnType().get(),p);
		}
	}
	public static class MissingReturnStatement extends EqualByTypeAndPosException {
		public MissingReturnStatement(Position p) {
			super("Missing return statement.",p);
		}
	}
	public static class MayNotHaveBeenInitialized extends EqualByTypeAndPosException {
		public MayNotHaveBeenInitialized(polyglot.types.Name n, Position p) {
			super("\"" + n + "\" may not have been initialized", p);
		}
	}
	public static class FinalLocalVariableCannotBeAssignedTo extends EqualByTypeAndPosException {
		public FinalLocalVariableCannotBeAssignedTo(polyglot.types.Name name, Position p) {
			super("Final local variable \"" + name +
                    "\" cannot be assigned to in an inner class.",
                    p);
		}
	}
	public static class FinalVariableAlreadyInitialized extends EqualByTypeAndPosException {
		public FinalVariableAlreadyInitialized(polyglot.types.Name name, Position p) {
			super("Final variable \"" + name +
                    "\" might already have been initialized",
                    p);
		}
	}
	public static class LocalVariableMustBeInitializedBeforeClassDeclaration extends EqualByTypeAndPosException {
		public LocalVariableMustBeInitializedBeforeClassDeclaration(polyglot.types.Name name, Position p) {
			super("Local variable \"" + name +
                    "\" must be initialized before the class " +
                    "declaration.",
                    p);
		}
	}
	public static class UnreachableStatement extends EqualByTypeAndPosException {
		public UnreachableStatement(Position p) {
			super("Unreachable statement.",p);
		}
	}
	public static class InitializersMustCompleteNormally extends EqualByTypeAndPosException {
		public InitializersMustCompleteNormally(Position p) {
			super("Initializers must be able to complete normally.",
                    p);
		}
	}
	public static class NumberTypeArgumentsNotSameAsNumberTypeParameters extends EqualByTypeAndPosException {
		public NumberTypeArgumentsNotSameAsNumberTypeParameters(int size, QName name, int numParams, Position p) {
			super("Number of type arguments (" + size + ") for " + name + 
					" is not the same as number of type parameters (" + numParams + ").", 
					p);
		}
	}
	public static class AnnotationMustBeInterfacetype extends EqualByTypeAndPosException {
		public AnnotationMustBeInterfacetype(Position p) {
			super("Annotation must be an interface type.", p);
		}
	}
	public static class TypeOfPropertyIsNotSubtypeOfPropertyType extends EqualByTypeAndPosException {
		public TypeOfPropertyIsNotSubtypeOfPropertyType(Type type, List<FieldInstance> props, int i, Position p) {
			super("The type " + type + " of the initializer for property " + props.get(i) + 
					" is not a subtype of the property type " + props.get(i).type(), 
					p);
		}
	}
	public static class PropertyStatementMayOnlyOccurInBodyOfConstuctor extends EqualByTypeAndPosException {
		public PropertyStatementMayOnlyOccurInBodyOfConstuctor(Position p) {
			super("A property statement may only occur in the body of a constructor.", p);
		}
	}
	public static class PropertyInitializerMustHaveSameNumberOfArgumentsAsPropertyForClass extends EqualByTypeAndPosException {
		public PropertyInitializerMustHaveSameNumberOfArgumentsAsPropertyForClass(Position p) {
			super("The property initializer must have the same number of arguments as properties for the class.", p);
		}
	}
	public static class ClockedAsyncMustBeInvokedInsideAStaticallyEnclosingClockedFinish extends EqualByTypeAndPosException {
		public ClockedAsyncMustBeInvokedInsideAStaticallyEnclosingClockedFinish(Position p) {
			super("Clocked async must be invoked inside a statically enclosing clocked finish.", p);
		}
	}
	public static class TypeMustBeX10LangClock extends EqualByTypeAndPosException {
		public TypeMustBeX10LangClock(Type t, Position p) {
			super("Type \"" + t + "\" must be x10.lang.clock.", p);
		}
	}
	public static class CannotOccurOutsideCodeBody extends EqualByTypeAndPosException {
		public static enum Element { Closure, At, Async};
		public CannotOccurOutsideCodeBody(Element str, Position p) {
			super(str + " cannot occur outside code body.", p);
		}
	}
	public static class TypeConstraintMustBeBoolean extends EqualByTypeAndPosException {
		public TypeConstraintMustBeBoolean(Expr e, Type t, Position p) {
			super("The type of the constraint "+ e + " must be boolean, not " + t + ".", p);
		}
	}
	public static class DomainIteratedForLoopMustBeLocal extends EqualByTypeAndPosException {
		public DomainIteratedForLoopMustBeLocal(Position p) {
			super("The domain of this iterated for loop must be local", p);
		}
	}
	public static class ConstraintInconsistency extends EqualByTypeAndPosException {
		public ConstraintInconsistency(XFailure e, Position p) {
			super("Constraint on here is inconsistent; " + e.getMessage(), p);
		}
	}
	public static class TypeDefinitionMustBeStaticClassOrInterfaceMembers extends EqualByTypeAndPosException {
		public TypeDefinitionMustBeStaticClassOrInterfaceMembers(Position p) {
			super("Type definitions must be static class or interface members.  This is a limitation of the current implementation.", p);
		}
	}
	public static class CannotCompareUnsignedVersusSignedValues extends EqualByTypeAndPosException {
		public CannotCompareUnsignedVersusSignedValues(Position p) {
			super("Cannot compare unsigned versus signed values.",p);
		}
	}
	public static class CannotCompareSignedVersusUnsignedValues extends EqualByTypeAndPosException {
		public CannotCompareSignedVersusUnsignedValues(Position p) {
			super("Cannot compare signed versus unsigned values.",p);
		}
	}
	public static class OperatorMustHaveOperandsOfComparabletype extends EqualByTypeAndPosException {
		public OperatorMustHaveOperandsOfComparabletype(Type lbase, Type rbase, Position p) {
			super("Operator must have operands of comparable type; the types " + lbase + 
					" and " + rbase + " do not share any values.", 
					p);
		}
	}
	public static class NoOperationFoundForOperands extends EqualByTypeAndPosException {
		public NoOperationFoundForOperands(Operator op, Type l, Type r, Position p) {
			super("No operation " + op + " found for operands " + l + " and " + r + ".", p);
		}
	}
	public static class ArgumentOfWhenMustBeBoolean extends EqualByTypeAndPosException {
		public ArgumentOfWhenMustBeBoolean(Position p) {
			super("The type of the argument of a 'when' statement must be Boolean", p);
		}
	}
	public static class InvalidType extends EqualByTypeAndPosException {
		public InvalidType(Type t, Position p) {
			super("Invalid type; the real clause of " + t + " is inconsistent.", p);
		}
	}
	public static class TypeInconsistent extends EqualByTypeAndPosException {
		public TypeInconsistent(Type t, Position p) {
			super("Type " + t + " is inconsistent.", p);
		}
	}
	public static class CannotReferToTypeParameterFromStaticContext extends EqualByTypeAndPosException {
		public CannotReferToTypeParameterFromStaticContext(ParameterType pt, Def def, Position p) {
			super("Cannot refer to type parameter "+ pt.fullName() + " of " + def + " from a static context.", p);
		}
	}
	public static class CannotQualifyTypeParameter extends EqualByTypeAndPosException {
		public CannotQualifyTypeParameter(ParameterType pt, Def def, Flags flags, Position p) {
			super("Cannot qualify type parameter "+ pt.fullName() + " of " + def + " with flags " + flags + ".", p);
		}
	}
	public static class DublicateConstructor extends EqualByTypeAndPosException {
		public DublicateConstructor(X10ConstructorDef cj, X10ConstructorDef ci, Position p) {
			super("Duplicate constructor \"" + cj + "\"; previous declaration at " + ci.position() + ".", p);
		}
	}
	public static class DuplicateMethod extends EqualByTypeAndPosException {
		public DuplicateMethod(X10MethodDef mj, X10MethodDef mi, Position p) {
			super("Duplicate method \"" + mj + "\"; previous declaration at " + mi.position() + ".", p);
		}
	}
	public static class PublicTypeMustBeDeclaredInX10 extends EqualByTypeAndPosException {
		public PublicTypeMustBeDeclaredInX10(ClassDef type, Position p) {
			super("Public type " + type.fullName() + " must be declared in " + type.name() + ".x10.", p);
		}
	}
	public static class InterfaceCannotHaveSuperclass extends EqualByTypeAndPosException {
		public InterfaceCannotHaveSuperclass(ClassDef type, Position p) {
			super("Interface " + type + " cannot have a superclass.", p);
		}
	}
	public static class ClassCannotOerridePropertyOfSuperclass extends EqualByTypeAndPosException {
		public ClassCannotOerridePropertyOfSuperclass(ClassDef type, FieldInstance fi, Position p) {
			super(type + " cannot override property " 
            		+ fi.name() + " of superclass " + Types.get(fi.def().container()) + ".", p);
		}
	}
	public static class CanOnlyQualifySuperConstructorInvocation extends EqualByTypeAndPosException {
		public CanOnlyQualifySuperConstructorInvocation(Position p) {
			super("Can only qualify a \"super\" constructor invocation.", p);
		}
	}
	public static class ClassNotInnerClass extends EqualByTypeAndPosException {
		public ClassNotInnerClass(Type superType, Position p) {
			super("The class \"" + superType + "\" is not an inner class, or was declared in a static context; a qualified constructor invocation cannot be used.", p);
		}
	}
	public static class QualifierDoesNotMatchEnclosingClass extends EqualByTypeAndPosException {
		public QualifierDoesNotMatchEnclosingClass(Type qt, ContainerType container, Position p) {
			super("The type of the qualifier \"" + qt + "\" does not match the immediately enclosing class of the super class \"" + container + "\".", p);
		}
	}
	public static class ConstructorsCannotHaveTypeParameters extends EqualByTypeAndPosException {
		public ConstructorsCannotHaveTypeParameters(Position p) {
			super("Constructors cannot have type parameters.", p);
		}
	}
	public static class ReturnTypeOfConstructorMustBeFromTypeOfClass extends EqualByTypeAndPosException {
		public ReturnTypeOfConstructorMustBeFromTypeOfClass(Type retTypeBase, Type clazz, Position p) {
			super("The return type of the constructor (" + retTypeBase + ") must be derived from the type of the class (" + clazz + ") on which the constructor is defined.",    p);
		}
	}
	public static class CannotInferFieldType extends EqualByTypeAndPosException {
		public CannotInferFieldType(Position p) {
			super("Cannot infer field type; field has no initializer.", p);
		}
	}
	public static class CannotInferNonFinalFieldType extends EqualByTypeAndPosException {
		public CannotInferNonFinalFieldType(Position p) {
			super("Cannot infer type of non-final fields.", p);
		}
	}
	public static class CannotDeclareStaticNonFinalField extends EqualByTypeAndPosException {
		public CannotDeclareStaticNonFinalField(Position p) {
			super("Cannot declare static non-final field.", p);
		}
	}
	public static class IllegalFieldDefinition extends EqualByTypeAndPosException {
		public IllegalFieldDefinition(FieldDef fi, Position p) {
			super("Illegal " + fi +  "; structs cannot have var fields.",p);
		}
	}
	public static class FieldCannotHaveType extends EqualByTypeAndPosException {
		public FieldCannotHaveType(Type type, Position p) {
			super("Field cannot have type " + type + ".", p);
		}
	}
	public static class StructMayNotHaveVarFields extends EqualByTypeAndPosException {
		public StructMayNotHaveVarFields(Position p) {
			super("A struct may not have var fields.", p);
		}
	}
	public static class StaticFieldMustHaveInitializer extends EqualByTypeAndPosException {
		public StaticFieldMustHaveInitializer(Id name, Position p) {
			super("Static field "+name+" must have an initializer.", p);
		}
	}
	public static class TransientFieldMustHaveTypeWithDefaultValue extends EqualByTypeAndPosException {
		public TransientFieldMustHaveTypeWithDefaultValue(Id name, Position p) {
			super("The transient field '"+ name +"' must have a type with a default value.", p);
		}
	}
	public static class LocalVaraibleMultiplyDefined extends EqualByTypeAndPosException {
		public LocalVaraibleMultiplyDefined(Id name, Position outerP, Position p) {
			super("Local variable \"" + name + "\" multiply defined. Previous definition at " + outerP + ".", p);
		}
	}
	public static class CannotInferTypeForFormalParameter extends EqualByTypeAndPosException {
		public CannotInferTypeForFormalParameter(Id name, Position p) {
			super("Could not infer type for formal parameter " + name + ".", p);
		}
	}
	public static class FormalParameterCannotHaveType extends EqualByTypeAndPosException {
		public FormalParameterCannotHaveType(Type type, Position p) {
			super("Formal parameter cannot have type " + type + ".", p);
		}
	}
	public static class LocalVariableAccessedFromInnerClass extends EqualByTypeAndPosException {
		public LocalVariableAccessedFromInnerClass(polyglot.types.Name liName, Position p) {
			super("Local variable \"" + liName +"\" is accessed from an inner class or a closure, and must be declared final.", p);
		}
	}
	public static class LocalVariableCannotBeCapturedInAsync extends EqualByTypeAndPosException {
		public LocalVariableCannotBeCapturedInAsync(polyglot.types.Name liName, Position p) {
			super("Local variable \"" + liName + 
					"\" cannot be captured in an async if there is no enclosing finish in the same scoping-level as \"" + liName +
					"\"; consider changing \"" + liName +"\" from var to val.", p);
		}
	}
	public static class LocalVariableAccessedAtDifferentPlace extends EqualByTypeAndPosException {
		public LocalVariableAccessedAtDifferentPlace(polyglot.types.Name liName, Position p) {
			super("Local variable \"" + liName +"\" is accessed at a different place, and must be declared final.", p);
		}
	}
	public static class CannotInferTypeofMutalVariable extends EqualByTypeAndPosException {
		public CannotInferTypeofMutalVariable(Position p) {
			super("Cannot infer type of a mutable (non-val) variable.", p);
		}
	}
	public static class CannotInferVariableType extends EqualByTypeAndPosException {
		public CannotInferVariableType(polyglot.types.Name name, Position p) {
			super("Cannot infer variable type; variable "+ name +" has no initializer.", p);
		}
	}
	public static class LocalVariableCannotHaveType extends EqualByTypeAndPosException {
		public LocalVariableCannotHaveType(Type type, Position p) {
			super("Local variable cannot have type " + type + ".", p);
		}
	}
	public static class LoopDomainIsNotOfExpectedType extends EqualByTypeAndPosException {
		public LoopDomainIsNotOfExpectedType(ConstrainedType formalType, Type domainType, Position p) {
			super("Loop domain is not of expected type." 
	                + "\n\t Expected type: Iterable[" + formalType + "]" 
	                + "\n\t Actual type: " + domainType, p);
		}
	}
	public static class CannotInferMethodReturnType extends EqualByTypeAndPosException {
		public CannotInferMethodReturnType(Position p) {
			super("Cannot infer method return type; method has no body.", p);
		}
	}
	public static class MissingConstructorBody extends EqualByTypeAndPosException {
		public MissingConstructorBody(Position p) {
			super("Missing constructor body.", p);
		}
	}
	public static class NativeConstructorCannotHaveABody extends EqualByTypeAndPosException {
		public NativeConstructorCannotHaveABody(Position p) {
			super("A native constructor cannot have a body.", p);
		}
	}
	public static class LiteralOutOfRange extends EqualByTypeAndPosException {
		public LiteralOutOfRange(String str, long value, Position p) {
			super(str + " literal " + value + " is out of range.", p);
		}
	}
	public static class NonAbstractPropertyMethodMustBeFinal extends EqualByTypeAndPosException {
		public NonAbstractPropertyMethodMustBeFinal(Position p) {
			super("A non-abstract property method must be final.", p);
		}
	}
	public static class PropertyMethodCannotBeStatic extends EqualByTypeAndPosException {
		public PropertyMethodCannotBeStatic(Position p) {
			super("A property method cannot be static.", p);
		}
	}
	public static class PropertyMethodCannotHaveGuard extends EqualByTypeAndPosException {
		public PropertyMethodCannotHaveGuard(DepParameterExpr guard, Position p) {
			super("A property method cannot have a guard.", guard != null ? guard.position() : p);
		}
	}
	public static class InconsistentContext extends EqualByTypeAndPosException {
	    public InconsistentContext(Type ct, Position p) {
	        super("Context for type " + ct + " is inconsistent.", p);
	    }
	    public InconsistentContext(CConstraint c, Position p) {
	        super("Context becomes inconsistent when " + c + " is added.", p);
	    }
	}
	public static class InconsistentType extends EqualByTypeAndPosException {
        public InconsistentType(Type t, Position p) {
            super("The type " + t + " is inconsistent.", p);
        }
    }
	public static class CouldNotFindEnclosingClass extends EqualByTypeAndPosException {
        public CouldNotFindEnclosingClass(polyglot.types.Name name, Position p) {
            super("Could not find enclosing class or package for type definition \"" + name + "\".", p);
        }
    }
	public static class TypeIsconsistent extends EqualByTypeAndPosException {
        public TypeIsconsistent(Type t, Position p) {
            super("Type " + t + " is inconsistent.", p);
        }
    }
	public static class SuperTypeIsNotAClass extends EqualByTypeAndPosException {
        public SuperTypeIsNotAClass(ClassType ct, Position p) {
            super("Super type of " + ct + " is not a class.", p);
        }
    }
	public static class ClassTypeMustHaveEnclosingInstance extends EqualByTypeAndPosException {
        public ClassTypeMustHaveEnclosingInstance(ClassType ct, ClassType superContainer, Position p) {
            super(ct + " must have an enclosing instance that is a subtype of " + superContainer, p);
        }
    }
	public static class ClassTypeMustBeSpecifiedInSuperConstructor extends EqualByTypeAndPosException {
        public ClassTypeMustBeSpecifiedInSuperConstructor(ClassType ct, ClassType superContainer, Position p) {
            super(ct + " is a subtype of " + superContainer +
            		"; an enclosing instance that is a subtype of " + superContainer + 
            		" must be specified in the super constructor call.", p);
        }
    }
	public static class CannotAccessField extends EqualByTypeAndPosException {
        public CannotAccessField(Id name, X10ClassType tCt, Position p) {
            super("Cannot access field " + name + " of " + tCt+ 
            		" in class declaration header; the field may be a member of a superclass.", p);
        }
    }
	public static class UnableToFindImplementingPropertyMethod extends EqualByTypeAndPosException {
        public UnableToFindImplementingPropertyMethod(polyglot.types.Name name, Position p) {
            super("Unable to find the implementing property method for interface property "+name, p);
        }
    }
	public static class OnlyTypePointOrArrayCanBeExploded extends EqualByTypeAndPosException {
        public OnlyTypePointOrArrayCanBeExploded(Type myType, Position p) {
            super("Only a formal of type Point or Array can be exploded, however the formal's type is "+myType, p);
        }
    }
	public static class LocalVariableNotAllowedInContainer extends EqualByTypeAndPosException {
        public LocalVariableNotAllowedInContainer(polyglot.types.Name liName, Position p) {
            super("A var local variable " + liName
					+ " is not allowed in a constraint.", 
					p);
        }
    }
	public static class MethodBodyMustBeConstraintExpressiong extends EqualByTypeAndPosException {
        public MethodBodyMustBeConstraintExpressiong(Position p) {
            super("Property method body must be a constraint expression.", p);
        }
    }
	public static class MustHaveSameClassAsContainer extends EqualByTypeAndPosException {
        public MustHaveSameClassAsContainer(Position p) {
            super("The return type or the formal type of an explicit or implicit operator 'as' " +
            		"must have the same class as the container.", p);
        }
    }
	public static class TypeParameterMultiplyDefined extends EqualByTypeAndPosException {
        public TypeParameterMultiplyDefined(polyglot.types.Name name, Position p) {
            super("Type parameter \"" + name + "\" multiply defined.", p);
        }
    }
	public static class LocalVariableMultiplyDefined extends EqualByTypeAndPosException {
        public LocalVariableMultiplyDefined(polyglot.types.Name name, Position p) {
            super("Local variable \"" + name + "\" multiply defined.", p);
        }
    }
	public static class CouldNotFindNonStaticMemberClass extends EqualByTypeAndPosException {
        public CouldNotFindNonStaticMemberClass(polyglot.types.Name name, Position p) {
            super("Could not find non-static member class \"" + name + "\".", p);
        }
    }
	public static class OnlySimplyNameMemberClassMayBeInstantiated extends EqualByTypeAndPosException {
        public OnlySimplyNameMemberClassMayBeInstantiated(Position p) {
            super("Only simply-named member classes may be instantiated by a qualified new expression.", p);
        }
    }
	public static class CannotInstantiateMemberClass extends EqualByTypeAndPosException {
        public CannotInstantiateMemberClass(Position p) {
            super("Cannot instantiate member class of non-class type.", p);
        }
    }
	public static class CannotInstantiateType extends EqualByTypeAndPosException {
        public CannotInstantiateType(Type ct, Position p) {
            super("Cannot instantiate type " + ct + "; incorrect number of type arguments.", p);
        }
    }
	public static class MustReturnValueFromNonVoidMethod extends EqualByTypeAndPosException {
        public MustReturnValueFromNonVoidMethod(Position p) {
            super("Must return value from non-void method.", p);
        }
    }
	public static class CannotReturnValueFromVoidMethod extends EqualByTypeAndPosException {
        public CannotReturnValueFromVoidMethod(Position p) {
            super("Cannot return value from void method or closure.", p);
        }
    }
	public static class SelfMayOnlyBeUsedWithinDependentType extends EqualByTypeAndPosException {
        public SelfMayOnlyBeUsedWithinDependentType(Position p) {
            super("self may only be used within a dependent type", p);
        }
    }
	public static class CannotAccessNonStaticFromStaticContext extends EqualByTypeAndPosException {
        public CannotAccessNonStaticFromStaticContext(Position p) {
            super("Cannot access a non-static field or method, or refer to \"this\" or \"super\" from a static context.", p);
        }
    }
	public static class ConstraintOnThisIsInconsistent extends EqualByTypeAndPosException {
        public ConstraintOnThisIsInconsistent(XFailure e, Position p) {
            super("Constraint on this is inconsistent; " + e.getMessage(), p);
        }
    }
	public static class ConstraintOnSuperIsInconsistent extends EqualByTypeAndPosException {
        public ConstraintOnSuperIsInconsistent(XFailure e, Position p) {
            super("Constraint on super is inconsistent; " + e.getMessage(), p);
        }
    }
	public static class CannotApplyToFinalVariable extends EqualByTypeAndPosException {
        public CannotApplyToFinalVariable(Unary.Operator op, Position p) {
            super("Cannot apply " + op + " to a final variable.", p);
        }
    }
	public static class CannotApplyToArbitraryMethodCall extends EqualByTypeAndPosException {
        public CannotApplyToArbitraryMethodCall(Unary.Operator op, Position p) {
            super("Cannot apply " + op + " to an arbitrary method call.", p);
        }
    }
	public static class CannotApplyToArbitraryExpression extends EqualByTypeAndPosException {
        public CannotApplyToArbitraryExpression(Unary.Operator op, Position p) {
            super("Cannot apply " + op + " to an arbitrary expression.", p);
        }
    }
	public static class NoMethodFoundInType extends EqualByTypeAndPosException {
        public NoMethodFoundInType(Name name, Type type, Position p) {
            super("No "+name+" method found in " + type, p);
        }
    }
	public static class NoBinaryOperatorFoundInType extends EqualByTypeAndPosException {
        public NoBinaryOperatorFoundInType(Binary.Operator binaryOp, Type t, Position p) {
            super("No binary operator " + binaryOp + " found in type " + t, p);
        }
    }
	public static class IncompatibleReturnTypeOfBinaryOperator extends EqualByTypeAndPosException {
        public IncompatibleReturnTypeOfBinaryOperator(Binary.Operator binaryOp, Type resultType, Type et, Position p) {
            super("Incompatible return type of binary operator "+binaryOp+
            		" found:\n\t operator return type: " + resultType + 
            		"\n\t expression type: "+ et, p);
        }
    }
	public static class NoOperationFoundForOperand extends EqualByTypeAndPosException {
        public NoOperationFoundForOperand(Unary.Operator op, Type t, Position p) {
            super("No operation " + op + " found for operand " + t + ".", p);
        }
    }
	public static class UnknownType extends EqualByTypeAndPosException {
        public UnknownType(Position p) {
            super("Complaining about UnknownType", p);
        }
    }
	public static class InconsistentTypeSelf extends EqualByTypeAndPosException {
        public InconsistentTypeSelf(Type toType, XTerm sv, Position p) {
            super("Inconsistent type: " + toType + " {self==" + sv+"}", p);
        }
    }
	public static class AnnotationMustImplementType extends EqualByTypeAndPosException {
		public static enum Element { types, expressions, statements, method_declarations, field_declarations,
			class_declarations, package_declarations, imports;
			public String toString() {
				return name().replace('_', ' ');
			}
		};
        public AnnotationMustImplementType(X10ClassType at, Element element, Type type, Position p) {
            super("Annotation "+ at +" on " + element + " must implement " + type, p);
        }
        public AnnotationMustImplementType(X10ClassType at, Type type, Position p) {
            super("Annotation "+ at +" must implement " + type, p);
        }
    }
	public static class GeneralError extends EqualByTypeAndPosException {
		public GeneralError(String str, Position p) {
            super(str, p);
        }
    }
	public static class RecursiveTypeDefinition extends EqualByTypeAndPosException {
		public RecursiveTypeDefinition(Position p) {
            super("Recursive type definition; type definition depends on itself.", p);
        }
    }
	public static class MethodsOverrideWithCompatibleSignatures extends EqualByTypeAndPosException {
		public MethodsOverrideWithCompatibleSignatures(MethodInstance mj, MethodInstance mi, Position p) {
            super("Method " + mj.signature() + " in " + mj.container() + " and method " + mi.signature() + " in " + mi.container()
                    + " override methods with compatible signatures.", p);
        }
    }
	public static class DumplicateTypeDefinition extends EqualByTypeAndPosException {
		public DumplicateTypeDefinition(TypeDef mj, TypeDef mi, Position p) {
            super("Duplicate type definition \"" + mj + "\"; previous declaration at " + mi.position() + ".", p);
        }
    }
	public static class TypeDefinitionSameNameAsMemberClass extends EqualByTypeAndPosException {
		public TypeDefinitionSameNameAsMemberClass(TypeDef mi, Type ct, Position p) {
            super("Type definition " + mi + " has the same name as member class " + ct + ".", p);
        }
    }
	public static class ClockedLoopMayOnlyBeClockedOnClock extends EqualByTypeAndPosException {
		public ClockedLoopMayOnlyBeClockedOnClock(Position p) {
            super("Clocked loop may only be clocked on a clock.", p);
        }
    }
	public static class TernaryExpressiongMustBeBoolean extends EqualByTypeAndPosException {
		public TernaryExpressiongMustBeBoolean(Position p) {
            super("Condition of ternary expression must be of type boolean.", p);
        }
    }
	public static class ConstructorGuardNotSatisfied extends EqualByTypeAndPosException {
		public ConstructorGuardNotSatisfied(Position p) {
            super("The constructor guard was not satisfied.", p);
        }
    }
	public static class DoStatementMustHaveBooleanType extends EqualByTypeAndPosException {
		public DoStatementMustHaveBooleanType(Type type, Position p) {
            super("Condition of do statement must have boolean type, and not " + type + ".", p);
        }
    }
	public static class StructsCircularity extends EqualByTypeAndPosException {
		public StructsCircularity(Position p) {
            super("Circularity in the usage of structs will cause this field to have infinite size. Use a class instead of a struct.",p);
        }
    }
	public static class IfStatementMustHaveBooleanType extends EqualByTypeAndPosException {
		public IfStatementMustHaveBooleanType(Type type, Position p) {
            super("Condition of if statement must have boolean type, and not " + type + ".", p);
        }
    }
	public static class CannotReturnFromAsync extends EqualByTypeAndPosException {
		public CannotReturnFromAsync(Position p) {
            super("Cannot return from an async.",p);
        }
    }
	public static class SourceContainsMoreThanOnePublicDeclaration extends EqualByTypeAndPosException {
		public SourceContainsMoreThanOnePublicDeclaration(Position p) {
            super("The source contains more than one public declaration.", p);
        }
    }
	public static class CannotReferToSuperFromDeclarationHeader extends EqualByTypeAndPosException {
		public CannotReferToSuperFromDeclarationHeader(Position p) {
            super("Cannot refer to \"super\" from within a class or interface declaration header.", p);
        }
    }
	public static class NestedClassMissingEclosingInstance extends EqualByTypeAndPosException {
		public NestedClassMissingEclosingInstance(X10ClassType c, Type ct, Position p) {
            super("The nested class \"" 
                    +c 
                    + "\" does not have an enclosing instance of type \"" 
                    +ct + "\".", p);
        }
    }
	public static class InvalidQualifierForSuper extends EqualByTypeAndPosException {
		public InvalidQualifierForSuper(Position p) {
            super("Invalid qualifier for \"this\" or \"super\".", p);
        }
    }
	public static class WhileStatementMustHaveBooleanType extends EqualByTypeAndPosException {
		public WhileStatementMustHaveBooleanType(Type type, Position p) {
            super("Condition of while statement must have boolean type, and not " + type + ".", p);
        }
    }
	public static class MaxMacroExpansionDepth extends EqualByTypeAndPosException {
		public MaxMacroExpansionDepth(Type t, Position p) {
            super("Reached max macro expansion depth with " + t, p);
        }
    }
}
