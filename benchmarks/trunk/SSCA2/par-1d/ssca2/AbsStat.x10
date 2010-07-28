package ssca2;
import x10.util.*;
public abstract class AbsStat[T]{
	
	global val start: DistArray[T](1);
global val acc: DistArray[T](1);

global val sum: DistArray[Double](1);
global val min: DistArray[Pair[Double, Int]](1);
global val max: DistArray[Pair[Double,Int]](1);
global val mean: DistArray[Double](1);
global val variance: DistArray[Double](1);

global val name: String;

public static def round_2(f:Double): String {
	val tmp = Math.round(f*100) as Int;
	val a = tmp/100;
	val b = tmp % 100;
	return a.toString() + "." + b.toString();
	
}

def this (name: String): AbsStat[T] {
	val DIST: Dist(1) = Dist.makeUnique();
start = DistArray.make[T](DIST, (var pt: Point(1))=>0 as T);
acc = DistArray.make[T](DIST, (var pt:Point(1))=>0 as T);

sum = DistArray.make[Double](DIST);
max = DistArray.make[Pair[Double,Int]](DIST);
min = DistArray.make[Pair[Double,Int]](DIST);
mean = DistArray.make[Double](DIST);
variance = DistArray.make[Double](DIST);

this.name = name;
} 

public global def gather():  void {
	val unique = Dist.makeUnique();
	finish ateach ((p) in unique) {
		val world = Comm.WORLD();
		
		sum(here.id) = world.sum(acc(here.id) as Double);
		
		max(here.id) = world.maxPair(Pair[Double, Int](acc(here.id) as Double,  here.id));
		min(here.id) = world.maxPair(Pair[Double, Int](-(acc(here.id) as Double),  here.id));
		
		mean(here.id) =  (sum(here.id)  as Double)/ (Place.MAX_PLACES as double);
		
		val tmp = ((acc(here.id) as Double) - (mean(here.id) as Double)) as Double;
		
		variance(here.id) = (world.sum(tmp * tmp) as Double)/ Place.MAX_PLACES;
	}
}


public global def reset() {
	acc(here.id)  = 0 as T;
}

public static def resetAll[C](collection: List[AbsStat[C]]!) {
	for (elem: AbsStat[C] in collection) {
		finish ateach ((p): Point in Dist.makeUnique()){
			elem.reset();
		}
	}
}

public global def elapsed() = acc(here.id);

public abstract global def print(): void;

public static def printAll[C](collection: List[AbsStat[C]]!) {
	for (val elem: AbsStat[C] in  collection) {
		elem.print();
	}
} 

public static def printDetailed[C](collection: List[AbsStat[C]]!, val factor: Double) {
	val unique = Dist.makeUnique();
	
	
	val buf_title = new StringBuilder();
	buf_title.add ("Place\t");
	for (val elem: AbsStat[C] in collection ) {
		buf_title.add(elem.name + "\t\t" );
	}
	x10.io.Console.OUT.println(buf_title);
	
	for((p) in unique) {
		val collection_copy = collection.toValRail();
		finish async (Place.places(p)) {
			val buf = new StringBuilder();
			buf.add(here.id.toString() + "\t\t");
			for ((i) in 0..collection_copy.length()-1) {
				val lcl_time = (collection_copy(i).acc(here.id) as double)*factor;
				buf.add(round_2(lcl_time) + "\t\t");
			}
			x10.io.Console.OUT.println (buf);
		}
	}
	
	val buf_sep = new StringBuilder();
	buf_sep.add("----------------------------------------------------");
	val buf_total = new StringBuilder();
	buf_total.add("total\t");
	val buf_max = new StringBuilder();
	buf_max.add("max\t");
	val buf_min = new StringBuilder();
	buf_min.add("min\t");
	val buf_mean = new StringBuilder();
	buf_mean.add("mean\t");
	val buf_variance = new StringBuilder();
	buf_variance.add("var\t");
	for (val elem: AbsStat[C] in  collection) {
		elem.gather();
		buf_total.add(round_2(elem.sum(here.id)*factor) + "\t\t" );
		buf_mean.add( round_2(elem.mean(here.id)*factor) + "\t\t" );
		buf_max.add( elem.max(here.id).second + "\t\t" );
		buf_min.add( elem.min(here.id).second + "\t\t" );
		buf_variance.add( round_2(elem.variance(here.id)*factor*factor) + "\t\t");
	}
	x10.io.Console.OUT.println(buf_sep);
	x10.io.Console.OUT.println(buf_total);
	x10.io.Console.OUT.println(buf_max);
	x10.io.Console.OUT.println(buf_min);
	x10.io.Console.OUT.println(buf_mean);
	x10.io.Console.OUT.println(buf_variance);
}

}

