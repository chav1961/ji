package chav1961.ji.screen;

import java.net.URI;
import java.util.Properties;

import chav1961.ji.ResourceRepository;
import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.ui.swing.JMapMenuWithMeta;
import chav1961.purelib.ui.swing.interfaces.JComponentMonitor;

public class FactoriesToolbarCard extends JMapMenuWithMeta {
	private static final long serialVersionUID = -4819655483829857224L;
	
	public FactoriesToolbarCard(final ContentNodeMetadata metadata, final JComponentMonitor monitor) throws LocalizationException, SyntaxException {
		super(metadata, monitor, Utils.mkProps(JMapMenuWithMeta.SIZE_PROP, "1000x1000",
				"menu.factory.academy.transform", "translate(10,10)",
				"menu.factory.conserv.transform", "translate(300,10)",
				"menu.factory.depo.transform", "translate(600,10)",
				"menu.factory.domennycex.transform", "translate(10,280)",
				"menu.factory.electrostation.transform", "translate(300,280)",
				"menu.factory.fabricamebeli.transform", "translate(600,280)",
				"menu.factory.fabricaodezhdy.transform", "translate(10,570)",
				"menu.factory.lesopilka.transform", "translate(300,570)",
				"menu.factory.mashzavod.transform", "translate(600,570)",
				"menu.factory.nabor.transform", "translate(10,830)",
				"menu.factory.neftezavod.transform", "translate(300,830)",
				"menu.factory.tkatskayafabrica.transform", "translate(600,830)",
				"menu.factory.univercity.transform", "translate(10,500)")
				);
	}
}
