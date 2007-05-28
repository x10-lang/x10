#include "types.h"
#include <iostream>

using namespace std;

int asyncSwitch (async_handler_t h, async_arg_t* args, int n)
{
   cout << "asyncSwitch should be overriddern \n";
   exit(-1);
}
