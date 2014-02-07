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
package x10.util.synthesizer;

import polyglot.ast.ClassMember;
import polyglot.types.MemberDef;
import polyglot.types.SemanticException;

/**
 * A class member synthesizer could be added into the class.
 * When the class synthesizer close it self, it will ask for
 * all the class member synthesizer to return the object
 *
 * Typical class member synthesizer could be a class synthesizer, a field synthesizer or a method synthesizer
 * 
 */
public interface IClassMemberSynth {
    
    public MemberDef getDef(); //get the current def()
    public ClassMember close()throws SemanticException; //close and generate the member
    
}
