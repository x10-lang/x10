/*
============================================================================
 Name        : Job.h
 Author      : Rajkishore Barik
 Version     :
 Copyright   : IBM Corporation 2007
 Description : Exe source file
============================================================================
*/
#ifndef x10lib_Job_h
#define x10lib_Job_h

namespace x10lib_cws {

enum { LABEL_0=0,LABEL_1=1, LABEL_2=2, LABEL_3=3};

class Job : public Closure {
	
protected:
	void compute(Worker *w, Frame *frame);
	int spawnTask(Worker *ws) ;
	
public:
	Pool *pool;
	
	void completed();
	void waitForCompletion();
	bool isCancelled();
	bool cancel(bool b);
	pthread_cond_t cond_done;
	
	class GloballyQuiescentJob : public Job {
	private:
	protected:
	public:
		GloballyQuiescentJob(Pool *pool);
		GloballyQuiescentJob(Pool *pool, Frame *f);
		bool requiresGlobalQuiescence();
		void compute(Worker *w, Frame *frame);
	};
	
	class GFrame : public JobFrame {
	public:
		volatile int PC;
		void setOutletOn(Closure *c);
		Closure *makeClosure();
		GFrame();
	};
	
	class JobFrame : public Frame {
	public :
		class ResultOutlet : public Outlet {
		public:
			Closure *closure;
			JobFrame *jframe;
			void run();
			ResultOutlet(Closure *c, JobFrame *f);
		}
		volatile int PC;
		int x;
		JobFrame();
		Closure *makeClosure();
		void setOutletOn(Closure *c);
	}
}
}
#endif
