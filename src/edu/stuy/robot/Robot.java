package edu.stuy.robot;

import static edu.stuy.robot.RobotMap.JONAH_ID;
import static edu.stuy.robot.RobotMap.SHOOTER_SPEED_LABEL;
import static edu.stuy.robot.RobotMap.YUBIN_ID;

import edu.stuy.robot.commands.ShooterSetOutWorksSpeed;
import edu.stuy.robot.commands.auton.GoOverMoatCommand;
import edu.stuy.robot.commands.auton.GoOverRampartsCommand;
import edu.stuy.robot.commands.auton.GoOverRockWallCommand;
import edu.stuy.robot.commands.auton.GoOverRoughTerrainCommand;
import edu.stuy.robot.commands.auton.PassChevalCommand;
import edu.stuy.robot.commands.auton.PassPortcullisCommand;
import edu.stuy.robot.commands.auton.ReachObstacleCommand;
import edu.stuy.robot.commands.auton.RunLogFileCommand;
import edu.stuy.robot.commands.auton.ShootOuterworkCommand;
import edu.stuy.robot.subsystems.Acquirer;
import edu.stuy.robot.subsystems.BlueSignalLight;
import edu.stuy.robot.subsystems.Drivetrain;
import edu.stuy.robot.subsystems.DropDown;
import edu.stuy.robot.subsystems.Flashlight;
import edu.stuy.robot.subsystems.Hood;
import edu.stuy.robot.subsystems.Hopper;
import edu.stuy.robot.subsystems.Shooter;
import edu.stuy.robot.subsystems.Sonar;
import edu.stuy.util.LogData;
import edu.stuy.util.Recorder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

    /*
     * Subsystems
     */
    public static Hopper hopper;
    public static Drivetrain drivetrain;
    public static Acquirer acquirer;
    public static DropDown dropdown;
    public static Shooter shooter;
    public static Hood hood;
    public static Sonar sonar;
    public static BlueSignalLight cvSignalLight;
    public static Flashlight flashlight;

    public static boolean recording = false;
    public static Recorder recorder;
    /*
     * Operator Interface
     */
    public static OI oi;

    /*
     * Autonomous Variables
     */
    Command autonomousCommand;

    public static SendableChooser debugChooser;
    public static SendableChooser autonChooser;
    public static SendableChooser operatorChooser;
    public static SendableChooser autonPositionChooser;
    public static SendableChooser autonShootChooser;

    public static boolean dontStartCommands;

    private double autonStartTime;
    private boolean debugMode;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        // Setup debug chooser and build the SmartDashboard layout based on the
        // chosen option
        setupDebugChooser();
        debugMode = (Boolean) debugChooser.getSelected();

        // GyroPID
        SmartDashboard.putNumber("Gyro P", 0);
        SmartDashboard.putNumber("Gyro I", 0);
        SmartDashboard.putNumber("Gyro D", 0);

        // Start the operator chooser before anything else
        chooseOperator();

        // Initialize all the subsystems
        drivetrain = new Drivetrain();
        acquirer = new Acquirer();
        dropdown = new DropDown();
        hopper = new Hopper();
        shooter = new Shooter();
        hood = new Hood();
        sonar = new Sonar();
        cvSignalLight = new BlueSignalLight();
        flashlight = new Flashlight();

        oi = new OI();

        recorder = new Recorder();

        dontStartCommands = false;

        drivetrain.setDrivetrainBrakeMode(true);
        shooter.setShooterBrakeMode(false);
        hopper.setHopperBrakeMode(true);
        dropdown.setDropDownBreakMode(true);

        SmartDashboard.putNumber(SHOOTER_SPEED_LABEL, 0.0);

        // Set up the auton chooser
        setupAutonChooser();
        setupAutonPositionChooser();
    }

    /**
     * Maps the relative locations of the obstacles
     */
    private void setupAutonPositionChooser() {
        autonPositionChooser = new SendableChooser();
        SmartDashboard.putString("1", "The low bar is in position 1, not a valid autonomous choice");
        autonPositionChooser.addObject("2", 2);
        autonPositionChooser.addDefault("3", 3);
        autonPositionChooser.addObject("4", 4);
        autonPositionChooser.addObject("5", 5);
        SmartDashboard.putData("Auton Position", autonPositionChooser);
    }
    
    private void shouldShootAfterCross() {
        autonShootChooser = new SendableChooser();
        autonShootChooser.addDefault("No", false);
        autonShootChooser.addObject("Yes", true);
        SmartDashboard.putData("Should shoot after cross?", autonShootChooser);
    }

    private void chooseOperator() {
        operatorChooser = new SendableChooser();
        operatorChooser.addDefault("Jonah", JONAH_ID);
        operatorChooser.addObject("Yubin", YUBIN_ID);
        SmartDashboard.putData("Choose thy Operator Knight!", operatorChooser);
    }

    private void setupAutonChooser() {

        autonChooser = new SendableChooser();
        autonChooser.addDefault("0. Reach edge of obstacle but refrain from going over", new ReachObstacleCommand());
        autonChooser.addObject("1. Do nothing", new CommandGroup());
        autonChooser.addObject("2. Rock Wall", new GoOverRockWallCommand());
        autonChooser.addObject("3. Moat", new GoOverMoatCommand());
        autonChooser.addObject("4. Rough Terrain", new GoOverRoughTerrainCommand());
        autonChooser.addObject("5. Ramparts", new GoOverRampartsCommand());
        autonChooser.addObject("6. Cheval", new PassChevalCommand());
        autonChooser.addObject("7. Portcullis", new PassPortcullisCommand());
        autonChooser.addObject("-1: Run Log File", new RunLogFileCommand());
        SmartDashboard.putData("Auton setting", autonChooser);
    }

    private void setupDebugChooser() {
        debugChooser = new SendableChooser();
        debugChooser.addDefault("Competition Mode", false);
        debugChooser.addObject("Debug Mode", true);
        SmartDashboard.putData("SmartDashboard Mode", debugChooser);
    }

    public void disabledPeriodic() {
        Scheduler.getInstance().run();
    }

    public void autonomousInit() {
        debugMode = (Boolean) debugChooser.getSelected();
        Command selectedCommand = (Command) autonChooser.getSelected();
        int autonPosition = (Integer) autonPositionChooser.getSelected();
        Boolean autonShoot = (Boolean) autonShootChooser.getSelected();
        autonomousCommand = selectedCommand;
        if (autonShoot) {
            new ShootOuterworkCommand(autonomousCommand).start();
        } else {
            autonomousCommand.start();
        }
        Robot.drivetrain.resetEncoders();
        autonStartTime = Timer.getFPGATimestamp();
    }

    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
        if (debugMode) {
            SmartDashboard.putNumber("drivetrain left encoder", Robot.drivetrain.getLeftEncoder());
            SmartDashboard.putNumber("drivetrain right encoder", Robot.drivetrain.getRightEncoder());
            SmartDashboard.putNumber("Max distance of drivetrain encoders", Robot.drivetrain.getDistance());
            SmartDashboard.putNumber("potentiometer", Robot.dropdown.getAngle());
            SmartDashboard.putNumber("Potentiometer voltage", Robot.dropdown.getVoltage());
        }
        // This block is here instead of teleop init to save battery voltage
        // because teleop init does not run immediately after auton disables
        if (Timer.getFPGATimestamp() - autonStartTime > 14) {
            Robot.shooter.stop();
            Robot.hopper.stop();
        }
    }

    public void teleopInit() {
        // This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to
        // continue until interrupted by another command, remove
        // this line or comment it out.
        if (autonomousCommand != null) {
            autonomousCommand.cancel();
        }
        debugMode = (Boolean) debugChooser.getSelected();
        Robot.drivetrain.resetEncoders();
        // This is here and also in autonomus periodic as a safety measure
        Robot.shooter.stop();
    }

    /**
     * This function is called when the disabled button is hit. You can use it
     * to reset subsystems before shutting down.
     */
    public void disabledInit() {
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        /// HANDLE RECORDER
        if (oi.operatorGamepad.getDPadLeft().get()) {
            //TODO: Find buttons that work.
            recording = true;
        }
        if (oi.operatorGamepad.getDPadRight().get()) {
            //TODO: Find buttons that work.
            if (recording) {
                recorder.writeToFile(RobotMap.LOG_FILE_NAME);
            }
            recording = false;
        } 
        if (recording) {
            recorder.update();
        }

        Scheduler.getInstance().run();
        if (debugMode) {
            // SmartDashboard.putNumber("gyro",
            // Robot.drivetrain.getGyroAngle());
            SmartDashboard.putNumber("Current Shooter Motor Speed:", Robot.shooter.getCurrentMotorSpeedInRPM());
            SmartDashboard.putNumber("drivetrain left encoder", Robot.drivetrain.getLeftEncoder());
            SmartDashboard.putNumber("drivetrain right encoder", Robot.drivetrain.getRightEncoder());
            SmartDashboard.putBoolean("Gear shift override", drivetrain.overrideAutoGearShifting);
            try {
                SmartDashboard.putNumber("Hopper Sensor", Robot.hopper.getDistance());
            } catch (Exception e) {
                SmartDashboard.putNumber("Hopper Sensor", -1.0);
            }
            // Sonar:
            double[] sonarData = sonar.getData();
            SmartDashboard.putNumber("Sonar L", sonarData[0]);
            SmartDashboard.putNumber("Sonar R", sonarData[1]);

            // Solenoids:
            SmartDashboard.putBoolean("Hood piston", Robot.hood.getState());
            SmartDashboard.putBoolean("Gear shift solenoid", Robot.drivetrain.gearUp);

            // Thresholds:
            SmartDashboard.putNumber("Gear Shifting Threshold", 40);
        }
        SmartDashboard.putNumber("potentiometer", Robot.dropdown.getAngle());
        SmartDashboard.putNumber("Potentiometer voltage", Robot.dropdown.getVoltage());

    }

    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        LiveWindow.run();
    }
}
