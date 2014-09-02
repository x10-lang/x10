/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 *  (C) Copyright Australian National University 2013.
 *
 * This code emulates collective operations using point-to-point messages
 * between places.  Barrier, bcast, reduce and allreduce operations are
 * emulated by a two phase blocking tree communication.  In the first phase
 * messages travel down to the root (which is place 0 for a barrier); in the
 * second phase messages travel up from the root to all children. Reduction
 * data may be piggybacked on messages in the first phase, and broadcast data
 * may be piggybacked in the second phase.  Allreduce is thus implemented as
 * a reduce followed by a broadcast.
 */
#ifdef __CYGWIN__
#undef __STRICT_ANSI__ // Strict ANSI mode is too strict in Cygwin
#endif

#include <cstdlib>
#include <cstdio>
#include <cassert>
#include <new>
#if defined(__WIN32__) && defined(__STRICT_ANSI__)
_CRTIMP int __cdecl __MINGW_NOTHROW     swprintf (wchar_t*, const wchar_t*, ...);
_CRTIMP int __cdecl __MINGW_NOTHROW     vswprintf (wchar_t*, const wchar_t*, __VALIST);
#endif
#include <algorithm>
#include <cfloat>
#include<complex>

#include <x10rt_types.h>
#include <x10rt_internal.h>
#include <x10rt_net.h>
#include <x10rt_ser.h>
#include <x10rt_cpp.h>
#include <x10rt_front.h>

namespace {

    // note this cannot be a re-entrant lock because Preempt (below) will break
    Lock global_lock;

    // for scoped locks
    struct Synchronized { // define an atomic section
        Lock * const lock;
        template <class T> Synchronized (T &t) : lock(&t.lock_) { lock->acquire(); }
        template <class T> Synchronized (T *t) : lock(&t->lock) { lock->acquire(); }
        Synchronized (Lock *l) : lock(l) { lock->acquire(); }
        Synchronized (Lock &l) : lock(&l) { lock->acquire(); }
        ~Synchronized (void) { lock->release(); }
    };
    #define NUMVAR(x) _ ## x
    #define MACROVAR(x) NUMVAR(x)
    #define SYNCHRONIZED(x) Synchronized MACROVAR(__LINE__) (x)

    struct Preempt { // cut a hole in the middle of an atomic section
        Lock * const lock;
        template <class T> Preempt (T &t) : lock(&t.lock_) { lock->release(); }
        template <class T> Preempt (T *t) : lock(&t->lock) { lock->release(); }
        Preempt (Lock *l) : lock(l) { lock->release(); }
        Preempt (Lock &l) : lock(&l) { lock->release(); }
        ~Preempt (void) { lock->acquire(); }
    };
    #define PREEMPT(x) Preempt MACROVAR(__LINE__) (x)

    // Store everything a particular member needs to know about itself
    struct MemberObj {
        x10rt_team team;
        x10rt_team role;
        struct {
            x10rt_place root;
            int childToReceive;
            int parentToSend; // threadlocal
            int parentToReceive;
            x10rt_completion_handler *ch;
            void *arg;
        } barrier; // other collectives use barrier so keep its state separate
        struct {
            x10rt_place root;
            const void *sbuf;
            void *dbuf;
            size_t el; // element size
            size_t count;
            x10rt_completion_handler *ch;
            void *arg;
            bool barrier_done;
            bool data_done;
        } scatter;
        struct {
            x10rt_place root;
            const void *sbuf;
            void *dbuf;
            size_t el; // element size
            size_t count;
        } bcast;
        struct {
            const void *sbuf;
            void *dbuf;
            size_t el; // element size
            size_t count;
            x10rt_completion_handler *ch;
            x10rt_place counter; // how many bcasts we have done so far
            void *arg;
        } alltoall;
        struct {
            x10rt_place root;
            const void *sbuf;
            void *dbuf;
            void *rbuf; // temporary buffer to store received reduction data
            void *rbuf2;
            size_t el; // element size
            size_t count;
            x10rt_completion_handler *ch;
            void *arg;
            bool started;
        } reduce;
        struct {
            x10rt_place *sbuf;
            x10rt_place role; // this member's role in the new team
            x10rt_completion_handler2 *ch;
            x10rt_place *rbuf; // a temporary buffer for storing results of alltoall
            x10rt_place *newTeamPlaces; // a temporary buffer for the local team
            x10rt_place *newTeamOldRoles; // a temporary buffer for the local team
            x10rt_place newTeamSz; // size of the above buffers
            void *arg;
        } split;
        MemberObj (x10rt_team team_, x10rt_team role_)
            : team(team_), role(role_)
        {
            memset(&bcast,0,sizeof(bcast));
            memset(&alltoall,0,sizeof(alltoall));
            memset(&reduce,0,sizeof(reduce));
            memset(&barrier,0,sizeof(barrier));
        }
    };

    // each team that gets created has local state represented with one of these
    struct TeamObj {
        x10rt_place localUsers;
        x10rt_place memberc;
        MemberObj **memberv;
        x10rt_place *placev; // INVARIANT: memberv[i]!=null <==> placev[i]==here
        TeamObj (x10rt_team id, x10rt_place placec, x10rt_place *placev_)
          : localUsers(0)
        {
            memberc = placec;
            memberv = safe_malloc<MemberObj*>(memberc);
            placev = safe_malloc<x10rt_place>(memberc);
            for (x10rt_place i=0 ; i<memberc ; ++i) {
                placev[i] = placev_[i];
                if (placev[i] == x10rt_net_here()) {
                    memberv[i] = new (safe_malloc<MemberObj>()) MemberObj(id, i);
                    localUsers++;
                } else {
                    memberv[i] = NULL;
                }
            }
        }
        ~TeamObj (void)
        {
            for (x10rt_place i=0 ; i<memberc ; ++i) {
                if (memberv[i]!=NULL) {
                    memberv[i]->~MemberObj();
                    safe_free(memberv[i]);
                }
            }
            safe_free(memberv);
            safe_free(placev);
        }
        MemberObj *&operator[] (x10rt_place r) { return memberv[r]; }
    };

    struct CollOp;

    // global team database: stores the teams currently in use
    struct TeamDB {

        TeamDB (void) : teamc(0), team_next(0), teamv(NULL) { }

        ~TeamDB (void) { free(teamv); }

        TeamObj *&operator[] (x10rt_team t) { SYNCHRONIZED(global_lock); assert(t<teamc); return teamv[t]; }

        // must be called with global_lock taken
        void allocTeam (x10rt_team t, x10rt_place members, x10rt_place *placev)
        {
            SYNCHRONIZED(global_lock);
            allocTeam_(t, members, placev);
        }

        x10rt_team allocTeam (x10rt_place members, x10rt_place *placev)
        {
            SYNCHRONIZED(global_lock);
            x10rt_team t = team_next;
            allocTeam_(t, members, placev);
            team_next++;
            return t;
        }

