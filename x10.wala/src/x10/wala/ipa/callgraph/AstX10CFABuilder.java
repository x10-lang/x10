package x10.wala.ipa.callgraph;

import com.ibm.wala.cast.ipa.callgraph.AstCFAPointerKeys;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.cha.IClassHierarchy;

public class AstX10CFABuilder extends AstX10SSAPropagationCallGraphBuilder {

    public AstX10CFABuilder(IClassHierarchy cha, AnalysisOptions options, AnalysisCache cache) {
      super(cha, options, cache, new AstCFAPointerKeys());
    }
}
