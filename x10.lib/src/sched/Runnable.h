#ifndef RUNNABLE_H_
#define RUNNABLE_H_

namespace x10lib_cws {

class Runnable {
public:
	virtual void run()=0;
	virtual ~Runnable() {}
};

};

#endif /*RUNNABLE_H_*/
