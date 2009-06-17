#include <stdlib.h>

#include "x10_internal.h"

#define ADDRMASK 0x01

namespace X10{

RemoteRef::RemoteRef() {}

RemoteRef::RemoteRef(x10_remote_ref_t ref)
  :__ref(ref) {}

RemoteRef::operator x10_remote_ref_t() const
{
  return __ref;
}

RemoteRef::RemoteRef(const RemoteRef& ref)
  :__ref(ref) {}

Addr RemoteRef::GetAddr() const
{
  return __ref.addr;
}

Place RemoteRef::GetLoc() const
{
  return __ref.loc;
}

void RemoteRef::SetLoc(Place p) 
{
  __ref.loc = p;
}

void RemoteRef::SetAddr(Addr a)
{
  __ref.addr = a;
}

bool RemoteRef::IsLocal(Addr ref)
{
  return (((long)ref) & ADDRMASK==0);
}

RemoteRef RemoteRef::Serialize(Addr ref) 
{
  RemoteRef remote_ref;
  
  int loc_mask = ((long)ref) & ADDRMASK;
  
  if (loc_mask == 0) { //check local or remote
    remote_ref.SetLoc(__x10::here); 
    remote_ref.SetAddr(ref);
  } else {
    remote_ref.SetLoc(((RemoteRef*) (((long) ref) ^ ADDRMASK))->GetLoc());
    remote_ref.SetAddr((Addr) (((RemoteRef*) (((long) ref) ^ ADDRMASK))->GetAddr()));
  }
  
  return remote_ref;
}

Addr RemoteRef::Deserialize(RemoteRef ref)
{
  if (ref.GetLoc() == Place(__x10::here))
    return ref.GetAddr();
  else {
    RemoteRef* remote_ref = (RemoteRef*) malloc(sizeof(RemoteRef));
    remote_ref->SetLoc(ref.GetLoc());
    remote_ref->SetAddr(ref.GetAddr());
    remote_ref = (RemoteRef*) (((long) remote_ref) | ADDRMASK);
    return (Addr) remote_ref;
  }
}

Place RemoteRef::GetLoc(Addr ref)
{
  int loc_mask = ((long)ref) & ADDRMASK;
  return loc_mask == 0 ?  Place(__x10::here) : ((RemoteRef*) (((long) ref) ^ ADDRMASK))->GetLoc();
}

Addr RemoteRef::GetAddr(Addr ref)
{
  int loc_mask = ((long)ref) & ADDRMASK;
  
  if (loc_mask == 0) { //check local or remote
    return ref;
  } else {    
    return ((RemoteRef*) (((long) ref) ^ ADDRMASK))->GetAddr();
  }
}
}

//C Bindings

bool x10_ref_is_local(x10_addr_t ref)
{
  return X10::RemoteRef::IsLocal(ref);
}

x10_remote_ref_t x10_ref_serialize(x10_addr_t ref) 
{
  return X10::RemoteRef::Serialize(ref);
}

x10_addr_t x10_ref_deserialize(x10_remote_ref_t ref)
{
  return X10::RemoteRef::Deserialize(ref);
}

x10_place_t x10_ref_get_loc (x10_addr_t ref)
{
  return X10::RemoteRef::GetLoc(ref);
}

x10_addr_t x10_ref_get_addr(x10_addr_t ref)
{
  return X10::RemoteRef::GetAddr(ref);
}


