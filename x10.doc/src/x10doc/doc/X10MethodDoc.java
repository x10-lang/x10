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

import polyglot.types.Flags;
import polyglot.types.LocalDef;
import x10.types.ParameterType;
import x10.types.X10MethodDef;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.Tag;
import com.sun.javadoc.ThrowsTag;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;

public class X10MethodDoc extends X10Doc implements MethodDoc {
    private X10MethodDef methodDef;
    private Flags flags;
    private X10ClassDoc containingClass;
    private X10TypeVariable[] typeParams;
    private X10RootDoc rootDoc;
    private Type returnType;
    private ArrayList<X10Parameter> parameters;
    private boolean included;

    public X10MethodDoc() {
        super.processComment("");
    }

    public X10MethodDoc(X10MethodDef methodDef, X10ClassDoc containingClass, String comment) {
        this.methodDef = methodDef;
        this.containingClass = containingClass;
        flags = methodDef.flags();
        if (containingClass.flags.isStruct()) flags = flags.clearFinal();       // remove redundancy
        if (containingClass.flags.isInterface()) flags = flags.clearAbstract(); // remove redundancy
        this.rootDoc = X10RootDoc.getRootDoc();

        // the following lines simply retrieve the formal parameter types from
        // methodDef, to ensure that methodDef.signature() does not contain
        // <unknown> in place
        // of formal parameter types; this is a hack, and ideally there should
        // be no need to do it;
        // X10ClassDoc.addMethod(...) uses X10MethodDef.signature() as the key
        // against which X10MethodDoc
        // objects are mapped
        // List<Ref<? extends polyglot.types.Type>> ls =
        // methodDef.formalTypes();
        // for (Ref<? extends polyglot.types.Type> ref: ls) {
        // polyglot.types.Type formalType = ref.get();
        // }

        // type parameters should be initialized before return types and
        // parameter types
        // because the latter may use the former; further, getType requires
        // access to this
        // X10MethodDoc object to find the type parameters, hence the need to
        // add this object to
        // the containing class's ClassDoc object
        initTypeParameters();
        // containingClass.addMethod(this);

        // initialize returnType
        returnType = rootDoc.getType(methodDef.returnType().get(), typeParams);

        // initialize parameters
        List<LocalDef> formals = methodDef.formalNames();
        int n = ((formals == null) ? 0 : formals.size());
        parameters = new ArrayList<X10Parameter>(n);
        for (LocalDef ld : formals) {
            String paramName = ld.name().toString();
            polyglot.types.Type paramType = ld.type().get();
            parameters.add(new X10Parameter(paramName, rootDoc.getType(paramType, typeParams)));
        }

        // X10Doc.isIncluded(..., this) valid only if
        // this.{isPublic(),...,isPrivate()} are valid, which requires
        // this.methodDef to have been set appropriately
        this.included = X10Doc.isIncluded(rootDoc.accessModFilter(), this);
        super.processComment(comment);
    }
    
    void initTypeParameters() {
        List<ParameterType> params = methodDef.typeParameters();
        typeParams = new X10TypeVariable[params.size()];
        int i = 0;
        for (ParameterType p : params) {
            X10TypeVariable v = new X10TypeVariable(p, this);
            typeParams[i++] = v;
            // typeParams.put(typeParameterKey(p), v);
        }    }

    public int compareTo(Object other) {
        if (other instanceof X10MethodDoc) {
            X10MethodDoc omd = (X10MethodDoc)other;
            int nc = name().compareTo(omd.name());
            if (nc != 0) return nc;
            int myNumFormals = methodDef.formalTypes().size();
            int otherNumFormals = omd.methodDef.formalTypes().size();
            if (myNumFormals < otherNumFormals) return -1;
            if (myNumFormals > otherNumFormals) return 1;
            return signature().compareTo(omd.signature());
        } else {
            return super.compareTo(other);
        }
    }
    
    public static String typeParameterKey(ParameterType p) {
        return p.name().toString();
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
        // so that the declaration is displayed in the "Methods Summary" table 
        // before the first sentence
        firstSentenceTags = concat(declTags, firstSentenceTags);
        inlineTags = concat(concat(declTags, tags), inlineTags);
    }

