/*
 * Created on Sep 26, 2005
 */
package x10.wala.translator;

import java.io.PrintWriter;

import x10.wala.loader.X10SourceLoaderImpl;
import x10.wala.util.X10CAstPrinter;

import com.ibm.wala.cast.java.translator.Java2IRTranslator;
import com.ibm.wala.cast.java.translator.TranslatorToCAst;
import com.ibm.wala.cast.tree.CAstEntity;

public class X10ToIRTranslator extends Java2IRTranslator {
    public X10ToIRTranslator(TranslatorToCAst sourceTranslator,
			     X10SourceLoaderImpl srcLoader) {
	super(sourceTranslator, srcLoader);
    }

    public void translate(Object srcFile, String N) {
      CAstEntity ce= fSourceTranslator.translate(srcFile, N);
      PrintWriter printWriter= new PrintWriter(System.out);

      X10CAstPrinter.printTo(ce, printWriter);
      printWriter.flush();

      new X10CAst2IRTranslator(ce, (X10SourceLoaderImpl) fLoader).translate();
    }
}
