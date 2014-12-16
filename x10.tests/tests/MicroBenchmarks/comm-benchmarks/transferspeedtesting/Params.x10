public struct Params(
		abort:Boolean,
		len:Long,
		window:Long,
		iterations:Int,
		v:Boolean, // didn't implement it
		put:Boolean, // off by default
		get:Boolean, // off by default
		automatic:Boolean // on by default
){
	public static Default = Params(false,1024,100,320n,false, false, false, false);
}
