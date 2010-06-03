#!/bin/sh
WALA="../../com.ibm.wala.cast.x10"
Path="src/com/ibm/wala/cast/x10"
X10="../../../x10-compiler/x10.compiler/src/x10/"

if test $1 -eq "1" 
then
        if test ! -f "./patch.wala/SSAAtStmtInstruction.java"
        then
                echo "./patch.wala/SSAAtStmtInstruction.java is missing\n"
                exit
        fi

        if test ! -f "./patch.wala/SSAFinishInstruction.java"
        then
                echo "./patch.wala/SSAFinishStmtInstruction.java is missing\n"
                exit
        fi

        if test ! -f "./patch.wala/X10CAst2IRTranslator.java"
        then
                echo "./patch.wala/X10CAst2IRTranslator.java is missing\n"
                exit
        fi

        if test ! -f "./patch.wala/X10InstructionFactory.java"
        then
                echo "./patch.wala/X10InstructionFactory.java is missing\n"
                exit
        fi

        if test ! -f "./patch.wala/X10Language.java"
        then
                echo "./patch.wala/X10Languag.java is missing\n"
                exit
        fi

        if test -d $WALA
        then
                cp ./patch.wala/SSAAtStmtInstruction.java $WALA/$Path/ssa
                cp ./patch.wala/SSAFinishInstruction.java $WALA/$Path/ssa
                cp ./patch.wala/X10InstructionFactory.java $WALA/$Path/ssa
                echo "com.ibm.wala.cast.x10.ssa has been updated\n"
                cp ./patch.wala/X10CAst2IRTranslator.java $WALA/$Path/translator/polyglot
                echo "com.ibm.wala.cast.x10.translator.polyglot has been updated\n"
                cp ./patch.wala/X10Language.java $WALA/$Path/loader
                echo "com.ibm.wala.cast.x10.loader has been updated\n"
        else
                echo "cannot find com.ibm.wala.cast.x10"
        fi
fi

if test $1 -eq "2" 
then
        
        if test ! -f "./patch.x10/FinishAsyncVisitor.java"
        then
                echo "./patch.x10/FinishAsyncVisitor.java is missing\n"
                exit
        fi

        if test ! -f "./patch.x10/ExtensionInfo.java"
        then
                echo "./patch.x10/EntensionInfo.java is missing\n"
                exit
        fi

        if test ! -f "./patch.x10/Configuration.java"
        then
                echo "./patch.x10/Configuration.java is missing\n"
                exit
        fi
        if test -d $X10
        then
                cp ./patch.x10/FinishAsyncVisitor.java $X10/visit/
                cp ./patch.x10/ExtensionInfo.java $X10/
                cp ./patch.x10/Configuration.java $X10/
                echo "copy is successful"
        else
                echo "cannot find $X10"
        fi
fi
