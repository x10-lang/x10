/*
 * Created on Sep 26, 2005
 */
package com.ibm.domo.ast.x10.translator.polyglot;

import com.ibm.capa.ast.CAstEntity;
import com.ibm.capa.ast.CAstNode;
import com.ibm.capa.ast.CAstType;
import com.ibm.domo.ast.java.loader.JavaSourceLoaderImpl;
import com.ibm.domo.ast.java.translator.JavaCAst2IRTranslator;
import com.ibm.domo.ast.java.types.JavaPrimitiveTypeMap;
import com.ibm.domo.ast.x10.ssa.AsyncCallSiteReference;
import com.ibm.domo.ast.x10.ssa.SSAAtomicInstruction;
import com.ibm.domo.ast.x10.ssa.SSAFinishInstruction;
import com.ibm.domo.ast.x10.ssa.SSAForceInstruction;
import com.ibm.domo.ast.x10.ssa.SSAHereInstruction;
import com.ibm.domo.ast.x10.ssa.SSARegionIterHasNextInstruction;
import com.ibm.domo.ast.x10.ssa.SSARegionIterInitInstruction;
import com.ibm.domo.ast.x10.ssa.SSARegionIterNextInstruction;
import com.ibm.domo.ast.x10.translator.X10CAstEntity;
import com.ibm.domo.ast.x10.translator.X10CastNode;
import com.ibm.domo.ssa.SSAInstructionFactory;
import com.ibm.domo.types.Descriptor;
import com.ibm.domo.types.MethodReference;
import com.ibm.domo.types.TypeName;
import com.ibm.domo.types.TypeReference;
import com.ibm.domo.util.Atom;

public class X10CAst2IRTranslator extends JavaCAst2IRTranslator {
    public X10CAst2IRTranslator(CAstEntity sourceFileEntity, JavaSourceLoaderImpl loader) {
	super(sourceFileEntity, loader);
    }

    public MethodReference asyncEntityToMethodReference(CAstEntity asyncEntity) {
	CAstType.Method bodyType= (CAstType.Method) asyncEntity.getType();
	CAstType retType= bodyType.getReturnType();
	CAstType owningType= bodyType.getDeclaringType();

	Atom asyncName= Atom.findOrCreateUnicodeAtom(asyncEntity.getName());
	Descriptor asyncDesc= Descriptor.findOrCreate(null, TypeName.string2TypeName(retType.getName()));
	TypeReference owningTypeRef= TypeReference.findOrCreate(fLoader.getReference(), TypeName.string2TypeName(owningType.getName()));

	return MethodReference.findOrCreate(owningTypeRef, asyncName, asyncDesc);
    }

    protected boolean doVisit(CAstNode n, Context context) {
	switch (n.getKind()) {
	    case X10CastNode.ASYNC_INVOKE: {
		if (visitAsyncInvoke(n, context))
		    break;
		leaveAsyncInvoke(n, context);
		break;
	    }
	    case X10CastNode.ATOMIC_ENTER: {
		if (visitAtomicEnter(n, context))
		    break;
		leaveAtomicEnter(n, context);
		break;
	    }
	    case X10CastNode.ATOMIC_EXIT: {
		if (visitAtomicExit(n, context))
		    break;
		leaveAtomicExit(n, context);
		break;
	    }
	    case X10CastNode.FINISH_ENTER: {
		if (visitFinishEnter(n, context))
		    break;
		leaveFinishEnter(n, context);
		break;
	    }
	    case X10CastNode.FINISH_EXIT: {
		if (visitFinishExit(n, context))
		    break;
		leaveFinishExit(n, context);
		break;
	    }
	    case X10CastNode.FORCE: {
		if (visitForce(n, context))
		    break;
		visit(n.getChild(0), context);
		leaveForce(n, context);
		break;
	    }
	    case X10CastNode.REGION_ITER_INIT: {
		if (visitRegionIterInit(n, context))
		    break;
		visit(n.getChild(0), context);
		leaveRegionIterInit(n, context);
		break;
	    }
	    case X10CastNode.REGION_ITER_HASNEXT: {
		if (visitRegionIterHasNext(n, context))
		    break;
		visit(n.getChild(0), context);
		leaveRegionIterHasNext(n, context);
		break;
	    }
	    case X10CastNode.REGION_ITER_NEXT: {
		if (visitRegionIterNext(n, context))
		    break;
		visit(n.getChild(0), context);
		leaveRegionIterNext(n, context);
		break;
	    }
	    case X10CastNode.HERE: {
		if (visitHere(n, context))
		    break;
		leaveHere(n, context);
		break;
	    }
	    default:
		return super.doVisit(n, context);
	}
	return true;
    }

