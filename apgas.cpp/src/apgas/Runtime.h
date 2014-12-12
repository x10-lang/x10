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

#ifndef APGAS_POOL_H
#define APGAS_POOL_H

namespace apgas {
    class Task;

    class Runtime {
    public:
        Task* _mainTask; //   TEMPORARY HACK MAKE PUBLIC (should be private)

        /**
         * Create a pool and specify the main task that the pool should execute.
         * Note that this does not begin executing mainTask; execution does not
         * start until the start method is called on the Runtime.
         */
        Runtime(Task* mainTask);

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

        
        template<class T> static inline T* alloc(size_t size = sizeof(T)) { return (T*)alloc_impl(size); }
        template<class T> static inline void dealloc(const T* obj) { dealloc_impl((void*)obj); }
        template<class T> static inline T* realloc(T* src, size_t dsz) { return (T*)realloc_impl(src, dsz); }

    private:
        static void* alloc_impl(size_t size);
        static void dealloc_impl(void* obj);
        static void* realloc_impl(void* src, size_t dsz);
    };
}

#endif /* APGAS_POOL_H */
