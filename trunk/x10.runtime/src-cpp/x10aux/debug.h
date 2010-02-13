//
// A list of interfaces agreed upon with the Toronto Parallel Debugger
// team for passing source program information to the C++ debugger
// back-end.
//
// Based on X10LineNumberDebug Design Document Version 2, Jan 25, 2010
// Updated as agreed with Roger Pett on Feb 11, 2010
//

#ifndef X10AUX_DEBUG_H
#define X10AUX_DEBUG_H

// Each generated C++ file is expected to have the following information:
//
// static const char _X10strings[] = {};
//   // All strings, concatenated, with intervening nulls.
//   // e.g., for the strings "aa", "bb", and "cc" , this variable would contain  "aa\0bb\0ccc".
// static const struct _X10sourceFile _X10sourceList[];
//   // A list of X10 source files that contributed to the generation of the current C++ file.
// static const struct _X10toCPPxref _X10toCPPlist[] = {};
//   // A cross reference of X10 statements to the first C++ statement.
//   // Sorted by X10 file index and X10 source file line.
// static const struct _CPPtoX10xref _CPPtoX10xrefList[] = {};
//   // A cross reference of C++ statements to X10 statements.
//   // Sorted by C++ file index and C++ source file line. 
//   // A line range is used to minimize the storage required.
// static const struct _X10methodName _X10methodNameList[] = {};
//   // A list of the X10 method names.
//   // Sorted by X10 method name.

struct _X10sourceFile
{
    uint32_t _numLines;    // The number of lines in the X10 source file
    uint32_t _stringIndex; // Index in _X10strings of the name of the X10 source file. 
};

struct _X10toCPPxref
{
    uint16_t _X10index;       // Index of X10 file name in _X10sourceList
    uint16_t _X10method;      // Index into _X10methodNameList of the X10 method (see Method Mapping)
    uint32_t _CPPindex;       // Index of C++ file name in _X10strings
    uint32_t _X10line;        // Line number of X10 source file line
    uint32_t _CPPline;        // Line number of C++ source file line
};

struct _CPPtoX10xref
{
    uint16_t _X10index;       // Index of X10 file name in _X10sourceList
    uint16_t _X10method;      // Index into _X10methodNameList of the X10 method (see Method Mapping)
    uint32_t _CPPindex;       // Index of C++ file name in _X10strings
    uint32_t _X10line;        // Line number of X10 line
    uint32_t _CPPfromLine;    // First line number of C++ line range
    uint32_t _CPPtoLine;      // Last line number of C++ line range
};

struct _MethodSignature
{
};

struct _X10methodName
{
    uint32_t _x10class;          // Index of the X10 containing class name in _X10strings
    uint32_t _x10method;         // Index of the X10 method name in _X10strings
    uint32_t _x10returnType;     // Index of the X10 return type in _X10strings
    uint64_t _x10args;           // A pointer to a string that contains binary encodings of the
                                 // argument indices.  Each group of 4 bytes represents the index
                                 // of the corresponding argument in _X10strings
    uint32_t _cppClass;          // Index of the C++ class name in _X10strings
    uint16_t _x10argCount;       // The number of X10 arguments
    uint16_t _lineIndex;         // Index into _X10toCPPlist of the first line of the method
};

extern void _X10_Entry_Hook();     // A hook at the start of every X10 method.
extern void _X10_Exit_Hook();      // A hook at the end of every X10 method.
extern void _X10_Statement_Hook(); // A hook at the start of every X10 executable statement.
                                   // Follows any method start hook, and precedes any method end hook.

#endif //X10AUX_DEBUG_H
// vim:tabstop=4:shiftwidth=4:expandtab
