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

            // FIXME: optimize refs coming home
            static void _serialize(x10aux::ref<Ref> this_,
                                   x10aux::serialization_buffer &buf,
                                   x10aux::addr_map &m)
            {
                if (this_.isNull()) {
                    _S_("Serializing a "<<ANSI_SER<<ANSI_BOLD<<"null reference"<<ANSI_RESET<<" to buf: "<<&buf);
                    buf.write((x10aux::serialization_id_t)0, m);
                    return;
                }
                x10aux::serialization_id_t id = this_->_get_serialization_id();
                _S_("Serializing a "<<ANSI_SER<<ANSI_BOLD<<"class id "<<id<<ANSI_RESET<<" to buf: "<<&buf);
                buf.write(id, m);
                _S_("Serializing the "<<ANSI_SER<<"class body"<<ANSI_RESET<<" to buf: "<<&buf);
                this_->_serialize_body(buf, m);
            }

            virtual x10aux::serialization_id_t _get_serialization_id() { return _serialization_id; };

            // WARNING: this code interacts with the code in _deserialize
            virtual void _serialize_body(x10aux::serialization_buffer &buf, x10aux::addr_map &m) {
                buf.write(this->location, m);
                if (this->location == x10aux::here) {
                    _S_("Serialising a local Ref object of type "<<_type()->name());
                    buf.write((x10aux::x10_addr_t)(size_t)this, m);
                } else {
                    _S_("Serialising a remote Ref object of type "<<_type()->name());
                    void* tmp = x10aux::remote_ref::get_remote_ref(this);
                    buf.write((x10aux::x10_addr_t)(size_t)tmp, m);
                }
            };

            template<class T> static x10aux::ref<T> _deserializer(x10aux::deserialization_buffer &);

            // WARNING: this code interacts with the code in _serialize_body
            template<class T> static x10aux::ref<T> _deserialize(x10aux::deserialization_buffer &buf) {
                x10aux::serialization_id_t id = buf.read<x10aux::serialization_id_t>();
                if (id == 0) {
                    _S_("Deserializing a "<<ANSI_SER<<ANSI_BOLD<<"null reference"<<ANSI_RESET<<" from buf: "<<&buf);
                    return x10aux::null;
                }
                x10_int loc = buf.peek<x10_int>();
                _S_("Attempting to deserialize a "<<ANSI_SER<<ANSI_BOLD<<"ref"<<ANSI_RESET<<
                        " (with id "<<id<<") at "<<loc<<" from buf: "<<&buf);
                if (loc == x10aux::here) { // a remote object coming home to roost
                    _S_("\ta local object come home");
                    x10aux::ref<T> ref = x10aux::DeserializationDispatcher::create<T>(buf, id); // consume the buffer
                    T* ptr = static_cast<T*>(x10aux::remote_ref::get_remote_ref(ref.operator->()));
                    //x10aux::dealloc(ref.operator->());
                    return ptr;
                }
                // extract the id and execute a callback to instantiate the right concrete class
                _S_("Deserializing a "<<ANSI_SER<<ANSI_BOLD<<"class"<<ANSI_RESET<<
                        " (with id "<<id<<") from buf: "<<&buf);
                return x10aux::DeserializationDispatcher::create<T>(buf, id);
            }

            virtual void _deserialize_body(x10aux::deserialization_buffer &buf) {
                _S_("Deserialising a Ref object of type "<<_type()->name());
                this->location = buf.read<x10_int>();
                //assert (this->location != x10aux::here); // FIXME: optimize refs coming home and re-enable
                void* ref = (void*)(size_t)buf.read<x10aux::x10_addr_t>();
                x10aux::remote_ref::set_remote_ref(this, ref);
            };

            template<class T> friend class x10aux::ref;

            virtual x10_int hashCode();

            virtual x10aux::ref<String> toString();

            // Needed for linking - do not override
            virtual x10_boolean _struct_equals(x10aux::ref<Object> other) {
                if (other == x10aux::ref<Ref>(this)) return true;
                if (this->location == x10aux::here) return false; // already tested above
                if (other->location == this->location &&
                    x10aux::remote_ref::get_remote_ref(other.operator->()) == x10aux::remote_ref::get_remote_ref(this))
                {
                    return true;
                }
                return false;
            }
        };

        template<class T> x10aux::ref<T> Ref::_deserializer(x10aux::deserialization_buffer &buf) {
            x10aux::ref<Ref> this_ = new (x10aux::remote_alloc<Ref>()) Ref();
            this_->_deserialize_body(buf);
            return this_;
        }

    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
