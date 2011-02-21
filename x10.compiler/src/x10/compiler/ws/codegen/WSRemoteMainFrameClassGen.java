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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.If;
import polyglot.ast.Local;
import polyglot.ast.Stmt;
import polyglot.types.Flags;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Pair;
import x10.ast.AtStmt;
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
 * Generate the remote main frame class
 * It is used to express at(p) S and async at(p) S
 *
 */
public class WSRemoteMainFrameClassGen extends WSRegularFrameClassGen {
    protected final AbstractWSClassGen parentR; //it's a remote parent, cannot accessed directly
    protected final AtStmt atStmt;
    protected final boolean isAsync;
    protected final List<Pair<Name,Type>> formals; //the formals are not real formals, but local var copied from parent frames;
    
    
    public WSRemoteMainFrameClassGen(AbstractWSClassGen parent, AtStmt atStmt, boolean isAsync) {
        super(parent, null, //up frame is null
              atStmt.body(),
              WSCodeGenUtility.getRemoteRemoteClassName(parent.getClassName()), parent.wts.remoteMainFrameType);
        this.atStmt = atStmt;
        this.isAsync = isAsync;
        this.parentR = parent; //the parent is not the real parent
        formals = new ArrayList<Pair<Name, Type>>();
        
        //need add formal blockFlag if it is an atsmt
        if(!isAsync){
            classSynth.createField(compilerPos, BLOCK_FLAG.toString(), wts.globalRefBBType);
        }
        
    }
    
    public boolean isAsync() {
        return isAsync;
    }
    
    public Expr getPlace(){
        return atStmt.place();
    }
    
    /**
     * This method will be called by localvartofieldaccess replacer
     * If a local var is found in remote main frame, but defined above outside it
     * it should be copied into the remote frame as a formal
     * 
     * And it should be called in case of "at(p)S" to add the flag as one formal
     * 
     * For a local var, it should be called only once. This is enforced by the cache in localvartofieldaccess
     * @param name
     * @param type
     */
    protected void addFormal(Name name, Type type){
        formals.add(new Pair<Name, Type>(name, type));
        //now create a field 
        classSynth.createField(compilerPos, name.toString(), type);
        fieldNames.add(name);
    }

    /* 
     * Need add blockFlag and other accessed outside local var as formals.
     * @see x10.compiler.ws.codegen.WSRegularFrameClassGen#genClassConstructor()
     */
    protected void genClassConstructor() throws SemanticException{
        super.genClassConstructor();
        //Continue to add other statements
        CodeBlockSynth conCodeSynth = conSynth.createConstructorBody(compilerPos);
        
        //the block flag one

        //This ref
        Expr thisRef = synth.thisRef(classSynth.getDef().asType(), compilerPos);
        
        if(!isAsync){
            Expr blockFlagRef = conSynth.addFormal(compilerPos, Flags.FINAL, wts.globalRefBBType, BLOCK_FLAG);
            //make a field access
            Stmt flagAssign = xnf.Eval(compilerPos, 
                              synth.makeFieldAssign(compilerPos, thisRef, BLOCK_FLAG, blockFlagRef, xct));
            conCodeSynth.addStmt(flagAssign);            
        }
        //all formals as constructor's formal
        for(Pair<Name, Type> formal: formals){
            Name formalName = formal.fst();
            Type formalType = formal.snd();
            
            Expr fRef = conSynth.addFormal(compilerPos, Flags.FINAL, formalType, formalName);
            //make a field access
            Stmt s = xnf.Eval(compilerPos, 
                              synth.makeFieldAssign(compilerPos, thisRef, formalName, fRef, xct));
            conCodeSynth.addStmt(s);
        }
    }

    /* 
     * For at stmt case, the fast/resume path need all add: Worker.remoteAtNotify(bbRef);
     * @see x10.compiler.ws.codegen.WSRegularFrameClassGen#transformMethodBody()
     */
    @Override
    protected Triple<CodeBlockSynth, SwitchSynth, SwitchSynth> transformMethodBody() throws SemanticException {
        Triple<CodeBlockSynth, SwitchSynth, SwitchSynth> methodBodyCodes = super.transformMethodBody();
        
        if(!isAsync){
            Stmt remoteCallStmt = genRemoteAtNotifyStmt();
            methodBodyCodes.first().addStmt(remoteCallStmt);
            ArrayList<Integer> switchTable = methodBodyCodes.second().getSwitchTable();
            int pcValue = switchTable.get(switchTable.size() - 1);
            methodBodyCodes.second().insertStatementInCondition(pcValue, remoteCallStmt);
        }

        return methodBodyCodes;
    }


    /**
     * @return a statement "Worker.remoteAtNotify(bbRef);"
     * @throws SemanticException 
     */
    protected Stmt genRemoteAtNotifyStmt() throws SemanticException{
        //get the block flag
        Expr flagRef = synth.makeFieldAccess(compilerPos, getThisRef(), BLOCK_FLAG, xct);
        
        //Worker.remoteAtNotify(flagRef);
        Call call = synth.makeStaticCall(compilerPos, wts.workerType, REMOTE_AT_NOTIFY, Collections.singletonList(flagRef), xts.Void(), xct);
        return xnf.Eval(compilerPos, call);
    }





    
}
