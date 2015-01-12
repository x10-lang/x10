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

package x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.types.ClassType;
import polyglot.types.CodeInstance;
import polyglot.types.LocalDef;
import polyglot.types.Name;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.VarDef;
import polyglot.types.VarInstance;
import polyglot.util.Position;
import polyglot.util.TypedList;

public class AsyncDef_c extends X10MethodDef_c implements AsyncDef {
    private static final long serialVersionUID = -592249260325162987L;

    // TODO: factor out common bits of code from here and AtDef_c
    protected Ref<? extends CodeInstance<?>> methodContainer;
    protected Ref<? extends ClassType> typeContainer;
    protected List<VarInstance<? extends VarDef>> capturedEnvironment;

    public AsyncDef_c(TypeSystem ts, Position pos,
            ThisDef thisDef,
            List<ParameterType> typeParameters,
            Ref<? extends CodeInstance<?>> methodContainer,
            Ref<? extends ClassType> typeContainer,
            boolean isStatic)
    {
        super(ts, pos.markCompilerGenerated(), pos.markCompilerGenerated(), Types.ref(ts.Runtime()),
                isStatic ? ts.Public().Static() : ts.Public(), Types.ref(ts.Void()),
                Name.make(AtDef.DUMMY_AT_ASYNC), typeParameters,
                Collections.<Ref<? extends Type>>emptyList(), Collections.<Ref<? extends Type>>emptyList(), thisDef,
                Collections.<LocalDef>emptyList(), null, null, null, null);
        this.typeContainer = typeContainer;
        this.methodContainer = methodContainer;
        this.capturedEnvironment = new ArrayList<VarInstance<? extends VarDef>>();
    }

    public AsyncDef position(Position pos) {
        AsyncDef_c n = (AsyncDef_c) copy();
        n.position = pos;
        return n;

    }

    public AsyncInstance asInstance() {
        if (asInstance == null) {
            asInstance = new AsyncInstance_c(ts, position(), Types.<AsyncDef>ref(this));
        }
        return (AsyncInstance) asInstance;
    }

    public Ref<? extends ClassType> typeContainer() {
        return typeContainer;
    }

    /**
     * @param container The container to set.
     */
    public void setTypeContainer(Ref<? extends ClassType> container) {
        this.typeContainer = container;
    }

    public Ref<? extends CodeInstance<?>> methodContainer() {
        return methodContainer;
    }

    public void setMethodContainer(Ref<? extends CodeInstance<?>> container) {
        methodContainer = container;
    }

    public List<VarInstance<? extends VarDef>> capturedEnvironment() {
        return Collections.unmodifiableList(capturedEnvironment);
    }

    public void setCapturedEnvironment(List<VarInstance<? extends VarDef>> env) {
        capturedEnvironment = TypedList.<VarInstance<? extends VarDef>>copy(env, VarInstance.class, false);
    }
    
    public void addCapturedVariable(VarInstance<? extends VarDef> vi) {
        ClosureDef_c.addCapturedVariable(this.capturedEnvironment, vi);
    }

    public String designator() {
        return "async";
    }

    public String toString() {
        return designator() + " in " + position();
    }
}
