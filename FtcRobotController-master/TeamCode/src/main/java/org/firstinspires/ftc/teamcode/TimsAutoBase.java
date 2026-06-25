package org.firstinspires.ftc.teamcode;

import static java.lang.Math.toRadians;

import android.os.SystemClock;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

public abstract class TimsAutoBase extends LinearOpMode {

//    static final double  DRIVE_SPEED_20       = 0.20;    // Lower speed for moving from a standstill
//    static final double  DRIVE_SPEED_30       = 0.30;    // Lower speed for fine control going sideways
//    static final double  DRIVE_SPEED_50       = 0.50;    //
//    static final double  DRIVE_SPEED_55       = 0.55;    //
//    static final double  DRIVE_SPEED_70       = 0.70;    //
//    static final double  DRIVE_SPEED_100      = 1.00;    //
//    static final double  TURN_SPEED_20        = 0.15;    //
//    static final double  TURN_SPEED_30        = 0.30;    //
//    static final double  TURN_SPEED_40        = 0.40;    //
//    static final double  TURN_SPEED_50        = 0.50;    //
//    static final double  TURN_SPEED_60        = 0.60;    //
//    static final double  TURN_SPEED_70        = 0.70;    //
//    static final double  TURN_SPEED_80        = 0.80;    //
//    static final double  TURN_SPEED_100       = 1.00;    //

    // Define our hardware class here (so it's available in every Autonomous program we write
    HardwareTimsOdomBalls robot = new HardwareTimsOdomBalls();

    // gamepad controls for changing autonomous options
    boolean gamepad1_circle_last,   gamepad1_circle_now  =false;
    boolean gamepad1_cross_last,    gamepad1_cross_now   =false;
    boolean gamepad1_l_bumper_last, gamepad1_l_bumper_now=false;
    boolean gamepad1_r_bumper_last, gamepad1_r_bumper_now=false;
    boolean gamepad1_dpad_up_last, gamepad1_dpad_up_now = false;
    boolean gamepad1_dpad_down_last, gamepad1_dpad_down_now = false;
    boolean gamepad1_dpad_left_last, gamepad1_dpad_left_now = false;
    boolean gamepad1_dpad_right_last, gamepad1_dpad_right_now = false;
//    static final double MIN_DRIVE_POW = 0.05;    // Minimum speed to move the robot
//    static final double MIN_SPIN_RATE = 0.05;    // Minimum power to turn the robot
//    static final double MIN_DRIVE_MAGNITUDE = Math.sqrt(MIN_DRIVE_POW * MIN_DRIVE_POW + MIN_DRIVE_POW * MIN_DRIVE_POW);
//    double robotGlobalXCoordinatePosition = 0.0;   // inches
//    double robotGlobalYCoordinatePosition = 0.0;   // inches
//    double robotOrientationRadians = 0.0;   // radians 0deg (straight forward)
//    double pos_y = 0, pos_x = 0, pos_angle = 0.0;  // Allows us to specify movement INCREMENTALLY, not ABSOLUTE
//    static final int DRIVE_TO = 1;   // ACCURACY: tighter tolerances, and slows then stops at final position
//    static final int DRIVE_THRU = 2;   // SPEED: looser tolerances, and leave motors running (ready for next command)
    ElapsedTime motionTimer = new ElapsedTime();  // for driving

    public void performEveryLoop () {
        // Get updated motor encoder values (in a single transaction)
        robot.readBulkData();
        // Command the goBilda Pinpoint to update the current position
        robot.odom.update();
        // Get updated odometry x/y/angle values from goBilda Pinpoint
//        Pose2D pos = robot.odom.getPosition();  // x,y pos in inch; heading in radians
//        robotGlobalXCoordinatePosition = pos.getX(DistanceUnit.INCH);
//        robotGlobalYCoordinatePosition = pos.getY(DistanceUnit.INCH);
//        robotOrientationRadians = pos.getHeading(AngleUnit.RADIANS);
        double xPos = robot.odom.getPosX();
        double yPos = robot.odom.getPosY();
        double zPos = robot.odom.getHeading();
    } // performEveryLoop'

    /**
     * Ensure angle is in the range of -PI to +PI (-180 to +180 deg)
     * @param angleRadians
     * @return
     */
    public double AngleWrapRadians( double angleRadians ){
        while( angleRadians < -Math.PI ){
            angleRadians += 2.0*Math.PI;
        }
        while( angleRadians > Math.PI ){
            angleRadians -= 2.0*Math.PI;
        }
        return angleRadians;
    }

    /**
     * Ensure angle is in the range of -180 to +180 deg (-PI to +PI)
     * @param angleDegrees
     * @return
     */
    public double AngleWrapDegrees( double angleDegrees ){
        while( angleDegrees < -180 ) {
            angleDegrees += 360.0;
        }
        while( angleDegrees > 180 ){
            angleDegrees -= 360.0;
        }
        return angleDegrees;
    } // AngleWrapDegrees

