package x10.compiler.ws.codegen;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.MethodDecl;
import polyglot.ast.New;
import polyglot.ast.Return;
import polyglot.ast.Special;
import polyglot.ast.Stmt;
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.MethodDef;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Pair;
import x10.ast.X10MethodDecl;
import x10.ast.X10NodeFactory;
import x10.compiler.ws.WSCodeGenerator;
import x10.compiler.ws.WSTransformState;
import x10.compiler.ws.util.TransCodes;
import x10.compiler.ws.util.WSCodeGenUtility;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10Context;
import x10.util.synthesizer.CodeBlockSynth;
import x10.util.synthesizer.ConstructorSynth;
import x10.util.synthesizer.FieldSynth;
import x10.util.synthesizer.InstanceCallSynth;
import x10.util.synthesizer.MethodSynth;
import x10.util.synthesizer.NewInstanceSynth;
import x10.util.synthesizer.NewLocalVarSynth;

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
    

    public WSMethodFrameClassGen(Job job, X10NodeFactory xnf, X10Context xct,
                                  MethodDef methodDef, MethodDecl methodDecl, WSTransformState wsTransformState) {
    
        super(job, xnf, xct, wsTransformState, null/*A method has no parent*/,
             WSCodeGenUtility.getMethodBodyClassName(methodDef));
        this.methodDecl = methodDecl;
        frameDepth = 0;
        //now consider the flags/kind and outer class
        if(methodDef.flags().isStatic()){
            classSynth.setFlags(Flags.STATIC.Final());//class is static
        }
        else{
            classSynth.setFlags(Flags.FINAL);//class is not static
        }
        //class is nested
        classSynth.setKind(ClassDef.MEMBER);
        ClassType outerClassType = (ClassType) methodDef.container().get();
        ClassDef outerClassDef = outerClassType.def();
        classSynth.setOuter(outerClassDef);
        
        //processing the return
        returnFlagName = ((X10Context)xct).makeFreshName("returnFlag");
        FieldSynth returnFlagSynth = classSynth.createField(compilerPos, returnFlagName.toString(), xts.Boolean());
        returnFlagSynth.addAnnotation(genUninitializedAnnotation());
        fieldNames.add(returnFlagName); //add it as one field for query
        
        Type returnType = methodDef.returnType().get();
        if (returnType != xts.Void()){
            returnFieldName = ((X10Context)xct).makeFreshName("result");
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
        Block block = methodDecl.body();
        //And if the method is instance method, need process the method body's all special
        //this/super need set the qualifier
        ClassDef outerDef = classSynth.getOuter();
        if(outerDef != null){
            block = (Block) WSCodeGenUtility.setSpeicalQualifier(block, outerDef, xnf);
        }        
        this.codeBlock = block;
        
        //finally changes fast/slow method's return type
        fastMSynth.setReturnType(returnType);

        genMethodFormalAsFields();
    }
    
    /**
     * Generate apply() method for non-void method's inner class
     * @param returnType
     * @throws SemanticException
     */
    protected void genApplyMethod(Type returnType) throws SemanticException{
        MethodSynth applyMSynth = classSynth.createMethod(compilerPos, "apply");
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
    
    public void genClass() throws SemanticException {
        //do the formals translation
        
        Type returnType = methodDecl.methodDef().returnType().get();
        if (returnType != xts.Void()){
            genApplyMethod(returnType);
        }
        
        super.genClass(); //transform: method skeleton/methods body/constructors/remap
        genWSMethod(fastWrapperMethodSynth); //now generate fast/slow path, register them and put them in container class
    }

    
    protected ConstructorSynth genClassConstructor() throws SemanticException{
        ConstructorSynth conSynth = super.genClassConstructor();
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
        return conSynth;
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
