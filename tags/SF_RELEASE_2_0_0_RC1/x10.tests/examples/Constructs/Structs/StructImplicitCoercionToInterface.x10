import harness.x10Test;

class StructImplicitCoercionToInterface extends x10Test {
    static interface I { 
    def i():Int;
    }
    static struct S implements I {
        val i:Int;
    def this(i:Int) { this.i=i;}
    public def typeName() = "S";
    public def i() = i;
    public static operator (x:S):I = new I() {
        public def i() = x.i;
                public def toString() = "<I i=" + x.i+">";
        };
    }
    static def q(x:I) {
   
    }
    public def run() {
     val x:I= S(4);
        q(x);
        q(S(5));
        return true;
    }
    public static def main(Rail[String]) {
       new StructImplicitCoercionToInterface().execute();
       
    }
}