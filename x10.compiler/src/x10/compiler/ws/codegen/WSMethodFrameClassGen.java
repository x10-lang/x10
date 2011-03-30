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
import x10.compiler.ws.util.WSCodeGenUtility;
import x10.types.ParameterType;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;

import x10.types.X10MethodDef;
import x10.util.HierarchyUtils;
import x10.util.synthesizer.CodeBlockSynth;
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
    protected MethodSynth fastWrapperMethodSynth;
    protected Name returnFieldName;
    protected Name returnFlagName; //=> boolean returnFlagName;
    protected final boolean isMain;
    

    public WSMethodFrameClassGen(Job job, NodeFactory xnf, Context xct,
                                  X10MethodDef methodDef, MethodDecl methodDecl, WSTransformState wts) {
        this(job, xnf, xct, methodDef, methodDecl, wts,
                HierarchyUtils.isMainMethod(methodDef, xct));
    }
    public WSMethodFrameClassGen(Job job, NodeFactory xnf, Context xct,
                                  X10MethodDef methodDef, MethodDecl methodDecl, WSTransformState wts,
                                  boolean isMain) {
    
        super(job, xnf, xct, wts, WSCodeGenUtility.getMethodBodyClassName(methodDef),
             methodDecl.body(), ((ClassType) methodDef.container().get()).def(),
             methodDef.flags().isStatic() ? Flags.FINAL.Static() : Flags.FINAL,
                     isMain ? wts.mainFrameType : wts.regularFrameType);
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
        returnFlagName = ((Context)xct).makeFreshName("returnFlag");
        FieldSynth returnFlagSynth = classSynth.createField(compilerPos, returnFlagName.toString(), xts.Boolean());
        returnFlagSynth.addAnnotation(genUninitializedAnnotation());
        fieldNames.add(returnFlagName); //add it as one field for query
        
        Type returnType = methodDef.returnType().get();
        if (returnType != xts.Void()){
            returnFieldName = ((Context)xct).makeFreshName("result");
            FieldSynth resurnFieldSynth = classSynth.createField(compilerPos, returnFieldName.toString(), returnType);
            resurnFieldSynth.addAnnotation(genUninitializedAnnotation());
            fieldNames.add(returnFieldName); //add it as one field for query
            
            //and also need add "=> type" as one interface
            classSynth.addInterface(synth.simpleFunctionType(returnType, compilerPos));
        }
        
        // prepare the two wrapper method synth and need set their types
        X10ClassType containerClassType = (X10ClassType) methodDef.container().get();
        X10ClassDef containerClassDef = containerClassType.x10Def();

        String fastPathName = WSCodeGenUtility.getMethodFastPathName(methodDef);
        fastWrapperMethodSynth = new MethodSynth(xnf, xct, containerClassDef, fastPathName);
        fastWrapperMethodSynth.setFlag(methodDef.flags());
        fastWrapperMethodSynth.setReturnType(returnType);
       
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
        MethodSynth operatorMSynth = classSynth.createMethod(compilerPos, OPERATOR.toString());
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

        genWSMethod(fastWrapperMethodSynth); //now generate fast/slow path, register them and put them in container class

        if (isMain) {
            return getNewMainMethod();
        } else {
            return null;
        }
    }
    
    public MethodDecl getNewMainMethod() throws SemanticException{
        
        NewInstanceSynth rSynth = new NewInstanceSynth(xnf, xct, compilerPos, wts.rootFinishType);
        InstanceCallSynth riSynth = new InstanceCallSynth(xnf, xct, rSynth.genExpr(), "init");
        NewLocalVarSynth nvSynth = new NewLocalVarSynth(xnf, xct, compilerPos, Name.make("rootFinish"), Flags.FINAL, riSynth.genExpr(), wts.rootFinishType, Collections.EMPTY_LIST);

        //new _main(args)
        NewInstanceSynth niSynth = new NewInstanceSynth(xnf, xct, compilerPos, this.getClassType());
        niSynth.addAnnotation(genStackAllocateAnnotation());
        niSynth.addArgument(wts.frameType, nvSynth.getLocal());
        niSynth.addArgument(wts.finishFrameType, nvSynth.getLocal());
        for(Formal f : formals){
            //now add the type
            Type fType = f.localDef().type().get(); 
            Expr formalRef = xnf.Local(compilerPos, xnf.Id(compilerPos, f.name().id())).localInstance(f.localDef().asInstance()).type(fType);
            niSynth.addArgument(fType, formalRef);
        }
        Expr newMainExpr = niSynth.genExpr();
        // new _main(args).run();
        NewLocalVarSynth localSynth = new NewLocalVarSynth(xnf, xct, compilerPos, Flags.FINAL, newMainExpr);
        localSynth.addAnnotation(genStackAllocateAnnotation());

        Expr mainCall = synth.makeStaticCall(compilerPos, wts.workerType, Name.make("main"), Collections.<Expr>singletonList(localSynth.getLocal()), xts.Void(), xct);
        CodeBlockSynth cbSynth = new CodeBlockSynth(xnf, xct, methodDecl.body().position());
        cbSynth.addStmt(nvSynth.genStmt());
        cbSynth.addStmt(localSynth.genStmt());
        cbSynth.addStmt(xnf.Eval(compilerPos, mainCall));

        return (MethodDecl) methodDecl.body(cbSynth.close());
    }

    protected void genClassConstructor() throws SemanticException{
        super.genClassConstructor();
        //Continue to add other statements
        CodeBlockSynth conCodeSynth = conSynth.createConstructorBody(compilerPos);
        //all formals as constructor's formal
        //This ref
        Expr thisRef = synth.thisRef(classSynth.getDef().asType(), compilerPos);
        for(Formal f: formals){
            Expr fRef = conSynth.addFormal((Formal) f.copy());
            //make a field access
            Stmt s = xnf.Eval(compilerPos, 
                              synth.makeFieldAssign(compilerPos, thisRef, f.name().id(), fRef, xct));
            
            conCodeSynth.addStmt(s);
        }
    }
    
    /**
     * Generate one wrapper method 
     *   static def fib_fast(worker:Worker!, up:Frame!, ff:FinishFrame!, pc:Int, n:Int):Int {
     *       @StackAllocate val _tmp = @StackAllocate new _fib(up, ff, pc, n);
     *       return _tmp.fast(worker);
     *   }
     * @param containerClassDef the method's container class's def
     * @param methodName the newly method's name
     * @param targetClassDef the target class's def
     * @param targetMethodName the target method's name, fast or slow
     * @return the method synthesizer
     * @throws SemanticException 
     */
    private MethodSynth genWSMethod(MethodSynth methodSynth) throws SemanticException{
        
        String targetMethodName = FAST.toString();
        
        //now process the formals
        Expr workerRef = methodSynth.addFormal(compilerPos, Flags.FINAL, wts.workerType, WORKER.toString());
        Expr upRef = methodSynth.addFormal(compilerPos, Flags.FINAL, wts.frameType, UP.toString());
        Expr ffRef = methodSynth.addFormal(compilerPos, Flags.FINAL, wts.finishFrameType, FF.toString());
        
        //all other formals
        List<Expr> orgFormalRefs = new ArrayList<Expr>();
        List<Type> orgFormalTypes = new ArrayList<Type>();
        for(Formal f : methodDecl.formals()){
            orgFormalTypes.add(f.type().type());
            orgFormalRefs.add(methodSynth.addFormal(f)); //all formals are added in
        }
        //add all type parameters (template)
        X10MethodDecl mDecl = (X10MethodDecl)methodDecl;
        X10MethodDef mDef = mDecl.methodDef();
        int paramSize = mDef.typeParameters().size();
        for(int i = 0; i < paramSize; i++){
            methodSynth.addTypeParameter(mDef.typeParameters().get(i),
            							mDecl.typeParameters().get(i).variance());        	
        }

        //now create the body
        CodeBlockSynth mBodySynth = methodSynth.getMethodBodySynth(compilerPos);        
        NewInstanceSynth niSynth = new NewInstanceSynth(xnf, xct, compilerPos, classSynth.getClassDef().asType());
        niSynth.addArgument(wts.frameType, upRef);
        niSynth.addArgument(wts.finishFrameType, ffRef);
        niSynth.addArguments(orgFormalTypes, orgFormalRefs);
        niSynth.addAnnotation(genStackAllocateAnnotation());
        //special process for the new statement
        New newE = (New) niSynth.genExpr();        
        Special s = (Special) newE.qualifier();
        if(s != null){
            s = s.qualifier(null); //clear the type node            
            newE = newE.qualifier(s); //this method is outer, no need qualifier typenode
        }

        //special process for the new statement end
        
        NewLocalVarSynth localSynth = mBodySynth.createLocalVar(compilerPos, Flags.FINAL, newE);
        localSynth.addAnnotation(genStackAllocateAnnotation());
        Expr localRef = localSynth.getLocal(); //point to this inner class instance

        //_tmp.fast(worker) or _temp.slow(worker)
        InstanceCallSynth callSynth = new InstanceCallSynth(xnf, xct, localRef, targetMethodName);
        callSynth.addArgument(wts.workerType, workerRef);
        Expr callExpr = callSynth.genExpr();
        //if the method has return type, insert return, others, just call them
        if(callExpr.type() != null && callExpr.type() != xts.Void()){
            mBodySynth.addStmt(xnf.Return(compilerPos, callExpr));
        }
        else{
            mBodySynth.addStmt(xnf.Eval(callExpr.position(), callExpr));
        }
               
        return methodSynth;
    }
        
    
    public Name getReturnFieldName() {
        return returnFieldName;
    }
    
    public Name getReturnFlagName() {
        return returnFlagName;
    }


    public X10MethodDecl getWraperMethod() throws SemanticException{
        return fastWrapperMethodSynth.close();
    }
}
