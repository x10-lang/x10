public class Utils {
    public static def max(a:int, b:int) = a>b?a:b;
    public static def min(a:int, b:int) = a>b?b:a;
    public static def max(a:double, b:double) = a>b?a:b;
    public static def min(a:double, b:double) = a>b?b:a;
    public static def fabs(v:double) =   v>=0.0?v:-v; 
    public static def FFT_Flops(n:int)= ((4.0*n-3.0)*n-1.0)*n/6.0; 
    public static def powerOf2(var p:int) {
	if (p <=0) return false;
	if (p==1) return true;
	while (true) {
	    if (p%2==1) return false;
	    p /=2;
	    if (p==1) return true;
	}
    }
    public static def log2(var p:int) {
	assert powerOf2(p) : "p=" + p + " is not a power of 2";
	if (p==1) return 0;
	var i:int=0;
	while (p>1) { p=p/2; i++;}
	return i;
    }
}