        void releaseTeam (x10rt_team team)
        {
            SYNCHRONIZED(global_lock);
            TeamObj *&t = teamv[team];
            if (--t->localUsers) return;
            TeamObj *tmp = NULL;
            std::swap(t, tmp);
            tmp->~TeamObj();
            safe_free(tmp);
        }

        void fifo_push_back (CollOp *o)
        {
            SYNCHRONIZED(global_lock);
            fifo.push_back(o);
        }
    
        CollOp *fifo_pop (void)
        {
            SYNCHRONIZED(global_lock);
            return fifo.pop();
        }
    
        size_t fifo_size (void)
        {
            SYNCHRONIZED(global_lock);
            return fifo.size;
        }
    
        private:

        Fifo<CollOp> fifo; // where we record incomplete collectives

        x10rt_team teamc; // size of teamv buffer
        x10rt_team team_next; // the next new team gets this id
        TeamObj **teamv;

        void ensureIndex (x10rt_team i)
        {
            if (i>=teamc) {
                teamc = i+1;
                teamv = safe_realloc(teamv, teamc);
            }
        }

        void allocTeam_ (x10rt_team t, x10rt_place members, x10rt_place *placev)
        {
            ensureIndex(t);
            this->teamv[t] = new (safe_malloc<TeamObj>()) TeamObj(t, members, placev);
        }


    } gtdb;

    x10rt_msg_type TEAM_NEW_PLACE_ZERO_ID, TEAM_NEW_ID, TEAM_NEW_FINISHED_ID;

    x10rt_msg_type SPLIT_ID;

    x10rt_msg_type SCATTER_COPY_ID;

}

static void team_new_decrement_counter (int *counter, x10rt_completion_handler2 *ch,
                                        x10rt_team t, void *arg)
{
    (*counter)--;
    if (*counter == 0) {
        ch(t, arg);
        safe_free(counter);
    }
}

static void team_new_finished_recv (const x10rt_msg_params *p)
{
    x10rt_deserbuf b;
    x10rt_deserbuf_init(&b, p);
    x10rt_team t; x10rt_deserbuf_read(&b, &t);
    x10rt_remote_ptr ch_; x10rt_deserbuf_read(&b, &ch_);
    x10rt_remote_ptr arg_; x10rt_deserbuf_read(&b, &arg_);
    x10rt_remote_ptr counter_; x10rt_deserbuf_read(&b, &counter_);

    int *counter = (int*)(size_t)counter_;
    x10rt_completion_handler2 *ch = (x10rt_completion_handler2*)(size_t)ch_;
    void *arg = (void*)(size_t)arg_;

    team_new_decrement_counter(counter, ch, t, arg);
}

static void send_team_new_finished (x10rt_place home, x10rt_team t, x10rt_remote_ptr ch_,
                                    x10rt_remote_ptr arg_, x10rt_remote_ptr counter_)
{
    if (x10rt_net_here()==home) {
        int *counter = (int*)(size_t)counter_;
        x10rt_completion_handler2 *ch = (x10rt_completion_handler2*)(size_t)ch_;
        void *arg = (void*)(size_t)arg_;
        team_new_decrement_counter(counter, ch, t, arg);
    } else {
        x10rt_serbuf b2;
        x10rt_serbuf_init(&b2, home, TEAM_NEW_FINISHED_ID);
        x10rt_serbuf_write(&b2, &t);
        x10rt_serbuf_write(&b2, &ch_);
        x10rt_serbuf_write(&b2, &arg_);
        x10rt_serbuf_write(&b2, &counter_);
        x10rt_net_send_msg(&b2.p);
        x10rt_serbuf_free(&b2);
    }
}

static void team_new_recv (const x10rt_msg_params *p)
{
    x10rt_deserbuf b;
    x10rt_deserbuf_init(&b, p);
    x10rt_team t; x10rt_deserbuf_read(&b, &t);
    x10rt_place members; x10rt_deserbuf_read(&b, &members);
    x10rt_place *placev = safe_malloc<x10rt_place>(members);
    x10rt_deserbuf_read_ex(&b, placev, sizeof(*placev), members);
    x10rt_remote_ptr counter_; x10rt_deserbuf_read(&b, &counter_);
    x10rt_place home; x10rt_deserbuf_read(&b, &home);
    x10rt_remote_ptr ch_; x10rt_deserbuf_read(&b, &ch_);
    x10rt_remote_ptr arg_; x10rt_deserbuf_read(&b, &arg_);

    gtdb.allocTeam(t, members, placev);

    send_team_new_finished(home,t,ch_,arg_,counter_);
}

static void team_new_place_zero (x10rt_place members, x10rt_place *placev,
                                 x10rt_remote_ptr counter_, x10rt_place home,
                                 x10rt_remote_ptr ch_, x10rt_remote_ptr arg_)
{
    assert(0 == x10rt_net_here());

    x10rt_team t = gtdb.allocTeam(members, placev);
    send_team_new_finished(home,t,ch_,arg_,counter_);

    for (x10rt_place i=1 ; i<x10rt_net_nhosts() ; ++i) {
        x10rt_serbuf b;
        x10rt_serbuf_init(&b, i, TEAM_NEW_ID);
        x10rt_serbuf_write(&b, &t);
        x10rt_serbuf_write(&b, &members);
        x10rt_serbuf_write_ex(&b, placev, sizeof(*placev), members);
        x10rt_serbuf_write(&b, &counter_);
        x10rt_serbuf_write(&b, &home);
        x10rt_serbuf_write(&b, &ch_);
        x10rt_serbuf_write(&b, &arg_);
        x10rt_net_send_msg(&b.p);
        x10rt_serbuf_free(&b);
    }

}

static void team_new_place_zero_recv (const x10rt_msg_params *p)
{
    x10rt_deserbuf b;
    x10rt_deserbuf_init(&b, p);
    x10rt_place members; x10rt_deserbuf_read(&b, &members);
    x10rt_place *placev = safe_malloc<x10rt_place>(members);
    x10rt_deserbuf_read_ex(&b, placev, sizeof(*placev), members);
    x10rt_remote_ptr counter_; x10rt_deserbuf_read(&b, &counter_);
    x10rt_place home; x10rt_deserbuf_read(&b, &home);
    x10rt_remote_ptr ch_; x10rt_deserbuf_read(&b, &ch_);
    x10rt_remote_ptr arg_; x10rt_deserbuf_read(&b, &arg_);

    team_new_place_zero(members, placev, counter_, home, ch_, arg_);
}

