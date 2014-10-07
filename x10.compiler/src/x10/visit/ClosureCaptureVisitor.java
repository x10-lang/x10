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

import polyglot.ast.Field;
import polyglot.ast.Local;
import polyglot.ast.Node;
import polyglot.ast.Special.Kind;
import polyglot.types.Context;
import polyglot.types.LocalInstance;
import polyglot.types.VarInstance;
import polyglot.util.InternalCompilerError;
import polyglot.visit.NodeVisitor;
import x10.ast.X10Field_c;
import x10.ast.X10Special;
import x10.types.EnvironmentCapture;
import x10.types.ThisDef;
import x10.types.X10MemberDef;

/**
 * Compute the captured environment of a closure literal by
 * visiting its body and recording upward exposed variable references.
 */
public class ClosureCaptureVisitor extends NodeVisitor {
    private final Context context;
    private final EnvironmentCapture cd;
    public ClosureCaptureVisitor(Context context, EnvironmentCapture cd) {
        this.context = context;
        this.cd = cd;
        this.cd.setCapturedEnvironment(new ArrayList<VarInstance<?>>());
    }
    @Override
    public Node leave(Node old, Node n, NodeVisitor v) {
        if (n instanceof Local) {
            LocalInstance li = ((Local) n).localInstance();
            VarInstance<?> o = context.findVariableSilent(li.name());
            if (li == o || (o != null && li.def() == o.def())) {
                cd.addCapturedVariable(li);
            }
        } else if (n instanceof Field) {
            Field f = (Field) n;
            if (X10Field_c.isFieldOfThis(f)) {
                cd.addCapturedVariable(f.fieldInstance());
            }
        } else if (n instanceof X10Special) {
            X10Special s = (X10Special)n;
            ThisDef thisDef = s.type().toClass().def().thisDef();
            cd.addCapturedVariable(thisDef.asInstance());
        }
        return n;
    }
}