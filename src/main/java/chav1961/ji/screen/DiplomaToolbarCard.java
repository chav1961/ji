package chav1961.ji.screen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ToolTipManager;

import chav1961.ji.ResourceRepository;
import chav1961.ji.interfaces.ClickMetadataListener;
import chav1961.ji.interfaces.Country;
import chav1961.ji.interfaces.PopupMetadataListener;
import chav1961.ji.utils.Utils;
import chav1961.ji.world.WorldGenerator;
import chav1961.ji.world.WorldGenerator.CountryDescriptor;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.concurrent.LightWeightListenerList;
import chav1961.purelib.concurrent.LightWeightListenerList.LightWeightListenerCallback;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.ui.swing.SwingUtils;

public class DiplomaToolbarCard extends JPanelWithImage {
	private static final long 		serialVersionUID = 2221326122681380614L;

	private static final String		FIRST_CAPTION = "DiplomaToolbarCard.firstCaption";
	private static final String		ORDINAL_CAPTION = "DiplomaToolbarCard.ordinalCaption";
	private static final String		SELECT_PLAYER_TOOLTIP = "DiplomaToolbarCard.selectPlayer.tt";
	private static final String		INFORMATION_TOOLTIP = "DiplomaToolbarCard.information.tt";
	private static final String		WAR_AND_PEACE_TOOLTIP = "DiplomaToolbarCard.warAndPeace.tt";
	private static final String		FRIENDSHIP_TOOLTIP = "DiplomaToolbarCard.friendship.tt";
	private static final String		HYPERLINK1_TEXT = "DiplomaToolbarCard.hyperlink1";
	private static final String		HYPERLINK2_TEXT = "DiplomaToolbarCard.hyperlink2";
	private static final String		HYPERLINK3_TEXT = "DiplomaToolbarCard.hyperlink3";

	public enum DiplomaState {
		SELECT_PLAYER(FIRST_CAPTION, SELECT_PLAYER_TOOLTIP, false),
		INFOFMATION(ORDINAL_CAPTION, INFORMATION_TOOLTIP, true),
		WAR_ANG_PEACE(ORDINAL_CAPTION, WAR_AND_PEACE_TOOLTIP, true),
		FRIENDSHIP(ORDINAL_CAPTION, FRIENDSHIP_TOOLTIP, true);
		
		private final String 	caption;
		private final String 	tooltip;
		private final boolean	rightPanelOn;
		
		DiplomaState(final String caption, final String tooltip, final boolean rightPanelOn) {
			this.caption = caption;
			this.tooltip = tooltip;
			this.rightPanelOn = rightPanelOn;
		}
		
		public String getWindowCaption() {
			return caption;
		}

		public String getTooltip() {
			return tooltip;
		}
		
		public boolean isRightPanelVisible() {
			return rightPanelOn;
		}
	}

	private final Localizer			localizer;
	private final LoggerFacade		logger;
	private final LightWeightListenerList<ClickMetadataListener>	clickListeners = new LightWeightListenerList<>(ClickMetadataListener.class);
	private final LightWeightListenerList<PopupMetadataListener>	popupListeners = new LightWeightListenerList<>(PopupMetadataListener.class);
	private final JPanel			rightPanel = new JPanel(new BorderLayout());
	private final DrawingMap		drawing;
	private final HyperlinkButton	hyperlink1 = new HyperlinkButton(32, "Тайныя сведения", (e)->setState(DiplomaState.INFOFMATION));
	private final HyperlinkButton	hyperlink2 = new HyperlinkButton(32, "Война и миръ", (e)->setState(DiplomaState.WAR_ANG_PEACE));
	private final HyperlinkButton	hyperlink3 = new HyperlinkButton(32, "Дружба", (e)->setState(DiplomaState.FRIENDSHIP));
	private final JGotoURIButton	gotoURI;
	private final LeaveButton		leave;
    private final JLabel			caption = new JLabel("",JLabel.CENTER); 
	
	private DiplomaState			currentState = DiplomaState.INFOFMATION;
	private WorldGenerator			wg = null;
	
