#include<x10/util/GrowableRail.h>


using namespace x10::lang;
using namespace std;
using namespace x10aux;
using namespace x10::util;

#include <assert.h>


template<class T> void GrowableRail<T>::grow(x10_int newSize) {
    if (newSize <= size()) {
        return;
    }
    
    assert(false); // TODO:
}

template<class T> void GrowableRail<T>::shrink(x10_int newSize) {
    if (newSize > size()/2 || newSize < 8) {
        return;
    }

    assert(false); // TODO
}

template <class T> ref<Rail<T> > GrowableRail<T>::toRail() {
    assert(false); // TODO
}

template <class T> ref<ValRail<T> > GrowableRail<T>::toValRail() {
    assert(false); // TODO
}


