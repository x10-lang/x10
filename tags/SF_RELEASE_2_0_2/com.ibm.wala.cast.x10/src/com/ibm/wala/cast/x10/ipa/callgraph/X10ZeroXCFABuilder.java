package com.ibm.wala.cast.x10.ipa.callgraph;

import com.ibm.wala.cast.ipa.callgraph.MiscellaneousHacksContextSelector;
import com.ibm.wala.ipa.callgraph.*;
import com.ibm.wala.ipa.callgraph.impl.DefaultContextSelector;
import com.ibm.wala.ipa.callgraph.impl.DelegatingContextSelector;
import com.ibm.wala.ipa.callgraph.propagation.SSAContextInterpreter;
import com.ibm.wala.ipa.callgraph.propagation.cfa.*;
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

      ContextSelector def = new DefaultContextSelector(options);

      ContextSelector  contextSelector = 
	  appContextSelector == null? 
			             def: 
			            new DelegatingContextSelector(appContextSelector, def);
			   
	  contextSelector = new MiscellaneousHacksContextSelector(
			       		  new nCFAContextSelector(1, contextSelector),
			       		  contextSelector,
			       		  cha,
			       		  new String[][]{
			       			  new String[]{"X10Primordial", 
			       					  	   "X10", 
			       					  	   "Lx10/lang/clock$factory",
			       					  	   "clock", 
			       					  	   "()Lx10/lang/clock;"},
			       			 new String[]{"X10Primordial", 
			   					  	   "X10", 
			   					  	   "Lx10/lang/clock",
			   					  	   "registered", 
			   					  	   "()Z"},
			   				new String[]{"X10Primordial", 
			   					  	   "X10", 
			   					  	   "Lx10/lang/clock",
			   					  	   "drop", 
			   					  	   "()V"},
			   				new String[]{"X10Primordial", 
			   					  	   "X10", 
			   					  	   "Lx10/lang/clock",
			   					  	   "doNext", 
			   					  	   "()V"},
			   				new String[]{"X10Primordial", 
			   					  	   "X10", 
			   					  	   "Lx10/lang/clock",
			   					  	   "resume", 
			   					  	   "()V"}
			   
			       		  });
			         
      setContextSelector(contextSelector);

      setInstanceKeys(
        new X10ScopeMappingInstanceKeys(cha, this, 
          new ZeroXInstanceKeys(options, cha, contextInterpreter, instancePolicy)));
    }
}
