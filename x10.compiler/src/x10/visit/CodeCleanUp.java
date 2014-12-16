/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2010.
 */
package x10.visit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import polyglot.ast.Block;
import polyglot.ast.Branch;
import polyglot.ast.Empty;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.For;
import polyglot.ast.Id;
import polyglot.ast.Labeled;
import polyglot.ast.Local;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Return;
import polyglot.ast.Stmt;
import polyglot.ast.SwitchBlock;
import polyglot.ast.Throw;
import polyglot.ast.Try;
import polyglot.frontend.Job;
import polyglot.types.Name;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.StmtExpr;
import x10.ast.StmtSeq;
import x10.extension.X10Ext;
import x10.util.CollectionFactory;

/**
 * The <code>CodeCleaner</code> runs over the AST and performs some trivial dead
 * code elimination, while flattening blocks wherever possible.
 **/
public class CodeCleanUp extends ContextVisitor {

    private TypeSystem xts;
    private NodeFactory xnf;
    final protected boolean report;
    final protected boolean reportStats;
    static protected long blockCount;
    static protected long unreachableCount;
    protected X10AlphaRenamer alphaRenamer;
    protected Map<Name, Integer> labelInfo;

    /**
     * Creates a visitor for cleaning code.
     * 
     * @param nf The node factory to be used when generating new nodes.
     **/
    public CodeCleanUp(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = ts;
        xnf = nf;
        this.report = reporter.should_report("CodeCleanUp", 1);
        this.reportStats = reporter.should_report("CodeCleanUpStats", 1);
        this.alphaRenamer = new X10AlphaRenamer(this, true);
        this.labelInfo = CollectionFactory.newHashMap();
    }

    @Override
    public Object shallowCopy() {
        CodeCleanUp copy = (CodeCleanUp) super.shallowCopy();
        return copy;
    }

    @Override
    protected NodeVisitor enterCall(Node parent, Node child) {
        if (child instanceof Labeled) {
            Name labelName = ((Labeled) child).labelNode().id();
            assert !labelInfo.containsKey(labelName) : "CodeCleanup: "+labelName+" already in map! Shadowed labels?";
            labelInfo.put(labelName, 0);
        } else if (child instanceof Branch) {
            Id labelNode = ((Branch) child).labelNode();
            if (labelNode != null) {
                labelInfo.put(labelNode.id(), labelInfo.get(labelNode.id()) + 1);
            }
        }

        return this;        
    }

