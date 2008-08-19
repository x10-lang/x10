/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.ext.x10.ast;

import polyglot.ast.AmbQualifierNode;
import polyglot.ast.Ambiguous;
import polyglot.ast.Id;
import polyglot.ast.Prefix;
import polyglot.ast.QualifierNode;

/**
 * An <code>AmbTypeNode</code> is an ambiguous AST node composed of
 * dot-separated list of identifiers that must resolve to a type.
 */
public interface X10AmbQualifierNode extends AmbQualifierNode, Ambiguous
{
}
