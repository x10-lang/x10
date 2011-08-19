#!/bin/bash

#
# (c) Copyright IBM Corporation 2009
#
# $Id$
# This file is part of X10 v 2.0 Unified Test Harness.

. ../usage.sh
. ../version.sh

function usageMain() {
    case "$1" in
        short)
            printUsage 1 0
            ;;
        long)
            printUsage 0 1
            ;;
        *)
            ;;
    esac
}

__x10th_progname="x10th"
usageMain "long" 3>&2

# vim:tabstop=4:shiftwidth=4:expandtab:nu