    @Override
    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) {
        if (n instanceof Labeled) {
            int uses = labelInfo.remove(((Labeled) n).labelNode().id());    // unboxing is safe as we always removing an element we put before
            // If the label was never used, then eliminate the Labeled node and just return the statement itself.
            if (uses == 0) return ((Labeled)n).statement();
            else if (uses == 1 && isBreakLast((Labeled)n)) return removeBreakLast((Labeled)n);
            else return n;
        } 
        
        if (n instanceof Eval && ((Eval)n).expr() instanceof StmtExpr  && !(parent instanceof For)) {
            return sinkEval((StmtExpr)((Eval)n).expr(), n.position());
        }
        
        if (n instanceof Eval && ((Eval)n).expr() instanceof Local) {
            return nf.Empty(n.position());
        }
        
        if (n instanceof StmtExpr) {
            StmtExpr ste = (StmtExpr)n;
            if (ste.statements().isEmpty()) {
                // Simplify StmtExpr({}, E) to just E
                return ste.result();
            }
        }
        
        if (n instanceof Return && ((Return) n).expr() instanceof StmtExpr) {
            Block b = sinkReturn((StmtExpr)((Return)n).expr(), n.position());
            return clean(flattenBlock(b));
        }
        
        if (!(n instanceof Block) || n instanceof SwitchBlock) {
            return n;
        }

        // Flatten any blocks that may be contained in this one
        // and eliminate unreachable code.
        Block b = (Block) n;
        if (!((X10Ext) b.ext()).annotations().isEmpty()) {
            return n;
        }
        b = clean(flattenBlock(b));

        return b;
    }

    // Eval(StmtExpr(Block(S), e) ===> B(S, Eval(e))
    private Block sinkEval(StmtExpr stexp, Position pos) {
        Block b = nf.Block(pos, stexp.statements());
        if (!((X10Ext)stexp.ext()).annotations().isEmpty()) {
            b = (Block)((X10Ext)b.ext()).annotations(((X10Ext)stexp.ext()).annotations());
        }
        
        Expr result = stexp.result();
        if (result != null) {
            if (result instanceof StmtExpr) {
                b = b.append(sinkEval((StmtExpr)result, result.position()));
                b = clean(flattenBlock(b));
            } else {
                b = b.append(nf.Eval(result.position(), result));
            }
        }
        
        return b;
    }

    //Return(StmtExpr(Block(S), e)) ==> Block(S, return e)
    private Block sinkReturn(StmtExpr stexp, Position pos) {
        Block b = nf.Block(pos, stexp.statements());
        if (!((X10Ext)stexp.ext()).annotations().isEmpty()) {
            b = (Block)((X10Ext)b.ext()).annotations(((X10Ext)stexp.ext()).annotations());
        }
        Expr res = stexp.result();
        Stmt ret;
        if (res instanceof StmtExpr) {
            ret = clean(flattenBlock(sinkReturn((StmtExpr)res, pos)));
        } else {
            ret = nf.Return(pos, res);
        }
        
        b = b.append(ret);
        
        return b;
    }
    
    
    /**
     * Turns a Block into a list of Stmts.
     **/
    protected Block flattenBlock(Block b) {
        List<Stmt> stmtList = new LinkedList<Stmt>();
        boolean changeMade = false;
        boolean bIsStmtSeq = b instanceof StmtSeq;
        for (Stmt stmt : b.statements()) {
            if (stmt instanceof Block) {
                boolean innerIsStmtSeq = stmt instanceof StmtSeq;
                Block inner = (Block) stmt;
                if ((!bIsStmtSeq || innerIsStmtSeq) && ((X10Ext) inner.ext()).annotations().isEmpty()) {
                    // Alpha-rename local decls in the block that we're flattening.
                    if (report) System.out.println("Cleaning up a block" + inner.position());
                    if (reportStats) blockCount++;
                    if (!innerIsStmtSeq) inner = (Block) inner.visit(alphaRenamer);
                    // Could add a check here that scopeLevel is back to 0???
                    stmtList.addAll(inner.statements());
                    changeMade = true;
                } else {
                    if (report) System.out.println("StmtSeq holds a block " + b.position());
                    stmtList.add(inner);
                }
            } else {
                stmtList.add(stmt);
            }
        }
        if (changeMade) b = b.statements(stmtList);

        return b;
    }

    /**
     * Performs some trivial unreachable code elimination on a list of
     * statements.
     **/
    protected Block clean(Block b) {
        List<Stmt> stmtList = new LinkedList<Stmt>();
        boolean changeMade = false;
        // for (Stmt stmt : stl) {
        for (Iterator<Stmt> i = b.statements().iterator(); i.hasNext();) {
            Stmt stmt = i.next();
            
            if (stmt instanceof Empty) {
                // remove empty statements
                changeMade = true;
                continue;
            }
            stmtList.add(stmt);

            if (stmt instanceof Branch || stmt instanceof Return || stmt instanceof Throw) {
                if (i.hasNext()) {
                    if (report) System.out.println("Found a block ender in middle " + b);
                    if (reportStats) unreachableCount++;
                    changeMade = true;
                }
                break;
            }
        }

        if (changeMade) {
            b = b.statements(stmtList);
        }
        return b;
    }
    
    /**
     * Checks if `break label;` is the last statement in labeled block `label: { ... }`
     */
    private boolean isBreakLast(Labeled labeled) {
        Name label = labeled.labelNode().id();
        Stmt stmt = labeled.statement();
        if (stmt instanceof Block) {
            Block block = (Block) stmt;
            List<Stmt> statements = block.statements();
            Stmt last = statements.get(statements.size()-1);
            if (last instanceof Branch) {
                Branch branch = (Branch) last;
                return branch.kind() == Branch.BREAK && branch.labelNode() != null && branch.labelNode().id().equals(label);
            } else if (last instanceof Try) {
                Block tb  = ((Try)last).tryBlock();
                Stmt inner_last = tb.statements().get(tb.statements().size()-1);
                if (inner_last instanceof Branch) {
                    Branch branch = (Branch) inner_last;
                    return branch.kind() == Branch.BREAK && branch.labelNode() != null && branch.labelNode().id().equals(label);
                }
            }
        }
        return false;
    }
    
    private Block removeBreakLast(Labeled labeled) {
        List<Stmt> osts = ((Block) labeled.statement()).statements();
        List<Stmt> statements = new ArrayList<Stmt>(osts);
        Stmt last = statements.get(statements.size()-1);
        if (last instanceof Branch) {
            statements.remove(statements.size()-1);
        } else if (last instanceof Try) {
            Try t = ((Try)last);
            List<Stmt> old_tbstmts = t.tryBlock().statements();
            List<Stmt> tbstmts = new ArrayList<Stmt>(old_tbstmts);
            tbstmts.remove(tbstmts.size()-1);
            last = t.tryBlock(xnf.Block(((Try)last).tryBlock().position(), tbstmts));
            statements.set(statements.size()-1, last);
        }
        return xnf.Block(labeled.position(), statements);
    }

    @Override
    public void finish() {
        if (reportStats) {
            System.out.println("CodeCleanUp: Blocks removed " + blockCount);
            System.out.println("CodeCleanUp: Unreachable code removed " + unreachableCount);
        }
    }
}
