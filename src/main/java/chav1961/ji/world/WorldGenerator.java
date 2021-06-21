package chav1961.ji.world;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import chav1961.ji.ResourceRepository;
import chav1961.ji.interfaces.Country;
import chav1961.ji.interfaces.CountryType;
import chav1961.ji.interfaces.FriendshipDepth;
import chav1961.ji.interfaces.RelationType;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.model.interfaces.NodeMetadataOwner;

public class WorldGenerator {
	private static final GeneralPath	CELL = new GeneralPath();
	private static final float			CELL_SIZE = 16;
	private static final double			HALF = 0.5; 
	private static final double			SQRT_3 = Math.sqrt(3); 
	private static final double			SQRT_3_HALF = SQRT_3 * HALF; 
	
	static {
		CELL.moveTo(-SQRT_3_HALF, HALF);
		CELL.lineTo(0, 1);
		CELL.lineTo(SQRT_3_HALF, HALF);
		CELL.lineTo(SQRT_3_HALF, -HALF);
		CELL.lineTo(0, -1);
		CELL.lineTo(-SQRT_3_HALF, -HALF);
		CELL.closePath();
	}
	
	
	
	private final long			seed;
	private final Random		rand;
	private final int			width, height;
	private final WorldMesh 	mesh;
	private final Map<Country,CountryDescriptor>	countries = new HashMap<>(); 
	
	public WorldGenerator(final int width, final int height) throws ContentException {
		this((long) (Long.MAX_VALUE * Math.random()), width, height);
	}
	
	public WorldGenerator(final long startRandom, final int width, final int height) throws ContentException {
		this.seed = startRandom;
		this.rand = new Random(startRandom);
		this.width = width;
		this.height = height;
		this.mesh = new WorldMesh(width, height);
		
		createDraftMesh(ResourceRepository.ROOT_META.byUIPath(URI.create("ui:/model/navigation.top.mainmenu/navigation.node.menu.countries")));
	}
	
	public long getStartRandom() {
		return seed;
	}

	public CountryDescriptor getCountryDescriptor(final Country country) {
		return countries.get(country);
	}
	
	public Dimension getCardSize() {
		return new Dimension((int)((width + 2)* CELL_SIZE * SQRT_3), (int)(1.5 * (height + 2) * CELL_SIZE));
	}
	
	private void createDraftMesh(final ContentNodeMetadata meta) throws ContentException {
		for (Country item : Country.values()) {
			if (item.getType() == CountryType.METROPOLY) {
				countries.put(item, createCountryDescriptor(meta, item, locateCountry(rand, mesh, width, height, item)));
			}
		}
		
		for (Country item : Country.values()) {
			if (item.getType() == CountryType.COLONY) {
				countries.put(item, createCountryDescriptor(meta, item, locateCountry(rand, mesh, width, height, item)));
			}
		}
	}

	private static CountryDescriptor createCountryDescriptor(final ContentNodeMetadata meta, final Country country, final Shape shape) throws ContentException {
		for (ContentNodeMetadata item : meta) {
			if (item.getName().endsWith(country.name())) {
				return new CountryDescriptor(item, country, shape); 
			}
		}
		throw new ContentException("Country menu doesn't contain metadata for country ["+country+"]"); 
	}
	
	private static Shape locateCountry(final Random rand, final WorldMesh wm, final int width, final int height, final Country country) {
		final int		initialSize = country.getType().getInitialSize();
		final int[][]	coords = new int[initialSize][2];
		final 			GeneralPath gp = new GeneralPath();
		
		for (;;) {
			int	x = (int) (rand.nextDouble() * width);
			int	y = (int) (rand.nextDouble() * height);
			
			if (wm.cellFree(x,y)) {
				if (fill(rand, wm, x, y, initialSize, country, WorldMesh.Direction.UNDEFINED, coords)) {
					break;
				}
			}
		}
		final Area	area = new Area();
		
		for (int index = 0; index < initialSize; index++) {
			area.add(new Area(buildShape(width, height, 1, 1, CELL_SIZE, coords[index])));
		}
		
		gp.append(area.getPathIterator(new AffineTransform()), true);
		return gp;
	}
	
	private static boolean fill(final Random rand, final WorldMesh locations, final int x, final int y, final int count, final Country country, final WorldMesh.Direction lastDirection, final int[][] coords) {
		if (!locations.cellExists(x, y) || !locations.cellFree(x, y)) {
			return false;
		}
		else if (count == 0) {
			return true;
		}
		else {
			final int[][]	neighbours = locations.neighbours(x, y);
			final boolean[]	passed = new boolean[neighbours.length];
			
			locations.setCell(x, y, country);
			coords[count-1][0] = x;
			coords[count-1][1] = y;
			do {
				final int 					dir = (int) (neighbours.length * rand.nextDouble());
				final int					xTo = x+neighbours[dir][0], yTo = y+neighbours[dir][1];
				final WorldMesh.Direction	newDirection = locations.calculateDirection(x, y, xTo, yTo); 
				
				if (!passed[dir]) {
					passed[dir] = true;
					if (newDirection != lastDirection && fill(rand, locations, xTo, yTo, count-1, country, newDirection, coords)) {
						return true;
					}
				}
			} while (!allPassed(passed));
			
			locations.setCell(x, y, null);
			return false;
		}
	}
	
