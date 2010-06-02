package com.ibm.wala.cast.x10.tests.ir;

import com.ibm.wala.ipa.callgraph.CallGraph;


public interface IRChecker {
  
  public boolean isValid(final CallGraph callGraph);

}
