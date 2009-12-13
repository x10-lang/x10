#ifndef X10_LANG_CLOSURE_H
#define X10_LANG_CLOSURE_H

#include <x10aux/config.h>
#include <x10aux/ref.h>
#include <x10aux/serialization.h>
#include <x10aux/RTT.h>

#include <x10/lang/Object.h>
namespace x10 { namespace lang { class String; } }

namespace x10 {

    namespace lang {

        class Closure : public Object {
        public:
            RTT_H_DECLS_CLASS

            Closure() {
                location = x10aux::here;
            }

            static x10aux::ref<Closure> _make();

            x10aux::ref<Closure> _constructor() { return this; }

            static const x10aux::serialization_id_t _serialization_id;

            virtual x10aux::serialization_id_t _get_serialization_id() { return _serialization_id; };

            virtual void _serialize_body(x10aux::serialization_buffer &) {
                // there are no fields
            }

            template<class T> static x10aux::ref<T> _deserializer(x10aux::deserialization_buffer &);

            void _deserialize_body(x10aux::deserialization_buffer &) {
                // there are no fields
            }

            static void _serialize(x10aux::ref<Closure> this_,
                                   x10aux::serialization_buffer &buf);
    
            template<class T> static x10aux::ref<T> _deserialize(x10aux::deserialization_buffer &buf);
    
            virtual x10_int hashCode() {
                // All instances of Closure are equal, so their hashcodes can be too.
                return 0;
            }

            virtual x10aux::ref<String> toString();

            virtual x10_boolean _struct_equals(x10aux::ref<Object> other);
        };

        template<class T> x10aux::ref<T> Closure::_deserialize(x10aux::deserialization_buffer &buf){
            // extract the id and execute a callback to instantiate the right concrete class
            _S_("Deserializing a "<<ANSI_SER<<ANSI_BOLD<<"value"<<ANSI_RESET<<
                " (expecting id) from buf: "<<&buf);
            return x10aux::DeserializationDispatcher::create<T>(buf);
        }

        template<class T> x10aux::ref<T> Closure::_deserializer(x10aux::deserialization_buffer &) {
            x10aux::ref<Closure> this_ = new (x10aux::alloc<Closure>()) Closure();
            return this_;
        }
    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
