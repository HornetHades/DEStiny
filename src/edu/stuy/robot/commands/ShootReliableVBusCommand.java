package edu.stuy.robot.commands;

import static edu.stuy.robot.RobotMap.SHOOTER_ENCODER_MAXSPEED;
import edu.stuy.robot.Robot;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class ShootReliableVBusCommand extends Command {

    private double desiredSpeed = 3500.0;
    private double currentSpeed = 3500.0;

    public ShootReliableVBusCommand() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        requires(Robot.shooter);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        Robot.shooter.setSpeed(currentSpeed);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        if (Math.abs(Robot.shooter.getCurrentMotorSpeedInRPM() - desiredSpeed * SHOOTER_ENCODER_MAXSPEED) < 150.0) {
            if (Robot.shooter.getCurrentMotorSpeedInRPM() - desiredSpeed * SHOOTER_ENCODER_MAXSPEED > 0.0) {
                currentSpeed -= 0.05;
            } else {
                currentSpeed += 0.05;
            }
            Robot.shooter.setSpeed(currentSpeed);
        }
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
