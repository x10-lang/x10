/*
 * Created on Nov 3, 2005
 */
package x10.wala.translator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;


import polyglot.types.ClassType;
import polyglot.types.JavaArrayType;
import polyglot.types.JavaPrimitiveType;
import polyglot.types.ObjectType;

import polyglot.types.Type;
import polyglot.types.TypeSystem;
import x10.types.ConstrainedType;
import x10.types.MacroType;
import x10.types.ParameterType;
import x10.types.VoidType;
import x10.types.X10ClassType;
import x10.wala.translator.X10toCAstTranslator.PolyglotJavaParametricType;
import x10.wala.translator.X10toCAstTranslator.PolyglotJavaType;

import com.ibm.wala.cast.java.types.JavaPrimitiveTypeMap;
import com.ibm.wala.cast.tree.CAstType;
import com.ibm.wala.cast.tree.impl.CAstTypeDictionaryImpl;
import com.ibm.wala.util.debug.Assertions;

public class X10TypeDictionary extends CAstTypeDictionaryImpl {
    private final class PolyglotJavaArrayType implements CAstType.Array {
        private final Type fEltPolyglotType;

        private final CAstType fEltCAstType;

        private PolyglotJavaArrayType(JavaArrayType arrayType) {
          super();
          fEltPolyglotType = arrayType.base();
          fEltCAstType = getCAstTypeFor(fEltPolyglotType);
        }

        public int getNumDimensions() {
          return 1; // always 1 for Java
        }

        public CAstType getElementType() {
          return fEltCAstType;
        }

        public String getName() {
          return "[" + fEltCAstType.getName();
        }

        @SuppressWarnings("unchecked")
        public Collection getSupertypes() {
          if (fEltPolyglotType.isJavaPrimitive())
            return Collections.singleton(getCAstTypeFor(fTypeSystem.Any()));
          Assertions.productionAssertion(fEltPolyglotType.isReference(), "Non-primitive, non-reference array element type!");
          ObjectType baseRefType = (ObjectType) fEltPolyglotType;
          Collection<CAstType> supers = new ArrayList<CAstType>();
          for (Iterator superIter = baseRefType.interfaces().iterator(); superIter.hasNext(); ) {
            supers.add(getCAstTypeFor(superIter.next()));
          }
          if (baseRefType instanceof ClassType) {
              ClassType baseClassType = (ClassType) baseRefType;
            if (baseClassType.superClass() != null)
                  supers.add(getCAstTypeFor(baseRefType.superClass()));
          }
          return supers;
        }
      }

      protected final TypeSystem fTypeSystem;

      protected final X10toCAstTranslator fTranslator;

      public X10TypeDictionary(TypeSystem typeSystem, X10toCAstTranslator translator) {
        fTypeSystem = typeSystem;
        fTranslator = translator;
      }

      public CAstType SUPER_getCAstTypeFor(Object astType) {
        CAstType type = super.getCAstTypeFor(astType);
        // Handle the case where we haven't seen an AST decl for some type before
        // processing a reference. This can certainly happen with classes in byte-
        // code libraries, for which we never see an AST decl.
        // In this case, just create a new type and return that.
        if (type == null) {
          final Type polyglotType = (Type) astType;

          if (polyglotType.isClass())
            type = fTranslator.new PolyglotJavaType((ClassType) astType, this, fTypeSystem);
          else if (polyglotType.isJavaPrimitive()) {
            type = JavaPrimitiveTypeMap.lookupType(((JavaPrimitiveType) polyglotType).name().toString());
          } else if (polyglotType instanceof VoidType) {
              type = JavaPrimitiveTypeMap.VoidType;
          } else if (polyglotType.isArray()) {
            type = new PolyglotJavaArrayType((JavaArrayType) polyglotType);
          } else
            Assertions.UNREACHABLE("getCAstTypeFor() passed type that is not primitive, array, or class?");
          super.map(astType, type);
        }
        return type;
      }

    public CAstType getCAstTypeFor(Object astType) {
        if (astType instanceof ConstrainedType) {
          return getCAstTypeFor(((ConstrainedType) astType).baseType().get());
        }
        if (astType instanceof MacroType) {
            return getCAstTypeFor(((MacroType) astType).definedType());
        }
        if (astType instanceof X10ClassType && ((X10ClassType) astType).typeArguments() != null && ((X10ClassType) astType).typeArguments().size() > 0) {
            X10ClassType classType = (X10ClassType) astType;
            Type baseType = classType.def().asType();
            List<Type> typeArgs = classType.typeArguments();
            PolyglotJavaParametricType result = ((X10toCAstTranslator) fTranslator).new PolyglotJavaParametricType((ClassType) baseType, typeArgs, this, fTypeSystem);
            super.map(astType, result);
            return result;
        }
        if (astType instanceof JavaPrimitiveType) {
            JavaPrimitiveType primitiveType = (JavaPrimitiveType) astType;
            String javaPrimitiveName = X10IdentityMapper.getJavaPrimitiveTypeFor(primitiveType.fullName().toString());
            final CAstType castType = JavaPrimitiveTypeMap.lookupType(javaPrimitiveName);
            super.map(astType, castType);
            return castType;
        }
        if (astType instanceof ParameterType) {
            final ParameterType parameterType = (ParameterType) astType;
            final CAstType castType = new CAstType() {
                private Collection<CAstType> justObject = new HashSet<CAstType>();

                public String getName() {
                    return parameterType.name().toString();
                }

                public Collection getSupertypes() {
                    return justObject;
                }
            };
            super.map(astType, castType);
            return castType;
        }
        return SUPER_getCAstTypeFor(astType);
    }
}
