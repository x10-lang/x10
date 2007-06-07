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
namespace x10lib_cws {
class Frame {
		// The label at which computation must be continued by the associated
		// closure.
public:
	Frame();
	Frame(Frame *f);
	Closure *makeClosure();
	void setOutletOn(Closure *c);
	void setInt(int x);
};
}
#endif