    protected boolean visitAsyncInvoke(CAstNode n, Context c) { /* empty */ return false; }
    protected void leaveAsyncInvoke(CAstNode n, Context c) {
	WalkContext context = (WalkContext)c;
	CAstEntity bodyEntity = (CAstEntity) n.getChild(1).getValue();
	// Figure out whether this is a future or an async
	int exceptValue = context.scope().allocateTempValue();
	AsyncCallSiteReference acsr = new AsyncCallSiteReference(asyncEntityToMethodReference(bodyEntity), context.cfg().getCurrentInstruction());

	if (((CAstType.Function) bodyEntity.getType()).getReturnType() == JavaPrimitiveTypeMap.VoidType)
	    context.cfg().addInstruction(SSAInstructionFactory.InvokeInstruction(new int[0], exceptValue, acsr));
	else {
	    int retValue = context.scope().allocateTempValue();

	    context.cfg().addInstruction(SSAInstructionFactory.InvokeInstruction(retValue, new int[0], exceptValue, acsr));
	    setValue(n, retValue);
	}
    }
    protected boolean visitAtomicEnter(CAstNode n, Context c) { /* empty */ return false; }
    protected void leaveAtomicEnter(CAstNode n, Context c) {
	WalkContext context = (WalkContext)c;
	context.cfg().addInstruction(new SSAAtomicInstruction(true));
    }
    protected boolean visitAtomicExit(CAstNode n, Context c) { /* empty */ return false; }
    protected void leaveAtomicExit(CAstNode n, Context c) {
	WalkContext context = (WalkContext)c;
	context.cfg().addInstruction(new SSAAtomicInstruction(false));
    }
    protected boolean visitFinishEnter(CAstNode n, Context c) { /* empty */ return false; }
    protected void leaveFinishEnter(CAstNode n, Context c) {
	WalkContext context = (WalkContext)c;
	context.cfg().addInstruction(new SSAFinishInstruction(true));
    }
    protected boolean visitFinishExit(CAstNode n, Context c) { /* empty */ return false; }
    protected void leaveFinishExit(CAstNode n, Context c) {
	WalkContext context = (WalkContext)c;
	context.cfg().addInstruction(new SSAFinishInstruction(false));
    }
    protected boolean visitForce(CAstNode n, Context c) { /* empty */ return false; }
    protected void leaveForce(CAstNode n, Context c) {
	WalkContext context = (WalkContext)c;
	int targetValue = getValue(n.getChild(0));
	int retValue = context.scope().allocateTempValue();
	context.cfg().addInstruction(new SSAForceInstruction(retValue, targetValue, (TypeReference) n.getChild(1).getValue()));
	setValue(n, retValue);
    }
    protected boolean visitRegionIterInit(CAstNode n, Context c) { /* empty */ return false; }
    protected void leaveRegionIterInit(CAstNode n, Context c) {
	WalkContext context = (WalkContext)c;
	int targetValue = getValue(n.getChild(0));
	int retValue = context.scope().allocateTempValue();
	context.cfg().addInstruction(new SSARegionIterInitInstruction(retValue, targetValue));
	setValue(n, retValue);
    }
    protected boolean visitRegionIterHasNext(CAstNode n, Context c) { /* empty */ return false; }
    protected void leaveRegionIterHasNext(CAstNode n, Context c) {
	WalkContext context = (WalkContext)c;
	int targetValue = getValue(n.getChild(0));
	int retValue = context.scope().allocateTempValue();
	context.cfg().addInstruction(new SSARegionIterHasNextInstruction(retValue, targetValue));
	setValue(n, retValue);
    }
    protected boolean visitRegionIterNext(CAstNode n, Context c) { /* empty */ return false; }
    protected void leaveRegionIterNext(CAstNode n, Context c) {
	WalkContext context = (WalkContext)c;
	int targetValue = getValue(n.getChild(0));
	int retValue = context.scope().allocateTempValue();
	context.cfg().addInstruction(new SSARegionIterNextInstruction(retValue, targetValue));
	setValue(n, retValue);
    }
    protected boolean visitHere(CAstNode n, Context c) { /* empty */ return false; }
    protected void leaveHere(CAstNode n, Context c) {
	WalkContext context = (WalkContext)c;
	int retValue = context.scope().allocateTempValue();
	context.cfg().addInstruction(new SSAHereInstruction(retValue));
	setValue(n, retValue);
    }

    protected boolean isFunctionEntity(CAstEntity n) {
	return n.getKind() == X10CAstEntity.ASYNC_BODY || super.isFunctionEntity(n);
    }

    protected boolean doVisitEntity(CAstEntity n, Context context) {
	switch (n.getKind()) {
	    case X10CAstEntity.ASYNC_BODY: {
		Context codeContext = makeCodeContext(context, n);
		visitAsyncBodyEntity(n, context, codeContext);
		// visit the AST if any
		if (n.getAST() != null)
		    visit(n.getAST(), codeContext);
		// process any remaining scoped children
		visitScopedEntities(n, n.getScopedEntities(null), codeContext);
		leaveAsyncBodyEntity(n, context, context);
		break;
	    }
	    default:
		return super.doVisitEntity(n, context);
	}
	return true;
    }

    protected boolean visitAsyncBodyEntity(CAstEntity n, Context context, Context codeContext) {
	initFunctionEntity(n, (WalkContext)context, (WalkContext)codeContext);
	return false;
    }
    protected void leaveAsyncBodyEntity(CAstEntity n, Context context, Context codeContext) {
	closeFunctionEntity(n, (WalkContext)context, (WalkContext)codeContext);
    }
}
