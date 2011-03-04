package x10.compiler.ws.codegen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Formal;
import polyglot.ast.Local;
import polyglot.ast.MethodDecl;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import x10.ast.X10NodeFactory;
import x10.compiler.ws.WSCodeGenerator;
import x10.compiler.ws.WSTransformState;
import x10.compiler.ws.util.WSCodeGenUtility;
import x10.constraint.XEquals;
import x10.constraint.XFailure;
import x10.constraint.XField;
import x10.constraint.XLit;
import x10.constraint.XNameWrapper;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;
import x10.types.ConstrainedType;
import x10.types.X10ClassDef;
import x10.types.X10Context;
import x10.types.X10FieldDef;
import x10.types.X10FieldInstance;
import x10.types.X10Flags;
import x10.types.X10MethodDef;
import x10.types.X10MethodDef_c;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.constraints.CConstraint;
import x10.util.Synthesizer;
import x10.util.synthesizer.ClassSynth;
import x10.util.synthesizer.CodeBlockSynth;
import x10.util.synthesizer.ConstructorSynth;
import x10.util.synthesizer.FieldSynth;
import x10.util.synthesizer.InstanceCallSynth;
import x10.util.synthesizer.NewInstanceSynth;
import x10.util.synthesizer.NewLocalVarSynth;
import x10.util.synthesizer.SuperCallSynth;
import x10.visit.ConstantPropagator;

/**
 * @author Haichuan
 * 
 * This class will transform the main method to a inner class, and add it into parent class
 *
 */
public class WSMainMethodClassGen extends WSMethodFrameClassGen {
    
    public WSMainMethodClassGen(Job job, X10NodeFactory xnf, X10Context context,
            MethodDef mainMethodDef, MethodDecl mainMethodDecl, WSTransformState wsTransformState) {
        super(job, xnf, context, mainMethodDef, mainMethodDecl, wsTransformState);

//        this.className = WSCodeGenUtility.getMainMethodClassName();
        classSynth.setSuperType(wsTransformState.mainFrameType);        
    }
    
    /**
     * Just return 
     * public static def main(formal:type) {
     *     val rootFinish = new RootFinish().init();
     *     new _main(rootFinish, rootFinish, formal).run();
     * }
     * @return
     * @throws SemanticException
     */
    public MethodDecl getNewMainMethod() throws SemanticException{
        
        NewInstanceSynth rSynth = new NewInstanceSynth(xnf, xct, compilerPos, wts.rootFinishType);
        InstanceCallSynth riSynth = new InstanceCallSynth(xnf, xct, rSynth.genExpr(), "init");
        NewLocalVarSynth nvSynth = new NewLocalVarSynth(xnf, xct, compilerPos, Name.make("rootFinish"), X10Flags.FINAL, riSynth.genExpr(), wts.finishFrameType, Collections.EMPTY_LIST);
        
        //new _main(args)
        NewInstanceSynth niSynth = new NewInstanceSynth(xnf, xct, compilerPos, this.getClassType());
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
        InstanceCallSynth icSynth = new InstanceCallSynth(xnf, xct, newMainExpr, "run");
        //icSynth.setMethodLocationType(mainFrameType); //the run is in main
        CodeBlockSynth cbSynth = new CodeBlockSynth(xnf, xct, methodDecl.body().position());
        cbSynth.addStmt(nvSynth.genStmt());
        cbSynth.addStmt(icSynth.genStmt());

        return (MethodDecl) methodDecl.body(cbSynth.close());
    }
}
