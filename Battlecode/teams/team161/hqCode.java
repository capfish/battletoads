package team161;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Upgrade;

public class hqCode {
    private static Direction randomDir(RobotController rc) {
        Direction dir = Direction.values()[(int)(Math.random()*8)];
        if(rc.canMove(dir)) return dir;
        else return randomDir(rc);
    }

	public static void hqRun(RobotController rc, MapLocation enemyHQ) throws GameActionException {
		if (rc.isActive()) {
			/*if (rc.getTeamPower() < 50) {
				if (!rc.hasUpgrade(Upgrade.PICKAXE)) rc.researchUpgrade(Upgrade.PICKAXE);
				else if(!rc.hasUpgrade(Upgrade.FUSION)) rc.researchUpgrade(Upgrade.FUSION);
				else rc.researchUpgrade(Upgrade.NUKE);
			} else {
				if (Math.random() < 0.2) rc.spawn(randomDir(rc));
				else if (!rc.hasUpgrade(Upgrade.PICKAXE)) rc.researchUpgrade(Upgrade.PICKAXE);
				else rc.spawn(randomDir(rc));
			}*/
			
			 if (rc.canMove(rc.getLocation().directionTo(enemyHQ))) rc.spawn(rc.getLocation().directionTo(enemyHQ));
			 else rc.spawn(randomDir(rc));
		}
	}

}
