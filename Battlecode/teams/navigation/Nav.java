package navigation;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;

public interface Nav {
	
	Direction getNextStep(MapLocation curLoc, MapLocation target, MapLocation[] nMine, MapLocation[] eMine);

}
