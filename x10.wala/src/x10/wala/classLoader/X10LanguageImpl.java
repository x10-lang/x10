package x10.wala.classLoader;

import java.util.Collection;

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
  
  public static X10LanguageImpl X10Lang = new X10LanguageImpl();

  public static TypeReference X10LangObject = TypeReference.findOrCreate(X10SourceLoaderImpl.X10SourceLoader, "Lx10/lang/Object");

  public final static TypeReference x10LangPoint =
    TypeReference.findOrCreate(X10SourceLoaderImpl.X10SourceLoader, "Lx10/lang/point");

  public final static TypeReference x10LangPlace =
    TypeReference.findOrCreate(X10SourceLoaderImpl.X10SourceLoader, "Lx10/lang/place");

  public final static TypeReference x10LangRail =
    TypeReference.findOrCreate(X10SourceLoaderImpl.X10SourceLoader, "Lx10/lang/Rail");

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
      return ShrikeUtil.makeTypeReference(X10SourceLoaderImpl.X10SourceLoader, ((ClassToken) value).getTypeName());
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

  public SSAInstructionFactory instructionFactory() {
    return Language.JAVA.instructionFactory();
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
}
