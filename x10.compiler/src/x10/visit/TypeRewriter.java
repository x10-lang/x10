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

import java.util.Map;
import java.util.Set;

import polyglot.types.LocalDef;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.InternalCompilerError;
import x10.constraint.XTerm;
import x10.types.constraints.ConstraintManager;
import x10.constraint.XVar;
import x10.types.MethodInstance;
import x10.types.ParameterType;
import x10.types.X10ConstructorInstance;
import x10.types.X10FieldInstance;
import x10.types.X10LocalDef;
import x10.types.X10LocalInstance;

import x10.types.matcher.Subst;

public class TypeRewriter extends TypeTransformer {

    /**
     * 
     */
    private Map<Name, Name> renamingMap;
    private Map<Name, LocalDef> localDefMap;

    /**
     * @param localDefMap 
     * @param renamingMap 
     */
    TypeRewriter(Map<Name, Name> renamingMap, Map<Name, LocalDef> localDefMap) {
        this.renamingMap = renamingMap;
        this.localDefMap = localDefMap;
    }

    @Override
    protected Type transformType(Type type) {
        try {
            Set<Name> vars = renamingMap.keySet();
            XVar[] x = new XVar[vars.size() + 1];
            XTerm[] y = new XTerm[x.length];
            int i = 0;
            for (Name n : vars) {
                Name m = renamingMap.get(n);
                X10LocalDef ld = (X10LocalDef) localDefMap.get(n);
                
                x[i] = ConstraintManager.getConstraintSystem().makeLocal(ld, n.toString());
                y[i] = ConstraintManager.getConstraintSystem().makeLocal(ld, m.toString());
                ++i;
            }
            x[i] = ConstraintManager.getConstraintSystem().makeUQV(); // to force substitution
            y[i] = ConstraintManager.getConstraintSystem().makeUQV();
            type = Subst.subst(type, y, x);
        } catch (SemanticException e) {
            throw new InternalCompilerError("Cannot alpha-rename locals in type " + type, e);
        }
        return super.transformType(type);
    }

    @Override
    protected ParameterType transformParameterType(ParameterType pt) {
        // TODO: [IP] Do we need to do anything with parameter types?
        return super.transformParameterType(pt);
    }

    @Override
    protected X10LocalInstance transformLocalInstance(X10LocalInstance li) {
        Name name = renamingMap.get(li.name());
        if (name != null) {
            // List<Type> annotations = transformTypeList(li.annotations()); // TODO
            Type type = transformType(li.type());
            if (/* li.annotations() != annotations || */li.name() != name || li.type() != type) {
                li = li/* .annotations(annotations) */.name(name).type(type);
            }
        }
        return super.transformLocalInstance(li);
    }

    @Override
    protected X10FieldInstance transformFieldInstance(X10FieldInstance fi) {
        // TODO: [IP] We don't change field instances yet, but would have to for local classes
        return super.transformFieldInstance(fi);
    }

    @Override
    protected MethodInstance transformMethodInstance(MethodInstance mi) {
        // TODO: [IP] We don't change method instances yet, but would have to for local classes
        return super.transformMethodInstance(mi);
    }

    @Override
    protected X10ConstructorInstance transformConstructorInstance(X10ConstructorInstance ci) {
        // TODO: [IP] We don't change constructor instances yet, but would have to for local classes
        return super.transformConstructorInstance(ci);
    }
}