/*
============================================================================
 Name        : Frame.h
 Author      : Rajkishore Barik
 Version     :
 Copyright   : IBM Corporation 2007
 Description : Exe source file
============================================================================
*/
#ifndef x10lib_Frame_h
#define x10lib_Frame_h

#include "Closure.h"


namespace x10lib_cws {

class Frame;
class Closure;

class Frame {
public:
	Frame();
	Frame(Frame *f);
	~Frame();

	Closure *makeClosure();
	void setOutletOn(Closure *c);
	void setInt(int x);
};

}
#endif
