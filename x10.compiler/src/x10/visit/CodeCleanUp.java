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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import polyglot.ast.Block;
import polyglot.ast.Branch;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.For;
import polyglot.ast.Id;
import polyglot.ast.Labeled;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.Loop;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Return;
import polyglot.ast.Stmt;
import polyglot.ast.SwitchBlock;
import polyglot.ast.Throw;
import polyglot.frontend.Job;
import polyglot.main.Reporter;
import polyglot.types.LocalDef;
import polyglot.types.Name;
import polyglot.types.TypeSystem;
import polyglot.util.CollectionUtil;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.AtStmt;
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
    protected boolean report;
    protected boolean reportStats;
    static protected long blockCount;
    static protected long unreachableCount;
    protected X10AlphaRenamer alphaRenamer;
    protected Map<Name, Boolean> labelInfo;

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
            labelInfo.put(labelName, Boolean.FALSE);
        } else if (child instanceof Branch) {
            Id labelNode = ((Branch) child).labelNode();
            if (labelNode != null) {
                labelInfo.put(labelNode.id(), Boolean.TRUE);
            }
        }

        return this;        
    }

    @Override
    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) {
        if (n instanceof Labeled) {
            Boolean used = labelInfo.remove(((Labeled) n).labelNode().id());
            // If the label was never used, then eliminate the Labeled node and just return the statement itself.
            return used.booleanValue() ? n : ((Labeled)n).statement();
        } 
        
        if (n instanceof Eval && ((Eval)n).expr() instanceof StmtExpr  && !(parent instanceof For)) {
            return sinkEval((StmtExpr)((Eval)n).expr(), n.position());
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
                    // Alpha-rename local decls in the block that we're
                    // flattening.
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

    public void finish() {
        if (reportStats) {
            System.out.println("CodeCleanUp: Blocks removed " + blockCount);
            System.out.println("CodeCleanUp: Unreachable code removed " + unreachableCount);
        }
    }
}
