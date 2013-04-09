/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.util;

import x10.compiler.Native;

/** Interface to low level collective operations.  A team is a collection of
 * activities that work together by simultaneously doing 'collective
 * operations', expressed as calls to methods in the Team struct.  Each member
 * of the team identifies itself using the 'role' integer, which is a value
 * from 0 to team.size() - 1.  Each member can only live at a particular place,
 * which is indicated by the user when the Team is created.
 */
public struct Team {

    private static struct DoubleIdx(value:Double, idx:Int) {}

    /** A team that has one member at each place.
     */
    public static WORLD = Team(0);

    /** The underlying representation of a team's identity.
     */
    private id: Int;

    public def id() = id;

    private def this (id:Int) { this.id = id; }

    /** Create a team by defining the place where each member lives.  This would usually be called before creating an async for each member of the team.
     * @param places The place of each member
     */
    public def this (places:Rail[Place]) {
        val result = new Rail[Int](1);
        val count = places.size as Int; // LONG_RAIL
        finish nativeMake(places, count, result);
        id = result(0);
    }

    private static def nativeMake (places:Rail[Place], count:Int, result:Rail[Int]) : void {
        @Native("java", "x10.x10rt.TeamSupport.nativeMake(places, count, result);")
    	@Native("c++", "x10rt_team_new(count, (x10rt_place*)places->raw, x10aux::coll_handler2, x10aux::coll_enter2(result->raw));") {}
    }

    /** Returns the number of elements in the team.
     */
    public def size () : Int = nativeSize(id);

    private static def nativeSize (id:Int) : Int {
        @Native("java", "return x10.x10rt.TeamSupport.nativeSize(id);")
        @Native("c++", "return (x10_int)x10rt_team_sz(id);") { return -1; }
    }

    /** Blocks until all team members have reached the barrier.
     * @param role Our role in this collective operation
     */
    public def barrier (role:Int) : void {
        finish nativeBarrier(id, role);
    }

    private static def nativeBarrier (id:int, role:Int) : void {
        @Native("java", "x10.x10rt.TeamSupport.nativeBarrier(id, role);")
        @Native("c++", "x10rt_barrier(id, role, x10aux::coll_handler, x10aux::coll_enter());") {}
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
        finish nativeScatter(id, role, root, src, src_off, dst, dst_off, count);
    }

    private static def nativeScatter[T] (id:Int, role:Int, root:Int, src:Rail[T], src_off:Int, dst:Rail[T], dst_off:Int, count:Int) : void {
        @Native("java", "x10.x10rt.TeamSupport.nativeScatter(id, role, root, src, src_off, dst, dst_off, count);")
        @Native("c++", "x10rt_scatter(id, role, root, &src->raw[src_off], &dst->raw[dst_off], sizeof(TPMGL(T)), count, x10aux::coll_handler, x10aux::coll_enter());") {}
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
        finish nativeBcast(id, role, root, src, src_off, dst, dst_off, count);
    }

    private static def nativeBcast[T] (id:Int, role:Int, root:Int, src:Rail[T], src_off:Int, dst:Rail[T], dst_off:Int, count:Int) : void {
        @Native("java", "x10.x10rt.TeamSupport.nativeBcast(id, role, root, src, src_off, dst, dst_off, count);")
        @Native("c++", "x10rt_bcast(id, role, root, &src->raw[src_off], &dst->raw[dst_off], sizeof(TPMGL(T)), count, x10aux::coll_handler, x10aux::coll_enter());") {}
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
        finish nativeAlltoall(id, role, src, src_off, dst, dst_off, count);
    }
    
    private static def nativeAlltoall[T](id:Int, role:Int, src:Rail[T], src_off:Int, dst:Rail[T], dst_off:Int, count:Int) : void {
        @Native("java", "x10.x10rt.TeamSupport.nativeAllToAll(id, role, src, src_off, dst, dst_off, count);")
        @Native("c++", "x10rt_alltoall(id, role, &src->raw[src_off], &dst->raw[dst_off], sizeof(TPMGL(T)), count, x10aux::coll_handler, x10aux::coll_enter());") {}
    }

    /** Indicates the operation to perform when reducing. */
    public static val ADD = 0;
    /** Indicates the operation to perform when reducing. */
    public static val MUL = 1;
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

