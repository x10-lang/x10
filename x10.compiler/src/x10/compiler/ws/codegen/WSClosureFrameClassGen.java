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
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.MethodDef;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import x10.ast.Closure;
import x10.ast.TypeParamNode;
import x10.ast.X10MethodDecl;
import x10.compiler.ws.WSTransformState;
import x10.compiler.ws.util.WSUtil;
import x10.types.ClosureDef;
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
 * Used to generate a closure fast/slow/back path
 * A closure frame is a extension to a Regular frame
 * It need process formals, return values. It need provide fast/slow method
 * The inner class extends RegularFrame.
 * And it will return the closure with the wrapper code
 *
 */
public class WSClosureFrameClassGen extends WSRegularFrameClassGen {
    protected final Closure closure;
    protected List<Formal> formals; //original methods's all formals
    protected MethodSynth fastWrapperMethodSynth;
    protected Name returnFieldName;
    protected Name returnFlagName; //=> boolean returnFlagName;
    

    public WSClosureFrameClassGen(Job job, NodeFactory xnf, Context xct,
    		Closure closure, X10ClassDef containerClassDef, WSTransformState wts) {
    
        super(job, xnf, xct, wts, WSUtil.getClosureBodyClassName(closure),
             closure.body(), containerClassDef,
             Flags.FINAL.Static(), job.extensionInfo().typeSystem().RegularFrame());


        this.closure = closure;
        
        ClosureDef closureDef = closure.closureDef();
        
        //processing the type parameters
        List<ParameterType> paramTypes = closureDef.typeParameters();
        //TODO: need check the closure's type paramters;
        //List<TypeParamNode> paramNodes = closure.typeParameters();
        for(int i = 0; i < paramTypes.size(); i++){
        	classSynth.addTypeParameter(paramTypes.get(i), ParameterType.Variance.INVARIANT/*paramNodes.get(i).variance()*/);
        }
        
        //processing the return
        returnFlagName = wsynth.createReturnFlagField(classSynth);
        fieldNames.add(returnFlagName); 
        
        Type returnType = closureDef.returnType().get();
        if (returnType != xts.Void()){
            returnFieldName = wsynth.createReturnValueField(classSynth, returnType);
            fieldNames.add(returnFieldName); //add it as one field for query 
            //and also need add "=> type" as one interface
            classSynth.addInterface(synth.simpleFunctionType(returnType, compilerPos));
        }
        
        formals = closure.formals(); //record all formals
        
        //finally changes fast/slow method's return type
        genMethodFormalAsFields();
    }
    
    /**
     * Generate apply() method for non-void method's inner class
     * @param returnType
     * @throws SemanticException
     */
    protected void genApplyMethod(Type returnType) throws SemanticException{
        MethodSynth applyMSynth = classSynth.createMethod(classSynth.pos(), "apply");
        applyMSynth.setFlag(Flags.PUBLIC);
        applyMSynth.setReturnType(returnType);
        CodeBlockSynth applyMBSynth = applyMSynth.getMethodBodySynth(compilerPos);
        //return result;
        
        Return r = xnf.Return(compilerPos, synth.makeFieldAccess(compilerPos, getThisRef(), returnFieldName, xct));
        applyMBSynth.addStmt(r);
    }
    
    protected void genMethodFormalAsFields(){ 
        //transform formals as fields
        for(Formal f:formals){
            Name fieldName = f.name().id();
            classSynth.createField(compilerPos, fieldName.toString(), f.type().type()).setFlags(f.flags().flags());
            fieldNames.add(fieldName);
        }
    }
    
    public Closure transform() throws SemanticException {
        Type returnType = closure.returnType().type();
        if (returnType != xts.Void()){
            genApplyMethod(returnType);
        }
        
        genClass();
        return getNewClosure();
    }
    
