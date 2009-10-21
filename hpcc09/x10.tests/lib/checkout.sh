#!/bin/bash

#
# (c) Copyright IBM Corporation 2009
#
# $Id$
# This file is part of X10 v 2.0 Unified Test Harness.

# checkout all the x10 and polyglot modules into the working directory
# usage: doCheckOut
# global variables used:
# __x10th_x10_modules
# __x10th_polyglot_modules
# __x10th_x10_svnroot
# __x10th_polyglot_svnroot
# __x10th_workRoot
# __x10th_polyglot
# __x10th_revision
# __x10th_checkout_attempts

function doCheckOut() {
    local -i num_attempts
    local -i status

    (cd "${__x10th_workRoot}"; \
    for module in ${__x10th_polyglot_modules}; do \
        let 'num_attempts = 1'; \
        printf >&3 "${__x10th_progname}: checking out module ${module}...\n"; \
        while (( $num_attempts <= $__x10th_checkout_attempts )); do \
            printf >&3 "${__x10th_progname}: ## doing attempt ${num_attempts} for ${module} ##\n"; \
            rm -rf $module; \
            svn co -r "${__x10th_polyglot}" ${__x10th_polyglot_svnroot}/${module}; \
            status=$?; \
            if (( $status != 0 )); then \
                if (( $num_attempts >= $__x10th_checkout_attempts )); then \
                    printf >&3 "${__x10th_progname}: Error ## checkout of module $module failed!\n"; \
                    exit $status; \
                else \
                    let 'num_attempts += 1'; \
                    continue; \
                fi; \
            else \
                printf >&3 "${__x10th_progname}: ## attempt ${num_attempts} for ${module} succeeded ##\n"; \
                break; \
            fi; \
        done; \
        printf >&3 "${__x10th_progname}: ...done with module ${module}\n"; \
    done; \
    for module in ${__x10th_x10_modules}; do \
        let 'num_attempts = 1'; \
        printf >&3 "${__x10th_progname}: checking out module ${module}...\n"; \
        while (( $num_attempts <= $__x10th_checkout_attempts )); do \
            printf >&3 "${__x10th_progname}: ## doing attempt ${num_attempts} for ${module} ##\n"; \
            rm -rf $module; \
            svn co -r "${__x10th_revision}" ${__x10th_x10_svnroot}/${module}; \
            status=$?; \
            if (( $status != 0 )); then \
                if (( $num_attempts >= $__x10th_checkout_attempts )); then \
                    printf >&3 "${__x10th_progname}: Error ## checkout of module $module failed!\n"; \
                    exit $status; \
                else \
                    let 'num_attempts += 1'; \
                    continue; \
                fi; \
            else \
                printf >&3 "${__x10th_progname}: ## attempt ${num_attempts} for ${module} succeeded ##\n"; \
                break; \
            fi; \
        done; \
        printf >&3 "${__x10th_progname}: ...done with module ${module}\n"; \
    done;)
    return $?
}

# vim:tabstop=4:shiftwidth=4:expandtab:nu
