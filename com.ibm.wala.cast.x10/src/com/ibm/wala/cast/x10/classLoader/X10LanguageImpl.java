package com.ibm.wala.cast.x10.classLoader;

import java.util.Collection;

import com.ibm.wala.analysis.typeInference.PrimitiveType;
import com.ibm.wala.cast.ir.ssa.AstLexicalAccess.Access;
import com.ibm.wala.cast.java.loader.JavaSourceLoaderImpl;
import com.ibm.wala.cast.x10.loader.X10SourceLoaderImpl;
import com.ibm.wala.cast.x10.ssa.AsyncInvokeInstruction;
import com.ibm.wala.cast.x10.ssa.TupleInstruction;
import com.ibm.wala.cast.x10.ssa.AtStmtInstruction;
import com.ibm.wala.cast.x10.ssa.AtomicInstruction;
import com.ibm.wala.cast.x10.ssa.FinishInstruction;
import com.ibm.wala.cast.x10.ssa.ForceInstruction;
import com.ibm.wala.cast.x10.ssa.HereInstruction;
import com.ibm.wala.cast.x10.ssa.NextInstruction;
import com.ibm.wala.cast.x10.ssa.PlaceOfPointInstruction;
import com.ibm.wala.cast.x10.ssa.RegionIterHasNextInstruction;
import com.ibm.wala.cast.x10.ssa.RegionIterInitInstruction;
import com.ibm.wala.cast.x10.ssa.RegionIterNextInstruction;
import com.ibm.wala.cast.x10.ssa.ArrayLoadByIndexInstruction;
import com.ibm.wala.cast.x10.ssa.ArrayLoadByPointInstruction;
import com.ibm.wala.cast.x10.ssa.ArrayStoreByIndexInstruction;
import com.ibm.wala.cast.x10.ssa.ArrayStoreByPointInstruction;
import com.ibm.wala.cast.x10.ssa.AstX10InstructionFactory;
import com.ibm.wala.classLoader.BytecodeLanguage;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.Language;
import com.ibm.wala.classLoader.LanguageImpl;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.shrikeBT.IInstruction;
import com.ibm.wala.shrikeBT.ConstantInstruction.ClassToken;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.shrike.ShrikeUtil;
import com.ibm.wala.util.strings.Atom;

public class X10LanguageImpl extends LanguageImpl implements BytecodeLanguage {
  
  public static X10LanguageImpl X10Lang = new X10LanguageImpl();

  public static TypeReference X10LangObject = TypeReference.findOrCreate(X10SourceLoaderImpl.X10SourceLoader, "Lx10/lang/Object");

  private X10LanguageImpl() {
    super(Language.JAVA);
  }

  public Atom getName() {
    return X10SourceLoaderImpl.X10;
  }

  public TypeReference getRootType() {
    return X10LangObject;
  }

