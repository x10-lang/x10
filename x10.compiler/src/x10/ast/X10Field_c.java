/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
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
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Special;
import polyglot.ast.TypeNode;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.ContainerType;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.ErrorInfo;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;

import x10.constraint.XVar;
import x10.constraint.XTerm;
import x10.types.constraints.ConstraintManager;
import x10.constraint.XVar;
import x10.types.ParameterType;
import x10.types.ParametrizedType_c;
import x10.types.X10ClassType;
import polyglot.types.Context;
import x10.types.X10FieldInstance;

import x10.types.MethodInstance;


import polyglot.types.TypeSystem;
import x10.types.checker.Checker;
import x10.types.checker.PlaceChecker;
import x10.types.constraints.CConstraint;
import x10.types.matcher.Subst;
import x10.types.matcher.X10FieldMatcher;
import x10.visit.X10TypeChecker;
import x10.errors.Errors;
import x10.errors.Errors.IllegalConstraint;


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

	@Override
	public X10FieldInstance fieldInstance() {
	    return (X10FieldInstance) super.fieldInstance();
	}

	public X10Field_c reconstruct(Receiver target, Id name) {
	    return (X10Field_c) super.reconstruct(target, name);
	}

    public static X10FieldInstance findAppropriateField(ContextVisitor tc,
            Type targetType, Name name, boolean isStatic, boolean receiverInContext, Position pos) {
        TypeSystem ts = (TypeSystem) tc.typeSystem();
        Context c = (Context) tc.context();
        X10FieldInstance fi = null;
        try {
            //// vj: Hack to work around the design decision to represent "here" as this.home for
            //// instance methods. This decision creates a problem for non-final variables that are 
            //// located in the current place. "this" is going to get quantified out by the FieldMatcher.
            //// therefore we temporarily replace this.home with a new UQV, currentPlace, and then on
            //// return from the matcher, substitute it back in.
            //XTerm placeTerm = c.currentPlaceTerm()==null ? null: c.currentPlaceTerm().term();
            //XVar currentPlace = XTerms.makeUQV("place");
            //Type tType2 = placeTerm==null ? targetType : Subst.subst(targetType, currentPlace, (XVar) placeTerm);
            //fi = (X10FieldInstance) ts.findField(targetType, tType2, name, c, receiverInContext);
            // IP: we may still need the above hack because "here" is this.home in field initializers
            fi = (X10FieldInstance) ts.findField(targetType, targetType, name, c, receiverInContext);
            if (isStatic && !fi.flags().isStatic())
                throw new Errors.CannotAccessNonStaticFromStaticContext(fi, pos);
            assert (fi != null);
            //// substitute currentPlace back in.
            //fi = placeTerm == null ? fi : Subst.subst(fi, placeTerm, currentPlace);
        } catch (SemanticException e) {
            fi = findAppropriateField(tc, targetType, name, isStatic, e);
        }
        return fi;
    }

	private static X10FieldInstance findAppropriateField(ContextVisitor tc, Type targetType,
	        Name name, boolean isStatic, SemanticException e)
	{
	    X10FieldInstance fi;
	    TypeSystem xts =  tc.typeSystem();
	    Context context = tc.context();
	    boolean haveUnknown = xts.hasUnknown(targetType);
	    Set<FieldInstance> fis = xts.findFields(targetType, targetType, name, context);
	    // If exception was not thrown, there is at least one match.  Fake it.
	    // See if all matches have the same type, and save that to avoid losing information.
	    Type rt = null;
	    for (FieldInstance xfi : fis) {
	        if (rt == null) {
	            rt = xfi.type();
	        } else if (!xts.typeEquals(rt, xfi.type(), context)) {
	            if (xts.typeBaseEquals(rt, xfi.type(), context)) {
	                rt = Types.baseType(rt);
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
	                ct = Types.baseType(ct);
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
	    if (!targetType.isClass()) {
	        QName tName = targetType.fullName();
	        if (tName == null) {
	        	tName = QName.make(null, targetType.toString());
	        }
	        targetType = xts.createFakeClass(tName, new SemanticException("Target type is not a class: "+targetType));
	    }
	    fi = xts.createFakeField(targetType.toClass(), flags, name, e);
	    if (rt == null) rt = fi.type();
	    rt = PlaceChecker.AddIsHereClause(rt, context);
	    fi = fi.type(rt);
	    return fi;
	}
	
    // XTENLANG-945
    public static boolean isInterfaceProperty(FieldInstance fi) {
        if (fi.flags().isProperty()) {
            // check if the container is interface
            Flags flags = getFlags(Types.baseType(fi.container()));
            if (flags != null) {
                return flags.isInterface();
            }
        }
        return false;
    }

    private static Flags getFlags(Type type) {
        if (type instanceof ClassType) {
            return ((ClassType) type).flags();
        } else if (type instanceof ParametrizedType_c) {
            // TODO add flags() to ParametrizedType and use it
            return ((ParametrizedType_c) type).flags();
        }
        return null;
    }
	
    @Override
    public Node typeCheck(ContextVisitor tc) {
		final TypeSystem ts = (TypeSystem) tc.typeSystem();
		final NodeFactory nf = (NodeFactory) tc.nodeFactory();
		final Context c = (Context) tc.context(); 
		Type tType = target != null ? target.type() : c.currentClass();

		SemanticException error = null;

		Position pos = position();
		if (target instanceof TypeNode) {
			Type t = Types.baseType(tType);
			if (t instanceof ParameterType) {
				SemanticException e = new Errors.CannotAccessStaticFieldOfTypeParameter(t, pos);
				if (error == null) { error = e; }
				Errors.issue(tc.job(), e);
			}
		}

		if (c.inSuperTypeDeclaration()) {
			Type tBase = Types.baseType(tType);
			if (tBase instanceof X10ClassType) {
				X10ClassType tCt = (X10ClassType) tBase;
				if (tCt.def() == c.supertypeDeclarationType()) {
					// The only fields in scope here are the ones explicitly declared here.
					for (FieldDef fd : tCt.x10Def().properties()) {
						if (fd.name().equals(name.id())) {
							X10FieldInstance fi = (X10FieldInstance) fd.asInstance();
							try {
					            fi = X10FieldMatcher.instantiateAccess((X10FieldInstance)fi,name.id(),tType,false,c);
							}
							catch (SemanticException e) {
							}
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
                    // the outer instance is also a property-like entity (see XTENLANG-47)
                    boolean isStatic = c.inStaticContext();
                    if (!isStatic) {
                        ClassType outer = tCt.outer();
                        while (outer!=null && !outer.flags().isStatic()) {
                            X10FieldInstance fi = findAppropriateField(tc, outer, name.id(),
                                false, Types.contextKnowsType(target), position());
                            if (fi.error()==null) {
                                Position genP = pos.markCompilerGenerated();
                                Special special = (Special) nf.This(genP, nf.CanonicalTypeNode(genP, outer)).type(outer);
                                Field result = (Field) this.target(special);
                                result = (Field) result.typeCheck(tc);
                                return result;
                            }
                            outer = outer.outer();
                        }
                    }

					SemanticException e = new Errors.CannotAccessField(name, tCt, pos);
					if (error == null) { error = e; }
					Errors.issue(tc.job(), e);
				}
			}
		}

		X10FieldInstance fi = findAppropriateField(tc, tType, name.id(),
		        target instanceof TypeNode, Types.contextKnowsType(target), position());

		if (fi.error() == null && !c.inDepType() && isInterfaceProperty(fi)) { // XTENLANG-945
		    fi = fi.error(new Errors.UnableToFindImplementingPropertyMethod(fi.name(), position()));
		}

        if (fi.error() != null) {
            if (target instanceof Expr) {
                // Now try 0-ary property methods.
                try {
                    MethodInstance mi = ts.findMethod(target.type(), 
                           ts.MethodMatcher(target.type(), name.id(), 
                                            Collections.<Type>emptyList(), c));
                    if (mi.flags().isProperty()) {
                        Call call = nf.Call(pos, target, this.name);
                        call = call.methodInstance(mi);
                        Type nt = mi.rightType();
                        if (c.inDepType()) {
                                nt = Checker.rightType(nt, mi.x10Def(), target, c);
                        } else {
                        	try {
                             nt =  Checker.expandCall(nt, call, c);
                        	} catch (IllegalConstraint z) {
                        		// ignore, we will go with mi.rightType.
                        	}
                        }
                            
                        call = (Call) call.type(nt);
                        return call;
                    }
                }
                catch (SemanticException ex) {
                }
            }
            fi.error().setPosition(position); // because in X10FieldMatcher.instantiateAccess the position is of the instance which is incorrect.
            Errors.issue(tc.job(), fi.error(), this);
        }

        if (fi.error() == null && error != null) {
            fi = fi.error(error);
        }

        if (isFieldOfThis(this)) {
            c.recordCapturedVariable(fi);
        }

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
		if (fi.error() == null) {
		    result.checkConsistency(c);
		    try {
		        checkFieldAccessesInDepClausesAreFinal(pos, target, fi, tc);
		    } catch (SemanticException e) {
		        Errors.issue(tc.job(), e);
		    }
		    try {
		        checkClockedFieldAccessesAreInClockedMethods(pos, fi, tc);
		    } catch (SemanticException e) {
		        Errors.issue(tc.job(), e);
		    }
		}

		// Not needed in the orthogonal locality proposal.
		// result = PlaceChecker.makeFieldAccessLocalIfNecessary(result, tc);

		//System.err.println("X10Field_c: typeCheck " + result+ " has type " + result.type());
		return result;
	}

	public static boolean isFieldOfThis(Field f) {
	    Receiver target = f.target();
	    if (target == null && !f.flags().isStatic()) return true;
	    return target instanceof X10Special && ((X10Special) target).qualifier() == null;
	}

	/**
	 * Check that if this field is a clocked field, it is being accessed from within a clocked method.
	 * @param fi
	 * @param tc
	 * @throws SemanticException
	 */
	protected static void checkClockedFieldAccessesAreInClockedMethods(Position pos,
	        X10FieldInstance fi, ContextVisitor tc) throws SemanticException {
		// Check that field accesses in dep clauses refer to final fields.
		Context xtc = tc.context();
		if (fi.flags().isClocked() && !xtc.isClocked()) {
			throw new Errors.IllegalClockedAccess(fi, pos);
		}
	}

	private static final boolean ENABLE_PLACE_TYPES = true;

	protected static void checkFieldAccessesInDepClausesAreFinal(Position pos, Receiver target,
	        X10FieldInstance fi, ContextVisitor tc) throws SemanticException {
		// Check that field accesses in dep clauses refer to final fields.
		Context xtc = tc.context();
		if (xtc.inDepType()) {
			if (! fi.flags().contains(Flags.FINAL))
				throw new Errors.DependentClauseErrorFieldMustBeFinal(fi, pos);
			if ((target instanceof X10Special) &&
					((X10Special)target).kind()==X10Special.SELF) {
				// The fieldInstance must be a property.
				//Report.report(1, "X10Field_c checking " + fi  + " is a property. ");
				// The following is going to look for property propertyNames$
				// and may throw a MissingDependencyException asking for the field to be set.
				if (!fi.isProperty())
					throw new Errors.DependentClauseErrorSelfMayAccessOnlyProperties(fi, pos);
			}
		}
	}
}
