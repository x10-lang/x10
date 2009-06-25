/*
 *
 * (C) Copyright IBM Corporation 2006, 2007, 2008, 2009
 *
 *  This file is part of X10 Language.
 *
 */

/*
 * Created on Feb 24 2009
 *
 */

package polyglot.ext.x10cuda.visit;

import java.util.ArrayList;

import polyglot.ast.ArrayInit_c;
import polyglot.ast.Assert_c;
import polyglot.ast.Assign_c;
import polyglot.ast.Binary_c;
import polyglot.ast.BooleanLit_c;
import polyglot.ast.Branch_c;
import polyglot.ast.Case_c;
import polyglot.ast.Catch_c;
import polyglot.ast.CharLit_c;
import polyglot.ast.ClassBody_c;
import polyglot.ast.Conditional_c;
import polyglot.ast.ConstructorDecl_c;
import polyglot.ast.Do_c;
import polyglot.ast.Empty_c;
import polyglot.ast.Eval_c;
import polyglot.ast.FieldDecl_c;
import polyglot.ast.Field_c;
import polyglot.ast.FloatLit_c;
import polyglot.ast.For_c;
import polyglot.ast.Formal_c;
import polyglot.ast.Id_c;
import polyglot.ast.If_c;
import polyglot.ast.Import_c;
import polyglot.ast.Initializer_c;
import polyglot.ast.IntLit_c;
import polyglot.ast.Labeled_c;
import polyglot.ast.LocalClassDecl_c;
import polyglot.ast.LocalDecl_c;
import polyglot.ast.Local_c;
import polyglot.ast.MethodDecl_c;
import polyglot.ast.New_c;
import polyglot.ast.Node;
import polyglot.ast.Block_c;
import polyglot.ast.NullLit_c;
import polyglot.ast.PackageNode_c;
import polyglot.ast.Return_c;
import polyglot.ast.Stmt;
import polyglot.ast.StringLit_c;
import polyglot.ast.SwitchBlock_c;
import polyglot.ast.Switch_c;
import polyglot.ast.Throw_c;
import polyglot.ast.Try_c;
import polyglot.ast.Unary_c;
import polyglot.ast.While_c;
import polyglot.ext.x10.ast.AssignPropertyBody_c;
import polyglot.ext.x10.ast.Await_c;
import polyglot.ext.x10.ast.ClosureCall_c;
import polyglot.ext.x10.ast.Closure_c;
import polyglot.ext.x10.ast.ConstantDistMaker_c;
import polyglot.ext.x10.ast.ForLoop_c;
import polyglot.ext.x10.ast.ParExpr_c;
import polyglot.ext.x10.ast.PropertyDecl_c;
import polyglot.ext.x10.ast.RegionMaker_c;
import polyglot.ext.x10.ast.SubtypeTest_c;
import polyglot.ext.x10.ast.Tuple_c;
import polyglot.ext.x10.ast.TypeDecl_c;
import polyglot.ext.x10.ast.X10Binary_c;
import polyglot.ext.x10.ast.X10Call_c;
import polyglot.ext.x10.ast.X10CanonicalTypeNode_c;
import polyglot.ext.x10.ast.X10Cast_c;
import polyglot.ext.x10.ast.X10ClassDecl_c;
import polyglot.ext.x10.ast.X10Instanceof_c;
import polyglot.ext.x10.ast.X10Special_c;
import polyglot.ext.x10.ast.X10Unary_c;
import polyglot.ext.x10.extension.X10Ext;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10cpp.visit.MessagePassingCodeGenerator;
import polyglot.ext.x10cuda.types.X10CUDAContext_c;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.visit.Translator;
import x10c.util.ClassifiedStream;
import x10c.util.StreamWrapper;

/**
 * Visitor that prettyprints an X10 AST to the CUDA subset of c++.
 *
 * @author Dave Cunningham
 */

public class CUDACodeGenerator extends MessagePassingCodeGenerator {
	
	private static final String CUDA_ANNOTATION = "x10.compiler.Cuda";

    public CUDACodeGenerator(StreamWrapper sw, Translator tr) {
        super(sw,tr);
    }

    
    private X10CUDAContext_c context() {
        return (X10CUDAContext_c) tr.context();
    }
    
    // defer to CUDAContext.cudaStream()
    private ClassifiedStream cudaStream() {
        return context().cudaStream(sw);
    }
    
    private boolean generatingCuda() { return context().generatingCuda(); }

    private void generatingCuda(boolean v) { context().generatingCuda(v); }

    // does the block have the annotation that denotes that it should be split-compiled to cuda?
    private boolean nodeHasCudaAnnotation(Node n) {
        X10TypeSystem xts = (X10TypeSystem) tr.typeSystem();
        X10Ext ext = (X10Ext) n.ext();
        try {
            Type cudable = (Type) xts.systemResolver().find(QName.make(CUDA_ANNOTATION));
            return !ext.annotationMatching(cudable).isEmpty();
        } catch (SemanticException e) {
            assert false : e;
        	return false; // in case asserts are off
        }
    }
    
