package com.ibm.domo.ast.x10.translator.polyglot;

import polyglot.ext.x10.types.*;
import polyglot.types.*;

import com.ibm.wala.cast.java.translator.polyglot.*;
import com.ibm.wala.types.*;

import java.util.*;

class X10PolyglotIdentityMapper extends PolyglotIdentityMapper {

  public X10PolyglotIdentityMapper(ClassLoaderReference clr, TypeSystem ts) {
    super(clr, ts);
  }

  public String typeToTypeID(Type type) {
    if (type instanceof NullableType) {
      return typeToTypeID(((NullableType)type).base());
    } else {
      return super.typeToTypeID(type);
    }
  }

}
