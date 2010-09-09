#include "XX.h"  // class implemented


/////////////////////////////// PUBLIC ///////////////////////////////////////

//============================= LIFECYCLE ====================================

XX::XX()
{
}// XX

XX::XX(const XX&)
{
}// XX

XX::~XX()
{
}// ~XX


//============================= OPERATORS ====================================

XX& 
XX::operator=(XX&);
{
   return *this;

}// =

//============================= OPERATIONS ===================================
//============================= ACCESS     ===================================
//============================= INQUIRY    ===================================
/////////////////////////////// PROTECTED  ///////////////////////////////////

/////////////////////////////// PRIVATE    ///////////////////////////////////

Use Header File Guards
Include files should protect against multiple inclusion through the use of macros that "guard" the files.
When Not Using Namespces

#ifndef filename_h
#define filename_h

#endif 
