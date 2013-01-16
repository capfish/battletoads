package team161;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Upgrade;

public class hqCode {
    private static Direction randomDir(RobotController rc, int depth) {
    	if ( depth == 0 ) return null;
        Direction dir = Direction.values()[(int)(Math.random()*8)];
        if(rc.canMove(dir)) return dir;
        else return randomDir(rc, depth -1);
    }

	public static void hqRun(RobotController rc, MapLocation enemyHQ) throws GameActionException {
		// TODO Auto-generated method stub
		if (rc.isActive()) {
			if (rc.getTeamPower() < 50) {
				if (!rc.hasUpgrade(Upgrade.PICKAXE)) rc.researchUpgrade(Upgrade.PICKAXE);
				//else if(!rc.hasUpgrade(Upgrade.FUSION)) rc.researchUpgrade(Upgrade.FUSION);
				else rc.researchUpgrade(Upgrade.NUKE);
			} else {
				if (Math.random() < 0.6 && !rc.hasUpgrade(Upgrade.PICKAXE)) rc.researchUpgrade(Upgrade.PICKAXE);
				else {
					Direction dir = randomDir(rc, 15);
					if ( dir == null ) rc.researchUpgrade(Upgrade.NUKE);
					else rc.spawn(randomDir(rc, 15));
				}
			}
		}
	}

}
