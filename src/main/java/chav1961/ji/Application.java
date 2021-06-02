package chav1961.ji;

import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JFrame;

import chav1961.ji.screen.Newspaper;
import chav1961.ji.screen.Newspaper.Quarter;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.interfaces.Localizer;

public class Application extends JFrame {
	private static final long serialVersionUID = -3215568116065439459L;

	private final Newspaper	newspaper = new Newspaper();
	
	public Application() {
		super("sdsdsdsd");
		
		newspaper.startNotifications(1800, Quarter.AUTUMN);
		newspaper.notify("Ура","Ура ");
		newspaper.complete();
		
		
		getContentPane().add(newspaper);
		setMinimumSize(new Dimension(1200,1024));
		setLocationRelativeTo(null);
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

}
