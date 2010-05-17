package ssca2;

import x10.compiler.Native;
import x10.util.*;

final public class Comm {

    private global val my_id:Int;

    private static class Integer {
        var value:Int;

        def this(i:Int) { value = i; }
    }

    private const world = PlaceLocalHandle.make[Comm](Dist.makeUnique(), ()=>new Comm(0));;

    private const last_id = PlaceLocalHandle.make[Integer](Dist.makeUnique(), ()=>new Integer(0));

    private def this(new_id:Int) {
        my_id = new_id;
        val nplaces = Place.MAX_PLACES;
    }

    public static def WORLD() = world();

    public def split(color:Int, rank:Int) {
    }

    public def barrier() {
    }

    public def broadcast(a:Rail[Int]!, rootRank:Int) {

    }
    public def broadcast_d(a:Rail[Double]!, rootRank:Int) {
    }

    public def sum(i:Long): Long {
           return i;
    }

    public def sum(i:Int):Int {
           return i;
    }

    public def sum(d:Double):Double {
           return d;
    }

    public def min(i:Int):Int {
         return i;
    }

    public def min(d:Double):Double {
         return d;
    }

    public def max(i:Int):Int {
         return i;
    }

    public def max(d:Double):Double {
         return d;
    }

    public def indexOfAbsMax(d:Double, i:Int):Int {
       return i;
    }

    public def maxPair(d:Pair[Double,Int]): Pair[Double,int] {
        return d;
    }

    public def minPair(d:Pair[Double,Int]): Pair[Double,int] {
        return d;
    }




    public def allgather[T](A: Rail[T]!, my_size: long) {
	      val B: Rail[T]! = Rail.make[T](my_size as Int, (i:Int)=>A(i));
              return B;
   }

    public def allgatherv[T](A: GrowableRail[T]!, my_size: long) {
	      val B: Rail[T]! = Rail.make[T](my_size as Int, (i:Int)=>A(i));
              return B;
    }


    public def alltoallv[T] (A: Rail[GrowableRail[T]]!, B: GrowableRail[T]!) {
       B.setLength(A(0).length());
      for (var i: Int= 0; i < A(0).length(); i++)
              B(i) =A(0)(i);
    }

      public def usort[T](values: Rail[T]!, map: (T)=>Int): GrowableRail[T]! {
          val tmp: Rail[GrowableRail[T]]! = Rail.make[GrowableRail[T]](Place.MAX_PLACES, (i:Int)=>new GrowableRail[T](0));
          for ((i) in 0..values.length()-1) {
             tmp(map(values(i))).add(values(i));
          }
          val out_pairs = new GrowableRail[T](0);
          this.alltoallv[T](tmp, out_pairs);
          return out_pairs;
        }

      public def usort[T](values: Rail[T]!, map: (T)=>Int,g:GrowableRail[T]!) {
          val tmp: Rail[GrowableRail[T]]! = Rail.make[GrowableRail[T]](Place.MAX_PLACES, (i:Int)=>new GrowableRail[T](0));
          for ((i) in 0..values.length()-1) {
             tmp(map(values(i))).add(values(i));
          }
          this.alltoallv[T](tmp, g);
        }

      public def usort[T](values: GrowableRail[T]!, map: (T)=>Int) {
          val tmp: Rail[GrowableRail[T]]! = Rail.make[GrowableRail[T]](Place.MAX_PLACES, (i:Int)=>new GrowableRail[T](0));
          for ((i) in 0..values.length()-1) {
             tmp(map(values(i))).add(values(i));
          }
          val g = new GrowableRail[T](0);
          this.alltoallv[T](tmp, g);
          return g;
        }


      public def usort_val[V](values: Rail[V]!, map: (Int)=>Int,g:GrowableRail[V]!) {
          val tmp: Rail[GrowableRail[V]]! = Rail.make[GrowableRail[V]](Place.MAX_PLACES, (i:Int)=>new GrowableRail[V](0));
          for ((i) in 0..values.length()-1) {
             tmp(map(i)).add(values(i));
          }
          this.alltoallv[V](tmp, g);
      }
}