void x10rt_emu_team_new (x10rt_place members, x10rt_place *placev,
                         x10rt_completion_handler2 *ch, void *arg)
{
    x10rt_place home = x10rt_net_here();
    x10rt_remote_ptr ch_ = (x10rt_remote_ptr) (size_t) ch;
    x10rt_remote_ptr arg_ = (x10rt_remote_ptr) (size_t) arg;

    int *counter = (safe_malloc<int>());
    *counter = x10rt_net_nhosts();
    x10rt_remote_ptr counter_ = (x10rt_remote_ptr) (size_t) counter;

    // go to place zero
    if (x10rt_net_here()==0) {
        team_new_place_zero(members, placev, counter_, home, ch_, arg_);
    } else {
        x10rt_serbuf b;
        x10rt_serbuf_init(&b, 0, TEAM_NEW_PLACE_ZERO_ID);
        x10rt_serbuf_write(&b, &members);
        x10rt_serbuf_write_ex(&b, placev, sizeof(*placev), members);
        x10rt_serbuf_write(&b, &counter_);
        x10rt_serbuf_write(&b, &home);
        x10rt_serbuf_write(&b, &ch_);
        x10rt_serbuf_write(&b, &arg_);
        x10rt_net_send_msg(&b.p);
        x10rt_serbuf_free(&b);
    }
}

x10rt_place x10rt_emu_team_sz (x10rt_team t)
{
    return gtdb[t]->memberc;
}

void x10rt_emu_team_del (x10rt_team team, x10rt_place role, x10rt_completion_handler *ch, void *arg)
{
    assert(gtdb[team]->placev[role] == x10rt_net_here());
    gtdb.releaseTeam(team);
    ch(arg);
}

namespace {
    struct CollOp : FifoElement<CollOp> {
        const x10rt_team team;
        const x10rt_place role;
        CollOp (x10rt_team team_, x10rt_place role_)
          : team(team_), role(role_) { }
        bool progress (void);
        void handlePendingReduce(MemberObj *m);
    };
}

/* Sometimes m.barrier.wait will be negative when barriers are used from multiple threads.
 * To see why, consider the following sequence of events in a team of size 2,
 * each role handled by a separate thread at the same place:
 *
 * role 0 enters x10rt_emu_barrier         (0,  0)
 * role 0 increments own counter by 2      (2,  0)
 * role 0 decrements role 0's counter by 1 (1,  0)
 * role 0 decrements role 1's counter by 1 (1, -1)
 * role 1 enters x10rt_emu_barrier         (1, -1)
 * role 1 increments own counter by 2      (1,  1)
 * role 1 decrements role 0's counter by 1 (0,  1)
 * role 1 decrements role 1's counter by 1 (0,  0)
 *
 * This is benign, however an alternative sequence can present with a problem:
 *
 * role 0 enters x10rt_emu_barrier         (0,  0)
 * role 0 increments own counter by 2      (2,  0)
 * role 0 decrements role 0's counter by 1 (1,  0)
 * role 0 decrements role 1's counter by 1 (1, -1)
 * role 1 enters x10rt_emu_barrier         (1, -1)
 * role 1 increments own counter by 2      (1,  1)
 * role 1 decrements role 0's counter by 1 (0,  1) 
 * (at this point role 0 is released and can enter the next barrier in the program)
 * role 0 enters x10rt_emu_barrier         (0,  1)  ** new line
 * role 0 increments own counter by 2      (2,  1)  ** new line
 * role 0 decrements role 0's counter by 1 (1,  1)  ** new line
 * role 0 decrements role 1's counter by 1 (1,  0)  ** new line
 * role 1 decrements role 1's counter by 1 (0, -1)
 *
 * Now, role 0 is waiting for role 1 to decrement its counter, and role 1 is on -1.
 * Role 1 has to realise that it should release control to the program, so we use
 * the condition >0 rather than !=0 in the 'process' function above.
 */

// functions that define the shape of a balanced binary tree

// return role that acts as parent to a given role r
static x10rt_place get_parent (x10rt_place r, x10rt_place sz, x10rt_place root)
{
    x10rt_place rel_r = (r - root + sz) % sz;
    return  ((long(rel_r) - 1)/2 + root) % sz;
}

// given role r and size sz, provide the number of children under r and their identities
static x10rt_place get_children (x10rt_place r, x10rt_place sz, x10rt_place root, 
                                 x10rt_place &left, x10rt_place &right)
{
    assert(r<sz);
    x10rt_place rel_r = (r - root + sz) % sz;
    x10rt_place rel_left = rel_r*2 + 1;
    x10rt_place rel_right = rel_r*2 + 2;
    left = (rel_left + root) % sz;
    right = (rel_right + root) % sz;
    return x10rt_place(rel_left<sz) + x10rt_place(rel_right<sz);
}

static x10rt_msg_type BARRIER_C_TO_P_UPDATE_ID; // child to parent
static x10rt_msg_type REDUCE_C_TO_P_UPDATE_ID; // child to parent

static x10rt_msg_type BARRIER_P_TO_C_UPDATE_ID; // parent to child
static x10rt_msg_type BCAST_P_TO_C_UPDATE_ID; // parent to child

static void barrier_c_to_p_update_recv (const x10rt_msg_params *p)
{
    x10rt_deserbuf b;
    x10rt_deserbuf_init(&b, p);
    x10rt_team team; x10rt_deserbuf_read(&b, &team);
    x10rt_place role; x10rt_deserbuf_read(&b, &role);

    TeamObj &t = *gtdb[team];
    MemberObj &m = *t[role];
    //fprintf(stderr, "%d: Decrementing child from %d to %d\n", (int)role, (int) m.barrier.wait, (int) m.barrier.wait-1);
    SYNCHRONIZED (global_lock);
    m.barrier.childToReceive--;
}

static void reduce_c_to_p_update_recv (const x10rt_msg_params *p)
{
    x10rt_deserbuf b;
    x10rt_deserbuf_init(&b, p);
    x10rt_team team; x10rt_deserbuf_read(&b, &team);
    x10rt_place role; x10rt_deserbuf_read(&b, &role);
    size_t el; x10rt_deserbuf_read(&b, &el);
    size_t count; x10rt_deserbuf_read(&b, &count);

    TeamObj &t = *gtdb[team];
    MemberObj &m = *t[role];
    void* recv = malloc(el * count);
    x10rt_deserbuf_read_ex(&b, recv, el, count);


	SYNCHRONIZED (global_lock);
	if (m.reduce.rbuf != NULL) {
		// already a pending reduce - swap
		m.reduce.rbuf2 = m.reduce.rbuf;
		m.reduce.rbuf = NULL;
	}
	m.reduce.rbuf = recv;
	if (m.reduce.started) {
		m.reduce.ch(m.reduce.arg);
	}
	//fprintf(stderr, "%d: Decrementing child from %d to %d\n", (int)role, (int) m.barrier.wait, (int) m.barrier.wait-1);
	m.barrier.childToReceive--;
}

static void barrier_p_to_c_update_recv (const x10rt_msg_params *p)
{
    x10rt_deserbuf b;
    x10rt_deserbuf_init(&b, p);
    x10rt_team team; x10rt_deserbuf_read(&b, &team);
    x10rt_place role; x10rt_deserbuf_read(&b, &role);

    TeamObj &t = *gtdb[team];
    MemberObj &m = *t[role];
    //fprintf(stderr, "%d: Decrementing parent from %d to %d\n", (int)role, (int) m.barrier.parentToReceive, (int) m.barrier.parentToReceive-1);
    SYNCHRONIZED (global_lock);
    m.barrier.parentToReceive--;
}

