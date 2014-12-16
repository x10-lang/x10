/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2010.
 */
package x10.visit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import polyglot.ast.Call;
import polyglot.ast.Field;
import polyglot.ast.Formal;
import polyglot.ast.LocalDecl;
import polyglot.ast.Special;
import polyglot.ast.TypeNode;
import polyglot.types.LocalDef;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Pair;
import polyglot.util.SubtypeSet;
import x10.ast.DepParameterExpr;
import x10.ast.TypeParamNode;
import x10.ast.X10ClassDecl;
import x10.ast.X10ConstructorCall;
import x10.ast.X10ConstructorDecl;
import x10.ast.X10FieldDecl;
import x10.ast.X10Formal;
import x10.ast.X10MethodDecl;
import x10.constraint.XLocal;
import x10.types.ClosureInstance;
import x10.types.MethodInstance;
import x10.types.TypeParamSubst;
import x10.types.X10ConstructorDef;
import x10.types.X10ConstructorInstance;
import x10.types.X10FieldInstance;
import x10.types.X10LocalDef;
import x10.types.X10LocalInstance;
import x10.types.X10MethodDef;

import x10.types.constraints.ConstraintManager;
import x10.types.matcher.Subst;

public class Reinstantiator extends TypeParamSubstTransformer {
        public Reinstantiator(TypeParamSubst subst) {
            super(subst);
        }

        // TODO: move this up to TypeTransformer
        private Pair<XLocal[], XLocal[]> getLocalSubstitution() {
            Map<X10LocalDef, X10LocalDef> map = vars;
            XLocal[] X = new XLocal[map.keySet().size()];
            XLocal[] Y = new XLocal[X.length];
            int i = 0;
            for (X10LocalDef ld : map.keySet()) {
                X[i] = ConstraintManager.getConstraintSystem().makeLocal(ld);
                Y[i] = ConstraintManager.getConstraintSystem().makeLocal(map.get(ld));
                i++;
            }
            return new Pair<XLocal[], XLocal[]>(X, Y);
        }

        @Override
        protected X10ConstructorInstance transformConstructorInstance(X10ConstructorInstance ci) {
            Pair<XLocal[], XLocal[]> p = getLocalSubstitution();
            XLocal[] X = p.fst();
            XLocal[] Y = p.snd();
            try {
                ci = Subst.subst(ci, Y, X);
            } catch (SemanticException e) {
                throw new InternalCompilerError("Unexpected exception while reinstantiating "+ci, e);
            }
            return super.transformConstructorInstance(ci);
        }

        @Override
        protected ClosureInstance transformClosureInstance(ClosureInstance ci) {
            Pair<XLocal[], XLocal[]> p = getLocalSubstitution();
            XLocal[] X = p.fst();
            XLocal[] Y = p.snd();
            try {
                ci = Subst.subst(ci, Y, X);
            } catch (SemanticException e) {
                throw new InternalCompilerError("Unexpected exception while reinstantiating "+ci, e);
            }
            return super.transformClosureInstance(ci);
        }

        @Override
        protected X10FieldInstance transformFieldInstance(X10FieldInstance fi) {
            Pair<XLocal[], XLocal[]> p = getLocalSubstitution();
            XLocal[] X = p.fst();
            XLocal[] Y = p.snd();
            try {
                fi = Subst.subst(fi, Y, X);
            } catch (SemanticException e) {
                throw new InternalCompilerError("Unexpected exception while reinstantiating "+fi, e);
            }
            return super.transformFieldInstance(fi);
        }

        @Override
        protected X10LocalInstance transformLocalInstance(X10LocalInstance li) {
//            X10LocalDef ld = li.x10Def();
//            X10LocalDef newld = vars.get(ld);
//            if (newld == null) {
//                newld = copyLocalDef(ld); // force reinstantiation
//                mapLocal(newld, newld);
//            }
//            mapLocal(ld, newld);
            Pair<XLocal[], XLocal[]> p = getLocalSubstitution();
            XLocal[] X = p.fst();
            XLocal[] Y = p.snd();
            try {
                li = Subst.subst(li, Y, X);
            } catch (SemanticException e) {
                throw new InternalCompilerError("Unexpected exception while reinstantiating "+li, e);
            }
            return super.transformLocalInstance(li);
        }

