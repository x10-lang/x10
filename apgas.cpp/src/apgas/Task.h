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

#ifndef APGAS_TASK_H
#define APGAS_TASK_H

#include <x10/lang/VoidFun_0_0.h>
#include <x10/lang/Closure.h>

namespace apgas {
    class Pool;
    
    class Task : public x10::lang::Closure {
        /*
         * Public API (for use by clients of the library)
         */

    public:
        /**
         * Return the Pool instance which is executing this Task
         */
        Pool *getPool() { return myPool; }

        /**
         * The body of the Task.
         * Subclasses of Task override this method to provide their body.
         */
        virtual void execute() = 0;


        /*
         * Implementation level API (not intended for direct use by clients of the library)
         */
    protected:
        Pool* myPool;

    public:
        void setPool(Pool* p) { myPool = p; }
        
        static x10::lang::VoidFun_0_0::itable<Task> _itable;
        static x10aux::itable_entry _itables[2];
        virtual x10aux::itable_entry* _getITables() { return _itables; }
    
        void __apply() {
            this->execute();
        }
    
        static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
        virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }


        // TODO: use to implement distributed APIs
        x10aux::serialization_id_t _get_serialization_id() {
            assert(false);
            return 0;
        }
    
        void _serialize_body(x10aux::serialization_buffer &buf) {
            assert(false);
        }
    };
}



#endif /* APGAS_TASK_H */
