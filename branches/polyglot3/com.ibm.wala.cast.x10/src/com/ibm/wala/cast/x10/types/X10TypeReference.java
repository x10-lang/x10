package com.ibm.wala.cast.x10.types;

import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.types.TypeReference;

public class X10TypeReference {
    private final static TypeName x10LangPointName = TypeName.string2TypeName("Lx10/lang/point");
    public final static TypeReference x10LangPoint =
      TypeReference.findOrCreate(ClassLoaderReference.Primordial, x10LangPointName);

    private final static TypeName x10LangPlaceName = TypeName.string2TypeName("Lx10/lang/place");
    public final static TypeReference x10LangPlace =
	      TypeReference.findOrCreate(ClassLoaderReference.Primordial, x10LangPlaceName);
}
