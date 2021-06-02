package chav1961.ji.screen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;

import chav1961.ji.ResourceRepository;
import chav1961.ji.ResourceRepository.ApplicationFont;
import chav1961.ji.ResourceRepository.ApplicationImage;
import chav1961.ji.repos.NewspaperNotesRepository.NewspaperNote;

public class Newspaper extends JComponent {
	private static final long 			serialVersionUID = 1L;
	private static final String			ORD1_NAME = "", ORD2_NAME = "", H_BAR_NAME = "";
	private static final String			NOTE_REPO_NAME = "";
	private static final int			MAX_NOTES = 10;
	
	private static final float			PAGE_WIDTH = 100;
	private static final float			PAGE_LEFT_LINE = 3;
	private static final float			PAGE_RIGHT_LINE = PAGE_WIDTH - PAGE_LEFT_LINE;
	private static final float			PAGE_TOP_LINE = 10;
	private static final float			PAGE_UPPER_LINE = PAGE_TOP_LINE + 1;
	private static final float			PAGE_LOWER_LINE = 96;
	private static final float			PAGE_LEFT_X_GAP = 1;
	private static final float			PAGE_RIGHT_X_GAP = 1;
	private static final float			PAGE_TOP_Y_GAP = 1;
	private static final float			PAGE_BOTTOM_Y_GAP = 3;
	private static final float			PAGE_INNER_X_GAP = 1;
	private static final float			PAGE_INNER_Y_GAP = 1;
	private static final int			PAGE_COLUMNS = 3;
	private static final Color 			PAGE_COLOR = new Color(132,78,24);
	private static final float			MESH_WIDTH = 0.25f;
	private static final Stroke			MESH_STROKE = new BasicStroke(MESH_WIDTH);
	private static final Font			CAPTION_FONT, SUBCAPTION_FONT, SEASON_FONT, YEAR_FONT;
	
	
	static {
		AffineTransform	at;
		
		at = new AffineTransform();
		at.scale(8,6);
		CAPTION_FONT = ResourceRepository.ApplicationFont.FONT_9.getFont().deriveFont(Font.PLAIN,at);

		at = new AffineTransform();
		at.scale(4,3);
		SUBCAPTION_FONT = ResourceRepository.ApplicationFont.FONT_9.getFont().deriveFont(Font.PLAIN,at);
		
		at = new AffineTransform();
		at.scale(3,2);
		SEASON_FONT = ResourceRepository.ApplicationFont.FONT_9.getFont().deriveFont(Font.PLAIN,at); 

		at = new AffineTransform();
		at.scale(2,2);
		YEAR_FONT = new Font("Courier",Font.PLAIN,1).deriveFont(Font.PLAIN,at); 
	}

	public enum Quarter {
		WINTER("Зима"), 
		SPRING("Весна"), 
		SUMMER("Лето"), 
		AUTUMN("Осень");
		
		private final String	seasonName;
		
		private Quarter(final String seasonName) {
			this.seasonName = seasonName;
		}
		
		public String getSeasonName() {
			return seasonName;
		}
	}
	
	public enum NoteType {
		All
	}
	
	private final List<NewspaperNote>	notes = new ArrayList<>();
	private final List<AttributedCharacterIterator>	toDraw = new ArrayList<>();
	private int					year = 1800;
	private Quarter				quarter = Quarter.SPRING;
	
	public Newspaper() {
		
	}
	
	public int currentYear() {
		return year;
	}
	
	public Quarter currentQuarter() {
		return quarter;
	}
	
	public void startNotifications(final int year, final Quarter quarter) {
		if (year < ResourceRepository.NEWS.getMinYear() || year > ResourceRepository.NEWS.getMaxYear()) {
			throw new IllegalArgumentException("Year ["+year+"] must be in range "+ResourceRepository.NEWS.getMinYear()+".."+ResourceRepository.NEWS.getMaxYear()); 
		}
		else if (quarter == null) {
			throw new NullPointerException("Quarter can't be null"); 
		}
		else {
			notes.clear();
			this.year = year;
			this.quarter = quarter;
		}
	}

	public void notify(final String caption, final String body) {
		if (caption == null || caption.isEmpty()) {
			throw new IllegalArgumentException("Note caption can't be null or empty"); 
		}
		else if (body == null || body.isEmpty()) {
			throw new IllegalArgumentException("Note body can't be null or empty"); 
		}
		else {
			notify(new NewspaperNote(caption, body));
		}
	}

