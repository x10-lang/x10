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

        class Value : public Object {
        public:
            class RTT : public x10aux::RuntimeType { 
                public:
                static const RTT* const it; 
            
                RTT() : RuntimeType()
                { }
                
                virtual std::string name() const {
                    return "x10.lang.Value";
                }

            };

            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<Value>();
            }


            static const int SERIALIZATION_ID = -1;

            virtual void _serialize_fields(x10aux::serialization_buffer& buf, x10aux::addr_map& m) { };

            explicit Value()
#ifdef REF_COUNTING
                : __count(0)
#endif
            {
                _T_("Creating value "<<this<<" of type "<<_type()->name());
            }

            explicit Value(x10aux::SERIALIZATION_MARKER m)
#ifdef REF_COUNTING
                : __count(0)
#endif
            {
                (void) m;
                _T_("Creating value "<<this<<" of type "<<_type()->name());
            }

            ~Value() {
                _T_("Destroying value "<<this<<" of type "<<_type()->name());
            }

            template<class T> friend class x10aux::ref;

            virtual x10_int hashCode();

            virtual x10aux::ref<String> toString();

            virtual x10_boolean equals(const x10aux::ref<Value> &other) {
                if (!RTT::it->concreteInstanceOf(other)) return false;
                // now compare fields but there aren't any
                return true;
            }

        };

    }
}


#endif
