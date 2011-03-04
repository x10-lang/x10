/*
 * Created on Sep 26, 2005
 */
package x10.wala.translator;

import x10.wala.classLoader.AsyncCallSiteReference;
import x10.wala.loader.X10SourceLoaderImpl;
import x10.wala.ssa.AstX10InstructionFactory;
import x10.wala.translator.X10toCAstTranslator.AsyncEntity;
import x10.wala.translator.X10toCAstTranslator.ClosureBodyEntity;
import x10.wala.translator.X10toCAstTranslator.TypeDeclarationCAstEntity;
import x10.wala.tree.X10CAstEntity;
import x10.wala.tree.X10CastNode;
import x10.wala.tree.visit.X10DelegatingCAstVisitor;

import com.ibm.wala.cast.ir.translator.AstTranslator;
import com.ibm.wala.cast.ir.translator.AstTranslator.WalkContext;
import com.ibm.wala.cast.java.loader.JavaSourceLoaderImpl;
import com.ibm.wala.cast.java.translator.JavaCAst2IRTranslator;
import com.ibm.wala.cast.java.types.JavaPrimitiveTypeMap;
import com.ibm.wala.cast.loader.AstMethod.DebuggingInformation;
import com.ibm.wala.cast.tree.CAstEntity;
import com.ibm.wala.cast.tree.CAstNode;
import com.ibm.wala.cast.tree.CAstType;
import com.ibm.wala.cast.tree.visit.CAstVisitor;
import com.ibm.wala.cfg.AbstractCFG;
import com.ibm.wala.classLoader.NewSiteReference;
import com.ibm.wala.ssa.SymbolTable;
import com.ibm.wala.types.Descriptor;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.debug.Assertions;
import com.ibm.wala.util.strings.Atom;

public class X10CAst2IRTranslator extends X10DelegatingCAstVisitor /* implements ArrayOpHandler */ {
	private static class X10JavaCAst2IRTranslator extends JavaCAst2IRTranslator {
	    protected final X10SourceLoaderImpl x10Loader;
		private X10JavaCAst2IRTranslator(CAstEntity sourceFileEntity, JavaSourceLoaderImpl loader) {
			super(sourceFileEntity, loader);
			x10Loader = (X10SourceLoaderImpl) loader;
		}

		/* (non-Javadoc)
		 * @see com.ibm.wala.cast.ir.translator.AstTranslator#doLexicallyScopedRead(com.ibm.wala.cast.tree.CAstNode, com.ibm.wala.cast.ir.translator.AstTranslator.WalkContext, java.lang.String)
		 *
		 * Ugly hack to allow code in this class to see this protected method, since it is now declared in this
		 * package in this subclass.
		 */
		@Override
		protected int doLexicallyScopedRead(CAstNode node, WalkContext context, String name) {
			return super.doLexicallyScopedRead(node, context, name);
		}

        @Override
        protected void defineFunction(CAstEntity n,
                      WalkContext definingContext,
                      AbstractCFG cfg,
                      SymbolTable symtab,
                      boolean hasCatchBlock,
                      TypeReference[][] catchTypes,
                      boolean hasMonitorOp,
                      AstLexicalInformation lexicalInfo,
                      DebuggingInformation debugInfo) {
            if (n.getKind() == X10CAstEntity.ASYNC_BODY) {
                x10Loader.defineAsync(n,
                        asyncTypeReference(x10Loader, n),
                        n.getPosition(), definingContext, cfg, symtab, hasCatchBlock, catchTypes,
                        hasMonitorOp, lexicalInfo, debugInfo);
            } else if (n.getKind() == X10CAstEntity.CLOSURE_BODY) {
                x10Loader.defineClosure(n,
                        closureTypeReference(x10Loader, n),
                        n.getPosition(), definingContext, cfg, symtab, hasCatchBlock, catchTypes,
                        hasMonitorOp, lexicalInfo, debugInfo);
            } else
                super.defineFunction(n, definingContext, cfg, symtab, hasCatchBlock, catchTypes, hasMonitorOp, lexicalInfo, debugInfo);
        }
    }

    public X10CAst2IRTranslator(CAstEntity sourceFileEntity, X10SourceLoaderImpl loader) {
        this(new X10JavaCAst2IRTranslator(sourceFileEntity, loader));
    }

    private final X10JavaCAst2IRTranslator translator;

    private final AstX10InstructionFactory insts;
    
    private X10CAst2IRTranslator(X10JavaCAst2IRTranslator translator) {
        super(translator);
        this.translator = translator;
//        this.translator.setArrayOpHandler(this);
        this.insts = (AstX10InstructionFactory) translator.loader().getInstructionFactory();
    }

