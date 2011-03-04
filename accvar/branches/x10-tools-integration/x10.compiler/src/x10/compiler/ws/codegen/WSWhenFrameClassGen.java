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


package x10.compiler.ws.codegen;

import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Expr;
import polyglot.ast.If;
import polyglot.ast.Local;
import polyglot.ast.Stmt;
import polyglot.types.SemanticException;
import x10.ast.When;
import x10.compiler.ws.util.AddIndirectLocalDeclareVisitor;
import x10.compiler.ws.util.ClosureDefReinstantiator;
import x10.compiler.ws.util.Triple;
import x10.compiler.ws.util.WSCodeGenUtility;
import x10.util.synthesizer.CodeBlockSynth;
import x10.util.synthesizer.InstanceCallSynth;
import x10.util.synthesizer.MethodSynth;
import x10.util.synthesizer.NewLocalVarSynth;
import x10.util.synthesizer.SwitchSynth;

/**
 * @author Haichuan
 * 
 * Generate the when class
 * Currently, it only supports the first branch of when
 *
 */
public class WSWhenFrameClassGen extends WSRegularFrameClassGen {
    protected final When whenStmt;
    
    public WSWhenFrameClassGen(AbstractWSClassGen parent, When whenStmt) {
        super(parent, whenStmt.stmt(),
              WSCodeGenUtility.getWhenClassName(parent.getClassName()));
        this.whenStmt = whenStmt;
    }
    
    
    
    /* This method is different to the regular frame's genTreeMethods,
     * Besides the fast/resume/back, it needs a condition method
     * @see x10.compiler.ws.codegen.WSRegularFrameClassGen#genThreeMethods()
     */
    @Override
    protected void genMethods() throws SemanticException { 
        //firstly translate the bodies
        Triple<CodeBlockSynth, SwitchSynth, SwitchSynth> bodyCodes = transformMethodBody();
        
        //now prepare the body synth
        CodeBlockSynth fastBodySynth = genPathBody(fastMSynth, bodyCodes.first().close());
        CodeBlockSynth resumeBodySynth = genPathBody(resumeMSynth, bodyCodes.second().genStmt());
        CodeBlockSynth backBodySynth = backMSynth.getMethodBodySynth(whenStmt.position());
        
        //finally the back
        backBodySynth.addStmt(bodyCodes.third());
        
        //need final process closure issues
        fastBodySynth.addCodeProcessingJob(new ClosureDefReinstantiator(xts, xct,
                                                                        this.getClassDef(),
                                                                        fastMSynth.getDef()));
        
        resumeBodySynth.addCodeProcessingJob(new ClosureDefReinstantiator(xts, xct,
                                                                        this.getClassDef(),
                                                                        resumeMSynth.getDef()));
        //add all references

        fastBodySynth.addCodeProcessingJob(new AddIndirectLocalDeclareVisitor(xnf, this.getRefToDeclMap()));
        resumeBodySynth.addCodeProcessingJob(new AddIndirectLocalDeclareVisitor(xnf, this.getRefToDeclMap()));    
        backBodySynth.addCodeProcessingJob(new AddIndirectLocalDeclareVisitor(xnf, this.getRefToDeclMap()));
    }
    
    /**
     * Synthesize the following codes
     * var b:Boolean;
     * atomic if (b = condition()) {
     *    // fast body
     * }
     * if (!b) redo(worker);
     * @param methodSynth
     * @param bodyStmt
     * @return
     * @throws SemanticException
     */
    protected CodeBlockSynth genPathBody(MethodSynth methodSynth, Stmt bodyStmt) throws SemanticException{
        
        CodeBlockSynth bodySynth = methodSynth.getMethodBodySynth(whenStmt.position());
        
        NewLocalVarSynth bVarSynth  = new NewLocalVarSynth(xnf, xct, whenStmt.position(), xts.Boolean());
        Local bVar = bVarSynth.getLocal();
        bodySynth.addStmt(bVarSynth);
        
        //b = condition();
        Expr orgWhenExpr = (Expr) this.replaceLocalVarRefWithFieldAccess(whenStmt.expr());
        Expr assign = xnf.LocalAssign(whenStmt.position(), bVar, Assign.ASSIGN, orgWhenExpr).type(orgWhenExpr.type());
        
        If ifStmt = xnf.If(whenStmt.position(), assign, bodyStmt);
        
        //finally, put it into atomic
        Stmt atmCheck = xnf.Atomic(whenStmt.position(), xnf.Here(compilerPos).type(xts.Place()), ifStmt); 
        bodySynth.addStmt(atmCheck);
        
        //!b
        Expr redoCheck = xnf.Binary(whenStmt.position(), bVar, Binary.EQ,
               synth.booleanValueExpr(false, whenStmt.position())).type(xts.Boolean());
        
        //redo(worker)
        Expr thisRef = genUpcastCall(getClassType(), wts.regularFrameType, getThisRef());
        InstanceCallSynth redoCallSynth = new InstanceCallSynth(xnf, xct, whenStmt.position(), thisRef, REDO.toString());
        Expr workerRef = bodySynth.getLocal(WORKER.toString());
        redoCallSynth.addArgument(wts.workerType, workerRef);
        Stmt ifRedoStmt = xnf.If(whenStmt.position(), redoCheck, redoCallSynth.genStmt());
        bodySynth.addStmt(ifRedoStmt);
        return bodySynth;
    }
    
}