static void bcast_p_to_c_update_recv (const x10rt_msg_params *p)
{
    x10rt_deserbuf b;
    x10rt_deserbuf_init(&b, p);
    x10rt_team team; x10rt_deserbuf_read(&b, &team);
    x10rt_place role; x10rt_deserbuf_read(&b, &role);

    TeamObj &t = *gtdb[team];
    MemberObj &m = *t[role];
    x10rt_deserbuf_read_ex(&b, m.bcast.dbuf, m.bcast.el, m.bcast.count);
    //fprintf(stderr, "%d: Decrementing parent from %d to %d\n", (int)role, (int) m.barrier.parentToReceive, (int) m.barrier.parentToReceive-1);
    SYNCHRONIZED (global_lock);
    m.barrier.parentToReceive--;
}

static void init_barrier (x10rt_msg_type *counter)
{
    x10rt_net_register_msg_receiver(BARRIER_C_TO_P_UPDATE_ID = (*counter)++, barrier_c_to_p_update_recv);
    x10rt_net_register_msg_receiver(REDUCE_C_TO_P_UPDATE_ID = (*counter)++, reduce_c_to_p_update_recv);
    x10rt_net_register_msg_receiver(BARRIER_P_TO_C_UPDATE_ID = (*counter)++, barrier_p_to_c_update_recv);
    x10rt_net_register_msg_receiver(BCAST_P_TO_C_UPDATE_ID = (*counter)++, bcast_p_to_c_update_recv);
}

bool CollOp::progress (void)
{
    TeamObj &t = *gtdb[team];
    MemberObj &m = *t[role];
    if (m.barrier.childToReceive > 0) {
        // still waiting for message from children, do nothing
        gtdb.fifo_push_back(this);
        return false;
    } else {
        handlePendingReduce(&m);
        if (m.barrier.parentToSend > 0) {
            // received messages from children, will now send to parent
            x10rt_place parent_role = get_parent(role, t.memberc, m.barrier.root);
            x10rt_place parent_role_place = t.placev[parent_role];
            if (parent_role_place==x10rt_net_here()) {
                //decrement counter locally;
                MemberObj *m2 = t.memberv[parent_role];
                assert(m2!=NULL);

                if (m.reduce.count > 0) {
                    void* recv = malloc(m.reduce.count * m.reduce.el);
                    memcpy(recv, m.reduce.dbuf, m.reduce.count * m.reduce.el);
                    m2->reduce.rbuf2 = recv;
                }
                {
                    SYNCHRONIZED (global_lock);
                    m2->barrier.childToReceive--;
                }
            } else {
                //send a message there to decrement the counter
                x10rt_serbuf b;
                if (m.reduce.count > 0) {
                    x10rt_serbuf_init(&b, parent_role_place,
                            REDUCE_C_TO_P_UPDATE_ID);
                } else {
                    x10rt_serbuf_init(&b, parent_role_place,
                            BARRIER_C_TO_P_UPDATE_ID);
                }
                x10rt_serbuf_write(&b, &team);
                x10rt_serbuf_write(&b, &parent_role);
                if (m.reduce.count > 0) {
                    x10rt_serbuf_write(&b, &(m.reduce.el));
                    x10rt_serbuf_write(&b, &(m.reduce.count));
                    x10rt_serbuf_write_ex(&b, m.reduce.dbuf, m.reduce.el,
                            m.reduce.count);
                }
                x10rt_net_send_msg(&b.p);
                x10rt_serbuf_free(&b);
            }
            {
                SYNCHRONIZED (global_lock);
                m.barrier.parentToSend--;
                if (m.reduce.count > 0) {
                    m.reduce.count = 0;
                    m.reduce.started = false;  // reduce completed
                }
            }
            gtdb.fifo_push_back(this);
            return true;
        } else {
            if (m.reduce.count > 0) {
                m.reduce.count = 0;
                m.reduce.started = false;  // reduce completed
            }
            if (m.barrier.parentToReceive > 0) {
                // still waiting for message from parent, do nothing
                gtdb.fifo_push_back(this);
                return false;
            } else {
                x10rt_place left, right;
                x10rt_place num_children = get_children(role, t.memberc,
                        m.barrier.root, left, right);
                for (unsigned i = 0; i < num_children; ++i) {
                    x10rt_place child_role = i == 0 ? left : right;
                    x10rt_place child_role_place = t.placev[child_role];
                    if (child_role_place == x10rt_net_here()) {
                        // decrement counter locally;
                        MemberObj *m2 = t.memberv[child_role];
                        assert(m2!=NULL);
                        if (m.bcast.count > 0) {
                            // perform bcast locally
                            memcpy(m2->bcast.dbuf, m.bcast.dbuf,
                                    m.bcast.count * m.bcast.el);
                        }
                        {
                            SYNCHRONIZED(global_lock);
                            m2->barrier.parentToReceive--;
                        }
                    } else {
                        //send a message there to decrement the counter
                        x10rt_serbuf b;
                        if (m.bcast.count > 0) {
                            x10rt_serbuf_init(&b, child_role_place,
                                    BCAST_P_TO_C_UPDATE_ID);
                        } else {
                            x10rt_serbuf_init(&b, child_role_place,
                                    BARRIER_P_TO_C_UPDATE_ID);
                        }
                        x10rt_serbuf_write(&b, &team);
                        x10rt_serbuf_write(&b, &child_role);
                        if (m.bcast.count > 0) {
                            x10rt_serbuf_write_ex(&b, m.bcast.dbuf,
                                    m.bcast.el, m.bcast.count);
                        }
                        x10rt_net_send_msg(&b.p);
                        x10rt_serbuf_free(&b);
                    }
                }
                safe_free(this);
                m.bcast.count = 0;  // bcast completed
                m.barrier.ch(m.barrier.arg);
                return true;
            }
        }
    }
}

// handle any reduction data that arrived before m called into the reduce
void CollOp::handlePendingReduce(MemberObj *m) {
    if (m->reduce.count > 0) {
        SYNCHRONIZED (global_lock);
        if (m->reduce.rbuf != NULL) {
            m->reduce.ch(m->reduce.arg);
        }
        if (m->reduce.rbuf2 != NULL) {
            m->reduce.rbuf = m->reduce.rbuf2;
            m->reduce.rbuf2 = NULL;
            m->reduce.ch(m->reduce.arg);
        }
    }
}

