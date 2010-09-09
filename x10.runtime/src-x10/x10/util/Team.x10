package x10.util;

import x10.compiler.Native;
import x10.compiler.NativeRep;

public struct Team {

    public static WORLD = Team(0);

    private id: Int;

    private def this (id:Int) { this.id = id; }

    public def this (places:Rail[Place]) {
        var id:Int = 0;
        @Native("c++",
                "x10rt_team nu_team = 0;" +
                "x10rt_team_new(places->length(), (x10rt_place*)places->raw(), x10rt_team_setter, &nu_team);" +
                "while (nu_team==0) x10rt_probe();" +
                "id = nu_team;") { }
        this.id = id;
    }

    public def size () : Int {
        var r:Int;
        @Native("c++",
                "r = x10rt_team_sz(this_.FMGL(id));") { r = 0; }
        return r;
    }
    
    public def barrier (role:Int) : void {
        @Native("c++",
                "int finished = 0;" +
                "x10rt_barrier(this_.FMGL(id), role, x10rt_one_setter, &finished);" +
                "while (!finished) x10rt_probe();") {}
    }
    
    public def scatter[T] (role:Int, root:Int, src:Rail[T], src_off:Int, dst:Rail[T], dst_off:Int, count:Int) : void {
        @Native("c++",
                "int finished = 0;" +
                "x10rt_scatter(this_.FMGL(id), role, root, &src->raw()[src_off], &dst->raw()[dst_off], sizeof(FMGL(T)), count, x10rt_one_setter, &finished);" +
                "while (!finished) x10rt_probe();") {}
    }
    
    public def bcast[T] (role:Int, root:Int, src:Rail[T], src_off:Int, dst:Rail[T], dst_off:Int, count:Int) : void {
        @Native("c++",
                "int finished = 0;" +
                "x10rt_bcast(this_.FMGL(id), role, root, &src->raw()[src_off], &dst->raw()[dst_off], sizeof(FMGL(T)), count, x10rt_one_setter, &finished);" +
                "while (!finished) x10rt_probe();") {}
    }
    
    public def alltoall[T] (role:Int, src:Rail[T], src_off:Int, dst:Rail[T], dst_off:Int, count:Int) : void {
        @Native("c++",
                "int finished = 0;" +
                "x10rt_alltoall(this_.FMGL(id), role, &src->raw()[src_off], &dst->raw()[dst_off], sizeof(FMGL(T)), count, x10rt_one_setter, &finished);" +
                "while (!finished) x10rt_probe();") {}
    }

    public static val ADD = 0;
    public static val MUL = 1;
    public static val DIV = 2;
    public static val AND = 3;
    public static val OR  = 4;
    public static val XOR = 5;
    public static val MAX = 6;
    public static val MIN = 7;

    private def allreduce_[T] (role:Int, src:Rail[T], src_off:Int, dst:Rail[T], dst_off:Int, count:Int, op:Int) : void {
        @Native("c++",
                "int finished = 0;" +
                "x10rt_red_type type = x10rt_get_red_type<FMGL(T)>();" +
                "x10rt_allreduce(this_.FMGL(id), role, &src->raw()[src_off], &dst->raw()[dst_off], (x10rt_red_op_type)op, type, count, x10rt_one_setter, &finished);" +
                "while (!finished) x10rt_probe();") {}
    }

    /* using overloading is the correct thing to do here since the set of supported
     * types are finite, however the java backend will not be able to distinguish
     * these methods' prototypes so we use the unsafe generic approach for now.
     */
    /*
    public def allreduce (role:Int, src:Rail[Byte], src_off:Int, dst:Rail[Byte], dst_off:Int, count:Int, op:Int) : void {
        allreduce_(role, src, src_off, dst, dst_off, count, op);
    }
    
    public def allreduce (role:Int, src:Rail[UByte], src_off:Int, dst:Rail[UByte], dst_off:Int, count:Int, op:Int) : void {
        allreduce_(role, src, src_off, dst, dst_off, count, op);
    }
    
    public def allreduce (role:Int, src:Rail[Short], src_off:Int, dst:Rail[Short], dst_off:Int, count:Int, op:Int) : void {
        allreduce_(role, src, src_off, dst, dst_off, count, op);
    }
    
    public def allreduce (role:Int, src:Rail[UShort], src_off:Int, dst:Rail[UShort], dst_off:Int, count:Int, op:Int) : void {
        allreduce_(role, src, src_off, dst, dst_off, count, op);
    }
    
    public def allreduce (role:Int, src:Rail[Int], src_off:Int, dst:Rail[Int], dst_off:Int, count:Int, op:Int) : void {
        allreduce_(role, src, src_off, dst, dst_off, count, op);
    }
    
    public def allreduce (role:Int, src:Rail[Int], src_off:Int, dst:Rail[Int], dst_off:Int, count:Int, op:Int) : void {
        allreduce_(role, src, src_off, dst, dst_off, count, op);
    }
    
    public def allreduce (role:Int, src:Rail[Long], src_off:Int, dst:Rail[Long], dst_off:Int, count:Int, op:Int) : void {
        allreduce_(role, src, src_off, dst, dst_off, count, op);
    }
    
    public def allreduce (role:Int, src:Rail[ULong], src_off:Int, dst:Rail[ULong], dst_off:Int, count:Int, op:Int) : void {
        allreduce_(role, src, src_off, dst, dst_off, count, op);
    }
    
    public def allreduce (role:Int, src:Rail[Float], src_off:Int, dst:Rail[Float], dst_off:Int, count:Int, op:Int) : void {
        allreduce_(role, src, src_off, dst, dst_off, count, op);
    }
    
    public def allreduce (role:Int, src:Rail[Double], src_off:Int, dst:Rail[Double], dst_off:Int, count:Int, op:Int) : void {
        allreduce_(role, src, src_off, dst, dst_off, count, op);
    }
    */
    
