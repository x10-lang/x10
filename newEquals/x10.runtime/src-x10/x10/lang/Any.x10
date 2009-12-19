package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;
/**
 * The top of the type hierarchy.
 * @author vj 12/14/09
 * 
 */
@NativeRep("java", "x10.core.Any", null, null)
public interface Any {
	
	@Native("java", "x10.lang.Place.place(x10.core.Ref.home(#0))")
	property def home():Place;
	
	@Native("java", "x10.core.Ref.at(#0, #1)")
	property def at(p:Object):Boolean;
	
	@Native("java", "x10.core.Ref.at(#0, #1.id)")
	property def at(p:Place):Boolean;
	
	global safe def toString():String;
	
	@Native("java", "x10.core.Ref.typeName(#0)")
	global safe def typeName():String;
}