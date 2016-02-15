package edu.stuy.util;

import edu.stuy.robot.Robot;

public class AutoGearShiftingThread implements Runnable {

	@Override
	public void run() {
		Robot.drivetrain.autoGearShift();	
	}

}