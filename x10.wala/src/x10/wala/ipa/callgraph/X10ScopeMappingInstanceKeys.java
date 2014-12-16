package x10.wala.ipa.callgraph;

import x10.wala.loader.X10AsyncObject;
import x10.wala.loader.X10ClosureObject;

import com.ibm.wala.cast.java.ipa.callgraph.JavaScopeMappingInstanceKeys;
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
    if (cls instanceof X10AsyncObject || cls instanceof X10ClosureObject ) {
      return true;
    }
    return super.isPossiblyLexicalClass(cls);
  }
}
