#ifndef X10_LANG_VALUE_H
#define X10_LANG_VALUE_H

#include <x10aux/config.h>
#include <x10aux/ref.h>
#include <x10aux/serialization.h>
#include <x10aux/RTT.h>

#include <x10/lang/Object.h>
namespace x10 { namespace lang { class String; } }

namespace x10 {

    namespace lang {

        class Value : public virtual Object {
        public:

            class RTT : public x10aux::RuntimeType { 
                public:
                static RTT* const it; 
            
                virtual void init() { initParents(1,x10aux::getRTT<Object>()); }
                
                virtual std::string name() const {
                    return "x10.lang.Value";
                }

            };

            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<Value>();
            }


            //static const int SERIALIZATION_ID = -1;

            virtual void _serialize_fields(x10aux::serialization_buffer&, x10aux::addr_map&) { };

            virtual void _deserialize_fields(x10aux::serialization_buffer&, x10aux::addr_map&) { };

            explicit Value() { }

            explicit Value(x10aux::SERIALIZATION_MARKER) { }

            template<class T> friend class x10aux::ref;

            virtual x10_int hashCode();

            virtual x10aux::ref<String> toString();

            virtual x10_boolean equals(x10aux::ref<Object> other) {
                if (!CONCRETE_INSTANCEOF(other,Value)) return false;
                // now compare fields but there aren't any
                return true;
            }

        };

    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
