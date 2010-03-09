#!/bin/bash

#
# (c) Copyright IBM Corporation 2009
#
# $Id$
# This file is part of X10 v 2.0 Unified Test Harness.

# build all x10 modules and associated libraries
# usage: doBuild
# global variables used:
# __x10th_backend
# __x10th_module_polyglot
# __x10th_module_dist
# __x10th_module_common
# __x10th_module_constraints
# __x10th_module_cppbackend
# __x10th_workRoot
# __x10th_pgas

function doBuild() {
    printf >&3 "${__x10th_progname}: building module ${__x10th_module_polyglot}...\n"
    (cd "${__x10th_workRoot}/${__x10th_module_polyglot}"; ant clean jar >&3 ; exit $?)
    if (( $? != 0 )); then
        printf >&3 "${__x10th_progname}: Error ## failed to build module ${__x10th_module_polyglot}!\n"
        exit 1
    fi
    printf >&3 "${__x10th_progname}: ...done module ${__x10th_module_polyglot} build\n"
    LOCAL_POLYGLOT_JAR="${__x10th_workRoot}/${__x10th_module_polyglot}/lib/polyglot.jar"
    if [[ "$(uname -s)" == CYGWIN* ]]; then
        LOCAL_POLYGLOT_JAR="$(cygpath -w -a $LOCAL_POLYGLOT_JAR)"
    fi
    export LOCAL_POLYGLOT_JAR
    
    local dist_target="dist-java"
    if [[ "${__x10th_backend}" == "c++" ]]; then
        dist_target="dist-cpp"
        X10LIB="${__x10th_pgas}"
        if [[ "$(uname -s)" == CYGWIN* ]]; then
            X10LIB="$(cygpath -w -a $X10LIB)"
        fi
        export X10LIB
    fi
    printf >&3 "${__x10th_progname}: building module ${__x10th_module_dist}...\n"
    (cd "${__x10th_workRoot}/${__x10th_module_dist}"; ant clean ${dist_target} >&3; exit $?)
    if (( $? != 0 )); then
        printf >&3 "${__x10th_progname}: Error ## failed to build module ${__x10th_module_dist}!\n"
        exit 1
    fi
    printf >&3 "${__x10th_progname}: ...done module ${__x10th_module_dist} build\n"

    for module in ${__x10th_module_common} ${__x10th_module_constraints} ${__x10th_module_cppbackend}
    do
        prinf >&3 "${__x10th_progname}: building module ${module}...\n"
        (cd "${__x10th_workRoot}/${module}"; ant clean dist >&3; exit $?)
        if (( $? != 0 )); then
            printf >&3 "${__x10th_progname}: Error ## failed to build module ${module}!\n"
            exit 1
        fi
        printf >&3 "${__x10th_progname}: ...done module ${module} build\n"
    done
}

# vim:tabstop=4:shiftwidth=4:expandtab:nu
