package x10.lang;

/** The class of all multidimensional, settable distributed int arrays
 * in X10.  Specialized from ReferenceArray by replacing the type parameter
 * with float.

 * Handtranslated from the X10 code in x10/lang/FloatReferenceArray.x10
 * 
 * @author cmd
 */

public abstract class Complex4ReferenceArray extends complex4Array {
	
	public Complex4ReferenceArray( dist D) {
		super( D );
	}

	abstract public float setReal( float real,  point/*(region)*/ p);
	abstract /*value*/ public float setReal(float real,  int p);
	abstract /*value*/ public float setReal(float real,  int p, int q);
	abstract /*value*/ public float setReal(float real,  int p, int q, int r);
	abstract /*value*/ public float setReal(float real,  int p, int q, int r, int s);
	
	abstract public float setImag( float real,  point/*(region)*/ p);
	abstract /*value*/ public float setImag(float real, int p);
	abstract /*value*/ public float setImag(float real, int p, int q);
	abstract /*value*/ public float setImag(float real, int p, int q, int r);
	abstract /*value*/ public float setImag(float real, int p, int q, int r, int s);
	
	public float addSetReal( float real,float imag, point/*(region)*/ p) {
		return setReal(getReal(p)+real,p);
	}
	public float addSetReal(float real, float imag, int p) {
		return setReal(getReal(p)+real,p);
	}
	public float addSetReal(float real, float imag, int p, int q) {
		return setReal(getReal(p,q)+real,p,q);
	}
	public float addSetReal(float real, float imag, int p, int q, int r) {
		return setReal(getReal(p,q,r)+real,p,q,r);
	}
	public float addSetReal(float real, float imag, int p, int q, int r, int s) {
		return setReal(getReal(p,q,r,s)+real,p,q,r,s);
	}	
	public float mulSetReal( float real, float imag, point/*(region)*/ p) {
		float real_a,imag_a,result;
		real_a = getReal(p);
		imag_a = getImag(p);
		result = real * real_a - imag*imag_a;
		return setReal(result,p);
	}
	public float mulSetReal(float real, float imag, int p) {
		float real_a,imag_a,result;
		real_a = getReal(p);
		imag_a = getImag(p);
		result = real * real_a - imag*imag_a;
		return setReal(getReal(p)*real,p);
	}
	public float mulSetReal(float real, float imag, int p, int q) {
		float real_a,imag_a,result;
		real_a = getReal(p,q);
		imag_a = getImag(p,q);
		result = real * real_a - imag*imag_a;
		return setReal(result,p,q);
	}
	public float mulSetReal(float real, float imag, int p, int q, int r) {
		float real_a,imag_a,result;
		real_a = getReal(p,q,r);
		imag_a = getImag(p,q,r);
		result = real * real_a - imag*imag_a;
		return setReal(result,p,q,r);
	}
	public float mulSetReal(float real, float imag, int p, int q, int r, int s) {
		float real_a,imag_a,result;
		real_a = getReal(p,q,r,s);
		imag_a = getImag(p,q,r,s);
		result = real * real_a - imag*imag_a;
		return setReal(result,p,q,r,s);
	}
	public float subSetReal( float real, float imag, point/*(region)*/ p) {
		return setReal(getReal(p)-real,p);
	}
	public float subSetReal(float real, float imag, int p) {
		return setReal(getReal(p)-real,p);
	}
	public float subSetReal(float real, float imag, int p, int q) {
		return setReal(getReal(p,q)-real,p,q);
	}
	public float subSetReal(float real, float imag, int p, int q, int r) {
		return setReal(getReal(p,q,r)-real,p,q,r);
	}
	public float subSetReal(float real, float imag, int p, int q, int r, int s) {
		return setReal(getReal(p,q,r,s)-real,p,q,r,s);
	}
	public float divSetReal( float real, float imag, point/*(region)*/ p) {
		return setReal(getReal(p)/real,p);
	}
	public float divSetReal(float real, float imag, int p) {
		float real_a,imag_a,result;
		real_a = getReal(p);
		imag_a = getImag(p);
		result = real_a / real - imag_a/imag;
		return setReal(result,p);
		
	}
	public float divSetReal(float real, float imag, int p, int q) {
		float real_a,imag_a,result;
		real_a = getReal(p,q);
		imag_a = getImag(p,q);
		result = real_a / real - imag_a/imag;
		return setReal(result,p,q);
	}
	public float divSetReal(float real, float imag, int p, int q, int r) {
		float real_a,imag_a,result;
		real_a = getReal(p,q,r);
		imag_a = getImag(p,q,r);
		result = real_a / real - imag_a/imag;
		return setReal(result,p,q,r);
	}
	public float divSetReal(float real, float imag, int p, int q, int r, int s) {
		float real_a,imag_a,result;
		real_a = getReal(p,q,r,s);
		imag_a = getImag(p,q,r,s);
		result = real_a / real - imag_a/imag;
		return setReal(result,p,q,r,s);
	}
	
	
	public float addSetImag( float real,float imag, point/*(region)*/ p) {
		return setImag(getImag(p)+imag,p);
	}
	public float addSetImag(float real, float imag, int p) {
		return setImag(getImag(p)+imag,p);
	}
	public float addSetImag(float real, float imag, int p, int q) {
		return setImag(getImag(p,q)+imag,p,q);
	}
	public float addSetImag(float real, float imag, int p, int q, int r) {
		return setImag(getImag(p,q,r)+imag,p,q,r);
	}
	public float addSetImag(float real, float imag, int p, int q, int r, int s) {
		return setImag(getImag(p,q,r,s)+imag,p,q,r,s);
	}	
	public float mulSetImag( float real, float imag, point/*(region)*/ p) {
		float real_a,imag_a,result;
		real_a = getImag(p);
		imag_a = getImag(p);
		result = real * imag_a + imag*real_a;
		return setImag(result,p);
	}
	public float mulSetImag(float real, float imag, int p) {
		float real_a,imag_a,result;
		real_a = getImag(p);
		imag_a = getImag(p);
		result = real * imag_a + imag*real_a;
		return setImag(getImag(p)*real,p);
	}
	public float mulSetImag(float real, float imag, int p, int q) {
		float real_a,imag_a,result;
		real_a = getImag(p,q);
		imag_a = getImag(p,q);
		result = real * imag_a + imag*real_a;
		return setImag(result,p,q);
	}
	public float mulSetImag(float real, float imag, int p, int q, int r) {
		float real_a,imag_a,result;
		real_a = getImag(p,q,r);
		imag_a = getImag(p,q,r);
		result = real * imag_a + imag*real_a;
		return setImag(result,p,q,r);
	}
	public float mulSetImag(float real, float imag, int p, int q, int r, int s) {
		float real_a,imag_a,result;
		real_a = getImag(p,q,r,s);
		imag_a = getImag(p,q,r,s);
		result = real * imag_a + imag*real_a;
		return setImag(result,p,q,r,s);
	}
	public float subSetImag( float real, float imag, point/*(region)*/ p) {
		return setImag(getImag(p)-imag,p);
	}
	public float subSetImag(float real, float imag, int p) {
		return setImag(getImag(p)-imag,p);
	}
	public float subSetImag(float real, float imag, int p, int q) {
		return setImag(getImag(p,q)-imag,p,q);
	}
	public float subSetImag(float real, float imag, int p, int q, int r) {
		return setImag(getImag(p,q,r)-imag,p,q,r);
	}
	public float subSetImag(float real, float imag, int p, int q, int r, int s) {
		return setImag(getImag(p,q,r,s)-imag,p,q,r,s);
	}
	public float divSetImag( float real, float imag, point/*(region)*/ p) {
		return setImag(getImag(p)/real,p);
	}
	public float divSetImag(float real, float imag, int p) {
		float real_a,imag_a,result;
		real_a = getImag(p);
		imag_a = getImag(p);
		result = real_a / imag - imag_a/real;
		return setImag(result,p);
		
	}
	public float divSetImag(float real, float imag, int p, int q) {
		float real_a,imag_a,result;
		real_a = getImag(p,q);
		imag_a = getImag(p,q);
		result = real_a / imag - imag_a/real;
		return setImag(result,p,q);
	}
	public float divSetImag(float real, float imag, int p, int q, int r) {
		float real_a,imag_a,result;
		real_a = getImag(p,q,r);
		imag_a = getImag(p,q,r);
		result = real_a / imag - imag_a/real;
		return setImag(result,p,q,r);
	}
	public float divSetImag(float real, float imag, int p, int q, int r, int s) {
		float real_a,imag_a,result;
		real_a = getImag(p,q,r,s);
		imag_a = getImag(p,q,r,s);
		result = real_a / imag - imag_a/real;
		return setImag(result,p,q,r,s);
	}
	
}
