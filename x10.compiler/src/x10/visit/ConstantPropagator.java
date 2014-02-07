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

package x10.visit;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.Branch;
import polyglot.ast.Conditional;
import polyglot.ast.Empty;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FloatLit;
import polyglot.ast.If;
import polyglot.ast.IntLit;
import polyglot.ast.Lit;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Return;
import polyglot.ast.Stmt;
import polyglot.ast.Throw;
import polyglot.ast.VarDecl;
import polyglot.frontend.Job;
import polyglot.types.Context;
import polyglot.types.LocalDef;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.VarDef;
import polyglot.types.VarInstance;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.ErrorHandlingVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.Async;
import x10.ast.Async_c;
import x10.ast.AtEach;
import x10.ast.AtExpr;
import x10.ast.AtStmt;
import x10.ast.Closure;
import x10.ast.Closure_c;
import x10.ast.StmtExpr;
import x10.ast.X10Formal;
import x10.constraint.XLit;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.extension.X10Ext;
import x10.optimizations.ForLoopOptimizer;
import x10.types.TypeParamSubst;
import x10.types.checker.Converter;
import x10.types.constants.BooleanValue;
import x10.types.constants.ClosureValue;
import x10.types.constants.ConstantValue;
import x10.types.constants.StringValue;
import x10.types.constraints.CConstraint;
import x10.util.AltSynthesizer;

/**
 * Very simple constant propagation pass. 
 * <p> If an expr has a constant value, replace it with the value. 
 * 
 * <p> Replace branches on constants with the consequent or alternative as appropriate. 
 * 
 * <p> TODO: Dead code elimination. visitor.
 * 
 * @author nystrom
 */
public class ConstantPropagator extends ContextVisitor {
    
    private static AltSynthesizer syn;
    private final Job         job;
    private final TypeSystem  xts;
    private final boolean propClosures;
    
    public ConstantPropagator(Job job, TypeSystem ts, NodeFactory nf, boolean propagateClosures) {
        super(job, ts, nf);
        syn = new AltSynthesizer(ts, nf);
        this.job = job;
        this.xts = ts;
        this.propClosures = propagateClosures;
    }
    
    @Override
    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        Position pos = n.position();
        
        // AST nodes that capture lexical environments need to have the captured environment
        // recomputed in leaveCall because we may have done one of two things:
        //   (a) Constant propagated away uses of a lexically captured variable, thus 
        //       reducing the captured environment.
        //   (b) Propagated one or more closure literals into the body of the construct,
        //       which can increase the captured environment. 
        if (n instanceof Async) {
            ((Async)n).body().visit(new ClosureCaptureVisitor(this.context(), ((Async)n).asyncDef()));
        } else if (n instanceof AtStmt) {
            ((AtStmt)n).body().visit(new ClosureCaptureVisitor(this.context(), ((AtStmt)n).atDef()));                
        } else if (n instanceof AtEach) {
            // Only want to visit the body to recompute capture, but need to simulate 
            // visiting the formal so that it gets in scope for the body so env capture can find it.
            AtEach ateach = (AtEach)n;
            Context tmpContext = this.context().pushBlock();
            ateach.formal().addDecls(tmpContext);
            ateach.body().visit(new ClosureCaptureVisitor(tmpContext, ateach.atDef()));
        } else if (n instanceof AtExpr) {
            ((AtExpr)n).body().visit(new ClosureCaptureVisitor(this.context(), ((AtExpr)n).closureDef()));                
        } else if (n instanceof Closure) {
            n.visit(new ClosureCaptureVisitor(this.context(), ((Closure)n).closureDef()));                
        }

        if (!(n instanceof Expr || n instanceof Stmt)) return n;
        if (n instanceof StmtExpr) return n;
        if (n instanceof Lit) return n;
        
        if (n instanceof LocalDecl) {
            LocalDecl d = (LocalDecl) n;
            if (d.flags().flags().isFinal()) {
                Expr init = d.init();
                if (init != null && init.isConstant()) {
                    ConstantValue cv = constantValue(init);
                    if (propClosures || !(cv instanceof ClosureValue)) {
                        d.localDef().setConstantValue(constantValue(init));
                        if (isConstant(init)) {
                            return nf.Empty(d.position());
                        }
                    }
                }
            }
        }

        if (n instanceof Local) {
            Local l = (Local) n;
            if (l.localInstance().def().isConstant()) {
                ConstantValue o = l.localInstance().def().constantValue();
                if (null != o && !(o instanceof StringValue)) {
                    if (o instanceof ClosureValue) {
                        if (propClosures) {
                            return makeClosureLiteral((ClosureValue)o);
                        } else {
                            return n;
                        }
                    } else {
                        return o.toLit(nf, xts, l.type(), n.position());  
                    }
                }
            }
        }
        
