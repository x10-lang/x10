#include <x10aux/string_utils.h>
#include <x10aux/rail_utils.h>

#include <x10/lang/String.h>
#include <x10/lang/Rail.h>

using namespace x10::lang;
using namespace x10aux;

Rail<ref<String> > *x10aux::convert_args(int ac, char **av) {
    // TODO: assert that ac >= 1
    x10_int x10_argc = ac  - 1;
    Rail<ref<String> >* arr =
            alloc_rail<ref<String>, Rail<ref<String> > > (x10_argc);
    //std::cerr<<"convert_args: allocated "<<arr->length<<" elements"<<std::endl;
    for (int i = 1; i < ac; i++) {
        //std::cerr<<"convert_args: allocating arg "<<i<<": "<<av[i]<<std::endl;
        String* val = new (x10aux::alloc<String>()) String(av[i]);
        //std::cerr<<"convert_args: allocated arg "<<i<<": "<<val._content<<std::endl;
        (*arr)[i-1] = val;
        //std::cerr<<"convert_args: assigned arg "<<i<<": "<<(*arr)[i-1]._content<<std::endl;
    }
    return arr;
}

void x10aux::free_args(const ref<Rail<ref<String> > > &arr) {
    //cerr << "free_args: freeing " << arr->length << " elements" << endl;
    //x10_int length = arr->length;
    //cerr << "free_args: freeing array " << arr << endl;
    free_rail<Rail<ref<String> > >(arr);
    //cerr << "free_args: freed array " << arr << endl;
}

// [DC] I'm sure Igor will hate this but it will do for now.
template<class T> T x10aux::from_string(const ref<String> &s) {
    std::istringstream ss(*s);
    T x;
    if (!(ss >> x)) {
        // number format exception
    }
    return x;
}

// [DC] I'm sure Igor will hate this but it will do for now.
template<class T> ref<String> to_string_general(T v) {
    std::ostringstream ss;
    ss << v;
    return new (alloc<String>()) String(ss.str());
}

ref<String> x10aux::to_string(bool v) {
    return to_string_general(v);
}
    
ref<String> x10aux::to_string(char v) {
    return to_string_general(v);
}
    
ref<String> x10aux::to_string(short v) {
    return to_string_general(v);
}
    
ref<String> x10aux::to_string(int v) {
    return to_string_general(v);
}
    
ref<String> x10aux::to_string(int64_t v) {
    return to_string_general(v);
}
    
ref<String> x10aux::to_string(float v) {
    return to_string_general(v);
}

ref<String> x10aux::to_string(double v) {
    return to_string_general(v);
}

ref<String> x10aux::to_string(const char *v) {
    return to_string_general(std::string(v));
}
    
// vim:tabstop=4:shiftwidth=4:expandtab
