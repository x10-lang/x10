package x10dt.ui.editor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.imp.editor.EditorUtility;
import org.eclipse.imp.language.Language;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.language.ServiceFactory;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.utils.NullMessageHandler;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.DocumentProviderRegistry;
import org.eclipse.ui.texteditor.IDocumentProvider;

public class TestEditor {
  public IParseController parseController;

  public IDocumentProvider docProvider;

  public IDocument document;

  FileEditorInput input;

  Language language;

  public TestEditor(IFile file) throws CoreException {
    input = new FileEditorInput(file);
    docProvider = DocumentProviderRegistry.getDefault().getDocumentProvider(input);
    docProvider.connect(input);

    document = docProvider.getDocument(input);
    language = LanguageRegistry.findLanguage(input, docProvider);

    parseController = ServiceFactory.getInstance().getParseController(language);
    parseController.initialize(file.getProjectRelativePath(), EditorUtility.getSourceProject(input), new NullMessageHandler());
  }

  public Object getAst() {
    if (parseController.getCurrentAst() == null) {
      parseController.parse(document.get(), new NullProgressMonitor());
    }

    return parseController.getCurrentAst();
  }

  public int getOffset(int line, int col) throws BadLocationException {
    return document.getLineInformation(line).getOffset() + col;
  }

  public Point getLocation(int offset) throws BadLocationException {
    int row = document.getLineOfOffset(offset);
    int linePos = document.getLineOffset(row);
    int col = offset - document.getLineOffset(row);
    String line = document.get(linePos, col);

    Pattern p = Pattern.compile("\\t");
    Matcher m = p.matcher(line);
    int numTabs = 0;
    while (m.find()) {
      numTabs++;
    }

    col += 3 * numTabs;
    return new Point(row + 1, col + 1);
  }
}
