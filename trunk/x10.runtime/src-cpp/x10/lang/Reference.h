#ifndef X10_LANG_REFERENCE_H
#define X10_LANG_REFERENCE_H

#include <x10aux/config.h>
#include <x10aux/ref.h>
#include <x10aux/RTT.h>
#include <x10aux/itables.h>

#include <x10aux/serialization.h>
#include <x10aux/deserialization_dispatcher.h>

namespace x10 {

    namespace lang {

        class String;
        class Any;

        /**
         * This is a class that exists only at the C++ implementation level,
         * not at the X10 language level.  Therefore it does not have an
         * associated RTT.
         * 
         * The purpose of this class is to provide a common C++ level superclass
         * for Object (X10 objects), Closure (function objects created from X10 closure literals),
         * and IBox<T> (X10 structs of type T that have been boxed because they were upcast to an interface type).
         * The single common superclass is needed because pointers to instances of any of its subclasses could
         * appear in variables of interface type and we need a common C++ level
         * ancestor class so that virtual dispatch will work.
         * 
         * This class is intentionally not a parent of Struct, because Structs
         * don't have virtual methods and cannot be pointed to by variables of
         * interface type unless they are wrapped in an IBox (which is a subclass of this class).
         */
        class Reference {
        public:
            x10aux::place location;

            Reference(){ }
            virtual ~Reference() { }

            /*********************************************************************************
             * Implementation-level object model functions assumed to be defined for all types
             *********************************************************************************/
            virtual x10aux::itable_entry* _getITables() = 0;

            virtual const x10aux::RuntimeType *_type() const = 0;


            /*********************************************************************************
             * X10-level functions assumed to be defined for all types
             *********************************************************************************/
            virtual x10aux::ref<String> toString() = 0;

            virtual x10_boolean equals(x10aux::ref<Any> other) {
                return this->_struct_equals(x10aux::ref<Reference>(other));
            }

            virtual x10_boolean _struct_equals(x10aux::ref<Reference> other) {
                return other == x10aux::ref<Reference>(this);
            }
            
            virtual x10_int hashCode() = 0;

            /*********************************************************************************
             * Serialization/Deserialization functions assumed to be defined for all types
             *********************************************************************************/
            virtual x10aux::serialization_id_t _get_serialization_id() = 0;
            virtual void _serialize_body(x10aux::serialization_buffer &) = 0;

            // This pair of functions should be overridden to not emit/extract the id in subclasses
            // that satisfy the following property:
            //
            //      "All instances of all my subclasses are de/serialised by the same code"
            //
            // Examples of classes that satisfy this property:  All final classes, and root classes
            // in hierarchies that use double dispatch for deserialization (to save serialization id
            // space).
            //
            // If one of these functions is overridden, the other should be too.
            //
            // Note these functions are static as we want to dispatch on the static type.

            static void _serialize(x10aux::ref<Reference> this_,
                                   x10aux::serialization_buffer &buf);

            // Should only be overridden in Ref
            virtual x10aux::serialization_id_t _get_interface_serialization_id() {
                _S_("===> Reference's _get_interface_serialization_id() called");
                return _get_serialization_id();
            }
            // Should only be overridden in Ref
            virtual void _serialize_interface(x10aux::serialization_buffer &buf);

            template<class T> static x10aux::ref<T> _deserialize(x10aux::deserialization_buffer &buf);
        };

        template<class T> x10aux::ref<T> Reference::_deserialize(x10aux::deserialization_buffer &buf) {
            x10aux::serialization_id_t id = buf.peek<x10aux::serialization_id_t>();
            if (id == 0) {
                buf.read<x10aux::serialization_id_t>();
                return x10aux::null;
            }
            // extract the id and execute a callback to instantiate the right concrete class
            _S_("Deserializing an "<<ANSI_SER<<ANSI_BOLD<<"interface"<<ANSI_RESET<<
                " (expecting id " << id << ") from buf: "<<&buf);
            return x10aux::DeserializationDispatcher::create<T>(buf);
        }
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab:textwidth=100
