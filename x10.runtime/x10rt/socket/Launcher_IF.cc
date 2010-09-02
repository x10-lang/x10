#include "Launcher.h"

extern "C" int Launcher_Init(int argc, char ** argv)
{
	Sock::Launcher::Setup(argc, argv);
	return 0;
}

extern "C" int Launcher_myproc()
{
	return Sock::Launcher::myproc();
}

extern "C" int Launcher_nprocs()
{
	return Sock::Launcher::nprocs();
}
