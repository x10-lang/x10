#include <cstdlib>
#include <iostream>
#include <iomanip>

#include <unistd.h>

#include <x10rt_front.h>

const char *typestr(x10rt_place p) {
    if (x10rt_is_host(p)) return "[1mHOST[0m";
    if (x10rt_is_cuda(p)) return "[32mCUDA[0m";
    return "UNK!";
}

int main(int argc, char **argv)
{
    x10rt_error init_err = x10rt_init(&argc, &argv);
    if (init_err != X10RT_ERR_OK) {
        if (x10rt_error_msg() != NULL)
            std::cerr << "X10RT fatal initialization error:  " << x10rt_error_msg() << std::endl;
        abort();
    }
    std::cout << "Initialisation of x10rt complete." << std::endl;
    x10rt_registration_complete();

    sleep(x10rt_here());

    std::cout << "Hosts: " << x10rt_nhosts() << std::endl;
    std::cout << "Nodes: " << x10rt_nplaces() << std::endl;
    std::cout << "Here: " << x10rt_here() << std::endl;

    std::cout << "┐" << std::endl;
    for (x10rt_place i=0 ; i<x10rt_nhosts() ; ++i) {

        int last = i==x10rt_nhosts()-1;

        std::cout << (last?"└":"├") << "─"<<std::setfill('0')<<std::setw(2)<<i<<"("<<std::setfill('0')<<std::setw(2)<<x10rt_parent(i)<<")─"<<typestr(i);
        if (x10rt_nchildren(i)>0) {
            std::cout << "─┐";
        }

        std::cout << std::endl;

        for (x10rt_place j=0 ; j<x10rt_nchildren(i) ; ++j) {
            x10rt_place p = x10rt_child(i,j);
            int last2 = j==x10rt_nchildren(i)-1;
            std::cout << (last?" ":"│")<<"             "
                      << (last2?"└":"├")<<"─"<<j<<"─"<<p<<"("<<x10rt_parent(p)<<")-"
                      << typestr(p)<<std::endl;
                   
        }

    }

    x10rt_finalize();
    return EXIT_SUCCESS;
}

// vim: shiftwidth=2:tabstop=2:expandtab:textwidth=80

