package x10.lang;

public abstract struct Struct implements Any {
	
	public def this() {}
	//public native def toString():String;
	//public native def typeName():String;
	public global property def loc():Place=here;
	public global property def at(p:Place):Boolean=true;
	public global property def at(o:Object):Boolean=true;
}