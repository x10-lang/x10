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

import x10.lang.annotations.StatementAnnotation;

/**
 * This annotation on an async indicates that the async should be ignored
 * in the context of finish and clocks.
 * The body of an @Uncounted async may only contain @Uncounted asyncs.
 * The body of an @Uncounted async may contain a finish, which in turn can contain both regular and @Uncounted asyncs.
 * @Uncounted asyncs are not accounted for by the enclosing finish.
 * That is, the enclosing finish may complete before the async completes.
 *
 * CURRENT LIMITATIONS:
 * The compiler does not enforce any of the above restrictions.
 * If the main method return before all of the uncounted asyncs have completed
 * then the application may exit without waiting for the uncounted asyncs to complete.
 * Exceptions in uncounted asyncs are discarded (but the runtime prints debug a message to the error console).
 *
 * EXAMPLE:
 *
 * import x10.compiler.Uncounted;
 *
 * class Box[T] {
 *     var t:T;
 * 
 *     def this(init:T) { t = init; }
 * }
 * 
 * public class Foo {
 *     public static def main(args:Rail[String]) {
 *         val box = new Box[Boolean](false);
 *         @Uncounted async (here.next()) {
 *             Runtime.println("HELLO");
 *             @Uncounted async (box) {
 *                 atomic box.t = true;
 *             }
 *         }
 *         await box.t;
 *     }
 * }
 * 
 */
public interface Uncounted
    extends StatementAnnotation {
}