	public void notify(final NewspaperNote note) {
		if (note == null) {
			throw new NullPointerException("Note can't be null"); 
		}
		else {
			notes.add(note);
		}
	}
	
	public void appendScratchNotifications() {
		while (notes.size() < MAX_NOTES) {
			final int	index = (int) (ResourceRepository.NEWS.getSize(currentYear(), currentQuarter()) * Math.random()); 
			
			notes.add(ResourceRepository.NEWS.getNote(currentYear(), currentQuarter(), index));
		}
	}

	public void complete() {
		for (NewspaperNote note : notes) {
			toDraw.add(new NoteItem(note.getCaption(),true));
			toDraw.add(new NoteItem(note.getBody(),false));
		}
		repaint();
	}
	
	@Override
	protected void paintComponent(final Graphics g) {
		final Graphics2D		g2d = (Graphics2D)g;
		final AffineTransform	oldAt = g2d.getTransform();
		final AffineTransform	newAt = pickCoordinates(g2d);

		g2d.setTransform(newAt);
		paintBackground(g2d);
		paintMeshAndCaption(g2d);
		
		if (toDraw != null) {
			paintColumn(g2d, toDraw);
		}
		g2d.setTransform(oldAt);
	}

	private float getYSize() {
		return 1.0f * getHeight() / getWidth();
	}
	
	private AffineTransform pickCoordinates(final Graphics2D g2d) {
		final AffineTransform	at = new AffineTransform(g2d.getTransform());
		final float				kx = 1.0f * getWidth() / PAGE_WIDTH;
		final float				ky = getYSize();

		at.scale(kx, kx);
		return at;
	}

	private void paintBackground(final Graphics2D g2d) {
		final AffineTransform	at = new AffineTransform();
		final Image				image = ApplicationImage.NEWSPAPER_BACKGROUND.getImage();
		final int				w = image.getWidth(null), h = image.getHeight(null);
		final float				kx = PAGE_WIDTH / w, ky = PAGE_WIDTH * getYSize() / h;
		
		at.scale(kx, ky);
		g2d.drawImage(image, at, null);
	}

	private void paintMeshAndCaption(final Graphics2D g2d) {
		final Color			oldColor = g2d.getColor();
		final Stroke		oldStroke = g2d.getStroke();
		
		final String		caption = ResourceRepository.NEWS.getCaption();
		final String		subCaption = ResourceRepository.NEWS.getSubCaption();
		final String		season = currentQuarter().getSeasonName();
		final String		year = ""+currentYear();
		final GeneralPath	path = new GeneralPath();
		final TextLayout	captionLayout = new TextLayout(caption, CAPTION_FONT, g2d.getFontRenderContext());
		final TextLayout	subcaptionLayout = new TextLayout(subCaption, SUBCAPTION_FONT, g2d.getFontRenderContext());
		final TextLayout	seasonLayout = new TextLayout(season, SEASON_FONT, g2d.getFontRenderContext());
		final TextLayout	yearLayout = new TextLayout(year, YEAR_FONT, g2d.getFontRenderContext());
		final float			columnWidth = PAGE_WIDTH / PAGE_COLUMNS;
		
		path.moveTo(PAGE_LEFT_LINE, PAGE_TOP_LINE);		
		path.lineTo(PAGE_RIGHT_LINE, PAGE_TOP_LINE);
		for (int index = 1; index < PAGE_COLUMNS; index++) {
			path.moveTo(index * columnWidth, PAGE_UPPER_LINE);
			path.lineTo(index * columnWidth, PAGE_LOWER_LINE * getYSize());
		}
		
		g2d.setColor(PAGE_COLOR);
		g2d.setStroke(MESH_STROKE);
		g2d.draw(path);
		
		final Rectangle2D	rectCaption = captionLayout.getBounds(), rectSubcaption = subcaptionLayout.getBounds(), rectSeason = seasonLayout.getBounds(), rectYear = yearLayout.getBounds();
		float	rightText = (float) (PAGE_RIGHT_LINE - rectYear.getWidth());

		yearLayout.draw(g2d, rightText, PAGE_TOP_LINE - PAGE_INNER_Y_GAP);
		rightText -= PAGE_INNER_X_GAP + rectSeason.getWidth();
		seasonLayout.draw(g2d, rightText, PAGE_TOP_LINE - PAGE_INNER_Y_GAP);
		rightText = (float) ((PAGE_LEFT_LINE + rightText - rectSubcaption.getWidth()) / 2);
		subcaptionLayout.draw(g2d, rightText , PAGE_TOP_LINE - PAGE_INNER_Y_GAP);
		captionLayout.draw(g2d, (float) ((PAGE_WIDTH - rectCaption.getWidth()) / 2), (float) (PAGE_TOP_LINE - 2 * PAGE_INNER_Y_GAP - rectSubcaption.getHeight()));
		
		g2d.setStroke(oldStroke);
		g2d.setColor(oldColor);
	}
	