  public TypeReference getThrowableType() {
    return TypeReference.findOrCreate(X10SourceLoaderImpl.X10SourceLoader, TypeName.findOrCreate("Lx10/lang/Throwable"));
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

  public Collection<TypeReference> getImplicitExceptionTypes(IInstruction pei) {
    return Language.JAVA.getImplicitExceptionTypes(pei);
  }

  public Collection<TypeReference> inferInvokeExceptions(MethodReference target, IClassHierarchy cha) throws InvalidClassFileException {
    return Language.JAVA.inferInvokeExceptions(target, cha);
  }

  public Object getMetadataToken(Object value) {
    if (value instanceof ClassToken) {
      return ShrikeUtil.makeTypeReference(X10PrimordialClassLoaderImpl.X10Primordial, ((ClassToken) value).getTypeName());
    } else {
      assert value instanceof TypeReference;
      return value;
    }
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

  public InstructionFactory instructionFactory() {
    return insts;
  }

  public TypeReference getMetadataType() {
    return Language.JAVA.getMetadataType();
  }

  public TypeReference getPointerType(final TypeReference pointee) {
    return Language.JAVA.getPointerType(pointee);
  }

  public PrimitiveType getPrimitive(final TypeReference reference) {
    return Language.JAVA.getPrimitive(reference);
  }

  public TypeReference getStringType() {
    return Language.JAVA.getStringType();
  }

  public boolean isBooleanType(final TypeReference type) {
    return Language.JAVA.isBooleanType(type);
  }

  public boolean isCharType(final TypeReference type) {
    return Language.JAVA.isCharType(type);
  }

  public boolean isVoidType(TypeReference type) {
    return Language.JAVA.isVoidType(type);
  }

  public static class InstructionFactory extends JavaSourceLoaderImpl.InstructionFactory implements AstX10InstructionFactory {

    public ArrayLoadByIndexInstruction ArrayLoadByIndex(int result, int arrayRef, int[] dims, TypeReference declaredType) {
      return new ArrayLoadByIndexInstruction(result, arrayRef, dims, declaredType);
    }

    public ArrayLoadByPointInstruction ArrayLoadByPoint(int result, int arrayRef, int pointIndex, TypeReference declaredType) {
      return new ArrayLoadByPointInstruction(result, arrayRef, pointIndex, declaredType);
    }

    public ArrayStoreByIndexInstruction ArrayStoreByIndex(int arrayRef, int[] indices, int value, TypeReference declaredType) {
      return new ArrayStoreByIndexInstruction(arrayRef, indices, value, declaredType);
    }

    public ArrayStoreByPointInstruction ArrayStoreByPoint(int arrayRef, int pointIndex, int value,
                                                             TypeReference declaredType) {
      return new ArrayStoreByPointInstruction(arrayRef, pointIndex, value, declaredType);
    }

    public AsyncInvokeInstruction AsyncInvoke(int result, int[] params, int exception, CallSiteReference site,
                                              int[] clocks) {
      return new AsyncInvokeInstruction(result, params, exception, site, clocks);
    }

    public AsyncInvokeInstruction AsyncInvoke(int[] params, int exception, CallSiteReference site, int[] clocks) {
      return new AsyncInvokeInstruction(params, exception, site, clocks);
    }

    public AsyncInvokeInstruction AsyncInvoke(int[] results, int[] params, int exception, Access[] lexicalReads,
                                              Access[] lexicalWrites, CallSiteReference csr) {
      return new AsyncInvokeInstruction(results, params, exception, lexicalReads, lexicalWrites, csr);
    }

    public AtomicInstruction Atomic(boolean isEnter) {
      return new AtomicInstruction(isEnter);
    }

    public FinishInstruction Finish(boolean isEnter) {
      return new FinishInstruction(isEnter);
    }
    
    public NextInstruction Next() {
        return new NextInstruction();
      }

    public ForceInstruction Force(int retValue, int targetValue, TypeReference valueType) {
      return new ForceInstruction(retValue, targetValue, valueType);
    }

    public HereInstruction Here(int retValue) {
      return new HereInstruction(retValue);
    }

    public PlaceOfPointInstruction PlaceOfPoint(int hasNextValue, int regionIter) {
      return new PlaceOfPointInstruction(hasNextValue, regionIter);
    }

    public RegionIterHasNextInstruction RegionIterHasNext(int hasNextValue, int regionIter) {
      return new RegionIterHasNextInstruction(hasNextValue, regionIter);
    }

    public RegionIterInitInstruction RegionIterInit(int iterVal, int regionVal) {
      return new RegionIterInitInstruction(iterVal, regionVal);
    }

    public RegionIterNextInstruction RegionIterNext(int nextValue, int regionIter) {
      return new RegionIterNextInstruction(nextValue, regionIter);
    }

    public TupleInstruction NewTuple(int retValue, int[] slotValues) {
      return new TupleInstruction(retValue, slotValues);
    }
    
    public AtStmtInstruction AtStmt(final boolean isEnter) {
      return new AtStmtInstruction(isEnter);
    }
    
  }

  private static final InstructionFactory insts = new InstructionFactory();
  
}
