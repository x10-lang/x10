#ifndef X10_LANG_OBJECT_H
#define X10_LANG_OBJECT_H

#include <x10/lang/Reference.h>

namespace x10 {

    namespace lang {

        class String;
        class Ref;
        class Any;
        
        class Object : public Reference {
        private:
            static x10aux::itable_entry _itables[1];
            
        public:
            RTT_H_DECLS_CLASS

            virtual x10aux::itable_entry* _getITables() { return _itables; }
            
            Object(){ }

            virtual ~Object() { }

            virtual x10_boolean equals(x10aux::ref<Any> other);

            virtual x10_int hashCode() = 0;

            virtual x10_boolean _struct_equals(x10aux::ref<Object> other) {
                if (other == x10aux::ref<Object>(this)) return true;
                return false;
            }
        };
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab:textwidth=100
