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

    typedef void *(*BufferFinder)(deserialization_buffer &buf, x10_int len);
    template<> inline const char *typeName<BufferFinder>() { return "BufferFinder"; }

    typedef x10_short serialization_id_t;

    template<> inline const char *typeName<serialization_id_t>() { return "serialization_id_t"; }

    class DeserializationDispatcher {
        protected:
        static DeserializationDispatcher *it;

        
        BufferFinder *put_bfinder_v;
        size_t put_bfinder_sz;

        BufferFinder *get_bfinder_v;
        size_t get_bfinder_sz;

        Deserializer *deser_v;
        size_t deser_sz;

        int next_id;

        public:
        DeserializationDispatcher () : put_bfinder_v(NULL), put_bfinder_sz(0),
                                       get_bfinder_v(NULL), get_bfinder_sz(0),
                                       deser_v(NULL), deser_sz(0), next_id(1) { }
        ~DeserializationDispatcher () {
            ::free(put_bfinder_v); // do not use GC
            ::free(get_bfinder_v); // do not use GC
            ::free(deser_v); // do not use GC
        }

        
        template<class T> static ref<T> create(deserialization_buffer &buf);
        template<class T> static ref<T> create(deserialization_buffer &buf,
                                               x10aux::serialization_id_t id);

        ref<x10::lang::Object> create_(deserialization_buffer &buf);
        ref<x10::lang::Object> create_(deserialization_buffer &buf, x10aux::serialization_id_t id);

        static serialization_id_t addDeserializer(Deserializer deser, bool is_async=false);
        serialization_id_t addDeserializer_(Deserializer deser, bool is_async);

        static serialization_id_t addPutBufferFinder(BufferFinder bfinder);
        serialization_id_t addPutBufferFinder_(BufferFinder bfinder);
        static BufferFinder getPutBufferFinder(x10aux::serialization_id_t id);
        BufferFinder getPutBufferFinder_(x10aux::serialization_id_t id);

        static serialization_id_t addGetBufferFinder(BufferFinder bfinder);
        serialization_id_t addGetBufferFinder_(BufferFinder bfinder);
        static BufferFinder getGetBufferFinder(x10aux::serialization_id_t id);
        BufferFinder getGetBufferFinder_(x10aux::serialization_id_t id);

        static void registerHandlers();
        void registerHandlers_();
    };

    inline BufferFinder DeserializationDispatcher::getPutBufferFinder (serialization_id_t id) {
        return it->getPutBufferFinder_(id); 
    }

    inline BufferFinder DeserializationDispatcher::getGetBufferFinder (serialization_id_t id) {
        return it->getGetBufferFinder_(id); 
    }

    template<class T> ref<T> DeserializationDispatcher::create(deserialization_buffer &buf,
                                                               x10aux::serialization_id_t id) {
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