    protected void leaveFunctionExpr(CAstNode n, Context c, CAstVisitor visitor) {
        int result;
        CAstEntity fn= (CAstEntity) n.getChild(0).getValue();

        if (fn.getKind() == X10CAstEntity.ASYNC_BODY)
            result= processAsyncExpr(n, c);
        else if (fn.getKind() == X10CAstEntity.CLOSURE_BODY)
            result= processClosureExpr(n, c);
        else {
            Assertions.UNREACHABLE("FUNCTION_EXPR neither async nor closure in leaveFunctionExpr().");
            return;
        }
        translator.setValue(n, result);
    }

    private int processAsyncExpr(CAstNode n, Context c) {
        WalkContext context= (WalkContext) c;
        CAstEntity fn= (CAstEntity) n.getChild(0).getValue();
        int result= context.currentScope().allocateTempValue();
        int ex= context.currentScope().allocateTempValue();
        doMaterializeAsync(context, result, ex, fn);
        return result;
    }

    private void doMaterializeAsync(WalkContext context, int result, int ex, CAstEntity fn) {
        TypeReference asyncRef= asyncTypeReference(fn);

        context.cfg().addInstruction(insts.NewInstruction(result,
                NewSiteReference.make(context.cfg().getCurrentInstruction(), asyncRef)));
    }

    private int processClosureExpr(CAstNode n, Context c) {
        WalkContext context= (WalkContext) c;
        CAstEntity fn= (CAstEntity) n.getChild(0).getValue();
        int result= context.currentScope().allocateTempValue();
        int ex= context.currentScope().allocateTempValue();
        doMaterializeClosure(context, result, ex, fn);
        return result;
    }

    private void doMaterializeClosure(WalkContext context, int result, int ex, CAstEntity fn) {
        TypeReference closureRef= closureTypeReference(fn);

        context.cfg().addInstruction(insts.NewInstruction(result,
                NewSiteReference.make(context.cfg().getCurrentInstruction(), closureRef)));
    }

    private static TypeReference asyncTypeReference(JavaSourceLoaderImpl loader, CAstEntity fn) {
        return TypeReference.findOrCreate(loader.getReference(), "LA" + fn.getName());
    }

    private TypeReference asyncTypeReference(CAstEntity fn) {
        return asyncTypeReference(translator.loader(), fn);
    }

    public MethodReference asyncEntityToMethodReference(CAstEntity asyncEntity) {
        CAstType.Method bodyType= (CAstType.Method) asyncEntity.getType();
        CAstType retType= bodyType.getReturnType();
//      CAstType owningType= bodyType.getDeclaringType();
//      JavaSourceLoaderImpl fLoader = translator.loader();

        Atom asyncName= Atom.findOrCreateUnicodeAtom(asyncEntity.getName());
        Descriptor asyncDesc= Descriptor.findOrCreate(null, TypeName.string2TypeName(retType.getName()));
        // RMF 1/12/07 - Type ref must agree with what's used when the async type is defined!
        // The following commented-out version didn't do that...
//      TypeReference owningTypeRef= TypeReference.findOrCreate(fLoader.getReference(), TypeName.string2TypeName(owningType.getName()));
        TypeReference owningTypeRef= asyncTypeReference(asyncEntity);

        return MethodReference.findOrCreate(owningTypeRef, asyncName, asyncDesc);
    }

    protected boolean visitAsyncBodyEntity(CAstEntity n, Context context, Context codeContext, CAstVisitor visitor) {
        translator.initFunctionEntity(n, (WalkContext)context, (WalkContext)codeContext);
        return false;
    }
    protected void leaveAsyncBodyEntity(CAstEntity n, Context context, Context codeContext, CAstVisitor visitor) {
        translator.closeFunctionEntity(n, (WalkContext)context, (WalkContext)codeContext);
    }

    protected boolean visitAsyncInvoke(CAstNode n, Context c, CAstVisitor visitor) { /* empty */ return false; }
    protected void leaveAsyncInvoke(CAstNode n, Context c, CAstVisitor visitor) {
        WalkContext context = (WalkContext)c;
        CAstEntity bodyEntity = (CAstEntity) n.getChild(n.getChildCount()-1).getChild(0).getValue();
        
        // Figure out whether this is a future or an async
        int exceptValue = context.currentScope().allocateTempValue();
        AsyncCallSiteReference acsr = new AsyncCallSiteReference(asyncEntityToMethodReference(bodyEntity), context.cfg().getCurrentInstruction());
        int rcvrValue = translator.getValue(n.getChild(n.getChildCount()-1));
        int clockValues[] = new int[ n.getChildCount() - 1];
        for(int i = 0; i < clockValues.length; i++) {
            clockValues[i] = translator.getValue(n.getChild(i));
        }

        if (((CAstType.Function) bodyEntity.getType()).getReturnType() == JavaPrimitiveTypeMap.VoidType)
            context.cfg().addInstruction(insts.AsyncInvoke(new int[] { rcvrValue }, exceptValue, acsr, clockValues));
        else {
            int retValue = context.currentScope().allocateTempValue();

            context.cfg().addInstruction(insts.AsyncInvoke(retValue, new int[] { rcvrValue }, exceptValue, acsr, clockValues));
            translator.setValue(n, retValue);
        }
    }

