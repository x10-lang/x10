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

class Closure;

class Frame {
 protected:
  Frame(const Frame &f);
public:
	Frame();
	virtual ~Frame();

	virtual Closure *makeClosure();
	virtual void setOutletOn(Closure *c);
	virtual void setInt(int x);
	virtual Frame *copy() = 0; //should be implemented by sub-classes
};

}
#endif
