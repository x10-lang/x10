#ifndef X10POINT_H_
#define X10POINT_H_

namespace x10lib{

template<int RANK>
class Point {

 public:
   Point (const int values) {}
  
 private:

  int values_[RANK]; 
};

template<>
class Point<1>
{
  public:
    Point (int i) : i_(i) {}

  private:
   const int i_;
};

template<>
class Point<2>
{
  public:
    Point (int i, int j) : i_(i), j_(j) {}

  private:
   const int i_;
   const int j_;
};

template<>
class Point<3>
{
  public:
    Point (int i, int j, int k) : i_(i), j_(j), k_(k) {}

  private:
   const int i_;
   const int j_;
   const int k_;
};

}
#endif /*X10POINT_H_*/