void x10rt_emu_barrier (x10rt_team team, x10rt_place role, x10rt_completion_handler *ch, void *arg)
{
    TeamObj &t = *gtdb[team];
    MemberObj &m = *t[role];
    x10rt_place left, right;
    x10rt_place num_children = get_children(role, t.memberc, m.barrier.root, left, right);
    x10rt_place parent = get_parent(role, t.memberc, m.barrier.root);
    {
        SYNCHRONIZED (global_lock);
        //fprintf(stderr, "%d: Incrementing from %d to %d\n", (int)role, m.barrier.wait, m.barrier.wait+t.memberc);
        m.barrier.childToReceive += num_children;
        if (parent!=role) m.barrier.parentToReceive++;
    }

    // send a message to parent if we have one and if childWait > 0
    m.barrier.parentToSend = parent==role ? 0 : 1;
    m.barrier.ch = ch;
    m.barrier.arg = arg;

    if (ch!=NULL) {
        //if (x10rt_net_here()==0) fprintf(stderr,"before pushd\n");
        gtdb.fifo_push_back(new (safe_malloc<CollOp>()) CollOp(team, role));
    }
}

static void scatter_copy_recv (const x10rt_msg_params *p)
{
    x10rt_deserbuf b;
    x10rt_deserbuf_init(&b, p);
    x10rt_team team; x10rt_deserbuf_read(&b, &team);
    x10rt_place role; x10rt_deserbuf_read(&b, &role);
    TeamObj &t = *gtdb[team];
    MemberObj &m = *t[role];
    x10rt_deserbuf_read_ex(&b, m.scatter.dbuf, m.scatter.el, m.scatter.count);

    SYNCHRONIZED (global_lock);
    m.scatter.data_done = true;
    if (m.scatter.barrier_done && m.scatter.ch != NULL) {
        PREEMPT (global_lock);
        m.scatter.ch(m.scatter.arg);
    }
}

namespace {
    struct callback_arg {
        x10rt_team team;
        x10rt_place role;
    };
}

static void scatter_after_barrier (void *arg)
{
    MemberObj &m = *(static_cast<MemberObj*>(arg));
    TeamObj &t = *gtdb[m.team];

    if (m.scatter.root == m.role) {

        // send data to everyone
        for (x10rt_place i=0 ; i<t.memberc ; ++i) {
            x10rt_place role_place = t.placev[i];
            const char *sbuf_ = reinterpret_cast<const char*>(m.scatter.sbuf);
            const char *sbuf = &sbuf_[i * m.scatter.count * m.scatter.el];
            if (role_place==x10rt_net_here()) {
                MemberObj *m2 = t.memberv[i];
                assert(m2!=NULL);
                memcpy(m2->scatter.dbuf, sbuf, m.scatter.count * m.scatter.el);
                SYNCHRONIZED (global_lock);
                m2->scatter.data_done = true;
                if (m2->scatter.barrier_done && m2->scatter.ch != NULL) {
                    PREEMPT (global_lock);
                    m2->scatter.ch(m2->scatter.arg);
                }
            } else {
                // serialise all the data
                // TODO: hoist some of this serialisation out of the loop (reuse buffer)
                x10rt_serbuf b;
                x10rt_serbuf_init(&b, role_place, SCATTER_COPY_ID);
                x10rt_serbuf_write(&b, &m.team);
                x10rt_serbuf_write(&b, &i);
                x10rt_serbuf_write_ex(&b, sbuf, m.scatter.el, m.scatter.count);
                x10rt_net_send_msg(&b.p);
                x10rt_serbuf_free(&b);
            }
        }

        // the barrier must have completed or we wouldn't even be here
        // signal completion to root role
        if (m.scatter.ch != NULL) {
            m.scatter.ch(m.scatter.arg);
        }

    } else {

        // if we have already received the data (rare) then signal completion to non-root role
        SYNCHRONIZED (global_lock);
        m.scatter.barrier_done = true;
        if (m.scatter.data_done && m.scatter.ch != NULL) {
            PREEMPT (global_lock);
            m.scatter.ch(m.scatter.arg);
        }
    }
}

void x10rt_emu_scatter (x10rt_team team, x10rt_place role,
                      x10rt_place root, const void *sbuf, void *dbuf,
                      size_t el, size_t count, x10rt_completion_handler *ch, void *arg)
{
    TeamObj &t = *gtdb[team];

    MemberObj &m = *t[role];

    m.scatter.root = root;
    m.scatter.sbuf = sbuf;
    m.scatter.dbuf = dbuf;
    m.scatter.el = el;
    m.scatter.count = count;
    m.scatter.ch = ch;
    m.scatter.arg = arg;
    m.scatter.barrier_done = false;
    m.scatter.data_done = false;

    // FIXME: there is currently no support for preventing, warning, or
    // accepting two 'concurrent' collective operations from the same role of
    // the same team.  In other words.  This looks like an atomicity violation
    // here because m.scatter.* may change but this in fact will not happen
    // unless operations are invoked in this way..

    x10rt_emu_barrier (team, role, scatter_after_barrier, &m);

    // after barrier:
    // root sends to everyone else and immediately signals completion
    // everyone else signals completion when they receive root's message
    //    AND after the barrier is done
    // if you don't wait until after the barrier is done then the next barrier will
    // 'run into' the current barrier causing race conditions
}

void x10rt_emu_bcast (x10rt_team team, x10rt_place role,
                      x10rt_place root, const void *sbuf, void *dbuf,
                      size_t el, size_t count, x10rt_completion_handler *ch, void *arg)
{
    TeamObj &t = *gtdb[team];

    MemberObj &m = *t[role];

    m.bcast.root = root;
    m.bcast.sbuf = sbuf;
    m.bcast.dbuf = dbuf;
    m.bcast.el = el;
    m.bcast.count = count;
    if (m.bcast.root == m.role) {
        // root just copies from source to destination
        memcpy(m.bcast.dbuf, m.bcast.sbuf, m.bcast.count * m.bcast.el);
    }

    m.barrier.root = root;

    x10rt_emu_barrier (team, role, ch, arg);
}


static void alltoall_intermediate (void *arg)
{
    MemberObj &m = *(static_cast<MemberObj*>(arg));

    // do lots of scatters then a barrier
    if (m.alltoall.counter > 0) {
        x10rt_place root = --m.alltoall.counter;
        char *dbuf = static_cast<char*>(m.alltoall.dbuf);
        size_t el=m.alltoall.el, count=m.alltoall.count;
        // FIXME: would be good to avoid the extra scatters within the bcast
        x10rt_emu_scatter(m.team, m.role, root,
                          m.alltoall.sbuf,
                          &dbuf[el*count*root],
                          el, count,
                          alltoall_intermediate, arg);
    } else {
        //fprintf(stderr, "Finished alltoall send at role %d\n", cbarg->role);
        x10rt_emu_barrier(m.team, m.role, m.alltoall.ch, m.alltoall.arg);
    }
}

void x10rt_emu_alltoall (x10rt_team team, x10rt_place role,
                         const void *sbuf, void *dbuf,
                         size_t el, size_t count, x10rt_completion_handler *ch, void *arg)
{
    TeamObj &t = *gtdb[team];

    MemberObj &m = *t[role];

    m.alltoall.sbuf = sbuf;
    m.alltoall.dbuf = dbuf;
    m.alltoall.el = el;
    m.alltoall.count = count;
    m.alltoall.ch = ch;
    m.alltoall.arg = arg;
    m.alltoall.counter = t.memberc;

    x10rt_emu_barrier(team, role, alltoall_intermediate, &m);

    // after barrier:
    // everyone does a bcast then a barrier
    // after barrier:
    // everyone signals completion
}

