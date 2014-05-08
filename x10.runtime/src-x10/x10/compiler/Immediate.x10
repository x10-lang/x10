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

package x10.compiler;

import x10.lang.annotations.MethodAnnotation;
import x10.lang.annotations.FieldAnnotation;
import x10.lang.annotations.ExpressionAnnotation;
import x10.lang.annotations.StatementAnnotation;

/**
 * This annotation on <tt>async</tt> and <tt>finish</tt> indicates that a
 * portion of code may be executed in a network header handler.
 * The body of an <tt>@Immediate async</tt> satisfies significant restrictions. (1) It must execute only a short piece of sequential code
 * (i.e. the code cannot use <tt>at</tt>, <tt>async</tt>, </tt>finish</tt>, <tt>when</tt> constructs). 
 * (2) It accesses a very small amount of memory, (3) The <tt>finish</tt> it is governed by must be marked <tt>@immediate</tt>.
 * <p>
 * The body <tt>S</tt> of an <tt>@Immediate finish S</tt> must satisfy the restriction that the only asyncs it generates
 * are <tt>@Immediate asyncs</tt>. 
 * <p>
 <p> The point of marking a <tt>finish</tt> and its enclosed <tt>asyncs</tt> with <tt>@Immediate</tt> is that 
 significant performance gains may be realized on certain transport networks, such as LAPI, that must acknowledge
 receipt of network packets to the sender, and are able to execute a small piece of user code on the destination
 (in the "header handler"), before sending the acknowledgment).  
 
 <p> On such systems, the compiler can translate an <tt>@Immediate async</tt> as follows: 
 if the target place is local, execute the code in-line. If it is remote,
 execute it in the header handler. Correspondingly, an <tt>@Immediate finish</tt> can be 
 translated into the invocation of a "local fence". This fence is a blocking
 operation that succeeds only when acknowledgments for all messages sent out from the source 
 (since the last "local fence" call) have been received. (While it blocks, it services incoming messages.)
 
 */
public interface Immediate
    extends ExpressionAnnotation, StatementAnnotation, MethodAnnotation {
}
