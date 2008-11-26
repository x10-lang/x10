#ifndef X10_LANG_REF_H
#define X10_LANG_REF_H

#include <x10aux/config.h>
#include <x10aux/ref.h>
#include <x10aux/RTT.h>

#include <x10/lang/Object.h>

namespace x10 {

    namespace lang {

        class String;

        class Ref : public virtual Object {
        public:
            class RTT : public x10aux::RuntimeType { 
                public:
                static RTT* const it; 
            
                virtual void init() {
                    initParents(1,x10aux::getRTT<Object>());
                }
                
                virtual std::string name() const {
                    return "x10.lang.Ref";
                }

            };

            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<Ref>();
            }

            virtual ~Ref() { }

            template<class T> friend class x10aux::ref;

            virtual x10_int hashCode();

            virtual x10aux::ref<String> toString();

            virtual bool equals(x10aux::ref<Object> other) {
                if (other==x10aux::ref<Ref>(this)) return true;
                return false;
            }

        };

    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
