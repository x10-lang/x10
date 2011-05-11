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
import polyglot.frontend.Job;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.ErrorHandlingVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.StmtExpr;
import x10.constraint.XLit;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.extension.X10Ext;
import x10.optimizations.ForLoopOptimizer;
import x10.types.checker.Converter;
import x10.types.constraints.CConstraint;
import x10.util.AltSynthesizer;

/**
 * Very simple constant propagation pass. 
 * <p> If an expr has a constant value, replace it with the value. 
 * 
 * <p> Replace branches on constants with the consequent or alternative as appropriate. 
 * 
 * <p> TODO: Handle constant rails and conversions.
 * <p> TODO: Propagate through rails A(0) = v; ... A(0) --> v. TODO: Dead code
 * elimination. visitor.
 * 
 * FIXME: [IP] propagate closure literals
 * 
 * @author nystrom
 */
public class ConstantPropagator extends ContextVisitor {
    
    private static AltSynthesizer syn;
    private final Job         job;
    private final TypeSystem  xts;
    
    public ConstantPropagator(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        syn = new AltSynthesizer(ts, nf);
        this.job = job;
        this.xts = ts;
    }
    
    @Override
    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        Position pos = n.position();

        if (!(n instanceof Expr || n instanceof Stmt)) return n;
        if (n instanceof Lit) return n;
        
        if (n instanceof LocalDecl) {
            LocalDecl d = (LocalDecl) n;
            if (d.flags().flags().isFinal() && d.init() != null && isConstant(d.init())) {
                d.localDef().setConstantValue(constantValue(d.init()));
                return nf.Empty(d.position());
            }
        }

        if (n instanceof Local) {
            Local l = (Local) n;
            if (l.localInstance().def().isConstant()) {
                Object o = l.localInstance().def().constantValue();
                Expr result = toExpr(o, n.position());
                if (result != null)
                    return result;
                
            }
        }
        
        if (n instanceof Expr) {
            Expr e = (Expr) n;
            if (isConstant(e)) {
                Object o = constantValue(e);
                Expr result = toExpr(o, e.position());
                if (result != null)
                    return result;
            }
        }

        if (n instanceof Conditional) {
            Conditional c = (Conditional) n;
            Expr cond = c.cond();
            if (isConstant(cond)) {
                boolean b = (boolean) (Boolean) constantValue(cond);
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
                Object o = constantValue(cond);
                if (o instanceof Boolean) {
                    boolean b = (boolean) (Boolean) o;
                    if (b)
                        return c.consequent();
                    else
                        return c.alternative() != null ? c.alternative() : nf.Empty(pos);
                }
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

    public static Object constantValue(Expr e) {
        if (e.isConstant())
            return e.constantValue();
        
        if (e.type().isNull())
            return null;

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
        				return l.val();
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
                return l.val();
            }
        }
        return null;
    }

    public static boolean isConstant(Expr e) {
        
        if (isNative(e))
            return false;
        
        Type type = e.type();
        if (null == type) // TODO: this should never happen, determine if and why it does
            return false;
        
        if (type.typeSystem().isSubtype(type, type.typeSystem().String()))
            return false; // Strings have reference semantics
        
        if (type.isNull())
            return true;
        
        if (e.isConstant())
            return true;
        
        if (e instanceof Field) {
            Field f = (Field) e;
            if (f.target() instanceof Expr) {
                Expr target = (Expr) f.target();
                if (isNative(target))
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

    public Expr toExpr(Object o, Position pos) {
        NodeFactory nf = (NodeFactory) this.nf;

        Expr e = null;
        if (o == null) {
            e = nf.NullLit(pos);
        } else
        if (o instanceof Integer) {
            e = nf.IntLit(pos, IntLit.INT, (long) (int) (Integer) o);
        } else
        if (o instanceof Long) {
            e = nf.IntLit(pos, IntLit.LONG, (long) (Long) o);
        } else
        if (o instanceof Float) {
            e = nf.FloatLit(pos, FloatLit.FLOAT, (double) (float) (Float) o);
        } else
        if (o instanceof Double) {
            e = nf.FloatLit(pos, FloatLit.DOUBLE, (double) (Double) o);
        } else
        if (o instanceof Character) {
            e = nf.CharLit(pos, (char) (Character) o);
        } else
        if (o instanceof Boolean) {
            e = nf.BooleanLit(pos, (boolean) (Boolean) o);
        } else
        if (o instanceof String) {
            e = null; // strings have reference semantics
        } else
        if (o instanceof Object[]) {
            Object[] a = (Object[]) o;
            List<Expr> args = new ArrayList<Expr>(a.length);
            for (Object ai : a) {
                Expr ei = toExpr(ai, pos);
                if (ei == null)
                    return null;
                args.add(ei);
            }
            e = nf.Tuple(pos, args);
        }
        try {
            if (e != null) e = Converter.check(e, this);
        } catch (SemanticException cause) {
            throw new InternalCompilerError("Unexpected exception when typechecking "+e, e.position(), cause);
        }
        return e;
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

    private static final QName NATIVE_ANNOTATION = QName.make("x10.compiler.Native");
    /**
     * Determine if a node is annotated "@Native".
     * 
     * @param node a node which may appear constant but be native instead
     * @return true, if node has an "@Native" annotation; false, otherwise
     */
    private static boolean isNative(Node node) {
        return    null != node.ext() 
               && node.ext() instanceof X10Ext 
               && !((X10Ext) node.ext()).annotationNamed(NATIVE_ANNOTATION).isEmpty();
    }

}