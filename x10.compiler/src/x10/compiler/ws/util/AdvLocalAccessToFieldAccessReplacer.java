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


package x10.compiler.ws.util;

import java.util.HashSet;
import java.util.Set;

import polyglot.ast.Expr;
import polyglot.ast.FieldAssign;
import polyglot.ast.Formal;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.visit.NodeVisitor;
import polyglot.types.Context;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import x10.util.Synthesizer;

/**
 * @author Haichuan
 * 
 * This class is used to replace local var access by field access.
 * In WS code gen, the original code may contain local access.
 * e.g.
 *    static def fib(n:Int):Int {
 *       var t1:Int;
 *       t1 = async fib(n-1);
 *    }
 * After transformation, n/t1 are added as fields in a inner class.
 * Suppose tmp points to one instance, then we need change the last statement to
 *    tmp.t1 = async fib(t1.n - 1);
 * This work is done by the LocalAccessToFieldAccessReplacer
 * 
 *
 */
public class AdvLocalAccessToFieldAccessReplacer extends NodeVisitor {
    protected ILocalToFieldContainerMap refMap;
    protected Synthesizer synth;
    protected Context context;
    
    private boolean replaceError; //record weather there are some replacing errors;
    
    protected Set<Name> localDeclaredVar; //all locals with these names will not be replaced
     
    
    public AdvLocalAccessToFieldAccessReplacer(ILocalToFieldContainerMap refMap, Synthesizer synth, Context context,
                                               Set<Name> declaredNames){
        this.refMap = refMap;
        this.synth = synth;
        this.context = context;
        localDeclaredVar = CollectionFactory.newHashSet(declaredNames);
    }

    @Override
    public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
        Node ret = n;
        
        if(n instanceof LocalDecl){
            LocalDecl ld = (LocalDecl)n;
            localDeclaredVar.add(ld.name().id());
            //System.out.println("local added:" + ld.name().id());
        }
        
        if(n instanceof Formal){
            Formal f = (Formal)n;
            localDeclaredVar.add(f.name().id());
            //System.out.println("formal local added:" + f.name().id());
        }
        
        if(n instanceof Local && !localDeclaredVar.contains(((Local)n).name().id())
                && /*two conditions below, be very careful   */
           (!(parent instanceof LocalAssign) || (((LocalAssign)parent).local() != n))
           ){
            Local l = (Local)n;
            Name name = l.name().id();
            try {
                Expr instanceRef = refMap.getFieldContainerRef(name, l.type());
                if(instanceRef != null){
                    ret = synth.makeFieldAccess(n.position(), instanceRef, name, context);                    
                }
                else{
                    replaceError = true;
                    System.err.println("[WSCodeGen_Info] Ingore replacing local variable with field access:" + name );
                }
            } catch (SemanticException e) {
                replaceError = true;
                System.err.println("[WSCodeGen_ERR]:cannot replace local access with field access");
                e.printStackTrace();
            }
        }
        if(n instanceof LocalAssign && !localDeclaredVar.contains(((LocalAssign)n).local().name().id())){
            LocalAssign ls = ((LocalAssign)n);
            Local l = ls.local();
            Name name = l.name().id();
            try {
                Expr instanceRef = refMap.getFieldContainerRef(name, l.type());
                if(instanceRef != null){
                    FieldAssign fa = (FieldAssign) synth.makeFieldAssign(n.position(), instanceRef, name, ls.right(), context);
                    ret = fa.operator(ls.operator());
                }
                else{
                    replaceError = true;
                   System.err.println("[WSCodeGen_Info] Ingore replacing local variable with field access:" + name);
                }
            }
            catch (SemanticException e) {
                replaceError = true;
                System.err.println("[WSCodeGen_ERR]:cannot replace local access with field access");
                e.printStackTrace();
            }
           
        }
        return ret;
    }

    public boolean isReplaceError() {
        return replaceError;
    }
    
    
    
}