    /* using overloading is the correct thing to do here since the set of supported
     * types are finite, however the java backend will not be able to distinguish
     * these methods' prototypes so we use the unsafe generic approach for now.
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
    public def reduce[T] (role:Int, root:Int, src:Rail[T], src_off:Int, dst:Rail[T], dst_off:Int, count:Int, op:Int) : void {
        finish nativeReduce(id, role, root, src, src_off, dst, dst_off, count, op);
	}
	
    private static def nativeReduce[T](id:Int, role:Int, root:Int, src:Rail[T], src_off:Int, dst:Rail[T], dst_off:Int, count:Int, op:Int) : void {
        @Native("java", "x10.x10rt.TeamSupport.nativeReduce(id, role, root, src, src_off, dst, dst_off, count, op);")
    	@Native("c++", "x10rt_reduce(id, role, root, &src->raw[src_off], &dst->raw[dst_off], (x10rt_red_op_type)op, x10rt_get_red_type<TPMGL(T)>(), count, x10aux::coll_handler, x10aux::coll_enter());") {}
    }

    /** Performs a reduction on a single value, returning the result at the root */
    public def reduce (role:Int, root:Int, src:Byte, op:Int) = genericReduce(role, root, src, op);
    /** Performs a reduction on a single value, returning the result at the root */
    public def reduce (role:Int, root:Int, src:UByte, op:Int) = genericReduce(role, root, src, op);
    /** Performs a reduction on a single value, returning the result at the root */
    public def reduce (role:Int, root:Int, src:Short, op:Int) = genericReduce(role, root, src, op);
    /** Performs a reduction on a single value, returning the result at the root */
    public def reduce (role:Int, root:Int, src:UShort, op:Int) = genericReduce(role, root, src, op);
    /** Performs a reduction on a single value, returning the result at the root */
    public def reduce (role:Int, root:Int, src:UInt, op:Int) = genericReduce(role, root, src, op);
    /** Performs a reduction on a single value, returning the result at the root */
    public def reduce (role:Int, root:Int, src:Int, op:Int) = genericReduce(role, root, src, op);
    /** Performs a reduction on a single value, returning the result at the root */
    public def reduce (role:Int, root:Int, src:Long, op:Int) = genericReduce(role, root, src, op);
    /** Performs a reduction on a single value, returning the result at the root */
    public def reduce (role:Int, root:Int, src:ULong, op:Int) = genericReduce(role, root, src, op);
    /** Performs a reduction on a single value, returning the result at the root */
    public def reduce (role:Int, root:Int, src:Float, op:Int) = genericReduce(role, root, src, op);
    /** Performs a reduction on a single value, returning the result at the root */
    public def reduce (role:Int, root:Int, src:Double, op:Int) = genericReduce(role, root, src, op);

    private def genericReduce[T] (role:Int, root:Int, src:T, op:Int) : T {
        val chk = new Rail[T](1, src);
        val dst = new Rail[T](1, src);
        finish nativeReduce[T](id, role, root, chk, dst, op);
        return dst(0);
    }