	private static boolean allPassed(final boolean[] passed) {
		for (boolean item : passed) {
			if (!item) {
				return false;
			}
		}
		return true;
	}
	
	private static Shape buildShape(final float width, final float height, final float deltaX, final float deltaY, final float cellSize, final int[] point) {
		final AffineTransform	at = new AffineTransform();

		at.translate((point[0]  + (point[1] % 2 != 0 ? 0 : HALF) + deltaX) * cellSize * SQRT_3, 1.5 * (point[1] + deltaY) * cellSize);
		at.scale(cellSize, cellSize);
		return CELL.createTransformedShape(at); 
	}
	
	
	private static class WorldMesh {	// {X , Y}
		private static final int[][]	EVEN_NEIGHBOURS = {{0,-1}, {1,-1}, {1,0}, {1,1}, {0,1}, {-1,0}};
		private static final int[][]	ODD_NEIGHBOURS = {{-1,-1}, {0,-1}, {1,0}, {0,1}, {-1,1}, {-1,0}};
		
		private static final int[][]	UPPER_LEFT_NEIGHBOURS = {{1,0}, {0,1}};
		private static final int[][]	UPPER_RIGHT_NEIGHBOURS = {{-1,0}, {0,1}};
		private static final int[][]	LOWER_LEFT_EVEN_NEIGHBOURS = {{0,-1}, {1,0}};
		private static final int[][]	LOWER_LEFT_ODD_NEIGHBOURS = {{0,-1}, {1,-1}, {1,0}};
		private static final int[][]	LOWER_RIGHT_EVEN_NEIGHBOURS = {{-1,0}, {-1,0}};
		private static final int[][]	LOWER_RIGHT_ODD_NEIGHBOURS = {{-1,0}, {-1,-1}, {0,-1}};
		
		private static final int[][]	UPPER_NEIGHBOURS = {{1,0}, {1,1}, {0,1}, {-1,0}};
		private static final int[][]	LOWER_NEIGHBOURS = {{-1,0}, {-1,-1}, {0,-1}, {1,0}};
		private static final int[][]	LEFT_EVEN_NEIGHBOURS = {{0,-1}, {1,0}, {0,1}};
		private static final int[][]	LEFT_ODD_NEIGHBOURS = {{0,-1}, {1,-1}, {1,0}, {1,1}, {0,1}};
		private static final int[][]	RIGHT_EVEN_NEIGHBOURS = {{-1,-1}, {-1,0}, {-1,1}};
		private static final int[][]	RIGHT_ODD_NEIGHBOURS = {{1,-1}, {0,-1}, {-1,0}, {0,1}, {1,1}};
		
		
		private final int				width;
		private final int				height;
		private final Country[][]		cells;
		
		public enum Direction {
			UNDEFINED, UP_LEFT, UP_RIGHT, RIGHT, DOWN_RIGHT, DOWN_LEFT, LEFT
		}
		
		public WorldMesh(final int width, final int height) {
			if (width <= 0) {
				throw new IllegalArgumentException("World width ["+width+"] must be positive"); 
			}
			else if (height <= 0) {
				throw new IllegalArgumentException("World height ["+height+"] must be positive"); 
			}
			else {
				this.width = width;
				this.height = height;
				this.cells = new Country[height][width];
			}
		}
		
		public Country getCell(final int x, final int y) {
			return cells[y][x];
		}

		public void setCell(final int x, final int y, final Country cell) {
			cells[y][x] = cell;
		}
		
		public Direction calculateDirection(final int xFrom, final int yFrom, final int xTo, final int yTo) {
			if (!cellExists(xFrom, yFrom) || !cellExists(xTo, yTo)) {
				return Direction.UNDEFINED;
			}
			else if (yFrom == yTo) {
				return xFrom < xTo ? Direction.RIGHT : Direction.LEFT;
			}
			else if (yFrom % 2 == 0) {
				if (yFrom < yTo) {
					return xFrom == xTo ? Direction.DOWN_LEFT : Direction.DOWN_RIGHT;
				}
				else {
					return xFrom == xTo ? Direction.UP_LEFT : Direction.UP_RIGHT;
				}
			}
			else {
				if (yFrom == yTo) {
					return xFrom == xTo ? Direction.DOWN_LEFT : Direction.DOWN_RIGHT;
				}
				else {
					return xFrom == xTo ? Direction.UP_LEFT : Direction.UP_RIGHT;
				}
			}
		}
		
		public boolean cellExists(final int x, final int y) {
			if (y < 0 || y >= height) {
				return false;
			}
			else if (x < 0 || x >= (y % 2 == 0 ? width : width - 1)) {
				return false;
			}
			else {
				return true;
			}
		}

