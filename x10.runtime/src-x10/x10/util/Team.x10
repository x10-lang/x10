package x10.util;

import x10.compiler.Native;
import x10.compiler.NativeRep;

/** Interface to low level collective operations.  A team is a collection of
 * activities that work together by simultaneously doing 'collective
 * operations', expressed as calls to methods in the Team struct.  Each member
 * of the team identifies itself using the 'role' integer, which is a value
 * from 0 to team.size() - 1.  Each member can only live at a particular place,
 * which is indicated by the user when the Team is created.
 *
 * @author Dave Cunningham
 */
public struct Team {

    /** A team that has one member at each place.
     */
    public static WORLD = Team(0);

    /** The underlying representation of a team's identity.
     */
    private id: Int;

    private def this (id:Int) { this.id = id; }

    /** Create a team by defining the place where each member lives.  This would usually be called before creating an async for each member of the team.
     * @param places The place of each member
     */
    public def this (places:Rail[Place]) {
        var id:Int = 0;
        @Native("c++",
                "x10rt_team nu_team = 0;" +
                "x10rt_team_new(places->length(), (x10rt_place*)places->raw(), x10rt_team_setter, &nu_team);" +
                "while (nu_team==0) x10rt_probe();" +
                "id = nu_team;") { }
        this.id = id;
    }

    public def this (places:Array[Place]) {
        var id:Int = 0;
        @Native("c++",
                "x10rt_team nu_team = 0;" +
                "x10rt_team_new(places->FMGL(rawLength), (x10rt_place*)places->raw()->raw(), x10rt_team_setter, &nu_team);" +
                "while (nu_team==0) x10rt_probe();" +
                "id = nu_team;") { }
        this.id = id;
    }

    /** Returns the number of elements in the team.
     */
    public def size () : Int {
        var r:Int;
        @Native("c++",
                "r = x10rt_team_sz(this_.FMGL(id));") { r = 0; }
        return r;
    }
    
    /** Blocks until all team members have reached the barrier.
     * @param role Our role in this collective operation
     */
    public def barrier (role:Int) : void {
        @Native("c++",
                "int finished = 0;" +
                "x10rt_barrier(this_.FMGL(id), role, x10rt_one_setter, &finished);" +
                "while (!finished) x10rt_probe();") {}
    }

    /** Blocks until all members have received their part of root's array.
     * Each member receives a contiguous and distinct portion of the src array.
     * src should be structured so that the portions are sorted in ascending
     * order, e.g., the first member gets the portion at offset src_off of sbuf, and the
     * last member gets the last portion.
     *
     * @param role Our role in the team
     *
     * @param root The member who is supplying the data
     *
     * @param src The data that will be sent (will only be used by the root
     * member)
     *
     * @param src_off The offset into src at which to start reading
     *
     * @param dst The rail into which the data will be received for this member
     *
     * @param dst_off The offset into dst at which to start writing
     *
     * @param count The number of elements being transferred
     */
    public def scatter[T] (role:Int, root:Int, src:Rail[T], src_off:Int, dst:Rail[T], dst_off:Int, count:Int) : void {
        @Native("c++",
                "int finished = 0;" +
                "x10rt_scatter(this_.FMGL(id), role, root, &src->raw()[src_off], &dst->raw()[dst_off], sizeof(FMGL(T)), count, x10rt_one_setter, &finished);" +
                "while (!finished) x10rt_probe();") {}
    }
    public def scatter[T] (role:Int, root:Int, src:Array[T], src_off:Int, dst:Array[T], dst_off:Int, count:Int) : void {
        @Native("c++",
                "int finished = 0;" +
                "x10rt_scatter(this_.FMGL(id), role, root, &src->raw()->raw()[src_off], &dst->raw()->raw()[dst_off], sizeof(FMGL(T)), count, x10rt_one_setter, &finished);" +
                "while (!finished) x10rt_probe();") {}
    }
    
