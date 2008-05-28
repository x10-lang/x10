#include "x10_internal.h"


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

}
