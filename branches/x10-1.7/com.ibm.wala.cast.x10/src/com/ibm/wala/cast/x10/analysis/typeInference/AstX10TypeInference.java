package com.ibm.wala.cast.x10.analysis.typeInference;

import com.ibm.wala.util.debug.Assertions;
import com.ibm.wala.analysis.typeInference.ConeType;
import com.ibm.wala.analysis.typeInference.PointType;
import com.ibm.wala.cast.x10.loader.X10PrimordialClassLoader;
import com.ibm.wala.cast.x10.ssa.AstX10InstructionVisitor;
import com.ibm.wala.cast.x10.ssa.SSAAtomicInstruction;
import com.ibm.wala.cast.x10.ssa.SSAFinishInstruction;
import com.ibm.wala.cast.x10.ssa.SSAForceInstruction;
import com.ibm.wala.cast.x10.ssa.SSAHereInstruction;
import com.ibm.wala.cast.x10.ssa.SSAPlaceOfPointInstruction;
import com.ibm.wala.cast.x10.ssa.SSARegionIterHasNextInstruction;
import com.ibm.wala.cast.x10.ssa.SSARegionIterInitInstruction;
import com.ibm.wala.cast.x10.ssa.SSARegionIterNextInstruction;
import com.ibm.wala.cast.x10.ssa.X10ArrayLoadByIndexInstruction;
import com.ibm.wala.cast.x10.ssa.X10ArrayLoadByPointInstruction;
import com.ibm.wala.cast.x10.ssa.X10ArrayStoreByIndexInstruction;
import com.ibm.wala.cast.x10.ssa.X10ArrayStoreByPointInstruction;
import com.ibm.wala.cast.x10.types.X10TypeReference;
import com.ibm.wala.cast.java.analysis.typeInference.AstJavaTypeInference;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.types.TypeReference;

public class AstX10TypeInference extends AstJavaTypeInference {

    public AstX10TypeInference(IR ir, IClassHierarchy cha, boolean doPrimitives) {
	super(ir, cha, doPrimitives);
    }

    protected class AstX10TypeOperatorFactory extends AstJavaTypeOperatorFactory implements AstX10InstructionVisitor {

	public void visitAtomic(SSAAtomicInstruction instruction) {
	    Assertions.UNREACHABLE("Type operator requested for X10 atomic instruction");
	}

	public void visitFinish(SSAFinishInstruction instruction) {
	    Assertions.UNREACHABLE("Type operator requested for X10 finish instruction");
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
		    result= new DeclaredTypeOperator(new ConeType(klass));
		}
	    } else {
		result= null;
	    }
	}

	public void visitRegionIterInit(SSARegionIterInitInstruction instruction) {
	    // Pretend that this instruction produces a value of type "java.lang.Object".
	    // It produces a region iterator, for which we have no type a priori.
	    TypeReference type= TypeReference.JavaLangObject;
	    IClass klass= cha.lookupClass(type);

	    result= new DeclaredTypeOperator(new ConeType(klass));
	}

	public void visitRegionIterHasNext(SSARegionIterHasNextInstruction instruction) {
	    // NOOP
	    result= null;
	}

	public void visitRegionIterNext(SSARegionIterNextInstruction instruction) {
	    // This instruction always produces a value of type "x10.lang.point".
	    TypeReference type= X10TypeReference.x10LangPoint;
	    IClass klass= cha.lookupClass(type);

	    result= new DeclaredTypeOperator(new PointType(klass));
	}

	public void visitHere(SSAHereInstruction instruction) {
	    // This instruction always produces a value of type "x10.lang.point".
	    TypeReference type= X10TypeReference.x10LangPlace;
	    IClass klass= cha.lookupClass(type);

	    result= new DeclaredTypeOperator(new ConeType(klass));
	}

	public void visitArrayLoadByIndex(X10ArrayLoadByIndexInstruction instruction) {
	    TypeReference type = instruction.getDeclaredType();
	    IClass klass= cha.lookupClass(type);

	    result = new DeclaredTypeOperator(new ConeType(klass));
	}

	public void visitArrayLoadByPoint(X10ArrayLoadByPointInstruction instruction) {
	    TypeReference type = instruction.getDeclaredType();
	    IClass klass= cha.lookupClass(type);

	    result = new DeclaredTypeOperator(new ConeType(klass));
	}

	public void visitArrayStoreByIndex(X10ArrayStoreByIndexInstruction instruction) {
	    result = null; // ??? is this correct ???
	}

	public void visitArrayStoreByPoint(X10ArrayStoreByPointInstruction instruction) {
	    result = null; // ??? is this correct ???
	}
	
 	public void visitPlaceOfPoint(SSAPlaceOfPointInstruction instruction) {
 		 		TypeReference placeType = TypeReference.findOrCreate(X10PrimordialClassLoader.X10Primordial, "Lx10/lang/place");
 		 		IClass placeClass = cha.lookupClass(placeType);
 		 		result = new DeclaredTypeOperator(new ConeType(placeClass));
 	}
 	
    }

    protected void initialize() {
	init(ir, new TypeVarFactory(), new AstX10TypeOperatorFactory());
    }
}
