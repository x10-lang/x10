package com.ibm.wala.cast.x10.tests.ir;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.Descriptor;
import com.ibm.wala.types.Selector;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.strings.Atom;


abstract class AbstractIRChecker {
  
  protected final IMethod getMethod(final String method, final IClassHierarchy cha) {
    final String[] tokens = method.split("\\#");
    
    final Atom loaderName = Atom.findOrCreateUnicodeAtom(tokens[0]);
    IClassLoader clLoader = null;
    for (final IClassLoader classLoader : cha.getLoaders()) {
      if (classLoader.getName() == loaderName) {
        clLoader = classLoader;
        break;
      }
    }
    if (clLoader == null) {
      throw new AssertionError(String.format("Could not find class loader named %s", tokens[0]));
    }
    final TypeReference typeRef = TypeReference.findOrCreate(clLoader.getReference(), 'L' + tokens[1]);
    final IClass clazz = cha.lookupClass(typeRef);
    if (clazz == null) {
      throw new AssertionError(String.format("Could not find class for %s", typeRef)); 
    }
    final Descriptor descriptor = Descriptor.findOrCreateUTF8(clLoader.getLanguage(), tokens[3]);
    final Selector selector = new Selector(Atom.findOrCreateAsciiAtom(tokens[2]), descriptor);
    final IMethod walaMethod = clazz.getMethod(selector);
    if (walaMethod == null) {
      final StringBuilder sb = new StringBuilder();
      for (final IMethod declaredMethod : clazz.getDeclaredMethods()) {
        sb.append(declaredMethod).append('\n');
      }
      throw new AssertionError(String.format("Could not find method '%s' in type '%s'.\nAvailable methods are:\n%s", selector,
                                             clazz, sb.toString()));
    }
    return walaMethod;
  }

}
