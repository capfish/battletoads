package MessageTest;

import java.io.UnsupportedEncodingException;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

/* So it seems like each channel can have at most 1 int in it.
 * If another int gets written, then the original one is overwritten.
 * For long messages, the only way seems to be writing on different channels.
 * This Message class is assuming perfect situations. I'm playing around
 * with a better one as well. It should have the same public functions.
 * Feel free to play around as well.
 */

public class Message {
	private RobotController rc;
	private int s_channel, r_channel;
	Message(RobotController rc) {
		this.rc = rc;
		s_channel = 0;
		r_channel = 0;
	}
	//String should be 8ish long and consist of 1s and 0s. 
	//lsb bit = isLocation? here, should be 0.
	//if !isLocation, then second lsb bit = gotowards/getencampments/mines/whatever
	public void send(String bits) {
		try {
			rc.broadcast(s_channel, Integer.parseInt(bits, 2));
			s_channel = s_channel + 1;
		} catch (Exception e) {}
	}
	public void sendLoc(MapLocation loc) {
		int msg = ((loc.x * 256 + loc.y) * 256) + 1;
		try {
			rc.broadcast(s_channel, msg);
			s_channel = s_channel + 1;
		} catch (Exception e) {}
	}
	public String receive() {
		try {
			int msg = rc.readBroadcast(r_channel);
			r_channel = r_channel + 1;
			if (msg % 2 == 0) return decrypt(msg);
			else return decryptLoc(msg);
		} catch (Exception e) {
			return "error";
		}
	}
	private static String decrypt(int msg) {
		return Integer.toBinaryString(msg);
	}
	private static String decryptLoc(int msg) {
		msg = msg - 1;
		msg = msg/256;
		int y = msg % 256;
		msg = msg/256;
		int x = msg;
		return "x: " + x + " y: " + y;
	}
	public void reset() {
		r_channel = 0;
		s_channel = 0;
	}
}
