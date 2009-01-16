#include <x10aux/config.h>
#include <x10aux/string_utils.h>
#include <x10aux/rail_utils.h>
#include <x10aux/alloc.h>
#include <x10aux/math.h>

#include <x10/lang/String.h>
#include <x10/lang/Rail.h>

using namespace x10::lang;
using namespace x10aux;

Rail<ref<String> > *x10aux::convert_args(int ac, char **av) {
    assert(ac>=1);
    x10_int x10_argc = ac  - 1;
    Rail<ref<String> > *arr = alloc_rail<ref<String>, Rail<ref<String> > > (x10_argc);
    for (int i = 1; i < ac; i++) {
        ref<String> val = String::Lit(av[i]);
        (*arr)[i-1] = val;
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

String x10aux::vrc_to_string(ref<ValRail<x10_char> > v) {
    std::string str(v->FMGL(length), '\0');
    for (int i = 0; i < v->FMGL(length); ++i)
        str[i] = (*v)[i];
    String r;
    r._constructor(str);
    return r;
}

// vim:tabstop=4:shiftwidth=4:expandtab
