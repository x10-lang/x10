#!/bin/bash

#
# (c) Copyright IBM Corporation 2009
#
# $Id$
# This file is part of X10 v 2.0 Unified Test Harness.

# make the binary distribution tarball or zipfile
# for the underlying platform
# usage: doMakeDist
# global variables used:
# __x10th_backend
# __x10th_workRoot
# __x10th_os
# __x10th_arch
# __x10th_work_revision
# __x10th_release_version
function doMakeDist() {
    local pkgfile
    local distname
    local tarball
    local gztarball
    local bz2tarball
    local zipfile
    local distroot

    distroot="x10-${__x10th_release_version}"
    if [[ "${__x10th_backend}" == "c++" ]]; then
        distname="${distroot}_r${__x10th_work_revision}_${__x10th_os}_${__x10th_arch}"
        pkgfile="${__x10th_workRoot}/x10.tests/data/pkg-c++.lst"
        #pkgfile="$HOME/x10.tests/lib/pkg-c++.lst"
    else
        distname="${distroot}_r${__x10th_work_revision}"
        pkgfile="${__x10th_workRoot}/x10.tests/data/pkg-java.lst"
        #pkgfile="$HOME/x10.tests/lib/pkg-java.lst"
    fi

    tarball="${distname}.tar"
    gztarball="${tarball}.gz"
    bz2tarball="${tarball}.bz2"
    zipfile="${distname}.zip"

    printf >&3 "${__x10th_progname}: making distribution ${distname} for ${__x10th_backend}...\n"
    rm -rf ${__x10th_workRoot}/${distroot}
    mkdir -p ${__x10th_workRoot}/${distroot}

    (cd ${__x10th_workRoot}/${__x10th_module_dist}; \
    find $(cat $pkgfile) -name '*.svn' -type d -prune -o -name '*' -print | tar cvf - -T - | \
        (cd ${__x10th_workRoot}/${distroot} && tar xvpf -); \
    cd ${__x10th_workRoot}; \
    if [[ "${__x10th_backend}" == "c++" ]]; then \
        rm -f ${tarball}; \
        tar cvf ${tarball} ${distroot}/*; \
        rm -f ${gztarball}; \
        gzip -9 -v ${tarball}; \
    else \
        rm -f ${zipfile}; \
        zip -r ${zipfile} ${distroot}; \
    fi;)
    if (( $? != 0 )); then
        printf >&3 "${__x10th_progname}: Error ## failed to make distribution ${distname} for ${__x10th_backend}!\n"
        exit 1
    fi
    printf >&3 "${__x10th_progname}: ...finished making distribution ${distname} for ${__x10th_backend}!\n"
}

# vim:tabstop=4:shiftwidth=4:expandtab:nu