    public String declString() {
        // the X10 method declaration needs to be displayed in the method's
        // comments only if a param type
        // or return type is X10-specific (has associated closures, constraints)
        // or the method has contraints
        boolean hasTrivialMethodGuard = methodDef.guard() == null || methodDef.guard().get().atoms().isEmpty();
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

        String guard = hasTrivialMethodGuard ? "" : methodDef.guard().toString();
        // construct result from X10 compiler method signatures and toString functions
        String result = "<B>Declaration:</B> <TT>" + methodDef.signature() + guard + ":"
                + methodDef.returnType().toString() + ".</TT><PRE>\n</PRE>";
        return result;
    }

    public boolean isAbstract() {
        if (X10RootDoc.printSwitch) System.out.println(name() + ".isAbstract() called.");
        return flags.isAbstract();
    }

    /**
     * @since 1.8
     * @return
     */
    public boolean isDefault() {
        // TODO Auto-generated method stub
        if (X10RootDoc.printSwitch) System.out.println(name() + ".isDefault() called.");
        return false;
    }

    @Override
    public boolean isIncluded() {
        if (X10RootDoc.printSwitch) System.out.println(name() + ".isIncluded() called.");
        return this.included;
    }

    @Override
    public boolean isMethod() {
        if (X10RootDoc.printSwitch) System.out.println(name() + ".isMethod() called.");
        return true;
    }

    @Override
    public String name() {
        if (X10RootDoc.printSwitch) System.out.println(methodDef.name() + ".name() called.");
        return methodDef.name().toString();
    }

    public ClassDoc overriddenClass() {
        // TODO Auto-generated method stub
        System.out.println(name() + ".overriddenClass() called.");
        return null;
    }

    public MethodDoc overriddenMethod() {
        // TODO Auto-generated method stub
        // System.out.println(name() + ".overriddenMethod() called.");
        return null;
    }

    public Type overriddenType() {
        // TODO Auto-generated method stub
        System.out.println(name() + ".overriddenType() called.");
        return null;
    }

    public boolean overrides(MethodDoc arg0) {
        // TODO Auto-generated method stub
        // System.out.println(name() + ".overrides(" + arg0.name() +
        // ") called.");
        return false;
    }

    public Type returnType() {
        return returnType;
        // System.out.print(name() + ".returnType() called. ");
        // if (retType == null) return null;
        // if (retType.isPrimitive()) {
        // System.out.println("Primitive X10Type returned.");
        // return new X10Type(retType);
        // }
        // if (retType.isClass()) {
        // System.out.println("X10ClassDoc returned.");
        //
        // X10ClassDef classDef = (X10ClassDef) retType.toClass().def();
        // return createRecClassDoc(classDef);
        // }
        // System.out.println("null returned.");
        // return null;
    }

    public String flatSignature() {
        return signature();
    }

    public boolean isNative() {
        System.out.println(name() + ".isNative() called.");
        return flags.isNative();
    }

    public boolean isSynchronized() {
        System.out.println(name() + ".isSynchronized() called.");
        return false;
    }

    public boolean isVarArgs() {
        // System.out.println(name() + ".isVarArgs() called.");
        return false; // no var. args. methods in current implementation of X10
    }

    public ParamTag[] paramTags() {
        // TODO Auto-generated method stub
        // System.out.println(name() + ".paramTags() called.");
        return paramTags.toArray(new ParamTag[0]);
    }

    public Parameter[] parameters() {
        // System.out.println(name() + ".parameters() called.");
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
        // System.out.println(name() + ".signature() called. result = " +
        // signature(methodDef));
        return signature(methodDef);
        // String sig = methodDef.signature();
        // return sig.substring(sig.indexOf('('));
    }

    public static String signature(X10MethodDef md) {
        String sig = md.signature();
        sig = sig.replaceAll("[^(:,]+:", "");
        sig = sig.replaceAll("\\{[^}]+\\}", "");

        return sig.substring(sig.indexOf('('));
    }

