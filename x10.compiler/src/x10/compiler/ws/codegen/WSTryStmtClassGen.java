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


package x10.compiler.ws.codegen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.Catch;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Stmt;
import polyglot.ast.Try;
import polyglot.ast.TypeNode;
import polyglot.types.ClassDef;
import polyglot.types.Flags;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import x10.ast.Finish;
import x10.compiler.ws.util.AddIndirectLocalDeclareVisitor;
import x10.compiler.ws.util.ClosureDefReinstantiator;
import x10.compiler.ws.util.TransCodes;
import x10.compiler.ws.util.WSUtil;
import x10.types.X10ClassType;
import x10.util.synthesizer.ClassSynth;
import x10.util.synthesizer.CodeBlockSynth;
import x10.util.synthesizer.ConstructorSynth;
import x10.util.synthesizer.InstanceCallSynth;
import x10.util.synthesizer.SuperCallSynth;
import x10.util.synthesizer.SwitchSynth;

/**
 * @author Haichuan
 * 
 * Transform a try stmt to a inner class
 * 
 */
public class WSTryStmtClassGen extends AbstractWSClassGen {
    
    protected Try tryStmt;
    
    public WSTryStmtClassGen(AbstractWSClassGen parent, Try tryStmt) {
        super(parent, parent,
                WSUtil.getExceptionFrameClassName(parent.getClassName()),
                parent.xts.TryFrame(), tryStmt.tryBlock());
        this.tryStmt = tryStmt;
        //Never need PC value in fact
//        if(WSOptimizeConfig.OPT_PC_FIELD == 0){
//            addPCField();
//        }
    }

    /*
     * The constructor is the same as a regular frame's constructor
     * (non-Javadoc)
     * @see x10.compiler.ws.codegen.AbstractWSClassGen#genClassConstructor()
     */
    protected void genClassConstructor() throws SemanticException {
        ConstructorSynth conSynth = wsynth.genClassConstructorType2Base(classSynth);
    }

    @Override
    protected void genMethods() throws SemanticException {
        
        CodeBlockSynth fastBodySynth = fastMSynth.getMethodBodySynth(compilerPos);
        CodeBlockSynth resumeBodySynth = resumeMSynth.getMethodBodySynth(compilerPos);
        CodeBlockSynth backBodySynth = backMSynth.getMethodBodySynth(compilerPos);
        

        AbstractWSClassGen childFrameGen = genChildFrame(xts.RegularFrame(), codeBlock, WSUtil.getBlockFrameClassName(getClassName()));
        List<Stmt> stmts = wsynth.genInvocateFrameStmts(1, classSynth, fastMSynth, childFrameGen);
        
        //now add codes to three path;
        //FIXME: just a simple try. Not correct
        //Some style
        //try {  child frame call }
        //catch { original code but replace the local var access with field access}
        //finally {still replace the local var access with field access}
        // 
        
        
        //fast path & resume path
        Block tryBlockFast = xnf.Block(tryStmt.tryBlock().position(), stmts);
        Block tryBlockResume = xnf.Block(tryStmt.tryBlock().position(), wsynth.genRethrowStmt(resumeMSynth));
        
        Try tryFast = tryStmt.tryBlock(tryBlockFast);
        Try tryResume = tryStmt.tryBlock(tryBlockResume);
        
        List<Catch> catchBlocksFast = new ArrayList<Catch>();
        List<Catch> catchBlocksResume = new ArrayList<Catch>();

        Name formalName = xct.getNewVarName();
        
        Formal fa = synth.createFormal(compilerPos, xts.Abort(), formalName, Flags.NONE);
        Stmt ea = xnf.Throw(compilerPos, xnf.Local(compilerPos, xnf.Id(compilerPos, formalName)).localInstance(fa.localDef().asInstance()).type(xts.Abort()));
        Catch ca = xnf.Catch(compilerPos, fa, xnf.Block(compilerPos, ea));
        
        catchBlocksFast.add(ca);
        catchBlocksResume.add(ca);
        for(Catch c: tryStmt.catchBlocks()){
            //note there is only one local var, the exception
            int pc = 1; //No need the pc;
            TransCodes catchBody = transNormalStmt(c.body(), pc, Collections.singleton(c.formal().name().id()));
            catchBlocksFast.add(c.body(WSUtil.stmtToBlock(xnf, catchBody.getFastStmts().get(0))));
            catchBlocksResume.add(c.body(WSUtil.stmtToBlock(xnf, catchBody.getResumeStmts().get(0))));
        }
        tryFast = tryFast.catchBlocks(catchBlocksFast);
        tryResume = tryResume.catchBlocks(catchBlocksResume);
        
        if (tryStmt.finallyBlock() != null) {
            int pc = 1;
            TransCodes finalBody = transNormalStmt(tryStmt.finallyBlock(), pc, Collections.EMPTY_SET);
            tryFast.finallyBlock(WSUtil.stmtToBlock(xnf, finalBody.getFastStmts().get(0)));
            tryResume.finallyBlock(WSUtil.stmtToBlock(xnf, finalBody.getResumeStmts().get(0)));
        }
        fastBodySynth.addStmt(tryFast);
        resumeBodySynth.addStmt(tryResume);
   }
}