    private static def nativeReduce[T](id:Int, role:Int, root:Int, src:Rail[T], dst:Rail[T], op:Int) : void {
        @Native("java", "x10.x10rt.TeamSupport.nativeReduce(id, role, root, src, 0, dst, 0, 1, op);")
        @Native("c++", "x10rt_reduce(id, role, root, src->raw, dst->raw, (x10rt_red_op_type)op, x10rt_get_red_type<TPMGL(T)>(), 1, x10aux::coll_handler, x10aux::coll_enter());") {}
    }

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
        finish nativeAllreduce(id, role, src, src_off, dst, dst_off, count, op);
    }

    private static def nativeAllreduce[T](id:Int, role:Int, src:Rail[T], src_off:Int, dst:Rail[T], dst_off:Int, count:Int, op:Int) : void {
        @Native("java", "x10.x10rt.TeamSupport.nativeAllReduce(id, role, src, src_off, dst, dst_off, count, op);")
    	@Native("c++", "x10rt_allreduce(id, role, &src->raw[src_off], &dst->raw[dst_off], (x10rt_red_op_type)op, x10rt_get_red_type<TPMGL(T)>(), count, x10aux::coll_handler, x10aux::coll_enter());") {}
    }

    /** Performs a reduction on a single value, returning the result */
    public def allreduce (role:Int, src:Byte, op:Int) = genericAllreduce(role, src, op);
    /** Performs a reduction on a single value, returning the result */
    public def allreduce (role:Int, src:UByte, op:Int) = genericAllreduce(role, src, op);
    /** Performs a reduction on a single value, returning the result */
    public def allreduce (role:Int, src:Short, op:Int) = genericAllreduce(role, src, op);
    /** Performs a reduction on a single value, returning the result */
    public def allreduce (role:Int, src:UShort, op:Int) = genericAllreduce(role, src, op);
    /** Performs a reduction on a single value, returning the result */
    public def allreduce (role:Int, src:UInt, op:Int) = genericAllreduce(role, src, op);
    /** Performs a reduction on a single value, returning the result */
    public def allreduce (role:Int, src:Int, op:Int) = genericAllreduce(role, src, op);
    /** Performs a reduction on a single value, returning the result */
    public def allreduce (role:Int, src:Long, op:Int) = genericAllreduce(role, src, op);
    /** Performs a reduction on a single value, returning the result */
    public def allreduce (role:Int, src:ULong, op:Int) = genericAllreduce(role, src, op);
    /** Performs a reduction on a single value, returning the result */
    public def allreduce (role:Int, src:Float, op:Int) = genericAllreduce(role, src, op);
    /** Performs a reduction on a single value, returning the result */
    public def allreduce (role:Int, src:Double, op:Int) = genericAllreduce(role, src, op);

    private def genericAllreduce[T] (role:Int, src:T, op:Int) : T {
        val chk = new Rail[T](1, src);
        val dst = new Rail[T](1, src);
        finish nativeAllreduce[T](id, role, chk, dst, op);
        return dst(0);
    }

    private static def nativeAllreduce[T](id:Int, role:Int, src:Rail[T], dst:Rail[T], op:Int) : void {
        @Native("java", "x10.x10rt.TeamSupport.nativeAllReduce(id, role, src, 0, dst, 0, 1, op);")
        @Native("c++", "x10rt_allreduce(id, role, src->raw, dst->raw, (x10rt_red_op_type)op, x10rt_get_red_type<TPMGL(T)>(), 1, x10aux::coll_handler, x10aux::coll_enter());") {}
    }

    /** Returns the index of the biggest double value across the team */
    public def indexOfMax (role:Int, v:Double, idx:Int) : Int {
        val src = new Rail[DoubleIdx](1, DoubleIdx(v, idx));
        val dst = new Rail[DoubleIdx](1);
        finish nativeIndexOfMax(id, role, src, dst);
        return dst(0).idx;
    }

    private static def nativeIndexOfMax(id:Int, role:Int, src:Rail[DoubleIdx], dst:Rail[DoubleIdx]) : void {
        @Native("java", "x10.x10rt.TeamSupport.nativeIndexOfMax(id, role, src, dst);")
        @Native("c++", "x10rt_allreduce(id, role, src->raw, dst->raw, X10RT_RED_OP_MAX, X10RT_RED_TYPE_DBL_S32, 1, x10aux::coll_handler, x10aux::coll_enter());") {}
    }

    /** Returns the index of the smallest double value across the team */
    public def indexOfMin (role:Int, v:Double, idx:Int) : Int {
        val src = new Rail[DoubleIdx](1, DoubleIdx(v, idx));
        val dst = new Rail[DoubleIdx](1);
        finish nativeIndexOfMin(id, role, src, dst);
        return dst(0).idx;
    }

    private static def nativeIndexOfMin(id:Int, role:Int, src:Rail[DoubleIdx], dst:Rail[DoubleIdx]) : void {
        @Native("java", "x10.x10rt.TeamSupport.nativeIndexOfMin(id, role, src, dst);")
        @Native("c++", "x10rt_allreduce(id, role, src->raw, dst->raw, X10RT_RED_OP_MIN, X10RT_RED_TYPE_DBL_S32, 1, x10aux::coll_handler, x10aux::coll_enter());") {}
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
        val result = new Rail[Int](1);
        finish nativeSplit(id, role, color, new_role, result);
        return Team(result(0));
    }

    private static def nativeSplit(id:Int, role:Int, color:Int, new_role:Int, result:Rail[Int]) : void {
        @Native("java", "x10.x10rt.TeamSupport.nativeSplit(id, role, color, new_role, result);")
        @Native("c++", "x10rt_team_split(id, role, color, new_role, x10aux::coll_handler2, x10aux::coll_enter2(result->raw));") {}
    }

    /** Destroy a team that is no-longer needed.  Called simultaneously by each member of
     * the team.  There should be no operations on the team after this.
     *
     * @param role Our role in this team
     */
    public def del (role:Int) : void {
        if (this == WORLD) throw new IllegalArgumentException("Cannot delete Team.WORLD");
        finish nativeDel(id, role);
    }

    private static def nativeDel(id:Int, role:Int) : void {
        @Native("java", "x10.x10rt.TeamSupport.nativeDel(id, role);")
        @Native("c++", "x10rt_team_del(id, role, x10aux::coll_handler, x10aux::coll_enter());") {}
    }

    public def toString() = "Team(" + this.id + ")";
    public def equals(that:Team) = that.id==this.id;
    public def equals(that:Any) = that instanceof Team && (that as Team).id==this.id;
    public def hashCode()=id;

}

// vim: shiftwidth=4:tabstop=4:expandtab
