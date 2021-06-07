package chav1961.ji.screen;

import java.net.URI;

import javax.swing.JFrame;

import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.ui.swing.useful.JDialogContainer;

public class HelpPopup extends JDialogContainer<Object, Enum<?>, Object> {
	private static final long serialVersionUID = -9111029320394298909L;

	public HelpPopup(Localizer localizer, JFrame parent, ContentNodeMetadata meta, URI javaResource, final int width, final int height) throws LocalizationException {
		super(localizer, parent, meta.getLabelId(), new HelpScreen(localizer, meta, javaResource, width, height));
	}

	
}
