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

            x10aux::ref<Ref> _constructor() {
                location = x10aux::here;
                return this;
            }

            static const x10aux::serialization_id_t _serialization_id;

            static void _serialize(x10aux::ref<Ref> this_,
                                   x10aux::serialization_buffer &buf,
                                   x10aux::addr_map &m);

            // A helper method for serializing a final ref with no global state
            static void _serialize_reference(x10aux::ref<Ref> this_,
                                             x10aux::serialization_buffer &buf,
                                             x10aux::addr_map &m);

            virtual x10aux::serialization_id_t _get_serialization_id() { return _serialization_id; };

            virtual void _serialize_body(x10aux::serialization_buffer &buf, x10aux::addr_map &m) { }

            template<class T> static x10aux::ref<T> _deserializer(x10aux::deserialization_buffer &);

            template<class T> static x10aux::ref<T> _deserialize(x10aux::deserialization_buffer &buf);

            // A helper method for deserializing a final ref with no global state
            template<class R> static x10aux::ref<R> _deserialize_reference(x10aux::deserialization_buffer &buf);

            virtual void _deserialize_body(x10aux::deserialization_buffer &buf) { }

            template<class T> friend class x10aux::ref;

            virtual x10_int hashCode();

            virtual x10aux::ref<String> toString();

            // Needed for linking - do not override
            virtual x10_boolean _struct_equals(x10aux::ref<Object> other) {
                if (other == x10aux::ref<Ref>(this)) return true;
                if (this->location == x10aux::here) return false; // already tested above
                if (other->location == this->location &&
                    x10aux::get_remote_ref(other.operator->()) == x10aux::get_remote_ref(this))
                {
                    return true;
                }
                return false;
            }
        };

        template<class T> x10aux::ref<T> Ref::_deserializer(x10aux::deserialization_buffer &buf) {
            x10aux::ref<Ref> this_ = new (x10aux::alloc_remote<Ref>()) Ref();
            this_->_deserialize_body(buf);
            return this_;
        }

        template<class T> x10aux::ref<T> Ref::_deserialize(x10aux::deserialization_buffer &buf) {
            x10aux::serialization_id_t id = buf.read<x10aux::serialization_id_t>();
            // FIXME: factor out common code with _deserialize_reference
            x10_int loc = buf.read<x10_int>();
            x10aux::x10_addr_t ref = buf.read<x10aux::x10_addr_t>();
            if (ref == 0 /*TODO: id == 0*/) {
                _S_("Deserializing a "<<ANSI_SER<<ANSI_BOLD<<"null reference"<<ANSI_RESET<<" from buf: "<<&buf);
                return x10aux::null;
            }
            _S_("Attempting to deserialize a "<<ANSI_SER<<ANSI_BOLD<<"ref"<<ANSI_RESET<<
                    " (with id "<<id<<") at "<<loc<<" from buf: "<<&buf);
            if (loc == x10aux::here) { // a remote object coming home to roost
                _S_("\ta local object come home");
                x10aux::ref<T> obj = x10aux::DeserializationDispatcher::create<T>(buf, id); // consume the buffer
                T* ptr = static_cast<T*>((void*)(size_t)ref);
                x10aux::dealloc_remote(obj.operator->());
                return ptr;
            }
            // extract the id and execute a callback to instantiate the right concrete class
            _S_("Deserializing a "<<ANSI_SER<<ANSI_BOLD<<"class"<<ANSI_RESET<<
                    " (with id "<<id<<") from buf: "<<&buf);
            x10aux::ref<Ref> obj = x10aux::DeserializationDispatcher::create<T>(buf, id);
            obj->location = loc;
            x10aux::set_remote_ref(obj.operator->(), ref);
            return obj;
        }

        // FIXME: factor out common code with _deserialize
        template<class R> x10aux::ref<R> Ref::_deserialize_reference(x10aux::deserialization_buffer &buf) {
            x10_int loc = buf.read<x10_int>();
            x10aux::x10_addr_t ref = buf.read<x10aux::x10_addr_t>();
            if (ref == 0 /*TODO: id == 0*/) {
                _S_("Deserializing a "<<ANSI_SER<<ANSI_BOLD<<"null reference"<<ANSI_RESET<<" from buf: "<<&buf);
                return x10aux::null;
            }
            _S_("Attempting to deserialize a "<<ANSI_SER<<ANSI_BOLD<<"ref"<<ANSI_RESET<<
                    " of type "<<TYPENAME(R)<<" at "<<loc<<" from buf: "<<&buf);
            if (loc == x10aux::here) { // a remote object coming home to roost
                _S_("\ta local object come home");
                // Nothing left in the buffer (if _serialize_reference was used to serialize the object)
                return static_cast<R*>((void*)(size_t)ref);
            }
            x10aux::ref<R> obj = new (x10aux::alloc_remote<R>()) R();
            _S_("Deserializing a "<<ANSI_SER<<ANSI_BOLD<<"class"<<ANSI_RESET<<
                    " "<<obj->_type()->name()<<" from buf: "<<&buf);
            obj->location = loc;
            x10aux::set_remote_ref(obj.operator->(), ref);
            return obj;
        }

    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
