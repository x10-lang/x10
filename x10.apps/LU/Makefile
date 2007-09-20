
CXX = mpCC_r
CXXFLAGS = -g

all: plu-dist

plu-dist: plu-dist.cc
	$(CXX) $(CXXFLAGS) -O3 -qarch=pwr5 -qrtti -o plu-dist plu-dist.cc -lblas -lm -lpthread -lf

