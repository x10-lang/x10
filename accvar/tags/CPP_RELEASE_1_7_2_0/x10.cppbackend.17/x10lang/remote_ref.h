#ifndef __REMOTE_REF_H
#define __REMOTE_REF_H

// [IP] Temporary, to get __x10_my_place
#include <x10/reduce.h>

typedef unsigned char* x10_addr_t;

typedef struct {
    x10_place_t loc;
    x10_addr_t addr;
} x10_proxy_t;
   
typedef x10_proxy_t x10_remote_ref_t;

#define ADDRMASK 0x01

inline bool x10_is_localref(x10_addr_t ref) {
    return (((long)ref) & ADDRMASK==0);
}

inline x10_remote_ref_t x10_serialize_ref(x10_addr_t ref) {
    x10_remote_ref_t remote_ref;

    int loc_mask = ((long)ref) & ADDRMASK;

    if (loc_mask == 0) { //check local or remote
        remote_ref.loc = x10lib::__x10_my_place; 
        remote_ref.addr = ref;
    } else {
        remote_ref.loc = ((x10_proxy_t*) (((long) ref) ^ ADDRMASK))->loc;
        remote_ref.addr =(x10_addr_t) (((x10_proxy_t*) (((long) ref) ^ ADDRMASK))->addr);
    }

    return remote_ref;
}

inline x10_addr_t x10_deserialize_ref(x10_remote_ref_t ref) {
    if (ref.loc == x10lib::__x10_my_place)
        return ref.addr;
    else {
        x10_proxy_t* remote_ref = (x10_proxy_t*) malloc(sizeof(x10_proxy_t));
        remote_ref->loc = ref.loc;
        remote_ref->addr = ref.addr;
        remote_ref = (x10_proxy_t*) (((long) remote_ref) | ADDRMASK);
        return (x10_addr_t) remote_ref;
    }
}

inline x10_place_t x10_get_loc(x10_addr_t ref) {
    int loc_mask = ((long)ref) & ADDRMASK;
    return loc_mask == 0 ? x10lib::__x10_my_place : ((x10_proxy_t*) (((long) ref) ^ ADDRMASK))->loc;
}

inline x10_addr_t x10_get_addr(x10_addr_t ref) {
    int loc_mask = ((long)ref) & ADDRMASK;

    if (loc_mask == 0) { //check local or remote
        return ref;
    } else {    
        return ((x10_proxy_t*) (((long) ref) ^ ADDRMASK))->addr;
    }
}

#endif 

