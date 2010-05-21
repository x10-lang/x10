package ssca2;
import x10.util.*;
final public class PTimer{

  static val timers: List[PTimer]!  = new ArrayList[PTimer]();

  global val start: DistArray[long](1);
  global val acc: DistArray[long](1);

  global val sum: DistArray[Double](1);
  global val mean: DistArray[Double](1);
  global val variance: DistArray[Double](1);
  global val min: DistArray[Pair[Double,Int]](1);
  global val max: DistArray[Pair[Double,Int]](1);

  global val name: String;

  public static def round_2(f:Double): String {
    val tmp = Math.round(f*100) as Int;
    val a = tmp/100;
    val b = tmp % 100;
    return a.toString() + "." + b.toString();

  }

  def this (name: String): PTimer {
   val DIST: Dist(1) = Dist.makeUnique();
   start = DistArray.make[long](DIST, (var pt: Point(1)):long=>0);
   acc = DistArray.make[long](DIST, (var pt:Point(1))=>0l);

   sum = DistArray.make[Double](DIST);
   max = DistArray.make[Pair[Double,Int]](DIST);
   min = DistArray.make[Pair[Double,Int]](DIST);
   mean = DistArray.make[Double](DIST);
   variance = DistArray.make[Double](DIST);

   this.name = name;
  } 

  public static def make (name: String): PTimer! {
  val timer = new PTimer(name);
  timers.add(timer);
   return timer;
  }

  public global def  start(): void {
    start(here.id) = Timer.nanoTime();
  }

  public global def gather():  void {
    val unique = Dist.makeUnique();
    finish ateach ((p) in unique) {
      val world = Comm.WORLD();

      sum(here.id) = world.sum(acc(here.id)*1e-9 as double);

      max(here.id) = world.maxPair(Pair[Double, Int](acc(here.id)*1e-9 as double,  here.id));
      min(here.id) = world.maxPair(Pair[Double, Int](-acc(here.id)*1e-9 as double,  here.id));
      min(here.id) = Pair[Double, Int](Math.abs(min(here.id).first), min(here.id).second);
      
      mean(here.id) =  sum(here.id) / (Place.MAX_PLACES as double);
      
      val tmp = (acc(here.id)*1e-9 as double )- mean(here.id);

      variance(here.id) = (world.sum(tmp*tmp)) / Place.MAX_PLACES;
    }
  }

  public global def stop() {
    acc(here.id) += Timer.nanoTime() - start(here.id);
  }

   public global def elapsed() {
         return acc(here.id);
   }

  public global def reset() {
    acc(here.id)  = 0;
  }

  public static def resetTimers() {
   for (timer: PTimer in timers) {
    finish ateach ((p): Point in Dist.makeUnique()){
     timer.reset();
    }
   }
  }

  public global def print() {
    x10.io.Console.OUT.println("PTimer " + name + " Seconds: " + acc(here.id)*1e-9);
  }

   public static def printTimers() {
   for (val timer: PTimer in  timers) {
     (timer as PTimer).print();
   }
  } 

   public static def printDetailed() {
    val unique = Dist.makeUnique();


    val buf_title = new StringBuilder();
    buf_title.add ("Place\t");
    for (val timer: PTimer in timers ) {
          buf_title.add(timer.name + "\t\t" );
    }
    x10.io.Console.OUT.println(buf_title);

    for((p) in unique) {
        val timers_copy = timers.toValRail();
      finish async (Place.places(p)) {
        val buf = new StringBuilder();
        buf.add(here.id.toString() + "\t\t");
        for ((i) in 0..timers_copy.length()-1) {
          val lcl_time = timers_copy(i).acc(here.id)*1e-9;
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
    for (val timer: PTimer in  timers) {
      timer.gather();
      buf_total.add(round_2(timer.sum(here.id)) + "\t\t" );
      buf_mean.add( round_2(timer.mean(here.id)) + "\t\t" );
      buf_max.add( timer.max(here.id).second + "\t\t" );
      buf_min.add( timer.min(here.id).second + "\t\t" );
      buf_variance.add( round_2(timer.variance(here.id))+ "\t\t");
    }
    x10.io.Console.OUT.println(buf_sep);
    x10.io.Console.OUT.println(buf_total);
    x10.io.Console.OUT.println(buf_max);
    x10.io.Console.OUT.println(buf_min);
    x10.io.Console.OUT.println(buf_mean);
    x10.io.Console.OUT.println(buf_variance);
  }

}

