#include <x10aux/itables.h>
#include <x10/lang/Object.h>

using namespace x10aux;
using namespace x10::lang;

void*
x10aux::findItable(ref<Object> obj, RuntimeType **id) {
    itable_entry* itables = obj->_getITables();

    int i = 0;
    do { 
        if (itables[i].id == id) {
            return itables[i].itable;
        }
    } while (itables[++i].id != 0);
    
    fprintf(stderr, "ITable search failed: receiver class %s, target interface %s\n", obj->_type()->name(), (*id)->name());
    abort();
}