    public def allreduce[T] (role:Int, src:Rail[T], src_off:Int, dst:Rail[T], dst_off:Int, count:Int, op:Int) : void {
        allreduce_(role, src, src_off, dst, dst_off, count, op);
    }
    
    private def allreduce_[T] (role:Int, src:T, op:Int) : T {
        var dst:T;
        @Native("c++",
                "int finished = 0;" +
                "x10rt_red_type type = x10rt_get_red_type<FMGL(T)>();" +
                "x10rt_allreduce(this_.FMGL(id), role, &src, &dst, (x10rt_red_op_type)op, type, 1, x10rt_one_setter, &finished);" +
                "while (!finished) x10rt_probe();") { dst = src; }
        return dst;
    }

    public def allreduce (role:Int, src:Byte, op:Int) = allreduce_(role, src, op);
    public def allreduce (role:Int, src:UByte, op:Int) = allreduce_(role, src, op);
    public def allreduce (role:Int, src:Short, op:Int) = allreduce_(role, src, op);
    public def allreduce (role:Int, src:UShort, op:Int) = allreduce_(role, src, op);
    public def allreduce (role:Int, src:UInt, op:Int) = allreduce_(role, src, op);
    public def allreduce (role:Int, src:Int, op:Int) = allreduce_(role, src, op);
    public def allreduce (role:Int, src:Long, op:Int) = allreduce_(role, src, op);
    public def allreduce (role:Int, src:ULong, op:Int) = allreduce_(role, src, op);
    public def allreduce (role:Int, src:Float, op:Int) = allreduce_(role, src, op);
    public def allreduce (role:Int, src:Double, op:Int) = allreduce_(role, src, op);
    
    public def indexOfMax (role:Int, v:Double, idx:Int) : Int {
        var r:Int;
        @Native("c++",
                "int finished = 0;" +
                "x10rt_dbl_s32 src = {v, idx};" +
                "x10rt_dbl_s32 dst;" +
                "x10rt_allreduce(this_.FMGL(id), role, &src, &dst, X10RT_RED_OP_MAX, X10RT_RED_TYPE_DBL_S32, 1, x10rt_one_setter, &finished);" +
                "while (!finished) x10rt_probe();" +
                "r = dst.idx;") { r = 0; }
        return r;
    }
    
    public def indexOfMin (role:Int, v:Double, idx:Int) : Int {
        var r:Int;
        @Native("c++",
                "int finished = 0;" +
                "x10rt_dbl_s32 src = {v, idx};" +
                "x10rt_dbl_s32 dst;" +
                "x10rt_allreduce(this_.FMGL(id), role, &src, &dst, X10RT_RED_OP_MIN, X10RT_RED_TYPE_DBL_S32, 1, x10rt_one_setter, &finished);" +
                "while (!finished) x10rt_probe();" +
                "r = dst.idx;") { r = 0; }
        return r;
    }
    
    public def split (role:Int, color:Int, new_role:Int) : Team {
        var id:Int = 0;
        @Native("c++",
                "x10rt_team nu_team = 0;" +
                "x10rt_team_split(this_.FMGL(id), role, color, new_role, x10rt_team_setter, &nu_team);" +
                "while (nu_team==0) x10rt_probe();" +
                "id = nu_team;") { }
        return Team(id);
    }
    
    public def del (role:Int) : void {
        @Native("c++",
                "int finished = 0;" +
                "x10rt_team_del(this_.FMGL(id), role, x10rt_one_setter, &finished);" +
                "while (!finished) x10rt_probe();") { }
    }
    
}

// vim: shiftwidth=4:tabstop=4:expandtab
