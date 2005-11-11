package com.ibm.domo.ast.x10.ipa.callgraph;

import com.ibm.domo.ipa.callgraph.AnalysisOptions;
import com.ibm.domo.ipa.callgraph.propagation.cfa.CFAPointerKeys;
import com.ibm.domo.ipa.cha.ClassHierarchy;
import com.ibm.domo.util.warnings.WarningSet;

public class X10CFABuilder extends X10SSAPropagationCallGraphBuilder {

    /**
     * @param cha
     * @param warnings
     */
    public X10CFABuilder(ClassHierarchy cha, WarningSet warnings, AnalysisOptions options) {
      super(cha, warnings, options, new CFAPointerKeys(cha));
    }
}
