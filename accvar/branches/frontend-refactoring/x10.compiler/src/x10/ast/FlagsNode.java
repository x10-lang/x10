package x10.ast;

import x10.types.Flags;

public interface FlagsNode extends Node {
	    Flags flags();
	    FlagsNode flags(Flags flags);
}
