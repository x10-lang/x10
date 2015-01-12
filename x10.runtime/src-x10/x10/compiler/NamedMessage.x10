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

package x10.compiler;

import x10.lang.annotations.ExpressionAnnotation;
import x10.lang.annotations.StatementAnnotation;

/**
 * <p> This annotation is used to provide explicit programmer
 * control over the name of the compiler-generated class
 * that is used to implement an X10-source level <code>at</code>. 
 * By allowing the programmer to specify the logical message name
 * assigned to a particular <code>'at'</code> expression or statement,
 * it allows name-stability of the message name across recompilations 
 * of the source file.</p>
 *
 * <p>It is the responsibility of the programmer who is using this 
 * annotation to ensure that the name is unique within the compilation
 * scope (currently an X10 class). Failure to ensure a unique name
 * will result in post-compilation failures due to multiple definitions
 * of the name in the generated code.</p>
 */
public interface NamedMessage(name:String) extends RemoteInvocation {
}
