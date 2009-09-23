#ifndef X10AUX_DESERIALIZATION_DISPATCHER_H
#define X10AUX_DESERIALIZATION_DISPATCHER_H

#include <x10aux/config.h>

#include <x10aux/alloc.h>
#include <x10aux/ref.h>

namespace x10 { namespace lang { class Object; } }

namespace x10aux {

    class deserialization_buffer;

    typedef ref<x10::lang::Object> (*Deserializer)(deserialization_buffer &buf);

    template<> inline const char *typeName<Deserializer>() { return "Deserializer"; }

    typedef x10_short serialization_id_t;

    template<> inline const char *typeName<serialization_id_t>() { return "serialization_id_t"; }

    class DeserializationDispatcher {
        protected:
        static DeserializationDispatcher *it;

        Deserializer *initv;
        int initc;
        size_t initsz;

        public:
        DeserializationDispatcher () : initv(NULL), initc(0), initsz(0) { }
        ~DeserializationDispatcher () { ::free(initv); } // do not use GC
        
        template<class T> static ref<T> create(deserialization_buffer &buf);
        template<class T> static ref<T> create(deserialization_buffer &buf,
                                               x10aux::serialization_id_t id);

        ref<x10::lang::Object> create_(deserialization_buffer &buf);
        ref<x10::lang::Object> create_(deserialization_buffer &buf, x10aux::serialization_id_t id);

        static serialization_id_t addDeserializer(Deserializer init, bool is_async=false);
        serialization_id_t addDeserializer_(Deserializer init, bool is_async);

        static void registerAsyncHandlers();
        void registerAsyncHandlers_();
    };

    template<class T> ref<T> DeserializationDispatcher::create(deserialization_buffer &buf,
                                                               x10aux::serialization_id_t id) {
        // runtime pointer offset magic (for interfaces) happens here
        return static_cast<ref<T> >(it->create_(buf,id)); 
    }

    template<class T> ref<T> DeserializationDispatcher::create(deserialization_buffer &buf) {
        return static_cast<ref<T> >(it->create_(buf)); 
    }
    
    template<> inline const char *typeName<DeserializationDispatcher>()
    { return "DeserializationDispatcher"; }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
