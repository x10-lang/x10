/*
 * Created on Nov 3, 2005
 */
package com.ibm.wala.cast.x10.translator.polyglot;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import polyglot.types.ClassType;
import polyglot.types.PrimitiveType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import x10.types.ConstrainedType;
import x10.types.MacroType;
import x10.types.ParameterType;
import x10.types.X10ClassType;

import com.ibm.wala.cast.java.translator.polyglot.PolyglotJava2CAstTranslator;
import com.ibm.wala.cast.java.translator.polyglot.PolyglotTypeDictionary;
import com.ibm.wala.cast.java.types.JavaPrimitiveTypeMap;
import com.ibm.wala.cast.tree.CAstType;
import com.ibm.wala.cast.x10.translator.polyglot.X10toCAstTranslator.PolyglotJavaParametricType;

public class X10TypeDictionary extends PolyglotTypeDictionary {
    public X10TypeDictionary(TypeSystem typeSystem, PolyglotJava2CAstTranslator translator) {
        super(typeSystem, translator);
    }

    public CAstType getCAstTypeFor(Object astType) {
        if (astType instanceof ConstrainedType) {
          return getCAstTypeFor(((ConstrainedType) astType).baseType().get());
        }
        if (astType instanceof MacroType) {
            return getCAstTypeFor(((MacroType) astType).definedType());
        }
        if (astType instanceof X10ClassType && ((X10ClassType) astType).typeArguments().size() > 0) {
            X10ClassType classType = (X10ClassType) astType;
            Type baseType = classType.def().asType();
            List<Type> typeArgs = classType.typeArguments();
            PolyglotJavaParametricType result = ((X10toCAstTranslator) fTranslator).new PolyglotJavaParametricType((ClassType) baseType, typeArgs, this, fTypeSystem);

            return result;
        }
        if (astType instanceof PrimitiveType) {
            PrimitiveType primitiveType = (PrimitiveType) astType;
            String javaPrimitiveName = X10PolyglotIdentityMapper.getJavaPrimitiveTypeFor(primitiveType.fullName().toString());

            return JavaPrimitiveTypeMap.lookupType(javaPrimitiveName);
        }
        if (astType instanceof ParameterType) {
            final ParameterType parameterType = (ParameterType) astType;
            return new CAstType() {
                private Collection<CAstType> justObject = new HashSet<CAstType>();
//                {
//                    justObject.add()
//                }
                public String getName() {
                    return parameterType.name().toString();
                }

                public Collection getSupertypes() {
                    return justObject;
                } };
        }
/*      if (astType instanceof FutureType) {
            FutureType futureType = (FutureType) astType;
            Type baseType = futureType.base();
            try {
                ClassType x10_lang_future = (ClassType) fTypeSystem.typeForName(QName.make("x10.lang.Future"));
                List<Type> typeArgs = Collections.singletonList(baseType);
                PolyglotJavaParametricType result = ((X10toCAstTranslator) fTranslator).new PolyglotJavaParametricType(
                        x10_lang_future, typeArgs, this, fTypeSystem);

                return result;
            } catch (SemanticException e) {
                Assertions.UNREACHABLE("Couldn't find x10.lang.Future?");
                return null;
            }
        } else if (astType instanceof NullableType) {
            NullableType nullableType = (NullableType) astType;
            Type baseType = nullableType.base();
            ClassType x10_lang_nullable = ((X10TypeSystem_c) fTypeSystem).nullable();
            List<Type> typeArgs = Collections.singletonList(baseType);
            PolyglotJavaParametricType result = ((X10toCAstTranslator) fTranslator).new PolyglotJavaParametricType(
                    x10_lang_nullable, typeArgs, this, fTypeSystem);

            return result;
        }
*/
        return super.getCAstTypeFor(astType);
    }
}
