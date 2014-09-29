/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.util;

import x10.compiler.Native;
import x10.compiler.NoInline;
import x10.util.concurrent.AtomicInteger;
import x10.util.concurrent.Lock;
import x10.compiler.Pragma;

/**
 * A team is a collection of activities that work together by simultaneously 
 * doing 'collective operations', expressed as calls to methods in the Team struct.
 * All methods are blocking operations and must be called by each member of the
 * team before the collective operation can progress.
 */
public struct Team {
    private static struct DoubleIdx(value:Double, idx:Int) {}
    private static val DEBUG:Boolean = false;
    private static val DEBUGINTERNALS:Boolean = false;

    /** A team that has one member at each place. */
    public static val WORLD = Team(0n, Place.places(), here.id());
    
    // TODO: the role argument is not really needed, and can be buried in lower layers, 
    // but BG/P is difficult to modify so we need to track it for now
    private static val roles:GrowableRail[Int] = new GrowableRail[Int](); // only used with native collectives
    private static val state:GrowableRail[LocalTeamState] = new GrowableRail[LocalTeamState](); // only used with X10 emulated collectives

    private val collectiveSupportLevel:Int; // what level of collectives are supported
    // these values correspond to x10rt_types:x10rt_coll_support
    private static val X10RT_COLL_NOCOLLECTIVES:Int = 0n;
    private static val X10RT_COLL_BARRIERONLY:Int = 1n;
    private static val X10RT_COLL_ALLBLOCKINGCOLLECTIVES:Int = 2n;
    private static val X10RT_COLL_NONBLOCKINGBARRIER:Int = 3n;
    private static val X10RT_COLL_ALLNONBLOCKINGCOLLECTIVES:Int = 4n;

    public static FORCE_X10_COLLECTIVES = (System.getenv("X10RT_X10_FORCE_COLLECTIVES") != null);
    
    private val id:Int; // team ID
    public def id() = id;
    
    // this constructor is intended to be called at all places of a split, at the same time.
    private def this (id:Int, places:PlaceGroup, role:Long) {
        this.id = id;
        collectiveSupportLevel = collectiveSupport();
        if (DEBUG) Runtime.println(here + " reported native collective support level of " + collectiveSupportLevel);
        if (collectiveSupportLevel > X10RT_COLL_NOCOLLECTIVES) {
            if (Team.roles.capacity() <= id) // TODO move this check into the GrowableRail.grow() method
                Team.roles.grow(id+1);
            while (Team.roles.size() < id)
                Team.roles.add(-1n); // I am not a member of this team id.  Insert a dummy value.
            Team.roles(id) = role as Int;
            if (DEBUG) Runtime.println(here + " created native team "+id);
        }
        if (collectiveSupportLevel < X10RT_COLL_ALLNONBLOCKINGCOLLECTIVES) {
            if (DEBUG) Runtime.println(here + " creating our own team "+id);
            if (Team.state.capacity() <= id) // TODO move this check into the GrowableRail.grow() method
                Team.state.grow(id+1);
            while (Team.state.size() < id)
                Team.state.add(null); // I am not a member of this team id.  Insert a dummy value.
            val teamState = new LocalTeamState(places, id, places.indexOf(here));
            if (id == 0n) {
                // Team.WORLD is constructed by each place during Runtime.start()
                Team.state(id) = teamState;
            } else {
                atomic { Team.state(id) = teamState; }
                teamState.init();
            }
            if (DEBUG) Runtime.println(here + " created our own team "+id);
        }
    }

    /** 
     * Create a team by defining the place where each member lives.
     * Unlike most methods on Team, this is called by only ONE place, not all places
     * @param places The place of each member in the team
     */
    public def this (places:PlaceGroup) {
        if (DEBUG) Runtime.println(here + " creating new team ");
        collectiveSupportLevel = collectiveSupport();
        if (DEBUG) Runtime.println(here + " reported native collective support level of " + collectiveSupportLevel);
        if (collectiveSupportLevel > X10RT_COLL_NOCOLLECTIVES) {
            val result = new Rail[Int](1);
            val count = places.size();
            // CRITICAL!! placeRail is a Rail of Int because in x10rt "x10rt_place" is 32bits
            val placeRail = new Rail[Int](count);
            for (var i:Long=0; i<count; i++)
                placeRail(i) = places(i).id() as Int;
            finish nativeMake(placeRail, count as Int, result);
            this.id = result(0);
            
            // team created - fill in the role at all places
            val teamidcopy:Long = this.id as Long;
            Place.places().broadcastFlat(()=>{
                if (Team.roles.capacity() <= teamidcopy) // TODO move this check into the GrowableRail.grow() method
                    Team.roles.grow(teamidcopy+1);
                while (Team.roles.size() < teamidcopy)
                    Team.roles.add(-1n); // I am not a member of this team id.  Insert a dummy value.
                   Team.roles(teamidcopy) = places.indexOf(here) as Int;
            });
        } else {
            this.id = Team.state.size() as Int; // id is determined by the number of pre-defined places
        }
        if (DEBUG) Runtime.println(here + " new team ID is "+this.id);
        if (collectiveSupportLevel < X10RT_COLL_ALLNONBLOCKINGCOLLECTIVES) {
            val teamidcopy = this.id;
            Place.places().broadcastFlat(()=>{
                if (Team.state.capacity() <= teamidcopy)
                    Team.state.grow(teamidcopy+1);
                while (Team.state.size() < teamidcopy)
                    Team.state.add(null); // I am not a member of this team id.  Insert a dummy value.
                Team.state(teamidcopy) = new LocalTeamState(places, teamidcopy, places.indexOf(here));
                Team.state(teamidcopy).init();
            });
        }
    }

    private static def nativeMake (places:Rail[Int], count:Int, result:Rail[Int]) : void {
        @Native("java", "x10.x10rt.TeamSupport.nativeMake(places, count, result);")
        @Native("c++", "x10rt_team_new(count, (x10rt_place*)places->raw, ::x10aux::coll_handler2, ::x10aux::coll_enter2(result->raw));") {}
    }

    private static def collectiveSupport():Int {
        if (FORCE_X10_COLLECTIVES) return X10RT_COLL_NOCOLLECTIVES;
        else return nativeCollectiveSupport();
    }
    
    private static def nativeCollectiveSupport() : Int {
        @Native("java", "return x10.x10rt.X10RT.collectiveSupport();")
        @Native("c++", "return x10rt_coll_support();") { return -1n; }
    }

    /** Returns the number of places in the team. */
    public def size () : Long {
        if (collectiveSupportLevel >= X10RT_COLL_ALLNONBLOCKINGCOLLECTIVES)
            return nativeSize(id);
        else
            return Team.state(id).places.size();
    }

    private static def nativeSize (id:Int) : Int {
        @Native("java", "return x10.x10rt.TeamSupport.nativeSize(id);")
        @Native("c++", "return (x10_int)x10rt_team_sz(id);") { return -1n; }
    }

