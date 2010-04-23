package ssca2;

import x10.util.*;

import x10.compiler.Native;

import x10.util.concurrent.atomic.AtomicBoolean;

       class retType {
         public global val S: x10.util.GrowableRail[types.VERT_T]!;
         public global val count: x10.util.GrowableRail[types.INT_T]!;

         public def this(S: x10.util.GrowableRail[types.VERT_T]!, count: x10.util.GrowableRail[types.INT_T]!) {
           this.S = S;
           this.count = count;
         }
      };
class betweenessCentrality {




      public static def compute_bfs (pg: defs.pGraph, place: Int, world: Comm!, source:types.VERT_T,  pred: Array[GrowableRail[types.VERT_T]](1)!, sig: Array[types.DOUBLE_T](1), d:Array[types.LONG_T](1)): retType {

           val S = new GrowableRail[types.VERT_T](0);
           val  count = new GrowableRail[types.INT_T](0);
           val pg_here = pg.restrict_here();
           val vertices = pg_here.vertices;
           val visited = new Array[Boolean](vertices, (p: Point(1))=>false);

           val fsize = pg.owner(source) == here ? 1 : 0;
           var L: Rail[types.UVDSQuad]! = Rail.make[types.UVDSQuad](fsize, (i:Int)=>types.UVDSQuad(-1, source, 0, 0.0));
           if (pg.owner(source) == here)  {d(source ) = 0; sig(source) = 1;}

            var l: Int = 0;
            count.add(0);
            while (true) {

             val N = Rail.make[GrowableRail[types.UVDSQuad]!](Place.MAX_PLACES, (i:Int)=>new GrowableRail[types.UVDSQuad](0));

              val frontier: Rail[types.UVDSQuad]! = L;
              val flength = frontier.length();
              val frontierSize = world.sum(flength);
              world.barrier();
              //x10.io.Console.OUT.println("frontier Size " + frontierSize + " " + flength);
              if (frontierSize == 0) break;

              count.add(count(l));
              for ((i) in 0..flength-1) {
                val vertex = frontier(i).second;

                if (visited(vertex) == true) continue;

               if (l >0 ) {
                 S.add(vertex);
                 count(l+1)++;
               }
                visited(vertex) = true;
                val lo = pg_here.numEdges(vertex);
                val hi = pg_here.numEdges(vertex+1)-1;
                  //x10.io.Console.OUT.println("lo..hi " + lo + " " + hi);


                for ((k) in  lo..hi) {
                  val neighbor = pg_here.endV(k);
                  val owner = pg.owner(neighbor);
                  if (neighbor == vertex) continue;
                  N(owner.id).add(types.UVDSQuad(vertex, neighbor, d(vertex), sig(vertex)));
                }
              }


              world.barrier();
              L = world.alltoallv[types.UVDSQuad](N);
              world.barrier();
                   
             for ((i) in 0..L.length()-1) {
               val t = L(i);
               val w = t.second;
               val v = t.first;
              if(d(w) == -1) {
               d(w)  = t.third + 1;
               sig(w) = t.fourth;
               (pred(w) as GrowableRail[types.VERT_T]!).add(v);
                //x10.io.Console.OUT.println("if: v w" + v + " " + w + " " + sig(w) + " " + d(w));

             } else if (d(w) ==  t.third+1) {
                  sig(w) += t.fourth;
                 (pred(w) as GrowableRail[types.VERT_T]!).add(v);
                //x10.io.Console.OUT.println("else: v w" + v + " " +  w + " " + sig(w) + " " + d(w));
             }
             } 
             l++; 
            }

           return new retType(S, count);
         }
         
