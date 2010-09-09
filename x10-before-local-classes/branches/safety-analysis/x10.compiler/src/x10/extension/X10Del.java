/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.extension;

import polyglot.ast.JL;
import polyglot.ast.Node;
import polyglot.types.SemanticException;
import polyglot.visit.ContextVisitor;
import x10.effects.EffectComputer;

public interface X10Del extends X10Ext, JL {	
	Node computeEffectsOverride(Node n, EffectComputer ec) throws SemanticException;
	EffectComputer computeEffectsEnter(EffectComputer ec);
	Node computeEffects(EffectComputer ec);

}
