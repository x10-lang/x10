/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10doc.doc;

import java.util.ArrayList;
import java.util.List;

import polyglot.types.LocalDef;
import x10.types.ParameterType;
import x10.types.TypeDef;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ThrowsTag;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;

public class X10TypeDefDoc extends X10Doc implements MethodDoc {
    TypeDef typeDef;
    X10ClassDoc containingClass;
    X10TypeVariable[] typeParams;
    X10RootDoc rootDoc;
    Type returnType;
    ArrayList<X10Parameter> parameters;

    // String descriptor;

    public X10TypeDefDoc(TypeDef typeDef, X10ClassDoc containingClass, String comments) {
        // super(comments);
        this.typeDef = typeDef;
        this.containingClass = containingClass;
        this.rootDoc = X10RootDoc.getRootDoc();

        initTypeParameters();

        // initialize returnType
        returnType = rootDoc.getType(typeDef.returnType().get(), typeParams);

        // initialize parameters
        List<LocalDef> formals = typeDef.formalNames();
        int n = ((formals == null) ? 0 : formals.size());
        parameters = new ArrayList<X10Parameter>(n);
        for (LocalDef ld : formals) {
            String paramName = ld.name().toString();
            polyglot.types.Type paramType = ld.type().get();
            parameters.add(new X10Parameter(paramName, rootDoc.getType(paramType, typeParams)));
        }
        super.processComment(comments);
    }

    void initTypeParameters() {
        List<ParameterType> params = typeDef.typeParameters();
        typeParams = new X10TypeVariable[params.size()];
        int i = 0;
        for (ParameterType p : params) {
            X10TypeVariable v = new X10TypeVariable(p, this);
            typeParams[i++] = v;
        }
    }

    @Override
    public boolean isIncluded() {
        return true;
    }

    @Override
    public boolean isMethod() {
        return true;
    }

    @Override
    public String name() {
        return typeDef.name().toString();
    }

    public boolean isAbstract() {
        return typeDef.flags().isAbstract();
    }

    /**
     * @since 1.8
     * @return
     */
    public boolean isDefault() {
        // TODO Auto-generated method stub
        return false;
    }

    public ClassDoc overriddenClass() {
        return null;
    }

    public MethodDoc overriddenMethod() {
        return null;
    }

    public Type overriddenType() {
        return null;
    }

    public boolean overrides(MethodDoc arg0) {
        return false;
    }

    public Type returnType() {
        return returnType;
    }

    public String flatSignature() {
        return signature();
    }

    public boolean isNative() {
        return typeDef.flags().isNative();
    }

    public boolean isSynchronized() {
        return false;
    }

    public boolean isVarArgs() {
        return false;
    }

    public void addDeclTag(String declString) {
        if (declString == null) {
            return;
        }
        X10Tag[] declTags = createInlineTags(declString, this).toArray(new X10Tag[0]);

        // place declaration before the first sentence of the existing comment
        // so that
        // the declaration is displayed in the "Methods Summary" table before
        // the first sentence
        firstSentenceTags = X10Doc.concat(declTags, firstSentenceTags);
        inlineTags = concat(declTags, inlineTags);
    }

    public String declString() {
        // display that this is a type definition in associated comments
        String result = "<B>Type definition</B>: <TT>type " + typeDef.signature() + " = " + typeDef.returnType()
                + ".</TT><PRE>\n</PRE>";
        // String result = "<PRE>\n</PRE><B>Type definition</B>: " + descriptor;
        // // if typeDef is used, param names not shown
        return result;
    }

    public ParamTag[] paramTags() {
        // TODO Auto-generated method stub
        return new ParamTag[0];
    }

    public Parameter[] parameters() {
        return parameters.toArray(new Parameter[0]);
    }

    /**
     * @since 1.8
     * @return
     */
    public Type receiverType() {
        // TODO Auto-generated method stub
        return null;
    }

    public String signature() {
        String sig = typeDef.signature();
        // return sig.substring(sig.indexOf('('));
        return sig;
    }

    public Type[] thrownExceptionTypes() {
        // TODO Auto-generated method stub
        return new Type[0];
    }

    public ClassDoc[] thrownExceptions() {
        // TODO Auto-generated method stub
        return new ClassDoc[0];
    }

    public ThrowsTag[] throwsTags() {
        // TODO Auto-generated method stub
        return new ThrowsTag[0];
    }

    public ParamTag[] typeParamTags() {
        // TODO Auto-generated method stub
        return new ParamTag[0];
    }

    public TypeVariable[] typeParameters() {
        return typeParams;
    }

    public boolean isSynthetic() {
        return false;
    }

    public AnnotationDesc[] annotations() {
        // TODO Auto-generated method stub
        return new AnnotationDesc[0];
    }

    public ClassDoc containingClass() {
        return containingClass;
    }

    public PackageDoc containingPackage() {
        return containingClass.containingPackage();
    }

    public boolean isFinal() {
        return typeDef.flags().isFinal();
    }

    public boolean isPackagePrivate() {
        return typeDef.flags().isPackage();
    }

    public boolean isPrivate() {
        return typeDef.flags().isPrivate();
    }

    public boolean isProtected() {
        return typeDef.flags().isProtected();
    }

    public boolean isPublic() {
        return typeDef.flags().isPublic();
    }

    public boolean isStatic() {
        return typeDef.flags().isStatic();
    }

    public int modifierSpecifier() {
        return X10Doc.flagsToModifierSpecifier(typeDef.flags().flags());
    }

    public String modifiers() {
        return typeDef.flags().toString() + " type";
    }

    public String qualifiedName() {
        return typeDef.name().toString();
    }

    // void archived() {
    // String descriptor = typeDef.asType().toString() +
    // ((typeParams.length == 0) ? "" : Arrays.toString(typeParams));
    // List<LocalDef> formals = typeDef.formalNames();
    // int n = ((formals == null) ? 0 : formals.size());
    // parameters = new ArrayList<X10Parameter>(n);
    // if (n > 0) {
    // // TODO: "()" is not added to descriptor if there are no formals in the
    // type defn; this is correct for
    // // x10.lang._.void, but not for type defn "Foo() = Foo{x=0}";
    // typeDef.signature could be used to determine
    // // if the defn contains "()"; note that typeDef.signature contains only
    // param types not param names, hence
    // // the need to construct the descriptor as done below
    // descriptor += "(";
    // boolean first = true;
    // for (LocalDef ld: formals) {
    // String paramName = ld.name().toString();
    // polyglot.types.Type paramType = ld.type().get();
    // parameters.add(new X10Parameter(paramName, rootDoc.getType(paramType,
    // typeParams)));
    //
    // if (first) {
    // first = false;
    // descriptor += paramType.toString() + " " + paramName;
    // }
    // else {
    // descriptor += ", " + paramType.toString() + " " + paramName;
    // }
    // }
    // descriptor += ")";
    // }
    // descriptor += " = " + typeDef.returnType().toString();
    // this.descriptor = descriptor;
    // }
}
