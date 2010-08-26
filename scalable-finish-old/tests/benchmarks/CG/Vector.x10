package npb2;

/**
  Assume given a 2d unique distribution [0:px-1,0:py-1], mapping each
  point to a different place.  Such a distribution can be constructed
  through the call dist.factory.unique(R), where R=[0:px-1,0:py-1].
  Below we shall call such a distribution a 2-d grid.

  Given a grid G over [0:px-1,0:py-1], a column-replicated vector V is
  a sequence of px*Bx elements that is partitioned across the rows of
  the grid and replicated across the columns of the
  grid. Specifically, it is imlemented as a rail of Bx elements per
  place, with the rail at place (pi,pj) for pi in 0:px-1 and py in
  0:py-1 containing the [i*Bx:(i+1*Bx-1] elements of V.

  Such a vector is useful in matrix vector multiplications. Each place
  has a portion of the vector and the matrix and can compute its
  contribution to the product through local operations. Thus
  contributions can then be sumReduced across all rows of the grid to
  compute the final result.

  @author tongwen
  @author vj  

TODO: The following are compiler problems.
	:rail is not being propagated correctly for arrays. 
	
	Dependent types are not working correctly (compiler crashes
        with assertion violation) in the presence of clauses of the
        form col==this.col.
        
        @current is not correctly processed.

(C) Copyright IBM Corp. 2007
*/

final public value Vector(region(:rank==2&&rect/*&&unique*/) R, int localSize) {	
	// @current means: for all p. a[p].loc==D[p].
	//final LocalVector/*@current*/ value [. /*: distribution==this.D*/] a;
	final LocalVector  [.] a;
	final int px, py;
	final dist(:self.unique) D;
	public Vector(:self.R==R && self.localSize==localSize)
	(final region(:rank==2&&rect/*&&unique*/) R, final int localSize, final double initValue) {
		property(R,localSize);
		px = R.rank(0).high()+1; py = R.rank(1).high()+1;
		final int logx = Util.log2(px), logy=Util.log2(py);
		D=dist.UNIQUE;
		assert px==py; // Limitation of the current implementation.
		a = new LocalVector/*@current*/ [D] (point [q])  {
			int pi=q/py,pj=q%py;
		    return new LocalVector(pi,pj,this, localSize, initValue);
		};	
	}
	/*public Vector(region(:rect&&rank==2) R, int localSize, double initValue) {
	    this((dist(:rank==2&&rect)) dist.factory.unique(R), localSize, initValue);
	}*/
	/*public Vector(region(:rect&&rank==2) R, int localSize) {
	    this(R, localSize, 0.0D);
	}*/
	public Vector(:R==orig.R&&localSize==orig.localSize)(final Vector orig) { 
		//this(orig.R, orig.localSize,  0.0D);
		final region(:self==orig.R) R = (region(:self==orig.R)) orig.R;
		final int(:self==orig.localSize) localSize= (int(:self==orig.localSize)) orig.localSize;
		final double initValue = 0.0D;
		property(R,localSize);
		px = R.rank(0).high()+1; py = R.rank(1).high()+1;
		final int logx = Util.log2(px), logy=Util.log2(py);
		D=dist.UNIQUE;
		assert px==py; // Limitation of the current implementation.
		a = new LocalVector/*@current*/ [D] (point [q])  {
			int pi=q/py,pj=q%py;
		    return new LocalVector(pi,pj,this, localSize, initValue);
		};	
		
	}
	public final void set(final double x){ 	
		finish ateach( point q : D) a[q].set(x); 
	}
	public final void copyFrom(final Vector(:R==this.R&&localSize==this.localSize) source) {
		finish ateach( point q:D) a[q].copyFrom((double[:rail]) source.a[q].e);
	}
	public final void add(final Vector/*(:R==this.R &&localSize==this.localSize)*/ target, 
			      final int alpha) {
		finish ateach( point q  : D) a[q].add((double[:rail]) target.a[q].e, alpha);
	}	
	public final void addScalar(final double x){
		finish ateach( point q : D) a[q].addScalar(x);
	}
	public final void multByScalar(final double x){
		finish ateach( point q: D) a[q].multByScalar(x);
	}
	public final void axpy(final double alpha, final double beta,
			final Vector/*(:R==this.R &&localSize==this.localSize)*/ x, 
			final Vector /*(:R==this.R &&localSize==this.localSize)*/ y ){ 
		finish ateach( point q : D) 
		    a[q].axpy(alpha, beta,  (double[:rail]) x.a[q].e, (double[:rail]) y.a[q].e);
	}
	public final void transpose(final Vector out) {
		finish ateach (point [q] : D) {
			final double[:rail] e = (double[:rail]) a[q].e;
			int pi=q/py,pj=q%py, qt=pj*py+pi;
			final LocalVector target = out.a[qt];
			async (D[qt]) x10.lang.X10System.arraycopy(e, target.e);
		}
	}
	/** For a column vector, sum up the values in each row (across
	    all columns) in all to all fashion.  At the end, every
	    column has the sum (for each row).
	*/
	public void sumReducePieces() { 
		if (py > 1) 
			finish ateach(point q:D)  a[q].sumReducePiecesComm(); 
	}

	/** col vector: Return the sum of all elements in one column of the process grid.
	    Must be called at [0,0]. 

	    TODO: Remove this limitation. Need language extension to
	    get the point in the distribution for the current
	    place. (Restriction gives a region.)

	*/
	public final double sumReduce(){
	    assert here == D[0];
	    finish ateach(point q:D)  a[q].sumComm(); 
	    return a[0].getSum();
	}

	/** Return the dot product of this and o. The computation is
	    performed at every place; at place (I,J), a[I,J].getSum()
	    will return the dot product.
	 */

	public final double dot(final Vector/*(:D==this.D &&localSize==this.localSize)*/ o) { 
	    assert here == D[0];
	    finish ateach(point q : D)  a[q].dotComm((double[:rail]) o.a[q].e); 
	    return a[0].getSum();
	}
	/** Return the square-root of the sum of the squares of the
	    elements in this vector.  An all-to-all SPMD computation,
	    result available at every place (I,J) via a[here].getSum();
	*/
	public final double norm2(){
	    assert here == D[0];
	    finish ateach(point q : D)  a[q].norm2Comm(); 
	    return a[0].getSum();
	}
	public void print() {
	    for( point [q] : D) // this way the blocks print in the same squence.
		finish async (D[q]) {
	    	int pi=q/py,pj=q%py;
		System.out.println("block at place "+D[q].id +" : " + a[q].toStringValue());
	    }
	}
}
	
