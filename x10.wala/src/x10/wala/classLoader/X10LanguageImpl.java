package x10.wala.classLoader;

import java.util.Collection;
import java.util.Collections;

import x10.wala.loader.X10SourceLoaderImpl;

import com.ibm.wala.analysis.typeInference.PrimitiveType;
import com.ibm.wala.classLoader.BytecodeLanguage;
import com.ibm.wala.classLoader.Language;
import com.ibm.wala.classLoader.LanguageImpl;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.shrikeBT.IInstruction;
import com.ibm.wala.shrikeBT.ConstantInstruction.ClassToken;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.ssa.SSAInstructionFactory;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.shrike.ShrikeUtil;
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

  public SSAInstructionFactory instructionFactory() {
    return Language.JAVA.instructionFactory();
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
}
