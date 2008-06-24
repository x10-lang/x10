/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.ext.x10.ast;

import polyglot.ast.Ambiguous;
import polyglot.ast.Id;
import polyglot.ast.Prefix;
import polyglot.ast.QualifierNode;

/**
 * An <code>AmbTypeNode</code> is an ambiguous AST node composed of
 * dot-separated list of identifiers that must resolve to a type.
 */
public interface X10AmbQualifierNode extends QualifierNode, Ambiguous
{
	/**
	 * Qualifier of the type.
	 */
	Prefix qual();

	/**
	 * Set the qualifier of the type.
	 */
	X10AmbQualifierNode qual(Prefix qual);

	/**
	 * Ambiguous name.
	 */
	Id id();

	/**
	 * Set the ambiguous name.
	 */
	X10AmbQualifierNode id(Id name);
}
