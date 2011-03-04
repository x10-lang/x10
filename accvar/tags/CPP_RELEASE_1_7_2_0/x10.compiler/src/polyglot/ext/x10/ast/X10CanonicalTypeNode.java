package polyglot.ext.x10.ast;

import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.TypeNode;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.TypeChecker;

public interface X10CanonicalTypeNode extends X10TypeNode, CanonicalTypeNode {
  
}
