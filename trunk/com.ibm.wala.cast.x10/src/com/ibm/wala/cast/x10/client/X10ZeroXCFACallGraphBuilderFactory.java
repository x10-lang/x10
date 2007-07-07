package com.ibm.domo.ast.x10.client;

import com.ibm.domo.ast.x10.ipa.callgraph.X10ZeroXCFABuilder;
import com.ibm.wala.client.CallGraphBuilderFactory;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CallGraphBuilder;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.cfa.ZeroXInstanceKeys;
import com.ibm.wala.ipa.cha.IClassHierarchy;

public class X10ZeroXCFACallGraphBuilderFactory implements CallGraphBuilderFactory {

    public CallGraphBuilder make(AnalysisOptions options, IClassHierarchy cha, AnalysisScope scope, boolean keepPointsTo) {
        Util.addDefaultSelectors(options, cha);
        Util.addDefaultBypassLogic(options, scope, Util.class.getClassLoader(), cha);
        return new X10ZeroXCFABuilder(cha, options, null, null, options.getReflectionSpec(), ZeroXInstanceKeys.NONE);
    }
}
