package com.ibm.domo.ast.x10.types;

import com.ibm.domo.types.ClassLoaderReference;
import com.ibm.domo.types.TypeName;
import com.ibm.domo.types.TypeReference;

public class X10TypeReference {
    private final static TypeName x10LangPointName = TypeName.string2TypeName("Lx10/lang/point");
    public final static TypeReference x10LangPoint =
      TypeReference.findOrCreate(ClassLoaderReference.Primordial, x10LangPointName);
}
