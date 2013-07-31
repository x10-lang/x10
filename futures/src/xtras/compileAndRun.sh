echo .................................
echo Compile:
# Compile
~/workspace2/x10.dist/bin/x10c -O AsyncWhenTest.x10

echo .................................
echo Run:
# Run
X10_NPLACES=4 ~/workspace2/x10.dist/bin/x10 AsyncWhenTest
echo .................................



