package navigation;

import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class RobotPlayer {
	static Nav myNavigation = new bugNav();
	
	public static void run(RobotController rc) {
		while (true) {
			try {
				if (rc.getType() == RobotType.HQ) {
					if (rc.isActive()) {
						//spawn a soldier
						Direction dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
						if (rc.canMove(dir))
							rc.spawn(dir);
					}
				}
				else if (rc.getType() == RobotType.SOLDIER) {
					if (rc.isActive()) {
						//find mines
						MapLocation[] eMines = {}; //rc.senseMineLocations(rc.getLocation(), 2, rc.getTeam().opponent());
						MapLocation[] nMines = {}; //rc.senseMineLocations(rc.getLocation(),  2,  Team.NEUTRAL);
						
						MapLocation target = rc.senseEnemyHQLocation();
						
						Direction dir = myNavigation.getNextStep(rc.getLocation(), target, nMines, eMines);
						if (rc.canMove(dir)) {
							if (rc.senseMine(rc.getLocation().add(dir)) == Team.NEUTRAL)
								rc.defuseMine(rc.getLocation().add(dir));
							rc.move(dir);
						}
					}
				}
				//End turn
				rc.yield();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
