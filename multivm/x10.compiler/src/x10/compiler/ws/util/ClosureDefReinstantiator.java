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


package x10.compiler.ws.util;

import polyglot.ast.Node;
import polyglot.types.ClassDef;
import polyglot.types.MethodDef;
import polyglot.types.Types;
import polyglot.visit.NodeVisitor;
import x10.ast.Closure;
import x10.types.ClosureDef;
import polyglot.types.TypeSystem;

/**
 * @author Haichuan
 * 
 * In a ws code transformation, each code path will be transformed into fast and 
 * slow path.
 * 
 * But for a closure, originally it has one declare and one def.
 * But after transformation, it will have two declares with still only one def,
 *  which causes error for further transformation.
 *  
 * This code visitor will create a new closure def for all closure in the AST tree.
 * 
 * It should be added as one job in synthesizing the slow path
 */
public class ClosureDefReinstantiator extends NodeVisitor {

    TypeSystem xts;
    ClassDef containerClassDef;
    MethodDef containerMethodDef;
    
    public ClosureDefReinstantiator(TypeSystem xts, ClassDef containerClassDef, MethodDef containerMethodDef){
        this.xts = xts;
        this.containerClassDef = containerClassDef;
        this.containerMethodDef = containerMethodDef;
            
    }
    
    public Node leave(Node old, Node n, NodeVisitor v) {

        
        if(n instanceof Closure){
            Closure c = (Closure)n;
            
            ClosureDef cd = c.closureDef();
            
            ClosureDef ncd = xts.closureDef(c.position(),
                                            Types.ref(containerClassDef.asType()), 
                                            Types.ref(containerMethodDef.asInstance()), 
                                            cd.returnType(), 
                                            cd.formalTypes(), 
                                            cd.thisDef(), 
                                            cd.formalNames(),
                                            cd.guard(), 
                                            //cd.throwTypes(),
                                            cd.offerType());
            n = c.closureDef(ncd);
        }
        return n;
    }
    
    
    
}
