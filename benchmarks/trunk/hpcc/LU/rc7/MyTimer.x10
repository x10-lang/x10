package rc7;
import x10.util.*;
final value public class MyTimer{

//  static val timers: List[MyTimer]  = new ArrayList[MyTimer]();

  val start: Array[long](1);
  val acc: Array[long](1);
  val name: String;

  def this (name: String): MyTimer {
   val DIST: Dist(1) = Dist.makeUnique();
   start = Array.make[long](DIST, (var pt: Point):long=>0);
   acc = Array.make[long](DIST, (var pt : Point):long=>0);
   this.name = name;
  } 

  public static def make (name: String): MyTimer {
  val timer = new MyTimer(name);
 //  timers.add(timer);
   return timer;
  }

  public def start(): void {
    start(here.id) = Timer.nanoTime();
  }

  public def stop() {
    acc(here.id) += Timer.nanoTime() - start(here.id);
  }

/*  public def reset() {
   acc(here.id) = 0;
  }*/

  public def print() {
    x10.io.Console.OUT.println("MyTimer " + name + " Seconds: " + acc(here.id)*1e-9);
  }

  /* public static def printTimers() {
   for (val timer: MyTimer in  timers) {
     timer.print();
   }
  } */
}

