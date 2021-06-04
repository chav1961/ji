package chav1961.ji.screen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import chav1961.ji.ResourceRepository;
import chav1961.ji.interfaces.IconKeeper;
import chav1961.ji.interfaces.ThreeStateSwitchKeeper;
import chav1961.ji.interfaces.ValueKeeper;
import chav1961.ji.models.GoodsRecord.TradeOperationType;
import chav1961.ji.interfaces.ThreeStateSwitchKeeper.SwitchState;
import chav1961.purelib.basic.PureLibSettings;

public class GoodsToolbarCard extends JPanelWithImage {
	private static final long 	serialVersionUID = -4675279556401388556L;
	private static final int[]	COL_WIDTHS = {64, 120, 60, 100, 0};
	private static final Icon	SHIP_ICON = new ImageIcon(ResourceRepository.ApplicationImage.SHIP_ICON.getImage());
	private static final String	BLOOD_COLOR_NAME = "red2";
	private static final Color	DEFAULT_BLOOD_COLOR_NAME = Color.RED;
	
	private final TableModel	model;
	private final int			shipAvailable;
    private final JLabel		shipments = new JLabel("", SHIP_ICON, JLabel.LEFT); 
	private int					planned2Sale = 0;
	
	public GoodsToolbarCard(final TableModel model, final int shipAvailable) {
		if (model == null) {
			throw new NullPointerException("Table model can't be null");
		}
		else if (shipAvailable < 0) {
			throw new IllegalArgumentException("Ship available ["+shipAvailable+"] can't be negative");
		}
		else {
			this.model = model;
			this.shipAvailable = shipAvailable;
			
			setLayout(new BorderLayout());
			
	        final JTable 		table = new JTable(model);
	        final JScrollPane	scroll = new JScrollPane(table) {@Override
								        protected JViewport createViewport() {
								        	final JViewport	vp = super.createViewport();
								        	
								        	vp.setOpaque(isOpaque());
								        	return vp;
								        }
	        					};
	
	        table.setDefaultRenderer(IconKeeper.class, new IconKeeperRenderer());
	        table.setDefaultRenderer(ValueKeeper.class, new ValueKeeperRenderer());
	        table.setDefaultRenderer(ThreeStateSwitchKeeper.class, new ThreeStateSwitchKeeperRenderer());
	     
	        table.setDefaultEditor(ValueKeeper.class, new ValueCellEditor());
	        table.setDefaultEditor(ThreeStateSwitchKeeper.class, new ThreeStateSwitchEditor());
	        
	        final TableColumnModel	cm = table.getColumnModel();
	        
	        for (int index = 0; index < cm.getColumnCount(); index++) {
	        	prepareCellHeader(cm, index, COL_WIDTHS[index], Color.GREEN);
	        }
	        
	        table.setRowHeight(32);
	        table.getTableHeader().setPreferredSize(new Dimension(100,32));
	        table.setOpaque(false);
	        
	        table.setFont(ResourceRepository.ApplicationFont.FONT_9.getFont().deriveFont(Font.PLAIN,24));
	        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
	        table.getTableHeader().setOpaque(false);
	        table.getTableHeader().setReorderingAllowed(false);
	        table.getTableHeader().setResizingAllowed(false);
	
	        scroll.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
	        scroll.setOpaque(false);
	        add(scroll, BorderLayout.CENTER);
	
	        final JPanelWithImage	topPanel = new JPanelWithImage(new BorderLayout());
	        final JLabel			caption = new JLabel("Сотонинсъкоё торжище",JLabel.CENTER); 
	        
	        shipments.setForeground(Color.WHITE);
	        shipments.setFont(new Font("Monospace",Font.BOLD,32));
	        caption.setForeground(PureLibSettings.colorByName(BLOOD_COLOR_NAME, DEFAULT_BLOOD_COLOR_NAME));
	        caption.setFont(ResourceRepository.ApplicationFont.FONT_9.getFont().deriveFont(Font.BOLD, 64));
	        
	        topPanel.setPreferredSize(new Dimension(10,SHIP_ICON.getIconHeight() + 10));
	        topPanel.add(shipments, BorderLayout.WEST);
	        topPanel.add(caption, BorderLayout.CENTER);
	        topPanel.setBackgroundImage(ResourceRepository.ApplicationImage.CARDS_BACKGROUND.getImage());
	        
	        add(topPanel, BorderLayout.NORTH);
	        setOpaque(false);
	        setBackgroundImage(ResourceRepository.ApplicationImage.CARDS_BACKGROUND.getImage());
	
	        model.addTableModelListener((e)->recalculateSale());
	        recalculateSale();
		}
	}

	private int getPlanned2Sale() {
		return planned2Sale;
	}