	private void paintColumn(final Graphics2D g2d, final Iterable<AttributedCharacterIterator> iterableText) {
	    final float		width = PAGE_WIDTH;
	    final float 	height = width * getHeight() / getWidth();
	    final float 	colWidth = width/PAGE_COLUMNS;

	    float	yLine = PAGE_UPPER_LINE + PAGE_TOP_Y_GAP, xStart = PAGE_LEFT_LINE + PAGE_LEFT_X_GAP;
	    int		colNo = 0;

all:	for (AttributedCharacterIterator columns : iterableText) {
		    final LineBreakMeasurer	lbm = new LineBreakMeasurer(columns, g2d.getFontRenderContext());
		    final int 	start = columns.getBeginIndex();
		    final int 	end = columns.getEndIndex();
		    final float	currentWidth = (colNo+1)*colWidth - xStart - (colNo == PAGE_COLUMNS - 1 ? PAGE_RIGHT_X_GAP : PAGE_INNER_X_GAP); 
		    
		    lbm.setPosition(start);
		    while (lbm.getPosition() < end) {
		        while (lbm.getPosition() < end && yLine < height - PAGE_BOTTOM_Y_GAP) {
				    final TextLayout 	textLayout = lbm.nextLayout(currentWidth);
				    
				    yLine += textLayout.getAscent();
				    textLayout.draw(g2d, xStart, yLine);
					yLine += textLayout.getDescent() + textLayout.getLeading();
		        }
		        if (yLine >= height - PAGE_BOTTOM_Y_GAP) {
			        if (++colNo >= PAGE_COLUMNS) {
			        	break all;
			        }
			        else {
				        xStart = colNo * colWidth + PAGE_INNER_X_GAP;
				        yLine = PAGE_UPPER_LINE + PAGE_TOP_Y_GAP;
			        }
		        }
		        else {
		        	yLine += PAGE_INNER_Y_GAP;
		        }
		    }
	    }
	} 

	
	private static class NoteItem implements AttributedCharacterIterator {
		private static final Attribute[]				CAPTION_KEYS = {TextAttribute.FONT, 
																		TextAttribute.FAMILY, 
																		TextAttribute.WEIGHT,
																		TextAttribute.FOREGROUND}; 
		private static final Map<Attribute, Object>		CAPTION_ATTR = new HashMap<>(); 
		private static final Attribute[]				BODY_KEYS = {	TextAttribute.FONT, 
																		TextAttribute.FAMILY,
																		TextAttribute.WEIGHT,
																		TextAttribute.FOREGROUND};
		private static final Map<Attribute, Object>		BODY_ATTR = new HashMap<>();
		private static final Set<Attribute>				ALL_ATTR;
		private static final String						DEFAULT_FONT_FAMILY = "Times New Roman";
		
		static {
			final Set<Attribute>	temp = new HashSet<>();
			AffineTransform			at;
			
			temp.addAll(Arrays.asList(CAPTION_KEYS));
			temp.addAll(Arrays.asList(BODY_KEYS));
			ALL_ATTR = Collections.unmodifiableSet(temp);

			at = new AffineTransform();
			at.scale(1.3,1.3);
			CAPTION_ATTR.put(TextAttribute.FONT, ApplicationFont.FONT_13.getFont().deriveFont(Font.BOLD,at));
			CAPTION_ATTR.put(TextAttribute.FAMILY, DEFAULT_FONT_FAMILY);
			CAPTION_ATTR.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_ULTRABOLD);
			CAPTION_ATTR.put(TextAttribute.FOREGROUND, PAGE_COLOR);

