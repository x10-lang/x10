/*
 * Created on Nov 3, 2005
 */
package com.ibm.domo.ast.x10.translator.polyglot;

import java.util.Collection;
import java.util.List;

import polyglot.ext.x10.types.FutureType;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;

import com.ibm.capa.ast.CAstType;
import com.ibm.capa.impl.debug.Assertions;
import com.ibm.domo.ast.java.translator.polyglot.PolyglotJava2CAstTranslator;
import com.ibm.domo.ast.java.translator.polyglot.PolyglotTypeDictionary;

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
	}
        return super.getCAstTypeFor(astType);
    }
}
