package chav1961.ji.screen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.net.MalformedURLException;
import java.net.URI;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SpringLayout;
import javax.swing.border.LineBorder;

import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.ui.swing.useful.JCreoleHelpWindow;

class HelpScreen extends JSplitPane {
	private static final long 			serialVersionUID = 4057265063657628793L;
	
	private final Localizer				localizer;
	private final ContentNodeMetadata	meta;
	private final JLabel				caption1 = new JLabel("", JLabel.CENTER);
	private final JLabel				caption2 = new JLabel("", JLabel.CENTER);
	private final JLabel				image = new JLabel();
	private final JCreoleHelpWindow		content;

	HelpScreen(final Localizer localizer, final ContentNodeMetadata meta, final URI javaId, final int width, final int height) throws LocalizationException {
		super(JSplitPane.VERTICAL_SPLIT);
		if (localizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else if (meta == null) {
			throw new NullPointerException("Metadata can't be null");
		}
		else if (javaId == null) {
			throw new NullPointerException("Java id can't be null");
		}
		else if (meta.getHelpId() == null) {
			throw new IllegalArgumentException("Metadata must contain help Id");
		}
		else if (meta.getIcon() == null) {
			throw new IllegalArgumentException("Metadata must contain icon attribute");
		}
		else {
			this.localizer = localizer;
			this.meta = meta;
			this.content = new JCreoleHelpWindow(localizer, meta.getHelpId()); 
			
			final JPanel		topPanel = new JPanel(new FlowLayout()), bottomPanel = new JPanel(), rightPanel = new JPanel(new GridLayout(2,1)); 
			final JScrollPane	pane = new JScrollPane(content);

			try{final Icon	icon = new ImageIcon(meta.getIcon().toURL());
				final int	halfWidth = Math.max(0,width - icon.getIconWidth());
				final int	halfHeight = Math.max(0, height - icon.getIconHeight());
				final int	halfHeight2 = Math.max(0, icon.getIconHeight() / 2);
			
				image.setIcon(icon);
				image.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
//				rightPanel.setPreferredSize(new Dimension(halfWidth, halfHeight));
				topPanel.setPreferredSize(new Dimension(width, halfHeight));
				bottomPanel.setPreferredSize(new Dimension(width, halfHeight));
				setPreferredSize(new Dimension(width, height));
				setMaximumSize(new Dimension(width, height));
				setDividerLocation(icon.getIconHeight());
			} catch (MalformedURLException exc) {
				throw new LocalizationException(exc.getLocalizedMessage(),exc);
			}

			image.setBorder(new LineBorder(Color.RED));
			caption1.setBorder(new LineBorder(Color.GREEN));
			caption2.setBorder(new LineBorder(Color.BLUE));
			pane.setBorder(new LineBorder(Color.MAGENTA));
			content.setBorder(new LineBorder(Color.YELLOW));

			rightPanel.add(caption1);
			rightPanel.add(caption2);
			topPanel.add(image);
			topPanel.add(rightPanel);
			bottomPanel.add(pane);
			
			setLeftComponent(topPanel);
			setRightComponent(bottomPanel);
			
			fillLocalizedStrings();
		}
	}
	
	private void fillLocalizedStrings() throws LocalizationException {
		caption1.setText(localizer.getValue(meta.getLabelId()).trim());
		
		if (meta.getTooltipId() != null) {
			caption2.setText(localizer.getValue(meta.getTooltipId()).trim());
		}
	}
}
