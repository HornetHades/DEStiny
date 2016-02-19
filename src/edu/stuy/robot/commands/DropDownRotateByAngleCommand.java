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
	
    public DropDownRotateByAngleCommand() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.dropdown);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	rpm = SmartDashboard.getNumber("Arm RPM");
    	double angle = SmartDashboard.getNumber("Arm Rotation");
    	targetDuration = rotationToTime(angle, rpm);
    	startTime = Timer.getFPGATimestamp();
    	Robot.dropdown.go(rpm);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return ((Timer.getFPGATimestamp() - startTime) >= targetDuration);
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