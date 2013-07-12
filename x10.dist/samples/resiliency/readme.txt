How to compile and run ResilientMontePi.x10

For Managed X10:
x10c ResilientMontePiPlaceZero.x10
X10_NPLACES=4 ./run.sh x10 ResilientMontePiPlaceZero

For Native X10:
x10c++ ResilientMontePiPlaceZero.x10
X10_NPLACES=4 ./run.sh a.out

----
How to compile and run ResilientKMeans.x10

For Managed X10:
x10c ResilientKMeans.x10
X10_RESILIENT_PLACE_ZERO=1 X10_NPLACES=4 run.sh x10 ResilientKMeans [num_points] [num_clusters]

For Native X10:
x10c++ ResilientKMeans.x10 -o ResilientKMeans
X10_RESILIENT_PLACE_ZERO=1 X10_NPLACES=4 run.sh runx10 ResilientKMeans [num_points] [num_clusters]

----
How to compile and run ResilientHeatTransfer.x10
(ResilientDistArray.x10 and ResilientPlaceGroup.x10 are also compiled)

For Managed X10:
x10c ResilientHeatTransfer.x10
X10_RESILIENT_PLACE_ZERO=1 X10_NPLACES=4 run.sh x10 ResilientHeatTransfer [size]

For Native X10:
x10c++ ResilientHeatTransfer.x10 -o ResilientHeatTransfer
X10_RESILIENT_PLACE_ZERO=1 X10_NPLACES=4 run.sh runx10 ResilientHeatTransfer [size]
