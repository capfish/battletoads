package attitudebot;

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
		// TODO Auto-generated method stub
		if (rc.isActive()) {
			if (rc.getTeamPower() < 2) {
				if (!rc.hasUpgrade(Upgrade.PICKAXE)) rc.researchUpgrade(Upgrade.PICKAXE);
				else if(!rc.hasUpgrade(Upgrade.FUSION)) rc.researchUpgrade(Upgrade.FUSION);
				else rc.researchUpgrade(Upgrade.NUKE);
			} else {
				rc.spawn(randomDir(rc));
			}
		}
	}

}
