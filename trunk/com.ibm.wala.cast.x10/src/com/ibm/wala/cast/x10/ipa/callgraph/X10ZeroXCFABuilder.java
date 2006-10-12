package com.ibm.domo.ast.x10.ipa.callgraph;

import com.ibm.domo.ast.java.ipa.callgraph.JavaScopeMappingInstanceKeys;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.ContextSelector;
import com.ibm.wala.ipa.callgraph.ReflectionSpecification;
import com.ibm.wala.ipa.callgraph.impl.DefaultContextSelector;
import com.ibm.wala.ipa.callgraph.impl.DelegatingContextSelector;
import com.ibm.wala.ipa.callgraph.propagation.DefaultPropagationContextSelector;
import com.ibm.wala.ipa.callgraph.propagation.SSAContextInterpreter;
import com.ibm.wala.ipa.callgraph.propagation.cfa.ZeroXInstanceKeys;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.util.warnings.WarningSet;

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

      SSAContextInterpreter contextInterpreter = 
	      makeDefaultContextInterpreters(appContextInterpreter, options, cha, reflect, warnings);
      setContextInterpreter(contextInterpreter);

      ContextSelector def = new DefaultContextSelector(cha, options.getMethodTargetSelector());
      ContextSelector contextSelector =
	appContextSelector == null? 
	def: 
        new DelegatingContextSelector(appContextSelector, def);

      setContextSelector(contextSelector);

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
