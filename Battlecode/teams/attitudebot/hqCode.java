package attitudebot;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Upgrade;

public class hqCode {

	public static void hqRun(RobotController rc, MapLocation enemyHQ) throws GameActionException {
		// TODO Auto-generated method stub
		if (rc.isActive()) {
			if (rc.getTeamPower() < 2) {
				if (!rc.hasUpgrade(Upgrade.PICKAXE)) rc.researchUpgrade(Upgrade.PICKAXE);
				else if(!rc.hasUpgrade(Upgrade.FUSION)) rc.researchUpgrade(Upgrade.FUSION));
				else rc.researchUpgrade(Upgrade.NUKE);
			} else {
				rc.spawn(arg0);
			}
		}
	}

}
