#ifndef X10_LANG_OBJECT_H
#define X10_LANG_OBJECT_H

#include <x10/lang/Reference.h>

namespace x10 {

    namespace lang {

        class String;
        class Ref;
        class Any;
        
        class SomeObject : public Reference {
        private:
            static x10aux::itable_entry _itables[1];
            
        public:
            RTT_H_DECLS_CLASS

            virtual x10aux::itable_entry* _getITables() { return _itables; }
            
            SomeObject(){ }

            virtual ~SomeObject() { }

            virtual x10_int hashCode() = 0;

        };
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab:textwidth=100
