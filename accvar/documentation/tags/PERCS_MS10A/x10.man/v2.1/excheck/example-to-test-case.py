# Common test case frobbies:
# On the first line of the test case, you can append ^^^ classname
# This will: (1) make the test-case class have the given name.
#            (2) write it to a file of that name (plus .x10).
#            Please use this when making a test case that ought to be stable.
# If the string NOTEST appears anywhere in an example's code
# (In particular, in the TeXly-commented parts)
# it will be deleted, and the example will not generate a test case.
# Similarly, NOCOMPILE means to not generate a compile check.
# NOTEST and NOCOMPILE are independent; both can be used to generate nothing.

from __future__ import with_statement
import os;
import os.path;
import re;
currentFileName = "this gets overwritten before use"
clue = "%~~"
sep="~~"
files = []
maxLengthOfTestName = 74 # Magic number imposed by CatTrack.
currentLine = 0
genTestCases = True

#If NOTEST appears anywhere in example code,
#   do not generate a test case.
#If NOCOMPILE does, do not generate a compileCheck.
NOTEST_pattern = "NOTEST"
NOCOMPILE_pattern = "NOCOMPILE"

# Clue indicating that a class should *not* be made a static inner class
# in a testcase
# BEWARE! This is a comment. It's entangled in the code later on.
# NONSTATIC = "/*NONSTATIC*/



texsource = "/Users/bard/x10/manual/x10.man/v2.1" # os.path.abspath("..") #
gennedFileDir = texsource + "/compileCheck"
testCaseDir  = texsource + "/testcases"

# INPUT:
# In file named 'Treeb.tex'
#   %~~stmt(xcd`~~`)~~a:Int,b:Int
#   fooble zimble plowk \xcd`I:Int = a + b;` transom
# OUTPUT
# File named 'Treeb1.x10',
#   public class Treeb1{ def check(a:Int,b:Int) { I:Int = a + b; }
# THAT IS:
#   This is a line of type 'stmt', which wraps a statement
#   in a method called 'check' with formals "a:Int,b:Int"
#   The statement starts on the following line at the string "xcd`", and
#   goes through the next "`" (which might be on that line or a still further one.)



def dealWithExample(f, line, basename):
    cmd, args, fileroot = parsley(line)
    if cmd == "stmt" : doStmt(cmd, args, f, line, basename, fileroot)
    elif cmd == "gen" : doGen(cmd, args, f, line, basename, fileroot)
    elif cmd == "exp" : doExp(cmd, args, f, line, basename, fileroot)
    elif cmd == "genexp" : doGenExp(cmd, args, f, line, basename, fileroot)
    elif cmd == "longexp" : doLongexp(cmd, args, f, line, basename, fileroot)
    elif cmd == "type" : doType(cmd, args, f, line, basename, fileroot)
    else:
        doom("In " + basename + " the command is \""+cmd + "\" -- what the feersnar is this line: " + line)


fileRootPat = re.compile("%~~(.*)\^\^\^(.*)")
# IN: %~~CMD~~a~~b~~c  ^^^fileroot
#     Any number of things after CMD
# OUT: [CMD, [a,b,c]]
def parsley(line):
    
    matcho = fileRootPat.match(line)
    if matcho != None: 
        L = matcho.group(1)
        fileroot = matcho.group(2).strip()
        #from __future__ import with_statement
import os;
import os.path;
import re;
clue = "%~~"
sep="~~"
files = []
currentLine = 0
genTestCases = True


texsource = "/Users/bard/x10/manual/x10.man/v2.1" # os.path.abspath("..") #
gennedFileDir = texsource + "/compileCheck"
testCaseDir  = texsource + "/testcases"

# INPUT:
# In file named 'Treeb.tex'
#   %~~stmt(xcd`~~`)~~a:Int,b:Int
#   fooble zimble plowk \xcd`I:Int = a + b;` transom
# OUTPUT
# File named 'Treeb1.x10',
#   public class Treeb1{ def check(a:Int,b:Int) { I:Int = a + b; }
# THAT IS:
#   This is a line of type 'stmt', which wraps a statement
#   in a method called 'check' with formals "a:Int,b:Int"
#   The statement starts on the following line at the string "xcd`", and
#   goes through the next "`" (which might be on that line or a still further one.)