        if (n instanceof Expr) {
            Expr e = (Expr) n;
            if (isConstant(e)) {
                ConstantValue o = constantValue(e);
                if (null != o) {
                    if (o instanceof ClosureValue) {
                        if (propClosures) {
                            return makeClosureLiteral((ClosureValue)o);
                        } else {
                            return n;
                        }
                    } else {
                        return o.toLit(nf, xts, e.type(), e.position());
                    }
                }
            }
        }

        if (n instanceof Conditional) {
            Conditional c = (Conditional) n;
            Expr cond = c.cond();
            if (isConstant(cond)) {
                boolean b = ((BooleanValue) constantValue(cond)).value();
                if (b)
                    return c.consequent();
                else
                    return c.alternative();
            }
        }

        if (n instanceof If) {
            If c = (If) n;
            Expr cond = c.cond();
            if (isConstant(cond)) {
                boolean b = ((BooleanValue) constantValue(cond)).value();
                if (b)
                    return c.consequent();
                else
                    return c.alternative() != null ? c.alternative() : nf.Empty(pos);
            }
        }

        if (n instanceof Block){
            Block b = (Block) n;
            List<Stmt> stmts = new ArrayList<Stmt>();
            for (Stmt s : b.statements()) {
                if (!(s instanceof Empty)) {
                    stmts.add(s);
                    if (divertsFlow(s)) {
                        if (b instanceof StmtExpr) { // ExpressionFlattener will have eliminated these for the Java back-end
                            b = ((StmtExpr) b).result(null); // result can't be reached, throw it away
                        }
                        return b.statements(stmts);
                    }
                }
            }
            if (stmts.size() < b.statements().size())
                return b.statements(stmts);
            return b;
        }

        return n;
    }
    
    private Closure_c makeClosureLiteral(ClosureValue cv) {
        Reinstantiator reinstantiator= new Reinstantiator(TypeParamSubst.IDENTITY);
        ContextVisitor visitor= new NodeTransformingVisitor(job, ts, nf, reinstantiator).context(context());
        Closure_c cls = (Closure_c)cv.getClosure().visit(visitor); // reinstantiate locals in the body
        cls.visit(new ClosureCaptureVisitor(this.context(), cls.closureDef()));
        return cls;
    }

    public static ConstantValue constantValue(Expr e) {
        if (e.isConstant())
            return e.constantValue();
        
        if (e instanceof Field) {
        	Field f = (Field) e;
        	if (f.target() instanceof Expr) {
        		Expr target = (Expr) f.target();
        		Type t = target.type();
        		CConstraint c = Types.xclause(t);
        		if (c != null) {
        			XTerm val = c.bindingForSelfField(f);
        			if (val instanceof XLit) {
        				XLit l = (XLit) val;
        				return ConstantValue.make(f.type(), l.val());
        			}
        		}
        	}
        }

        Type t = e.type();
        CConstraint c = Types.xclause(t);
        if (c != null) {
            XVar r = c.self();
            if (r instanceof XLit) {
                XLit l = (XLit) r;
                return ConstantValue.make(t, l.val());
            }
        }
        return null;
    }

    private static boolean isConstant(Expr e) {
        
        Type type = e.type();
        if (null == type) // TODO: this should never happen, determine if and why it does
            return false;
        
        TypeSystem ts = type.typeSystem();
        if (isNative(e, ts))
            return false;
                
        if (e.isConstant()) {
            ConstantValue cv = e.constantValue();
            if (type.isNull() || !type.isReference()) return true;
            return (cv instanceof ClosureValue);  // ClosureLiterals are the only non-null reference type that we allow ourselves to constant propagate.
        }

        if (e instanceof Field) {
            Field f = (Field) e;
            if (f.target() instanceof Expr) {
                Expr target = (Expr) f.target();
                if (isNative(target, ts))
                    return false;
                Type t = target.type();
                CConstraint c = Types.xclause(t);
                if (c != null) {
                	XTerm val = c.bindingForSelfField(f);
                	if (val instanceof XLit) {
                		return true;
                	}
                }
            }
        }

        CConstraint c = Types.xclause(type);
        if (c != null) {
            XVar r = c.self();
            if (r instanceof XLit) {
                XLit l = (XLit) r;
                return true;
            }
        }
        return false;
    }

    /**
     * Would a statement prevent control flow from reaching the sequentially next statement in a Block?
     * 
     * @param s the Stmt that might divert the flow of control
     * @return true iff s changes normal control flow
     */
    private boolean divertsFlow(Stmt s) {
        if (s instanceof Block) {
            List<Stmt> statements = ((Block) s).statements();
            int size = statements.size();
            if (0 == size) return false;
            return divertsFlow(statements.get(size-1));
        }
        return s instanceof Return || s instanceof Throw || s instanceof Branch;
    }

    /**
     * Determine if a node is annotated "@Native".
     * 
     * @param node a node which may appear constant but be native instead
     * @return true, if node has an "@Native" annotation; false, otherwise
     */
    private static boolean isNative(Node node, TypeSystem ts) {
        return null != node.ext() 
               && node.ext() instanceof X10Ext 
               && !((X10Ext) node.ext()).annotationMatching(ts.NativeType()).isEmpty();
    }

}
