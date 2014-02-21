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

#ifndef X10AUX_DESERIALIZATION_DISPATCHER_H
#define X10AUX_DESERIALIZATION_DISPATCHER_H

#include <x10aux/config.h>
#include <x10aux/network.h>
#include <x10aux/RTT.h>

namespace x10 { namespace lang { class Reference; } }

namespace x10aux {

    class deserialization_buffer;

    class DeserializationDispatcher {
        protected:
        static DeserializationDispatcher *it;

        public:
        struct Data {
            Deserializer deser;
            x10aux::serialization_id_t sid;
        };
        protected:

        Data *data_v;
        size_t data_c;
        size_t next_id;

        public:
        DeserializationDispatcher () : data_v(NULL), data_c(0), next_id(1) { }
        ~DeserializationDispatcher () {
            ::free(data_v); // do not use GC
        }
        
        static ::x10::lang::Reference* create(deserialization_buffer &buf) {
            return it->create_(buf);
        }
        static ::x10::lang::Reference* create(deserialization_buffer &buf, serialization_id_t id) {
            return it->create_(buf, id);
        }
        ::x10::lang::Reference* create_(deserialization_buffer &buf);
        ::x10::lang::Reference* create_(deserialization_buffer &buf, serialization_id_t id);

        static serialization_id_t addDeserializer (Deserializer deser);
        serialization_id_t addDeserializer_ (Deserializer deser);
    };

    template<> inline const char *typeName<DeserializationDispatcher>()
    { return "DeserializationDispatcher"; }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
