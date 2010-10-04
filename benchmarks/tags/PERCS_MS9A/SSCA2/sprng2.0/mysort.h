#include <iostream>
#include <algorithm>

using namespace std;

struct MyPair {

  MyPair(int _k, int _v) : k(_k), v(_v) {}

   int k;
   int v;

   bool  operator<(const MyPair& b) const {
      return k < b.k;
  }
};



void mysort (void* begin, int length) {

         std::sort((MyPair*) begin, ((MyPair*) begin) + length) ;

}
