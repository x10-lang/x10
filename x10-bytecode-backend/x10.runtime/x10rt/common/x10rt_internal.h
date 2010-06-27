#ifndef X10RT_INTERNAL_H
#define X10RT_INTERNAL_H

#include <cstring>


template<class T> static inline T* safe_malloc (size_t f=1, size_t a=0) {
    size_t sz = f*sizeof(T) + a;
    return sz==0 ? 0 : (T*)malloc(sz);
}

static inline void safe_free (void *p) { free (p); }

// Lookup table from message id to callback function
template<class T> struct Table {
    T *arrv;
    size_t arrc;
    Table() : arrv(NULL), arrc(0) { }
    unsigned reg (const T &v) {
        reg(arrc, v);
        return arrc-1;
    }
    void reg (unsigned id, const T &v) {
        if (id >= arrc) {
            arrv = (T*) ::realloc(arrv, sizeof(T)*(id+1));
            ::memset(&arrv[arrc], 0, ((id+1)-arrc)*sizeof(T));
            arrc = id+1;
        }
        arrv[id] = v;
    }
    const T &operator [] (unsigned id) { return arrv[id]; }
};

X10RT_C void x10rt_emu_remote_op (x10rt_place place, x10rt_remote_ptr remote_addr,
                                  x10rt_op_type type, unsigned long long value);

X10RT_C void x10rt_emu_init (x10rt_msg_type *counter);


#endif