def dealWithExample(f, line, basename):
    cmd, args, fileroot = parsley(line)
    if cmd == "stmt" : doStmt(cmd, args, f, line, basename, fileroot)
    elif cmd == "gen" : doGen(cmd, args, f, line, basename, fileroot)
    elif cmd == "exp" : doExp(cmd, args, f, line, basename, fileroot)
    elif cmd == "genexp" : doGenExp(cmd, args, f, line, basename, fileroot)
    elif cmd == "longexp" : doLongexp(cmd, args, f, line, basename, fileroot)
    elif cmd == "type" : doType(cmd, args, f, line, basename, fileroot)
    else:
        doom("In " + basename + " the command is \""+cmd + "\" -- what the feersnar is this line: " + line)


fileRootPat = re.compile("%~~(.*)\^\^\^(.*)")
# IN: %~~CMD~~a~~b~~c  ^^^fileroot
#     Any number of things after CMD
# OUT: [CMD, [a,b,c]]
def parsley(line):
    
    matcho = fileRootPat.match(line)
    if matcho != None: 
        L = matcho.group(1)
        fileroot = matcho.group(2).strip()
        #print "matcho! L='" + L + "', rilefoot='" + fileroot + "'"
    else: 
        L = line[len(clue):len(line)]
        fileroot = None
    S = L.split(sep)
    R = [S[0].strip(), [s.strip() for s in S[1:len(S)]], fileroot]
    return R

    
# IN %~~stmt~~xcd`~~`~~a:Int,b:Int
#       weasel's \xcd`I:Int = a+b;` food
# OUT:
#  public class Treebl1 {
#     def check(a:Int,b:Int)  {
#         I:Int = a+b;
#     }}
def doStmt(cmd, args, f, line, basename, fileroot):
    global currentLine
    if len(args) != 3 and len(args) != 4:
        doom("'stmt' takes 3 args -- in " + basename  + "\nline="  + line + " ... plus optionally a fourth for imports and such.")
    starter = args[0]
    ender = args[1]
    formals = args[2].strip()
    importses = args[3] if len(args)==4 else ""
    stmt = extract(f, starter, ender, basename)
    classname = numberedName(basename)
    fileroot = fileroot if fileroot != None else classname
    code = "\n".join([
#          " package stmtsome." + classname + ";",
          "// file " + basename + ".tex,  line " + str(currentLine),
          importses, 
          "class " + classname + "TestStmt{",
          "  def check(" + formals + ")  {",
          "    " + stmt,
          "  }}"])
    writeX10File("stmtsome_" + classname, classname, code, fileroot)

# IN:
#   %~~exp~~xcd`~~`~~a:Int
#   There should be \xcd`a+4` potato chips.
# OUT:
#   public class Chippie13 {
#     def check(a:Int) = a+4;
#   }
def doExp(cmd, args, f, line, basename, fileroot):
    if len(args) != 3 and len(args) != 4:
        doom("'exp' takes 3 args -- in " + basename  + "\nline="  + line + " ... plus optionally a fourth for imports and such.")
    starter = args[0]
    global currentLine
    ender = args[1]
    formals = args[2].strip()
    importses = args[3] if len(args)==4 else ""
    exp = extract(f, starter, ender, basename)
    classname = numberedName(basename)
    fileroot = fileroot if fileroot != None else classname
    code = "\n".join([
#          " package expsome." + classname + ";",
          "// file " + basename + " line " + str(currentLine),
          importses, 
          "class " + classname + "TestExp{",
          "  def check(" + formals + ")  = " + exp + ";"
          "  }"])
    writeX10File("expsome_" + classname, classname, code, fileroot)

# IN:
#   %~~genexp~~xcd`~~`~~T~~a:T, f:(T)=>Int
#   There should be \xcd`f(a)` potato chips.
# OUT:
#   public class Chippie13 {
#     def check[T](a:T, f:(T)=>Int) = f(a);
#   }
def doGenExp(cmd, args, f, line, basename, fileroot):
    if len(args) != 4 and len(args) != 5:
        doom("'genexp' takes 4 args -- in " + basename  + "\nline="  + line + " ... plus optionally a fifth for imports and such.")
    starter = args[0]
    global currentLine
    ender = args[1]
    generics = args[2]
    formals = args[3].strip()
    importses = args[4] if len(args)==5 else ""
    exp = extract(f, starter, ender, basename)
    classname = numberedName(basename)
    fileroot = fileroot if fileroot != None else classname
    code = "\n".join([
#          " package expsome." + classname + ";",
          "// file " + basename + " line " + str(currentLine),
          importses, 
          "class " + classname + "GenexpTest{",
          "  def check[" + generics + "](" + formals + ")  = " + exp + ";"
          "  }"])
    writeX10File("genexpsome_" + classname, classname, code, fileroot)