    /** Blocks until all members have received root's array.
     *
     * @param role Our role in the team
     *
     * @param root The member who is supplying the data
     *
     * @param src The data that will be sent (will only be used by the root member)
     *
     * @param src_off The offset into src at which to start reading
     *
     * @param dst The rail into which the data will be received for this member
     *
     * @param dst_off The offset into dst at which to start writing
     *
     * @param count The number of elements being transferred
     */
    public def bcast[T] (role:Int, root:Int, src:Rail[T], src_off:Int, dst:Rail[T], dst_off:Int, count:Int) : void {
        @Native("c++",
                "int finished = 0;" +
                "x10rt_bcast(this_.FMGL(id), role, root, &src->raw()[src_off], &dst->raw()[dst_off], sizeof(FMGL(T)), count, x10rt_one_setter, &finished);" +
                "while (!finished) x10rt_probe();") {}
    }
    public def bcast[T] (role:Int, root:Int, src:Array[T], src_off:Int, dst:Array[T], dst_off:Int, count:Int) : void {
        @Native("c++",
                "int finished = 0;" +
                "x10rt_bcast(this_.FMGL(id), role, root, &src->raw()->raw()[src_off], &dst->raw()->raw()[dst_off], sizeof(FMGL(T)), count, x10rt_one_setter, &finished);" +
                "while (!finished) x10rt_probe();") {}
    }
    
    /** Blocks until all members have received their part of each other member's array.
     * Each member receives a contiguous and distinct portion of the src array.
     * src should be structured so that the portions are sorted in ascending
     * order, e.g., the first member gets the portion at offset src_off of sbuf, and the
     * last member gets the last portion.
     *
     * @param role Our role in the team
     *
     * @param src The data that will be sent (will only be used by the root
     * member)
     *
     * @param src_off The offset into src at which to start reading
     *
     * @param dst The rail into which the data will be received for this member
     *
     * @param dst_off The offset into dst at which to start writing
     *
     * @param count The number of elements being transferred
     */
    public def alltoall[T] (role:Int, src:Rail[T], src_off:Int, dst:Rail[T], dst_off:Int, count:Int) : void {
        @Native("c++",
                "int finished = 0;" +
                "x10rt_alltoall(this_.FMGL(id), role, &src->raw()[src_off], &dst->raw()[dst_off], sizeof(FMGL(T)), count, x10rt_one_setter, &finished);" +
                "while (!finished) x10rt_probe();") {}
    }
    public def alltoall[T] (role:Int, src:Array[T], src_off:Int, dst:Array[T], dst_off:Int, count:Int) : void {
        @Native("c++",
                "int finished = 0;" +
                "x10rt_alltoall(this_.FMGL(id), role, &src->raw()->raw()[src_off], &dst->raw()->raw()[dst_off], sizeof(FMGL(T)), count, x10rt_one_setter, &finished);" +
                "while (!finished) x10rt_probe();") {}
    }

    /** Indicates the operation to perform when reducing. */
    public static val ADD = 0;
    /** Indicates the operation to perform when reducing. */
    public static val MUL = 1;
    /** Indicates the operation to perform when reducing. */
    public static val DIV = 2;
    /** Indicates the operation to perform when reducing. */
    public static val AND = 3;
    /** Indicates the operation to perform when reducing. */
    public static val OR  = 4;
    /** Indicates the operation to perform when reducing. */
    public static val XOR = 5;
    /** Indicates the operation to perform when reducing. */
    public static val MAX = 6;
    /** Indicates the operation to perform when reducing. */
    public static val MIN = 7;