    private static TypeReference closureTypeReference(JavaSourceLoaderImpl loader, CAstEntity fn) {
        return TypeReference.findOrCreate(loader.getReference(), "Lclosure" + fn.getPosition());
    }

    private TypeReference closureTypeReference(CAstEntity fn) {
        return closureTypeReference(translator.loader(), fn);
    }

    protected boolean visitClosureBodyEntity(CAstEntity n, Context context, Context codeContext, CAstVisitor visitor) {
        translator.initFunctionEntity(n, (WalkContext)context, (WalkContext)codeContext);
        return false;
    }
    protected void leaveClosureBodyEntity(CAstEntity n, Context context, Context codeContext, CAstVisitor visitor) {
        translator.closeFunctionEntity(n, (WalkContext)context, (WalkContext)codeContext);
    }

    protected boolean visitAtomicEnter(CAstNode n, Context c, CAstVisitor visitor) { /* empty */ return false; }
    protected void leaveAtomicEnter(CAstNode n, Context c, CAstVisitor visitor) {
        WalkContext context = (WalkContext)c;
        context.cfg().addInstruction(insts.Atomic(true));
    }
    protected boolean visitAtomicExit(CAstNode n, Context c, CAstVisitor visitor) { /* empty */ return false; }
    protected void leaveAtomicExit(CAstNode n, Context c, CAstVisitor visitor) {
        WalkContext context = (WalkContext)c;
        context.cfg().addInstruction(insts.Atomic(false));
    }
    protected boolean visitFinishEnter(CAstNode n, Context c, CAstVisitor visitor) { /* empty */ return false; }
    protected void leaveFinishEnter(CAstNode n, Context c, CAstVisitor visitor) {
        WalkContext context = (WalkContext)c;
        context.cfg().addInstruction(insts.Finish(true));
    }
    protected boolean visitFinishExit(CAstNode n, Context c, CAstVisitor visitor) { /* empty */ return false; }
    protected void leaveFinishExit(CAstNode n, Context c, CAstVisitor visitor) {
        WalkContext context = (WalkContext)c;
        context.cfg().addInstruction(insts.Finish(false));
    }
    
    
    protected boolean visitNext(CAstNode n, Context c, CAstVisitor visitor) {
        WalkContext context = (WalkContext)c;
        context.cfg().addInstruction(insts.Next());
        return true;
    }
    
    protected boolean visitIterInit(CAstNode n, Context c, CAstVisitor visitor) { /* empty */ return false; }
    protected void leaveIterInit(CAstNode n, Context c, CAstVisitor visitor) {
        WalkContext context = (WalkContext)c;
        int targetValue = translator.getValue(n.getChild(0));
        int retValue = context.currentScope().allocateTempValue();
        context.cfg().addInstruction(insts.IterInit(retValue, targetValue));
        translator.setValue(n, retValue);
    }
    protected boolean visitIterHasNext(CAstNode n, Context c, CAstVisitor visitor) { /* empty */ return false; }
    protected void leaveIterHasNext(CAstNode n, Context c, CAstVisitor visitor) {
        WalkContext context = (WalkContext)c;
        int targetValue = translator.getValue(n.getChild(0));
        int retValue = context.currentScope().allocateTempValue();
        context.cfg().addInstruction(insts.IterHasNext(retValue, targetValue));
        translator.setValue(n, retValue);
    }
    protected boolean visitIterNext(CAstNode n, Context c, CAstVisitor visitor) { /* empty */ return false; }
    protected void leaveIterNext(CAstNode n, Context c, CAstVisitor visitor) {
        WalkContext context = (WalkContext)c;
        int targetValue = translator.getValue(n.getChild(0));
        int retValue = context.currentScope().allocateTempValue();
        context.cfg().addInstruction(insts.IterNext(retValue, targetValue));
        translator.setValue(n, retValue);
    }
    protected boolean visitHere(CAstNode n, Context c, CAstVisitor visitor) { /* empty */ return false; }
    protected void leaveHere(CAstNode n, Context c, CAstVisitor visitor) {
        WalkContext context = (WalkContext)c;
        int retValue = context.currentScope().allocateTempValue();
        context.cfg().addInstruction(insts.Here(retValue));
        translator.setValue(n, retValue);
    }

