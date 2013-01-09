package MessageTest;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class RobotPlayer {
	public static void run(RobotController rc) {
		Message msg = new Message(rc);
		while (true) {
			try {
				msg.reset();
				if (rc.getType() == RobotType.HQ) {
					if (Clock.getRoundNum() < 2) {
						Direction dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
						if (rc.canMove(dir))
							rc.spawn(dir);
					} else {
						msg.send("010010110");
						msg.sendLoc(rc.senseEnemyHQLocation());
					}
				} else if (rc.getType() == RobotType.SOLDIER) {
					if (rc.isActive()) {
						System.out.println(msg.receive());
						System.out.println(msg.receive());
					}
				}

				// End turn
				rc.yield();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
