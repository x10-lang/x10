package com.ibm.wala.cast.x10.ipa.callgraph;

import com.ibm.wala.ipa.callgraph.*;
import com.ibm.wala.ipa.callgraph.propagation.cfa.CFAPointerKeys;
import com.ibm.wala.ipa.cha.IClassHierarchy;

public class X10CFABuilder extends X10SSAPropagationCallGraphBuilder {

    /**
     * @param cha
     */
    public X10CFABuilder(IClassHierarchy cha, AnalysisOptions options, AnalysisCache cache) {
      super(cha, options, cache, new CFAPointerKeys());
    }
}
