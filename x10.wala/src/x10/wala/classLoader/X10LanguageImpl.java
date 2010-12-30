package x10.wala.classLoader;

import java.util.Collection;
import java.util.Collections;

import x10.wala.loader.X10SourceLoaderImpl;
import x10.wala.ssa.AstX10InstructionFactory;
import x10.wala.ssa.AsyncInvokeInstruction;
import x10.wala.ssa.AtStmtInstruction;
import x10.wala.ssa.AtomicInstruction;
import x10.wala.ssa.FinishInstruction;
import x10.wala.ssa.HereInstruction;
import x10.wala.ssa.IterHasNextInstruction;
import x10.wala.ssa.IterInitInstruction;
import x10.wala.ssa.IterNextInstruction;
import x10.wala.ssa.NextInstruction;
import x10.wala.ssa.PlaceOfPointInstruction;
import x10.wala.ssa.TupleInstruction;

import com.ibm.wala.analysis.typeInference.PrimitiveType;
import com.ibm.wala.cast.ir.ssa.AstLexicalAccess.Access;
import com.ibm.wala.cast.java.loader.JavaSourceLoaderImpl;
import com.ibm.wala.classLoader.BytecodeLanguage;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.Language;
import com.ibm.wala.classLoader.LanguageImpl;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.shrikeBT.IInstruction;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.strings.Atom;

public class X10LanguageImpl extends LanguageImpl implements BytecodeLanguage {
  // FIXME: unsigned primitives, String, exceptions, Any vs Object

  public static X10LanguageImpl X10Lang = new X10LanguageImpl();

  public Atom getName() {
    return X10SourceLoaderImpl.X10;
  }

  public TypeReference getAnyType() {
    return TypeReference.findOrCreate(X10SourceLoaderImpl.X10SourceLoader, TypeName.findOrCreate("Lx10/lang/Any"));
  }

  public TypeReference getIteratorType() {
    return TypeReference.findOrCreate(X10SourceLoaderImpl.X10SourceLoader, TypeName.findOrCreate("Lx10/lang/Iterator"));
  }

  public TypeReference getPlaceType() {
    return TypeReference.findOrCreate(X10SourceLoaderImpl.X10SourceLoader, TypeName.findOrCreate("Lx10/lang/Place"));
  }

  public TypeReference getArrayType() {
    return TypeReference.findOrCreate(X10SourceLoaderImpl.X10SourceLoader, TypeName.findOrCreate("Lx10/array/Array"));
  }

  public TypeReference getRootType() {
    return TypeReference.findOrCreate(X10SourceLoaderImpl.X10SourceLoader, TypeName.findOrCreate("Lx10/lang/Object"));
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
    return new TypeReference[0];
  }

  public TypeName lookupPrimitiveType(String name) {
    return Language.JAVA.lookupPrimitiveType(name);
  }

  public Collection<TypeReference> getImplicitExceptionTypes(IInstruction pei) {
     // return Language.JAVA.getImplicitExceptionTypes(pei);
    return Collections.<TypeReference>emptyList();
  }

  public Collection<TypeReference> inferInvokeExceptions(MethodReference target, IClassHierarchy cha) throws InvalidClassFileException {
    // return Language.JAVA.inferInvokeExceptions(target, cha);
    return Collections.<TypeReference>emptyList();
  }

  public Object getMetadataToken(Object value) {
      throw new UnsupportedOperationException("X10 does not permit meta data");
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

  public TypeReference getMetadataType() {
    throw new UnsupportedOperationException("X10 does not permit meta data");
  }

  public TypeReference getPointerType(final TypeReference pointee) {
    throw new UnsupportedOperationException("X10 does not permit explicit pointers");
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

    public HereInstruction Here(int retValue) {
      return new HereInstruction(retValue);
    }

    public PlaceOfPointInstruction PlaceOfPoint(int hasNextValue, int regionIter) {
      return new PlaceOfPointInstruction(hasNextValue, regionIter);
    }

    public IterHasNextInstruction IterHasNext(int hasNextValue, int regionIter) {
      return new IterHasNextInstruction(hasNextValue, regionIter);
    }

    public IterInitInstruction IterInit(int iterVal, int regionVal) {
      return new IterInitInstruction(iterVal, regionVal);
    }

    public IterNextInstruction IterNext(int nextValue, int regionIter) {
      return new IterNextInstruction(nextValue, regionIter);
    }

    public TupleInstruction Tuple(int retValue, int[] slotValues) {
      return new TupleInstruction(retValue, slotValues);
    }
    
    public AtStmtInstruction AtStmt(final boolean isEnter) {
      return new AtStmtInstruction(isEnter);
    }
  }

  private static final InstructionFactory insts = new InstructionFactory();

  public InstructionFactory instructionFactory() {
      return insts;
  }
}
