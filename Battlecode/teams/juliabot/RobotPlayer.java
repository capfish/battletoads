package juliabot;


import java.util.Comparator;
import java.util.PriorityQueue;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;


public class RobotPlayer {
	private static MapLocation enemyHQ;
	private static MapLocation myHQ;
	private static int width, height;
    static boolean[][] map;
    static Point[][] weights;
    static PriorityQueue<Point> queue;
	public static void run(RobotController rc) {
		enemyHQ = rc.senseEnemyHQLocation();
		myHQ = rc.senseHQLocation();
		width = rc.getMapWidth();
		height = rc.getMapHeight();
	    map = new boolean[height][width];
	    weights = new Point[height][width];
	    
		while (true) {
			try {
				if (rc.getType() == RobotType.HQ) {
					if (rc.isActive()) {
                        MapLocation[] mines = rc.senseNonAlliedMineLocations(rc.getLocation(), width*height);
                        queue = new PriorityQueue<Point>(height, new costComparator());
                        for (int i = 0; i < height; i++) for (int j = 0; j < width; j++) map[i][j] = false;
                        for (int i = 0; i < mines.length; i++) map[mines[i].x][mines[i].y] = true;
                        for (int i = 0; i < height; i++) for (int j = 0; j < width; j++) weights[i][j] = new Point(i,j);
                        Point dude = new Point(myHQ.x, myHQ.y, 0);
                        queue.add(dude);
                        weights[myHQ.x][myHQ.y] = dude;
                        while(true) {
                        	if (expand(rc)) break;
                        }
                        System.out.println("here");
                        for (int i = 0; i < height; i++) {
                        	for (int j = 0; j < width; j++) System.out.printf("%5d ", weights[i][j].cost);
                        	System.out.println("");
                        }
                        
					}
				} else if (rc.getType() == RobotType.SOLDIER) {
				}
				rc.yield();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	private static boolean expand(RobotController rc) {
		Point square = queue.poll();
		int x = square.i;
		int y = square.j;
		if (enemyHQ.x == x && enemyHQ.y == y) return true;
		for (int i = -1; i < 2; i++)
			for (int j = -1; j < 2; j++) 
				if (!(i == 0 && j == 0) && inMap(x+i, y+j)) {
					int cost = square.cost + squareCost(x+i, y+j);
					if (weights[x+i][y+j].cost > cost) {
						queue.remove(weights[x+i][x+j]);
						weights[x+i][y+j].cost = cost;
						queue.add(weights[x+i][x+j]);
					}
				}
		return false;
	}
	private static int squareCost(int x, int y) {
		if (map[x][y]) return GameConstants.MINE_DEFUSE_DELAY;
		else return 1;
	}
	private static boolean inMap(int x, int y) {
		if (x<0 || x>=height || y<0 || y>=width) return false;
		return true;
	}
	/*private static void chase(RobotController rc) {
		if (rc.senseNearbyGameObjects(rc.getType(), 16, rc.getTeam().opponent())) rc.move(rc.getLocation().directionTo(location))
	}*/
	private static class costComparator implements Comparator<Point> {
        public int compare(Point a, Point b) {
            return ((Integer)a.cost).compareTo(b.cost);
        }
    }

	private static class Point {
		public int i;
		public int j;
		public int cost;
		Point(int i, int j, int cost) {
			this.i = i;
			this.j = j;
			this.cost = cost;
		}
		Point(int i, int j) {
			this.i = i;
			this.j = j;
			this.cost = 10000;
		}
	}
}
