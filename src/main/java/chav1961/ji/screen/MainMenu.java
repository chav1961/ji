package chav1961.ji.screen;

import java.util.Properties;

import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.ui.swing.JMapMenuWithMeta;
import chav1961.purelib.ui.swing.interfaces.JComponentMonitor;

public class MainMenu extends JMapMenuWithMeta {
	private static final long serialVersionUID = 1L;
	
	
	public MainMenu(final ContentNodeMetadata metadata, final JComponentMonitor monitor, final Properties areaDescriptors) throws LocalizationException, SyntaxException {
		super(metadata, monitor, areaDescriptors);
		// TODO Auto-generated constructor stub
	}
	
}
