/*
 * Created on Sep 26, 2005
 */
package com.ibm.wala.cast.x10.translator.polyglot;

import com.ibm.wala.cast.ir.translator.ArrayOpHandler;
import com.ibm.wala.cast.ir.translator.AstTranslator;
import com.ibm.wala.cast.ir.translator.AstTranslator.WalkContext;
import com.ibm.wala.cast.java.translator.JavaCAst2IRTranslator;
import com.ibm.wala.cast.java.types.JavaPrimitiveTypeMap;
import com.ibm.wala.cast.tree.CAstEntity;
import com.ibm.wala.cast.tree.CAstNode;
import com.ibm.wala.cast.tree.CAstType;
import com.ibm.wala.cast.tree.visit.CAstVisitor;
import com.ibm.wala.cast.x10.ssa.AsyncCallSiteReference;
import com.ibm.wala.cast.x10.ssa.X10InstructionFactory;
import com.ibm.wala.cast.x10.translator.X10CAstEntity;
import com.ibm.wala.cast.x10.translator.X10CastNode;
import com.ibm.wala.cast.x10.visit.X10CAstVisitor;
import com.ibm.wala.classLoader.NewSiteReference;
import com.ibm.wala.types.Descriptor;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.debug.Assertions;
import com.ibm.wala.util.strings.Atom;

public class X10CAst2IRTranslator extends X10CAstVisitor implements ArrayOpHandler {
    public X10CAst2IRTranslator(CAstEntity sourceFileEntity, X10SourceLoaderImpl loader) {
	this(new JavaCAst2IRTranslator(sourceFileEntity, loader));
    }

    private final JavaCAst2IRTranslator translator;

    private final X10InstructionFactory insts;
    
    private X10CAst2IRTranslator(JavaCAst2IRTranslator translator) {
	super(translator);
	this.translator = translator;
	this.translator.setArrayOpHandler(this);
	this.insts = (X10InstructionFactory) translator.loader().getInstructionFactory();
    }

