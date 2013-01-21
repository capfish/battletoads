package RealMessaging;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class RobotPlayer {
	public static void run(RobotController rc) {
		Message msg = new Message(rc);
		while (true) {
			try {
				msg.reset();
				if (rc.getType() == RobotType.HQ) {
					if (rc.isActive()) {
						Direction dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
						if (rc.canMove(dir))
							rc.spawn(dir);
					}
					msg.send(0, rc.senseEnemyHQLocation());
					msg.send(1, rc.senseEnemyHQLocation());
					msg.send(2, rc.senseEnemyHQLocation());
					msg.send(3, rc.senseEnemyHQLocation());
					
					for (int i = 0; i < 101; i++) {
						msg.receive(i);
						if (msg.command != -1) System.out.println(msg.command + " " + msg.xloc + "," + msg.yloc + " @" + i);
					}
							
					
				} else if (rc.getType() == RobotType.SOLDIER) {
					if (rc.isActive()) {
						rc.move(rc.getLocation().directionTo(rc.senseEnemyHQLocation()));
					}
					msg.send(2, new MapLocation(2,2));
					int i = -1;
					while (true) {
						msg.receive(i);
						if (msg.command == -1) break;
						System.out.println(msg.command + " " + msg.xloc + "," + msg.yloc);
						i--;
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
