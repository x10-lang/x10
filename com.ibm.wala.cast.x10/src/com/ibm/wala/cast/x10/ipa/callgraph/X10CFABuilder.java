package com.ibm.wala.cast.x10.ipa.callgraph;

import com.ibm.wala.cast.ipa.callgraph.AstCFAPointerKeys;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.cha.IClassHierarchy;

public class X10CFABuilder extends X10SSAPropagationCallGraphBuilder {

    /**
     * @param cha
     */
    public X10CFABuilder(IClassHierarchy cha, AnalysisOptions options, AnalysisCache cache) {
      super(cha, options, cache, new AstCFAPointerKeys());
    }
}