    protected void leaveThis(CAstNode n, Context c, CAstVisitor visitor) {
    	WalkContext context = (WalkContext)c;
    	CAstEntity entity = context.top();
    	if (entity instanceof AsyncEntity || entity instanceof ClosureBodyEntity) {
    		translator.setValue(n, translator.doLexicallyScopedRead(n, context, "this"));
    	} else {
    		super.leaveThis(n, c, visitor);
    	}
    }
    
    protected boolean visitTupleExpr(CAstNode n, Context c, CAstVisitor visitor) { /* empty */ return false; }
    protected void leaveTupleExpr(CAstNode n, Context c, CAstVisitor visitor) {
        WalkContext context = (WalkContext)c;
        int retValue = context.currentScope().allocateTempValue();
        int slotValues[] = new int[n.getChildCount() - 1];
        for(int i = 0; i < slotValues.length; i++) {
            slotValues[i] = translator.getValue(n.getChild(i+1));
        }
        context.cfg().addInstruction(insts.Tuple(retValue, slotValues));
        translator.setValue(n, retValue);
    }
    
    protected boolean visitAtStmtEnter(final CAstNode node, final Context context, final CAstVisitor visitor) {
      ((WalkContext) context).cfg().addInstruction(insts.AtStmt(true));
      return true;
    }
    
    protected boolean visitAtStmtExit(final CAstNode node, final Context context, final CAstVisitor visitor) {
      ((WalkContext) context).cfg().addInstruction(insts.AtStmt(false));
      return true;
    }
    
    protected boolean visitTypeEntity(final CAstEntity node, final Context context, final Context typeContext, 
                                      final CAstVisitor visitor) {
      // We avoid type declarations in _.x10 file.
      if (node instanceof TypeDeclarationCAstEntity) {
        return true;
      } else {
        return super.visitTypeEntity(node, context, typeContext, visitor);
      }
    }

    private void translate(final CAstEntity N, final String nm) {
        if (AstTranslator.DEBUG_TOP)
            System.err.println("translating " + nm);
//      PrintWriter printWriter= new PrintWriter(System.out);
//      X10CAstPrinter.printTo(N, printWriter);
//      printWriter.flush();
        visitEntities(N, translator.new DefaultContext(translator, N, nm), this);
    }

    /* UGH! */
    public void translate() {
        CAstEntity fSourceEntity = translator.sourceFileEntity();
        translate(fSourceEntity, fSourceEntity.getName());
    }
    
    public JavaCAst2IRTranslator getCAst2IRTranslator() {
        return translator;
    }

    /**
     * Returns true if the given array reference operation indexes using an x10.lang.point,
     * rather than an array of ints (as in ordinary Java)
     */
    private boolean isRefByPoint(CAstNode arrayRefNode) {
        return arrayRefNode.getChildCount() > 3 || // if there are multiple indices, it's not by point
            arrayRefNode.getKind() == X10CastNode.ARRAY_REF_BY_POINT;
    }
/*
    public void doArrayRead(WalkContext context, int result, int arrayValue, CAstNode arrayRefNode, int[] dimValues) {
        TypeReference arrayTypeRef= (TypeReference) arrayRefNode.getChild(1).getValue();

        if (isRefByPoint(arrayRefNode))
            context.cfg().addInstruction(
                    insts.ArrayLoadByPoint(result, arrayValue, dimValues[0], arrayTypeRef));
        else
            context.cfg().addInstruction(
                    insts.ArrayLoadByIndex(result, arrayValue, dimValues, arrayTypeRef));
    }

    public void doArrayWrite(WalkContext context, int arrayValue, CAstNode arrayRefNode, int[] dimValues, int rval) {
        TypeReference arrayTypeRef =
            arrayRefNode.getKind() == CAstNode.ARRAY_LITERAL ?
                    ((TypeReference) arrayRefNode.getChild(0).getChild(0).getValue()).getArrayElementType() :
                     (TypeReference) arrayRefNode.getChild(1).getValue();

        if (isRefByPoint(arrayRefNode))
            context.cfg().addInstruction(
                    insts.ArrayStoreByPoint(arrayValue, dimValues[0], rval, arrayTypeRef));
        else
            context.cfg().addInstruction(
                    insts.ArrayStoreByIndex(arrayValue, dimValues, rval, arrayTypeRef));
    }
*/
    @Override
    protected boolean doVisitAssignNodes(CAstNode n, Context context, CAstNode v, CAstNode a, CAstVisitor visitor) {
        int NT = a.getKind();
        boolean assign = NT == CAstNode.ASSIGN;
        boolean preOp = NT == CAstNode.ASSIGN_PRE_OP;
        if (n.getKind() == X10CastNode.ARRAY_REF_BY_POINT) {
            doVisitArrayRefNode(n, v, a, assign, preOp, context, visitor);
            return true;
        } else {
            return false;
        }
    }
}
