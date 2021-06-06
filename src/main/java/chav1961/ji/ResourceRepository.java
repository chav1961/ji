package chav1961.ji;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.synth.SynthLookAndFeel;

import chav1961.ji.repos.NewspaperNotesRepository;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.exceptions.EnvironmentException;
import chav1961.purelib.basic.exceptions.PreparationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.i18n.LocalizerFactory;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.streams.JsonStaxParser;
import chav1961.purelib.ui.swing.useful.JSimpleSplash;

public class ResourceRepository {
	public static final ContentMetadataInterface		ROOT_META;
	public static final Localizer						APP_LOCALIZER;
	public static final Map<ApplicationFont,Font>		FONTS = new HashMap<>();
	public static final Map<ApplicationImage,Image>		IMAGES = new HashMap<>();
	public static final NewspaperNotesRepository		NEWS;
	public static final Map<Class<?>,HelpContent>		TUTORIALS = new HashMap<>();
	
	public enum ApplicationFont {
		FONT_1("OglIeUcs8.ttf"),
		FONT_2("OglSoIeUcs8.ttf"),
		FONT_3("OglSoUcs8.ttf"),
		FONT_4("OglUcs8.ttf"),
		FONT_5("PochCIEUcs.ttf"),
		FONT_6("PochCKUcs.ttf"),
		FONT_7("PochCUcs.ttf"),
		FONT_8("PochIEUcs.ttf"),
		FONT_9("PochKUcs.ttf"),
		FONT_10("PochSoCIEUcs.ttf"),
		FONT_11("PochSoCKUcs.ttf"),
		FONT_12("PochSoCUcs.ttf"),
		FONT_13("PochSoIEUcs.ttf"),
		FONT_14("PochSoKUcs.ttf");
																																
		private final String	fontName;
		
		ApplicationFont(final String fontName) {
			this.fontName = fontName;
		}
		
		public String getFontName() {
			return fontName;
		}
		
		public Font getFont() {
			return FONTS.get(this);
		}
	}
	
	public enum ApplicationImage {
		MAINMENU_BACKGROUND("mainmenu.jpg"),
		NEWSPAPER_BACKGROUND("newspaper.png"),
		CARDS_BACKGROUND("wood.jpg"),
		SHIP_ICON("ship.png");
		
		private final String	fontName;
		
		ApplicationImage(final String fontName) {
			this.fontName = fontName;
		}
		
		public String getImageName() {
			return fontName;
		}
		
		public Image getImage() {
			return IMAGES.get(this);
		}
	}
	
	static {
		int	step = 0;
		
		try(final JSimpleSplash	pi = new JSimpleSplash();
			final LoggerFacade	trans = PureLibSettings.CURRENT_LOGGER.transaction("preparation")) {
			
			try{pi.start("Load Application model...",10);

				ROOT_META = ContentModelFactory.forXmlDescription(ResourceRepository.class.getResourceAsStream("application.xml"));
				pi.processed(++step);
				pi.caption("Load Localizer...");
				APP_LOCALIZER = LocalizerFactory.getLocalizer(ROOT_META.getRoot().getLocalizerAssociated());
				pi.processed(++step);
				
				pi.caption("Load fonts...");
				if (!loadFonts(trans, ApplicationFont.values())) {
					throw new EnvironmentException("Fonts loading failure");
				}
				pi.processed(++step);

				pi.caption("Load newspaper repository...");
				try(final InputStream		is = ResourceRepository.class.getResourceAsStream("/text/newspaper.json");
					final Reader			rdr = new InputStreamReader(is,"utf-8");
					final JsonStaxParser	parser = new JsonStaxParser(rdr)) {
					
					NEWS = new NewspaperNotesRepository(parser); 
				} catch (IOException | SyntaxException e) {
					e.printStackTrace();
					throw new EnvironmentException("Newspaper loading failure",e);
				}
				pi.processed(++step);
				
				pi.caption("Load images...");
				if (!loadImages(trans, ApplicationImage.values())) {
					throw new EnvironmentException("Images loading failure");
				}
				pi.processed(++step);
				
				for(int index = step; index < 10; index++) {
					pi.caption("test: "+index);
					pi.processed(index);
					Thread.sleep(100);
				}
				trans.rollback();
			} finally {
				pi.end();
			}
			
		} catch (EnvironmentException | InterruptedException /*| ParseException | IOException | UnsupportedLookAndFeelException*/ e) {
			e.printStackTrace();
			throw new PreparationException(e.getLocalizedMessage(),e);
		}
	}
	
	private static boolean loadFonts(final LoggerFacade logger, final ApplicationFont[] fontUris) {
		final GraphicsEnvironment 	env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		boolean						success = true;
		
		for (ApplicationFont item : fontUris) {
			final URI				uri = URI.create("root://"+ResourceRepository.class.getCanonicalName()+"/fonts/"+item.getFontName());
			
			try(final InputStream	is = uri.toURL().openStream()) {
            	final Font			font = Font.createFont(Font.TRUETYPE_FONT, is);
				
            	env.registerFont(font);
            	FONTS.put(item, font);
			} catch (MalformedURLException e) {
				logger.message(Severity.warning, "Error loading font ["+item+"] : illegal URI ("+e.getLocalizedMessage()+")");
				success = false;
			} catch (IOException e) {
				logger.message(Severity.warning, "Error loading font ["+item+"] : I/O error ("+e.getLocalizedMessage()+")");
				success = false;
			} catch (FontFormatException e) {
				logger.message(Severity.warning, "Error loading font ["+item+"] : illegal font format ("+e.getLocalizedMessage()+")");
				success = false;
			}
		}
		return success;
	}

	private static boolean loadImages(final LoggerFacade logger, final ApplicationImage[] imageUris) {
		boolean						success = true;
		
		for (ApplicationImage item : imageUris) {
			final URI				uri = URI.create("root://"+ResourceRepository.class.getCanonicalName()+"/images/"+item.getImageName());
			
			try(final InputStream	is = uri.toURL().openStream()) {
            	final Image			image = ImageIO.read(is);
				
            	IMAGES.put(item, image);
			} catch (MalformedURLException e) {
				logger.message(Severity.warning, "Error loading font ["+item+"] : illegal URI ("+e.getLocalizedMessage()+")");
				success = false;
			} catch (IOException e) {
				logger.message(Severity.warning, "Error loading font ["+item+"] : I/O error ("+e.getLocalizedMessage()+")");
				success = false;
			}
		}
		return success;
	}
	
	public static class HelpContent {
		
	}
}
