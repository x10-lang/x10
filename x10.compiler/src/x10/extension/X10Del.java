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

package x10.extension;

import x10.effects.EffectComputer;
import polyglot.ast.Node;
import polyglot.types.SemanticException;
import polyglot.visit.ContextVisitor;
import polyglot.ast.JL;

public interface X10Del extends X10Ext, JL {
       Node computeEffectsOverride(Node n, EffectComputer ec) throws SemanticException;
        EffectComputer computeEffectsEnter(EffectComputer ec);
        Node computeEffects(EffectComputer ec);
}
