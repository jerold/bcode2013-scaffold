package deepBluePlayer;

import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.engine.instrumenter.lang.System;

/** 
 * Baller AI designed to straight up dominate
 */
public class RobotPlayer {
	static MapLocation enemyHQLocation;
	
	public static void run(RobotController rc) {
		while (true) {
			try {
				if (enemyHQLocation == null) {
					enemyHQLocation = rc.senseEnemyHQLocation();
					// System.out.println("Enemy Location:" + enemyHQ);
				}
				
				if (rc.getType() == RobotType.HQ) {
					if (rc.isActive()) {
						// Spawn a soldier
						Direction dir = rc.getLocation().directionTo(enemyHQLocation);
						if (rc.canMove(dir)) {
							rc.spawn(dir);
						} else {
							Direction rl = dir.rotateLeft();
							if (rc.canMove(rl)) {
								rc.spawn(rl);
							} else {
								Direction rr = dir.rotateRight();
								if (rc.canMove(rr)) {
									rc.spawn(rr);
								}
							}
						}
					}
				} else if (rc.getType() == RobotType.SOLDIER) {
					if (rc.isActive()) {
						Direction dir = rc.getLocation().directionTo(enemyHQLocation);
						MapLocation[] mineLocations = rc.senseNonAlliedMineLocations(rc.getLocation(), 2);
						boolean moved = false;
						if (rc.canMove(dir)) {
							boolean mineFront = false;
							for (int i=0; i<mineLocations.length; i++) {
								if (dir.equals(rc.getLocation().directionTo(mineLocations[i])))
									mineFront = true;
							}
							if (!mineFront) {
								rc.move(dir);
								moved = true;
							}
						} else {
							Direction rlDir = dir.rotateLeft();
							if (rc.canMove(rlDir)) {
								boolean mineLeft = false;
								for (int i=0; i<mineLocations.length; i++) {
									if (rlDir.equals(rc.getLocation().directionTo(mineLocations[i])))
										mineLeft = true;
								}
								if (!mineLeft) {
									rc.move(rlDir);
									moved = true;
								}
							} else {
								Direction rrDir = dir.rotateRight();
								if (rc.canMove(rrDir)) {
									boolean mineRight = false;
									for (int i=0; i<mineLocations.length; i++) {
										if (rrDir.equals(rc.getLocation().directionTo(mineLocations[i])))
											mineRight = true;
									}
									if (!mineRight) {
										rc.move(rrDir);
										moved = true;
									}
								}
							}
						}
						if (!moved && mineLocations.length>0)
							rc.defuseMine(mineLocations[0]);
						else
							if (Math.random()<0.01)
								rc.layMine();
					}
					
					if (Math.random()<0.01 && rc.getTeamPower()>5) {
						// Write the number 5 to a position on the message board corresponding to the robot's ID
						rc.broadcast(rc.getRobot().getID()%GameConstants.BROADCAST_MAX_CHANNELS, 5);
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
