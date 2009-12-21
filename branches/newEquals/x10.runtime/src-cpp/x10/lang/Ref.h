#ifndef X10_LANG_REF_H
#define X10_LANG_REF_H

#include <x10aux/config.h>
#include <x10aux/ref.h>
#include <x10aux/RTT.h>
#include <x10aux/serialization.h>

#include <x10/lang/Object.h>

#define X10_LANG_PLACE_H_NODEPS
#include <x10/lang/Place.struct_h>
#undef X10_LANG_PLACE_H_NODEPS

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
                                   x10aux::serialization_buffer &buf);

            static const x10aux::serialization_id_t _interface_serialization_id;
            // Do not override
            virtual x10aux::serialization_id_t _get_interface_serialization_id() {
                _S_("===> Ref's _get_interface_serialization_id() called");
                return _interface_serialization_id;
            }
            // Do not override
            virtual void _serialize_interface(x10aux::serialization_buffer &buf);

            // A helper method for serializing reference state
            // Client responsible for checking for null
            static void _serialize_reference(x10aux::ref<Ref> this_,
                                             x10aux::serialization_buffer &buf);

            virtual x10aux::serialization_id_t _get_serialization_id() { return _serialization_id; };

            virtual void _serialize_body(x10aux::serialization_buffer &buf) { }

            template<class T> static x10aux::ref<T> _deserializer(x10aux::deserialization_buffer &);

            template<class T> static x10aux::ref<T> _deserialize(x10aux::deserialization_buffer &buf);

            struct _reference_state {
                x10_int loc;
                x10aux::x10_addr_t ref;
            };
            // A helper method for deserializing reference state
            // Client responsible for checking for null
            static Ref::_reference_state _deserialize_reference_state(x10aux::deserialization_buffer &buf) {
                _reference_state rr;
                rr.loc = buf.read<x10_int>();
                rr.ref = buf.read<x10aux::x10_addr_t>();
                if (rr.ref == 0) {
                    _S_("Deserializing a "<<ANSI_SER<<ANSI_BOLD<<"null reference"<<ANSI_RESET<<" from buf: "<<&buf);
                }
                return rr;
            }

            // A helper method for computing the final deserialized reference
            // res is ignored if rr.ref is null, and could even be uninitialized
            // res is freed if rr.loc is here
            template<class R> static x10aux::ref<R> _finalize_reference(x10aux::ref<Ref> res, Ref::_reference_state rr);

            virtual void _deserialize_body(x10aux::deserialization_buffer &buf) { }

            template<class T> friend class x10aux::ref;

            virtual x10_int hashCode();

            virtual x10aux::ref<String> toString();

            virtual x10_boolean at(x10::lang::Place p) {
                return location == p->FMGL(id);
            }
            
            virtual x10_boolean at(x10aux::ref<x10::lang::Ref> o) {
                return location == o->location;
            }

            virtual x10aux::ref<x10::lang::String> typeName();

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

            // Like the destructor, but called only by dealloc_object()
            // Needed only for native classes that have alloc'ed state
            virtual void _destructor() { }

            static void dealloc_object(Ref*);
        };

        template<class T> x10aux::ref<T> Ref::_deserializer(x10aux::deserialization_buffer &buf) {
            x10aux::ref<Ref> this_ = new (x10aux::alloc_remote<Ref>()) Ref();
            buf.record_reference(this_);
            this_->_deserialize_body(buf);
            return this_;
        }

        template<class T> x10aux::ref<T> Ref::_deserialize(x10aux::deserialization_buffer &buf) {
            // extract the id
            x10aux::serialization_id_t id = buf.read<x10aux::serialization_id_t>();
            _reference_state rr = _deserialize_reference_state(buf);
            x10aux::ref<Ref> res;
            if (rr.ref != 0) {
                _S_("Deserializing a "<<ANSI_SER<<ANSI_BOLD<<"class"<<ANSI_RESET<<
                        " (with id "<<id<<") at "<<rr.loc<<" from buf: "<<&buf);
                // execute a callback to instantiate the right concrete class
                res = x10aux::DeserializationDispatcher::create<T>(buf, id);
            }
            // res is uninitialized if rr.ref is null
            return _finalize_reference<T>(res, rr);
        }

        // Given a deserialized object pointer (allocated with alloc_remote) and
        // remote reference info, return the reference to the right object
        template<class R> x10aux::ref<R> Ref::_finalize_reference(x10aux::ref<Ref> obj, Ref::_reference_state rr) {
            if (rr.ref == 0) {
                return x10aux::null;
            }
            if (rr.loc == x10aux::here) { // a remote object coming home to roost
                _S_("\ta local object come home");
                x10aux::dealloc_remote(obj.operator->());
                return static_cast<R*>((void*)(size_t)rr.ref);
            }
            _S_("Deserialized a "<<ANSI_SER<<ANSI_BOLD<<"class"<<ANSI_RESET<<
                    " "<<obj->_type()->name());
            obj->location = rr.loc;
            x10aux::set_remote_ref(obj.operator->(), rr.ref);
            return obj;
        }

    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