	public DiplomaToolbarCard(final Localizer localizer, final LoggerFacade logger) throws LocalizationException, NullPointerException, IOException {
		if (localizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else if (logger == null) {
			throw new NullPointerException("Logger facade can't be null");
		}
		else {
			this.localizer = localizer;
			this.logger = logger;
			this.leave = new LeaveButton((e)->processAction(null, "exit"));
			this.gotoURI = new JGotoURIButton(localizer, Utils.buildContentReferenceByClass(getClass()));
	    	this.drawing = new DrawingMap(localizer, (desc)->processAction(desc, desc.getCountry().name()), (desc)->processPopup(desc));
	    	
			final JPanel	topRightPanel = new JPanel(new GridLayout(3,1));
			final JPanel	topPanel = new JPanel(new BorderLayout()), leftButtonPanel = new JPanel(), rightButtonPanel = new JPanel();
	        
	        caption.setForeground(PureLibSettings.colorByName(ResourceRepository.BLOOD_COLOR_NAME, ResourceRepository.DEFAULT_BLOOD_COLOR_NAME));
	        caption.setFont(ResourceRepository.ApplicationFont.FONT_9.getFont().deriveFont(Font.BOLD, 64));
	        caption.setPreferredSize(new Dimension(10,100));

	        gotoURI.setPreferredSize(new Dimension(gotoURI.getButtonSize(), gotoURI.getButtonSize()));
	        rightButtonPanel.setOpaque(false);
	        rightButtonPanel.add(gotoURI);

	        leave.setPreferredSize(new Dimension(leave.getButtonSize(), leave.getButtonSize()));
	        leftButtonPanel.setOpaque(false);
	        leftButtonPanel.add(leave);
	        
	        topPanel.setOpaque(false);
	        topPanel.add(leftButtonPanel, BorderLayout.WEST);
	        topPanel.add(caption, BorderLayout.CENTER);
	        topPanel.add(rightButtonPanel, BorderLayout.EAST);
			
	        topRightPanel.setOpaque(false);
	        topRightPanel.add(hyperlink1);
	        topRightPanel.add(hyperlink2);
	        topRightPanel.add(hyperlink3);
	        topRightPanel.setPreferredSize(new Dimension(250,150));
	
			rightPanel.setOpaque(false);
			rightPanel.add(topRightPanel, BorderLayout.NORTH);
			
			setLayout(new BorderLayout());
			add(drawing, BorderLayout.CENTER);
			add(rightPanel, BorderLayout.EAST);
			add(topPanel, BorderLayout.NORTH);
			
			setOpaque(false);
	        setBackgroundImage(ResourceRepository.ApplicationImage.NEWSPAPER_BACKGROUND.getImage());
			SwingUtils.assignActionKey(this, SwingUtils.KS_EXIT, (e)->leave.doClick(), "exit");
			SwingUtils.assignActionKey(this, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, SwingUtils.KS_EXIT, (e)->leave.doClick(), "exit");
	        
	        fillLocalizedStrings();
		}
	}
	
	public void setWorld(final WorldGenerator wg) {
		this.wg = wg;
		setState(DiplomaState.INFOFMATION);
		drawing.setWorld(wg);
	}
	
	public DiplomaState getState() {
		return currentState;
	}
	
	public void setState(final DiplomaState state) {
		if (state == null) {
			throw new NullPointerException("State to set can't be null");
		}
		else {
			currentState = state;
			
			switch (currentState) {
				case FRIENDSHIP		:
					hyperlink1.setEnabled(true);
					hyperlink2.setEnabled(true);
					hyperlink3.setEnabled(false);
					break;
				case INFOFMATION	:
					hyperlink1.setEnabled(false);
					hyperlink2.setEnabled(true);
					hyperlink3.setEnabled(true);
					break;
				case SELECT_PLAYER	:
					hyperlink1.setEnabled(false);
					hyperlink2.setEnabled(false);
					hyperlink3.setEnabled(false);
					break;
				case WAR_ANG_PEACE	:
					hyperlink1.setEnabled(true);
					hyperlink2.setEnabled(false);
					hyperlink3.setEnabled(true);
					break;
				default:
					throw new UnsupportedOperationException("State type ["+currentState+"] is not supported yet");
			}
			rightPanel.setVisible(currentState.isRightPanelVisible());
			drawing.setState(currentState);
			
			try{
				fillLocalizedStrings();
				logger.message(Severity.info, localizer.getValue(currentState.getTooltip()));
			} catch (LocalizationException e) {
				logger.message(Severity.error, e, e.getLocalizedMessage()); 
			}
		}
	}

	public void addClickMetadataListener(final ClickMetadataListener listener) {
		if (listener == null) {
			throw new NullPointerException("Listener to add can't be null!");
		}
		else {
			clickListeners.addListener(listener);
		}
	}

	public void removeClickMetadataListener(final ClickMetadataListener listener) {
		if (listener == null) {
			throw new NullPointerException("Listener to add can't be null!");
		}
		else {
			clickListeners.removeListener(listener);
		}
	}
	
	public void addPopupMetadataListener(final PopupMetadataListener listener) {
		if (listener == null) {
			throw new NullPointerException("Listener to add can't be null!");
		}
		else {
			popupListeners.addListener(listener);
		}
	}
	
	public void removePopupMetadataListener(final PopupMetadataListener listener) {
		if (listener == null) {
			throw new NullPointerException("Listener to add can't be null!");
		}
		else {
			popupListeners.addListener(listener);
		}
	}
	
	private void processAction(final CountryDescriptor desc, final String action) {
		clickListeners.fireEvent((c)->{
			try {
				c.process(desc == null ? null : desc.getNodeMetadata(), action);
			} catch (ContentException e) {
				logger.message(Severity.error, e, e.getLocalizedMessage());
			}
		});
	}
	
	private JPopupMenu processPopup(final CountryDescriptor desc) {
		final List<JPopupMenu>	result = new ArrayList<>();
		
		popupListeners.fireEvent((c)->{
			try {
				final JPopupMenu	menu = c.buildMenu(desc.getNodeMetadata());
				
				if (menu != null) {
					result.add(menu);
				}
			} catch (ContentException e) {
				logger.message(Severity.error, e, e.getLocalizedMessage());
			}
		});
		return result.isEmpty() ? null : result.get(0);
	}
	
	private void fillLocalizedStrings() throws LocalizationException {
		caption.setText(localizer.getValue(currentState.getWindowCaption()));
		hyperlink1.setText(localizer.getValue(HYPERLINK1_TEXT));
		hyperlink2.setText(localizer.getValue(HYPERLINK2_TEXT));
		hyperlink3.setText(localizer.getValue(HYPERLINK3_TEXT));
	}
	
	private static class DrawingMap extends JComponent {
		private static final long serialVersionUID = 1L;
		
		private final Localizer		localizer;
		private final Consumer<CountryDescriptor>	call;
		private final Function<CountryDescriptor, JPopupMenu>	popup;
		private DiplomaState		state = DiplomaState.INFOFMATION;
		private WorldGenerator		wg = null;
		private Country				lastSelected = null;

		private DrawingMap(final Localizer localizer, final Consumer<CountryDescriptor> call, final Function<CountryDescriptor, JPopupMenu> popup) {
			this.localizer = localizer;
			this.call = call;
			this.popup = popup;
			
			setOpaque(false);
			setFocusable(true);
			addMouseListener(new MouseListener() {
				@Override public void mouseReleased(MouseEvent e) {}
				@Override public void mousePressed(MouseEvent e) {}
				@Override public void mouseExited(MouseEvent e) {}
				@Override public void mouseEntered(MouseEvent e) {}
				
				@Override
				public void mouseClicked(MouseEvent e) {
					final CountryDescriptor	desc = getDescriptorByPoint(fromWindowToCard(e.getPoint()));
					
					if (e.getButton() == MouseEvent.BUTTON1) {
						setSelected(desc);
						if (desc != null && e.getClickCount() >= 2) {
							call.accept(desc);
						}
					}
					else if (e.getButton() == MouseEvent.BUTTON3) {
						if (desc != null) {
							final JPopupMenu	menu = popup.apply(desc);
							
							if (menu != null) {
								menu.show(DrawingMap.this, e.getX(), e.getY());
							}
						}
					}
				}
			});
			addMouseMotionListener(new MouseMotionListener() {
				@Override public void mouseDragged(MouseEvent e) {}
				
				@Override
				public void mouseMoved(MouseEvent e) {
					final CountryDescriptor	desc = getDescriptorByPoint(fromWindowToCard(e.getPoint()));

					if (desc != null) {
						setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					}
					else {
						setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
				}
			});
			ToolTipManager.sharedInstance().registerComponent(this);
		}

		private void setWorld(final WorldGenerator wg) {
			this.wg = wg;
			lastSelected = null;
			
			repaint();
		}
		
		private void setState(final DiplomaState state) {
			this.state = state;
			repaint();
		}
		
		private Point2D fromWindowToCard(final Point2D point) {
			try{return getCardTransform().inverseTransform(point, null);
			} catch (NoninvertibleTransformException e) {
				return new Point(0,0);
			}
		}
		
		@Override
		public String getToolTipText(final MouseEvent event) {
			final CountryDescriptor	desc = getDescriptorByPoint(fromWindowToCard(event.getPoint()));
			
			if (desc != null && desc.getNodeMetadata().getTooltipId() != null) {
				try{return localizer.getValue(desc.getNodeMetadata().getTooltipId());
				} catch (LocalizationException e) {
					return desc.getNodeMetadata().getName();
				}
			}
			else {
				return super.getToolTipText(event);
			}
		}
		
		private CountryDescriptor getDescriptorByPoint(final Point2D point) {
			for (Country item : Country.values()) {
				final CountryDescriptor	cd = wg.getCountryDescriptor(item);
				
				if (cd.getShape().contains(point)) {
					return cd;
				}
			}
			return null;
		}
		
		private AffineTransform getCardTransform() {
			final Dimension			windowSize = getSize();
			final Dimension			cardSize = wg.getCardSize();
			final AffineTransform	at = new AffineTransform();
			
			at.scale(windowSize.getWidth() / cardSize.getWidth(), windowSize.getHeight() / cardSize.getHeight());
			return at;
		}

		private void setSelected(final CountryDescriptor desc) {
			lastSelected = desc == null ? null : desc.getCountry();
			repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
			if (wg != null) {
				final Graphics2D		g2d = (Graphics2D)g;
				final AffineTransform	oldAt = g2d.getTransform();
				final Color				oldColor = g2d.getColor();
				final AffineTransform	newAt = new AffineTransform(oldAt);
				
				newAt.concatenate(getCardTransform());
				g2d.setTransform(newAt);
				
				for (Country item : Country.values()) {
					final CountryDescriptor	desc = wg.getCountryDescriptor(item);
					final Shape				shape = desc.getShape();

					switch (state) {
						case FRIENDSHIP		:
							g2d.setColor(desc.getFriendshipDepth().getColor());
							break;
						case INFOFMATION : case SELECT_PLAYER :
							g2d.setColor(item.getColor());
							break;
						case WAR_ANG_PEACE	:
							g2d.setColor(desc.getRelation().getColor());
							break;
						default :
							throw new UnsupportedOperationException("State type ["+state+"] is nt supported yet");
					}
					g2d.fill(shape);
					g2d.setColor(item == lastSelected ? Color.BLUE : Color.BLACK);
					g2d.draw(shape);
				}
				g2d.setColor(oldColor);
				g2d.setTransform(oldAt);
			}
			else {
				super.paintComponent(g);
			}
			
			
			
			// TODO Auto-generated method stub
//			final BufferedImage		bi = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
//			final Graphics2D		g2d = (Graphics2D)bi.getGraphics();
	//
//			g2d.setColor(new Color(224,255,255));
//			g2d.fillRect(0, 0, imageWidth, imageHeight);
//			
//			for (int x = 0; x < width; x++) {
//				for (int y = 0; y < height; y++) {
//					
//					if (wg.mesh.cellExists(x, y) && !wg.mesh.cellFree(x, y)) {
////						final AffineTransform	at = new AffineTransform();
//						final Country			c = wg.mesh.getCell(x, y);
//						final Shape				shape = buildShape(width, height, 1, 1, CELL_SIZE, new int[] {x, y});
	//
////						at.translate((x  + (y % 2 != 0 ? 0 : HALF) + 1) * cellSize * SQRT_3, 1.5 * (y + 1) * cellSize);
////						at.scale(cellSize, cellSize);
//						g2d.setColor(new Color(c.getColor().getRGB() & (c.getType() == CountryType.METROPOLY ? Color.WHITE : Color.GRAY).getRGB()));
//						g2d.fill(shape);
////						g2d.fill(CELL.createTransformedShape(at));
//					}
//				}
//			}
//			g2d.setColor(Color.BLACK);
//			for (Entry<Country, Shape> item : wg.countryShapes.entrySet()) {
//				g2d.draw(item.getValue());
//			}
			
			
			
		}
	}
}