namespace {

    // help avoid warnings about functions not returning when they call abort()
    template<class T> T abortv (void) { abort(); return T(); }

    template<class T> T zero (void) { return 0; }
    template<class T> T one (void) { return 1; }
    template<> x10rt_dbl_s32 zero<x10rt_dbl_s32> (void) { x10rt_dbl_s32 r; r.val = 0.0; r.idx = 0; return r; }
    template<> x10rt_dbl_s32 one<x10rt_dbl_s32> (void) { x10rt_dbl_s32 r; r.val = 1.0; r.idx = 1; return r; }

    template<class T> T min (void) { return 0; } // cover unsigned cases
    template<> int8_t   min<int8_t>   (void) { return (int8_t)0x80; }
    template<> int16_t  min<int16_t>  (void) { return (int16_t)0x8000; }
    template<> int32_t  min<int32_t>  (void) { return (int32_t)0x80000000; }
    template<> int64_t  min<int64_t>  (void) { return (int64_t)0x8000000000000000ULL; }
    template<> double   min<double>   (void) { return -DBL_MAX; }
    template<> float    min<float>    (void) { return -FLT_MAX; }

    template<class T> T max (void) { T::error(); } // specialised
    template<> uint8_t  max<uint8_t>  (void) { return 0xFF; }
    template<> int8_t   max<int8_t>   (void) { return 0x7F; }
    template<> int16_t  max<int16_t>  (void) { return 0x7FFF; }
    template<> uint16_t max<uint16_t> (void) { return 0xFFFF; }
    template<> int32_t  max<int32_t>  (void) { return 0x7FFFFFFF; }
    template<> uint32_t max<uint32_t> (void) { return 0xFFFFFFFF; }
    template<> int64_t  max<int64_t>  (void) { return 0x7FFFFFFFFFFFFFFFULL; }
    template<> uint64_t max<uint64_t> (void) { return 0xFFFFFFFFFFFFFFFFULL; }
    template<> double   max<double>   (void) { return DBL_MAX; }
    template<> float    max<float>    (void) { return FLT_MAX; }

    // these guys return max for the s32 part because they should always
    // return the lowest index in the case of equivalent values
    template<> x10rt_dbl_s32 min<x10rt_dbl_s32> (void) {
        x10rt_dbl_s32 r = {min<double>(), max<int32_t>()};
        return r;
    }
    template<> x10rt_dbl_s32 max<x10rt_dbl_s32> (void) {
        x10rt_dbl_s32 r = {max<double>(), max<int32_t>()};
        return r;
    }

    // value which when added to a reduction does not affect the result

    // should never hit this, check specialisations are working
    template<class T, x10rt_red_op_type op> struct ident{ static T _ (void) { T::error()(); } };
    template<class T> struct ident<T,X10RT_RED_OP_ADD> { static T _ (void) { return zero<T>(); } };
    template<class T> struct ident<T,X10RT_RED_OP_MUL> { static T _ (void) { return one<T>(); } };
    template<class T> struct ident<T,X10RT_RED_OP_AND> { static T _ (void) { return one<T>(); } };
    template<class T> struct ident<T,X10RT_RED_OP_OR>  { static T _ (void) { return zero<T>(); } };
    template<class T> struct ident<T,X10RT_RED_OP_XOR> { static T _ (void) { return zero<T>(); } };
    template<class T> struct ident<T,X10RT_RED_OP_MAX> { static T _ (void) { return min<T>(); } };
    template<class T> struct ident<T,X10RT_RED_OP_MIN> { static T _ (void) { return max<T>(); } };

    // actual reduction ops

    // should never hit this, check specialisations are working
    template<class T, x10rt_red_op_type op> struct reduce { static T _ (const T &a, const T &b)
    { T::error(); } };

    template<class T> struct reduce<T,X10RT_RED_OP_ADD> { static T _ (const T &a, const T &b)
    { return a + b; } };
    template<class T> struct reduce<T,X10RT_RED_OP_MUL> { static T _ (const T &a, const T &b)
    { return a * b; } };
    template<class T> struct reduce<T,X10RT_RED_OP_AND> { static T _ (const T &a, const T &b)
    { return a & b; } };
    template<class T> struct reduce<T,X10RT_RED_OP_OR>  { static T _ (const T &a, const T &b)
    { return a | b; } };
    template<class T> struct reduce<T,X10RT_RED_OP_XOR> { static T _ (const T &a, const T &b)
    { return a ^ b; } };
    template<class T> struct reduce<T,X10RT_RED_OP_MAX> { static T _ (const T &a, const T &b)
    { return a>=b?a:b; } };
    template<class T> struct reduce<T,X10RT_RED_OP_MIN> { static T _ (const T &a, const T &b)
    { return a<=b?a:b; } };

    static float bitwise_err (void)
    {
        fprintf(stderr, "X10RT: Cannot do bitwise arithmetic on floating point values.\n");
        return abortv<float>();
    }
    // special cases for floats, we just return 0 as they are not valid to call anyway
    template<> struct reduce<float,X10RT_RED_OP_AND>
    { static float _ (const float &, const float &) { return bitwise_err(); } };
    template<> struct reduce<float,X10RT_RED_OP_OR>
    { static float _ (const float &, const float &) { return bitwise_err(); } };
    template<> struct reduce<float,X10RT_RED_OP_XOR>
    { static float _ (const float &, const float &) { return bitwise_err(); } };
    template<> struct reduce<double,X10RT_RED_OP_AND>
    { static double _ (const double &, const double &) { return bitwise_err(); } };
    template<> struct reduce<double,X10RT_RED_OP_OR>
    { static double _ (const double &, const double &) { return bitwise_err(); } };
    template<> struct reduce<double,X10RT_RED_OP_XOR>
    { static double _ (const double &, const double &) { return bitwise_err(); } };


    static std::complex<double> bitwise_err_complex (void)
    {
        fprintf(stderr, "X10RT: Cannot do bitwise arithmetic on complex values.\n");
        return abortv<std::complex<double> >();
    }
    static std::complex<double> compare_err_complex (void)
    {
        fprintf(stderr, "X10RT: Cannot do compare on complex values.\n");
        return abortv<std::complex<double> >();
    }
    template<> struct reduce<std::complex<double>,X10RT_RED_OP_AND>
    { static std::complex<double> _ (const std::complex<double> &, const std::complex<double> &) { return bitwise_err_complex(); } };
    template<> struct reduce<std::complex<double>,X10RT_RED_OP_OR>
    { static std::complex<double> _ (const std::complex<double> &, const std::complex<double> &) { return bitwise_err_complex(); } };
    template<> struct reduce<std::complex<double>,X10RT_RED_OP_XOR>
    { static std::complex<double> _ (const std::complex<double> &, const std::complex<double> &) { return bitwise_err_complex(); } };
    template<> struct reduce<std::complex<double>,X10RT_RED_OP_MAX>
    { static std::complex<double> _ (const std::complex<double> &, const std::complex<double> &) { return compare_err_complex(); } };
    template<> struct reduce<std::complex<double>,X10RT_RED_OP_MIN>
    { static std::complex<double> _ (const std::complex<double> &, const std::complex<double> &) { return compare_err_complex(); } };
    
