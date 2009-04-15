/*
 * Created on Nov 3, 2005
 */
package com.ibm.wala.cast.x10.translator.polyglot;

import java.util.Collections;
import java.util.List;

import polyglot.ext.x10.types.NullableType;
import polyglot.ext.x10.types.ParametrizedType;

import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.types.ClassType;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;

import com.ibm.wala.cast.java.translator.polyglot.PolyglotJava2CAstTranslator;
import com.ibm.wala.cast.java.translator.polyglot.PolyglotTypeDictionary;
import com.ibm.wala.cast.tree.CAstType;
import com.ibm.wala.cast.x10.translator.polyglot.X10toCAstTranslator.PolyglotJavaParametricType;
import com.ibm.wala.util.debug.Assertions;

public class X10TypeDictionary extends PolyglotTypeDictionary {
    public X10TypeDictionary(TypeSystem typeSystem, PolyglotJava2CAstTranslator translator) {
        super(typeSystem, translator);
    }

    public CAstType getCAstTypeFor(Object astType) {
        if (astType instanceof ParametrizedType) {
            Assertions.UNREACHABLE("getCAstTypeFor(ParametrizedType)");
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