    private def allreduce_[T] (role:Int, src:Rail[T], src_off:Int, dst:Rail[T], dst_off:Int, count:Int, op:Int) : void {
        @Native("c++",
                "int finished = 0;" +
                "x10rt_red_type type = x10rt_get_red_type<FMGL(T)>();" +
                "x10rt_allreduce(this_.FMGL(id), role, &src->raw()[src_off], &dst->raw()[dst_off], (x10rt_red_op_type)op, type, count, x10rt_one_setter, &finished);" +
                "while (!finished) x10rt_probe();") {}
    }
    private def allreduce_[T] (role:Int, src:Array[T], src_off:Int, dst:Array[T], dst_off:Int, count:Int, op:Int) : void {
        @Native("c++",
                "int finished = 0;" +
                "x10rt_red_type type = x10rt_get_red_type<FMGL(T)>();" +
                "x10rt_allreduce(this_.FMGL(id), role, &src->raw()->raw()[src_off], &dst->raw()->raw()[dst_off], (x10rt_red_op_type)op, type, count, x10rt_one_setter, &finished);" +
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

    /** Blocks until all members have received the computed result.  Note that not all values of T are valid.
     * The dst array is populated for all members with the result of the operation applied pointwise to all given src arrays.
     *
     * @param role Our role in the team
     *
     * @param src The data that will be sent (will only be used by the root
     * member)
     *
     * @param src_off The offset into src at which to start reading
     *
     * @param dst The rail into which the data will be received for this member
     *
     * @param dst_off The offset into dst at which to start writing
     *
     * @param count The number of elements being transferred
     *
     * @param op The operation to perform
     */
    public def allreduce[T] (role:Int, src:Rail[T], src_off:Int, dst:Rail[T], dst_off:Int, count:Int, op:Int) : void {
        allreduce_(role, src, src_off, dst, dst_off, count, op);
    }

    public def allreduce[T] (role:Int, src:Array[T], src_off:Int, dst:Array[T], dst_off:Int, count:Int, op:Int) : void {
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

    /** Performs a reduction on a single value, returning the result */
    public def allreduce (role:Int, src:Byte, op:Int) = allreduce_(role, src, op);
    /** Performs a reduction on a single value, returning the result */
    public def allreduce (role:Int, src:UByte, op:Int) = allreduce_(role, src, op);
    /** Performs a reduction on a single value, returning the result */
    public def allreduce (role:Int, src:Short, op:Int) = allreduce_(role, src, op);
    /** Performs a reduction on a single value, returning the result */
    public def allreduce (role:Int, src:UShort, op:Int) = allreduce_(role, src, op);
    /** Performs a reduction on a single value, returning the result */
    public def allreduce (role:Int, src:UInt, op:Int) = allreduce_(role, src, op);
    /** Performs a reduction on a single value, returning the result */
    public def allreduce (role:Int, src:Int, op:Int) = allreduce_(role, src, op);
    /** Performs a reduction on a single value, returning the result */
    public def allreduce (role:Int, src:Long, op:Int) = allreduce_(role, src, op);
    /** Performs a reduction on a single value, returning the result */
    public def allreduce (role:Int, src:ULong, op:Int) = allreduce_(role, src, op);
    /** Performs a reduction on a single value, returning the result */
    public def allreduce (role:Int, src:Float, op:Int) = allreduce_(role, src, op);
    /** Performs a reduction on a single value, returning the result */
    public def allreduce (role:Int, src:Double, op:Int) = allreduce_(role, src, op);
    
    /** Returns the index of the biggest double value across the team */
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
    
    /** Returns the index of the smallest double value across the team */
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
    
    /** Create new teams by subdividing an existing team.  This is called by each member
     * of an existing team, indicating which of the new teams it will be a member of, and its role
     * within that team.  The old team is still available after this call.  All the members
     * of the old team must collectively assign themselves to new teams such that there is exactly 1
     * member of the original team for each role of each new team.  It is undefined behaviour if two
     * members of the original team decide to play the same role in one of the new teams, or if one of
     * the roles of a new team is left unfilled.
     *
     * @param role The caller's role within the old team
     *
     * @param color The new team, must be a number between 0 and the number of new teams - 1
     *
     * @param new_role The caller's role within the new team
     */
    public def split (role:Int, color:Int, new_role:Int) : Team {
        var id:Int = 0;
        @Native("c++",
                "x10rt_team nu_team = 0;" +
                "x10rt_team_split(this_.FMGL(id), role, color, new_role, x10rt_team_setter, &nu_team);" +
                "while (nu_team==0) x10rt_probe();" +
                "id = nu_team;") { }
        return Team(id);
    }
    
    /** Destroy a team that is no-longer needed.  Called simultaneously by each member of
     * the team.  There should be no operations on the team after this.
     *
     * @param role Our role in this team
     */
    public def del (role:Int) : void {
        @Native("c++",
                "int finished = 0;" +
                "x10rt_team_del(this_.FMGL(id), role, x10rt_one_setter, &finished);" +
                "while (!finished) x10rt_probe();") { }
    }
    
}

// vim: shiftwidth=4:tabstop=4:expandtab