    static x10rt_dbl_s32 arith_err (void)
    {
        fprintf(stderr, "X10RT: Cannot do arithmetic on paired values.\n");
        return abortv<x10rt_dbl_s32>();
    }
    template<> struct reduce<x10rt_dbl_s32,X10RT_RED_OP_ADD>
    { static x10rt_dbl_s32 _ (const x10rt_dbl_s32 &, const x10rt_dbl_s32 &) {return arith_err();} };
    template<> struct reduce<x10rt_dbl_s32,X10RT_RED_OP_MUL>
    { static x10rt_dbl_s32 _ (const x10rt_dbl_s32 &, const x10rt_dbl_s32 &) {return arith_err();} };
    template<> struct reduce<x10rt_dbl_s32,X10RT_RED_OP_AND>
    { static x10rt_dbl_s32 _ (const x10rt_dbl_s32 &, const x10rt_dbl_s32 &) {return arith_err();} };
    template<> struct reduce<x10rt_dbl_s32,X10RT_RED_OP_OR>
    { static x10rt_dbl_s32 _ (const x10rt_dbl_s32 &, const x10rt_dbl_s32 &) {return arith_err();} };
    template<> struct reduce<x10rt_dbl_s32,X10RT_RED_OP_XOR>
    { static x10rt_dbl_s32 _ (const x10rt_dbl_s32 &, const x10rt_dbl_s32 &) {return arith_err();} };
    template<> struct reduce<x10rt_dbl_s32,X10RT_RED_OP_MAX> {
        static x10rt_dbl_s32 _ (const x10rt_dbl_s32 &a, const x10rt_dbl_s32 &b) {
            if (a.val<b.val) return b;
            else if (a.val>b.val) return a;
            else return a.idx<=b.idx ? a : b;
        }
    };
    template<> struct reduce<x10rt_dbl_s32,X10RT_RED_OP_MIN> {
        static x10rt_dbl_s32 _ (const x10rt_dbl_s32 &a, const x10rt_dbl_s32 &b) {
            if (a.val<b.val) return a;
            else if (a.val>b.val) return b;
            else return a.idx<=b.idx ? a : b;
        }
    };

    template<x10rt_red_op_type op, x10rt_red_type dtype>
    void reduce3 (void *arg)
    {
        MemberObj &m = *(static_cast<MemberObj*>(arg));

        typedef typename x10rt_red_type_info<dtype>::Type T;
        T *tmp;
        tmp = reinterpret_cast<T*>(m.reduce.rbuf);
        m.reduce.rbuf = NULL;
        for (size_t i=0 ; i<m.reduce.count ; ++i) {
            T &dest = static_cast<T*>(m.reduce.dbuf)[i];
            dest = reduce<T,op>::_(dest,tmp[i]);
        }
        ::safe_free(tmp);
    }

    template<x10rt_red_op_type op, x10rt_red_type dtype>
    void reduce2 (x10rt_team team, x10rt_place role, x10rt_place root, 
                    const void *sbuf, void *dbuf, size_t count, 
                    x10rt_completion_handler *ch, void *arg,
                    bool allreduce)
    {
        TeamObj &t = *gtdb[team];

        MemberObj &m = *t[role];

        m.reduce.root = root;
        m.reduce.sbuf = sbuf;
        m.reduce.dbuf = dbuf;
        m.reduce.el = sizeof(typename x10rt_red_type_info<dtype>::Type);
        m.reduce.count = count;
        m.reduce.ch = reduce3<op,dtype>;
        m.reduce.arg = &m;

        if (allreduce) {
            // as reduce is piggybacked on a blocking barrier, just add a bcast
            m.bcast.root = root;
            m.bcast.sbuf = dbuf; // use result from reduce
            m.bcast.dbuf = dbuf;
            m.bcast.el = m.reduce.el;
            m.bcast.count = count;
        }

        m.barrier.root = root;

        memcpy(m.reduce.dbuf, m.reduce.sbuf, m.reduce.count * m.reduce.el);
        m.reduce.started = true;

        x10rt_emu_barrier (team, role, ch, arg);
    }

    template<x10rt_red_type dtype>
    void reduce1 (x10rt_team team, x10rt_place role, x10rt_place root,
                  const void *sbuf, void *dbuf, x10rt_red_op_type op, size_t count,
                  x10rt_completion_handler *ch, void *arg,
                  bool allreduce)
    {
        switch (op) {
            #define BORING_MACRO(x) \
            case x: reduce2<x,dtype>(team,role,root,sbuf,dbuf,count,ch,arg,allreduce); return
            BORING_MACRO(X10RT_RED_OP_ADD);
            BORING_MACRO(X10RT_RED_OP_MUL);
            BORING_MACRO(X10RT_RED_OP_AND);
            BORING_MACRO(X10RT_RED_OP_OR);
            BORING_MACRO(X10RT_RED_OP_XOR);
            BORING_MACRO(X10RT_RED_OP_MAX);
            BORING_MACRO(X10RT_RED_OP_MIN);
            #undef BORING_MACRO
            default: fprintf(stderr, "Corrupted operation? %x\n", op); if (ABORT_NEEDED && !x10rt_run_as_library()) abort();
        }
    }
}

void x10rt_emu_reduce (x10rt_team team, x10rt_place role, x10rt_place root,
                       const void *sbuf, void *dbuf, x10rt_red_op_type op,
                       x10rt_red_type dtype, size_t count,
                       x10rt_completion_handler *ch, void *arg,
                       bool allreduce)
{
    switch (dtype) {
        #define BORING_MACRO(x) \
        case x: reduce1<x>(team,role,root,sbuf,dbuf,op,count,ch,arg,allreduce); return
        BORING_MACRO(X10RT_RED_TYPE_U8);
        BORING_MACRO(X10RT_RED_TYPE_S8);
        BORING_MACRO(X10RT_RED_TYPE_S16);
        BORING_MACRO(X10RT_RED_TYPE_U16);
        BORING_MACRO(X10RT_RED_TYPE_S32);
        BORING_MACRO(X10RT_RED_TYPE_U32);
        BORING_MACRO(X10RT_RED_TYPE_S64);
        BORING_MACRO(X10RT_RED_TYPE_U64);
        BORING_MACRO(X10RT_RED_TYPE_DBL);
        BORING_MACRO(X10RT_RED_TYPE_FLT);
        BORING_MACRO(X10RT_RED_TYPE_DBL_S32);
        BORING_MACRO(X10RT_RED_TYPE_COMPLEX_DBL);
        #undef BORING_MACRO
        default: fprintf(stderr, "Corrupted type? %x\n", dtype); if (ABORT_NEEDED && !x10rt_run_as_library()) abort();
    }
}