    protected boolean visitFunctionExpr(CAstNode n, Context c, CAstVisitor visitor) {
	CAstEntity fn= (CAstEntity) n.getChild(0).getValue();

	if (fn.getKind() == X10CAstEntity.ASYNC_BODY)
	    declareAsync(fn, (WalkContext) c);
	else if (fn.getKind() == X10CAstEntity.CLOSURE_BODY)
	    declareClosure(fn, (WalkContext) c);
	return false;
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

    private void declareAsync(CAstEntity fn, WalkContext context) {
	TypeReference asyncRef= asyncTypeReference(fn);

	((X10SourceLoaderImpl) translator.loader()).defineAsync(fn, asyncRef, fn.getPosition());
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

    private void declareClosure(CAstEntity fn, WalkContext context) {
	TypeReference asyncRef= closureTypeReference(fn);

	((X10SourceLoaderImpl) translator.loader()).defineClosure(fn, asyncRef, fn.getPosition());
    }

    private TypeReference asyncTypeReference(CAstEntity fn) {
	return TypeReference.findOrCreate(translator.loader().getReference(), "LA" + fn.getName());
    }

    public MethodReference asyncEntityToMethodReference(CAstEntity asyncEntity) {
	CAstType.Method bodyType= (CAstType.Method) asyncEntity.getType();
	CAstType retType= bodyType.getReturnType();
//	CAstType owningType= bodyType.getDeclaringType();
//	JavaSourceLoaderImpl fLoader = translator.loader();

	Atom asyncName= Atom.findOrCreateUnicodeAtom(asyncEntity.getName());
	Descriptor asyncDesc= Descriptor.findOrCreate(null, TypeName.string2TypeName(retType.getName()));
	// RMF 1/12/07 - Type ref must agree with what's used when the async type is defined!
	// The following commented-out version didn't do that...
//	TypeReference owningTypeRef= TypeReference.findOrCreate(fLoader.getReference(), TypeName.string2TypeName(owningType.getName()));
	TypeReference owningTypeRef= asyncTypeReference(asyncEntity);

	return MethodReference.findOrCreate(owningTypeRef, asyncName, asyncDesc);
    }

    protected boolean visitAsyncBodyEntity(CAstEntity n, Context context, Context codeContext, CAstVisitor visitor) {
	translator.initFunctionEntity(n, (WalkContext)context, (WalkContext)codeContext);
	((X10SourceLoaderImpl) translator.loader()).defineAsync(n, asyncTypeReference(n), n.getPosition());
	return false;
    }
    protected void leaveAsyncBodyEntity(CAstEntity n, Context context, Context codeContext, CAstVisitor visitor) {
	translator.closeFunctionEntity(n, (WalkContext)context, (WalkContext)codeContext);
    }

    protected boolean visitAsyncInvoke(CAstNode n, Context c, CAstVisitor visitor) { /* empty */ return false; }
    protected void leaveAsyncInvoke(CAstNode n, Context c, CAstVisitor visitor) {
	WalkContext context = (WalkContext)c;
	CAstEntity bodyEntity = (CAstEntity) n.getChild(n.getChildCount()-1).getChild(0).getValue();
	CAstNode placeExpr = n.getChild(0);
	// Figure out whether this is a future or an async
	int exceptValue = context.currentScope().allocateTempValue();
	AsyncCallSiteReference acsr = new AsyncCallSiteReference(asyncEntityToMethodReference(bodyEntity), context.cfg().getCurrentInstruction());
    int rcvrValue = translator.getValue(n.getChild(n.getChildCount()-1));
    int placeValue = translator.getValue(placeExpr);
    int clockValues[] = new int[ n.getChildCount() - 2];
    for(int i = 0; i < clockValues.length; i++) {
    	clockValues[i] = translator.getValue(n.getChild(i+1));
    }
    
	if (((CAstType.Function) bodyEntity.getType()).getReturnType() == JavaPrimitiveTypeMap.VoidType)
	    context.cfg().addInstruction(insts.AsyncInvoke(new int[] { rcvrValue }, exceptValue, acsr, placeValue, clockValues));
	else {
	    int retValue = context.currentScope().allocateTempValue();

	    context.cfg().addInstruction(insts.AsyncInvoke(retValue, new int[] { rcvrValue }, exceptValue, acsr, placeValue, clockValues));
	    translator.setValue(n, retValue);
	}
    }

    private TypeReference closureTypeReference(CAstEntity fn) {
	return TypeReference.findOrCreate(translator.loader().getReference(), "Lclosure" + fn.getPosition());
    }

    protected boolean visitClosureBodyEntity(CAstEntity n, Context context, Context codeContext, CAstVisitor visitor) {
	translator.initFunctionEntity(n, (WalkContext)context, (WalkContext)codeContext);
	((X10SourceLoaderImpl) translator.loader()).defineClosure(n, closureTypeReference(n), n.getPosition());
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
    protected boolean visitForce(CAstNode n, Context c, CAstVisitor visitor) { /* empty */ return false; }
    protected void leaveForce(CAstNode n, Context c, CAstVisitor visitor) {
	WalkContext context = (WalkContext)c;
	int targetValue = translator.getValue(n.getChild(0));
	int retValue = context.currentScope().allocateTempValue();
	context.cfg().addInstruction(insts.Force(retValue, targetValue, (TypeReference) n.getChild(1).getValue()));
	translator.setValue(n, retValue);
    }
    protected boolean visitRegionIterInit(CAstNode n, Context c, CAstVisitor visitor) { /* empty */ return false; }
    protected void leaveRegionIterInit(CAstNode n, Context c, CAstVisitor visitor) {
	WalkContext context = (WalkContext)c;
	int targetValue = translator.getValue(n.getChild(0));
	int retValue = context.currentScope().allocateTempValue();
	context.cfg().addInstruction(insts.RegionIterInit(retValue, targetValue));
	translator.setValue(n, retValue);
    }
    protected boolean visitRegionIterHasNext(CAstNode n, Context c, CAstVisitor visitor) { /* empty */ return false; }
    protected void leaveRegionIterHasNext(CAstNode n, Context c, CAstVisitor visitor) {
	WalkContext context = (WalkContext)c;
	int targetValue = translator.getValue(n.getChild(0));
	int retValue = context.currentScope().allocateTempValue();
	context.cfg().addInstruction(insts.RegionIterHasNext(retValue, targetValue));
	translator.setValue(n, retValue);
    }
    protected boolean visitRegionIterNext(CAstNode n, Context c, CAstVisitor visitor) { /* empty */ return false; }
    protected void leaveRegionIterNext(CAstNode n, Context c, CAstVisitor visitor) {
	WalkContext context = (WalkContext)c;
	int targetValue = translator.getValue(n.getChild(0));
	int retValue = context.currentScope().allocateTempValue();
	context.cfg().addInstruction(insts.RegionIterNext(retValue, targetValue));
	translator.setValue(n, retValue);
    }
    protected boolean visitHere(CAstNode n, Context c, CAstVisitor visitor) { /* empty */ return false; }
    protected void leaveHere(CAstNode n, Context c, CAstVisitor visitor) {
	WalkContext context = (WalkContext)c;
	int retValue = context.currentScope().allocateTempValue();
	context.cfg().addInstruction(insts.Here(retValue));
	translator.setValue(n, retValue);
    }

    protected boolean visitPlaceOfPoint(CAstNode n, Context c, CAstVisitor visitor) { /* empty */ return false; }
    protected void leavePlaceOfPoint(CAstNode n, Context c, CAstVisitor visitor) {
	WalkContext context = (WalkContext)c;
	int retValue = context.currentScope().allocateTempValue();
	int targetValue = translator.getValue(n.getChild(0));
	context.cfg().addInstruction(insts.PlaceOfPoint(retValue, targetValue));
	translator.setValue(n, retValue);
    }

    private void translate(final CAstEntity N, final String nm) {
	if (AstTranslator.DEBUG_TOP)
	    java.util.logging.LogManager.getLogManager().getLogger("com.ibm.wala.trace").info("translating " + nm);
//	PrintWriter printWriter= new PrintWriter(System.out);
//	X10CAstPrinter.printTo(N, printWriter);
//	printWriter.flush();
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