	private void setPlanned2Sale(final int planned) {
		this.planned2Sale = planned;
		
		if (planned2Sale > shipAvailable) {
			shipments.setForeground(Color.RED);
		}
		else {
			shipments.setForeground(Color.GREEN);
		}
		shipments.setText(planned2Sale+"/"+shipAvailable);
	}

	private void recalculateSale() {
		int	total2Sale = 0;
		
		for (int row = 0, maxRow = model.getRowCount(); row < maxRow; row++) {
			final TradeOperationType	tot = ((ThreeStateSwitchKeeper<TradeOperationType>)model.getValueAt(row, 1)).getCargo();
			final int					sale = ((ValueKeeper)model.getValueAt(row, 4)).getValue();
			
			if (tot == TradeOperationType.TRADE_SELL) {
				total2Sale += sale;
			}
		}
		setPlanned2Sale(total2Sale);
	}

	
	private static void prepareCellHeader(final TableColumnModel model, final int columnIndex, final int width, final Color background) {
        final TableColumn	tc = model.getColumn(columnIndex);
		
        if (width > 0) {
            tc.setPreferredWidth(width);
            tc.setMinWidth(width);
            tc.setMaxWidth(width);
        }
        tc.setHeaderRenderer((table, value, isSelected, hasFocus, row, column) -> {
        		final JLabel	label = new JLabel(value.toString(), JLabel.CENTER);

        		label.setFont(ResourceRepository.ApplicationFont.FONT_9.getFont().deriveFont(Font.BOLD, 18));
        		label.setForeground(Color.WHITE);
				return label;
			}
		);
	}
	
	private static class IconKeeperRenderer implements TableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
			final Icon		icon = ((IconKeeper)value).getIcon();
			final JLabel	label = new JLabel(icon); 
			
