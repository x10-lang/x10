package com.ibm.domo.ast.x10.ipa.callgraph;

import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.propagation.cfa.CFAPointerKeys;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.util.warnings.WarningSet;

public class X10CFABuilder extends X10SSAPropagationCallGraphBuilder {

    /**
     * @param cha
     * @param warnings
     */
    public X10CFABuilder(IClassHierarchy cha, WarningSet warnings, AnalysisOptions options) {
      super(cha, warnings, options, new CFAPointerKeys());
    }
}
