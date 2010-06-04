#ifndef X10_LANG_CLOSURE_H
#define X10_LANG_CLOSURE_H

#include <x10aux/config.h>
#include <x10aux/ref.h>
#include <x10aux/serialization.h>
#include <x10aux/RTT.h>

#include <x10/lang/Reference.h>

#define X10_LANG_PLACE_H_NODEPS
#include <x10/lang/Place.struct_h>
#undef X10_LANG_PLACE_H_NODEPS

#define X10_LANG_ANY_H_NODEPS
#include <x10/lang/Any.h>
#undef X10_LANG_ANY_H_NODEPS

namespace x10 { namespace lang { class String; } }

namespace x10 {

    namespace lang {

        /**
         * This is a class that exists only at the C++ implementation level,
         * not at the X10 language level.  Therefore it does not have an
         * associated RTT.
         * 
         * The purpose of this class is to provide a common C++ level superclass
         * for all X10 closures.  This provides a class in which to locate object model
         * and other serialization/deserialization functions that are common for all
         * concrete Closure instances.  This is an abstract class.
         */
        class Closure : public Reference {
        public:
            Closure() {
                location = x10aux::here;
            }

            static void _serialize(x10aux::ref<Closure> this_,
                                   x10aux::serialization_buffer &buf);
    
            template<class T> static x10aux::ref<T> _deserialize(x10aux::deserialization_buffer &buf);

            virtual x10_int hashCode();
            
            virtual x10aux::ref<String> toString();

            virtual x10aux::ref<x10::lang::String> typeName();
        };

        template<class T> x10aux::ref<T> Closure::_deserialize(x10aux::deserialization_buffer &buf){
            // extract the id and execute a callback to instantiate the right concrete class
            _S_("Deserializing a "<<ANSI_SER<<ANSI_BOLD<<"value"<<ANSI_RESET<<
                " (expecting id) from buf: "<<&buf);
            return x10aux::DeserializationDispatcher::create<T>(buf);
        }
    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
