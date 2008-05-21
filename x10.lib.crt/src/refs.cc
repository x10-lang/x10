#include <stdlib.h>

#include "x10.h"

#define ADDRMASK 0x01

bool x10_is_localref(x10_addr_t ref)
{
  return (((int)ref) & ADDRMASK==0);
}

x10_remote_ref_t x10_serialize_ref (x10_addr_t ref) 
{
  x10_remote_ref_t remote_ref;
  
  int loc_mask = ((long)ref) & ADDRMASK;
  
  if (loc_mask == 0) { //check local or remote
    remote_ref.loc = __x10_here; 
    remote_ref.addr = ref;
  } else {
    remote_ref.loc = ((x10_proxy_t*) (((int) ref) ^ ADDRMASK))->loc;
    remote_ref.addr =(x10_addr_t) (((x10_proxy_t*) (((int) ref) ^ ADDRMASK))->addr);
  }
  
  return remote_ref;
}

x10_addr_t x10_deserialize_ref (x10_remote_ref_t ref)
{
  if (ref.loc == __x10_here)
    return ref.addr;
  else {
    x10_proxy_t* remote_ref = (x10_proxy_t*) malloc(sizeof(x10_proxy_t));
    remote_ref->loc = ref.loc;
    remote_ref->addr = ref.addr;
    remote_ref = (x10_proxy_t*) (((long) remote_ref) | ADDRMASK);
    return (x10_addr_t) remote_ref;
  }
}

x10_place_t x10_get_loc (x10_addr_t ref)
{
  int loc_mask = ((long)ref) & ADDRMASK;
  return loc_mask == 0 ?  __x10_here : ((x10_proxy_t*) (((long) ref) ^ ADDRMASK))->loc;
}

x10_addr_t x10_get_addr(x10_addr_t ref)
{
  int loc_mask = ((long)ref) & ADDRMASK;
  
  if (loc_mask == 0) { //check local or remote
    return ref;
  } else {    
    return ((x10_proxy_t*) (((int) ref) ^ ADDRMASK))->addr;
  }
}
