package com.ibm.wala.cast.x10.translator.polyglot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyglot.ext.x10.types.*;
import polyglot.types.*;

import com.ibm.wala.cast.java.translator.polyglot.*;
import com.ibm.wala.cast.java.types.JavaPrimitiveTypeMap;
import com.ibm.wala.cast.tree.CAstEntity;
import com.ibm.wala.types.*;

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
            X10ClassType classType = (X10ClassType) type;
            List<Type> typeParams = new ArrayList<Type>(classType.typeArguments().size());
            StringBuilder sb= new StringBuilder();

            sb.append("L");
            sb.append(classType.fullName().toString().replace('.', '/'));
            // Ignore the type arguments - they're not part of the type's "name"
//            sb.append("<");
//            int idx= 0;
//            for(Type typeParam: classType.typeArguments()) {
//                if (idx++ > 0) { sb.append(","); }
//                sb.append(typeToTypeID(typeParam));
//            }
//            sb.append(">");
            return sb.toString();
        }
        if (type instanceof ParameterType) {
            ParameterType parameterType = (ParameterType) type;
            Type bound = fTypeSystem.Object(); // How to get the real bound?
            return typeToTypeID(bound);
        }
        if (type instanceof ClosureType) {
            ClosureType closureType = (ClosureType) type;
            closureType.typeArguments();
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
}