# IN:
#   %~~type~~xcd`~~`~~a:Int
#   The nerve is of type \xcd`Tofu{a==3}`, unfortunately.
# OUT:
#   package TofuChk8;
#   public class TofuChk8 {
#     def check(a:Int) {
#        var checkycheck : Tofu{a==3};
#     }}
def doType(cmd, args, f, line, basename, fileroot):
    if len(args) != 3 and len(args) != 4:
        doom("'type' takes 3 args -- in " + basename  + "\nline="  + line + " ... plus optionally a fourth for imports and such.")
    starter = args[0]
    global currentLine
    ender = args[1]
    formals = args[2].strip()
    importses = args[3] if len(args)==4 else ""
    typer = extract(f, starter, ender, basename)
    classname = numberedName(basename)
    fileroot = fileroot if fileroot != None else classname
    code = "\n".join([
#          "package " + "typesome." + classname + ";",
          "// file " + basename + " line " + str(currentLine),
          importses, 
          "class " + classname + "TypeTest{",
          "  def check(" + formals + ")  { ",
          "     var checkycheck : " + typer + ";"
          "  }}"])
    writeX10File("typesome_" + classname, classname, code, fileroot)


# IN: 
#     %~~gen
#     %import frob.*;
#     %class Murnitz{      
#     %~~vis
#     \begin{xten}    
#        def this(x:Int) { this.x = x; }
#     \end{xten}
#     %~~siv
#     %}
#     %~~neg
# OUT:
#    import frob*
#    class Murnitz{
#        def this(x:Int) { this.x = x; }
#    }
# NOTE: The visible section (%~~vis ... %~~siv) is written as X10 + LaTeX.
# \begin and \end lines are stripped out.
# 
# The prelude and postlude  (%~~gen..%~~vis and %~~siv..%~~neg) have
# TeX comments in the LaTeX source, which must be stripped off for X10.
#
# Addendum
#
#    #~~gen~~foon
#      puts the output in the file named "foon.x10"


def doGen(cmd, args, f, line, basename, fileroot):
    #print("doGen: " + cmd + " on " + "!".join(args));
    global currentLine
    prelude = (
       ["// file " + basename + " line " + str(currentLine) + "\n"] +
       readLines(f, "%~~vis", True, False, basename)
       )
    body = readLines(f, "%~~siv", False, True, basename)
    postlude = readLines(f, "%~~neg", True, False, basename)
    classname = numberedName(basename)
    fileroot = fileroot if fileroot != None else classname
    if len(args) >= 1:
        classname = args[0];
    fullcode = "\n".join(prelude + body + postlude)
    (packagename, code) = dissectOutPackageName(fullcode, fileroot, classname)
    writeX10File(packagename, classname, code, fileroot)

# IN
#   %~~longexp~~`~~`
#   % package whatever;
#   % class Whatever{
#   %  def zap() { val x = 
#   %~~vis
#   I think \xten`this` is a bad name for "self"    
#   %~~siv
#   % ;
#   % }}
# OUT:
#   package whatever;
#   class Whatever{
#    def zap() { val x = this ; }}
#
# So it's got the invisible parts like %~~gen, but works on a part of a line like %~~exp
def doLongexp(cmd, args, f, line, basename, fileroot):
    # Mirroring exp code to get the delimiters of the exp to check
    global currentLine
    if len(args) != 2:
        doom("'longexp' takes exactly two args -- in " + basename  + "\nline="  + line)
    starter = args[0].strip()
    ender = args[1].strip()
    # mirroring gen code to get the prelude
    prelude = readLines(f, "%~~vis", True, False, basename)
    # mirroring exp code to get the exp to check
    exp = extract(f, starter, ender, basename)
    # Back to gen code for postlude
    postlude = readLines(f, "%~~pxegnol", True, False, basename)
    classname = numberedName(basename)
    fileroot = fileroot if fileroot != None else classname
    #print "longexp: starter = '" + starter + "', ender = '" + ender + "', exp='" + exp + "'"
    fullcode = "\n\n" + "\n".join(prelude + [exp] + postlude)
    #print "longexp: fullcode = " + fullcode
    (packagename, code) = dissectOutPackageName(fullcode, fileroot, classname)
    writeX10File(packagename, classname, code, fileroot)


