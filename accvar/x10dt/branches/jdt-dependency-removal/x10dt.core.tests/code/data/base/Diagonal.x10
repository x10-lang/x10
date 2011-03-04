
/** An N*N Diagonal matrix. 

    <p> Maintains a single-place Rail[Float] of N  elements.

 */
public class Diagonal(N:Int) implements Matrix(N,N) {
    public static type Diagonal(N:Int)=Diagonal{self.N==N};

    val d:Rail[Float](N);
    def this(x:Rail[Float](N)):Diagonal(N) {
	this.d=x;
    }

    def rail():Rail[Float](N) = d;
    public operator this * (that:Matrix) {
	if (that instanceof Diagonal) {
	    val other = that as Diagonal(N);
	    val dd = Rail.make[Float](N, (i:Int)=> (d(i)*other(i)));
	    return new Diagonal(dd);
	}
	// Otherwise do the usual thing
    }

}