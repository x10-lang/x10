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
      min(here.id) = world.minPair(Pair[Double, Int](acc(here.id)*1e-9 as double,  here.id));

      mean(here.id) =  sum(here.id) / (Place.MAX_PLACES as double);
      
      val tmp = (acc(here.id)*1e-9 as double )- mean(here.id);

      variance(here.id) = world.sum(tmp*tmp);
    }
  }

  public global def stop() {
    acc(here.id) += Timer.nanoTime() - start(here.id);
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
    for (val timer: PTimer in timers ) {
          buf_title.add(timer.name + " " );
    }
    x10.io.Console.OUT.println(buf_title);

    for((p) in unique) {
        val timers_copy = timers.toValRail();
      finish async (Place.places(p)) {
        val buf = new StringBuilder();
        for ((i) in 0..timers_copy.length()-1) {
          val lcl_time = timers_copy(i).acc(here.id)*1e-9;
          buf.add("  " + lcl_time + "   ");
        }
        x10.io.Console.OUT.println (buf);
      }
    }

    val buf_total = new StringBuilder();
    val buf_max = new StringBuilder();
    val buf_min = new StringBuilder();
    val buf_mean = new StringBuilder();
    val buf_variance = new StringBuilder();
    for (val timer: PTimer in  timers) {
      timer.gather();
      buf_total.add(timer.sum(here.id) + " " );
      buf_mean.add(timer.mean(here.id) + " " );
      buf_max.add(timer.max(here.id) + " " );
      buf_min.add(timer.min(here.id) + " " );
      buf_variance.add(timer.variance(here.id) + " " );
    }
    x10.io.Console.OUT.println(buf_total);
    x10.io.Console.OUT.println(buf_max);
    x10.io.Console.OUT.println(buf_min);
    x10.io.Console.OUT.println(buf_mean);
    x10.io.Console.OUT.println(buf_variance);
  }

}