        @Override
        protected MethodInstance transformMethodInstance(MethodInstance mi) {
            Pair<XLocal[], XLocal[]> p = getLocalSubstitution();
            XLocal[] X = p.fst();
            XLocal[] Y = p.snd();
            try {
                mi = Subst.subst(mi, Y, X);
            } catch (SemanticException e) {
                throw new InternalCompilerError("Unexpected exception while reinstantiating "+mi, e);
            }
            return super.transformMethodInstance(mi);
        }

        @Override
        protected Type transformType(Type type) {
            Pair<XLocal[], XLocal[]> p = getLocalSubstitution();
            XLocal[] X = p.fst();
            XLocal[] Y = p.snd();
            try {
                type = Subst.subst(type, Y, X);
            } catch (SemanticException e) {
                throw new InternalCompilerError("Unexpected exception while reinstantiating "+type, e);
            }
            return super.transformType(type);
        }

        @Override
        protected <T> Ref<T> transformRef(Ref<T> ref) {
            return remapRef(ref);
        }

        @Override
        protected TypeParamNode transform(TypeParamNode pn, TypeParamNode old) {
            return pn;
        }

        @Override
        protected Field transform(Field f, Field old) {
            f = f.targetImplicit(false);
            return super.transform(f, old);
        }

        @Override
        protected Call transform(Call c, Call old) {
            c = c.targetImplicit(false);
            return super.transform(c, old);
        }

        @Override
        protected X10ConstructorCall transform(X10ConstructorCall c, X10ConstructorCall old) {
            return super.transform(c, old);
        }
        @Override
        protected Special transform(Special s, Special old) {
            return super.transform(s, old);
        }

        @Override
        protected X10ClassDecl transform(X10ClassDecl d, X10ClassDecl old) {
            boolean sigChanged = d.superClass() != old.superClass();
            List<TypeNode> interfaces = d.interfaces();
            List<TypeNode> oldInterfaces = old.interfaces();
            for (int i = 0; i < interfaces.size(); i++) {
                sigChanged |= interfaces.get(i) != oldInterfaces.get(i);
            }
            if (sigChanged) {
                throw new InternalCompilerError("Inlining of code with instantiated local classes not supported");
            }
            return d;
        }

        @Override
        protected X10FieldDecl transform(X10FieldDecl d, X10FieldDecl old) {
            assert (false) : "Not yet implemented, can't instantiate " + d;
            return d;
        }

        @Override
        protected LocalDecl transform(LocalDecl d, LocalDecl old) {
            X10LocalDef ld = (X10LocalDef) d.localDef();
            X10LocalDef newld = vars.get(ld);
            if (newld == null) {
                newld = copyLocalDef(ld); // force reinstantiation
                newld.setType(d.type().typeRef());
                mapLocal(newld, newld);
            }
            mapLocal(ld, newld);
            return super.transform(d, old);
        }
        
        @Override
        protected X10Formal transform(X10Formal f, X10Formal old) {
            X10LocalDef ld = (X10LocalDef) f.localDef();
            X10LocalDef newld = vars.get(ld);
            if (newld == null) {
                newld = copyLocalDef(ld); // force reinstantiation
                newld.setType(f.type().typeRef());
                mapLocal(newld, newld);
            }
            mapLocal(ld, newld);
            return super.transform(f, old);
        }
        
        @Override
        protected X10ConstructorDecl transform(X10ConstructorDecl d, X10ConstructorDecl old) {
            boolean sigChanged = d.returnType() != old.returnType();
            List<Formal> params = d.formals();
            List<Formal> oldParams = old.formals();
            for (int i = 0; i < params.size(); i++) {
                sigChanged |= params.get(i) != oldParams.get(i);
            }
            sigChanged |= d.guard() != old.guard();
            List<Ref<? extends Type>> excTypes = new ArrayList<Ref<? extends Type>>();
            SubtypeSet excs = d.exceptions() == null ? new SubtypeSet(visitor().typeSystem()) : d.exceptions();
            SubtypeSet oldExcs = old.exceptions();
            if (null != excs) {
                for (Type et : excs) {
                    sigChanged |= !oldExcs.contains(et);
                    excTypes.add(Types.ref(et));
                }
            }
            sigChanged |= d.offerType() != old.offerType();
            if (sigChanged) {
                List<Ref<? extends Type>> argTypes = new ArrayList<Ref<? extends Type>>();
                List<LocalDef> formalNames = new ArrayList<LocalDef>();
                for (int i = 0; i < params.size(); i++) {
                    Formal p = params.get(i);
                    argTypes.add(p.type().typeRef());
                    formalNames.add(p.localDef());
                }
                return d.constructorDef(createConstructorDef(d, argTypes, excTypes, formalNames));
            }
            return d;
        }

