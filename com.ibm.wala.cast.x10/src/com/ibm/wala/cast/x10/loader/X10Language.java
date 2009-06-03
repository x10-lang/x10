package com.ibm.wala.cast.x10.loader;

import java.util.Collection;

import com.ibm.wala.analysis.typeInference.PrimitiveType;
import com.ibm.wala.cast.ir.ssa.AstLexicalAccess.Access;
import com.ibm.wala.cast.java.loader.JavaSourceLoaderImpl;
import com.ibm.wala.cast.x10.ssa.AsyncInvokeInstruction;
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
import com.ibm.wala.cast.x10.ssa.X10InstructionFactory;
import com.ibm.wala.classLoader.BytecodeLanguage;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.Language;
import com.ibm.wala.classLoader.LanguageImpl;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.shrikeBT.IInstruction;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.ssa.SSAInstructionFactory;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.strings.Atom;

public class X10Language extends LanguageImpl implements BytecodeLanguage {
    /**
     * Canonical name for the X10 language
     */
    public final static Atom X10 = Atom.findOrCreateUnicodeAtom("X10");

    public static X10Language X10Lang = new X10Language();

    private X10Language() {
	super(Language.JAVA);
    }

    public Atom getName() {
	return X10;
    }

    public TypeReference getRootType() {
	return Language.JAVA.getRootType();
    }

    public TypeReference getThrowableType() {
	return TypeReference.JavaLangThrowable;
    }

    public TypeReference getConstantType(Object o) {
	return Language.JAVA.getConstantType(o);
    }

    public boolean isNullType(TypeReference type) {
	return Language.JAVA.isNullType(type);
    }

	public TypeReference[] getArrayInterfaces() {
		return Language.JAVA.getArrayInterfaces();
	}

	public TypeName lookupPrimitiveType(String name) {
		return Language.JAVA.lookupPrimitiveType(name);
	}

	public Collection<TypeReference> getImplicitExceptionTypes(
			IInstruction pei) {
		return Language.JAVA.getImplicitExceptionTypes(pei);
	}

	public Collection<TypeReference> inferInvokeExceptions(
			MethodReference target, IClassHierarchy cha)
			throws InvalidClassFileException {
		return Language.JAVA.inferInvokeExceptions(target, cha);
	}

	public Object getMetadataToken(Object value) {
		return Language.JAVA.getMetadataToken(value);
	}

	public boolean isDoubleType(TypeReference type) {
		return Language.JAVA.isDoubleType(type);
	}

	public boolean isFloatType(TypeReference type) {
		return Language.JAVA.isFloatType(type);
	}

	public boolean isIntType(TypeReference type) {
		return Language.JAVA.isIntType(type);
	}

	public boolean isLongType(TypeReference type) {
		return Language.JAVA.isLongType(type);
	}

	public boolean isMetadataType(TypeReference type) {
		return Language.JAVA.isMetadataType(type);
	}

	public boolean isStringType(TypeReference type) {
		return Language.JAVA.isStringType(type);
	}

	public static class InstructionFactory extends JavaSourceLoaderImpl.InstructionFactory implements X10InstructionFactory {

		public X10ArrayLoadByIndexInstruction ArrayLoadByIndex(int result,
				int arrayRef, int[] dims, TypeReference declaredType) {
			return new X10ArrayLoadByIndexInstruction(result, arrayRef, dims, declaredType);
		}

		public X10ArrayLoadByPointInstruction ArrayLoadByPoint(int result,
				int arrayRef, int pointIndex, TypeReference declaredType) {
			return new X10ArrayLoadByPointInstruction(result, arrayRef, pointIndex, declaredType);
		}

		public X10ArrayStoreByIndexInstruction ArrayStoreByIndex(int arrayRef,
				int[] indices, int value, TypeReference declaredType) {
			return new X10ArrayStoreByIndexInstruction(arrayRef, indices, value, declaredType);
		}

		public X10ArrayStoreByPointInstruction ArrayStoreByPoint(int arrayRef,
				int pointIndex, int value, TypeReference declaredType) {
			return new X10ArrayStoreByPointInstruction(arrayRef, pointIndex, value, declaredType);
		}

		public AsyncInvokeInstruction AsyncInvoke(int result, int[] params,
				int exception, CallSiteReference site, int placeExpr,
				int[] clocks) {
			return new AsyncInvokeInstruction(result, params, exception, site, placeExpr, clocks);
		}

		public AsyncInvokeInstruction AsyncInvoke(int[] params, int exception,
				CallSiteReference site, int placeExpr, int[] clocks) {
			return new AsyncInvokeInstruction(params, exception, site, placeExpr, clocks);
		}

		public AsyncInvokeInstruction AsyncInvoke(int[] results, int[] params,
				int exception, Access[] lexicalReads, Access[] lexicalWrites,
				CallSiteReference csr) {
			return new AsyncInvokeInstruction(results, params, exception, lexicalReads, lexicalWrites, csr);
		}

		public SSAAtomicInstruction Atomic(boolean isEnter) {
			return new SSAAtomicInstruction(isEnter);
		}

		public SSAFinishInstruction Finish(boolean isEnter) {
			return new SSAFinishInstruction(isEnter);
		}

		public SSAForceInstruction Force(int retValue, int targetValue,
				TypeReference valueType) {
			return new SSAForceInstruction(retValue, targetValue, valueType);
		}

		public SSAHereInstruction Here(int retValue) {
			return new SSAHereInstruction(retValue);
		}

		public SSAPlaceOfPointInstruction PlaceOfPoint(int hasNextValue,
				int regionIter) {
			return new SSAPlaceOfPointInstruction(hasNextValue, regionIter);
		}

		public SSARegionIterHasNextInstruction RegionIterHasNext(
				int hasNextValue, int regionIter) {
			return new SSARegionIterHasNextInstruction(hasNextValue, regionIter);
		}

		public SSARegionIterInitInstruction RegionIterInit(int iterVal,
				int regionVal) {
			return new SSARegionIterInitInstruction(iterVal, regionVal);
		}

		public SSARegionIterNextInstruction RegionIterNext(int nextValue,
				int regionIter) {
			return new SSARegionIterNextInstruction(nextValue, regionIter);
		}
		
	}
	
	private static final InstructionFactory insts = new InstructionFactory();
	
	public InstructionFactory instructionFactory() {
		return insts;
	}

	public boolean isVoidType(TypeReference type) {
		return Language.JAVA.isVoidType(type);
	}

	public TypeReference getPointerType(TypeReference pointee) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("X10 does not permit explicit pointers");
	}

	public TypeReference getMetadataType() {
		return Language.JAVA.getMetadataType();
	}

	public TypeReference getStringType() {
		return Language.JAVA.getStringType();
	}

	public PrimitiveType getPrimitive(TypeReference reference) {
		return Language.JAVA.getPrimitive(reference);
	}

	public boolean isBooleanType(TypeReference type) {
		return Language.JAVA.isBooleanType(type);
	}

	public boolean isCharType(TypeReference type) {
		return Language.JAVA.isCharType(type);
	}

}
