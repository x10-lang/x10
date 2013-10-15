#ifdef __CYGWIN__
#undef __STRICT_ANSI__ // Strict ANSI mode is too strict in Cygwin
#endif

#include <cstdlib>
#include <cstring>
#include <iostream>
#include <iomanip>

#include <strings.h>
#include <unistd.h>
#include <signal.h>
#include <stdio.h>

#include <x10rt_front.h>

int *null_ptr = 0;

void report_fault(int signal)
{
	switch(signal)
	{
		case SIGSEGV:
			printf("Segmentation Fault\n");
		break;
		case SIGFPE:
			printf("Floating Point Exeption\n");
		break;
	}
	abort();
}

int main(int argc, char **argv)
{
	// the default signal handler doesn't flush stderr, so we register our own, which does.
//	signal(SIGSEGV,report_fault);
//	signal(SIGFPE,report_fault);

    x10rt_error init_err = x10rt_init(&argc, &argv);
    if (init_err != X10RT_ERR_OK) {
        if (x10rt_error_msg() != NULL)
            std::cerr << "X10RT fatal initialization error:  " << x10rt_error_msg() << std::endl;
        abort();
    }
    x10rt_registration_complete();

    if (x10rt_here()==0) {
        std::cout << "This test simulates a crash to ensure x10rt implementations shut down cleanly." << std::endl;
    }

    sleep(x10rt_here());
    
    const char *crash_mode = getenv("CRASH_MODE")==NULL ? "segfault" : getenv("CRASH_MODE");

    if (x10rt_here() == x10rt_nhosts()-1) {
        if (!strcasecmp(crash_mode, "segfault")) {
            std::cerr << "Dereferencing null at place " << x10rt_nhosts()-1 << "..." << std::endl;
            *null_ptr = 42;
        } else if (!strcasecmp(crash_mode, "exit")) {
            std::cerr << "Calling exit() before x10rt_finalize() at place " << x10rt_nhosts()-1 << "..." << std::endl;
            exit(EXIT_FAILURE);
        } else if (!strcasecmp(crash_mode, "abort")) {
            std::cerr << "Calling abort() at place " << x10rt_nhosts()-1 << "..." << std::endl;
            abort();
        } else {
            std::cerr << "CRASH_MODE is unknown: \"" << crash_mode << "\"" << std::endl;
            std::cerr << "Not simulating a crash." << std::endl;
        }
    }

    x10rt_finalize();
    return EXIT_SUCCESS;
}

// vim: shiftwidth=4:tabstop=4:expandtab:textwidth=100

