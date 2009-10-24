
public class MapReduce {
    def foo() = {
    	val r= [0..5];
    	var d: int = 5;
        val dVal= d;
        val aS= Array.make[int](Dist.makeUnique(),(Point)=>dVal);
        val D= Dist.makeBlock(r);
        // val HERE= here;
        finish 
           for (q in aS.region) async at (aS.dist(q)) {
               for (p in D(q)) {
                   blah();
                   aS(here.id) = op(aS(here.id), f);
               }
           }
        var a:int = aS.reduce((x:int,y:int)=> x+y, dVal);
    }
    def op(x: int, y: int) = { return x + y; }
    def blah() = { }
}