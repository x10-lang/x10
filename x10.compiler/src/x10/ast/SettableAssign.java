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

package x10.ast;

import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Expr;
import polyglot.types.ClassDef;
import polyglot.types.Name;
import polyglot.types.Type;
import polyglot.util.TypedList;
import x10.types.MethodInstance;

public interface SettableAssign extends Assign {

    /** The name of the method to use for settable assignment */
    public static final Name SET = OperatorNames.SET; 

    /** Get the array of the expression. */
    public Expr array();

    /** Set the array of the expression. */
    public SettableAssign array(Expr array);

    /** Get the index of the expression. */
    public List<Expr> index();

    /** Set the index of the expression. */
    public SettableAssign index(List<Expr> index) ;

    public MethodInstance methodInstance();
    SettableAssign methodInstance(MethodInstance mi);

    public MethodInstance applyMethodInstance();

    public SettableAssign applyMethodInstance(MethodInstance ami);
}
