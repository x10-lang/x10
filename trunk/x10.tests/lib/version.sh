#!/bin/bash

#
# (c) Copyright IBM Corporation 2009
#
# $Id$
# This file is part of X10 v 2.0 Unified Test Harness.

# these version globals are declared and set here
declare -i __x10th_version_major=2
declare -i __x10th_version_minor=0

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

# vim:tabstop=4:shiftwidth=4:expandtab:nu