        @Override
        protected X10MethodDecl transform(X10MethodDecl d, X10MethodDecl old) {
            boolean sigChanged = d.returnType() != old.returnType();
            List<Formal> params = d.formals();
            List<Formal> oldParams = old.formals();
            for (int i = 0; i < params.size(); i++) {
                sigChanged |= params.get(i) != oldParams.get(i);
            }
            sigChanged |= d.guard() != old.guard();
            List<Ref<? extends Type>> excTypes = new ArrayList<Ref<? extends Type>>();
            SubtypeSet excs = d.exceptions() == null ? new SubtypeSet(visitor().typeSystem()) : d.exceptions();
            SubtypeSet oldExcs = old.exceptions();
            if (null != excs) {
                for (Type et : excs) {
                    sigChanged |= !oldExcs.contains(et);
                    excTypes.add(Types.ref(et));
                }
            }
            sigChanged |= d.offerType() != old.offerType();
            if (sigChanged) {
                List<Ref<? extends Type>> argTypes = new ArrayList<Ref<? extends Type>>();
                List<LocalDef> formalNames = new ArrayList<LocalDef>();
                for (int i = 0; i < params.size(); i++) {
                    Formal p = params.get(i);
                    argTypes.add(p.type().typeRef());
                    formalNames.add(p.localDef());
                }
                return d.methodDef(createMethodDef(d, argTypes, excTypes, formalNames));
            }
            return d;
        }

        /**
         * @param d
         * @param argTypes
         * @param formalNames
         * @return
         */
        private X10MethodDef createMethodDef(X10MethodDecl d, List<Ref<? extends Type>> argTypes, List<Ref<? extends Type>> throwTypes, List<LocalDef> formalNames) {
            X10MethodDef md = d.methodDef();
            DepParameterExpr g = d.guard();
            TypeNode ot = d.offerType();
            return visitor().typeSystem().methodDef( md.position(), md.errorPosition(),
                                                     md.container(), 
                                                     md.flags(), 
                                                     d.returnType().typeRef(), 
                                                     md.name(), 
                                                     md.typeParameters(), 
                                                     argTypes, 
                                                     throwTypes,
                                                     md.thisDef(), 
                                                     formalNames, 
                                                     g == null ? null : g.valueConstraint(),
                                                     g == null ? null : g.typeConstraint(),
                                                     ot == null ? null : ot.typeRef(), 
                                                     null /* the body will never be used */ );
        }
        
        /**
         * @param d
         * @param argTypes
         * @param formalNames
         * @return
         */
        private X10ConstructorDef createConstructorDef(X10ConstructorDecl d, List<Ref<? extends Type>> argTypes, List<Ref<? extends Type>> throwTypes, List<LocalDef> formalNames) {
            X10ConstructorDef cd = d.constructorDef();
            DepParameterExpr g = d.guard();
            TypeNode ot = d.offerType();
            Ref<? extends Type> returnTypeRef;
            if (null != d.returnType()) {
                returnTypeRef = d.returnType().typeRef();
            } else {
                returnTypeRef = cd.returnType();
            }
            return visitor().typeSystem().constructorDef(
                    cd.position(), cd.errorPosition(),
                    cd.container(), 
                    cd.flags(), 
                    returnTypeRef, 
                    argTypes,
                    throwTypes,
                    cd.thisDef(), 
                    formalNames,
                    g == null ? null : g.valueConstraint(),
                    g == null ? null : g.typeConstraint(),
                    ot == null ? null : ot.typeRef() );
        }
    }