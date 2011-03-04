package x10.compiler.ws.util;

import java.util.HashSet;
import java.util.Set;

import polyglot.visit.NodeVisitor;
import x10.ast.Expr;
import x10.ast.FieldAssign;
import x10.ast.Formal;
import x10.ast.Local;
import x10.ast.LocalAssign;
import x10.ast.LocalDecl;
import x10.ast.Node;
import x10.types.Name;
import x10.types.SemanticException;
import x10.types.Context;
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
    
    protected HashSet<Name> localDeclaredVar; //all locals with these names will not be replaced
     
    
    public AdvLocalAccessToFieldAccessReplacer(ILocalToFieldContainerMap refMap, Synthesizer synth, Context context,
                                               Set<Name> declaredNames){
        this.refMap = refMap;
        this.synth = synth;
        this.context = context;
        localDeclaredVar = new HashSet<Name>(declaredNames);
    }

    
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
