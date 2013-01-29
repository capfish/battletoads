package team161;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.Team;

public class Bug {
	public MapLocation target;
	public RobotController rc;
	public Direction prev;
	public int distTravelled;
	public int turnDir;
	public int depth = 1;
	public Direction dir2target;
	public MapLocation stuck;
	public boolean burrow;
	public int dumb = 0;
	public int dist_btw_HQs;
	public int initTurn;
	
	public Bug(MapLocation target, RobotController rc) {
		this.target = target;
		this.rc = rc;
		this.prev = rc.getLocation().directionTo(target);
		this.distTravelled = 0;
		this.dir2target = rc.getLocation().directionTo(target);
		this.stuck = null;
		this.burrow = false;
		this.dist_btw_HQs = (int) Math.sqrt(rc.getLocation().distanceSquaredTo(rc.senseEnemyHQLocation()));
		if (Clock.getRoundNum()/10 % 7 < 4) initTurn = 7;
		else initTurn = 1;
		turnDir = initTurn;
	}
	public void retreat(MapLocation loc) throws GameActionException {
		Direction dir = rc.getLocation().directionTo(loc).opposite();
		if (rc.canMove(dir) && rc.senseMine(rc.getLocation().add(dir)) == null) rc.move(dir);
		else if (rc.canMove(dir.rotateLeft()) && rc.senseMine(rc.getLocation().add(dir.rotateLeft())) == null) rc.move(dir.rotateLeft());
		else if (rc.canMove(dir.rotateRight()) && rc.senseMine(rc.getLocation().add(dir.rotateRight())) == null) rc.move(dir.rotateRight());
		else if (rc.canMove(dir) && rc.senseMine(rc.getLocation().add(dir)) != null) defuse(rc, dir);
		else if (rc.canMove(dir.rotateLeft()) && rc.senseMine(rc.getLocation().add(dir.rotateLeft())) != null) defuse(rc, dir.rotateLeft());
		else if (rc.canMove(dir.rotateRight()) && rc.senseMine(rc.getLocation().add(dir.rotateRight())) != null) defuse(rc, dir.rotateRight());		
	}
	private void defuse(RobotController rc, Direction dir) throws GameActionException {
		rc.defuseMine(rc.getLocation().add(dir));
		rc.yield();rc.yield();rc.yield(); rc.yield();rc.yield();rc.yield();rc.yield();rc.yield();rc.yield();rc.yield();rc.yield();rc.yield();
	}
	
	public void shieldGo() throws GameActionException {
		if (rc.getLocation().distanceSquaredTo(target) > 49) go();
		else {
			dir2target = rc.getLocation().directionTo(target);
			if (rc.canMove(dir2target)) rc.move(dir2target);
			else if (rc.canMove(dir2target.rotateLeft())) rc.move(dir2target.rotateLeft());
			else if (rc.canMove(dir2target.rotateRight())) rc.move(dir2target.rotateRight());
		}
	}
	public void go() throws GameActionException {
		if (rc.getLocation().distanceSquaredTo(target) == 0) return;
		if (rc.getLocation().distanceSquaredTo(target) <= 2 && rc.senseEnemyHQLocation().equals(target)) return;
		if (rc.getLocation().distanceSquaredTo(target) > 81) {
			if (Clock.getRoundNum() > 400) {
				Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, 18, rc.getTeam().opponent());
				Robot[] friends = rc.senseNearbyGameObjects(Robot.class, 8, rc.getTeam());
				if (enemies.length > 0 && friends.length < 3) {
					retreat(rc.senseRobotInfo(enemies[0]).location);
					return;
				}
			}
		}
		rc.setIndicatorString(1, "turnDir" + turnDir);
		dir2target = rc.getLocation().directionTo(target);
		if (burrow) {
			rc.setIndicatorString(0, "burrowing");
			MapLocation newloc = rc.getLocation().add(dir2target);
			defuse(rc, dir2target);
			if (rc.canMove(dir2target)) {
				rc.move(dir2target);
				if (rc.senseMine(newloc.add(dir2target)) == null ||					//CHANGE TO NOT CARE ABOUT OUR MINES
					rc.senseMine(newloc.add(dir2target.rotateLeft())) == null ||
					rc.senseMine(newloc.add(dir2target.rotateRight())) == null) burrow = false;
				return;
			}
		}
		if (rc.getLocation().equals(stuck)) {
			burrow = true;
		}
		MapLocation loc = rc.getLocation();
		Direction dir = loc.directionTo(target);
		dir = turn(dir);
		int back = prev.opposite().ordinal();
		if (dir.ordinal() == back ||
			dir.rotateLeft().ordinal() == back ||
			dir.rotateRight().ordinal() == back) {
			rc.setIndicatorString(0, "toggled dir. prev = " + prev + " dir = " + dir);
			toggleDirection();
			if (turnDir == initTurn) stuck = rc.getLocation(); //BROADCAST STUCK SPOTS!!!
			dir = turn(dir2target);
		}
		if (dir.opposite().rotateLeft() == dir2target || dir.opposite().rotateRight() == dir2target) dumb ++;
		if (dir.opposite() == dir2target ||
				(( dir.opposite().rotateLeft() == dir2target || dir.opposite().rotateRight() == dir2target) && dumb > dist_btw_HQs/10)) {
			depth = 3;
			toggleDirection();
			if (turnDir == initTurn) {
			stuck = rc.getLocation();
			}
		}
		dir = turn(dir2target);
		if (rc.canMove(dir)) {
			rc.move(dir);
			prev = dir;
			distTravelled ++;	//if dist travelled > mineRatio * timeToDefuseMine * distBtwHeadquarters, broadcast shit
			rc.setIndicatorString(0, "depth = " + depth);
		}
		

	}
	public void toggleDirection() {
		if (turnDir == 1) turnDir = 7;
		else turnDir = 1;
	}
	public Direction turn(Direction dir) throws GameActionException {
		if (rc.canMove(dir)) {
			MapLocation newloc = rc.getLocation().add(dir);
			if (rc.senseMine(newloc) != null) {
				if (thinEnough(dir, newloc)) {
					defuse(rc, dir);
					toggleDirection();
					return dir;
				}
				else {
					depth = 1;
					return turn(Direction.values()[(dir.ordinal()+turnDir) % 8]);
				}
			} else {
				depth = 1;
				return dir;
			}
		} else {
			depth = 1;
			return turn(Direction.values()[(dir.ordinal()+turnDir) % 8]);
		}
	}
	public boolean thinEnough(Direction dir, MapLocation loc) {
		for(int i = 1; i < depth; i++) if (rc.senseMine(loc.add(dir, i)) == null) return true;
		return false;
	}
	
}
