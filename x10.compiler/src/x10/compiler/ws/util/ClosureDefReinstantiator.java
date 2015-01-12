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

import polyglot.ast.Field;
import polyglot.ast.Local;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.Context;
import polyglot.types.LocalInstance;
import polyglot.types.MethodDef;
import polyglot.types.Types;
import polyglot.types.VarDef;
import polyglot.types.VarInstance;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.Closure;
import x10.ast.X10Special;
import x10.types.ClosureDef;
import x10.types.ClosureDef_c;
import x10.types.EnvironmentCapture;
import x10.types.ThisDef;
import x10.types.X10ClassType;
import x10.types.X10MemberDef;
import x10.visit.ClosureCaptureVisitor;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;

/**
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
 * 
 * @author Haichuan
 */
public class ClosureDefReinstantiator extends ContextVisitor {

	
	public ClosureDefReinstantiator(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }


    public Node leaveCall(Node old, Node n, NodeVisitor v) {
        if(n instanceof Closure){
            
            Context ct = context();
            Closure c = (Closure)n;
            //WSUtil.debug("Found closure", c);
            ClosureDef cd = c.closureDef();
            
            cd.setStaticContext(ct.inStaticContext());
            cd.setTypeContainer(Types.ref(ct.currentClassDef().asType()));
            cd.setMethodContainer(Types.ref(ct.currentCode().asInstance()));
            
            c = c.closureDef(cd);
//            //Now show current vars
//            ClosureDef_c cdc = (ClosureDef_c)cd;
//            for(VarInstance<? extends VarDef> vi : cdc.capturedEnvironment()){
//                WSUtil.debug("as-is var:"+ vi.toString());
//            }
            
            //Now set the CapturedEnvironment
            NodeVisitor captureEnvVisitor = new ClosureCaptureVisitor(context, c.closureDef());
            c.visit(captureEnvVisitor);
//            for(VarInstance<? extends VarDef> vi : cdc.capturedEnvironment()){
//                WSUtil.debug("afer-process var:"+ vi.toString());
//            }
            return c;
        }
        return n;
    }

    
}
