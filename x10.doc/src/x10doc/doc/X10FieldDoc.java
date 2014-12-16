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

import x10.types.X10FieldDef;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.SerialFieldTag;
import com.sun.javadoc.Type;

public class X10FieldDoc extends X10Doc implements FieldDoc {
    X10FieldDef fieldDef;
    Type type;
    X10ClassDoc containingClass;
    X10RootDoc rootDoc;
    boolean included;

    public X10FieldDoc(X10FieldDef fd, X10ClassDoc containingClass, String comment) {
        // super(comment);
        this.fieldDef = fd;
        this.containingClass = containingClass;
        this.rootDoc = X10RootDoc.getRootDoc();
        this.type = rootDoc.getType(fieldDef.type().get());

        // X10Doc.isIncluded(..., this) valid only if
        // this.{isPublic(),...,isPrivate()} are valid, which requires
        // this.fieldDef to have been set appropriately
        this.included = X10Doc.isIncluded(this.rootDoc.accessModFilter(), this);
        super.processComment(comment);
    }

    public void addDeclTag(String declString) {
        if (declString == null) {
            return;
        }
        X10Tag[] declTags = createInlineTags(declString, this).toArray(new X10Tag[0]);

        // place declaration before the first sentence of the existing comment
        // so that
        // the declaration is displayed in the "Fields Summary" table before the
        // first sentence
        firstSentenceTags = X10Doc.concat(declTags, firstSentenceTags);
        inlineTags = concat(declTags, inlineTags);
    }

    public String declString() {
        // the X10 field declaration needs to be displayed in the field's
        // comments only if the field type
        // is X10-specific, or if the field has associated constraints
        // TODO: look for constraints, include constraints in declaration string
        if (X10Type.isX10Specific(type)) {
            String result = "<B>Field Type</B>: <TT>" + fieldDef.type().get().toString() + "</TT><PRE>\n</PRE>";
            return result;
        }
        return "";
    }

    @Override
    public String name() {
        return fieldDef.name().toString();
    }

    public Object constantValue() {
        return fieldDef.constantValue();
    }

    public String constantValueExpression() {
        return "";
    }

    @Override
    public boolean isField() {
        return true;
    }

    @Override
    public boolean isIncluded() {
        // return true;
        return included;
    }

    public boolean isTransient() {
        return fieldDef.flags().isTransient();
    }

    public boolean isVolatile() {
        return false;
    }

    public SerialFieldTag[] serialFieldTags() {
        return new SerialFieldTag[0];
    }

    public Type type() {
        // System.out.println("FieldDoc(" + name() + ").type() called.");
        // return new X10Type(fieldDef.type().get());
        return type;
    }

    public boolean isSynthetic() {
        // TODO Auto-generated method stub
        return false;
    }

    public AnnotationDesc[] annotations() {
        return new AnnotationDesc[0];
    }

    public ClassDoc containingClass() {
        return containingClass;
    }

    public PackageDoc containingPackage() {
        return containingClass.containingPackage();
    }

    public boolean isFinal() {
        return fieldDef.flags().isFinal();
    }

    public boolean isPackagePrivate() {
        return fieldDef.flags().isPackage();
    }

    public boolean isPrivate() {
        return fieldDef.flags().isPrivate();
    }

    public boolean isProtected() {
        return fieldDef.flags().isProtected();
    }

    public boolean isPublic() {
        return fieldDef.flags().isPublic();
    }

    public boolean isStatic() {
        return fieldDef.flags().isStatic();
    }

    public int modifierSpecifier() {
        return X10Doc.flagsToModifierSpecifier(fieldDef.flags().flags());
    }

    public String modifiers() {
        return fieldDef.flags().toString();
    }

    public String qualifiedName() {
        String str = fieldDef.type().toString();
        // System.out.println("FieldDoc.qualifiedName() called. fieldDef.type().toString() = "
        // + str);
        return str;
    }
}
