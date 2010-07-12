package npb2;

/** The class representing the local portion of a column or row Vector. 

   All methods in this class are local (do not involve any
   communication) unless their names end in Comm. Additionally, all
   Comm methods are SPMD: they must be invoked simultaneously by all
   activities on the process grid. Otherwise computation may deadlock.
   (The method involves point-to-point communication  and relies on receiving
   information from activities at other places.)

   @see Vector

(C) Copyright IBM Corp. 2007
*/
public final class LocalVector(int I, int J, int size) implements x10.lang.RemoteDoubleArrayCopier {
	public static int partner(int p, int i) {
		int x=1<<(i+1);
		return (p + x/2)%x + (p/x)*x;
	}
	final double[:rail] e;
	final double[:rail] scratch; // scratch memory used by reductions for incoming messages.
	final double[:rail] buffer; // scratch for outgoing copies of e. Used in reductions.
	final boolean[:rail] done; // used for signalling in all to all reductions.
	final int value [:rail] colPartner, rowPartner; // tables used in col and row allReduces.
	private double sum=0.0D;
	final region(:rail) Size, BufferSize;
	final Vector parent; // The Vector of which this is a component
	final int logx, logy;
	LocalVector(final int I, final int J, Vector parent, int size, double initValue) {
		//System.err.println("lv:I="+I+",J="+J+",size="+size);
		property(I, J,size);
		Size=[0:size-1];
		
		this.parent=parent;
		logx = Util.log2(parent.px); logy=Util.log2(parent.py);
		colPartner = new int value [[0:logx-1]] (point [i]) { return partner(I,i);};
		rowPartner = new int value [[0:logy-1]] (point [i]) { return partner(J,i);};
		int max = Math.max(logx, logy);
		BufferSize=[0:max*size-1];
		// need enough space for row and col iterations exchanging size elements.
		scratch = new double[BufferSize];
		buffer = new double[BufferSize];
		done = new boolean[[0:max-1]] (point [i]) { return false;};
		e = new double[Size];
		if (initValue != 0.0D) set(initValue);
		//for (point [i] : rowPartner) System.err.println(this + " rowPartner["+i+"]="+rowPartner[i]);

	}
	double getSum() { return sum;}
	void set(double x) { for (point [i]:e) e[i]=x;}
	void add(double[:rail/*&&size==this.size*/] o, int alpha) { 
		for (point [i] : e) e[i] += alpha*o[i];
	}
	void mult(double[:rail/*&&size==this.size*/] o) { 
		for(point [i] : e) e[i] *= o[i];
	}
	void addScalar(double x) {    for(point [i] : e) e[i] +=x; }
	void multByScalar(double x) { for(point [i] : e) e[i] *=x;}
	void copyFrom(double[:rail/*&&size==this.size&&onePlace==here*/] o) { 
		X10System.arraycopy(o, 0, e, 0, size);
	}
	void axpy(double alpha, double beta, double[:rail/*&&size==this.size&&onePlace==here*/] x, 
			double[:rail&&size==this.size] y ) {
		for (point [i] : e) e[i] = alpha*x[i]+beta*y[i];
	}
	public void postCopyRun(int s) { 
		atomic done[s/size]=true; 
		}
	public double[:rail] getDestArray() { return scratch; }
	public double[:rail] getSourceArray() { return buffer;}
	void sumReducePiecesComm() {
	    for (point [i] : rowPartner) {
		final int k = rowPartner[i];
		final int index = I*parent.py+k;
		x10.lang.Runtime.arrayCopy(e,0,buffer, i*size, size);
		x10.lang.Runtime.asyncDoubleArrayCopy(parent.a, i*size, 
				place.places(index), i*size, size, true);
		await done[i];
                done[i]=false;
		for (int m=0; m<size; ++m) e[m] += scratch[i*size+m];
	    }
	}
	
	/*void sumReducePiecesComm() {
	    for (point [i] : rowPartner) {
		final int k = rowPartner[i];
		final int index = I*parent.py+k;
		// TODO: Check with Igor how to do this without remote references.
		final Vector myParent = parent;
		X10System.arraycopy(e,0,buffer, i*size, size);
		async (dist.UNIQUE[index]) {
			final LocalVector target = myParent.a[index];
		    X10System.arraycopy(buffer, i*size, target.scratch,i*size, size);
		    atomic target.done[i]=true; }
		await done[i];
                done[i]=false;
		for (int m=0; m<size; ++m) e[m] += scratch[i*size+m];
	    }
	}*/
	/*int myI=-1;
	void sumReducePiecesComm(final int i) {
		final int k = rowPartner[i];
		final int index = I*parent.py+k;
		final LocalVector target=parent.a[index];
		X10System.arraycopy(e,0,buffer, i*size, size);
		atomic { myI++; }
		async (dist.UNIQUE[index]) {
		 // wait for previous messages to have been been processed.
			await target.myI==i; 
			for (int m=0; m < size; ++m) target.e[m] += buffer[i*size+m];
			if (i==logy-1) 
				atomic target.myI=-1; 
			else target.sumReducePiecesComm(i+1);
		}
	}*/
	/*int myI;
	void sumReducePiecesComm(int ignore) {
		myI=0;
		for (point [i] : rowPartner) {
			await myI==2*i;
			final int k = rowPartner[i];
			final int index = I*parent.py+k;
			final LocalVector target=parent.a[index];
			X10System.arraycopy(e,0,buffer, i*size, size);
			atomic { myI++; }
			async (dist.UNIQUE[index]) {
				// wait for previous messages to have been been processed.
				await target.myI==2*i+1; 
				for (int m=0; m < size; ++m) target.e[m] += buffer[i*size+m];
				atomic target.myI++;
			}
		}
	}*/
	
	void dotComm(double[:rail/*&&size==this.size*/] oe) {
	    sum=0.0D; for (point [p] : e) sum += oe[p]*e[p];
	    sumPropagate();
	}
	void norm2Comm() { 
		sum=0.0D; for (point [i] : e) sum +=e[i]*e[i];
		sumPropagate(); 
		sum = Math.sqrt(sum);
	}
	void sumComm() { 
		sum=0.0D;  
		for (point [i]:e) sum +=e[i];
		sumPropagate();
	}
	/** Exchange sum in an all-to-all fashion with all row partners. In the 
	    end each ends up with the sum of all the sum's in the row.
	 */
	void sumPropagate() {
	    final LocalVector me = this;
	    for (point [i] : rowPartner) {
		final int k = rowPartner[i];
		final LocalVector target=parent.a[I*parent.py+k];
		final double temp=sum;
		async (target) { 
		    target.scratch[i]=temp; 
		    atomic target.done[i]=true;
		}
		await done[i];
		done[i]=false;
		sum += scratch[i]; 
	    }
	}
	public String toString() {
		StringBuilder sb = new StringBuilder("lv(" + I + ","+J+"):");
		return sb.toString();
	}
	public String toStringValue() {
		StringBuilder sb = new StringBuilder("lv(" + I + ","+J+"):");
		for (int i=0; i < size; ++i) sb.append(" " + e[i]);
		return sb.toString();
	}
	
	
}