    /** Blocks until all team members have reached the barrier. */
    public def barrier () : void {
        if (collectiveSupportLevel >= X10RT_COLL_NONBLOCKINGBARRIER) {
            if (DEBUG) Runtime.println(here + " entering native barrier on team "+id);
            finish nativeBarrier(id, (id==0n?here.id() as Int:Team.roles(id)));
        }
        else {
            if (DEBUG) Runtime.println(here + " entering Team.x10 barrier on team "+id);
            state(id).collective_impl[Int](LocalTeamState.COLL_BARRIER, Place.FIRST_PLACE, null, 0, null, 0, 0, 0n);
        }
        if (DEBUG) Runtime.println(here + " leaving barrier of team "+id);
    }
    
    /** @deprecated use {@link barrier() instead} */
    public def nativeBarrier () : void {
        finish nativeBarrier(id, (id==0n?here.id() as Int:Team.roles(id)));
    }

    private static def nativeBarrier (id:Int, role:Int) : void {
        @Native("java", "x10.x10rt.TeamSupport.nativeBarrier(id, role);")
        @Native("c++", "x10rt_barrier(id, role, ::x10aux::coll_handler, ::x10aux::coll_enter());") {}
    }

    /** Blocks until all members have received their part of root's array.
     * Each member receives a contiguous and distinct portion of the src array.
     * src should be structured so that the portions are sorted in ascending
     * order, e.g., the first member gets the portion at offset src_off of sbuf, and the
     * last member gets the last portion.
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
    public def scatter[T] (root:Place, src:Rail[T], src_off:Long, dst:Rail[T], dst_off:Long, count:Long) : void {
        if (collectiveSupportLevel == X10RT_COLL_ALLNONBLOCKINGCOLLECTIVES)
            finish nativeScatter(id, id==0n?here.id() as Int:Team.roles(id), root.id() as Int, src, src_off as Int, dst, dst_off as Int, count as Int);
        else if (collectiveSupportLevel == X10RT_COLL_ALLBLOCKINGCOLLECTIVES || collectiveSupportLevel == X10RT_COLL_NONBLOCKINGBARRIER) {
            barrier();
            finish nativeScatter(id, id==0n?here.id() as Int:Team.roles(id), root.id() as Int, src, src_off as Int, dst, dst_off as Int, count as Int);
        }
        else
            state(id).collective_impl[T](LocalTeamState.COLL_SCATTER, root, src, src_off, dst, dst_off, count, 0n);
    }

    private static def nativeScatter[T] (id:Int, role:Int, root:Int, src:Rail[T], src_off:Int, dst:Rail[T], dst_off:Int, count:Int) : void {
        @Native("java", "x10.x10rt.TeamSupport.nativeScatter(id, role, root, src, src_off, dst, dst_off, count);")
        @Native("c++", "x10rt_scatter(id, role, root, &src->raw[src_off], &dst->raw[dst_off], sizeof(TPMGL(T)), count, ::x10aux::coll_handler, ::x10aux::coll_enter());") {}
    }

    /** Blocks until all members have received root's array.
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
     public def bcast[T] (root:Place, src:Rail[T], src_off:Long, dst:Rail[T], dst_off:Long, count:Long) : void {
        if (collectiveSupportLevel == X10RT_COLL_ALLNONBLOCKINGCOLLECTIVES)
            finish nativeBcast(id, id==0n?here.id() as Int:Team.roles(id), root.id() as Int, src, src_off as Int, dst, dst_off as Int, count as Int);
        else if (collectiveSupportLevel == X10RT_COLL_ALLBLOCKINGCOLLECTIVES || collectiveSupportLevel == X10RT_COLL_NONBLOCKINGBARRIER) {
            barrier();
            finish nativeBcast(id, id==0n?here.id() as Int:Team.roles(id), root.id() as Int, src, src_off as Int, dst, dst_off as Int, count as Int);
        }
         else
             state(id).collective_impl[T](LocalTeamState.COLL_BROADCAST, root, src, src_off, dst, dst_off, count, 0n);
    }

    private static def nativeBcast[T] (id:Int, role:Int, root:Int, src:Rail[T], src_off:Int, dst:Rail[T], dst_off:Int, count:Int) : void {
        @Native("java", "x10.x10rt.TeamSupport.nativeBcast(id, role, root, src, src_off, dst, dst_off, count);")
        @Native("c++", "x10rt_bcast(id, role, root, &src->raw[src_off], &dst->raw[dst_off], sizeof(TPMGL(T)), count, ::x10aux::coll_handler, ::x10aux::coll_enter());") {}
    }

    /** Blocks until all members have received their part of each other member's array.
     * Each member receives a contiguous and distinct portion of the src array.
     * src should be structured so that the portions are sorted in ascending
     * order, e.g., the first member gets the portion at offset src_off of sbuf, and the
     * last member gets the last portion.
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
    public def alltoall[T] (src:Rail[T], src_off:Long, dst:Rail[T], dst_off:Long, count:Long) : void {
        if (collectiveSupportLevel == X10RT_COLL_ALLNONBLOCKINGCOLLECTIVES) {
            if (DEBUG) Runtime.println(here + " entering native alltoall of team "+id);
            finish nativeAlltoall(id, id==0n?here.id() as Int:Team.roles(id), src, src_off as Int, dst, dst_off as Int, count as Int);
        } else /*if (collectiveSupportLevel == X10RT_COLL_ALLBLOCKINGCOLLECTIVES || collectiveSupportLevel == X10RT_COLL_NONBLOCKINGBARRIER) */ {
            if (DEBUG) Runtime.println(here + " entering pre-alltoall barrier of team "+id);
            barrier();
            if (DEBUG) Runtime.println(here + " entering native alltoall of team "+id);
            finish nativeAlltoall(id, id==0n?here.id() as Int:Team.roles(id), src, src_off as Int, dst, dst_off as Int, count as Int);
        }
// XTENLANG-3434 X10 alltoall is broken
/*
        else {
            if (DEBUG) Runtime.println(here + " entering Team.x10 alltoall of team "+id);
            state(id).collective_impl[T](LocalTeamState.COLL_ALLTOALL, Place.FIRST_PLACE, src, src_off, dst, dst_off, count, 0n);
        }
*/
        if (DEBUG) Runtime.println(here + " leaving alltoall of team "+id);
    }
    
    private static def nativeAlltoall[T](id:Int, role:Int, src:Rail[T], src_off:Int, dst:Rail[T], dst_off:Int, count:Int) : void {
        @Native("java", "x10.x10rt.TeamSupport.nativeAllToAll(id, role, src, src_off, dst, dst_off, count);")
        @Native("c++", "x10rt_alltoall(id, role, &src->raw[src_off], &dst->raw[dst_off], sizeof(TPMGL(T)), count, ::x10aux::coll_handler, ::x10aux::coll_enter());") {}
    }

    /** Indicates the operation to perform when reducing. */
    public static val ADD = 0n;
    /** Indicates the operation to perform when reducing. */
    public static val MUL = 1n;
    /** Indicates the operation to perform when reducing. */
    public static val AND = 3n;
    /** Indicates the operation to perform when reducing. */
    public static val OR  = 4n;
    /** Indicates the operation to perform when reducing. */
    public static val XOR = 5n;
    /** Indicates the operation to perform when reducing. */
    public static val MAX = 6n;
    /** Indicates the operation to perform when reducing. */
    public static val MIN = 7n;

    /* using overloading is the correct thing to do here since the set of supported
     * types are finite, however the java backend will not be able to distinguish
     * these methods' prototypes so we use the unsafe generic approach for now.
     */

    /** Blocks until all members have received the computed result.  Note that not all values of T are valid.
     *
     * @param root Which place will recieve the reduced value(s)
     * 
     * @param src The data that will be sent 
     *
     * @param src_off The offset into src at which to start reading
     *
     * @param dst The rail into which the data will be received for (will only be used by the root member)
     *
     * @param dst_off The offset into dst at which to start writing (will only be used by the root member)
     *
     * @param count The number of elements being transferred
     *
     * @param op The operation to perform
     */
    public def reduce[T] (root:Place, src:Rail[T], src_off:Long, dst:Rail[T], dst_off:Long, count:Long, op:Int) : void {
        if (collectiveSupportLevel == X10RT_COLL_ALLNONBLOCKINGCOLLECTIVES) {
            finish nativeReduce(id, id==0n?here.id() as Int:Team.roles(id), root.id() as Int, src, src_off as Int, dst, dst_off as Int, count as Int, op);
        } else if (collectiveSupportLevel == X10RT_COLL_ALLBLOCKINGCOLLECTIVES || collectiveSupportLevel == X10RT_COLL_NONBLOCKINGBARRIER) {
            barrier();
            finish nativeReduce(id, id==0n?here.id() as Int:Team.roles(id), root.id() as Int, src, src_off as Int, dst, dst_off as Int, count as Int, op);
        } else {
            state(id).collective_impl[T](LocalTeamState.COLL_REDUCE, root, src, src_off, dst, dst_off, count, op);
        }
    }
    
    private static def nativeReduce[T](id:Int, role:Int, root:Int, src:Rail[T], src_off:Int, dst:Rail[T], dst_off:Int, count:Int, op:Int) : void {
        @Native("java", "x10.x10rt.TeamSupport.nativeReduce(id, role, root, src, src_off, dst, dst_off, count, op);")
        @Native("c++", "x10rt_reduce(id, role, root, &src->raw[src_off], &dst->raw[dst_off], (x10rt_red_op_type)op, x10rt_get_red_type<TPMGL(T)>(), count, ::x10aux::coll_handler, ::x10aux::coll_enter());") {}
    }

    /** Performs a reduction on a single value, returning the result at the root */
    public def reduce (root:Place, src:Byte, op:Int) = genericReduce(root, src, op);
    /** Performs a reduction on a single value, returning the result at the root */
    public def reduce (root:Place, src:UByte, op:Int) = genericReduce(root, src, op);
    /** Performs a reduction on a single value, returning the result at the root */
    public def reduce (root:Place, src:Short, op:Int) = genericReduce(root, src, op);
    /** Performs a reduction on a single value, returning the result at the root */
    public def reduce (root:Place, src:UShort, op:Int) = genericReduce(root, src, op);
    /** Performs a reduction on a single value, returning the result at the root */
    public def reduce (root:Place, src:UInt, op:Int) = genericReduce(root, src, op);
    /** Performs a reduction on a single value, returning the result at the root */
    public def reduce (root:Place, src:Int, op:Int) = genericReduce(root, src, op);
    /** Performs a reduction on a single value, returning the result at the root */
    public def reduce (root:Place, src:Long, op:Int) = genericReduce(root, src, op);
    /** Performs a reduction on a single value, returning the result at the root */
    public def reduce (root:Place, src:ULong, op:Int) = genericReduce(root, src, op);
    /** Performs a reduction on a single value, returning the result at the root */
    public def reduce (root:Place, src:Float, op:Int) = genericReduce(root, src, op);
    /** Performs a reduction on a single value, returning the result at the root */
    public def reduce (root:Place, src:Double, op:Int) = genericReduce(root, src, op);

    private def genericReduce[T] (root:Place, src:T, op:Int) : T {
        val chk = new Rail[T](1, src);
        val dst = new Rail[T](1, src);
        reduce(root, chk, 0, dst, 0, 1, op);
        return dst(0);
    }

    private static def nativeReduce[T](id:Int, role:Int, root:Int, src:Rail[T], dst:Rail[T], op:Int) : void {
        @Native("java", "x10.x10rt.TeamSupport.nativeReduce(id, role, root, src, 0, dst, 0, 1, op);")
        @Native("c++", "x10rt_reduce(id, role, root, src->raw, dst->raw, (x10rt_red_op_type)op, x10rt_get_red_type<TPMGL(T)>(), 1, ::x10aux::coll_handler, ::x10aux::coll_enter());") {}
    }

    /** Blocks until all members have received the computed result.  Note that not all values of T are valid.
     * The dst array is populated for all members with the result of the operation applied pointwise to all given src arrays.
     *
     * @param src The data that will be sent to all members
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
    public def allreduce[T] (src:Rail[T], src_off:Long, dst:Rail[T], dst_off:Long, count:Long, op:Int) : void {
        if (collectiveSupportLevel == X10RT_COLL_ALLNONBLOCKINGCOLLECTIVES) {
            if (DEBUG) Runtime.println(here + " entering native allreduce on team "+id);
            finish nativeAllreduce(id, id==0n?here.id() as Int:Team.roles(id), src, src_off as Int, dst, dst_off as Int, count as Int, op);
        } else if (collectiveSupportLevel == X10RT_COLL_ALLBLOCKINGCOLLECTIVES || collectiveSupportLevel == X10RT_COLL_NONBLOCKINGBARRIER) {
            if (DEBUG) Runtime.println(here + " entering pre-allreduce barrier on team "+id);
            barrier();
            if (DEBUG) Runtime.println(here + " entering native allreduce on team "+id);
            finish nativeAllreduce(id, id==0n?here.id() as Int:Team.roles(id), src, src_off as Int, dst, dst_off as Int, count as Int, op);
        } else {
            if (DEBUG) Runtime.println(here + " entering Team.x10 allreduce on team "+id);
            state(id).collective_impl[T](LocalTeamState.COLL_ALLREDUCE, Place.FIRST_PLACE, src, src_off, dst, dst_off, count, op);
        }
        if (DEBUG) Runtime.println(here + " Finished allreduce on team "+id);
    }

    private static def nativeAllreduce[T](id:Int, role:Int, src:Rail[T], src_off:Int, dst:Rail[T], dst_off:Int, count:Int, op:Int) : void {
        @Native("java", "x10.x10rt.TeamSupport.nativeAllReduce(id, role, src, src_off, dst, dst_off, count, op);")
        @Native("c++", "x10rt_allreduce(id, role, &src->raw[src_off], &dst->raw[dst_off], (x10rt_red_op_type)op, x10rt_get_red_type<TPMGL(T)>(), count, ::x10aux::coll_handler, ::x10aux::coll_enter());") {}
    }

    /** Performs a reduction on a single value, returning the result */
    public def allreduce (src:Byte, op:Int) = genericAllreduce(src, op);
    /** Performs a reduction on a single value, returning the result */
    public def allreduce (src:UByte, op:Int) = genericAllreduce(src, op);
    /** Performs a reduction on a single value, returning the result */
    public def allreduce (src:Short, op:Int) = genericAllreduce(src, op);
    /** Performs a reduction on a single value, returning the result */
    public def allreduce (src:UShort, op:Int) = genericAllreduce(src, op);
    /** Performs a reduction on a single value, returning the result */
    public def allreduce (src:UInt, op:Int) = genericAllreduce(src, op);
    /** Performs a reduction on a single value, returning the result */
    public def allreduce (src:Int, op:Int) = genericAllreduce(src, op);
    /** Performs a reduction on a single value, returning the result */
    public def allreduce (src:Long, op:Int) = genericAllreduce(src, op);
    /** Performs a reduction on a single value, returning the result */
    public def allreduce (src:ULong, op:Int) = genericAllreduce(src, op);
    /** Performs a reduction on a single value, returning the result */
    public def allreduce (src:Float, op:Int) = genericAllreduce(src, op);
    /** Performs a reduction on a single value, returning the result */
    public def allreduce (src:Double, op:Int) = genericAllreduce(src, op);

    private def genericAllreduce[T] (src:T, op:Int) : T {
        val chk = new Rail[T](1, src);
        val dst = new Rail[T](1, src);
        allreduce(chk, 0, dst, 0, 1, op);
        return dst(0);
    }

    private static def nativeAllreduce[T](id:Int, role:Int, src:Rail[T], dst:Rail[T], op:Int) : void {
        @Native("java", "x10.x10rt.TeamSupport.nativeAllReduce(id, role, src, 0, dst, 0, 1, op);")
        @Native("c++", "x10rt_allreduce(id, role, src->raw, dst->raw, (x10rt_red_op_type)op, x10rt_get_red_type<TPMGL(T)>(), 1, ::x10aux::coll_handler, ::x10aux::coll_enter());") {}
    }

    /** This operation blocks until all members have received the computed result.  
     * 
     * @param v The value which is compared across all team members
     * 
     * @param idx An integer which is paired with v
     * 
     * @return The value of idx, which was passed in along with the largest v, by the first place with that v
     */
    public def indexOfMax (v:Double, idx:Int) : Int {
        val src = new Rail[DoubleIdx](1, DoubleIdx(v, idx));
        val dst = new Rail[DoubleIdx](1, DoubleIdx(0.0, -1n));
        if (collectiveSupportLevel == X10RT_COLL_ALLNONBLOCKINGCOLLECTIVES)
            finish nativeIndexOfMax(id, id==0n?here.id() as Int:Team.roles(id), src, dst);
        else if (collectiveSupportLevel == X10RT_COLL_ALLBLOCKINGCOLLECTIVES || collectiveSupportLevel == X10RT_COLL_NONBLOCKINGBARRIER) {
            barrier();
            finish nativeIndexOfMax(id, id==0n?here.id() as Int:Team.roles(id), src, dst);
        }
        else
            state(id).collective_impl[DoubleIdx](LocalTeamState.COLL_INDEXOFMAX, Place.FIRST_PLACE, src, 0, dst, 0, 1, 0n);
        return dst(0).idx;
    }

    private static def nativeIndexOfMax(id:Int, role:Int, src:Rail[DoubleIdx], dst:Rail[DoubleIdx]) : void {
        @Native("java", "x10.x10rt.TeamSupport.nativeIndexOfMax(id, role, src, dst);")
        @Native("c++", "x10rt_allreduce(id, role, src->raw, dst->raw, X10RT_RED_OP_MAX, X10RT_RED_TYPE_DBL_S32, 1, ::x10aux::coll_handler, ::x10aux::coll_enter());") {}
    }

    /** This operation blocks until all members have received the computed result.  
     * 
     * @param v The value which is compared across all team members
     * 
     * @param idx An integer which is paired with v
     * 
     * @return The value of idx, which was passed in along with the smallest v, by the first place with that v
     */
    public def indexOfMin (v:Double, idx:Int) : Int {
        val src = new Rail[DoubleIdx](1, DoubleIdx(v, idx));
        val dst = new Rail[DoubleIdx](1, DoubleIdx(0.0, -1n));
        if (collectiveSupportLevel == X10RT_COLL_ALLNONBLOCKINGCOLLECTIVES)
            finish nativeIndexOfMin(id, id==0n?here.id() as Int:Team.roles(id), src, dst);
        else if (collectiveSupportLevel == X10RT_COLL_ALLBLOCKINGCOLLECTIVES || collectiveSupportLevel == X10RT_COLL_NONBLOCKINGBARRIER) {
            barrier();
            finish nativeIndexOfMin(id, id==0n?here.id() as Int:Team.roles(id), src, dst);
        }
        else
            state(id).collective_impl[DoubleIdx](LocalTeamState.COLL_INDEXOFMIN, Place.FIRST_PLACE, src, 0, dst, 0, 1, 0n);
        return dst(0).idx;
    }

    private static def nativeIndexOfMin(id:Int, role:Int, src:Rail[DoubleIdx], dst:Rail[DoubleIdx]) : void {
        @Native("java", "x10.x10rt.TeamSupport.nativeIndexOfMin(id, role, src, dst);")
        @Native("c++", "x10rt_allreduce(id, role, src->raw, dst->raw, X10RT_RED_OP_MIN, X10RT_RED_TYPE_DBL_S32, 1, ::x10aux::coll_handler, ::x10aux::coll_enter());") {}
    }

    /** Create new teams by subdividing an existing team.  This is called by each member
     * of an existing team, indicating which of the new teams it will be a member of, and its role
     * within that team.  The old team is still available after this call.  All the members
     * of the old team must collectively assign themselves to new teams such that there is exactly 1
     * member of the original team for each role of each new team.  It is undefined behaviour if two
     * members of the original team decide to play the same role in one of the new teams, or if one of
     * the roles of a new team is left unfilled.
     *
     * @param color The new team, must be a number between 0 and the number of new teams - 1
     *
     * @param new_role The caller's position within the new team
     */
    public def split (color:Int, new_role:Long) : Team {
        val result = new Rail[Int](1);
        if (collectiveSupportLevel == X10RT_COLL_ALLNONBLOCKINGCOLLECTIVES) {
            if (DEBUG) Runtime.println(here + " calling native split on team "+id+" color="+color+" new_role="+new_role);        
            finish nativeSplit(id, id==0n?here.id() as Int:Team.roles(id), color, new_role as Int, result);
            if (DEBUG) Runtime.println(here + " finished native split on team "+id+" color="+color+" new_role="+new_role);
            return Team(result(0), null, new_role);
        }
        else {
            if (DEBUG) Runtime.println(here + " creating PlaceGroup for splitting team "+id+"(size="+this.size()+") color="+color+" new_role="+new_role);
            // all-to-all to distribute team and role information around        
            val myInfo:Rail[Int] = new Rail[Int](2);
            myInfo(0) = color;
            myInfo(1) = new_role as Int; // TODO: may need to preserve long someday
            val allInfo:Rail[Int] = new Rail[Int](this.size() * 2);
            alltoall(myInfo, 0, allInfo, 0, 2);
            
            // In case the underlying alltoall does not copy my info from src to dst
            myTeamPosition:Long = Team.state(this.id).places.indexOf(here.id()) * 2;
            allInfo(myTeamPosition) = color;
            allInfo(myTeamPosition+1) = new_role as Int;
            
            if (DEBUGINTERNALS) Runtime.println(here + " completed alltoall for splitting team "+id+" color="+color+" new_role="+new_role+" allInfo="+allInfo);
            // use the above to figure out the members of *my* team
            // count the new team size
            var numPlacesInMyTeam:Int = 0n;
            for (var i:Long=0; i<allInfo.size; i+=2)
                if (allInfo(i) == color)
                    numPlacesInMyTeam++;

            if (DEBUGINTERNALS) Runtime.println(here + " my new team has "+numPlacesInMyTeam+" places");
            // create a new PlaceGroup with all members of my new team
            val newTeamPlaceRail:Rail[Place] = new Rail[Place](numPlacesInMyTeam);
            for (var i:Long=0; i<allInfo.size; i+=2) {
                if (allInfo(i) == color) {
                    if (DEBUGINTERNALS) Runtime.println(here + " setting new team position "+allInfo(i+1)+" to place "+Team.state(this.id).places(i/2));
                    newTeamPlaceRail(allInfo(i+1)) = Team.state(this.id).places(i/2);
            }   }
            newTeamPlaceGroup:SparsePlaceGroup = new SparsePlaceGroup(newTeamPlaceRail);
            if (DEBUGINTERNALS) Runtime.println(here + " Created PlaceGroup for splitting team "+id+" color="+color+" new_role="+new_role+": "+newTeamPlaceRail);
            // now that we have a PlaceGroup for the new team, create it
            if (collectiveSupportLevel == X10RT_COLL_ALLBLOCKINGCOLLECTIVES || collectiveSupportLevel == X10RT_COLL_NONBLOCKINGBARRIER) {
                if (DEBUGINTERNALS) Runtime.println(here + " calling pre-native split barrier on team "+id+" color="+color+" new_role="+new_role);
                barrier();
                if (DEBUGINTERNALS) Runtime.println(here + " calling native split on team "+id+" color="+color+" new_role="+new_role);
                finish nativeSplit(id, id==0n?here.id() as Int:Team.roles(id), color, new_role as Int, result);
                if (DEBUG) Runtime.println(here + " finished native split on team "+id+" color="+color+" new_role="+new_role);
                return Team(result(0), newTeamPlaceGroup, new_role);
            }
            else {
                if (DEBUG) Runtime.println(here + " returning new split team "+id+" color="+color+" new_role="+new_role);
                return Team((Team.state.size() as Int) + color, newTeamPlaceGroup, new_role);
            }
        }
    }

    private static def nativeSplit(id:Int, role:Int, color:Int, new_role:Int, result:Rail[Int]) : void {
        @Native("java", "x10.x10rt.TeamSupport.nativeSplit(id, role, color, new_role, result);")
        @Native("c++", "x10rt_team_split(id, role, color, new_role, ::x10aux::coll_handler2, ::x10aux::coll_enter2(result->raw));") {}
    }

    /** Destroy a team that is no-longer needed.  Called simultaneously by each member of
     * the team.  There should be no operations on the team after this.
     */
    public def delete () : void {
        if (this == WORLD) throw new IllegalArgumentException("Cannot delete Team.WORLD");
        if (collectiveSupportLevel == X10RT_COLL_ALLNONBLOCKINGCOLLECTIVES)
            finish nativeDel(id, id==0n?here.id() as Int:Team.roles(id));
        else if (collectiveSupportLevel == X10RT_COLL_ALLBLOCKINGCOLLECTIVES || collectiveSupportLevel == X10RT_COLL_NONBLOCKINGBARRIER) {
            barrier();
            finish nativeDel(id, id==0n?here.id() as Int:Team.roles(id));
        }
        // TODO - see if there is something useful to delete with the local team implementation
    }

    private static def nativeDel(id:Int, role:Int) : void {
        @Native("java", "x10.x10rt.TeamSupport.nativeDel(id, role);")
        @Native("c++", "x10rt_team_del(id, role, ::x10aux::coll_handler, ::x10aux::coll_enter());") {}
    }

    public def toString() = "Team(" + this.id + ")";
    public def equals(that:Team) = that.id==this.id;
    public def equals(that:Any) = that instanceof Team && (that as Team).id==this.id;
    public def hashCode()=id;
    
    
    /*
     * State information for X10 collective operations
     * All collectives are implemented as a tree operation, with all members of the team 
     * communicating with a "parent" member in a gather phase up to the root of the team,
     * followed by a scatter phase from the root to all members.  Data and reduction operations
     * may be carried along as a part of these communication phases, depending on the collective.
     * 
     * All operations are initiated by leaf nodes, which push data to their parent's buffers.  The parent
     * then initiates a push to its parent, and so on, up to the root.  At the root, 
     * the direction changes, and the root pushes data to children, who push it to their children, etc.
     * 
     * For performance reasons, this implementation DOES NOT perform error checking.  It does not verify
     * array indexes, that all places call the same collective at the same time, that root matches, etc.
     */
    private static class LocalTeamState(places:PlaceGroup, teamid:Int, myIndex:Long) {
        private static struct TreeStructure(parentIndex:Long, child1Index:Long, child2Index:Long, totalChildren:Long){}
        
        private static PHASE_READY:Int = 0n;   // normal state, nothing in progress
        private static PHASE_INIT:Int = 1n;    // collective active, preparing local structures to accept data
        private static PHASE_GATHER1:Int = 2n; // waiting for data+signal from first child
        private static PHASE_GATHER2:Int = 3n; // waiting for data+signal from second child
        private static PHASE_SCATTER:Int = 4n; // waiting for data+signal from parent
        private static PHASE_DONE:Int = 5n;    // done, but not yet ready for the next collective call
        private val phase:AtomicInteger = new AtomicInteger(PHASE_READY); // which of the above phases we're in
        private val dstLock:Lock = new Lock();

        private static COLL_BARRIER:Int = 0n; // no data moved
        private static COLL_BROADCAST:Int = 1n; // data out only, single value
        private static COLL_SCATTER:Int = 2n; // data out only, many values
        private static COLL_ALLTOALL:Int = 3n; // data in and out, many values
        private static COLL_REDUCE:Int = 4n; // data in only
        private static COLL_ALLREDUCE:Int = 5n; // data in and out
        private static COLL_INDEXOFMIN:Int = 6n; // data in and out
        private static COLL_INDEXOFMAX:Int = 7n; // data in and out

        // local data movement fields associated with the local arguments passed in collective_impl
        private var local_src:Any = null; // becomes type Rail[T]{self!=null}
        private var local_src_off:Long = 0;
        private var local_dst:Any = null; // becomes type Rail[T]{self!=null}
        private var local_dst_off:Long = 0;
        private var local_temp_buff:Any = null; // Used to hold intermediate data moving up or down the tree structure, becomes type Rail[T]{self!=null}
        private var local_temp_buff2:Any = null;
        private var local_count:Long = 0;
        private var local_grandchildren:Long = 0; // total number of nodes in the tree structure below us
        private var local_child1Index:Long = -1;
        private var local_child2Index:Long = -1;

        private static def getCollName(collType:Int):String {
            switch (collType) {
                case COLL_BARRIER: return "Barrier";
                case COLL_BROADCAST: return "Broadcast";
                case COLL_SCATTER: return "Scatter";
                case COLL_ALLTOALL: return "AllToAll";
                case COLL_REDUCE: return "Reduce";
                case COLL_ALLREDUCE: return "AllReduce";
                case COLL_INDEXOFMIN: return "IndexOfMin";
                case COLL_INDEXOFMAX: return "IndexOfMax";
                default: return "Unknown";
            }
        }
        
        // recursive method used to find our parent and child links in the tree.  This method assumes that root is not in the tree (or root is at position 0)
        private def getLinks(parent:Long, startIndex:Long, endIndex:Long):TreeStructure {
            if (DEBUGINTERNALS) Runtime.println(here+" getLinks called with myIndex="+myIndex+" parent="+parent+" startIndex="+startIndex+", endIndex="+endIndex);
            
            if (myIndex == startIndex) { // we're at our own position in the tree
                val children:Long = endIndex-startIndex; // overall gap of children
                return new TreeStructure(parent, (children<1)?-1:(startIndex+1), (children<2)?-1:(startIndex+1+((endIndex-startIndex)/2)), children);
            }
            else {
                if (myIndex > startIndex+((endIndex-startIndex)/2)) // go down the tree, following the right branch (second child)
                    return getLinks(startIndex, startIndex+1+((endIndex-startIndex)/2), endIndex);
                else // go down the left branch (first child)
                    return getLinks(startIndex, startIndex+1, startIndex+((endIndex-startIndex)/2));
            }
        }
        
        // This is an internal barrier which can be called at the end of team creation.  The regular
        // barrier method assumes that the team is already in place.  This method adds some pre-checks
        // to ensure that the state information for the entire team is in place before running the 
        // regular barrier, which does not have these checks.
        private def init() {
            if (DEBUGINTERNALS) Runtime.println(here + " creating team "+teamid);
            val myLinks:TreeStructure = getLinks(-1, 0, places.numPlaces()-1);

            if (DEBUGINTERNALS) { 
                Runtime.println(here+":team"+this.teamid+", root=0 has parent "+((myLinks.parentIndex==-1)?Place.INVALID_PLACE:places(myLinks.parentIndex)));
                Runtime.println(here+":team"+this.teamid+", root=0 has children "+((myLinks.child1Index==-1)?Place.INVALID_PLACE:places(myLinks.child1Index))+", "+((myLinks.child2Index==-1)?Place.INVALID_PLACE:places(myLinks.child2Index)));
            }
            val teamidcopy = this.teamid; // needed to prevent serializing "this"
            if (myLinks.parentIndex != -1) {
                @Pragma(Pragma.FINISH_ASYNC) finish at (places(myLinks.parentIndex)) async {
                    when (Team.state.size() > teamidcopy) {}
            }   }
            if (DEBUGINTERNALS) Runtime.println(here+":team"+this.teamid+", moving on to init barrier");
            collective_impl[Int](COLL_BARRIER, places(0), null, 0, null, 0, 0, 0n); // barrier
            if (DEBUGINTERNALS) Runtime.println(here + " leaving init phase");
        }
        
        /*
         * This method contains the implementation for all collectives.  Some arguments are only valid
         * for specific collectives.
         */
        private def collective_impl[T](collType:Int, root:Place, src:Rail[T], src_off:Long, dst:Rail[T], dst_off:Long, count:Long, operation:Int):void {
            if (DEBUGINTERNALS) Runtime.println(here+":team"+teamid+" entered "+getCollName(collType)+" phase="+phase.get()+", root="+root);
            val teamidcopy = this.teamid; // needed to prevent serializing "this" in at() statements

            /**
             * Block the current activity until condition is set to true by
             * another activity, giving preference to activities incoming
             * from the network (x10rt probe).
             */
            val probeUntil = (condition:() => Boolean) => @NoInline {
                if (!condition()) {
                    while (!condition()) Runtime.probe();
                }
            };

            /**
             * Block the current activity until condition is set to true by
             * another activity, giving preference to activities running
             * locally on another worker thread.
             */
            val sleepUntil = (condition:() => Boolean) => @NoInline {
                if (!condition()) {
                    Runtime.increaseParallelism();
                    while (!condition()) System.threadSleep(0);
                    Runtime.decreaseParallelism(1n);
                }
            };

            // block if some other collective is in progress.
            sleepUntil(() => this.phase.compareAndSet(PHASE_READY, PHASE_INIT));
            
            // figure out our links in the tree structure
            val myLinks:TreeStructure;
            val rootIndex:Long = places.indexOf(root);
            if (myIndex > rootIndex || rootIndex == 0)
                myLinks = getLinks(-1, rootIndex, places.numPlaces()-1);
            else if (myIndex < rootIndex)
                myLinks = getLinks(rootIndex, 0, rootIndex-1);
            else // non-zero root
                myLinks = new TreeStructure(-1, 0, ((places.numPlaces()-1)==rootIndex)?-1:(rootIndex+1), places.numPlaces()-1);

            if (DEBUGINTERNALS) { 
                Runtime.println(here+":team"+teamidcopy+", root="+root+" has parent "+((myLinks.parentIndex==-1)?Place.INVALID_PLACE:places(myLinks.parentIndex)));
                Runtime.println(here+":team"+teamidcopy+", root="+root+" has children "+((myLinks.child1Index==-1)?Place.INVALID_PLACE:places(myLinks.child1Index))+", "+((myLinks.child2Index==-1)?Place.INVALID_PLACE:places(myLinks.child2Index)));
            }
            
            // make my local data arrays visible to other places
            local_src = src;
            local_src_off = src_off;
            local_dst = dst;
            local_dst_off = dst_off;
            local_count = count;
            local_grandchildren = myLinks.totalChildren;
            local_child1Index = myLinks.child1Index;
            local_child2Index = myLinks.child2Index;
            if ((collType == COLL_REDUCE || collType == COLL_ALLREDUCE)) {
                if (local_child1Index > -1 && src == dst) {
                    // src and dst aliased, use temp storage for child 1
                    local_temp_buff = Unsafe.allocRailUninitialized[T](count);
                }
                if (local_child2Index > -1) {
                    local_temp_buff2 = Unsafe.allocRailUninitialized[T](count);
                }
            } else if (myLinks.parentIndex != -1 && collType == COLL_SCATTER) {
                // data size may differ between places
if (DEBUGINTERNALS) Runtime.println(here+" allocated local_temp_buff size " + (myLinks.totalChildren+1)*count);
                local_temp_buff = Unsafe.allocRailUninitialized[T]((myLinks.totalChildren+1)*count);
            } else if ((collType == COLL_INDEXOFMIN || collType == COLL_INDEXOFMAX) && local_child1Index != -1) {
                // pairs of values move around
                local_temp_buff = Unsafe.allocRailUninitialized[T]((local_child2Index==-1)?1:2);
            }

            // check for valid input.  TODO: remove for performance?
            //if (dst == null && collType != COLL_BARRIER) Runtime.println("ERROR: dst is NULL!");
            //if (src == null && collType != COLL_BARRIER) Runtime.println("ERROR: src is NULL!");
            
            if (collType == COLL_INDEXOFMAX || collType == COLL_INDEXOFMIN)
                dst(0) = src(0);
            
            // allow children to update our dst array
            if (local_child1Index == -1) { // no children to wait for
                this.phase.set(PHASE_SCATTER);
            } else if (local_child2Index == -1) { // only one child, so skip a phase waiting for the second child.
                this.phase.set(PHASE_GATHER2);
            } else {
                this.phase.set(PHASE_GATHER1);
            }
        
            // wait for phase updates from children
            if (DEBUGINTERNALS) Runtime.println(here+":team"+teamidcopy+" waiting for children phase "+Team.state(teamidcopy).phase.get());
            probeUntil(() => this.phase.get() == PHASE_SCATTER);
            if (DEBUGINTERNALS) Runtime.println(here+":team"+teamidcopy+" released by children phase "+Team.state(teamidcopy).phase.get());

            if (collType == COLL_REDUCE || collType == COLL_ALLREDUCE) {
                if (local_child1Index != -1) { // reduce local and child data
                    if (src == dst) {
                        TeamReductionHelper.performReduction(local_temp_buff as Rail[T], 0, dst, dst_off, count, operation);
                    } else {
                        TeamReductionHelper.performReduction(src, src_off, dst, dst_off, count, operation);
                    }
                    if (local_child2Index != -1) {
                        TeamReductionHelper.performReduction(local_temp_buff2 as Rail[T], 0, dst, dst_off, count, operation);
                    }
                } else {
                    Rail.copy(src, src_off, dst, dst_off, count);
                }
            } else if (collType == COLL_ALLTOALL) {
                Rail.copy(src, src_off, dst, dst_off+(count*myIndex), count);
            }
        
            // all children have checked in.  Update our parent, and then wait for the parent to update us 
            if (myLinks.parentIndex == -1) { // this is the root
                // copy data locally from src to dst if needed
                if (collType == COLL_BROADCAST)
                    Rail.copy(src, src_off, dst, dst_off, count);
                else if (collType == COLL_SCATTER)
                    local_temp_buff = src;
                this.phase.set(PHASE_DONE); // the root node has no parent, and can skip its own state ahead
            } else {
                val waitForParentToReceive = () => @NoInline {
                    if (DEBUGINTERNALS) Runtime.println(here+" waiting for parent phase "+Team.state(teamidcopy).phase.get());
                     sleepUntil(() => {val state = Team.state(teamidcopy).phase.get();
                                       (state >= PHASE_GATHER1 && state < PHASE_SCATTER)
                                      });
                    if (DEBUGINTERNALS) Runtime.println(here+" parent ready to receive phase "+Team.state(teamidcopy).phase.get());
                };

                val incrementParentPhase = () => @NoInline {
                    if ( !(Team.state(teamidcopy).phase.compareAndSet(PHASE_GATHER1, PHASE_GATHER2)
                        || Team.state(teamidcopy).phase.compareAndSet(PHASE_GATHER2, PHASE_SCATTER)) )
                        Runtime.println("ERROR incrementing the parent "+here+":team"+teamidcopy+" current phase "+Team.state(teamidcopy).phase.get());
                };

                // move data from children to parent
                // Scatter and broadcast only move data from parent to children, so they have no code here
                if (collType >= COLL_ALLTOALL) {
                    if (DEBUGINTERNALS) Runtime.println(here+" moving data to parent");
                    val notnulldst = dst as Rail[T]{self!=null};
                    val gr = new GlobalRail[T](notnulldst);
                    if (collType == COLL_ALLTOALL) {
                        val sourceIndex = myIndex;
                        val totalData = count*(myLinks.totalChildren+1);
                        @Pragma(Pragma.FINISH_ASYNC) finish at (places(myLinks.parentIndex)) async {
                            waitForParentToReceive();
if (DEBUGINTERNALS) Runtime.println(here+ " alltoall gathering from offset "+(dst_off+(count*sourceIndex))+" to local_dst_off "+(Team.state(teamidcopy).local_dst_off+(count*sourceIndex))+" size " + totalData);
                            // copy my data, plus all the data filled in by my children, to my parent
                            Rail.uncountedCopy(gr, dst_off+(count*sourceIndex), Team.state(teamidcopy).local_dst as Rail[T], Team.state(teamidcopy).local_dst_off+(count*sourceIndex), totalData, incrementParentPhase);
                        }
                    } else if (collType == COLL_REDUCE || collType == COLL_ALLREDUCE) {
                        // copy reduced data to parent
                        val sourceIndex = places.indexOf(here);
                        @Pragma(Pragma.FINISH_ASYNC) finish at (places(myLinks.parentIndex)) async {
                            waitForParentToReceive();
                            var target:Rail[T];
                            var off:Long;
                            if (sourceIndex == Team.state(teamidcopy).local_child2Index) {
                                target = Team.state(teamidcopy).local_temp_buff2 as Rail[T];
                                off = 0;
                            } else if (Team.state(teamidcopy).local_src == Team.state(teamidcopy).local_dst) {
                                target = Team.state(teamidcopy).local_temp_buff as Rail[T];
                                off = 0;
                            } else {
                                // child 1 data written directly to dst
                                target = Team.state(teamidcopy).local_dst as Rail[T];
                                off = Team.state(teamidcopy).local_dst_off;
                            }
                            Rail.uncountedCopy(gr, dst_off, target, off, count, incrementParentPhase);
                        }
                    } else if (collType == COLL_INDEXOFMAX) {
                        val childVal:DoubleIdx = dst(0) as DoubleIdx;
                        @Pragma(Pragma.FINISH_ASYNC) finish at (places(myLinks.parentIndex)) async {
                            waitForParentToReceive();
                            sleepUntil(() => Team.state(teamidcopy).dstLock.tryLock());
                            val ldi:Rail[DoubleIdx] = (Team.state(teamidcopy).local_dst as Rail[DoubleIdx]);
                            if (DEBUGINTERNALS) Runtime.println(here+" IndexOfMax: parent="+ldi(0).value+" child="+childVal.value);
                            
                            // TODO: If there is  more than one instance of the min/max value, this 
                            // implementation will return the index associated with "one of" them, not necessarily
                            // the first one.  Do we need to return the "first", as the API says, or is that not really necessary?
                            if (childVal.value > ldi(0).value)
                                ldi(0) = childVal;
                            Team.state(teamidcopy).dstLock.unlock();
                            incrementParentPhase();
                        }
                    } else if (collType == COLL_INDEXOFMIN) {
                        val childVal:DoubleIdx = dst(0) as DoubleIdx;
                        @Pragma(Pragma.FINISH_ASYNC) finish at (places(myLinks.parentIndex)) async {
                            waitForParentToReceive();
                            sleepUntil(() => Team.state(teamidcopy).dstLock.tryLock());
                            val ldi:Rail[DoubleIdx] = (Team.state(teamidcopy).local_dst as Rail[DoubleIdx]);
                            if (childVal.value < ldi(0).value)
                                ldi(0) = childVal;
                            Team.state(teamidcopy).dstLock.unlock();
                            incrementParentPhase();
                         }
                    }
                } else {
                    @Pragma(Pragma.FINISH_ASYNC) finish at (places(myLinks.parentIndex)) async { 
                        waitForParentToReceive();
                        incrementParentPhase();
                    }
                }
                
                if (DEBUGINTERNALS) Runtime.println(here+ " waiting for parent "+places(myLinks.parentIndex)+":team"+teamidcopy+" to release us from phase "+phase.get());
                probeUntil(() => this.phase.get() == PHASE_DONE);
                if (DEBUGINTERNALS) Runtime.println(here+ " released by parent");
            }

            // move data from parent to children
            // reduce and barrier do not move data in this direction, so they are not included here
            if (local_child1Index != -1 && collType != COLL_BARRIER && collType != COLL_REDUCE) {
                val notnulldst = dst as Rail[T]{self!=null};
                val gr = new GlobalRail[T](notnulldst);

                if (collType == COLL_ALLTOALL) {
                    // only copy over the data that did not come from this child in the first place
                    val copyToChild = () => @NoInline {
                        val count = Team.state(teamidcopy).local_count;
                        val teamSize = Team.state(teamidcopy).places.size();
                        val lastChild = Team.state(teamidcopy).myIndex + Team.state(teamidcopy).local_grandchildren + 1;
                        finish {
if (DEBUGINTERNALS) Runtime.println(here+ " alltoall scattering first chunk from dst_off "+dst_off+" to local_dst_off "+Team.state(teamidcopy).local_dst_off+" size " + count*Team.state(teamidcopy).myIndex);
                            // position 0 up to the child id
                            Rail.asyncCopy(gr, dst_off, Team.state(teamidcopy).local_dst as Rail[T], Team.state(teamidcopy).local_dst_off, count*Team.state(teamidcopy).myIndex);
if (DEBUGINTERNALS) Runtime.println(here+ " alltoall scattering second chunk from offset "+(dst_off+(count*lastChild))+" to local_dst offset "+(Team.state(teamidcopy).local_dst_off+(count*lastChild))+" size " + count*(teamSize-lastChild));
                            // position of last child range, to the end
                            Rail.asyncCopy(gr, dst_off+(count*lastChild), Team.state(teamidcopy).local_dst as Rail[T], Team.state(teamidcopy).local_dst_off+(count*lastChild), count*(teamSize-lastChild));
                        }
                    };

                    @Pragma(Pragma.FINISH_SPMD) finish {
                        at (places(local_child1Index)) async copyToChild();
                        if (local_child2Index != -1) {
                            at (places(local_child2Index)) async copyToChild();
                        }
                    }
                } else if (collType == COLL_BROADCAST || collType == COLL_ALLREDUCE || 
                    collType == COLL_INDEXOFMIN || collType == COLL_INDEXOFMAX) {
                    // these all move a single value from root to all other team members
                    finish {
                        at (places(local_child1Index)) async {
                            if (DEBUGINTERNALS) Runtime.println(here+ " pulling data from "+gr+" into "+(Team.state(teamidcopy).local_dst as Rail[T]));
                            Rail.asyncCopy(gr, dst_off, Team.state(teamidcopy).local_dst as Rail[T], Team.state(teamidcopy).local_dst_off, Team.state(teamidcopy).local_count);
                        }
                        if (local_child2Index != -1) {
                            at (places(local_child2Index)) async {
                                if (DEBUGINTERNALS) Runtime.println(here+ " pulling data from "+gr+" into "+(Team.state(teamidcopy).local_dst as Rail[T]));
                                Rail.asyncCopy(gr, dst_off, Team.state(teamidcopy).local_dst as Rail[T], Team.state(teamidcopy).local_dst_off, Team.state(teamidcopy).local_count);
                            }
                        }
                    }
                } else if (collType == COLL_SCATTER) {
                    val notNullTmp = local_temp_buff as Rail[T]{self!=null};
                    val grTmp = new GlobalRail[T](notNullTmp);
                    // root scatters direct from src
                    val sourceOffset = (myLinks.parentIndex == -1) ? 0: Team.state(teamidcopy).myIndex*count;
                    val copyToChild = () => @NoInline {
                        val myOffset = (Team.state(teamidcopy).myIndex*count)-sourceOffset;
                        val count = Team.state(teamidcopy).local_count;
                        val totalData = (Team.state(teamidcopy).local_grandchildren+1)*count;
                        finish {
                            if (DEBUGINTERNALS) Runtime.println(here+ " scattering " + totalData + " from parent offset " + myOffset);
                            Rail.asyncCopy(grTmp, myOffset, Team.state(teamidcopy).local_temp_buff as Rail[T], 0, totalData);
                        }
                    };

                    @Pragma(Pragma.FINISH_SPMD) finish {
                        at (places(local_child1Index)) async copyToChild();
                        if (local_child2Index != -1) {
                            at (places(local_child2Index)) async copyToChild();
                        }
                    }
                }
                if (DEBUGINTERNALS) Runtime.println(here+ " finished moving data to children");
            }
        
            if (collType == COLL_SCATTER) {
                // root scatters own data direct from src to dst
                val temp_off_my_data = (myLinks.parentIndex == -1) ? (src_off + myIndex*count) : 0;
                if (DEBUGINTERNALS) Runtime.println(here+ " scatter " +count + " from local_temp_buff " + temp_off_my_data + " to dst");
                Rail.copy(local_temp_buff as Rail[T]{self!=null}, temp_off_my_data, dst, dst_off, count);
            }

            // our parent has updated us - update any children, and leave the collective
            if (local_child1Index != -1) { // free the first child, if it exists
                // NOTE: the use of runUncountedAsync allows the parent to continue past this section
                //   before the children have been set free.  This is necessary when there is a blocking
                //   call immediately after this collective completes (e.g. the barrier before a blocking 
                //   collective in MPI-2), because otherwise the at may not return before the barrier
                //   locks up the worker thread.
                val freeChild1 = () => @NoInline {
                    if (!Team.state(teamidcopy).phase.compareAndSet(PHASE_SCATTER, PHASE_DONE))
                        Runtime.println("ERROR root setting the first child "+here+":team"+teamidcopy+" to PHASE_DONE");
                    else if (DEBUGINTERNALS) Runtime.println("set the first child "+here+":team"+teamidcopy+" to PHASE_DONE");
                };
                Runtime.runUncountedAsync(places(local_child1Index), freeChild1, null);
                if (local_child2Index != -1) {
                    // NOTE: can't use the same closure because runUncountedAsync deallocates it
                    val freeChild2 = () => @NoInline {
                        if (!Team.state(teamidcopy).phase.compareAndSet(PHASE_SCATTER, PHASE_DONE))
                            Runtime.println("ERROR root setting the second child "+here+":team"+teamidcopy+" to PHASE_DONE");
                        else if (DEBUGINTERNALS) Runtime.println("set the second child "+here+":team"+teamidcopy+" to PHASE_DONE");
                    };
                    Runtime.runUncountedAsync(places(local_child2Index), freeChild2, null);
                }
            }
          
            local_src = null;
            local_dst = null;
            local_temp_buff = null;
            local_temp_buff2 = null;
            this.phase.set(PHASE_READY);
            // done!
            if (DEBUGINTERNALS) Runtime.println(here+":team"+teamidcopy+" leaving "+getCollName(collType));
        }
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
