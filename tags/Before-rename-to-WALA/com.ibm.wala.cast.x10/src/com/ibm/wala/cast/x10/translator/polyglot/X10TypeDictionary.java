/*
 * Created on Nov 3, 2005
 */
package com.ibm.domo.ast.x10.translator.polyglot;

import java.util.Collection;
import java.util.List;

import polyglot.ext.x10.types.FutureType;
import polyglot.ext.x10.types.NullableType;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;

import com.ibm.wala.cast.java.translator.polyglot.PolyglotJava2CAstTranslator;
import com.ibm.wala.cast.java.translator.polyglot.PolyglotTypeDictionary;
import com.ibm.wala.cast.tree.CAstType;
import com.ibm.wala.util.debug.Assertions;

public class X10TypeDictionary extends PolyglotTypeDictionary {

    public X10TypeDictionary(TypeSystem typeSystem, PolyglotJava2CAstTranslator translator) {
	super(typeSystem, translator);
    }

    public CAstType getCAstTypeFor(Object astType) {
	if (astType instanceof FutureType) {
	    try {
		return getCAstTypeFor(fTypeSystem.typeForName("x10.lang.Future"));
	    } catch (SemanticException e) {
		Assertions.UNREACHABLE("Couldn't find x10.lang.Future?");
		return null;
	    }
	} else if (astType instanceof NullableType) {
	        return getCAstTypeFor(((NullableType) astType).base());
	}
    return super.getCAstTypeFor(astType);
    }
}