      public static def compute (pg: defs.pGraph) {
          val  kernel4 = PTimer.make("kernel4");
          val  bfs = PTimer.make("bfs");
          val  back = PTimer.make("back");

           val n = pg.N;
           val chunkSize = pg.N / Place.MAX_PLACES;
           val unique = Dist.makeUnique();

           val NewVertices = DistArray.make[Rail[types.UVPair]](unique);
           finish ateach((p) in unique) {
               val world: Comm! = Comm.WORLD();
               NewVertices(here.id) =  util.random_permute_vertices(pg.restrict_here().vertices, n, Comm.WORLD());
           }
             val BC = PlaceLocalHandle.make[Array[types.DOUBLE_T](1)](unique, ()=>new Array[types.DOUBLE_T](pg.restrict_here().vertices, (p: Point(1))=>0d) as Array[types.DOUBLE_T](1)!);
             val Sig = PlaceLocalHandle.make[Array[types.DOUBLE_T](1)](unique, ()=>new Array[types.DOUBLE_T](pg.restrict_here().vertices, (p: Point(1))=>0.0d) as Array[types.DOUBLE_T](1)!);
             val Del = PlaceLocalHandle.make[Array[types.DOUBLE_T](1)](unique, ()=>new Array[types.DOUBLE_T](pg.restrict_here().vertices, (p: Point(1))=>0.0d) as Array[types.DOUBLE_T](1)!);
             val D = PlaceLocalHandle.make[Array[types.LONG_T](1)](unique, ()=>new Array[types.LONG_T](pg.restrict_here().vertices, (p: Point(1))=>-1) as Array[types.LONG_T](1)!);
             val Pred = PlaceLocalHandle.make[Array[GrowableRail[types.VERT_T]](1)](unique, ()=>new Array[GrowableRail[types.VERT_T]](pg.restrict_here().vertices, (p:Point(1))=>new GrowableRail[types.VERT_T]()) as Array[GrowableRail[types.VERT_T]](1)!);

           finish ateach((p) in unique) {
               val world: Comm! = Comm.WORLD();
               val pred = Pred() as Array[GrowableRail[types.VERT_T]](1);
               val del = Del() as Array[types.DOUBLE_T](1)!;
               val sig = Sig() as Array[types.DOUBLE_T](1)!;
               val d = D() as Array[types.LONG_T](1)!;
               val bc = BC() as Array[types.DOUBLE_T](1)!;

           for ((i) in 0..n-1) {


               //kernel4.start();

               for ((k) in pg.restrict_here().vertices) {
                        pred(k) =  new GrowableRail[types.VERT_T]();
                        del(k) = 0;
                        sig(k) = 0;
                        d(k) = -1;
               }

               //val new_vertices = NewVertices(here.id);
               //val a = world.sum(pg.owner(i) == here ? new_vertices as rail[types.UVPair]!)(i % chunkSize) 
               //val startVertex = world.sum(pg.owner(i) == here ? (new_vertices as Rail[types.UVPair]!)(i % chunkSize).second : 0); //actually a broadcast
               val startVertex = world.sum(pg.owner(i) == here ? i : 0); //actually a broadcast
               x10.io.Console.OUT.println ("i " + i + " " + startVertex);

               //bfs.start();
               world.barrier();
               val vert = betweenessCentrality.compute_bfs(pg, p, world, startVertex, pred as Array[GrowableRail[types.VERT_T]](1)!, sig as Array[types.DOUBLE_T](1), d as Array[types.LONG_T](1));
               world.barrier();
               //bfs.stop();
               x10.io.Console.OUT.println ("i after " + i + " " + startVertex);

               /* val S: GrowableRail[types.VERT_T]! = vert.S as GrowableRail[types.VERT_T]!;
               val count: GrowableRail[types.INT_T]! = vert.count as GrowableRail[types.VERT_T]!;

               val c_length = count.length();

               //x10.io.Console.OUT.println("before  barrier 1" );
               world.barrier();     
               //x10.io.Console.OUT.println("after  barrier 1" );
                //back.start();
                for (var l: Int = c_length-1; l >0; l--) {
                 val start = count(l-1);
                 val end = count(l)-1;

                 val tsum = world.sum(end-start+1);
                 world.barrier();
                 if (tsum == 0)  continue;
 //                x10.io.Console.OUT.println("tsum " + tsum);

                val N = Rail.make[GrowableRail[Triplet[types.VERT_T, Double,Double]]!](Place.MAX_PLACES, (i:Int)=>
					new GrowableRail[Triplet[types.VERT_T, Double, Double]](0));
                 for ((j) in start..end) {
                 val w = S(j);

                val pred_w  = pred(w);
                val pred_length = (pred(w) as GrowableRail[types.VERT_T]!).length();
                for ((k) in  0..pred_length-1) {
                  val v = (pred_w as GrowableRail[types.VERT_T]!)(k);
                  val del_w = del(w);
                  val sig_w = sig(w);
                  val owner = pg.owner(v);
                  N(owner.id).add(Triplet[types.VERT_T, Double, Double](v, del_w, sig_w));
                  
                 }
                }

                 val L = world.alltoallv[Triplet[types.VERT_T, Double, Double]](N);
                 world.barrier();

                  for ((k) in 0..L.length()-1) {
                    val v = L(k).first;
                    val del_w = L(k).second;
                    val sig_w = L(k).third;
                    del(v) = del(v) + sig(v)*(((1.0+del_w) as double)/sig_w);
                 } 
                 for ((j) in start..end) {
                   val w = S(j);
     //             x10.io.Console.OUT.println("bc " + w + " " + del(w));
                   bc(w) += del(w);
                 }
          } */
              // x10.io.Console.OUT.println("before barrier 2");
            world.barrier();     
               x10.io.Console.OUT.println("after barrier 2");
            //back.stop();
          } 

               //kernel4.stop();
        }
               //x10.io.Console.OUT.println ("ateach done" );


          finish ateach((p) in unique) {
               val world = Comm.WORLD();
             for ((i) in unique ) {
               world.barrier();
               if (i !=here.id) continue;
               val bc = BC() as Array[types.DOUBLE_T](1)!;
                for ((a) in bc.region) {
                 x10.io.Console.OUT.println ("BC " + bc(a));
                }
             }
         } 

    }
}
	
