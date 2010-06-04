#!/bin/bash

#
# (c) Copyright IBM Corporation 2009
#
# $Id: xversion.sh 11142 2009-09-08 12:41:53Z srkodali $
# This file is part of X10 v 2.0 Unified Test Harness.

. ../version.sh

function versionTest() {
    printVersion
}

versionTest 3>&2

# vim:tabstop=4:shiftwidth=4:expandtab:nu