		public boolean cellFree(final int x, final int y) {
			if (y < 0 || y >= height) {
				return false;
			}
			else if (x < 0 || x >= (y % 2 == 0 ? width : width - 1)) {
				return false;
			}
			else {
				return cells[y][x] == null;
			}
		}
		
		public int[][] neighbours(final int x, final int y) {
			if (x < 0 || x >= width) {
				throw new IllegalArgumentException("X coordinate ["+x+"] must be in 0.."+(width-1)+" range");
			}
			else if (y < 0 || y >= height) {
				throw new IllegalArgumentException("Y coordinate ["+y+"] must be in 0.."+(height-1)+" range");
			}
			else if (x == 0){
				if (y == 0) {
					return UPPER_LEFT_NEIGHBOURS;
				}
				else if (y == height - 1) {
					if (y % 2 == 0) {
						return LOWER_LEFT_EVEN_NEIGHBOURS;
					}
					else {
						return LOWER_LEFT_ODD_NEIGHBOURS;
					}
				}
				else {
					if (y % 2 == 0) {
						return LEFT_EVEN_NEIGHBOURS;
					}
					else {
						return LEFT_ODD_NEIGHBOURS;
					}
				}
			}
			else if (x == width - 1){
				if (y == 0) {
					return UPPER_RIGHT_NEIGHBOURS;
				}
				else if (y == height - 1) {
					if (y % 2 == 0) {
						return LOWER_RIGHT_EVEN_NEIGHBOURS;
					}
					else {
						return LOWER_RIGHT_ODD_NEIGHBOURS;
					}
				}
				else {
					if (y % 2 == 0) {
						return RIGHT_EVEN_NEIGHBOURS;
					}
					else {
						return RIGHT_ODD_NEIGHBOURS;
					}
				}
			}
			else if (y == 0) {
				return UPPER_NEIGHBOURS;
			}
			else if (y == height - 1) {
				return LOWER_NEIGHBOURS;
			}
			else if (y % 2 == 0) {
				return EVEN_NEIGHBOURS;
			}
			else {
				return ODD_NEIGHBOURS;
			}
		}
	}

	
	public static class CountryDescriptor implements NodeMetadataOwner {
		private final ContentNodeMetadata	meta;
		private final Country				country;
		private final Shape					shape;
		private RelationType				relation = RelationType.NEUTRAL;
		private FriendshipDepth				friendshipDepth = FriendshipDepth.NEUTRAL;
		
		public CountryDescriptor(final ContentNodeMetadata meta, final Country country, final Shape shape) {
			this.meta = meta;
			this.country = country;
			this.shape = shape;
		}

		@Override
		public ContentNodeMetadata getNodeMetadata() {
			return meta;
		}
		
		public Country getCountry() {
			return country;
		}
		
		public Shape getShape() {
			return shape;
		}

		public RelationType getRelation() {
			return relation;
		}

		public void setRelation(RelationType relation) {
			this.relation = relation;
		}

		public FriendshipDepth getFriendshipDepth() {
			return friendshipDepth;
		}

		public void setFriendshipDepth(FriendshipDepth friendshipDepth) {
			this.friendshipDepth = friendshipDepth;
		}
	}
	
//
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		final int				width = 20, height = 16;
//		final long				startRand = 648598753997723648L;//(long) (Long.MAX_VALUE * Math.random());
//		final WorldGenerator	wg = new WorldGenerator(startRand, width, height);
//		final int				imageWidth = (int)((width + 2)* CELL_SIZE * SQRT_3), imageHeight =  (int)(1.5 * (height + 2) * CELL_SIZE); 
//		final BufferedImage		bi = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
//		final Graphics2D		g2d = (Graphics2D)bi.getGraphics();
//
//		g2d.setColor(new Color(224,255,255));
//		g2d.fillRect(0, 0, imageWidth, imageHeight);
//		
//		for (int x = 0; x < width; x++) {
//			for (int y = 0; y < height; y++) {
//				
//				if (wg.mesh.cellExists(x, y) && !wg.mesh.cellFree(x, y)) {
////					final AffineTransform	at = new AffineTransform();
//					final Country			c = wg.mesh.getCell(x, y);
//					final Shape				shape = buildShape(width, height, 1, 1, CELL_SIZE, new int[] {x, y});
//
////					at.translate((x  + (y % 2 != 0 ? 0 : HALF) + 1) * cellSize * SQRT_3, 1.5 * (y + 1) * cellSize);
////					at.scale(cellSize, cellSize);
//					g2d.setColor(new Color(c.getColor().getRGB() & (c.getType() == CountryType.METROPOLY ? Color.WHITE : Color.GRAY).getRGB()));
//					g2d.fill(shape);
////					g2d.fill(CELL.createTransformedShape(at));
//				}
//			}
//		}
//		g2d.setColor(Color.BLACK);
//		for (Entry<Country, Shape> item : wg.countryShapes.entrySet()) {
//			g2d.draw(item.getValue());
//		}
//		
//		final JLabel	label = new JLabel(new ImageIcon(bi));
//		
//		JOptionPane.showMessageDialog(null, label);
//	}
//
}
