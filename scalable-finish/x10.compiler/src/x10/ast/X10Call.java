/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.ast;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Call;
import polyglot.ast.TypeNode;

public interface X10Call extends Call {
	List<TypeNode> typeArguments();

	X10Call typeArguments(List<TypeNode> args);


}
