package edu.stuy.robot.commands;

import edu.stuy.robot.Robot;
import static edu.stuy.robot.RobotMap.*;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class ShootRPMCommand extends Command {

    public ShootRPMCommand() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        requires(Robot.shooter);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        Robot.shooter.setRPM(SHOOTER_ENCODER_MAXSPEED * 0.95);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return true;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
