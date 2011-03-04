package com.ibm.wala.cast.x10.tests.ir;

import java.util.Iterator;

import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.impl.Everywhere;

public final class CallEdgeChecker extends AbstractIRChecker implements IRChecker {

  public CallEdgeChecker(final String srcMethod, final String targetMethod) {
    this.fSrcMethod = srcMethod;
    this.fTargetMethod = targetMethod;
  }
  
  // --- Interface methods implementation
  
  public boolean isValid(final CallGraph callGraph) {
    final IMethod srcMethod = getMethod(this.fSrcMethod, callGraph.getClassHierarchy());
    final CGNode srcNode = callGraph.getNode(srcMethod, Everywhere.EVERYWHERE);
    if (srcNode == null) {
      throw new AssertionError(String.format("Could not find call graph node for method %s", srcMethod));
    }
    
    final IMethod targetMethod = getMethod(this.fTargetMethod, callGraph.getClassHierarchy());
    final CGNode targetNode = callGraph.getNode(targetMethod, Everywhere.EVERYWHERE);
    if (targetNode == null) {
      System.err.println(srcNode.getIR());
      for (final Iterator<? extends CGNode> succIter = callGraph.getSuccNodes(srcNode); succIter.hasNext();) {
        final CGNode succNode = succIter.next();
        System.err.println(String.format("Found successor: %s", succNode));
      }
      throw new AssertionError(String.format("Could not find call graph node for method %s", targetMethod));
    }

    System.out.println(String.format("Checking %s", toString()));
    
    boolean found = false;
    for (final Iterator<? extends CGNode> succIter = callGraph.getSuccNodes(srcNode); succIter.hasNext();) {
      final CGNode succNode = succIter.next();
      System.out.println(String.format("Found successor: %s", succNode));
      if (succNode == targetNode) {
        found = true;
        System.out.println("Edge checked.");
        break;
      }
    }
    return found;
  }
  
  // --- Overridden methods
  
  public String toString() {
    return String.format("Edge %s -> %s", this.fSrcMethod, this.fTargetMethod);
  }
  
  // --- Fields
  
  private final String fSrcMethod;
  
  private final String fTargetMethod;

}
