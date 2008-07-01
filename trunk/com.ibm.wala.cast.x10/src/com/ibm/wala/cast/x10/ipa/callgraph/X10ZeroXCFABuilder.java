package com.ibm.wala.cast.x10.ipa.callgraph;

import com.ibm.wala.ipa.callgraph.*;
import com.ibm.wala.ipa.callgraph.impl.DefaultContextSelector;
import com.ibm.wala.ipa.callgraph.impl.DelegatingContextSelector;
import com.ibm.wala.ipa.callgraph.propagation.SSAContextInterpreter;
import com.ibm.wala.ipa.callgraph.propagation.cfa.ZeroXInstanceKeys;
import com.ibm.wala.ipa.cha.IClassHierarchy;

public class X10ZeroXCFABuilder extends X10CFABuilder {

    /**
     * @param cha
     * @param entrypoints
     * @param bypass
     * @param contextProvider
     */
    public X10ZeroXCFABuilder(IClassHierarchy cha, 
  			   AnalysisOptions options,
			   AnalysisCache cache,
  			   ContextSelector appContextSelector,
  			   SSAContextInterpreter appContextInterpreter, 
  			   ReflectionSpecification reflect, 
  			   int instancePolicy) {
      super(cha, options, cache);

      SSAContextInterpreter contextInterpreter = 
	      makeDefaultContextInterpreters(appContextInterpreter, options, cha, reflect);
      setContextInterpreter(contextInterpreter);

      ContextSelector def = new DefaultContextSelector();
      ContextSelector contextSelector =
	appContextSelector == null? 
	def: 
        new DelegatingContextSelector(appContextSelector, def);

      setContextSelector(contextSelector);

      setInstanceKeys(
        new X10ScopeMappingInstanceKeys(cha, this, 
          new ZeroXInstanceKeys(options, cha, contextInterpreter, instancePolicy)));
    }
}
