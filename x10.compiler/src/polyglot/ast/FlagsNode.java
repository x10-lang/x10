package polyglot.ast;

import polyglot.types.Flags;

public interface FlagsNode extends Node {
	    Flags flags();
	    FlagsNode flags(Flags flags);
}
