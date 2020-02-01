package org.team3128.testbench.main;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import org.team3128.common.generics.RobotConstants;

import org.team3128.common.NarwhalRobot;
import org.team3128.common.control.trajectory.Trajectory;
import org.team3128.common.control.trajectory.TrajectoryGenerator;
import org.team3128.common.control.trajectory.constraint.TrajectoryConstraint;
import org.team3128.common.drive.DriveCommandRunning;
import org.team3128.common.hardware.limelight.Limelight;
import org.team3128.common.hardware.gyroscope.Gyro;
import org.team3128.common.hardware.gyroscope.NavX;
import org.team3128.common.utility.units.Angle;
import org.team3128.common.utility.units.Length;
import org.team3128.common.vision.CmdHorizontalOffsetFeedbackDrive;
import org.team3128.athos.subsystems.Constants;
import org.team3128.common.utility.Log;
import org.team3128.common.utility.RobotMath;
import org.team3128.common.utility.datatypes.PIDConstants;
import org.team3128.common.narwhaldashboard.NarwhalDashboard;
import org.team3128.common.listener.ListenerManager;
import org.team3128.common.listener.controllers.ControllerExtreme3D;
import org.team3128.common.listener.controltypes.Button;
import org.team3128.common.hardware.motor.LazyCANSparkMax;
import org.team3128.common.utility.math.Pose2D;
import org.team3128.common.utility.math.Rotation2D;
import org.team3128.testbench.subsystems.NEODrive;
import org.team3128.testbench.subsystems.RobotTracker;
import org.team3128.common.drive.DriveSignal;

import com.revrobotics.CANSparkMaxLowLevel.MotorType;


import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import org.team3128.common.hardware.motor.LazyCANSparkMax;
import org.team3128.common.generics.Threaded;
import org.team3128.common.control.RateLimiter;
import org.team3128.common.control.AsynchronousPid;
import org.team3128.common.control.motion.RamseteController;
import org.team3128.common.control.trajectory.Trajectory;
import org.team3128.common.control.trajectory.Trajectory.State;
import org.team3128.common.drive.AutoDriveSignal;
import org.team3128.common.drive.DriveSignal;
import org.team3128.common.utility.math.Rotation2D;
import org.team3128.common.utility.NarwhalUtility;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;

import com.revrobotics.*;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import org.team3128.common.hardware.motor.LazyCANSparkMax;
import org.team3128.common.utility.RobotMath;

import java.util.ArrayList;
import java.util.concurrent.*;

import org.team3128.common.generics.ThreadScheduler;

public class MainTestBench extends NarwhalRobot {
    // NEODrive drive = NEODrive.getInstance();
    // RobotTracker robotTracker = RobotTracker.getInstance();

    public LazyCANSparkMax shooterMotor, shooterMotor1, shooterMotor2; 
    private CANEncoder shooterEncoder;

    ExecutorService executor = Executors.newFixedThreadPool(4);
    ThreadScheduler scheduler = new ThreadScheduler();
    Thread auto;

    public DigitalInput digitalInput;
    public DigitalInput digitalInput2;

    public Joystick joystick;
    public ListenerManager lm;
    
    public NetworkTable table;
    public NetworkTable limelightTable;

    public double startTime = 0;

    public String trackerCSV = "Time, X, Y, Theta, Xdes, Ydes";

    public boolean inPlace = false;
    public boolean inPlace2 = false;

    public int countBalls = 0;
    public int countBalls2 = 0;