			at = new AffineTransform();
			at.scale(1.1,1.1);
			BODY_ATTR.put(TextAttribute.FONT, ApplicationFont.FONT_13.getFont().deriveFont(Font.PLAIN,at));
			BODY_ATTR.put(TextAttribute.FAMILY, DEFAULT_FONT_FAMILY);
			BODY_ATTR.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_SEMIBOLD);
			BODY_ATTR.put(TextAttribute.FOREGROUND, PAGE_COLOR);
		}
		
		private final char[]				content;
		private final Map<Attribute, Object>[]	attributes;
		private final int[][]				attrRanges;
		private int							currentPos = 0;
		
		public NoteItem(final String note, final boolean isCaption) {
			if (note == null) {
				throw new IllegalArgumentException("Noted list can't be null or empty"); 
			}
			else {
				final List<Map<Attribute, Object>>	attrs = new ArrayList<>();
				final List<int[]>		ranges = new ArrayList<>();
				
				if (isCaption) {
					attrs.add(CAPTION_ATTR);
					ranges.add(new int[] {0,note.length() - 1});
				}
				else {
					attrs.add(BODY_ATTR);
					ranges.add(new int[] {0,note.length() - 1});
				}
				this.attributes = attrs.toArray(new Map[attrs.size()]);
				this.attrRanges = ranges.toArray(new int[ranges.size()][]);
				this.content = note.toCharArray();
			}
		}
		
		@Override
		public Object clone() {
			try{return super.clone();
			} catch (CloneNotSupportedException e) {
				return this;
			}			
		}
		
		@Override
		public char first() {
			setIndex(getBeginIndex());
			
			if (inside()) {
				return current();
			}
			else {
				return DONE;
			}
		}

		@Override
		public char last() {
			setIndex(getEndIndex());
			
			if (inside()) {
				return current();
			}
			else {
				return DONE;
			}
		}

		@Override
		public char current() {
			if (inside()) {
				return content[getIndex()];
			}
			else {
				return DONE;
			}
		}

		@Override
		public char next() {
			if (getIndex() >= getEndIndex()) {
				return DONE;
			}
			else {
				setIndex(getIndex()+1);
				return current();
			}
		}

		@Override
		public char previous() {
			if (getIndex() <= getBeginIndex()) {
				return DONE;
			}
			else {
				setIndex(getIndex()-1);
				return current();
			}
		}

		@Override
		public char setIndex(final int position) {
			currentPos = position;
			return current();
		}

		@Override
		public int getBeginIndex() {
			return 0;
		}

		@Override
		public int getEndIndex() {
			return content.length-1;
		}

		@Override
		public int getIndex() {
			return currentPos;
		}

		@Override
		public int getRunStart() {
			return attrRanges[getRange(getIndex())][0];
		}

		@Override
		public int getRunStart(final Attribute attribute) {
			for (int index = getRange(getIndex()); index >= 0; index--) {
				if (!attributes[index].containsKey(attribute)) {
					return index + 1;
				}
			}
			return attrRanges[0][0];
		}

		@Override
		public int getRunStart(final Set<? extends Attribute> attr) {
			for (int index = getRange(getIndex()); index >= 0; index--) {
				if (!attributes[index].keySet().contains(attr)) {
					return attrRanges[index + 1][0];
				}
			}
			return attrRanges[0][0];
		}

		@Override
		public int getRunLimit() {
			return attrRanges[getRange(getIndex())][1]+1;
		}

		@Override
		public int getRunLimit(final Attribute attribute) {
			for (int index = getRange(getIndex()); index < attributes.length; index++) {
				if (!attributes[index].containsKey(attribute)) {
					return attrRanges[index - 1][1]+1;
				}
			}
			return attrRanges[attributes.length-1][1];
		}

		@Override
		public int getRunLimit(final Set<? extends Attribute> attr) {
			for (int index = getRange(getIndex()); index < attributes.length; index++) {
				if (!attributes[index].keySet().contains(attr)) {
					return attrRanges[index - 1][1];
				}
			}
			return attrRanges[attributes.length-1][1];
		}

		@Override
		public Map<Attribute, Object> getAttributes() {
			final int	current = getIndex();
			
			for (int index = 0; index < attrRanges.length; index++) {
				if (current >= attrRanges[index][0] && current <= attrRanges[index][1]) {
					return attributes[index];
				}
			}
			return null;
		}

		@Override
		public Object getAttribute(final Attribute attribute) {
			return attributes[getRange(getIndex())].get(attribute);
		}

		@Override
		public Set<Attribute> getAllAttributeKeys() {
			return ALL_ATTR;
		}
		
		private boolean inside() {
			return getIndex() >= getBeginIndex() && getIndex() < getEndIndex();
		}
		
		private int getRange(final int index) {
			for (int r = 0, maxR = attrRanges.length; r < maxR; r++) {
				if (index >= attrRanges[r][0] && index <= attrRanges[r][1]) {
					return r;
				}
			}
			return 0;
		}
	}
}