    public Closure getNewClosure() throws SemanticException{
        
        NewInstanceSynth rSynth = new NewInstanceSynth(xnf, xct, compilerPos, xts.RootFinish());
        InstanceCallSynth riSynth = new InstanceCallSynth(xnf, xct, rSynth.genExpr(), "init");
        NewLocalVarSynth nvSynth = new NewLocalVarSynth(xnf, xct, compilerPos, Name.make("rootFinish"), Flags.FINAL, riSynth.genExpr(), xts.RootFinish(), Collections.EMPTY_LIST);

        //new _main(args)
        NewInstanceSynth niSynth = new NewInstanceSynth(xnf, xct, compilerPos, this.getClassType());
        niSynth.addArgument(xts.Frame(), nvSynth.getLocal());
        niSynth.addArgument(xts.FinishFrame(), nvSynth.getLocal());
        for(Formal f : formals){
            //now add the type
            Type fType = f.localDef().type().get(); 
            Expr formalRef = xnf.Local(compilerPos, xnf.Id(compilerPos, f.name().id())).localInstance(f.localDef().asInstance()).type(fType);
            niSynth.addArgument(fType, formalRef);
        }
        Expr newMainExpr = niSynth.genExpr();
        // new _main(args).run();
        InstanceCallSynth icSynth = new InstanceCallSynth(xnf, xct, newMainExpr, "run");

        
        CodeBlockSynth cbSynth = new CodeBlockSynth(xnf, xct, closure.body().position());
        cbSynth.addStmt(nvSynth.genStmt());
        cbSynth.addStmt(icSynth.genStmt());

        return (Closure) closure.body(cbSynth.close());
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
//    private MethodSynth genWSMethod(MethodSynth methodSynth) throws SemanticException{
//        
//        String targetMethodName = FAST.toString();
//        
//        //now process the formals
//        Expr workerRef = methodSynth.addFormal(compilerPos, Flags.FINAL, xts.Worker(), WORKER.toString());
//        Expr upRef = methodSynth.addFormal(compilerPos, Flags.FINAL, ts.Frame(), UP.toString());
//        Expr ffRef = methodSynth.addFormal(compilerPos, Flags.FINAL, ts.FinishFrame(), FF.toString());
//        
//        //all other formals
//        List<Expr> orgFormalRefs = new ArrayList<Expr>();
//        List<Type> orgFormalTypes = new ArrayList<Type>();
//        for(Formal f : methodDecl.formals()){
//            orgFormalTypes.add(f.type().type());
//            orgFormalRefs.add(methodSynth.addFormal(f)); //all formals are added in
//        }
//        //add all type parameters (template)
//        X10MethodDecl mDecl = (X10MethodDecl)methodDecl;
//        X10MethodDef mDef = mDecl.methodDef();
//        int paramSize = mDef.typeParameters().size();
//        for(int i = 0; i < paramSize; i++){
//            methodSynth.addTypeParameter(mDef.typeParameters().get(i),
//            							mDecl.typeParameters().get(i).variance());        	
//        }
//
//        //now create the body
//        CodeBlockSynth mBodySynth = methodSynth.getMethodBodySynth(compilerPos);        
//        NewInstanceSynth niSynth = new NewInstanceSynth(xnf, xct, compilerPos, classSynth.getClassDef().asType());
//        niSynth.addArgument(ts.Frame(), upRef);
//        niSynth.addArgument(ts.FinishFrame(), ffRef);
//        niSynth.addArguments(orgFormalTypes, orgFormalRefs);
//        niSynth.addAnnotation(genStackAllocateAnnotation());
//        //special process for the new statement
//        New newE = (New) niSynth.genExpr();        
//        Special s = (Special) newE.qualifier();
//        if(s != null){
//            s = s.qualifier(null); //clear the type node            
//            newE = newE.qualifier(s); //this method is outer, no need qualifier typenode
//        }
//
//        //special process for the new statement end
//        
//        NewLocalVarSynth localSynth = mBodySynth.createLocalVar(compilerPos, Flags.FINAL, newE);
//        localSynth.addAnnotation(genStackAllocateAnnotation());
//        Expr localRef = localSynth.getLocal(); //point to this inner class instance
//
//        //_tmp.fast(worker) or _temp.slow(worker)
//        InstanceCallSynth callSynth = new InstanceCallSynth(xnf, xct, localRef, targetMethodName);
//        callSynth.addArgument(xts.Worker(), workerRef);
//        Expr callExpr = callSynth.genExpr();
//        //if the method has return type, insert return, others, just call them
//        if(callExpr.type() != null && callExpr.type() != xts.Void()){
//            mBodySynth.addStmt(xnf.Return(compilerPos, callExpr));
//        }
//        else{
//            mBodySynth.addStmt(xnf.Eval(callExpr.position(), callExpr));
//        }
//               
//        return methodSynth;
//    }
        
    
    public Name getReturnFieldName() {
        return returnFieldName;
    }
    
    public Name getReturnFlagName() {
        return returnFlagName;
    }
}