packagepattern = re.compile(" *package(.*);")

def dissectOutPackageName(fullcode, fileroot, classname):
    splut = fullcode.splitlines()
    for i in range(len(splut)):
        matcho = packagepattern.match(splut[i])
        #print "LINE = " + splut[i] + " -> " + str(matcho)
        if matcho != None:
            otherlines = splut[0:i-1] + splut[i+1:len(splut)]
            pck = matcho.group(1).replace(".", "_")
            #print "\n\n IN " + fullcode + "\n ---> PACKAGE=" + pck
            return (pck, "\n".join(otherlines))
    defaulteriffic_package = fileroot + "_" + classname
    return (defaulteriffic_package, fullcode)


# Read lines from f.  Return the substring of f
# between 'starter' and 'ender' (exclusive).
def extract(f, starter, ender, basename):
    L1 = f.readline()
    global currentLine
    currentLine += 1
    p1 = L1.find(starter)
    if p1 < 0: doom("Need a '" + starter + "' in " + basename + " on the line currently " + L1 + " pos: " + f.tell())
    S = L1[p1 + len(starter) : len(L1)]
    while True:
        p2 = S.find(ender)
        if p2 < 0:
            L2 = f.readline()
            currentLine += 1
            S = S + L2
            continue
        extractment = S[0:p2]
        return extractment

# Read lines from f until a line==endMarker.
# If stripLeadingPercents=True, strip leading % signs from the lines.
# Return the lines as a list
# if stripBeginAndEnd==True, strip \begin and \end commands.
def readLines(f, endMarker, stripLeadingPercents, stripBeginAndEnd, basename):
    L = []
    global currentLine
    while True:
        line = f.readline()
        currentLine += 1
        if line == "": doom("End of file (" + basename + ") looking for " + endMarker)
        line = line.rstrip()
        if line.strip() == endMarker:
            #print "readLines("  + endMarker + ") ...\n   " + "\n   ".join(L)
            return L
        if stripLeadingPercents:
            if line.startswith("%") : line = line[1:len(line)]
        beginOrEnd = line.startswith("\\begin") or line.startswith("\\end")
        if not (stripBeginAndEnd and beginOrEnd): 
            L = L + [line]
        

def doom(msg):
    raise Exception(msg)


def cramCodeIntoFile(filename, code, forbidFlag):
    forbidFlagThere, deflaggedCode = findAndDelete(code, forbidFlag)
    if not forbidFlagThere: 
        f = open(filename, 'w')
        f.write(deflaggedCode)
        f.flush()
        f.close()
#    else:
        # print "Forbidden: " + filename

testPrelude = '''
/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;
'''
testHarnessBegin = '''
public class %s extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new %s().execute();
    }

'''

testHarnessEnd = "}"

findHookPat = ("class Hook")
standardHookCode = '''
class Hook {
   def run():Boolean = true;
}
'''

def nonStaticRepl(matchobj):
    #print "nonStaticRepl(%s)..." % matchobj.group(0)
    if matchobj.group(0).startswith("/*NONSTATIC*/"):
        return matchobj.group(2)
    else:
        return " static " + matchobj.group(0)

innerClassUlizeRE = re.compile("(/\\*NONSTATIC\\*/)? *\\b(class|interface|struct)\\b")
def innerClassIcate(code):
    s = re.sub(innerClassUlizeRE, nonStaticRepl, code)
    return s

def extractImportLines(code):
    imp = []
    non = []
    for L in code.splitlines():
        if L.find("import") > -1:
            imp = imp + [L]
            #print "IMPORT -> " + L
        else:
            non = non + [L]
            #print "NON -> " + L
    return ["\n".join(imp), "\n".join(non)]
        