void x10rt_emu_allreduce (x10rt_team team, x10rt_place role,
                          const void *sbuf, void *dbuf, x10rt_red_op_type op,
                          x10rt_red_type dtype, size_t count,
                          x10rt_completion_handler *ch, void *arg)
{
    x10rt_emu_reduce(team, role, 0, sbuf, dbuf, op, dtype, count, ch, arg, true);
}

static void split_recv (const x10rt_msg_params *p)
{
    x10rt_deserbuf b;
    x10rt_deserbuf_init(&b, p);
    x10rt_team team; x10rt_deserbuf_read(&b, &team);
    x10rt_place role; x10rt_deserbuf_read(&b, &role);
    x10rt_team new_team; x10rt_deserbuf_read(&b, &new_team);

    TeamObj &t = *gtdb[team];
    MemberObj &m = *t[role];

    if (m.split.ch != NULL) {
        m.split.ch(new_team, m.split.arg);
    }
}

static void receive_new_team (x10rt_team new_team, void *arg)
{
    // called at the first role of a new team, needs to distribute the team to other places
    // in the new team and call their callbacks
    // m.team is the parent team
    // new_team is the new team
    MemberObj &m = *(static_cast<MemberObj*>(arg));
    TeamObj &parent_t = *gtdb[m.team];

    for (x10rt_place i=0 ; i<m.split.newTeamSz ; ++i) {
        // send team id to every member in the list
        x10rt_place mr = m.split.newTeamOldRoles[i];
        x10rt_place mp = m.split.newTeamPlaces[i];
        assert(parent_t.placev[mr] == mp);
        if (mp == x10rt_net_here()) {
            MemberObj &mmobj = *parent_t[mr];
            // just run callback
            if (mmobj.split.ch != NULL) {
                mmobj.split.ch(new_team, mmobj.split.arg);
            }
        } else {
            // send message
            x10rt_serbuf b2;
            x10rt_serbuf_init(&b2, mp, SPLIT_ID);
            x10rt_serbuf_write(&b2, &m.team);
            x10rt_serbuf_write(&b2, &mr);
            x10rt_serbuf_write(&b2, &new_team);
            x10rt_net_send_msg(&b2.p);
            x10rt_serbuf_free(&b2);
        }
    }

    safe_free(m.split.newTeamOldRoles);
    safe_free(m.split.newTeamPlaces);
}

static void split (void *arg)
{
    MemberObj &m = *(static_cast<MemberObj*>(arg));
    TeamObj &t = *gtdb[m.team];

    //m.split.rbuf now contains all the colours chosen by everyone in the current team
    //m.split.role contains our role within the colour we chose
    x10rt_place our_color = m.split.rbuf[m.role];

    x10rt_place first = -1;
    x10rt_place new_team_sz = 0;
    {
        bool found_first = false;
        for (x10rt_place i=0 ; i<t.memberc ; ++i) {
            if (m.split.rbuf[i]==our_color) {
                new_team_sz++;
                if (!found_first) {
                    found_first = true;
                    first = i;
                }
            }
        }
        assert(found_first);
    }

    // first is the member of the new team (whose colour we chose) who will be
    // responsible for making the new team and distributing the new id.  the
    // value 'first' is a role in the parent team.

    // if we are first...
    if (m.role == first) {
        x10rt_place *new_team_places = safe_malloc<x10rt_place>(new_team_sz);
        x10rt_place *new_team_old_roles = safe_malloc<x10rt_place>(new_team_sz);

        {
            // filter rbuf down to just our colour (and remember its place in the original array)
            x10rt_place counter = 0;
            for (x10rt_place i=0 ; i<t.memberc ; ++i) {
                if (m.split.rbuf[i]==our_color) {
                    new_team_old_roles[counter] = i;
                    new_team_places[counter] = t.placev[i];
                    counter++;
                }
            }
            assert(counter==new_team_sz);
        }

        assert(new_team_places[m.split.role] == x10rt_net_here());

        m.split.newTeamPlaces = new_team_places;
        m.split.newTeamOldRoles = new_team_old_roles;
        m.split.newTeamSz = new_team_sz;
    
        // the first team member creates the team (this does communication everywhere)
        x10rt_emu_team_new(new_team_sz, new_team_places, receive_new_team, &m);
    }

    safe_free(m.split.rbuf);
    safe_free(m.split.sbuf);
}

void x10rt_emu_team_split (x10rt_team parent, x10rt_place parent_role,
                           x10rt_place color, x10rt_place new_role,
                           x10rt_completion_handler2 *ch, void *arg)
{
    // allocate memory
    // do the alltoall
    // calculate reduction locally
    // free memory
    // signal completion

    TeamObj &t = *gtdb[parent];
    MemberObj &m = *t[parent_role];

    m.split.rbuf = safe_malloc<x10rt_place>(t.memberc);
    m.split.role = new_role;
    m.split.sbuf = safe_malloc<x10rt_place>(t.memberc);
    m.split.ch = ch;
    m.split.arg = arg;

    for (x10rt_place p=0 ; p<x10rt_emu_team_sz(parent) ; ++p) m.split.sbuf[p] = color;

    x10rt_emu_alltoall(parent, parent_role, m.split.sbuf, m.split.rbuf,
                       sizeof(x10rt_place), 1,
                       split, &m);
}

void x10rt_emu_coll_init (x10rt_msg_type *counter)
{
    // create the world team
    x10rt_place *memberv = safe_malloc<x10rt_place>(x10rt_net_nhosts());
    for (x10rt_place i=0 ; i<x10rt_net_nhosts() ; ++i) {
        memberv[i] = i;
    }
    x10rt_team world = gtdb.allocTeam(x10rt_net_nhosts(), memberv);
    assert(world==0);

    x10rt_net_register_msg_receiver(TEAM_NEW_PLACE_ZERO_ID = (*counter)++,
                                    team_new_place_zero_recv);

    x10rt_net_register_msg_receiver(TEAM_NEW_ID = (*counter)++,
                                    team_new_recv);

    x10rt_net_register_msg_receiver(TEAM_NEW_FINISHED_ID = (*counter)++,
                                    team_new_finished_recv);

    init_barrier(counter);

    x10rt_net_register_msg_receiver(SCATTER_COPY_ID = (*counter)++,
                                    scatter_copy_recv);

    x10rt_net_register_msg_receiver(SPLIT_ID = (*counter)++,
                                    split_recv);

}

void x10rt_emu_coll_finalize (void)
{
    gtdb.releaseTeam(0);
}

bool x10rt_emu_coll_probe (void)
{
    unsigned iterations = gtdb.fifo_size();
    bool progressing = false;
    for (unsigned i=0 ; i<iterations ; ++i) {
        CollOp *op = gtdb.fifo_pop();
        if (op == NULL) break; // can happen if the queue shrinks while we're in the loop
        progressing = op->progress() || progressing;
    }
    return progressing;
}
