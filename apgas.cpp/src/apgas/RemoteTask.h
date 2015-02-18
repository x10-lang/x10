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

#ifndef APGAS_REMOTE_TASK_H
#define APGAS_REMOTE_TASK_H

#include <apgas/Task.h>

namespace apgas {

    /**
     * A RemoteTask is a task that can be executed at any Place
     * via Runtime::runAsyncAt
     */
    class RemoteTask : public apgas::Task {
        /*
         * Public API (for use by clients of the library)
         */

    public:
        /**
         * The body of the Task.
         * Subclasses of Task override this method to provide their body.
         */
        virtual void execute() = 0;

        /* TODO

    ::x10aux::serialization_id_t _get_serialization_id() {
        return _serialization_id;
    }
    
    ::x10aux::serialization_id_t _get_network_id() {
        return _network_id;
    }
    
    void _serialize_body(::x10aux::serialization_buffer &buf) {
        buf.write(this->args);
    }
    
    static x10::lang::Reference* _deserialize(::x10aux::deserialization_buffer &buf) {
        HelloWholeWorld__closure__1* storage = ::x10aux::alloc_z<HelloWholeWorld__closure__1>();
        buf.record_reference(storage);
        ::x10::lang::Rail< ::x10::lang::String* >* that_args = buf.read< ::x10::lang::Rail< ::x10::lang::String* >*>();
        HelloWholeWorld__closure__1* this_ = new (storage) HelloWholeWorld__closure__1(that_args);
        return this_;
    }
    

    static const ::x10aux::serialization_id_t _serialization_id;
    
    static const ::x10aux::serialization_id_t _network_id;

*/
        
        /*
         * Implementation level API (not intended for direct use by clients of the library)
         */
    protected:

    public:
        void __apply() {
            this->execute();
        }
    
        static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
        virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
    };
}



#endif /* APGAS_REMOTE_TASK_H */
