package edu.stuy.robot.subsystems;

import static edu.stuy.robot.RobotMap.ACQUIRER_MOTOR_CHANNEL;
import static edu.stuy.robot.RobotMap.ACQUIRER_POTENTIOMETER_CHANNEL;

import edu.stuy.robot.RobotMap;
import edu.stuy.robot.commands.AcquirerStopCommand;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;

/**
 *
 */
public class Acquirer extends Subsystem {
	double beforeTime;
	double changeHelperX;
	double changeHelperY;
	double MAX_DECELERATION;
	private CANTalon acquirerMotor;
	private Potentiometer potentiometer;
	private String outString = "";

	// Put methods for controlling this subsystem
	// here. Call these from Commands.

	public Acquirer() {
		acquirerMotor = new CANTalon(ACQUIRER_MOTOR_CHANNEL);
		potentiometer = new AnalogPotentiometer(ACQUIRER_POTENTIOMETER_CHANNEL,
				300, 0);
	}

	// Used for auton
	public void move(double speed) {
		acquirerMotor.set(speed);
	}

	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		setDefaultCommand(new AcquirerStopCommand());
	}

	public void acquire() {
		acquirerMotor.set(1.0);
	}

	public void stop() {
		acquirerMotor.set(0.0);
	}

	public void deacquire() {
		acquirerMotor.set(-1.0);
	}

	public double getVoltage() {
		return potentiometer.get();
	}

	public double getAngle() {
		double x = getVoltage();
		return (x - 18) * 15;
	}

	public void lowerAcquirerToDrivingPosition() {
		// TODO Auto-generated method stub
		acquirerMotor.set(0.25);
	}

	private double getSlope(double ax, double ay, double bx, double by) {
		return (by - ay) / (bx - ax);
	}

	private double getTangent(double ax, double ay) {
		double j = getSlope(changeHelperX, changeHelperY, ax, ay);
		beforeTime = changeHelperX;
		// changeHelperX = ax;
		// changeHelperY = ay;
		return j;
	}

	private double decelerate(double currentTime, double currentSpeed) {
		double j = getTangent(currentTime, currentSpeed);
		if (-RobotMap.EPSILON < j && j < RobotMap.EPSILON) {
			System.out.println("worked");
		}
		j = j - (MAX_DECELERATION * (currentTime - beforeTime));
		j = j * (currentTime - beforeTime);
		return currentSpeed + j;
	}
}
