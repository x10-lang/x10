/*
 * (c) Copyright IBM Corporation 2008
 *
 * This file is part of XRX/C++ native layer implementation.
 */

#ifndef X10_RUNTIME_DEQUE_H
#define X10_RUNTIME_DEQUE_H

#include <x10/lang/Ref.h>

namespace x10 {
    namespace runtime {

        class Lock;
        
       /**
        * A Deque for use by the workstealing implementation.
        *
        * This code is derived from a Java implementation that was 
        * written by Doug Lea with assistance from members of JCP JSR-166
        * Expert Group and released to the public domain, as explained at
        * http://creativecommons.org/licenses/publicdomain
        */
        class Deque : public x10::lang::Ref {
        public:
            class RTT : public x10aux::RuntimeType {
                public: 
                    static RTT* const it;
                    
                    virtual void init() {
                        initParents(1,x10aux::getRTT<x10::lang::Ref>());
                    }
                    
                    virtual const char *name() const {
                        return "x10.runtime.Deque";
                    }
                    
            };
            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<Deque>();
            }

            static x10aux::ref<Deque> _make() {
                x10aux::ref<Deque> this_ = new (x10aux::alloc<Deque>()) Deque();
                this_->_constructor();
                return this_;
            }

            x10aux::ref<Deque> _constructor();

            ~Deque() { _destructor(); }

        private:
            void _destructor();

            /**
             * Add in store-order the given task at given slot of q.
             * Caller must ensure q is nonnull and index is in range.
             */
            void setSlot(x10aux::ref<x10::lang::Object> *q, int i, x10aux::ref<x10::lang::Object> t);

            /**
             * CAS given slot of q to null. Caller must ensure q is nonnull
             * and index is in range.
             */
            bool casSlotNull(x10aux::ref<x10::lang::Object> *q, int i, x10aux::ref<x10::lang::Object> t);

            /**
             * Sets sp in store-order.
             */
            void storeSp(int s);

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
            void pushTask(x10aux::ref<x10::lang::Object> t);

            /**
             * Tries to take a task from the base of the queue, failing if
             * either empty or contended.
             * @return a task, or null if none or contended.
             */
            x10aux::ref<x10::lang::Object> deqTask();

            /**
             * Returns a popped task, or null if empty. Ensures active status
             * if nonnull. Called only by current thread.
             */
            x10aux::ref<x10::lang::Object> popTask();
                
            /**
             * Returns next task to pop.
             */
            x10aux::ref<x10::lang::Object> peekTask();

            /**
             * Returns an estimate of the number of tasks in the queue.
             */
            int getQueueSize();
            
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

            // FIXME: Really need to embed queueCapacity into struct with the queue data
            //        queue to get same semantics as java (capacity == array length)
            x10aux::ref<x10::lang::Object> *queue;
            int queueCapacity;

            volatile int sp;
            volatile int base;

            // TEMPORARY: Used to debug Deque independent of CAS implementation
            x10aux::ref<Lock> lock;
        };
    }
}
        
#endif /* X10_RUNTIME_DEQUE_H */

// vim:tabstop=4:shiftwidth=4:expandtab
