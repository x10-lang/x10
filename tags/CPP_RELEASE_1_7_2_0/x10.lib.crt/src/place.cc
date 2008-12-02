#include "x10.h"


X10::Place::Place(x10_place_t id) :
    __id(id) {} 
  
X10::Place::operator x10_place_t() const
{
  return __id;
}

X10::Place::operator x10_place_t() 
{
  return __id;
}

X10::Place::Place(const Place& p) :
  __id(p.__id) {}

const X10::Place::Place& X10::Place:: operator=(const Place& p)
{
  __id = p.__id;
  return *this;
}

const bool X10::Place::operator== (const X10::Place::Place& p)const
{
  return __id == p.__id;
}
  
  
