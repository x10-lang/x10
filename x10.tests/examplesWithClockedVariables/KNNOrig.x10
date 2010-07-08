
/* 
Approximate but deterministic KNN 
Author: Nalini Vasudevan

*/


import x10.util.Random;

class Point {
	var x : int;
	var y: int;
	var positive: boolean;
	
	public def this () {
	}
	
	public def this(xx: int, yy: int) {
		x = xx;
		y = yy;
	
	}
	
	public def dist(p: Point!): double {
		return Math.sqrt (((x - p.x) * (x - p.x) + (y - p.y) * (y - p.y)) as Double);
	}
	
}


public class KNNOrig {

 var n: int = 0; // Should be a power of 2 if using merge sort
 var k: int = 0;
 val data: Rail[Point]!;
 val testPoint: Point!;
 val eDist: Rail[double]!;




 public def knn() {

	val minTest = Array.make[int](0..k-1);

 	for((i) in 0..k-1) async {
 	
 		val first = (n *i/k);
 		val last = (n * i/k) + n/k - 1;
 		val dataf = data(first) as Point!;
 		var min: double = eDist(first) = testPoint.dist(dataf);
 		minTest(i) = dataf.positive? 1: 0;
 		for ((j) in first+1..last) {
 		    val dataj = data(j) as Point!;
 			eDist(j) = testPoint.dist(dataj);
 			if (eDist(j) < min) {
				 		min = eDist(j); 
				 		minTest(i) = dataj.positive? 1: 0;
 			}
 		}
 	
 	}
 	val result = minTest.reduce(Int.+, 0);
 	
 	for ((m) in 0..n-1) {
 		if (m % (n/k) == 0) Console.OUT.println ("----------");
 	    val datam = data(m) as Point!;
 	    val test = datam.positive? '+' : '-';
 		Console.OUT.println("(" +  datam.x +  "," + datam.y + "): "+ eDist(m) + " " + test);
 		
 	}
 	
 	Console.OUT.println("(" +  testPoint.x +  "," + testPoint.y + "): ");
 	if (result > k/2)
 	  Console.OUT.println("Positive result");
 	else
 	  Console.OUT.println("Negative result"); 
} 



 public def this() {
 	n = 12;
 	k = 3;
 	data = Rail.make[Point](n, (int) => new Point()) as Rail[Point]!;
 	eDist = Rail.make[double](n) as Rail[double]!;
    val r = new Random();
 	for ((i) in 0..n-1) {
 	   
 		val datai = data(i) as Point!;
 		datai.x = Math.abs(r.nextInt()) % n;
 		datai.y = Math.abs(r.nextInt()) % n;
 		datai.positive = (r.nextInt() < 0);
 	}

 	testPoint = new Point() as Point!;
 	testPoint.x = Math.abs(r.nextInt()) % n;
 	testPoint.y = Math.abs(r.nextInt()) % n;
 	
 
 
 }


 public static  def main(s: Rail[String]) {
  		
    new KNNOrig().knn();
 
 
 }



}