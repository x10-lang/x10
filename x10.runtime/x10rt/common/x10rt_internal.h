/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

#ifndef X10RT_INTERNAL_H
#define X10RT_INTERNAL_H

#include <cstring>
#include <cstdarg>
#include <cstdio>

#include <x10rt_types.h>

// set false to remove abort
#define ABORT_NEEDED	true

#if 1
#include <pthread.h>
namespace {
    struct Lock {
        pthread_mutex_t lock;
        Lock (void) { pthread_mutex_init(&lock, NULL); }
        ~Lock (void) { pthread_mutex_destroy(&lock); }
        void acquire (void) { pthread_mutex_lock(&lock); }
        void release (void) { pthread_mutex_unlock(&lock); }
    };
}

#else
// win32 implementation
#endif

template<class T> static inline T* safe_malloc (size_t f=1, size_t a=0) {
    size_t sz = f*sizeof(T) + a;
    return sz==0 ? 0 : (T*)malloc(sz);
}

template<class T> static inline T* safe_realloc (T *old, size_t f=1, size_t a=0) {
    size_t sz = f*sizeof(T) + a;
    return sz==0 && old==NULL ? 0 : (T*)realloc(old, sz);
}

static inline void safe_free (void *p) { free (p); }

static inline bool checkBoolEnvVar(char* value) {
	return (value && !(strcasecmp("false", value) == 0) && !(strcasecmp("0", value) == 0) && !(strcasecmp("f", value) == 0));
}

namespace {

    // Lookup table from message id to callback function
    template<class T> struct Table {
        T *arrv;
        size_t arrc;
        Table() : arrv(NULL), arrc(0) { }
        unsigned reg (const T &v) {
            reg(arrc, v);
            return arrc-1;
        }
        void ensureSize (unsigned id) {
            if (id >= arrc) {
                arrv = (T*) ::realloc(arrv, sizeof(T)*(id+1));
                ::memset(&arrv[arrc], 0, ((id+1)-arrc)*sizeof(T));
                arrc = id+1;
            }
        }
        void reg (unsigned id, const T &v) {
            ensureSize(id);
            arrv[id] = v;
        }
        const T &operator [] (unsigned id) { return arrv[id]; }
    };

    template<class T> struct FifoElement {
        T *next;
        FifoElement (void) : next(NULL) { }
        virtual ~FifoElement (void) { }
    };  

    template<class T> struct Fifo {
        int size; // number of ops held here
        T *fifo_b, *fifo_e; // begin/end of fifo
        T *current; // op currently associated with the stream
        Fifo () 
          : size(0), fifo_b(NULL), fifo_e(NULL), current(NULL)
        { } 
        ~Fifo ()
        {
        }
        void push_back (T *op)
        {
            if (fifo_e == NULL) {
                fifo_b = op;
                fifo_e = op;
            } else {
                fifo_e->next = op;
                fifo_e = op; 
            }   
            size++;
        }
        
        // pop from front
        T *pop (void)
        { 
            T *op = fifo_b;
            if (op==NULL) return op;
            fifo_b = op->next;
            // case for empty queue
            if (fifo_b == NULL) fifo_e = NULL;
            size--;
            op->next = NULL;
            return op;
        }   
        
    };  
}

X10RT_C void x10rt_emu_init (x10rt_msg_type *counter);

X10RT_C void x10rt_emu_remote_op (x10rt_place place, x10rt_remote_ptr remote_addr,
                                  x10rt_op_type type, unsigned long long value);

X10RT_C void x10rt_emu_remote_ops (x10rt_remote_op_params *ops, size_t num_ops);

X10RT_C void x10rt_emu_coll_init (x10rt_msg_type *counter);

X10RT_C void x10rt_emu_coll_finalize (void);

X10RT_C void x10rt_emu_team_new (x10rt_place placec, x10rt_place *placev,
                                 x10rt_completion_handler2 *ch, void *arg);
    
X10RT_C void x10rt_emu_team_del (x10rt_team team, x10rt_place role,
                                 x10rt_completion_handler *ch, void *arg);

X10RT_C x10rt_place x10rt_emu_team_sz (x10rt_team team);

X10RT_C void x10rt_emu_team_split (x10rt_team parent, x10rt_place parent_role,
                                   x10rt_place color, x10rt_place new_role,
                                   x10rt_completion_handler2 *ch, void *arg);
    
X10RT_C void x10rt_emu_barrier (x10rt_team team, x10rt_place role,
                                x10rt_completion_handler *ch, void *arg);
    
X10RT_C void x10rt_emu_bcast (x10rt_team team, x10rt_place role,
                              x10rt_place root, const void *sbuf, void *dbuf,
                              size_t el, size_t count,
                              x10rt_completion_handler *ch, void *arg);
    
X10RT_C void x10rt_emu_scatter (x10rt_team team, x10rt_place role,
                                x10rt_place root, const void *sbuf, void *dbuf,
                                size_t el, size_t count,
                                x10rt_completion_handler *ch, void *arg);
    
X10RT_C void x10rt_emu_alltoall (x10rt_team team, x10rt_place role,
                                 const void *sbuf, void *dbuf,
                                 size_t el, size_t count,
                                 x10rt_completion_handler *ch, void *arg);

X10RT_C void x10rt_emu_reduce (x10rt_team team, x10rt_place role,
                                x10rt_place root, const void *sbuf, void *dbuf,
                                x10rt_red_op_type op,
                                x10rt_red_type dtype,
                                size_t count,
                                x10rt_completion_handler *ch, void *arg,
                                bool allreduce);




X10RT_C bool x10rt_emu_coll_probe (void);


#endif
