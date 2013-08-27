public struct E_Struct_Template[T,U] {
    public val first:T;
    public val second:U;

    public def this(first:T, second:U):E_Struct_Template[T,U] {
        this.first = first;
        this.second = second;
    }

    public def toString():String {
        return "(" + first + ", " + second + ")";
    }

}