package x10.wala.analysis.typeInference;

import x10.wala.classLoader.X10LanguageImpl;
import x10.wala.ssa.ArrayLoadByIndexInstruction;
import x10.wala.ssa.ArrayLoadByPointInstruction;
import x10.wala.ssa.ArrayStoreByIndexInstruction;
import x10.wala.ssa.ArrayStoreByPointInstruction;
import x10.wala.ssa.AstX10InstructionVisitor;
import x10.wala.ssa.AtStmtInstruction;
import x10.wala.ssa.AtomicInstruction;
import x10.wala.ssa.FinishInstruction;
import x10.wala.ssa.ForceInstruction;
import x10.wala.ssa.HereInstruction;
import x10.wala.ssa.NextInstruction;
import x10.wala.ssa.PlaceOfPointInstruction;
import x10.wala.ssa.RegionIterHasNextInstruction;
import x10.wala.ssa.RegionIterInitInstruction;
import x10.wala.ssa.RegionIterNextInstruction;
import x10.wala.ssa.TupleInstruction;

import com.ibm.wala.analysis.typeInference.ConeType;
import com.ibm.wala.analysis.typeInference.PointType;
import com.ibm.wala.cast.java.analysis.typeInference.AstJavaTypeInference;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.debug.Assertions;

public class AstX10TypeInference extends AstJavaTypeInference {

    public AstX10TypeInference(IR ir, IClassHierarchy cha, boolean doPrimitives) {
	super(ir, cha, doPrimitives);
    }

    protected class AstX10TypeOperatorFactory extends AstJavaTypeOperatorFactory implements AstX10InstructionVisitor {

	public void visitAtomic(AtomicInstruction instruction) {
	    Assertions.UNREACHABLE("Type operator requested for X10 atomic instruction");
	}

	public void visitFinish(FinishInstruction instruction) {
	    Assertions.UNREACHABLE("Type operator requested for X10 finish instruction");
	}
	
	public void visitNext(NextInstruction instruction) {
	    Assertions.UNREACHABLE("Type operator requested for X10 finish instruction");
	}

	public void visitForce(ForceInstruction instruction) {
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

	public void visitRegionIterInit(RegionIterInitInstruction instruction) {
	    // Pretend that this instruction produces a value of type "java.lang.Object".
	    // It produces a region iterator, for which we have no type a priori.
	    TypeReference type= TypeReference.JavaLangObject;
	    IClass klass= cha.lookupClass(type);

	    result= new DeclaredTypeOperator(new ConeType(klass));
	}

	public void visitRegionIterHasNext(RegionIterHasNextInstruction instruction) {
	    // NOOP
	    result= null;
	}

	public void visitRegionIterNext(RegionIterNextInstruction instruction) {
	    // This instruction always produces a value of type "x10.lang.point".
	    TypeReference type= X10LanguageImpl.x10LangPoint;
	    IClass klass= cha.lookupClass(type);

	    result= new DeclaredTypeOperator(new PointType(klass));
	}

	public void visitHere(HereInstruction instruction) {
	    // This instruction always produces a value of type "x10.lang.point".
	    TypeReference type= X10LanguageImpl.x10LangPlace;
	    IClass klass= cha.lookupClass(type);

	    result= new DeclaredTypeOperator(new ConeType(klass));
	}

	public void visitArrayLoadByIndex(ArrayLoadByIndexInstruction instruction) {
	    TypeReference type = instruction.getDeclaredType();
	    IClass klass= cha.lookupClass(type);

	    result = new DeclaredTypeOperator(new ConeType(klass));
	}

	public void visitArrayLoadByPoint(ArrayLoadByPointInstruction instruction) {
	    TypeReference type = instruction.getDeclaredType();
	    IClass klass= cha.lookupClass(type);

	    result = new DeclaredTypeOperator(new ConeType(klass));
	}

	public void visitArrayStoreByIndex(ArrayStoreByIndexInstruction instruction) {
	    result = null; // ??? is this correct ???
	}

	public void visitArrayStoreByPoint(ArrayStoreByPointInstruction instruction) {
	    result = null; // ??? is this correct ???
	}
	
 	public void visitPlaceOfPoint(PlaceOfPointInstruction instruction) {
        TypeReference placeType= X10LanguageImpl.x10LangPlace;
        IClass placeClass = cha.lookupClass(placeType);
        result = new DeclaredTypeOperator(new ConeType(placeClass));
 	}

 	public void visitTuple(TupleInstruction tupleInstruction) {
 	    // This instruction always produces a value of type "x10.lang.Rail".
 	    TypeReference type= X10LanguageImpl.x10LangRail;
 	    IClass klass= cha.lookupClass(type);
 	    result = new DeclaredTypeOperator(new ConeType(klass));
 	}
 	
 	public void visitAtStmt(final AtStmtInstruction atStmtInstruction) {
      Assertions.UNREACHABLE("Type operator requested for X10 atStmt instruction");
    }
    }

    protected void initialize() {
	init(ir, new TypeVarFactory(), new AstX10TypeOperatorFactory());
    }
}
