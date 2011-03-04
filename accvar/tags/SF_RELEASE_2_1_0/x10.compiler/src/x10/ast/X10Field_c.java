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

package x10.ast;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Field_c;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.ast.Special;
import polyglot.ast.TypeNode;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.util.ErrorInfo;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;

import x10.constraint.XVar;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;
import x10.types.ConstrainedType;
import x10.types.ParameterType;
import x10.types.ParametrizedType_c;
import x10.types.X10ClassType;
import x10.types.X10Context;
import x10.types.X10FieldInstance;
import x10.types.X10Flags;
import x10.types.X10MethodInstance;
import x10.types.X10TypeSystem_c;

import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.checker.Checker;
import x10.types.checker.PlaceChecker;
import x10.types.constraints.CConstraint;
import x10.types.matcher.Subst;
import x10.visit.X10TypeChecker;
import x10.errors.Errors;


/**
 * An immutable representation of an X10 Field access. 
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

	public X10Field_c reconstruct(Receiver target, Id name) {
	    return (X10Field_c) super.reconstruct(target, name);
	}
	public Node typeCheck(ContextVisitor tc) {
		Node n;
		try {
		    n = typeCheck1(tc);		    
		} catch (SemanticException e) {
		    Errors.issue(tc.job(), e, this);
		    Type tType = target != null ? target.type() : tc.context().currentClass();
		    X10FieldInstance fi = findAppropriateField(tc, tType, name.id(), target instanceof TypeNode, e);
		    n = (X10Field_c)fieldInstance(fi).type(fi.type());
		}
		return n;
	}

    public static X10FieldInstance findAppropriateField(ContextVisitor tc,
            Type targetType, Name name, boolean isStatic, boolean receiverInContext) {
        X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
        X10Context c = (X10Context) tc.context();
        X10FieldInstance fi = null;
        try {
            // vj: Hack to work around the design decision to represent "here" as this.home for
            // instance methods. This decision creates a problem for non-final variables that are 
            // located in the current place. "this" is going to get quantified out by the FieldMatcher.
            // therefore we temporarily replace this.home with a new UQV, currentPlace, and then on
            // return from the matcher, substitute it back in.
            XTerm placeTerm = c.currentPlaceTerm()==null ? null: c.currentPlaceTerm().term();
            XVar currentPlace = XTerms.makeUQV("place");
            Type tType2 = placeTerm==null ? targetType : Subst.subst(targetType, currentPlace, (XVar) placeTerm);
            fi = (X10FieldInstance) ts.findField(targetType, ts.FieldMatcher(tType2, receiverInContext, name, c));
            if (isStatic && !fi.flags().isStatic())
                throw new SemanticException("Cannot access non-static field "+name+" in static context");
            assert (fi != null);
            // substitute currentPlace back in.
            fi = placeTerm == null ? fi : Subst.subst(fi, placeTerm, currentPlace);
        } catch (SemanticException e) {
            fi = findAppropriateField(tc, targetType, name, isStatic, e);
        }
        return fi;
    }

	private static X10FieldInstance findAppropriateField(ContextVisitor tc, Type targetType,
	        Name name, boolean isStatic, SemanticException e)
	{
	    X10FieldInstance fi;
	    X10TypeSystem_c xts = (X10TypeSystem_c) tc.typeSystem();
	    Context context = tc.context();
	    boolean haveUnknown = xts.hasUnknown(targetType);
	    Set<FieldInstance> fis = xts.findFields(targetType, xts.FieldMatcher(targetType, name, context));
	    // If exception was not thrown, there is at least one match.  Fake it.
	    // See if all matches have the same type, and save that to avoid losing information.
	    Type rt = null;
	    for (FieldInstance xfi : fis) {
	        if (rt == null) {
	            rt = xfi.type();
	        } else if (!xts.typeEquals(rt, xfi.type(), context)) {
	            if (xts.typeBaseEquals(rt, xfi.type(), context)) {
	                rt = X10TypeMixin.baseType(rt);
	            } else {
	                rt = null;
	                break;
	            }
	        }
	    }
	    // See if all matches have the same container, and save that to avoid losing information.
	    Type ct = null;
	    for (FieldInstance xfi : fis) {
	        if (ct == null) {
	            ct = xfi.container();
	        } else if (!xts.typeEquals(ct, xfi.container(), context)) {
	            if (xts.typeBaseEquals(ct, xfi.container(), context)) {
	                ct = X10TypeMixin.baseType(ct);
	            } else {
	                ct = null;
	                break;
	            }
	        }
	    }
	    if (ct != null) targetType = ct;
	    Flags flags = Flags.PUBLIC;
	    if (isStatic) flags = flags.Static();
	    if (haveUnknown)
	        e = new SemanticException(); // null message
	    fi = xts.createFakeField(targetType.toClass(), flags, name, e);
	    if (rt == null) rt = fi.type();
	    rt = PlaceChecker.AddIsHereClause(rt, context);
	    fi = fi.type(rt);
	    return fi;
	}
	
    // Fix XTENLANG-945
    public static boolean isInterfaceProperty(Type targetType, FieldInstance fi) {
        boolean isInterfaceProperty = false;

        if (X10Flags.toX10Flags(fi.flags()).isProperty()) {
            // check if the target is interface
            Type baseType = targetType;
            while (baseType instanceof ConstrainedType) {
                baseType = ((ConstrainedType) baseType).baseType().get();
            }
            Flags flags = null;
            if (baseType instanceof ClassType) {
                flags = ((ClassType) baseType).flags();
            } else if (baseType instanceof ParametrizedType_c) {
                // TODO add flags() to ParametrizedType and use it
                flags = ((ParametrizedType_c) baseType).flags();
            }
            if (flags != null) {
                isInterfaceProperty = flags.isInterface();
            }
        }

        return isInterfaceProperty;
    }
	
    public Node typeCheck1(ContextVisitor tc) throws SemanticException {
		final X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
		final X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
		final X10Context c = (X10Context) tc.context(); 
		Type tType = target != null ? target.type() : c.currentClass();

		if (target instanceof TypeNode) {
			Type t = ((TypeNode) target).type();
			t = X10TypeMixin.baseType(t);
			if (t instanceof ParameterType) {
				throw new Errors.CannotAccessStaticFieldOfTypeParameter(t, 
						position());
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
								Type t = c.inDepType()? Checker.rightType(fi.rightType(), fi.x10Def(), target, c)
										: fi.rightType(); //  fieldRightType(fi.rightType(), fi.x10Def(), target, c);
								result = (X10Field_c) result.fieldInstance(fi).type(t);
								return result;
							}
						}
					}

					throw new SemanticException("Cannot access field " + name + " of " + tCt+ " in class declaration header; the field may be a member of a superclass.",position());
				}
			}
		}

		X10FieldInstance fi = findAppropriateField(tc, tType, name.id(),
		        target instanceof TypeNode, X10TypeMixin.contextKnowsType(target));

        if (fi.error() != null) {
            if (target instanceof Expr) {
                Position pos = position();

                // Now try 0-ary property methods.
                try {
                    X10MethodInstance mi = ts.findMethod(target.type(), ts.MethodMatcher(target.type(), name.id(), Collections.<Type>emptyList(), c));
                    if (X10Flags.toX10Flags(mi.flags()).isProperty()) {
                        Call call = nf.Call(pos, target, this.name);
                        call = call.methodInstance(mi);
                        Type nt =  c.inDepType() ? 
                                Checker.rightType(mi.rightType(), mi.x10Def(), target, c)
                                : 
                                	 Checker.expandCall(mi.rightType(), call, c);
                            
                        call = (Call) call.type(nt);
                        return call;
                    }
                }
                catch (SemanticException ex) {
                }
            }
            throw fi.error();
        }

//		// Fix XTENLANG-945 (alternative common fix)
//		if (isInterfaceProperty(tType, fi)) {
//			throw new NoMemberException(NoMemberException.FIELD, "interface property access will be translated to property method call");
//		}

		Type type = c.inDepType()? Checker.rightType(fi.rightType(), fi.x10Def(), target, c) :
			Checker.fieldRightType(fi.rightType(), fi.x10Def(), target, c);

		Type retType = type;

		// Substitute in the actual target for this.  This is done by findField, now.
//		Type thisType = tType;
//		CConstraint rc = X10TypeMixin.realX(retType);
//		if (rc != null) {
//			XVar var= X10TypeMixin.selfVar(thisType);
//			if (var == null) 
//				var = ts.xtypeTranslator().genEQV(rc, thisType);
//			CConstraint newRC = rc.substitute(var, ts.xtypeTranslator().transThis(thisType));
//			retType = X10TypeMixin.xclause(retType, newRC);
//			fi = fi.type(retType);
//		}
		X10Field_c result = (X10Field_c)fieldInstance(fi).type(retType);
		result.checkConsistency(c);

		checkFieldAccessesInDepClausesAreFinal(result, tc);
		checkClockedFieldAccessesAreInClockedMethods(result,tc);
		// Not needed in the orthogonal locality proposal.
		// result = PlaceChecker.makeFieldAccessLocalIfNecessary(result, tc);

		//System.err.println("X10Field_c: typeCheck " + result+ " has type " + result.type());
		return result;
	}

	/**
	 * Check that if this field is a clocked field, it is being accessed from within a clocked method.
	 * @param result
	 * @param tc
	 * @throws SemanticException
	 */
	protected void checkClockedFieldAccessesAreInClockedMethods(X10Field_c result, ContextVisitor tc) 
	throws SemanticException {
		//		 Check that field accesses in dep clauses refer to final fields.
		X10Context xtc = (X10Context) tc.context();
		if (X10Flags.toX10Flags(result.flags()).isClocked() 
				&& ! ((X10Context) tc.context()).isClocked()) {
			throw new Errors.IllegalClockedAccess(this, position());
		}
	}

	private static final boolean ENABLE_PLACE_TYPES = true;

	protected void checkFieldAccessesInDepClausesAreFinal(X10Field_c result, ContextVisitor tc) 
	throws SemanticException {
		//		 Check that field accesses in dep clauses refer to final fields.
		X10Context xtc = (X10Context) tc.context();
		if (xtc.inDepType()) {
			FieldInstance fi = result.fieldInstance();
			if (! fi.flags().contains(Flags.FINAL))
				throw new Errors.DependentClauseErrorFieldMustBeFinal(this, 
						position());
			if ((target instanceof X10Special) &&
					((X10Special)target).kind()==X10Special.SELF) {
				// The fieldInstance must be a property.
				//Report.report(1, "X10Field_c checking " + fi  + " is a property. ");
				// The following is going to look for property propertyNames$
				// and may throw a MissingDependencyException asking for the field to be set.
				if (! (fi instanceof X10FieldInstance && ((X10FieldInstance) fi).isProperty()))
					throw new Errors.DependentClauseErrorSelfMayAccessOnlyProperties(fi,
							result.position());
			}
		}
	}
}
