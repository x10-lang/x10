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

#ifndef APGAS_POOL_H
#define APGAS_POOL_H

#include <x10aux/alloc.h>

namespace apgas {
    class Task;

    class Pool {
    public:
        Task* _mainTask; //   TEMPORARY HACK MAKE PUBLIC (should be private)

        /**
         * Create a pool and specify the main task that the pool should execute.
         * Note that this does not begin executing mainTask; execution does not
         * start until the start method is called on the Pool.
         */
        Pool(Task* mainTask);

        /**
         * Initialize the APGAS runtime and start executing the mainTask.
         * This method will not return until the mainTask and the implicit
         * Finish that wraps it terminate
         */
        void start();
        
        /**
         * Schedule the argument Task as an async to be executed
         * in the current Place.
         */
        void runAsync(Task* task);

        /**
         * Execute the body of the argument Task as the body of a Finish
         * statement. 
         */
        void runFinish(Task* task);

        /**
         * Execute the argument tasks within a newly created finish scope.
         * A shorthand for a finish block that contains runAsync of all argument tasks.
         */
        void runFinish(int numTasks, Task** tasks);

        
        template<class T> static inline T* alloc(size_t size = sizeof(T)) { return x10aux::alloc<T>(size); }
        template<class T> static inline void dealloc(const T* obj) { x10aux::dealloc(obj); }
        template<class T> static inline T* realloc(T* src, size_t dsz) { return x10aux::realloc<T>(src, dsz); }
    };
}

#endif /* APGAS_POOL_H */