    void handleCuda(Block_c b) {
        X10ClassType hostClassType = (X10ClassType) context().wrappingClosure().closureDef().typeContainer().get();
        String hostClassName = emitter.translate_mangled_FQN(hostClassType.fullName().toString(), "_");
        String kernel_name = getClosureName(hostClassName,context().closureId());
        sw.write("/* block split-compiled to cuda as "+kernel_name+" */ ");

        ClassifiedStream out = cudaStream();
        // disable name-mangling which seems to be inconsistent across cuda versions
        out.write("extern \"C\" {"); out.newline(4); out.begin(0);
        
        // kernel
        out.write("__global__ void "+kernel_name+"(char *buf)"); out.newline();
        
        // decode buffer
        //out.write("    var = *(*T)buf; buf += sizeof(T);")
        
        // body
        sw.pushCurrentStream(out);
        super.visit(b);
        sw.popCurrentStream();
        out.write(" // "+kernel_name);
        
        // end
        out.end(); out.newline();
        out.write("} // extern \"C\""); out.newline();
        out.forceNewline();
    }

    public void visit(Block_c b) {
        if (nodeHasCudaAnnotation(b)) {
            assert !generatingCuda() : "Nesting of cuda annotation makes no sense.";
            // TODO: assert the block is the body of an async
            generatingCuda(true);
            handleCuda(b);
            generatingCuda(false);
        }
        super.visit(b);
    }

    public void visit(Closure_c n) {
        Closure_c last = context().wrappingClosure();
        context().wrappingClosure(n);
        super.visit(n);
        context().wrappingClosure(last);
    }
    
    public void visit(New_c n) {
        assert !generatingCuda() : "New not allowed in @Cudable code.";
        super.visit(n);
    }


    @Override
    public void visit(ArrayInit_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(Assert_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(Assign_c asgn) {
        // TODO Auto-generated method stub
        super.visit(asgn);
    }


    @Override
    public void visit(AssignPropertyBody_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(Await_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(Binary_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(BooleanLit_c lit) {
        // TODO Auto-generated method stub
        super.visit(lit);
    }


    @Override
    public void visit(Branch_c br) {
        // TODO Auto-generated method stub
        super.visit(br);
    }


    @Override
    public void visit(Case_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(Catch_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(CharLit_c lit) {
        // TODO Auto-generated method stub
        super.visit(lit);
    }


    @Override
    public void visit(ClassBody_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(ClosureCall_c c) {
        // TODO Auto-generated method stub
        super.visit(c);
    }


    @Override
    public void visit(Conditional_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(ConstantDistMaker_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(ConstructorDecl_c dec) {
        // TODO Auto-generated method stub
        super.visit(dec);
    }


    @Override
    public void visit(Do_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(Empty_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(Eval_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(Field_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(FieldDecl_c dec) {
        // TODO Auto-generated method stub
        super.visit(dec);
    }


    @Override
    public void visit(FloatLit_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(For_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }



    @Override
    public void visit(ForLoop_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(Formal_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(Id_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(If_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(Import_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(Initializer_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(IntLit_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(Labeled_c label) {
        // TODO Auto-generated method stub
        super.visit(label);
    }


    @Override
    public void visit(Local_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(LocalClassDecl_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(LocalDecl_c dec) {
        // TODO Auto-generated method stub
        super.visit(dec);
    }


    @Override
    public void visit(MethodDecl_c dec) {
        // TODO Auto-generated method stub
        super.visit(dec);
    }


    @Override
    public void visit(Node n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(NullLit_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(PackageNode_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(ParExpr_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(PropertyDecl_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(RegionMaker_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(Return_c ret) {
        // TODO Auto-generated method stub
        super.visit(ret);
    }


    @Override
    public void visit(StringLit_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(SubtypeTest_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(Switch_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(SwitchBlock_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(Throw_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(Try_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(Tuple_c c) {
        // TODO Auto-generated method stub
        super.visit(c);
    }


    @Override
    public void visit(TypeDecl_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(Unary_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(While_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(X10Binary_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(X10Call_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(X10CanonicalTypeNode_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(X10Cast_c c) {
        // TODO Auto-generated method stub
        super.visit(c);
    }


    @Override
    public void visit(X10ClassDecl_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(X10Instanceof_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(X10Special_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }


    @Override
    public void visit(X10Unary_c n) {
        // TODO Auto-generated method stub
        super.visit(n);
    }
    

} // end of CUDACodeGenerator

// vim:tabstop=4:shiftwidth=4:expandtab
