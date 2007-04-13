#ifndef X10POINT_H_
#define X10POINT_H_

using namespace X10::lang;

template<int RANK>
class Point
{
 private:
 const int values_[RANK]; 
};

template<>
class Point<1>
{
   const int i_;
};

template<>
class Point<2>
{
   const int i_;
   const int j_;
};

template<>
class Point<3>
{
   const int i_;
   const int j_;
   const int k_;
};

#endif /*X10POINT_H_*/