    @Override
    protected void constructHardware() {

        digitalInput = new DigitalInput(0);
        digitalInput2 = new DigitalInput(1);


		shooterMotor = new LazyCANSparkMax(2, MotorType.kBrushless);
		shooterMotor1 = new LazyCANSparkMax(5, MotorType.kBrushless);
        shooterMotor2 = new LazyCANSparkMax(11, MotorType.kBrushless);
        
        shooterEncoder = shooterMotor1.getEncoder();


        joystick = new Joystick(1);
        lm = new ListenerManager(joystick);
        addListenerManager(lm);

        // straight
        // waypoints.add(new Pose2D(0, 0, Rotation2D.fromDegrees(180)));
        // waypoints.add(new Pose2D(60 * Constants.inchesToMeters, 0 *
        // Constants.inchesToMeters, Rotation2D.fromDegrees(0)));

        // quarterturn
        // waypoints.add(new Pose2D(0, 0, Rotation2D.fromDegrees(0)));
        // waypoints.add(new Pose2D(60 * Constants.inchesToMeters, 60 *
        // Constants.inchesToMeters, Rotation2D.fromDegrees(90)));

        // waypoints.add(new Pose2D(0, 0, Rotation2D.fromDegrees(0)));
        // waypoints.add(new Pose2D(60 * Constants.inchesToMeters, 30 *
        // Constants.inchesToMeters, Rotation2D.fromDegrees(45)));

        // waypoints.add(new Pose2D(0, 0, Rotation2D.fromDegrees(0)));
        // waypoints.add(new Pose2D(30 * Constants.inchesToMeters, 0 *
        // Constants.inchesToMeters, Rotation2D.fromDegrees(0)));
        // waypoints.add(new Pose2D(0, 0, Rotation2D.fromDegrees(0)));
        // waypoints.add(new Pose2D(70 * Constants.inchesToMeters, 40 *
        // Constants.inchesToMeters, Rotation2D.fromDegrees(20)));
        // waypoints.add(new Pose2D(140 * Constants.inchesToMeters, 85 *
        // Constants.inchesToMeters, Rotation2D.fromDegrees(45)));
        // waypoints.add(
        //         new Pose2D(0 * Constants.inchesToMeters, 70 * Constants.inchesToMeters, Rotation2D.fromDegrees(-45)));

        // trajectory = TrajectoryGenerator.generateTrajectory(waypoints, new ArrayList<TrajectoryConstraint>(), 0, 0,
        //         120 * Constants.inchesToMeters, 0.5, false);
    }

    @Override
    protected void constructAutoPrograms() {
    }

    @Override
    protected void setupListeners() {
        lm.nameControl(ControllerExtreme3D.TWIST, "MoveTurn");
        lm.nameControl(ControllerExtreme3D.JOYY, "MoveForwards");
        lm.nameControl(ControllerExtreme3D.THROTTLE, "Throttle");
        lm.nameControl(new Button(6), "PrintCSV");
        lm.nameControl(new Button(3), "ClearTracker");
        lm.nameControl(new Button(4), "ClearCSV");
        lm.nameControl(new Button(7), "SetPower");

        lm.addMultiListener(() -> {
            // drive.arcadeDrive(-0.7 * RobotMath.thresh(lm.getAxis("MoveTurn"), 0.1),
            //         -1.0 * RobotMath.thresh(lm.getAxis("MoveForwards"), 0.1), -1.0 * lm.getAxis("Throttle"), true);

        }, "MoveTurn", "MoveForwards", "Throttle");

        lm.nameControl(ControllerExtreme3D.TRIGGER, "AlignToTarget");
        lm.addButtonDownListener("AlignToTarget", () -> {
            // TODO: Add current implementation of vision alignment
            Log.info("MainAthos.java", "[Vision Alignment] Not created yet, would've started");
        });
        lm.addButtonUpListener("AlignToTarget", () -> {
            Log.info("MainAthos.java", "[Vision Alignment] Not created yet, would've ended");
        });

        lm.addButtonDownListener("SetPower", () -> {

        });

        lm.addButtonDownListener("PrintCSV", () -> {
            Log.info("MainAthos", trackerCSV);
        });
        lm.addButtonDownListener("ClearCSV", () -> {
            trackerCSV = "Time, X, Y, Theta, Xdes, Ydes";
            Log.info("MainAthos", "CSV CLEARED");
            startTime = Timer.getFPGATimestamp();
        });

        lm.addButtonDownListener("ClearTracker", () -> {
            // robotTracker.resetOdometry();
        });

    }

    @Override
    protected void teleopPeriodic() {
        if (inPlace == false && digitalInput.get()){
            countBalls++;
            System.out.println("Number of balls: " + countBalls);
            inPlace = true;
        }
        else if (!digitalInput.get()) {
            inPlace = false;
        }
        
        if (inPlace2 == false && digitalInput2.get()){
            countBalls--;
            System.out.println("Number of balls: " + countBalls);
            inPlace2 = true;
        }
        else if (!digitalInput2.get()) {
            inPlace2 = false;
        }
    }

    @Override
    protected void updateDashboard() {
        
    }

    @Override
    protected void teleopInit() {

    }

    @Override
    protected void autonomousInit() {
    
    }

    @Override
    protected void autonomousPeriodic() {
        shooterMotor1.set(-0.8);
        shooterMotor2.set(0.8);
        Log.info("", "[Velocity] " + String.valueOf(shooterEncoder.getVelocity()));
        shooterMotor.set(0.7);
    }

    @Override
    protected void disabledInit() {
        shooterMotor1.set(0);
        shooterMotor2.set(0);
        shooterMotor.set(0);
        //scheduler.pause();
    }

    public static void main(String... args) {
        RobotBase.startRobot(MainTestBench::new);
    }

    @Override
    public void endCompetition() {
        // TODO Auto-generated method stub

    }
}