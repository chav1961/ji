package chav1961.ji;

import java.awt.Dimension;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;

import chav1961.ji.interfaces.IconKeeper;
import chav1961.ji.interfaces.ThreeStateSwitchKeeper;
import chav1961.ji.interfaces.ValueKeeper;
import chav1961.ji.models.GoodsRecord;
import chav1961.ji.models.GoodsRecord.TradeOperationType;
import chav1961.ji.screen.GoodsToolbarCard;
import chav1961.ji.screen.Newspaper;
import chav1961.ji.screen.Newspaper.Quarter;
import chav1961.ji.screen.Newspaper.StandardNoteType;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.ui.swing.SwingUtils;

public class Application extends JFrame {
	private static final long 			serialVersionUID = -3215568116065439459L;
	
	private static final Set<Class<?>>	registeredWindowTypes = new HashSet<>();

	private final Newspaper	newspaper = new Newspaper();
	
	public Application() {
		super("sdsdsdsd");
		
//		newspaper.startNotifications(1800, Quarter.SPRING);
//		newspaper.notify(ResourceRepository.NEWS.getTypedNote(StandardNoteType.TSAR_START, "vassya"));
//		newspaper.notify("Ура","Ура ");
//		newspaper.appendScratchNotifications();
//		newspaper.complete();
//		
//		getContentPane().add(newspaper);
		
		getRootPane().add(new JLabel(new ImageIcon(ResourceRepository.ApplicationImage.CARDS_BACKGROUND.getImage())));
		getContentPane().add(new GoodsToolbarCard(new MyTableModel(),40));
		setMinimumSize(new Dimension(1200,1024));
		setLocationRelativeTo(null);
		SwingUtils.assignExitMethod4MainWindow(this, ()->System.exit(0));
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		try(final Localizer		l = ResourceRepository.APP_LOCALIZER) {
			final Application	app = new Application();
			
			app.setVisible(true);
			
			
			System.err.println("Start...");
		} catch (LocalizationException e) {
			System.err.println("Err...");
		}
		// TODO Auto-generated method stub
	}

	public static void registerWindow(final JComponent type) {
		if (type == null) {
			throw new NullPointerException("Component type can't be null");
		}
		else {
			final Class<?>	clazz = type.getClass();
			
			if (!registeredWindowTypes.contains(clazz)) {
				registeredWindowTypes.add(clazz);
				if (ResourceRepository.TUTORIALS.containsKey(clazz)) {
					
				}
			}
		}
	}
	
	
	
	private static class MyTableModel extends DefaultTableModel {
		private static final long 	serialVersionUID = 1L;
		
		private final GoodsRecord[]	recs; 
		
		private MyTableModel() {
			final GoodsRecord[]	temp = {new GoodsRecord(GoodsRecord.GoogsType.CLOTHES),
											new GoodsRecord(GoodsRecord.GoogsType.FOOD)
										};
			
			for (GoodsRecord item : temp) {
				item.setTotal(100);
				item.setPrice(1000);
			}
			recs = temp;
		}
		
		@Override
		public int getRowCount() {
			if (recs != null) {
				return recs.length;
			}
			else {
				return 0;
			}
		}

		@Override
		public int getColumnCount() {
			return 5;
		}

		@Override
		public String getColumnName(int columnIndex) {
			switch (columnIndex) {
				case 0 	: return "Товаръ"; 
				case 1 	: return "Купипродайка"; 
				case 2 	: return "Цена"; 
				case 3 	: return "Въ наличии"; 
				case 4 	: return "Хочу продать"; 
				default	: return null;
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
				case 0 	: return IconKeeper.class; 
				case 1 	: return ThreeStateSwitchKeeper.class; 
				case 2 	: return ValueKeeper.class; 
				case 3 	: return ValueKeeper.class; 
				case 4 	: return ValueKeeper.class; 
				default	: return null;
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			switch (columnIndex) {
				case 0 	: return false; 
				case 1 	: return true; 
				case 2 	: return false; 
				case 3 	: return false; 
				case 4 	: return true; 
				default	: return false;
			}
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch (columnIndex) {
				case 0 	: return recs[rowIndex].getType(); 
				case 1 	: return recs[rowIndex].getTradeState(); 
				case 2 	: return recs[rowIndex].getPrice(); 
				case 3 	: return recs[rowIndex].getTotal(); 
				case 4 	: return recs[rowIndex].getAvailable2Sell(); 
				default	: return false;
			}
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			switch (columnIndex) {
				case 1 	:
					for (TradeOperationType item : TradeOperationType.values()) {
						if (aValue == item.getState()) {
							recs[rowIndex].setTradeState(item);
							fireTableRowsUpdated(rowIndex, rowIndex);
							break;
						}
					}
					break;
				case 4 	:  
					recs[rowIndex].setAvailable2Sell(((Number)aValue).intValue());
					fireTableCellUpdated(rowIndex, columnIndex);
					break;
				default	: 
					break;
			}
		}
	}
	
}
