#!/bin/bash

#
# (c) Copyright IBM Corporation 2009
#
# $Id$
# This file is part of X10 v 2.0 Unified Test Harness.

# various runtime parameters are declared and set here
declare __x10th_system
declare __x10th_arch
declare __x10th_os

__x10th_system="$(uname -s)"
if [[ "${__x10th_system}" == CYGWIN* ]]; then
    __x10th_system="${__x10th_system%_*}"
fi
__x10th_os="$(echo ${__x10th_system} | tr '[:upper:]' '[:lower:]')"
__x10th_arch="$(uname -p)"

# vim:tabstop=4:shiftwidth=4:expandtab:nu
