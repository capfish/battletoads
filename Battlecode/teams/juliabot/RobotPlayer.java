package juliabot;


import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;


public class RobotPlayer {
	private static int bin_size = 2;
	private static MapLocation enemyHQ;
	private static MapLocation myHQ;
	private static int height, width, map_height, map_width;
    //static boolean[][] map;
	static int[][] map;
    static Point[][] weights;
    static Point[] queue;
    static int queueIndex;
	public static void run(RobotController rc) {
		enemyHQ = rc.senseEnemyHQLocation();
		myHQ = rc.senseHQLocation();
		map_height = rc.getMapHeight();
		map_width = rc.getMapWidth();
		height = map_height/bin_size;
		width = map_width/bin_size;
	    //map = new boolean[width][height];
	    map = new int[width][height];
	    weights = new Point[width][height];
		while (true) {
			try {
			    System.out.println("mapw" + map_width + " maph" + map_height);

				if (rc.getType() == RobotType.HQ) {
					if (rc.isActive()) {
                        MapLocation[] mines = rc.senseNonAlliedMineLocations(new MapLocation(map_width/2, map_height/2), map_height*map_width);
                        queue = new Point[(width*height)/3];
                        queueIndex = 0;
                        //for (int i = 0; i < width; i++) for (int j = 0; j < height; j++) map[i][j] = false;
                        //for (int i = 0; i < mines.length; i++) map[mines[i].x][mines[i].y] = true;
                        for (int i = 0; i < width; i++) for (int j = 0; j < height; j++) map[i][j] = 0;
                        System.out.println("width" + width + " height" + height);
                        for (int i = 0; i < mines.length; i++) 
                        	if (!(bin(mines[i].x) == width || bin(mines[i].y) == height)) map[bin(mines[i].x)][bin(mines[i].y)] ++ ;
                        for (int i = 0; i < width; i++) for (int j = 0; j < height; j++) weights[i][j] = new Point(i,j);
                        Point dude = new Point(bin(myHQ.x), bin(myHQ.y), 0);
                        dude.inQ = true;
                        queue[0] = dude;
                        weights[bin(myHQ.x)][bin(myHQ.y)] = dude;
                        System.out.println(Clock.getRoundNum());
                        while(true) {
                        	if (expand(rc)) break;
                        }
                        for (int i = 0; i < height; i++) {
                        	for (int j = 0; j < width; j++) 
                        		if (i == bin(enemyHQ.y) && j == bin(enemyHQ.x)) System.out.printf("x%3dx ", weights[j][i].cost);
                        		else System.out.printf("%5d ", weights[j][i].cost);
                        	System.out.println("");
                        }
                        System.out.println(Clock.getRoundNum());
                        while(true) {}
                        
					}
				} else if (rc.getType() == RobotType.SOLDIER) {
					if (rc.isActive()) {
					}
				}
				rc.yield();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	private static int bin(int n) {
		return n/bin_size;
	}
	
    private static Direction randomDir(RobotController rc) {
        Direction dir = Direction.values()[(int)(Math.random()*8)];
        if(rc.canMove(dir)) return dir;
        else return randomDir(rc);
    }
	private static boolean expand(RobotController rc) {
		Point square = poll();
		int x = square.i;
		int y = square.j;
		if (bin(enemyHQ.x) == x && bin(enemyHQ.y) == y) return true;
		for (int i = -1; i < 2; i++)
			for (int j = -1; j < 2; j++) 
				if (!(i == 0 && j == 0) && inMap(x+i, y+j)) {
					Point p = weights[x+i][y+j];
					if (! p.expanded) {
						int cost = square.cost + squareCost(x+i, y+j);
						if (p.cost > cost) {
							p.cost = cost;
							if (! p.inQ) add(p);
						}
					}
				}
		return false;
	}
	/*private static int squareCost(int x, int y) {
		if (map[x][y]) return GameConstants.MINE_DEFUSE_DELAY;
		else return 1;
	}*/
	private static int squareCost(int x, int y) {
		return map[x][y] * GameConstants.MINE_DEFUSE_DELAY + bin_size;
	}
	private static boolean inMap(int x, int y) {
		if (x<0 || x>=width || y<0 || y>=height) return false;
		return true;
	}

	private static Point poll() {
		Point cur = weights[bin(enemyHQ.x)][bin(enemyHQ.y)];
		double cur_h = 0;
		int index = -1;
		for (int i = 0; i < queue.length; i++) {
			if (queue[i] != null) {
				double h = 7 * Math.sqrt(new MapLocation(queue[i].i*bin_size, queue[i].j*bin_size).distanceSquaredTo(enemyHQ));
				if (queue[i].cost + h <= cur.cost + cur_h) {
					cur = queue[i];
					cur_h = h;
					index = i;
				}
			}
		}
		queue[index] = null;
		cur.expanded = true;
		return cur;
	}
	private static void add(Point p) {
		p.inQ = true;
		if (queue[queueIndex] == null) queue[queueIndex] = p;
		else {
			queueIndex = (queueIndex+1) % queue.length;
			add(p);
		}
	}
	//private static void
	
	private static class Point {
		public int i;
		public int j;
		public int cost;
		public boolean expanded;
		public boolean inQ;
		Point(int i, int j, int cost) {
			this.i = i;
			this.j = j;
			this.cost = cost;
			this.expanded = false;
			this.inQ = false;
		}
		Point(int i, int j) {
			this.i = i;
			this.j = j;
			this.cost = 10000;
			this.expanded = false;
			this.inQ = false;
		}
	}
}