    public Type[] thrownExceptionTypes() {
        if (X10RootDoc.printSwitch) System.out.println(name() + ".thrownExceptionTypes() called.");
        return thrownExceptions();
    }

    public ClassDoc[] thrownExceptions() {
        if (X10RootDoc.printSwitch) System.out.println(name() + ".thrownExceptions() called.");

        // TODO: look at the @Throws annotation when we have one
        // List<Ref<? extends polyglot.types.Type>> throwTypes =
        // methodDef.throwTypes();
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
        if (X10RootDoc.printSwitch) System.out.println(name() + ".throwsTags() called.");
        Tag[] tags = tags(X10Tag.THROWS);
        ThrowsTag[] newTags = new ThrowsTag[tags.length];
        System.arraycopy(tags, 0, newTags, 0, tags.length);
        return newTags;
    }

    public ParamTag[] typeParamTags() {
        // TODO Auto-generated method stub
        if (X10RootDoc.printSwitch) System.out.println(name() + ".typeParamTags() called.");
        return new ParamTag[0];
    }

    public TypeVariable[] typeParameters() {
        if (X10RootDoc.printSwitch) System.out.println(name() + ".typeParameters() called.");
        // return typeParams.values().toArray(new TypeVariable[0]);
        return typeParams;
    }

    public TypeVariable getTypeVariable(ParameterType p) {
        // return typeParams.get(typeParameterKey(p));
        return null;
    }

    public boolean isSynthetic() {
        // TODO Auto-generated method stub
        if (X10RootDoc.printSwitch) System.out.println(name() + ".isSynthetic() called.");
        return false;
    }

    public AnnotationDesc[] annotations() {
        // TODO Auto-generated method stub
        if (X10RootDoc.printSwitch) System.out.println(name() + ".annotations() called.");
        return new AnnotationDesc[0];
    }

    public ClassDoc containingClass() {
        if (X10RootDoc.printSwitch) System.out.println(name() + ".containingClass() called.");
        return containingClass;
    }

    public PackageDoc containingPackage() {
        if (X10RootDoc.printSwitch) System.out.println(name() + ".containingPackage() called.");
        return containingClass.containingPackage();
    }

    public boolean isFinal() {
        if (X10RootDoc.printSwitch) System.out.println(name() + ".isFinal() called.");
        return flags.isFinal();
    }

    public boolean isPackagePrivate() {
        if (X10RootDoc.printSwitch) System.out.println(name() + ".isPackagePrivate() called.");
        return flags.isPackage();
    }

    public boolean isPrivate() {
        if (X10RootDoc.printSwitch) System.out.println(name() + ".isPrivate() called.");
        return flags.isPrivate();
    }

    public boolean isProtected() {
        if (X10RootDoc.printSwitch) System.out.println(name() + ".isProtected() called.");
        return flags.isProtected();
    }

    public boolean isPublic() {
        if (X10RootDoc.printSwitch) System.out.println(name() + ".isPublic() called.");
        return flags.isPublic();
    }

    public boolean isStatic() {
        if (X10RootDoc.printSwitch) System.out.println(name() + ".isStatic() called.");
        return flags.isStatic();
    }

    public int modifierSpecifier() {
        if (X10RootDoc.printSwitch) System.out.println(name() + ".modifierSpecifier() called.");
//        int r = 0;
//        for (String flag : flags.flags()) {
//            // flag could be "property" which is not in flagsToHex (and not recognized by the standard doclet)
//            if (flagsToHex.containsKey(flag)) {
//                r |= flagsToHex.get(flag);
//            }
//        }
//        return r;
        return X10Doc.flagsToModifierSpecifier(flags.flags());
    }

    public String modifiers() {
        if (X10RootDoc.printSwitch) System.out.println(name() + ".modifiers() called.");
        return flags.toString();
    }

    public String qualifiedName() {
        if (X10RootDoc.printSwitch) System.out.println(name() + ".qualifiedName() called.");
        return methodDef.name().toString();
    }
}
