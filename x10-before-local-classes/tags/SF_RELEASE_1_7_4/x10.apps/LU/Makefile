
CXX = mpCC_r
CXXFLAGS = -g

all: plu-dist-amsend

plu-dist: plu-dist.cc
	$(CXX) $(CXXFLAGS) -O3 -qarch=pwr5 -qrtti -o plu-dist plu-dist.cc -lblas -lm -lpthread -lf

plu-dist-amsend: plu-dist.amsend.cc
	$(CXX) $(CXXFLAGS) -O3 -qarch=pwr5 -q64 -qrtti -o plu-dist-amsend plu-dist.amsend.cc -lblas -lm -lpthread -lf
