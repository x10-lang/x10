package com.ibm.domo.ast.x10.analysis.typeInference;

import com.ibm.capa.impl.debug.Assertions;
import com.ibm.domo.analysis.typeInference.ConeType;
import com.ibm.domo.ast.java.analysis.typeInference.AstJavaTypeInference;
import com.ibm.domo.ast.x10.ssa.AstX10InstructionVisitor;
import com.ibm.domo.ast.x10.ssa.SSAAtomicInstruction;
import com.ibm.domo.ast.x10.ssa.SSAFinishInstruction;
import com.ibm.domo.ast.x10.ssa.SSAForceInstruction;
import com.ibm.domo.classLoader.IClass;
import com.ibm.domo.ipa.cha.ClassHierarchy;
import com.ibm.domo.ssa.IR;
import com.ibm.domo.types.TypeReference;

public class AstX10TypeInference extends AstJavaTypeInference {

    public AstX10TypeInference(IR ir, ClassHierarchy cha) {
	super(ir, cha);
    }

    protected class AstX10TypeOperatorFactory extends AstJavaTypeOperatorFactory implements AstX10InstructionVisitor {

	public void visitAtomic(SSAAtomicInstruction instruction) {
	    Assertions.UNREACHABLE("Type operator requested for X10 atomic instruction");
	}

	public void visitFinish(SSAFinishInstruction instruction) {
	    Assertions.UNREACHABLE("Type operator requested for X10 atomic instruction");
	}

	public void visitForce(SSAForceInstruction instruction) {
	    TypeReference type= instruction.getValueType();

	    if (type.isReferenceType()) {
		IClass klass= cha.lookupClass(type);
		if (klass == null) {
		    // a type that cannot be loaded.
		    // be pessimistic
		    result= new DeclaredTypeOperator(BOTTOM);
		} else {
		    result= new DeclaredTypeOperator(new ConeType(klass, cha));
		}
	    } else {
		result= null;
	    }
	}
    }

    protected void initialize() {
	init(ir, new TypeVarFactory(), new AstX10TypeOperatorFactory());
    }
}
