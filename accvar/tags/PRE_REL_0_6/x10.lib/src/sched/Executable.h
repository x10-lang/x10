#ifndef x10lib_Executable_h
#define x10lib_Executable_h

namespace x10lib_cws {

  class Worker;
  
  class Executable {
  public:
    virtual Executable *execute(Worker *ws) = 0;
  };

};


#endif /*x10lib_Executable_h*/
