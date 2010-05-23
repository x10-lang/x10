#include <iostream>
#include <algorithm>

using namespace std;

extern "C" void mysort (void* begin, int length) {

         std::sort((std::pair<int,int>*) begin, ((std::pair<int,int>*) begin) + length) ;

}
