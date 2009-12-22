package com.ibm.wala.cast.x10.translator.polyglot;

import java.util.HashMap;
import java.util.Map;

import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.MethodDef;
import polyglot.types.PrimitiveType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import x10.types.ConstrainedType;
import x10.types.MacroType;
import x10.types.ParameterType;
import x10.types.X10ClassType;

import com.ibm.wala.cast.java.translator.polyglot.PolyglotIdentityMapper;
import com.ibm.wala.cast.java.types.JavaPrimitiveTypeMap;
import com.ibm.wala.types.ClassLoaderReference;

class X10PolyglotIdentityMapper extends PolyglotIdentityMapper {
    /**
     * Maps the names of certain predefined X10 types onto their Java counterparts.
     */
    private final static Map<String, String> sTypeTranslationMap = new HashMap<String, String>();

    static {
        // TODO Calculate the following values by reading the @nativeRep annotations from the runtime classes.
        sTypeTranslationMap.put("x10.lang.Boolean", "boolean");
        sTypeTranslationMap.put("x10.lang.Byte", "byte");
        sTypeTranslationMap.put("x10.lang.Char", "char");
        sTypeTranslationMap.put("x10.lang.Double", "double");
        sTypeTranslationMap.put("x10.lang.Float", "float");
        sTypeTranslationMap.put("x10.lang.Int", "int");
        sTypeTranslationMap.put("x10.lang.Long", "long");
        sTypeTranslationMap.put("x10.lang.Short", "short");
        sTypeTranslationMap.put("x10.lang.Void", "void");
    }

    public static String getJavaPrimitiveTypeFor(String x10PrimitiveName) {
        return sTypeTranslationMap.get(x10PrimitiveName);
    }

    public X10PolyglotIdentityMapper(ClassLoaderReference clr, TypeSystem ts) {
        super(clr, ts);
    }

    public String typeToTypeID(Type type) {
        /*
         * if (type instanceof NullableType) { return "Lnullable<" + typeToTypeID(((NullableType) type).base()) + ">"; }
         * else if (type instanceof FutureType) { return "Lfuture<" + typeToTypeID(((FutureType) type).base()) + ">"; }
         * else
         */
        if (type instanceof ConstrainedType) {
            ConstrainedType constrainedType = (ConstrainedType) type;
            return typeToTypeID(constrainedType.baseType().get());
        }
        if (type instanceof X10ClassType && ((X10ClassType) type).typeArguments().size() > 0) {
          final X10ClassType classType = (X10ClassType) type;
          if (classType.isLocal() || classType.isAnonymous()) {
            final ClassDef classDef = classType.def().outer().get();
            final MethodDef enclosingMethod = getEnclosingMethod(classDef, classType.position());
            if (enclosingMethod == null) {
              throw new AssertionError("We could not find the enclosing method for the type " + classType);
            }
            mapLocalAnonTypeToMethod(classType, enclosingMethod.asInstance());
          } else {
            final StringBuilder sb = new StringBuilder();
            sb.append('L').append(classType.fullName().toString().replace('.', '/'));
            return sb.toString();
          }
        }
        if (type instanceof ParameterType) {
            Type bound = fTypeSystem.Object(); // How to get the real bound?
            return typeToTypeID(bound);
        }
        if (type instanceof MacroType) {
            MacroType macroType = (MacroType) type;
            return typeToTypeID(macroType.definedType());
        }
        if (type.isPrimitive()) {
            String className = ((PrimitiveType) type).fullName().toString();
            if (sTypeTranslationMap.containsKey(className)) {
                return JavaPrimitiveTypeMap.getShortName(sTypeTranslationMap.get(className));
            }
        }
        return super.typeToTypeID(type);
    }

    public String getBaseTypeName(ClassType classType) {
        StringBuilder sb= new StringBuilder();
        sb.append("L");
        sb.append(classType.fullName());
        return sb.toString();
    }
    
    // --- Private code
    
    // This is a very hackish way of getting the enclosing method, since it is not provided at this time in Polyglot.
    // To change soon...
    private MethodDef getEnclosingMethod(final ClassDef classDef, final Position targetTypePosition) {
      for (final MethodDef methodDef : classDef.methods()) {
        final Position methodPos = methodDef.position();
        if (targetTypePosition.offset() >= methodPos.offset() && targetTypePosition.offset() <= methodPos.endOffset() &&
            targetTypePosition.endOffset() >= methodPos.offset() && targetTypePosition.endOffset() <= methodPos.endOffset()) {
          return methodDef;
        }
      }
      return null;
    }
    
}
