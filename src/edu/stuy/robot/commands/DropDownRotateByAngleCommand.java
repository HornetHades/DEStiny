package edu.stuy.robot.commands;

import edu.stuy.robot.Robot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class DropDownRotateByAngleCommand extends Command {
	private double targetDuration;
	private double rpm;
	private double startTime;
	private double angle;
	private double prevTime;
	private double counterDuration;
	// "Backup" duration.

	private double angleSum;
	
    public DropDownRotateByAngleCommand() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.dropdown);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	rpm = SmartDashboard.getNumber("Arm RPM");
    	angle = SmartDashboard.getNumber("Arm Rotation");
    	targetDuration = rotationToTime(angle, rpm);
    	startTime = Timer.getFPGATimestamp();
    	prevTime = startTime;

    	counterDuration = targetDuration;
    	angleSum = 0;

    	Robot.dropdown.go(rpm);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	double rpmReal = Robot.dropdown.getSpeed();
    	counterDuration = rotationToTime(angle - angleSum, rpmReal);
    	double timeElapsed = (Timer.getFPGATimestamp() - prevTime);
    	angleSum += 360 * rpmReal * timeElapsed / 60;

    	prevTime = Timer.getFPGATimestamp();
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return ( ((Timer.getFPGATimestamp() - startTime) >= targetDuration) 
        		&& (angleSum >= angle) );
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.dropdown.go(0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }

    private double rotationToTime(double angle, double rpm) {
		// In seconds
		return (angle / (rpm * 360 / 60));
	}
}