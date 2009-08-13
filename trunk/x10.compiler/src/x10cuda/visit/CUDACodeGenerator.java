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

package x10cuda.visit;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import polyglot.ast.ArrayInit_c;
import polyglot.ast.Assert_c;
import polyglot.ast.Assign_c;
import polyglot.ast.Binary_c;
import polyglot.ast.Block;
import polyglot.ast.Block_c;
import polyglot.ast.BooleanLit_c;
import polyglot.ast.Branch_c;
import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Case_c;
import polyglot.ast.Catch_c;
import polyglot.ast.CharLit_c;
import polyglot.ast.ClassBody_c;
import polyglot.ast.Conditional_c;
import polyglot.ast.ConstructorDecl_c;
import polyglot.ast.Do_c;
import polyglot.ast.Empty_c;
import polyglot.ast.Eval;
import polyglot.ast.Eval_c;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl_c;
import polyglot.ast.Field_c;
import polyglot.ast.FloatLit_c;
import polyglot.ast.For_c;
import polyglot.ast.Formal;
import polyglot.ast.Formal_c;
import polyglot.ast.Id_c;
import polyglot.ast.If_c;
import polyglot.ast.Import_c;
import polyglot.ast.Initializer_c;
import polyglot.ast.IntLit;
import polyglot.ast.IntLit_c;
import polyglot.ast.Labeled_c;
import polyglot.ast.LocalClassDecl_c;
import polyglot.ast.LocalDecl;
import polyglot.ast.LocalDecl_c;
import polyglot.ast.Local_c;
import polyglot.ast.MethodDecl_c;
import polyglot.ast.New_c;
import polyglot.ast.Node;
import polyglot.ast.NullLit_c;
import polyglot.ast.PackageNode_c;
import polyglot.ast.Receiver;
import polyglot.ast.Return_c;
import polyglot.ast.Stmt;
import polyglot.ast.StringLit_c;
import polyglot.ast.SwitchBlock_c;
import polyglot.ast.Switch_c;
import polyglot.ast.Throw_c;
import polyglot.ast.Try_c;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary_c;
import polyglot.ast.While_c;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.visit.Translator;
import x10.ast.AssignPropertyBody_c;
import x10.ast.Await_c;
import x10.ast.Closure;
import x10.ast.ClosureCall_c;
import x10.ast.Closure_c;
import x10.ast.ConstantDistMaker_c;
import x10.ast.ForLoop;
import x10.ast.ForLoop_c;
import x10.ast.ParExpr_c;
import x10.ast.PropertyDecl_c;
import x10.ast.RegionMaker;
import x10.ast.RegionMaker_c;
import x10.ast.SubtypeTest_c;
import x10.ast.Tuple_c;
import x10.ast.TypeDecl_c;
import x10.ast.X10Binary_c;
import x10.ast.X10Call;
import x10.ast.X10Call_c;
import x10.ast.X10CanonicalTypeNode_c;
import x10.ast.X10Cast_c;
import x10.ast.X10ClassDecl_c;
import x10.ast.X10Formal;
import x10.ast.X10Instanceof_c;
import x10.ast.X10LocalDecl_c;
import x10.ast.X10Special_c;
import x10.ast.X10Unary_c;
import x10.extension.X10Ext;
import x10.types.X10ClassType;
import x10.types.X10TypeSystem;
import x10c.util.ClassifiedStream;
import x10c.util.StreamWrapper;
import x10cpp.debug.LineNumberMap;
import x10cpp.visit.MessagePassingCodeGenerator;
import x10cpp.visit.X10CPPTranslator;
import x10cuda.types.SharedMem;
import x10cuda.types.X10CUDAContext_c;

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
    
    private void context(Context v) {
        ((X10CPPTranslator)tr).setContext(v);
    }
    
    // defer to CUDAContext.cudaStream()
    private ClassifiedStream cudaStream() {
        return context().cudaStream(sw);
    }
    
    private boolean generatingCuda() { return context().generatingCuda(); }

    private void generatingKernel(boolean v) { context().generatingCuda(v); }

    private Type getType(String name) throws SemanticException {
        X10TypeSystem xts = (X10TypeSystem) tr.typeSystem();
        return (Type) xts.systemResolver().find(QName.make(name));
    }
    
    // does the block have the annotation that denotes that it should be split-compiled to cuda?
    private boolean nodeHasCudaAnnotation(Node n) {
        X10Ext ext = (X10Ext) n.ext();
        try {
            Type cudable = getType(CUDA_ANNOTATION);
            return !ext.annotationMatching(cudable).isEmpty();
        } catch (SemanticException e) {
            assert false : e;
        	return false; // in case asserts are off
        }
    }
    
    void handleKernel(Block_c b) {
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
        //X10CUDAContext_c save_ctxt = context();

        //TypeSystem ts = tr.typeSystem();        
        //context(ts.emptyContext());
        //context((Context)save_ctxt.copy());
        sw.pushCurrentStream(out);
        super.visit(b);
        sw.popCurrentStream();
        out.write(" // "+kernel_name);
        //context(save_ctxt);
        
        
        // end
        out.end(); out.newline();
        out.write("} // extern \"C\""); out.newline();
        out.forceNewline();
    }
    
    // Java cannot return multiple values from a function
    class MultipleValues {
        public long iterations;
        public Formal var;
        public Block body;
    }
    
    protected MultipleValues processLoop (ForLoop loop) {
        MultipleValues r = new MultipleValues();
        Formal loop_formal = loop.formal();
        assert loop_formal instanceof X10Formal; // FIXME: proper error
        X10Formal loop_x10_formal = (X10Formal) loop_formal;
        assert loop_x10_formal.hasExplodedVars(); // FIXME: proper error
        assert loop_x10_formal.vars().size() == 1; // FIXME: proper error
        r.var = loop_x10_formal.vars().get(0);
        Expr domain = loop.domain();
        assert domain instanceof RegionMaker; // FIXME: proper error
        RegionMaker region = (RegionMaker) domain;
        assert region.name().toString().equals("makeRectangular") : region.name(); // FIXME: proper error
        Receiver target = region.target();
        assert target instanceof CanonicalTypeNode; // FIXME: proper error
        CanonicalTypeNode target_type_node = (CanonicalTypeNode) target;
        assert target_type_node.nameString().equals("Region") : target_type_node.nameString(); // FIXME: proper error
        assert region.arguments().size() == 2; // FIXME: proper error
        Expr from_ = region.arguments().get(0);
        Expr to_ = region.arguments().get(1);
        assert from_ instanceof IntLit; // FIXME: proper error
        assert to_ instanceof IntLit; // FIXME: proper error
        IntLit from = (IntLit) from_;
        IntLit to = (IntLit) to_;
        assert from.value() == 0; // FIXME: proper error
        r.iterations = to.value()+1;
        r.body = (Block) loop.body();
        return r;
    }


    public void visit(Block_c b) {
        super.visit(b);
        if (nodeHasCudaAnnotation(b)) {
            assert !generatingCuda() : "Nesting of cuda annotation makes no sense.";
            // TODO: assert the block is the body of an async
            
            assert b.statements().size() == 1; // FIXME: proper error
            Stmt loop_ = b.statements().get(0);
            // test that it is of the form for (blah in region)
            assert loop_ instanceof ForLoop; // FIXME: proper error
            ForLoop loop = (ForLoop) loop_;

            
            MultipleValues outer = processLoop(loop);
            //System.out.println("outer loop: "+outer.var+" "+outer.iterations);
            b = (Block_c) outer.body;

            Stmt last = b.statements().get(b.statements().size()-1);
            assert last instanceof ForLoop; // FIXME: proper error
            loop = (ForLoop) last;

            SharedMem shm = new SharedMem();
            context().shm(shm);
            // look at all but the last statement to find shm decls
            for (Stmt st : b.statements())  {
                if (st == last) continue;
                assert st instanceof LocalDecl; // FIXME: proper error
                LocalDecl ld = (LocalDecl) st;
                Expr init_expr = ld.init();
                // TODO: primitive vals and shared vars
                assert init_expr instanceof X10Call_c; // FIXME: proper error
                X10Call_c init_call = (X10Call_c) init_expr;
                // TODO: makeVal too
                Receiver init_call_target = init_call.target();
                assert init_call_target instanceof CanonicalTypeNode; // FIXME: proper error
                CanonicalTypeNode init_call_target_node = (CanonicalTypeNode) init_call_target;
                assert init_call_target_node.nameString().equals("Rail");
                assert init_call.name().toString().equals("makeVar") : init_call.name(); // FIXME: proper error
                assert init_call.typeArguments().size()==1;
                TypeNode rail_type_arg_node = init_call.typeArguments().get(0);
                Type rail_type_arg = rail_type_arg_node.type();
                // TODO: support other types
                assert rail_type_arg.isFloat();
                assert init_call.arguments().size()==2;
                Expr num_elements = init_call.arguments().get(0);
                Expr rail_init_closure = init_call.arguments().get(1);
                
                /*
                Type typ = ld.declType();
                assert typ.isClass();
                assert typ instanceof X10ClassType;
                X10ClassType ctyp = (X10ClassType)typ;
                assert ctyp.name().toString().equals("Rail");
                assert ctyp.typeArguments().size() == 1;
                Type rail_arg = ctyp.typeArguments().get(0);
                assert rail_arg.isFloat();
                //assert type.nameString().equals("Rail") : ld.type().nameString();
                */
                shm.addRail(ld, num_elements, rail_init_closure);
            }
            
            MultipleValues inner = processLoop(loop);
            //System.out.println("outer loop: "+outer.var+" "+outer.iterations);
            b = (Block_c) inner.body;
            
            assert b.statements().size() == 1; // FIXME: proper error
            Stmt async = b.statements().get(0);
            assert async instanceof Eval; // FIXME: proper error
            Eval async_eval = (Eval) async;
            Expr async_expr = async_eval.expr();
            assert async_expr instanceof Call; // FIXME: proper error
            Call async_call = (Call)async_expr;
            assert async_call.name().toString().equals("runAsync") : async_call.name(); // FIXME: proper error
            Receiver async_target = async_call.target();
            assert async_target instanceof CanonicalTypeNode; // FIXME: proper error
            CanonicalTypeNode async_target_type_node = (CanonicalTypeNode) async_target;
            assert async_target_type_node.nameString().equals("Runtime"); // FIXME: proper error
            assert async_call.arguments().size() == 1 : async_call.arguments(); // FIXME: proper error
            Expr async_arg = async_call.arguments().get(0);
            assert async_arg instanceof Closure; // FIXME: proper error
            Closure async_closure = (Closure) async_arg;
            assert async_closure.formals().size() == 0;
            Block async_body = async_closure.body();
            
            b = (Block_c) async_body;
            
            context().setCudaKernelCFG(outer.iterations, outer.var, inner.iterations, inner.var);            
            generatingKernel(true);
            handleKernel(b);
            generatingKernel(false);
        }
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
    private static boolean sameName (Name n1, Name n2) {
        return n1.toString().equals(n2.toString());
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
        if (generatingCuda()) {
            ClassifiedStream out = cudaStream();
            Name ln = n.name().id();
            if (ln == context().blocksVar().name().id()) {
                out.write("blockIdx.x");
            } else if (ln == context().threadsVar().name().id()) {
                out.write("threadIdx.x");
            } else if (context().shm().has(ln)) {
                out.write("some_shm_var");
            } else {
                super.visit(n);
            }
        } else {
            System.out.println(n);
            super.visit(n);
        }
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
        System.out.println(n);
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