    /*---------------------------------------------------------------------------------*/
    protected void captureGamepad1Buttons() {
        gamepad1_circle_last     = gamepad1_circle_now;      gamepad1_circle_now     = gamepad1.circle;
        gamepad1_cross_last      = gamepad1_cross_now;       gamepad1_cross_now      = gamepad1.cross;
        gamepad1_l_bumper_last   = gamepad1_l_bumper_now;    gamepad1_l_bumper_now   = gamepad1.left_bumper;
        gamepad1_r_bumper_last   = gamepad1_r_bumper_now;    gamepad1_r_bumper_now   = gamepad1.right_bumper;
        gamepad1_dpad_up_last    = gamepad1_dpad_up_now;     gamepad1_dpad_up_now    = gamepad1.dpad_up;
        gamepad1_dpad_down_last  = gamepad1_dpad_down_now;   gamepad1_dpad_down_now  = gamepad1.dpad_down;
        gamepad1_dpad_left_last  = gamepad1_dpad_left_now;   gamepad1_dpad_left_now  = gamepad1.dpad_left;
        gamepad1_dpad_right_last = gamepad1_dpad_right_now;  gamepad1_dpad_right_now = gamepad1.dpad_right;
    } // captureGamepad1Buttons

    double xKD = 0.0;
    double xKP = -0.0007;
    double xKI = 0.0;
    double yKD = 0.0;
    double yKP = 0.0007;
    double yKI = 0.0;
    double zKD = .5; //may not even be needed
    double zKP = .020; //luckily 1 on radian mode
    double zKI = 0;
    double lastXError = 0;
    double lastYError = 0;
    double lastZError = 0;
    double xErrorIntegral = 0;
    double yErrorIntegral = 0;
    double zErrorIntegral = 0;
    double minJuice = .03;
    void goTo(double xTarget, double yTarget, double zTarget, double xPos, double yPos, double zPos, double dt) {

            double xError = xTarget - xPos;
            double yError = yTarget - yPos;
            double zError = zTarget - robot.angleWrap(zPos * 180 / 3.14159265);
            // Rate of change of each of the errors. Attempts to prevent overshoot. ROC is short for "Rate Of Change"
            double xErrorROC = (xError - lastXError) / dt;
            double yErrorROC = (yError - lastYError) / dt;
            double zErrorROC = (zError - lastZError) / dt;
            // Integral of each of the errors. Attempts to eliminate persistent errors. Dangerous if ever gets stuck since its value may skyrocket
            xErrorIntegral += xError * dt;
            yErrorIntegral += yError * dt;
            zErrorIntegral += zError * dt;
            //Correction essentially means power or "juice"
            // Say that the robot was far away from the target in the x direction. It's correction should be high. Once it gets close, this correction should decrease to avoid overshoot.
            double xCorrection = xError * xKP + xErrorROC * xKD + xErrorIntegral * xKI;
            double yCorrection = yError * yKP + yErrorROC * yKD + yErrorIntegral * yKI;
            double zCorrection = zError * zKP + zErrorROC * zKD + zErrorIntegral * zKI;
            // If not for this code, it would move the robot relative to its heading, not the game. Standard formulas
            double rotatedXCorrection = xCorrection * Math.cos(-zPos) - yCorrection * Math.sin(-zPos);
            double rotatedYCorrection = xCorrection * Math.sin(-zPos) + yCorrection * Math.cos(-zPos);
            // Mechanum Wheel Math
            double frontRightCorrection = rotatedYCorrection - rotatedXCorrection + zCorrection; //TODO: check if this X-Y CHANGE is right... NOTE: it wasnt
            double frontLeftCorrection = rotatedYCorrection + rotatedXCorrection - zCorrection;
            double rearRightCorrection = rotatedYCorrection + rotatedXCorrection + zCorrection;
            double rearLeftCorrection = rotatedYCorrection - rotatedXCorrection - zCorrection;

            // Ensures that no motor is set to go above 1 (full) power. decreases all others accordingly so motion is smooth
            double maxPower = Math.max(Math.max(Math.abs(rearLeftCorrection), Math.abs(rearRightCorrection)),
                    Math.max(Math.abs(frontLeftCorrection), Math.abs(frontRightCorrection)));
            if (maxPower > 1.0) {
                rearLeftCorrection /= maxPower;
                rearRightCorrection /= maxPower;
                frontLeftCorrection /= maxPower;
                frontRightCorrection /= maxPower;
            }

            //Updates Motor Power
            robot.driveTrainMotors(frontLeftCorrection, frontRightCorrection, rearLeftCorrection, rearRightCorrection);
            //saves the previous error for delta calculations (derivatives/change over time)
            lastXError = xError;
            lastYError = yError;
            lastZError = zError;
    }
} // AutonomousBase