			label.setOpaque(true);
			label.setBackground(Color.GRAY);
			label.setBorder(null);
			return label;
		}
	}

	private static class ValueKeeperRenderer implements TableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
			final ValueKeeper	vk = (ValueKeeper)value;
			
			switch (vk.getType()) {
				case AMOUNT	:
					final JLabel		amountLabel = new JLabel(""+vk.getValue(),JLabel.CENTER);
					
					amountLabel.setForeground(Color.WHITE);
					amountLabel.setBorder(null);
					return amountLabel;
				case PRICE	:
					final JLabel		priceLabel = new JLabel("Р"+vk.getValue(),JLabel.CENTER);
					
					priceLabel.setForeground(Color.WHITE);
					priceLabel.setBorder(null);
					return priceLabel;
				case SLIDER	:
					final JPanel	panel = new JPanel(new BorderLayout());
					
					panel.setOpaque(false);
					if (!vk.isHidden()) {
						final JLabel		val = new JLabel(" "+vk.getValue()+" ");
						final JSlider		slider = new JSlider(vk.getMinValue(), vk.getMaxValue(), vk.getValue());
						
						slider.setOpaque(false);
						val.setForeground(Color.WHITE);
						panel.add(val, BorderLayout.WEST);
						panel.add(slider, BorderLayout.CENTER);
					}
					panel.setOpaque(false);
					return panel;
				default:
					return null;
			}
		}
	}
	
	private static class ThreeStateSwitchKeeperRenderer implements TableCellRenderer {
		private static final Icon	leftOffIcon = new ImageIcon(ThreeStateSwitchKeeperRenderer.class.getResource("leftMin.png"));
		private static final Icon	leftOnIcon = new ImageIcon(ThreeStateSwitchKeeperRenderer.class.getResource("rightMin.png"));
		private static final Icon	rightOffIcon = new ImageIcon(ThreeStateSwitchKeeperRenderer.class.getResource("leftMax.png"));
		private static final Icon	rightOnIcon = new ImageIcon(ThreeStateSwitchKeeperRenderer.class.getResource("rightMax.png"));
		
		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
			final ThreeStateSwitchKeeper<?>	sk = (ThreeStateSwitchKeeper<?>)value;
			final JPanel	panel = new JPanel(new BorderLayout());
			final  JLabel	leftOff = new JLabel(leftOffIcon);
			final  JLabel	rightOff = new JLabel(leftOnIcon);
			final  JLabel	leftOn = new JLabel(rightOffIcon);
			final  JLabel	rightOn = new JLabel(rightOnIcon);
			
			switch (sk.getState()) {
				case ALL_OFF	:
					panel.add(leftOff,BorderLayout.WEST);
					panel.add(rightOff,BorderLayout.EAST);
					break;
				case LEFT_ON	:
					panel.add(leftOn,BorderLayout.WEST);
					panel.add(rightOff,BorderLayout.EAST);
					break;
				case RIGHT_ON	:
					panel.add(leftOff,BorderLayout.WEST);
					panel.add(rightOn,BorderLayout.EAST);
					break;
				default:
					break;
			}
			panel.setOpaque(false);
			return panel;
		}		
	}
	
	private static class ThreeStateSwitchEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
		private static final long serialVersionUID = 1L;
		
		protected static final String LEFT = "left";
		protected static final String RIGHT = "right";
		
		private SwitchState	currentState;
	
		public ThreeStateSwitchEditor() {}
		
	    public boolean isCellEditable(final EventObject e) {
	    	if (e instanceof MouseEvent) {
    			return true;
	    	}
	    	else {
	    		return false;
	    	}
	    }
		
		public void actionPerformed(final ActionEvent e) {
			switch (e.getActionCommand()) {
				case LEFT	:
					switch (currentState) {
						case ALL_OFF	:
							currentState = SwitchState.LEFT_ON;
							break;
						case LEFT_ON	:
							currentState = SwitchState.ALL_OFF;
							break;
						case RIGHT_ON	:
							currentState = SwitchState.LEFT_ON;
							break;
						default :
					}
					fireEditingStopped();
					break;
				case RIGHT	:
					switch (currentState) {
						case ALL_OFF	:
							currentState = SwitchState.RIGHT_ON;
							break;
						case LEFT_ON	:
							currentState = SwitchState.RIGHT_ON;
							break;
						case RIGHT_ON	:
							currentState = SwitchState.ALL_OFF;
							break;
						default :
					}
					fireEditingStopped();
					break;
				default :
					fireEditingCanceled();
					break;
			}
		}
	
		public Object getCellEditorValue() {
			return currentState;
		}
	
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			final ThreeStateSwitchKeeper<?>	sk = (ThreeStateSwitchKeeper<?>)value;
			final JButton	buttonLeft = new JButton() {@Override public void paint(Graphics g) {}};
			final JButton	buttonRight = new JButton() {@Override public void paint(Graphics g) {}};
			final JButton	buttonRest = new JButton(" ") {@Override public void paint(Graphics g) {}};
			final JPanel	panel = new JPanel(new BorderLayout()); 
					
			buttonLeft.setActionCommand(LEFT);
			buttonLeft.addActionListener(this);
			buttonLeft.setBorderPainted(false);
			buttonLeft.setBackground(null);
			buttonLeft.setOpaque(false);
			
			buttonRight.setActionCommand(RIGHT);
			buttonRight.addActionListener(this);
			buttonRight.setBorderPainted(false);
			buttonRight.setBackground(null);
			buttonRight.setOpaque(false);

			buttonRest.setActionCommand("");
			buttonRest.addActionListener(this);
			buttonRest.setBorderPainted(false);
			buttonRest.setBackground(null);
			buttonRest.setOpaque(false);
			
			panel.setOpaque(false);
			
			switch (sk.getState()) {
				case ALL_OFF	:
					buttonLeft.setText(">");
					buttonRight.setText("<");
					break;
				case LEFT_ON	:
					buttonLeft.setText("<--");
					buttonRight.setText("<");
					break;
				case RIGHT_ON	:
					buttonLeft.setText(">");
					buttonRight.setText("-->");
					break;
			}
			
			panel.add(buttonLeft,BorderLayout.WEST);
			panel.add(buttonRest,BorderLayout.CENTER);
			panel.add(buttonRight,BorderLayout.EAST);
			currentState = sk.getState();
			
			return panel;
		}
	}

	private static class ValueCellEditor extends AbstractCellEditor implements TableCellEditor {
		private static final long serialVersionUID = 1L;
		
		private int	currentValue;
	
		public ValueCellEditor() {}
		
	    public boolean isCellEditable(final EventObject e) {
	    	if (e instanceof MouseEvent) {
    			return ((MouseEvent)e).getX() > 24;
	    	}
	    	else {
	    		return false;
	    	}
	    }
		
		public Object getCellEditorValue() {
			return currentValue;
		}
	
		public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column) {
			final ValueKeeper	vk = (ValueKeeper)value;
			
			switch (vk.getType()) {
				case SLIDER	:
					final JPanel		panel = new JPanel(new BorderLayout());

					panel.setOpaque(false);
					if (!vk.isHidden()) {
						final JLabel		val = new JLabel(" "+vk.getValue()+" ");
						final JSlider		slider = new JSlider(vk.getMinValue(), vk.getMaxValue(), vk.getValue());
						
						slider.setOpaque(false);
						val.setForeground(Color.WHITE);
						slider.addChangeListener((e)->{
							if (slider.getValueIsAdjusting()) {
								val.setText(" "+slider.getValue()+" ");
							}
							else {
								currentValue = slider.getValue();
								fireEditingStopped();
							}
						});
						panel.add(val, BorderLayout.WEST);
						panel.add(slider, BorderLayout.CENTER);
					}
					return panel;
				default		:
					final JButton	button = new JButton("...");
					
					button.addActionListener((e)->fireEditingCanceled());
					return new JButton();
			}
		}
	}

}
