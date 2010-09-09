package com.ibm.wala.cast.x10.loader;

import com.ibm.wala.classLoader.Language;
import com.ibm.wala.classLoader.LanguageImpl;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.strings.Atom;

public class X10Language extends LanguageImpl {
    /**
     * Canonical name for the X10 language
     */
    public final static Atom X10 = Atom.findOrCreateUnicodeAtom("X10");

    public static X10Language X10Lang = new X10Language();

    private X10Language() {
	super(Language.JAVA);
    }

    public Atom getName() {
	return X10;
    }

    public TypeReference getRootType() {
	return null;
    }

    public TypeReference getThrowableType() {
	return TypeReference.JavaLangThrowable;
    }

    public TypeReference getConstantType(Object o) {
	return Language.JAVA.getConstantType(o);
    }

    public boolean isNullType(TypeReference type) {
	return Language.JAVA.isNullType(type);
    }
}
