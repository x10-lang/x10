#ifndef X10RT_TYPES_H
#define X10RT_TYPES_H

// an integer type capable of representing anything from [0,max_places]
typedef unsigned long x10rt_place;

// an integer type capable of representing any message type
typedef unsigned x10rt_msg_type;

// an integer type capable of representing a remote void* and a remote size_t
typedef unsigned long long x10rt_remote_ptr;

// an integer type capable of representing the maximum size in bytes of an
// inter-place data copy (get/put)
typedef unsigned long x10rt_copy_sz;

struct x10rt_msg_params {
    x10rt_place dest_place;
    unsigned type;
    void *msg;
    unsigned long len;
};

#endif
