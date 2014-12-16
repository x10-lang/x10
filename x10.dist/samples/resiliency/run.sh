#!/usr/bin/env bash

[ -n "$BASE_PORT" ] || BASE_PORT=27000
[ -n "$X10_NPLACES" ] || X10_NPLACES=1
X10_FORCEPORTS="$BASE_PORT"

for X10_LAUNCHER_PLACE in `seq 1 $((X10_NPLACES-1))` ; do
    X10_FORCEPORTS="$X10_FORCEPORTS,$((BASE_PORT+X10_LAUNCHER_PLACE))"
done

for X10_LAUNCHER_PLACE in `seq 0 $((X10_NPLACES-1))` ; do
    export X10_FORCEPORTS X10_LAUNCHER_PLACE
    "$@" &
    eval "PID$X10_LAUNCHER_PLACE=$!"
    eval echo "PID$X10_LAUNCHER_PLACE=\$PID$X10_LAUNCHER_PLACE"
done

function abort() {
    echo -e "Received INT...\nKilling all child processes..."
    for X10_LAUNCHER_PLACE in `seq 0 $((X10_NPLACES-1))` ; do
        eval kill "\$PID$X10_LAUNCHER_PLACE"
    done
}

trap abort INT

wait
