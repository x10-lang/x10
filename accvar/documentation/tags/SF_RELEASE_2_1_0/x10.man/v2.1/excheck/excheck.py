from __future__ import with_statement
import os;
clue = "%~~"
sep="~~"
files = []
currentLine = 0


texsource = "/Users/bard/x10/manual/x10.man/v2.1"
gennedFileDir = texsource + "/testcases"

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
    cmd, args = parsley(line)
    if cmd == "stmt" : doStmt(cmd, args, f, line, basename)
    elif cmd == "gen" : doGen(cmd, args, f, line, basename)
    elif cmd == "exp" : doExp(cmd, args, f, line, basename)
    elif cmd == "genexp" : doGenExp(cmd, args, f, line, basename)
    elif cmd == "longexp" : doLongexp(cmd, args, f, line, basename)
    elif cmd == "type" : doType(cmd, args, f, line, basename)
    else:
        doom("In " + basename + " the command is \""+cmd + "\" -- what the feersnar is this line: " + line)

# IN: %~~CMD~~a~~b~~c
#     Any number of things after CMD
# OUT: [CMD, [a,b,c]]
def parsley(line):
    L = line[len(clue):len(line)]
    S = L.split(sep)
    R = [S[0].strip(), [s.strip() for s in S[1:len(S)]]]
    return R

    
# IN %~~stmt~~xcd`~~`~~a:Int,b:Int
#       weasel's \xcd`I:Int = a+b;` food
# OUT:
#  public class Treebl1 {
#     def check(a:Int,b:Int)  {
#         I:Int = a+b;
#     }}
def doStmt(cmd, args, f, line, basename):
    global currentLine
    if len(args) != 3 and len(args) != 4:
        doom("'stmt' takes 3 args -- in " + basename  + "\nline="  + line + " ... plus optionally a fourth for imports and such.")
    starter = args[0]
    ender = args[1]
    formals = args[2].strip()
    importses = args[3] if len(args)==4 else ""
    stmt = extract(f, starter, ender, basename)
    classname = numberedName(basename)
    code = "\n".join([
          " package stmtsome." + classname + ";",
          "// file " + basename + " line " + str(currentLine),
          importses, 
          "public class " + classname + "{",
          "  def check(" + formals + ")  {",
          "    " + stmt,
          "  }}"])
    writeX10File(classname, code)

# IN:
#   %~~exp~~xcd`~~`~~a:Int
#   There should be \xcd`a+4` potato chips.
# OUT:
#   public class Chippie13 {
#     def check(a:Int) = a+4;
#   }
def doExp(cmd, args, f, line, basename):
    if len(args) != 3 and len(args) != 4:
        doom("'exp' takes 3 args -- in " + basename  + "\nline="  + line + " ... plus optionally a fourth for imports and such.")
    starter = args[0]
    global currentLine
    ender = args[1]
    formals = args[2].strip()
    importses = args[3] if len(args)==4 else ""
    exp = extract(f, starter, ender, basename)
    classname = numberedName(basename)
    code = "\n".join([
          " package expsome." + classname + ";",
          "// file " + basename + " line " + str(currentLine),
          importses, 
          "public class " + classname + "{",
          "  def check(" + formals + ")  = " + exp + ";"
          "  }"])
    writeX10File(classname, code)

# IN:
#   %~~genexp~~xcd`~~`~~T~~a:T, f:(T)=>Int
#   There should be \xcd`f(a)` potato chips.
# OUT:
#   public class Chippie13 {
#     def check[T](a:T, f:(T)=>Int) = f(a);
#   }
def doGenExp(cmd, args, f, line, basename):
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
    code = "\n".join([
          " package expsome." + classname + ";",
          "// file " + basename + " line " + str(currentLine),
          importses, 
          "public class " + classname + "{",
          "  def check[" + generics + "](" + formals + ")  = " + exp + ";"
          "  }"])
    writeX10File(classname, code)

# IN:
#   %~~type~~xcd`~~`~~a:Int
#   The nerve is of type \xcd`Tofu{a==3}`, unfortunately.
# OUT:
#   package TofuChk8;
#   public class TofuChk8 {
#     def check(a:Int) {
#        var checkycheck : Tofu{a==3};
#     }}
def doType(cmd, args, f, line, basename):
    if len(args) != 3 and len(args) != 4:
        doom("'type' takes 3 args -- in " + basename  + "\nline="  + line + " ... plus optionally a fourth for imports and such.")
    starter = args[0]
    global currentLine
    ender = args[1]
    formals = args[2].strip()
    importses = args[3] if len(args)==4 else ""
    typer = extract(f, starter, ender, basename)
    classname = numberedName(basename)
    code = "\n".join([
          "package " + "typesome." + classname + ";",
          "// file " + basename + " line " + str(currentLine),
          importses, 
          "public class " + classname + "{",
          "  def check(" + formals + ")  { ",
          "     var checkycheck : " + typer + ";"
          "  }}"])
    writeX10File(classname, code)


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


def doGen(cmd, args, f, line, basename):
    #print("doGen: " + cmd + " on " + "!".join(args));
    global currentLine
    prelude = (
       ["// file " + basename + " line " + str(currentLine) + "\n"] +
       readLines(f, "%~~vis", True, False, basename)
       )
    body = readLines(f, "%~~siv", False, True, basename)
    postlude = readLines(f, "%~~neg", True, False, basename)
    classname = numberedName(basename)
    if len(args) >= 1:
        classname = args[0];
    code = "\n".join(prelude + body + postlude)
    writeX10File(classname, code)

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
def doLongexp(cmd, args, f, line, basename):
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
    # print "longexp: starter = '" + starter + "', ender = '" + ender + "', exp='" + exp + "'"
    code = "\n".join(prelude + [exp] + postlude)
    writeX10File(classname, code)
    


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



def writeX10File(classname, code):
    global files
    if classname == "plz-print-me":
        print code
    fn = gennedFileDir + "/" + classname + ".x10"
    f = open(fn, 'w')
    f.write(code)
    f.flush()
    f.close()
    files.append(fn)
    # print '***' + classname + " to " + fn  + "***\n" + code + "\n\n"

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
    texfiles = [ fn  for fn in os.listdir(texsource) if fn.endswith(".tex") ]
    for tf in texfiles:
        extractExamplesFrom(tf)
    print str(totalTestCases) + " test cases."
        
os.chdir(gennedFileDir);
os.system("rm -r " + gennedFileDir + "/*");
extractExamplesFromAllFiles()
# print ("\n".join(files))
os.system("x10c -STATIC_CALLS *.x10")
#for f in files:
#    print "... " + f + " ... "
#    s = ("/Users/bard/x10/x10-trunk/x10.dist/bin/x10c -STATIC_CALLS " + f + "")
#   # print "  s=" + s
#    os.system(s)
    
