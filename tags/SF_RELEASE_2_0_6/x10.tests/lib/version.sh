#!/bin/bash

#
# (c) Copyright IBM Corporation 2009
#
# $Id$
# This file is part of X10 v 2.0 Unified Test Harness.

# these version globals are declared and set here
declare -i __x10th_version_major=2
declare -i __x10th_version_minor=0
declare __x10th_work_revision
declare __x10th_release_version

# print the version string and exit if necessary
# usage: printVersion [exitcode]
# global variables used:
# __x10th_version_major
# __x10th_version_minor
function printVersion() {
    printf >&3 "X10 Unified Test Harness v ${__x10th_version_major}.${__x10th_version_minor}\n"
    if (( $# == 1 )); then
        exit $1
    fi
}

# set revision string for the working copy
# usage: setWorkRev
# should be called after the working root is finalized
# any pending checkouts are completed
# global variables used:
# __x10th_workRoot
# __x10th_module_dist
function setWorkRev() {
    __x10th_work_revision=$(svn info ${__x10th_workRoot}/${__x10th_module_dist} | \
                            egrep '^Revision:' | \
                            awk '{print $2}')
}

# set release string for the binary distribution
# usage: setRelVer
# should be called after the working root is finalized
# any pending build steps are completed
# global variables used:
# __x10th_workRoot
# __x10th_module_dist
# __x10th_backend
function setRelVer() {
    local compcmd

    if [[ "${__x10th_backend}" == "c++" ]]; then
        compcmd="${__x10th_workRoot}/${__x10th_module_dist}/bin/x10c++"
    else
        compcmd="${__x10th_workRoot}/${__x10th_module_dist}/bin/x10c"
    fi
    __x10th_release_version=$(${compcmd} -version | \
                            egrep '^x10c' | \
                            awk '{print $3}')
}

# vim:tabstop=4:shiftwidth=4:expandtab:nu
