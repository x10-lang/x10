package polyglot.ext.x10.ast;

import polyglot.types.Type;

public interface X10CastInfo {

	public boolean isPrimitiveCast();

	public boolean notNullRequired();

	public boolean isDynamicCheckNeeded();
	
	public Type type();

	public boolean isToTypeNullable();
}
