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
import polyglot.types.ClassType;
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
import x10.compiler.ws.util.WSUtil;
import x10.util.synthesizer.CodeBlockSynth;
import x10.util.synthesizer.ConstructorSynth;
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
    protected final List<Pair<Name,Type>> formals; //the formals are not real formals, but local var copied from parent frames;
    
    
    public WSRemoteMainFrameClassGen(AbstractWSClassGen parent, AtStmt atStmt) {
        super(parent, null, //up frame is null
              atStmt.body(),
              WSUtil.getRemoteRemoteClassName(parent.getClassName()), parent.xts.RegularFrame());
        this.atStmt = atStmt;
        this.parentR = parent; //the parent is not the real parent
        formals = new ArrayList<Pair<Name, Type>>();
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
     * And need set the _pc = 0, to make sure the resume could be called directly
     * @see x10.compiler.ws.codegen.WSRegularFrameClassGen#genClassConstructor()
     */
    protected void genClassConstructor() throws SemanticException{
        ConstructorSynth conSynth = wsynth.genClassConstructorType2Base(classSynth);
        CodeBlockSynth codeBlockSynth = conSynth.createConstructorBody(compilerPos);
        //This ref
        Expr thisRef = wsynth.genThisRef(classSynth);
        
        //all formals as constructor's formal
        for(Pair<Name, Type> formal: formals){
            Name formalName = formal.fst();
            Type formalType = formal.snd();
            
            Expr fRef = conSynth.addFormal(compilerPos, Flags.FINAL, formalType, formalName);
            //make a field access
            Stmt s = xnf.Eval(compilerPos, 
                              synth.makeFieldAssign(compilerPos, thisRef, formalName, fRef, xct));
            codeBlockSynth.addStmt(s);
        }
        //assign pc
        codeBlockSynth.addStmt(wsynth.genPCAssign(classSynth, 0));
    }

    /* 
     * Cannot inline RemoteMainFrame's fast path
     * @see x10.compiler.ws.codegen.AbstractWSClassGen#isFastPathInline()
     */
    public boolean isFastPathInline(ClassType frameType){
        return false; //default true;
    }
}
