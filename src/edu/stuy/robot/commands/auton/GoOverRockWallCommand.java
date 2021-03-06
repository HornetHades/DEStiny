package edu.stuy.robot.commands.auton;

import static edu.stuy.robot.RobotMap.ARM_CROSSING_OBSTACLE_ANGLE;
import edu.stuy.robot.commands.LowGearCommand;
import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * Prepares and drives over the wall
 */
public class GoOverRockWallCommand extends CommandGroup {

    private static final double INITIAL_DISTANCE = 72.0;
    private static final double INITIAL_TIME = 2.0;
    private static final double INITIAL_SPEED = 0.7;

    public GoOverRockWallCommand() {
        // Add Commands here:
        // e.g. addSequential(new Command1());
        // addSequential(new Command2());
        // these will run in order.

        // To run multiple commands at the same time,
        // use addParallel()
        // e.g. addParallel(new Command1());
        // addSequential(new Command2());
        // Command1 and Command2 will run in parallel.

        // A command group will require all of the subsystems that each member
        // would require.
        // e.g. if Command1 requires chassis, and Command2 requires arm,
        // a CommandGroup containing them would require both the chassis and the
        // arm.
        addSequential(new DropDownMoveToAngleCommand(ARM_CROSSING_OBSTACLE_ANGLE));
        addParallel(new LowGearCommand());
        addParallel(new DropDownMoveToAngleCommand(ARM_CROSSING_OBSTACLE_ANGLE));
        addSequential(new DriveForwardCommand(INITIAL_DISTANCE, INITIAL_TIME, INITIAL_SPEED));
        addParallel(new DropDownMoveToAngleCommand(ARM_CROSSING_OBSTACLE_ANGLE));
        addSequential(new DriveOverRockWallCommand());
    }
}
