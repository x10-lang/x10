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

package x10.types;

import java.util.List;

import polyglot.types.VarDef;
import polyglot.types.VarInstance;

/**
 * A CodeDef with the ability to capture its environment.
 */
public interface EnvironmentCapture {
    List<VarInstance<? extends VarDef>> capturedEnvironment();
    void setCapturedEnvironment(List<VarInstance<? extends VarDef>> env);
    void addCapturedVariable(VarInstance<? extends VarDef> vi);
}