#ifndef X10AUX_DESERIALIZATION_DISPATCHER_H
#define X10AUX_DESERIALIZATION_DISPATCHER_H

#include <x10aux/config.h>

#include <x10aux/alloc.h>
#include <x10aux/ref.h>

namespace x10 { namespace lang { class Object; } }

namespace x10aux {

    class serialization_buffer;

    typedef ref<x10::lang::Object> (*Deserializer)(serialization_buffer &buf);

    template<> inline const char *typeName<Deserializer>() { return "Deserializer"; }

    typedef x10_short serialization_id_t;

    template<> inline const char *typeName<serialization_id_t>() { return "serialization_id_t"; }

    // TODO: factor out common code from here and init_dispatcher to some common superclass
    class DeserializationDispatcher {
        protected:
        static DeserializationDispatcher *it;

        static void ensure_created() {
            if (it==NULL) it = new (alloc<DeserializationDispatcher>()) DeserializationDispatcher();
        }

        Deserializer *initv;
        int initc;
        size_t initsz;

        public:
        DeserializationDispatcher () : initv(NULL), initc(0), initsz(0) { }
        ~DeserializationDispatcher () { dealloc(initv); }
        
        template<class T> static ref<T> create(serialization_buffer &buf) {
            // runtime pointer offset magic (for interfaces) happens here
            return static_cast<ref<T> >(it->create_(buf)); 
        }

        ref<x10::lang::Object> create_(serialization_buffer &buf);

        static serialization_id_t addDeserializer(Deserializer init) {
            ensure_created();
            return it->addDeserializer_(init);
        }
        serialization_id_t addDeserializer_(Deserializer init) {
            if (initsz<=(size_t)initc) {
                // grow slowly as this is init phase and we don't want to take
                // up RAM unnecessarily
                size_t newsz = initsz+1;
                initv = realloc(initv, initsz*sizeof(Deserializer), newsz*sizeof(Deserializer));
                initsz = newsz;
            }
            initv[initc] = init;
            serialization_id_t r = initc++;
            _S_("DeserializationDispatcher registered the following handler for id: "
                <<r<<": "<<std::hex<<(size_t)init<<std::dec);
            return r;
            
        }
    };

    template<> inline const char *typeName<DeserializationDispatcher>()
    { return "DeserializationDispatcher"; }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
