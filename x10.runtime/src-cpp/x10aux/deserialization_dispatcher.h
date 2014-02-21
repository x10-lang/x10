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
#include <x10aux/chunked_array.h>

namespace x10 { namespace lang { class Reference; } }

namespace x10aux {

    class deserialization_buffer;

    class DeserializationDispatcher {
      protected:
        static DeserializationDispatcher *it;

        chunked_array<Deserializer> data;
        size_t next_id;

      public:
        DeserializationDispatcher () : data(true), next_id(1) { }
        
        static ::x10::lang::Reference* create(deserialization_buffer &buf, serialization_id_t id) {
            return it->create_(buf, id);
        }
        ::x10::lang::Reference* create_(deserialization_buffer &buf, serialization_id_t id);

        static serialization_id_t addDeserializer(Deserializer deser);
        serialization_id_t addDeserializer_(Deserializer deser);
    };
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
