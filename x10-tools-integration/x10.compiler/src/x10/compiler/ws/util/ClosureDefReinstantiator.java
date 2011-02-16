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

import polyglot.ast.Field;
import polyglot.ast.Local;
import polyglot.ast.Node;
import polyglot.types.ClassDef;
import polyglot.types.Context;
import polyglot.types.LocalInstance;
import polyglot.types.MethodDef;
import polyglot.types.Types;
import polyglot.types.VarInstance;
import polyglot.visit.NodeVisitor;
import x10.ast.Closure;
import x10.ast.X10Special;
import x10.types.ClosureDef;
import x10.types.EnvironmentCapture;
import x10.types.ThisDef;
import x10.types.X10MemberDef;
import x10.visit.Desugarer;
import x10.visit.Desugarer.ClosureCaptureVisitor;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;

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

	
	Context context;
    TypeSystem xts;
    ClassDef containerClassDef;
    MethodDef containerMethodDef;
    
    public ClosureDefReinstantiator(TypeSystem xts, Context context, ClassDef containerClassDef, MethodDef containerMethodDef){
        this.xts = xts;
        this.context = context;
        this.containerClassDef = containerClassDef;
        this.containerMethodDef = containerMethodDef;
            
    }
    
    public Node leave(Node old, Node n, NodeVisitor v) {

        
        if(n instanceof Closure){
            Closure c = (Closure)n;
            
            ClosureDef cd = c.closureDef();
            
            ClosureDef ncd = (ClosureDef) cd.copy();
            ncd.setTypeContainer(Types.ref(containerClassDef.asType()));
            ncd.setMethodContainer(Types.ref(containerMethodDef.asInstance()));
            
//            ClosureDef ncd = xts.closureDef(c.position(),
//                                            Types.ref(containerClassDef.asType()), 
//                                            Types.ref(containerMethodDef.asInstance()), 
//                                            cd.returnType(), 
//                                            cd.formalTypes(), 
//                                            cd.thisDef(), 
//                                            cd.formalNames(),
//                                            cd.guard(), 
//                                            //cd.throwTypes(),
//                                            cd.offerType());
            
            c = c.closureDef(ncd);
            //need set the ncd's captured environment variables

            //NodeVisitor captureEnvVisitor = new SimpleClosureCaptureVisitor(c.closureDef());
            //FIXME: after the WS context is correct, use the following visitor
            //NodeVisitor captureEnvVisitor = new Desugarer.ClosureCaptureVisitor(context, c.closureDef());
            //c.visit(captureEnvVisitor);
            n = c;
            //Use a simple closure capture visitor
            

        }
        return n;
    }
    
    
    public static class SimpleClosureCaptureVisitor extends NodeVisitor {
        private final EnvironmentCapture cd;
        public SimpleClosureCaptureVisitor(EnvironmentCapture cd) {
            this.cd = cd;
        }
        @Override
        public Node leave(Node old, Node n, NodeVisitor v) {
            if (n instanceof Local) {
                LocalInstance li = ((Local) n).localInstance();
                cd.addCapturedVariable(li);
            } else if (n instanceof Field) {
                if (((Field) n).target() instanceof X10Special) {
                    cd.addCapturedVariable(((Field) n).fieldInstance());
                }
            } else if (n instanceof X10Special) {
            }
            return n;
        }
    }
    
    
}
