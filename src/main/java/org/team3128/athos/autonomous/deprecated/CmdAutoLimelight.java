package org.team3128.athos.autonomous.deprecated;

import org.team3128.common.drive.DriveCommandRunning;
import org.team3128.common.hardware.gyroscope.Gyro;
import org.team3128.common.hardware.limelight.Limelight;
import org.team3128.common.hardware.limelight.LimelightKey;
import org.team3128.common.utility.datatypes.PIDConstants;
import org.team3128.common.utility.units.Angle;
import org.team3128.common.utility.units.Length;
import org.team3128.common.vision.CmdHorizontalOffsetFeedbackDrive;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class CmdAutoLimelight extends CommandGroup {

    public CmdAutoLimelight(Limelight ballLimelight, Gyro gyro, PIDConstants visionPID, PIDConstants blindPID, DriveCommandRunning driveCmdRunning) {
        double horizontalOff = ballLimelight.getValue(LimelightKey.HORIZONTAL_OFFSET, 5);
                //Need Pids
                CmdHorizontalOffsetFeedbackDrive horizontalDrive = 
                new CmdHorizontalOffsetFeedbackDrive(gyro, ballLimelight, ballLimelight, driveCmdRunning, 3.5*Length.in, visionPID, -2*Angle.DEGREES, 2.5*Length.ft, 0.6666666666666666666666 * Length.ft, blindPID, 42*Angle.DEGREES);
        addSequential(horizontalDrive);
    }

}