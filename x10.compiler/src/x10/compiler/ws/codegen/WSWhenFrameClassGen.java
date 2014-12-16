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


package x10.compiler.ws.codegen;

import java.util.Collections;

import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.Catch;
import polyglot.ast.Expr;
import polyglot.ast.If;
import polyglot.ast.Local;
import polyglot.ast.Stmt;
import polyglot.types.Flags;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import x10.ast.When;
import x10.compiler.ws.util.AddIndirectLocalDeclareVisitor;
import x10.compiler.ws.util.ClosureDefReinstantiator;
import x10.compiler.ws.util.Triple;
import x10.compiler.ws.util.WSUtil;
import x10.util.Synthesizer;
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
              WSUtil.getWhenClassName(parent.getClassName()));
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
        
        //TODO: we should be using transNormalStmt here (Olivier)
        CodeBlockSynth fastBodySynth = genPathBody(fastMSynth, bodyCodes.first().close());
        CodeBlockSynth resumeBodySynth = genPathBody(resumeMSynth, bodyCodes.first().close());
        CodeBlockSynth backBodySynth = backMSynth.getMethodBodySynth(whenStmt.position());
        
        //finally the back
        backBodySynth.addStmt(bodyCodes.third());
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
        
        NewLocalVarSynth bVarSynth  = new NewLocalVarSynth(xnf, xct, whenStmt.position(), Flags.NONE, xnf.BooleanLit(whenStmt.position(), false).type(xts.Boolean()));
        Local bVar = bVarSynth.getLocal();
        bodySynth.addStmt(bVarSynth);
        
        //b = condition();
        Expr orgWhenExpr = (Expr) this.replaceLocalVarRefWithFieldAccess(whenStmt.expr());
        Expr assign = xnf.LocalAssign(whenStmt.position(), bVar, Assign.ASSIGN, orgWhenExpr).type(orgWhenExpr.type());
        
        If ifStmt = xnf.If(whenStmt.position(), assign, bodyStmt);
        Stmt enter = xnf.Eval(whenStmt.position(), synth.makeStaticCall(whenStmt.position(), xts.Runtime(), WSSynthesizer.ENTER_ATOMIC, xts.Void(), xct));
        Stmt exit = xnf.Eval(whenStmt.position(), synth.makeStaticCall(whenStmt.position(), xts.Runtime(), WSSynthesizer.EXIT_WHEN, Collections.<Expr>singletonList(xnf.Local(whenStmt.position(), bVar.name()).localInstance(bVar.localInstance()).type(xts.Boolean())), xts.Void(), xct));
   
        Block fin = xnf.Block(whenStmt.position(), exit);
        //finally, put it into atomic
        Stmt atmCheck = xnf.Try(whenStmt.position(), xnf.Block(whenStmt.position(), enter, ifStmt), Collections.<Catch>emptyList(), fin); 
        bodySynth.addStmt(atmCheck);
        
        //!b
        Expr redoCheck = xnf.Binary(whenStmt.position(), bVar, Binary.EQ,
               synth.booleanValueExpr(false, whenStmt.position())).type(xts.Boolean());
        
        //continueLater(worker)
        Stmt redoStmt = wsynth.genContinueLaterStmt(classSynth, methodSynth);
        Stmt ifRedoStmt = xnf.If(whenStmt.position(), redoCheck, redoStmt);
        bodySynth.addStmt(ifRedoStmt);
        return bodySynth;
    }
    
}
