package x10dt.ui.parser;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.parser.IMessageHandler;

import x10.parser.X10SemanticRules.MessageHandler;
import x10dt.core.builder.BuildPathUtils;

public class MessageHandlerAdapterFilter implements lpg.runtime.IMessageHandler {
	private final IMessageHandler fIMPHandler;
	private final IPath fFilePath;
	private final ISourceProject fProject;
	
	public MessageHandlerAdapterFilter(IMessageHandler impHandler,
			IPath filePath, ISourceProject project) {
		fIMPHandler = impHandler;
		fFilePath = filePath;
		fProject = project;
	}

	public void handleMessage(int errorCode, int[] msgLocation,
			int[] errorLocation, String filename, String[] errorInfo) {
		if (fIMPHandler == null) // there might be no IMP msg handler if we're
									// parsing on behalf of the structure
									// compare view
			return;
		if (BuildPathUtils.isExcluded(fFilePath, fProject, LanguageRegistry.findLanguage("X10"))) //If this file is excluded from the build path, then ignore messages.
			return;
		int startOffset = msgLocation[lpg.runtime.IMessageHandler.OFFSET_INDEX];
		int length = msgLocation[lpg.runtime.IMessageHandler.LENGTH_INDEX];
		int startLine = msgLocation[lpg.runtime.IMessageHandler.START_LINE_INDEX];
		int endLine = msgLocation[lpg.runtime.IMessageHandler.END_LINE_INDEX];
		int startCol = msgLocation[lpg.runtime.IMessageHandler.START_COLUMN_INDEX];
		int endCol = msgLocation[lpg.runtime.IMessageHandler.END_COLUMN_INDEX];

		String message = MessageHandler.getErrorMessageFor(errorCode,
				errorInfo);

		if (fFilePath.equals(new Path(filename)))
			fIMPHandler.handleSimpleMessage(message, startOffset, startOffset
					+ length - 1, startCol, endCol, startLine, endLine);
	}
}
