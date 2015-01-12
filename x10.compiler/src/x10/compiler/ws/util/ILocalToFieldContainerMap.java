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


package x10.compiler.ws.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;

/**
 * Originally, all locals are just locals
 * After WS transformation, locals are transformed as fields in different levels' fields
 * So all local access should be transformed as field acess
 *   e.g. n = 10 ==> _fib.n = 10
 * However, the field container's ref "_fib" should be provided by the WS code gen, too.
 * So here we provide this interface to do this transformation.
 * 
 * @author Haichuan
 *
 */
public interface ILocalToFieldContainerMap {
    
    public static String UP_REF = "up"; //ref between typical two frames
    public static String K_REF = "k"; //ref between async frame and its continuation frame
    public static String R_REF = "r"; //ref between remote frame and its parent frame

    public Map<Expr, Stmt> getRefToDeclMap(); //return all container ref's declare so that to add into the statements
    
    public Expr getFieldContainerRef(Name fieldName, Type type) throws SemanticException;  //return the field container's ref

}
