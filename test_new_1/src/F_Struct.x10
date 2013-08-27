public struct F_Struct {
	public val first:int;
	public val second:long;

	public def this(first:int, second:long):F_Struct {
		this.first = first;
		this.second = second;
	}

	public def toString():String {
		return "(" + first + ", " + second + ")";
	}
}