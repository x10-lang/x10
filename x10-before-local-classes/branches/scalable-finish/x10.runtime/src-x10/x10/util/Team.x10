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
                "x10rt_barrier(this_.FMGL(id), role, root, &src->raw()[src_off], &dst->raw()[dst_off], count*sizeof(FMGL(T)), x10rt_one_setter, &finished);" +
                "while (!finished) x10rt_probe();") {}
    }
    
    public def alltoall[T] (role:UInt, src:Rail[T], src_off:UInt, dst:Rail[T], dst_off:UInt, count:UInt) : void {
        @Native("c++",
                "int finished = 0;" +
                "x10rt_alltoall(this_.FMGL(id), role, &src->raw()[src_off], &dst->raw()[dst_off], count*sizeof(FMGL(T)), x10rt_one_setter, &finished);" +
                "while (!finished) x10rt_probe();") {}
    }
    
    public def allreduce[T] (role:UInt, src:Rail[T], src_off:UInt, dst:Rail[T], dst_off:UInt, count:UInt) : void {
        @Native("c++",
                "int finished = 0;" +
                "x10rt_red_op_type op = X10RT_RED_OP_ADD;" +
                "x10rt_red_type type = x10rt_get_red_type<FMGL(T)>;" +
                "x10rt_alltoall(this_.FMGL(id), role, &src->raw()[src_off], &dst->raw()[dst_off], op, type, count*sizeof(FMGL(T)), x10rt_one_setter, &finished);" +
                "while (!finished) x10rt_probe();") {}
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
