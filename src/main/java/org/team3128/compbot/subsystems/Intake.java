package org.team3128.compbot.subsystems;

import org.team3128.common.utility.Log;

import edu.wpi.first.wpilibj.DigitalInput;

import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.team3128.common.generics.Threaded;
import org.team3128.common.hardware.motor.LazyCANSparkMax;

public class Intake extends Threaded {

    public static final Intake instance = new Intake();
    LazyCANSparkMax INTAKE_MOTOR;

    public static Intake getInstance() {
        return instance;
    }

    private Intake() {
        configMotors();
    }

    private void configMotors() {
        INTAKE_MOTOR = new LazyCANSparkMax(Constants.IntakeConstants.INTAKE_MOTOR_ID, MotorType.kBrushless);
    }

    public void turnOn() {
        INTAKE_MOTOR.set(Constants.IntakeConstants.INTAKE_MOTOR_ON_VALUE);
    }

    public void turnOnBackwards() {
        INTAKE_MOTOR.set(-Constants.IntakeConstants.INTAKE_MOTOR_ON_VALUE);
    }

    public void turnOff() {
        INTAKE_MOTOR.set(Constants.IntakeConstants.INTAKE_MOTOR_OFF_VALUE);
    }

    @Override
    public void update() {
        // nothing needed here
    }

}