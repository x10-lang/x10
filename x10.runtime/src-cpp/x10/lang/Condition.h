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

#ifndef X10_LANG_CONDITION_H
#define X10_LANG_CONDITION_H

#include <x10/lang/X10Class.h>

#include <x10aux/pcond.h>

namespace x10 {
    namespace lang {
        class Condition : public ::x10::lang::X10Class {
        public:
            RTT_H_DECLS_CLASS;
    
            virtual ::x10aux::serialization_id_t _get_serialization_id() {
                fprintf(stderr, "Condition cannot be serialized.  (Condition.h)\n");
                abort();
            }
            virtual void _serialize_body(::x10aux::serialization_buffer&) {
                fprintf(stderr, "Condition cannot be serialized.  (Condition.h)\n");
                abort();
            }

            virtual void _constructor (void) { }

            static Condition* _make();
            ~Condition() { }

        public:
            void lock() { _pcond.lock(); }
            void unlock() { _pcond.unlock(); }
            void release() { _pcond.release(); }
            void await() { _pcond.await(); }
            void await(x10_long timeout) { _pcond.await(timeout); }
            x10_boolean complete() { return _pcond.complete(); }

        private:
            ::x10aux::pcond _pcond;
        };
    }
}

#endif /* X10_LANG_CONDITION_H */

// vim:tabstop=4:shiftwidth=4:expandtab
