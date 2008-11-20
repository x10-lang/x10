#include <x10aux/config.h>

#include <x10/lang/Exception.h>

using namespace x10::lang;
using namespace x10aux;

Exception::Exception() : Throwable() {
}
    
    
//#line 17 "/home/spark/x10-cvs/x10.dist/lib/x10.jar:x10/lang/Exception.x10"
Exception::Exception(const ref<String> &message) : Throwable(message) {   
}
    
    
//#line 18 "/home/spark/x10-cvs/x10.dist/lib/x10.jar:x10/lang/Exception.x10"
Exception::Exception(const ref<String> &message, const ref<Throwable> &cause)
  : Throwable(message, cause) {
} 
   

//#line 19 "/home/spark/x10-cvs/x10.dist/lib/x10.jar:x10/lang/Exception.x10"
Exception::Exception(const ref<Throwable> &cause) : Throwable(cause) {
}



#if 0
void Exception::_serialize_fields(x10aux::serialization_buffer& buf, x10aux::addr_map& m) {
    Throwable::_serialize_fields(buf, m);
    
}

void Exception::_deserialize_fields(x10aux::serialization_buffer& buf) {
    
}
#endif

DEFINE_RTT(Exception);
// vim:tabstop=4:shiftwidth=4:expandtab
