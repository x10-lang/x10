import x10.io.Console;

import x10.compiler.*;


public class PolySat {
    static type Lit = Int;
    static type Vec[T] = ValRail[T];

    @NativeRep("c++", "x10aux::ref<PolySat__Solver>", "PolySat__Solver", null)
    @NativeCPPOutputFile("PolySat__Solver.h")
    @NativeCPPCompilationUnit("minisat_solver.cc")
    @NativeCPPOutputFile("minisat_solver.h")
    @NativeCPPOutputFile("minisat_solver_types.h")
    @NativeCPPOutputFile("minisat_util.h")
    @NativeCPPOutputFile("minisat_vec.h")
    @NativeCPPOutputFile("minisat_alg.h")
    @NativeCPPOutputFile("minisat_heap.h")
    @NativeCPPOutputFile("minisat_sort.h")
    static class Solver {
        public def this (filename:String) : Solver { }
        @Native("c++", "(#0)->solve((#1))")
        public def solve ((Vec[Lit])=>Void) = false;
        @Native("c++", "(#0)->addClause((#1))")
        public def addClause (Vec[Lit]) { }
        @Native("c++", "(#0)->kill()")
        public def kill () { }
        @Native("c++", "(#0)->dump()")
        public def dump () { }
    }

    public static def main (args : Rail[String]!) {
        val filename = args.length == 0 ? "example_small.cnf" : args(0);
        val everyone = Dist.makeUnique();
        val s_handle = PlaceLocalHandle.make[Solver](everyone, ()=>new Solver(filename));
        ateach (i in everyone) {
            val s = s_handle();
            val not_here = Dist.makeUnique(Rail.make[Place](Place.MAX_PLACES-1, (i:Int)=>Place.places(i<here.id ? i : i+1)));
            Console.OUT.println("Solving starting at "+here);
            val b = s.solve((clause:Vec[Lit])=>{
                foreach (i in not_here) async(not_here(i)) {
                    val local_solver = s_handle();
                    //Runtime.println("Clause from afar");
                    local_solver.addClause(clause);
                }
            });
            if (b) {
                Console.OUT.println("Solving done at "+here);
                s.dump();
                ateach (i in not_here) s_handle().kill();
            } else {
                Console.OUT.println("Solving done somewhere else");
            }
        }
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