def assembleTestCase(packageline, classname, code, fileroot):
    [importlines, deimp_code]  = extractImportLines(code)
    warning = "// Warning: This file is auto-generated from the TeX source of the language spec.\n"
    warning += "// If you need it changed, work with the specification writers.\n"
    #print "ATC: " + importlines + "\n ////// \n" + deimp_code
    testclassbeginning = testHarnessBegin % (fileroot, fileroot)
    testclassend = testHarnessEnd
    hook = "" if deimp_code.find(findHookPat) > -1 else standardHookCode
    innercode = innerClassIcate(deimp_code + "\n" + hook )
    bits = [packageline, warning, testPrelude, importlines, testclassbeginning, innercode, testclassend]
    return "\n".join(bits)

def findAndDelete(corpus, substring):
    hasFlag = corpus.find(substring) > -1
    deflagged = corpus.replace(NOTEST_pattern, "").replace(NOCOMPILE_pattern, "")
    #print "findAndDelete " + substring + "->" + str(hasFlag) + "\n" + corpus + "\n\n\n\n"
    return hasFlag, deflagged


def writeX10File(packagename, classname, code, fileroot):
    global files
    global currentFileName
    packageline =  "package " + packagename.strip() + ";\n"
    code1 = packageline + code;
    fn = gennedFileDir + "/" + fileroot + ".x10"
    cramCodeIntoFile(fn, code1, NOCOMPILE_pattern)
    NOpackageline = "/* Current test harness gets confused by packages, but it would be in " + packageline + "*/"
    
    testcasecode = assembleTestCase(NOpackageline, classname, code, fileroot)
    # print "\n\n\ntestcasecode=" + testcasecode
    if len(testCaseDir) > maxLengthOfTestName-20:
        print "A doom for me! The test case dir " + testCaseDir + " is too long, at " + str(len(testCaseDir))
    if len(fileroot) > maxLengthOfTestName-20:
        print "A Doom for you!  The name of " + fileroot + " is too long, at " + str(len(fileroot))
    subdir_for_this_test_case = testCaseDir + "/" + (fileroot.strip())

    if os.path.exists(subdir_for_this_test_case):
        print "OH NO! Duplicate package! Here's the second one:\n"  + code
    (testCaseSubDir, dummy)  = os.path.splitext(currentFileName)
    subdir = testCaseDir + "/" + testCaseSubDir 
    if not os.path.exists(subdir) :
        os.makedirs(subdir)
    wholeFileName =  subdir + "/" + fileroot + ".x10"
    cramCodeIntoFile(wholeFileName, testcasecode, NOTEST_pattern)
    files.append(fn)
    
    

# Return Foo1, Foo2, etc -- distinct class names for files from chapter Foo.
name2number = {}
def numberedName(basename):
    if basename in name2number:
        oldn = name2number[basename]
        newn = oldn + 1
        name2number[basename] = newn
        return basename + str(newn)
    else:
        name2number[basename] = 1
        return basename + "1"

totalTestCases = 0
def extractExamplesFrom(tf):
    global totalTestCases
    global currentLine
    currentLine = 0
    basename = tf[0:len(tf)-4]
    # print tf + " -> \"" + basename + "\""
    name2number[basename] = 0
    with open(texsource+"/"+tf) as f:
         line = f.readline()
         currentLine += 1
         while line != "": 
            if line.startswith(clue):
                dealWithExample(f, line, basename)
            elif line.find(clue) >= 0:
                doom("'" + clue + "' must appear at the start of the line!\n" + 
                     "line=" + line + "\n" +
                     "file=" + tf + "\n")
            line = f.readline()
            currentLine += 1
    print basename.ljust(22) + " = " + str(name2number[basename])
    totalTestCases += name2number[basename]

def extractExamplesFromAllFiles():
    global totalTestCases
    global currentFileName
    texfiles = [ fn  for fn in os.listdir(texsource) if fn.endswith(".tex") ]
    for tf in texfiles:
        currentFileName = tf
        extractExamplesFrom(tf)
    print str(totalTestCases) + " test cases."
        
os.chdir(gennedFileDir);
os.system("rm -r " + gennedFileDir + "/*");
os.system("rm -r " + testCaseDir + "/*");
extractExamplesFromAllFiles()
#print ("\n".join(files))
os.system("x10c -STATIC_CALLS *.x10")
#for f in files:
#    print "... " + f + " ... "
#    s = ("/Users/bard/x10/x10-trunk/x10.dist/bin/x10c -STATIC_CALLS " + f + "")
#   # print "  s=" + s
#    os.system(s)
    
