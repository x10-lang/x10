package com.ibm.domo.ast.x10.ipa.callgraph;

import com.ibm.domo.analysis.reflection.FactoryBypassInterpreter;
import com.ibm.domo.ast.java.ipa.callgraph.JavaScopeMappingInstanceKeys;
import com.ibm.domo.ipa.callgraph.AnalysisOptions;
import com.ibm.domo.ipa.callgraph.ContextSelector;
import com.ibm.domo.ipa.callgraph.ReflectionSpecification;
import com.ibm.domo.ipa.callgraph.impl.DefaultContextSelector;
import com.ibm.domo.ipa.callgraph.impl.DelegatingContextSelector;
import com.ibm.domo.ipa.callgraph.propagation.DefaultPropagationContextSelector;
import com.ibm.domo.ipa.callgraph.propagation.SSAContextInterpreter;
import com.ibm.domo.ipa.callgraph.propagation.cfa.DefaultSSAInterpreter;
import com.ibm.domo.ipa.callgraph.propagation.cfa.DelegatingSSAContextInterpreter;
import com.ibm.domo.ipa.callgraph.propagation.cfa.ZeroXInstanceKeys;
import com.ibm.domo.ipa.cha.ClassHierarchy;
import com.ibm.domo.util.warnings.WarningSet;

public class X10ZeroXCFABuilder extends X10CFABuilder {

    /**
     * @param cha
     * @param warnings
     * @param entrypoints
     * @param bypass
     * @param contextProvider
     */
    public X10ZeroXCFABuilder(ClassHierarchy cha, 
  			   WarningSet warnings,
  			   AnalysisOptions options,
  			   ContextSelector appContextSelector,
  			   SSAContextInterpreter appContextInterpreter, 
  			   ReflectionSpecification reflect, 
  			   int instancePolicy) {
      super(cha, warnings, options);

      SSAContextInterpreter c = new DefaultSSAInterpreter(options, cha, warnings);
      c = new DelegatingSSAContextInterpreter(new FactoryBypassInterpreter(options, cha, reflect, warnings), c);
      SSAContextInterpreter contextInterpreter = new DelegatingSSAContextInterpreter(appContextInterpreter, c);
      setContextInterpreter(contextInterpreter);

      ContextSelector def = new DefaultContextSelector(cha, options.getMethodTargetSelector());
      ContextSelector contextSelector = new DelegatingContextSelector(appContextSelector, def);
      DefaultPropagationContextSelector I = new DefaultPropagationContextSelector(contextSelector, cha);

      setContextSelector(I);

      setInstanceKeys(
        new JavaScopeMappingInstanceKeys(cha, this, 
          new ZeroXInstanceKeys(options, cha, contextInterpreter, warnings, instancePolicy)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.domo.ipa.callgraph.propagation.PropagationCallGraphBuilder#getDefaultDispatchBoundHeuristic()
     */
    protected byte getDefaultDispatchBoundHeuristic() {
      return AnalysisOptions.NO_DISPATCH_BOUND;
    }
}
