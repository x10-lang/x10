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

import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.MethodDecl;
import polyglot.ast.New;
import polyglot.ast.NodeFactory;
import polyglot.ast.Return;
import polyglot.ast.Special;
import polyglot.ast.Stmt;
import polyglot.frontend.Job;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.MethodDef;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import x10.ast.TypeParamNode;
import x10.ast.X10MethodDecl;
import x10.compiler.ws.WSTransformState;
import x10.compiler.ws.util.WSUtil;
import x10.types.ParameterType;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;

import x10.types.X10MethodDef;
import x10.util.HierarchyUtils;
import x10.util.synthesizer.CodeBlockSynth;
import x10.util.synthesizer.ConstructorSynth;
import x10.util.synthesizer.FieldSynth;
import x10.util.synthesizer.InstanceCallSynth;
import x10.util.synthesizer.MethodSynth;
import x10.util.synthesizer.NewInstanceSynth;
import x10.util.synthesizer.NewLocalVarSynth;
import x10.visit.X10PrettyPrinterVisitor;

/**
 * @author Haichuan
 * 
 * Used to generate a normal method's fast/slow/back path
 * A method frame is a extension to a Regular frame
 * It need process formals, return values. It need provide fast/slow method
 * The inner class extends RegularFrame
 *
 */
public class WSMethodFrameClassGen extends WSRegularFrameClassGen {
    protected final MethodDecl methodDecl;
    protected List<Formal> formals; //original methods's all formals
    protected Name returnFieldName;
    protected Name returnFlagName; //=> boolean returnFlagName;
    protected final boolean isMain;
    

    public WSMethodFrameClassGen(Job job, NodeFactory xnf, Context xct,
                                  X10MethodDef methodDef, MethodDecl methodDecl, WSTransformState wts,
                                  boolean isMain) {
    
        super(job, xnf, xct, wts, WSUtil.getMethodBodyClassName(methodDef),
             methodDecl.body(), ((ClassType) methodDef.container().get()).def(),
             methodDef.flags().isStatic() ? Flags.FINAL.Static() : Flags.FINAL,
                     isMain ? job.extensionInfo().typeSystem().MainFrame() : job.extensionInfo().typeSystem().RegularFrame());
        //And if the method is instance method, need process the method body's all special
        //this/super need set the qualifier
        this.isMain = isMain;
        this.methodDecl = methodDecl;
        

        //processing the type parameters
        List<ParameterType> paramTypes = ((X10MethodDef)methodDef).typeParameters();
        List<TypeParamNode> paramNodes = ((X10MethodDecl)methodDecl).typeParameters();
        for(int i = 0; i < paramTypes.size(); i++){
        	classSynth.addTypeParameter(paramTypes.get(i), paramNodes.get(i).variance());
        }
        
        //processing the return
        returnFlagName = wsynth.createReturnFlagField(classSynth);
        fieldNames.add(returnFlagName); 
        
        Type returnType = methodDef.returnType().get();
        if (returnType != xts.Void()){
            returnFieldName = wsynth.createReturnValueField(classSynth, returnType);
            fieldNames.add(returnFieldName); //add it as one field for query 
            //and also need add "=> type" as one interface
            classSynth.addInterface(synth.simpleFunctionType(returnType, compilerPos));
        }
       
        formals = methodDecl.formals(); //record all formals
        
        //finally changes fast/slow method's return type
        fastMSynth.setReturnType(returnType);
        genMethodFormalAsFields();
    }
    
    /**
     * Generate operator() method for non-void method's inner class
     * @param returnType
     * @throws SemanticException
     */
    protected void genOperatorMethod(Type returnType) throws SemanticException{
        MethodSynth operatorMSynth = classSynth.createMethod(classSynth.pos(), WSSynthesizer.OPERATOR.toString());
        operatorMSynth.setFlag(Flags.PUBLIC);
        operatorMSynth.setReturnType(returnType);
        CodeBlockSynth operatorMBSynth = operatorMSynth.getMethodBodySynth(compilerPos);
        //return result;
        Return r = xnf.Return(compilerPos, synth.makeFieldAccess(compilerPos, getThisRef(), returnFieldName, xct));
        operatorMBSynth.addStmt(r);
    }
    
    protected void genMethodFormalAsFields(){ 
        //transform formals as fields
        for(Formal f:formals){
            Name fieldName = f.name().id();
            classSynth.createField(compilerPos, fieldName.toString(), f.type().type()).setFlags(f.flags().flags());
            fieldNames.add(fieldName);
        }
    }
    
    public MethodDecl transform() throws SemanticException {
        Type returnType = methodDecl.methodDef().returnType().get();
        if (returnType != xts.Void()){
            genOperatorMethod(returnType);
        }
        
        genClass();

        //Generate the wrapper method
        if (isMain) {
            return wsynth.genNewMainMethod(classSynth, methodDecl);
        } else {
            return null;
        }
    }
    


    protected void genClassConstructor() throws SemanticException{
        ConstructorSynth conSynth = wsynth.genClassConstructorType2Base(classSynth);
        CodeBlockSynth codeBlockSynth = conSynth.createConstructorBody(compilerPos);
        //all formals as constructor's formal
        //This ref
        Expr thisRef = wsynth.genThisRef(classSynth);
        for(Formal f: formals){
            Expr fRef = conSynth.addFormal((Formal) f.copy());
            //make a field access
            Stmt s = xnf.Eval(compilerPos, 
                              synth.makeFieldAssign(compilerPos, thisRef, f.name().id(), fRef, xct));
            
            codeBlockSynth.addStmt(s);
        }
    }
        
    
    public Name getReturnFieldName() {
        return returnFieldName;
    }
    
    public Name getReturnFlagName() {
        return returnFlagName;
    }


    public X10MethodDecl getWraperMethod() throws SemanticException{
        return wsynth.genWSMethod(classSynth, (X10MethodDecl) methodDecl);
    }
}
