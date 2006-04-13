/*
 * Created on Sep 26, 2005
 */
package com.ibm.domo.ast.x10.translator.polyglot;

import java.io.PrintWriter;

import com.ibm.capa.ast.CAstEntity;
import com.ibm.capa.ast.CAstNode;
import com.ibm.capa.ast.CAstType;
import com.ibm.capa.ast.visit.*;
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
import com.ibm.domo.ast.x10.translator.X10CAstPrinter;
import com.ibm.domo.ast.x10.translator.X10CastNode;
import com.ibm.domo.ast.x10.visit.X10CAstVisitor;
import com.ibm.domo.ssa.SSAInstructionFactory;
import com.ibm.domo.types.Descriptor;
import com.ibm.domo.types.MethodReference;
import com.ibm.domo.types.TypeName;
import com.ibm.domo.types.TypeReference;
import com.ibm.domo.util.Atom;
import com.ibm.capa.util.debug.Trace;

import com.ibm.domo.ast.translator.AstTranslator.WalkContext;
import com.ibm.domo.ast.translator.AstTranslator.DefaultContext;

public class X10CAst2IRTranslator extends X10CAstVisitor {
    public X10CAst2IRTranslator(CAstEntity sourceFileEntity, JavaSourceLoaderImpl loader) {
	this(new JavaCAst2IRTranslator(sourceFileEntity, loader));
    }

    private final JavaCAst2IRTranslator translator;

    private X10CAst2IRTranslator(JavaCAst2IRTranslator translator) {
	super(translator);
	this.translator = translator;
    }

    public MethodReference asyncEntityToMethodReference(CAstEntity asyncEntity) {
	CAstType.Method bodyType= (CAstType.Method) asyncEntity.getType();
	CAstType retType= bodyType.getReturnType();
	CAstType owningType= bodyType.getDeclaringType();
	JavaSourceLoaderImpl fLoader = translator.loader();

	Atom asyncName= Atom.findOrCreateUnicodeAtom(asyncEntity.getName());
	Descriptor asyncDesc= Descriptor.findOrCreate(null, TypeName.string2TypeName(retType.getName()));
	TypeReference owningTypeRef= TypeReference.findOrCreate(fLoader.getReference(), TypeName.string2TypeName(owningType.getName()));

	return MethodReference.findOrCreate(owningTypeRef, asyncName, asyncDesc);
    }

    protected boolean visitAsyncInvoke(CAstNode n, Context c, CAstVisitor visitor) { /* empty */ return false; }
    protected void leaveAsyncInvoke(CAstNode n, Context c, CAstVisitor visitor) {
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
	    translator.setValue(n, retValue);
	}
    }
    protected boolean visitAtomicEnter(CAstNode n, Context c, CAstVisitor visitor) { /* empty */ return false; }
    protected void leaveAtomicEnter(CAstNode n, Context c, CAstVisitor visitor) {
	WalkContext context = (WalkContext)c;
	context.cfg().addInstruction(new SSAAtomicInstruction(true));
    }
    protected boolean visitAtomicExit(CAstNode n, Context c, CAstVisitor visitor) { /* empty */ return false; }
    protected void leaveAtomicExit(CAstNode n, Context c, CAstVisitor visitor) {
	WalkContext context = (WalkContext)c;
	context.cfg().addInstruction(new SSAAtomicInstruction(false));
    }
    protected boolean visitFinishEnter(CAstNode n, Context c, CAstVisitor visitor) { /* empty */ return false; }
    protected void leaveFinishEnter(CAstNode n, Context c, CAstVisitor visitor) {
	WalkContext context = (WalkContext)c;
	context.cfg().addInstruction(new SSAFinishInstruction(true));
    }
    protected boolean visitFinishExit(CAstNode n, Context c, CAstVisitor visitor) { /* empty */ return false; }
    protected void leaveFinishExit(CAstNode n, Context c, CAstVisitor visitor) {
	WalkContext context = (WalkContext)c;
	context.cfg().addInstruction(new SSAFinishInstruction(false));
    }
    protected boolean visitForce(CAstNode n, Context c, CAstVisitor visitor) { /* empty */ return false; }
    protected void leaveForce(CAstNode n, Context c, CAstVisitor visitor) {
	WalkContext context = (WalkContext)c;
	int targetValue = translator.getValue(n.getChild(0));
	int retValue = context.scope().allocateTempValue();
	context.cfg().addInstruction(new SSAForceInstruction(retValue, targetValue, (TypeReference) n.getChild(1).getValue()));
	translator.setValue(n, retValue);
    }
    protected boolean visitRegionIterInit(CAstNode n, Context c, CAstVisitor visitor) { /* empty */ return false; }
    protected void leaveRegionIterInit(CAstNode n, Context c, CAstVisitor visitor) {
	WalkContext context = (WalkContext)c;
	int targetValue = translator.getValue(n.getChild(0));
	int retValue = context.scope().allocateTempValue();
	context.cfg().addInstruction(new SSARegionIterInitInstruction(retValue, targetValue));
	translator.setValue(n, retValue);
    }
    protected boolean visitRegionIterHasNext(CAstNode n, Context c, CAstVisitor visitor) { /* empty */ return false; }
    protected void leaveRegionIterHasNext(CAstNode n, Context c, CAstVisitor visitor) {
	WalkContext context = (WalkContext)c;
	int targetValue = translator.getValue(n.getChild(0));
	int retValue = context.scope().allocateTempValue();
	context.cfg().addInstruction(new SSARegionIterHasNextInstruction(retValue, targetValue));
	translator.setValue(n, retValue);
    }
    protected boolean visitRegionIterNext(CAstNode n, Context c, CAstVisitor visitor) { /* empty */ return false; }
    protected void leaveRegionIterNext(CAstNode n, Context c, CAstVisitor visitor) {
	WalkContext context = (WalkContext)c;
	int targetValue = translator.getValue(n.getChild(0));
	int retValue = context.scope().allocateTempValue();
	context.cfg().addInstruction(new SSARegionIterNextInstruction(retValue, targetValue));
	translator.setValue(n, retValue);
    }
    protected boolean visitHere(CAstNode n, Context c, CAstVisitor visitor) { /* empty */ return false; }
    protected void leaveHere(CAstNode n, Context c, CAstVisitor visitor) {
	WalkContext context = (WalkContext)c;
	int retValue = context.scope().allocateTempValue();
	context.cfg().addInstruction(new SSAHereInstruction(retValue));
	translator.setValue(n, retValue);
    }

    protected boolean visitAsyncBodyEntity(CAstEntity n, Context context, Context codeContext, CAstVisitor visitor) {
	translator.initFunctionEntity(n, (WalkContext)context, (WalkContext)codeContext);
	return false;
    }
    protected void leaveAsyncBodyEntity(CAstEntity n, Context context, Context codeContext, CAstVisitor visitor) {
	translator.closeFunctionEntity(n, (WalkContext)context, (WalkContext)codeContext);
    }

    private void translate(final CAstEntity N, final String nm) {
	if (translator.DEBUG_TOP)
	    Trace.println("translating " + nm);
//	PrintWriter printWriter= new PrintWriter(System.out);
//	X10CAstPrinter.printTo(N, printWriter);
//	printWriter.flush();
	visitEntities(N, new DefaultContext(translator, N, nm), this);
    }

    /* UGH! */
    public void translate() {
	CAstEntity fSourceEntity = translator.sourceFileEntity();
	translate(fSourceEntity, fSourceEntity.getName());
    }
}

