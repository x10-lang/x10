/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10doc.doc;

import java.util.ArrayList;
import java.util.List;

import polyglot.types.LocalDef;
import x10.types.ParameterType;
import x10.types.X10ConstructorDef;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.Tag;
import com.sun.javadoc.ThrowsTag;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;

public class X10ConstructorDoc extends X10Doc implements ConstructorDoc {
    private X10ConstructorDef constrDef;
    private X10ClassDoc containingClass;
    private X10RootDoc rootDoc;
    private ArrayList<X10Parameter> parameters;
    private Type returnType;
    private X10TypeVariable[] typeParams;
    private boolean included;

    public X10ConstructorDoc() {
        super();
        processComment("");
    }

    public X10ConstructorDoc(X10ConstructorDef constrDef, X10ClassDoc containingClass, String comment) {
        // super(comment);
        this.constrDef = constrDef;
        this.containingClass = containingClass;
        this.rootDoc = X10RootDoc.getRootDoc();

        // the following ensure that methodDef.signature() does not contain
        // <unknown> in place
        // of formal parameter types; see constructor X10MethodDoc(...) for more
        // details
        // List<Ref<? extends polyglot.types.Type>> ls =
        // constrDef.formalTypes();
        // for (Ref<? extends polyglot.types.Type> ref: ls) {
        // polyglot.types.Type formalType = ref.get();
        // }

        // type parameters should be initialized before parameter types because
        // the latter may use the former
        initTypeParameters();
        // containingClass.addConstructor(this);

        // initialize returnType
        returnType = rootDoc.getType(constrDef.returnType().get(), typeParams);

        // initialize parameters
        List<LocalDef> formals = constrDef.formalNames();
        int n = ((formals == null) ? 0 : formals.size());
        parameters = new ArrayList<X10Parameter>(n);
        for (LocalDef ld : formals) {
            String paramName = ld.name().toString();
            polyglot.types.Type paramType = ld.type().get();
            parameters.add(new X10Parameter(paramName, rootDoc.getType(paramType, typeParams)));
        }

        // X10Doc.isIncluded(..., this) valid only if
        // this.{isPublic(),...,isPrivate()} are valid, which requires
        // this.constrDef to have been set appropriately
        this.included = X10Doc.isIncluded(rootDoc.accessModFilter(), this);
        super.processComment(comment);
    }

    void initTypeParameters() {
        // [IP] Constructors don't have type parameters, they inherit them from
        // the container.
        // List<ParameterType> params = constrDef.typeParameters();
        // typeParams = new X10TypeVariable[params.size()];
        // int i = 0;
        // for (ParameterType p: params) {
        // X10TypeVariable v = new X10TypeVariable(p, this);
        // // typeParams.put(typeParameterKey(p), v);
        // typeParams[i++] = v;
        // }
        typeParams = new X10TypeVariable[0];
    }

    public X10ConstructorDef getConstructorDef() {
        return constrDef;
    }

    public X10Tag[] getX10Tags() {
        List<X10Tag> list = new ArrayList<X10Tag>();
        addGuardTags(list);
        return list.toArray(new X10Tag[list.size()]);
    }

    public void addDeclTag(String declString) {
        if (declString == null) {
            return;
        }
        X10Tag[] declTags = createInlineTags(declString, this).toArray(new X10Tag[0]);
        X10Tag[] tags = getX10Tags();

        // place declaration before the first sentence of the existing comment
        // so that
        // the declaration is displayed in the "Methods Summary" table before
        // the first sentence
        firstSentenceTags = concat(declTags, firstSentenceTags);
        inlineTags = concat(concat(declTags, tags), inlineTags);
    }

    public String declString() {
        // the X10 constructor declaration needs to be displayed in the
        // constructors's comments only if a param type,
        // return type or the constructor is X10-specific

        boolean hasTrivialMethodGuard = constrDef.guard() == null || constrDef.guard().get().atoms().isEmpty();
        if (!(X10Type.isX10Specific(returnType)) && hasTrivialMethodGuard) {
            boolean hasConstraints = false;
            for (X10Parameter p : parameters) {
                if (p.isX10Specific()) {
                    hasConstraints = true;
                    break;
                }
            }
            if (!hasConstraints) {
                return "";
            }
        }
        String guard = hasTrivialMethodGuard ? "" : constrDef.guard().get().toString();
        String result = "<B>Declaration</B>: <TT>" + constrDef.signature() + guard + ":"
                + constrDef.returnType().toString() + ".</TT><PRE>\n</PRE>";
        return result;
    }

    @Override
    public boolean isConstructor() {
        return true;
    }

    @Override
    public boolean isIncluded() {
        return included;
    }

    @Override
    public String name() {
        return containingClass.name();
    }

    public String flatSignature() {
        return signature();
    }

    public boolean isNative() {
        return constrDef.flags().isNative();
    }

    public boolean isSynchronized() {
        return false;
    }

    public boolean isVarArgs() {
        return false; // no variable args. constructors in current X10
                      // implementation
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
        String sig = constrDef.signature();
        return sig.substring(sig.indexOf('('));
    }

    public Type[] thrownExceptionTypes() {
        return thrownExceptions();
    }

    public ClassDoc[] thrownExceptions() {
        // TODO: look at the @Throws annotation when we have one
        // List<Ref<? extends polyglot.types.Type>> throwTypes =
        // constrDef.throwTypes();
        // if(throwTypes != null && throwTypes.size() > 0)
        // {
        // ClassDoc[] types = new ClassDoc[throwTypes.size()];
        // int i = 0;
        // for(Ref<? extends polyglot.types.Type> type : throwTypes)
        // {
        // types[i++] = (ClassDoc)rootDoc.getType(type.get());
        // }
        //
        // return types;
        // }

        return new ClassDoc[0];
    }

    public ThrowsTag[] throwsTags() {
        Tag[] tags = tags(X10Tag.THROWS);
        ThrowsTag[] newTags = new ThrowsTag[tags.length];
        System.arraycopy(tags, 0, newTags, 0, tags.length);
        return newTags;
    }

    public ParamTag[] typeParamTags() {
        // TODO Auto-generated method stub
        return new ParamTag[0];
    }

    public TypeVariable[] typeParameters() {
        // return typeParams.values().toArray(new TypeVariable[0]);
        return typeParams;
    }

    public TypeVariable getTypeVariable(ParameterType p) {
        // return typeParams.get(typeParameterKey(p));
        return null;
    }

    public boolean isSynthetic() {
        // TODO Auto-generated method stub
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
        return constrDef.flags().isFinal();
    }

    public boolean isPackagePrivate() {
        return constrDef.flags().isPackage();
    }

    public boolean isPrivate() {
        return constrDef.flags().isPrivate();
    }

    public boolean isProtected() {
        return constrDef.flags().isProtected();
    }

    public boolean isPublic() {
        return constrDef.flags().isPublic();
    }

    public boolean isStatic() {
        return constrDef.flags().isStatic();
    }

    public int modifierSpecifier() {
        return X10Doc.flagsToModifierSpecifier(constrDef.flags().flags());
    }

    public String modifiers() {
        return constrDef.flags().toString();
    }

    public String qualifiedName() {
        return containingClass.qualifiedName();
    }

}
