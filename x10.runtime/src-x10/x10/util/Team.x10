package x10.util;

import x10.compiler.Native;
import x10.compiler.NativeRep;

public struct Team {

    public static WORLD = Team(0);

    private id: UInt;

    private def this (id:UInt) { this.id = id; }

    public def this (places:Rail[Place]) {
        var id:UInt = 0;
        @Native("c++",
                "x10rt_team nu_team = 0;" +
                "x10rt_team_new(places->length(), (x10rt_place*)places->raw(), x10rt_team_setter, &nu_team);" +
                "while (nu_team==0) x10rt_probe();" +
                "id = nu_team;") { }
        this.id = id;
    }

    public def barrier (role:UInt) : void {
        @Native("c++",
                "int finished = 0;" +
                "x10rt_barrier(this_.FMGL(id), role, x10rt_one_setter, &finished);" +
                "while (!finished) x10rt_probe();") {}
    }
    
    public def bcast[T] (role:UInt, root:UInt, src:Rail[T], src_off:UInt, dst:Rail[T], dst_off:UInt, count:UInt) : void {
        @Native("c++",
                "int finished = 0;" +
                "x10rt_bcast(this_.FMGL(id), role, root, &src->raw()[src_off], &dst->raw()[dst_off], sizeof(FMGL(T)), count, x10rt_one_setter, &finished);" +
                "while (!finished) x10rt_probe();") {}
    }
    
    public def alltoall[T] (role:UInt, src:Rail[T], src_off:UInt, dst:Rail[T], dst_off:UInt, count:UInt) : void {
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

    private def allreduce_[T] (role:UInt, src:Rail[T], src_off:UInt, dst:Rail[T], dst_off:UInt, count:UInt, op:Int) : void {
        @Native("c++",
                "int finished = 0;" +
                "x10rt_red_type type = x10rt_get_red_type<FMGL(T)>();" +
                "x10rt_allreduce(this_.FMGL(id), role, &src->raw()[src_off], &dst->raw()[dst_off], (x10rt_red_op_type)op, type, count, x10rt_one_setter, &finished);" +
                "while (!finished) x10rt_probe();") {}
    }

    /* using overloading is the correct thing to do here since the supported
     * types are finite, however the java backend will not be able to distinguish
     * these methods' prototypes so we use the unsafe generic approach for now.
     */
    /*
    public def allreduce (role:UInt, src:Rail[Byte], src_off:UInt, dst:Rail[Byte], dst_off:UInt, count:UInt, op:Int) : void {
        allreduce_(role, src, src_off, dst, dst_off, count, op);
    }
    
    public def allreduce (role:UInt, src:Rail[UByte], src_off:UInt, dst:Rail[UByte], dst_off:UInt, count:UInt, op:Int) : void {
        allreduce_(role, src, src_off, dst, dst_off, count, op);
    }
    
    public def allreduce (role:UInt, src:Rail[Short], src_off:UInt, dst:Rail[Short], dst_off:UInt, count:UInt, op:Int) : void {
        allreduce_(role, src, src_off, dst, dst_off, count, op);
    }
    
    public def allreduce (role:UInt, src:Rail[UShort], src_off:UInt, dst:Rail[UShort], dst_off:UInt, count:UInt, op:Int) : void {
        allreduce_(role, src, src_off, dst, dst_off, count, op);
    }
    
    public def allreduce (role:UInt, src:Rail[Int], src_off:UInt, dst:Rail[Int], dst_off:UInt, count:UInt, op:Int) : void {
        allreduce_(role, src, src_off, dst, dst_off, count, op);
    }
    
    public def allreduce (role:UInt, src:Rail[UInt], src_off:UInt, dst:Rail[UInt], dst_off:UInt, count:UInt, op:Int) : void {
        allreduce_(role, src, src_off, dst, dst_off, count, op);
    }
    
    public def allreduce (role:UInt, src:Rail[Long], src_off:UInt, dst:Rail[Long], dst_off:UInt, count:UInt, op:Int) : void {
        allreduce_(role, src, src_off, dst, dst_off, count, op);
    }
    
    public def allreduce (role:UInt, src:Rail[ULong], src_off:UInt, dst:Rail[ULong], dst_off:UInt, count:UInt, op:Int) : void {
        allreduce_(role, src, src_off, dst, dst_off, count, op);
    }
    
    public def allreduce (role:UInt, src:Rail[Float], src_off:UInt, dst:Rail[Float], dst_off:UInt, count:UInt, op:Int) : void {
        allreduce_(role, src, src_off, dst, dst_off, count, op);
    }
    
    public def allreduce (role:UInt, src:Rail[Double], src_off:UInt, dst:Rail[Double], dst_off:UInt, count:UInt, op:Int) : void {
        allreduce_(role, src, src_off, dst, dst_off, count, op);
    }
    */
    
    public def allreduce[T] (role:UInt, src:Rail[T], src_off:UInt, dst:Rail[T], dst_off:UInt, count:UInt, op:Int) : void {
        allreduce_(role, src, src_off, dst, dst_off, count, op);
    }
    
    private def allreduce_[T] (role:UInt, src:T, op:Int) : T {
        var dst:T;
        @Native("c++",
                "int finished = 0;" +
                "x10rt_red_type type = x10rt_get_red_type<FMGL(T)>();" +
                "x10rt_allreduce(this_.FMGL(id), role, &src, &dst, (x10rt_red_op_type)op, type, 1, x10rt_one_setter, &finished);" +
                "while (!finished) x10rt_probe();") { dst = src; }
        return dst;
    }

    public def allreduce (role:UInt, src:Byte, op:Int) = allreduce_(role, src, op);
    public def allreduce (role:UInt, src:UByte, op:Int) = allreduce_(role, src, op);
    public def allreduce (role:UInt, src:Short, op:Int) = allreduce_(role, src, op);
    public def allreduce (role:UInt, src:UShort, op:Int) = allreduce_(role, src, op);
    public def allreduce (role:UInt, src:Int, op:Int) = allreduce_(role, src, op);
    public def allreduce (role:UInt, src:UInt, op:Int) = allreduce_(role, src, op);
    public def allreduce (role:UInt, src:Long, op:Int) = allreduce_(role, src, op);
    public def allreduce (role:UInt, src:ULong, op:Int) = allreduce_(role, src, op);
    public def allreduce (role:UInt, src:Float, op:Int) = allreduce_(role, src, op);
    public def allreduce (role:UInt, src:Double, op:Int) = allreduce_(role, src, op);
    
    public def indexOfMax (role:UInt, v:Double, idx:Int) : Int {
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
    
    public def indexOfMin (role:UInt, v:Double, idx:Int) : Int {
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
    
    public def split (role:UInt, color:UInt, new_role:UInt) : Team {
        var id:UInt = 0;
        @Native("c++",
                "x10rt_team nu_team = 0;" +
                "x10rt_team_split(this_.FMGL(id), role, color, new_role, x10rt_team_setter, &nu_team);" +
                "while (nu_team==0) x10rt_probe();" +
                "id = nu_team;") { }
        return Team(id);
    }
    
}

// vim: shiftwidth=4:tabstop=4:expandtab
