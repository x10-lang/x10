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
    
    protected boolean doProcessNode(CAstNode n, WalkContext context) {
	switch(n.getKind()) {
	    case X10CastNode.ASYNC_INVOKE: {
		CAstEntity bodyEntity= (CAstEntity) n.getChild(1).getValue();
		// Figure out whether this is a future or an async
		int exceptValue= context.scope().allocateTempValue();
		AsyncCallSiteReference acsr= new AsyncCallSiteReference(asyncEntityToMethodReference(bodyEntity), context.cfg().getCurrentInstruction());

		if (((CAstType.Function) bodyEntity.getType()).getReturnType() == JavaPrimitiveTypeMap.VoidType)
		    context.cfg().addInstruction(SSAInstructionFactory.InvokeInstruction(new int[0], exceptValue, acsr));
		else {
		    int retValue= context.scope().allocateTempValue();

		    context.cfg().addInstruction(SSAInstructionFactory.InvokeInstruction(retValue, new int[0], exceptValue, acsr));
		    setValue(n, retValue);
		}
		break;
	    }
	    case X10CastNode.ATOMIC_ENTER: {
		context.cfg().addInstruction(new SSAAtomicInstruction(true));
		break;
	    }
	    case X10CastNode.ATOMIC_EXIT: {
		context.cfg().addInstruction(new SSAAtomicInstruction(false));
		break;
	    }
	    case X10CastNode.FINISH_ENTER: {
		context.cfg().addInstruction(new SSAFinishInstruction(true));
		break;
	    }
	    case X10CastNode.FINISH_EXIT: {
		context.cfg().addInstruction(new SSAFinishInstruction(false));
		break;
	    }
	}
        return super.doProcessNode(n, context);
    }

    protected boolean doProcessEntity(CAstEntity n, WalkContext context) {
	switch(n.getKind()) {
	    case X10CAstEntity.ASYNC_BODY: {
		handleConcreteFunction(n, context);
		break;
	    }
	}
        return super.doProcessEntity(n, context);
    }
}
