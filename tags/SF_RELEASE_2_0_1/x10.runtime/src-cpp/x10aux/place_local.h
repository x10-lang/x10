#ifndef X10AUX_PLACELOCAL_H
#define X10AUX_PLACELOCAL_H

#include <x10aux/config.h>
#include <x10aux/ref.h>

namespace x10 {
    namespace lang {
        class Lock__ReentrantLock;
    }
}

namespace x10aux {

    class place_local {
    private:
        class Bucket {
        public:
            x10_int _id;
            void *_data;
            Bucket *_next;
        };

        static x10_int _nextId;
        static Bucket **_buckets;
        static void** _fastData;
        static x10aux::ref<x10::lang::Lock__ReentrantLock> _lock;

    public:
        static void initialize();
        static x10_int nextId();
        static void* lookupData(x10_int id);
        static void registerData(x10_int id, void *data);
        static void unregisterData(x10_int id);
        template<class T> friend const char *x10aux::typeName();
    };

	template<> inline const char *typeName<place_local>() { return "place_local"; }
    template<> inline const char *typeName<place_local::Bucket*>() { return "place_local::Bucket *"; }
    template<> inline const char *typeName<place_local::Bucket>() { return "place_local::Bucket"; }
}

#endif


