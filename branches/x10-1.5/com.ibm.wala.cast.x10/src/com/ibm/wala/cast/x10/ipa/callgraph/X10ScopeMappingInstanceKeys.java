package com.ibm.wala.cast.x10.ipa.callgraph;

import com.ibm.wala.cast.java.ipa.callgraph.JavaScopeMappingInstanceKeys;
import com.ibm.wala.cast.x10.translator.polyglot.X10AsyncObject;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKeyFactory;
import com.ibm.wala.ipa.callgraph.propagation.PropagationCallGraphBuilder;
import com.ibm.wala.ipa.cha.IClassHierarchy;

public class X10ScopeMappingInstanceKeys extends JavaScopeMappingInstanceKeys {

  public X10ScopeMappingInstanceKeys(IClassHierarchy cha, PropagationCallGraphBuilder builder, InstanceKeyFactory basic) {
    super(cha, builder, basic);
  }

  @Override
  protected boolean isPossiblyLexicalClass(IClass cls) {
    if (cls instanceof X10AsyncObject) {
      return true;
    }
    return super.isPossiblyLexicalClass(cls);
  }
}
