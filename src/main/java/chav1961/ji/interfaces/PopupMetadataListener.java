package chav1961.ji.interfaces;

import javax.swing.JPopupMenu;

import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;

@FunctionalInterface
public interface PopupMetadataListener {
	JPopupMenu buildMenu(final ContentNodeMetadata metadata) throws ContentException;
}
