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

package x10.constraint;

/**
 * A representation of a Field.T is a FieldDef or a MethodDef.
 * @author vj
 *
 */
public interface XField<T> extends XVar {
    public T field(); 

    /** 
     * if this is r.f, then return newReceiver.f.
     * @param newReceiver
     * @return
     */
    public XField<T> copyReceiver(XVar newReceiver);
    public XVar receiver(); 
    public XVar rootVar();
  
}
