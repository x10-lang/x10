/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.TypeCheckFragmentGoal;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.ast.TypeNode_c;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.frontend.SetResolverGoal;
import polyglot.types.Context;
import polyglot.types.LazyRef;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ExceptionChecker;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeCheckPreparer;
import polyglot.visit.TypeChecker;


public class AmbDepTypeNode_c extends TypeNode_c implements AmbDepTypeNode {
   
    protected TypeNode base;
    
    protected DepParameterExpr dep;
    
    protected GenParameterExpr gen;
    
    public AmbDepTypeNode_c(Position pos, TypeNode b) {
        super(pos);
        base = b;
      
    }
    public AmbDepTypeNode_c(Position pos, TypeNode b, DepParameterExpr d, GenParameterExpr g) {
        super(pos);
        base = b;
        dep = d;
        gen = g;
    }
    
    public TypeNode base() { return base;}
    public AmbDepTypeNode base(TypeNode base)  {
        if (base == this.base)  return this;
        AmbDepTypeNode_c n = (AmbDepTypeNode_c) copy();
        n.base = base;
        return n;
    } 
    
    public GenParameterExpr gen() { return gen;}
    public AmbDepTypeNode gen(GenParameterExpr expr)  {
        if (expr == this.gen)  return this;
        AmbDepTypeNode_c n = (AmbDepTypeNode_c) copy();
        n.gen = expr;
        return n;
        } 
    
    public DepParameterExpr dep() { return dep;}
    public AmbDepTypeNode dep( DepParameterExpr expr) {
        if (expr == this.dep)  return this;
        AmbDepTypeNode_c n = (AmbDepTypeNode_c) copy();
        n.dep = expr;
       
        return n;
        }  
    
    public AmbDepTypeNode reconstruct(TypeNode tn, GenParameterExpr g, DepParameterExpr d) {
        if (tn == base && g == gen && d==dep) return this;
        AmbDepTypeNode_c n = (AmbDepTypeNode_c) copy();
        n.base=tn;
        n.gen=g;
        n.dep=d;
        return n;
    }
    
    public Context enterChildScope(Node child, Context c) {
    	if (child == this.dep) {
    	    TypeSystem ts = c.typeSystem();
    	    c = ((X10Context) c).pushDepType(base.typeRef());
    	}
        Context cc = super.enterChildScope(child, c);
        return cc;
    }
    public Node visitChildren(NodeVisitor v) {
        TypeNode tn = (TypeNode) visitChild(this.base, v);
        GenParameterExpr gen = (GenParameterExpr) visitChild(this.gen, v);
        DepParameterExpr dep = (DepParameterExpr) visitChild(this.dep, v);
        return reconstruct(tn, gen, dep);
    }
    
    public String toString() {
    	return base.toString() + (dep != null ? "(:" + dep + ")" : "");
    }
      
    public Node typeCheckOverride(Node parent, TypeChecker tc) throws SemanticException {
        // Override to disambiguate the base type and rebuild the node before type checking the dep clause.

        TypeSystem ts = tc.typeSystem();
        NodeFactory nf = tc.nodeFactory();

        AmbDepTypeNode_c n = this;

        TypeChecker childtc = (TypeChecker) tc.enter(parent, n);
        
        TypeNode tn = (TypeNode) n.visitChild(n.base, childtc);
        n = (AmbDepTypeNode_c) n.base(tn);

        LazyRef<Type> sym = (LazyRef<Type>) n.type;
        
        if (tn.type() instanceof UnknownType) {
            // Mark the type resolved to prevent us from trying to resolve this again and again.
            sym.update(ts.unknownType(position()));
            return n;
        }
        
        X10Type t = (X10Type) tn.type();

        // Update the symbol with the base type so that if we try to get the type while checking the constraint, we don't get a cyclic
        // dependency error, but instead get a less precise type.
        sym.update(t);
        
        DepParameterExpr dep = (DepParameterExpr) n.visitChild(n.dep, childtc);
        n = (AmbDepTypeNode_c) n.dep(dep);
        
        GenParameterExpr gen = (GenParameterExpr) n.visitChild(n.gen, childtc);
        n = (AmbDepTypeNode_c) n.gen(gen);
    
        if (dep != null) {
            t = t.depClause(dep.constraint());
        }

        if (gen != null) {
            List<Ref<? extends Type>> args = new ArrayList(gen.args().size());
            for (TypeNode arg : gen.args()) {
                args.add(arg.typeRef());
            }
            t = t.typeParams(args);
        }

        sym.update(t);

        return nf.CanonicalTypeNode(position(), sym);
    }
    
    public Node exceptionCheck(ExceptionChecker ec) throws SemanticException {
        throw new InternalCompilerError(position(),
            "Cannot exception check ambiguous node " + this + ".");
    }
    
    @Override
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        tr.print(this, base, w);
        if (gen != null) {
            tr.print(this, gen, w);
        }
        if (dep != null) {
            tr.print(this, dep, w);
        }
    }
}
