#ifndef X10_LANG_REF_H
#define X10_LANG_REF_H

#include <x10aux/config.h>
#include <x10aux/ref.h>
#include <x10aux/RTT.h>
#include <x10aux/serialization.h>

#include <x10/lang/Object.h>

namespace x10 {
    
    namespace lang {

        class String;

        class Ref : public Object {
        public:
            RTT_H_DECLS_CLASS;

            static x10aux::ref<Ref> _make();

            x10aux::ref<Ref> _constructor() { return this; }

            static const x10aux::serialization_id_t _serialization_id;

            static void _serialize(x10aux::ref<Ref> this_,
                                   x10aux::serialization_buffer &buf,
                                   x10aux::addr_map &m)
            {
                // don't send an id, just serialise the ref (null/local/remote -- we don't care)
                x10aux::remote_ref tmp = x10aux::remote_ref::make(this_.operator->());
                buf.write(tmp,m);
            }

            virtual x10aux::serialization_id_t _get_serialization_id() { return _serialization_id; };

            virtual void _serialize_body(x10aux::serialization_buffer &buf, x10aux::addr_map &m) {
                _S_("Serialising a local Ref object of type "<<_type()->name());
                x10aux::remote_ref tmp = x10aux::remote_ref::make(this);
                buf.write(tmp,m);
            };

            template<class T> static x10aux::ref<T> _deserialize(x10aux::deserialization_buffer &buf){
                return (T*)x10aux::remote_ref::take(buf.read<x10aux::remote_ref>());
            }

            template<class T> friend class x10aux::ref;

            virtual x10_int hashCode();

            virtual x10aux::ref<String> toString();

            // Needed for linking - do not override
            virtual x10_boolean _struct_equals(x10aux::ref<Object> other) {
                if (other == x10aux::ref<Ref>(this)) return true;
                return false;
            }
        };

    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
