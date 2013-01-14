package juliabot;


import java.util.Comparator;
import java.util.PriorityQueue;

import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;


public class RobotPlayer {
	private static MapLocation enemyHQ;
	private static MapLocation myHQ;
	private static int width, height;
    static boolean[][] map;
    static Point[][] weights;
    //static PriorityQueue<Point> queue;
    static Point[] queue;
    static int queueIndex;
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
						//rc.spawn(Direction.SOUTH);
                        MapLocation[] mines = rc.senseNonAlliedMineLocations(rc.getLocation(), width*height);
                        //queue = new PriorityQueue<Point>(height, new costComparator());
                        queue = new Point[(height+width)*5];
                        queueIndex = 0;
                        for (int i = 0; i < height; i++) for (int j = 0; j < width; j++) map[i][j] = false;
                        for (int i = 0; i < mines.length; i++) map[mines[i].x][mines[i].y] = true;
                        for (int i = 0; i < height; i++) for (int j = 0; j < width; j++) weights[i][j] = new Point(i,j);
                        Point dude = new Point(myHQ.x, myHQ.y, 0);
                        //queue.add(dude);
                        queue[0] = dude;
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
					if (rc.isActive()) {
						/*if (rc.getLocation().distanceSquaredTo(enemyHQ) < 70) 
							if (Math.random() < 50) rc.move(randomDir(rc));
							else rc.move(Direction.EAST);
						else {
							Direction dir = rc.getLocation().directionTo(enemyHQ);
							if (rc.senseMine(rc.getLocation().add(dir)) == Team.NEUTRAL) rc.defuseMine(rc.getLocation().add(dir));
							else rc.move(dir);
						}*/
					}
				}
				rc.yield();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
    private static Direction randomDir(RobotController rc) {
        Direction dir = Direction.values()[(int)(Math.random()*8)];
        if(rc.canMove(dir)) return dir;
        else return randomDir(rc);
    }
	private static boolean expand(RobotController rc) {
		//Point square = queue.poll();
		Point square = poll();
		int x = square.i;
		int y = square.j;
		if (enemyHQ.x == x && enemyHQ.y == y) return true;
		for (int i = -1; i < 2; i++)
			for (int j = -1; j < 2; j++) 
				if (!(i == 0 && j == 0) && inMap(x+i, y+j)) {
					int cost = square.cost + squareCost(x+i, y+j);
					if (weights[x+i][y+j].cost > cost) {
						//queue.remove(weights[x+i][x+j]);
						weights[x+i][y+j].cost = cost;
						//queue.add(weights[x+i][x+j]);
						add(weights[x+i][y+i]);
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
	private static Point poll() {
		Point cur = weights[enemyHQ.x][enemyHQ.y];
		int index = -1;
		for (int i = 0; i < queue.length; i++) {
			if (queue[i] != null)
			if (queue[i].cost <= cur.cost) {
				cur = queue[i];
				index = i;
			}
		}
		queue[index] = null;
		return cur;
	}
	private static void add(Point p) {
		for (int i = 0; i < queue.length; i++) 
			if (p.equals(queue[i])) return;
		if (queue[queueIndex] == null) queue[queueIndex] = p;
		else {
			queueIndex = (queueIndex+1) % queue.length;
			add(p);
		}
		
	}
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
