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

#ifndef X10_LANG_DEQUE_H
#define X10_LANG_DEQUE_H

#include <x10/lang/X10Class.h>

#include <x10aux/serialization.h>
#include <x10aux/atomic_ops.h>

namespace x10 {
    namespace lang {

       /**
        * A Deque for use by the workstealing implementation.
        *
        * Some code of this class is derived from a Java implementation that was
        * written by Doug Lea with assistance from members of JCP JSR-166
        * Expert Group and released to the public domain, as explained at
        * http://creativecommons.org/licenses/publicdomain
        */
        class Deque : public ::x10::lang::X10Class {
        public:
            RTT_H_DECLS_CLASS;

            static Deque* _make();

            Deque* _constructor();

            static const ::x10aux::serialization_id_t _serialization_id;

            virtual ::x10aux::serialization_id_t _get_serialization_id() { return _serialization_id; };

            virtual void _serialize_body(::x10aux::serialization_buffer &buf);

            static ::x10::lang::Reference* _deserializer(::x10aux::deserialization_buffer &buf);

            virtual void _deserialize_body(::x10aux::deserialization_buffer& buf);

#ifndef __FCC_VERSION
        private:
#else
        public:
#endif
            struct Slots {
            public:
                x10_int capacity;
                volatile void** data;
            };
            template<class T> friend const char *::x10aux::typeName();


            /**
             * Add in store-order the given task at given slot of q.
             * Caller must ensure q is nonnull and index is in range.
             */
            inline void setSlot(Slots *q, int i, ::x10::lang::Any *t) {
                q->data[i] = t;
                ::x10aux::atomic_ops::store_store_barrier();
            }


            /**
             * CAS given slot of q to null. Caller must ensure q is nonnull
             * and index is in range.
             */
            inline bool casSlotNull(Slots *q, int i, ::x10::lang::Any* t) {
                return ::x10aux::atomic_ops::compareAndSet_ptr(&(q->data[i]), t, NULL) == t;
            }

            /**
             * Sets sp in store-order.
             */
            inline void storeSp(int s) {
                sp = s;
                ::x10aux::atomic_ops::store_store_barrier();
            }

            /**
             * Doubles queue array size. Transfers elements by emulating
             * steals (deqs) from old array and placing, oldest first, into
             * new array.
             */
            void growQueue();

        public:
            /**
             * Pushes a task. Called only by current thread.
             * @param t the task. Caller must ensure nonnull
             */
            void push(::x10::lang::Any* t) {
                Slots *q = queue;
                int mask = q->capacity - 1;
                int s = sp;
                setSlot(q, s & mask, t);
                storeSp(++s);
                if ((s -= base) == 1) {
                    ;
                } else if (s >= mask) {
                    growQueue();
                }
            }

            /**
             * Tries to take a task from the base of the queue, failing if
             * either empty or contended.
             * @return a task, or null if none or contended.
             */
            ::x10::lang::Any* steal();

            /**
             * Returns a popped task, or null if empty. Ensures active status
             * if nonnull. Called only by current thread.
             */
            ::x10::lang::Any* poll() {
                int s = sp;
                while (s != base) {
                    Slots *q = queue;
                    int mask = q->capacity - 1;
                    int i = (s - 1) & mask;
                    Any *t = (Any*)(q->data[i]);
                    if (t == NULL || !casSlotNull(q, i, t))
                        break;
                    storeSp(s - 1);
                    return t;
                }
                return NULL;
            }

            /**
             * Returns next task to pop.
             */
            inline ::x10::lang::Any* peekTask() {
                Slots *q = queue;
                return q == NULL ? NULL : (::x10::lang::Any*)(q->data[(sp - 1) & (q->capacity - 1)]);
            }

            /**
             * Returns an estimate of the number of tasks in the queue.
             */
            inline int size() {
                int n = sp - base;
                return n < 0 ? 0 : n; // suppress momentarily negative values
            }

        private:
            /**
             * Capacity of work-stealing queue array upon initialization.
             * Must be a power of two. Initial size must be at least 2, but is
             * padded to minimize cache effects.
             */
            static const int INITIAL_QUEUE_CAPACITY = 1 << 13;

            /**
             * Maximum work-stealing queue array size.  Must be less than or
             * equal to 1 << 28 to ensure lack of index wraparound. (This
             * is less than usual bounds, because we need leftshift by 3
             * to be in int range).
             */
            static const int MAXIMUM_QUEUE_CAPACITY = 1 << 28;

            /**
             * Backing array for queue
             */
            Slots *queue;

            /**
             * top of stack index
             */
            volatile int sp;

            /**
             * bottom of stack
             */
            volatile int base;
        };
    }
}

namespace x10aux {
    template<> inline const char *typeName< ::x10::lang::Deque::Slots>() { return "::x10::lang::Deque::Slots"; }
}

#endif /* X10_LANG_DEQUE_H */

// vim:tabstop=4:shiftwidth=4:expandtab
