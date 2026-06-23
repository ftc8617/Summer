//package org.firstinspires.ftc.teamcode;
//
//import com.qualcomm.robotcore.hardware.Gamepad;
//
//public class goToFunction {
//    HardwareTimsOdomBalls robot = new HardwareTimsOdomBalls();
//    double xKD;
//    double xKP;
//    double xKI;
//    double yKD;
//    double yKP;
//    double yKI;
//    double zKD;
//    double zKP;
//    double zKI;
//    double lastXError = 0;
//    double lastYError = 0;
//    double lastZError = 0;
//    double xErrorIntegral = 0;
//    double yErrorIntegral = 0;
//    double zErrorIntegral = 0;
//    void goTo(double xTarget, double yTarget, double zTarget, double xPos, double yPos, double zPos, double dt) {
//        if (Math.max(Math.abs(gamepad1.left_stick_x), Math.max(Math.abs(gamepad1.left_stick_y), Math.abs(gamepad1.right_stick_x))) > 0.15) {
//            xErrorIntegral = 0;
//            lastXError = 0;
//            yErrorIntegral = 0;
//            lastYError = 0;
//            zErrorIntegral = 0;
//            lastZError = 0;
//
//            processStandardDriveMode();
//        }
//
//        else {
//
//            double xError = xTarget - xPos;
//            double yError = yTarget - yPos;
//            double zError = robot.angleWrap(zTarget - zPos); // TODO: check this
//            // Rate of change of each of the errors. Attempts to prevent overshoot. ROC is short for "Rate Of Change"
//            double xErrorROC = (xError - lastXError) / dt;
//            double yErrorROC = (yError - lastYError) / dt;
//            double zErrorROC = (zError - lastZError) / dt;
//            // Integral of each of the errors. Attempts to eliminate persistent errors. Dangerous if ever gets stuck since its value may skyrocket
//            xErrorIntegral += xError * dt;
//            yErrorIntegral += yError * dt;
//            zErrorIntegral += zError * dt;
//            //Correction essentially means power or "juice"
//            // Say that the robot was far away from the target in the x direction. It's correction should be high. Once it gets close, this correction should decrease to avoid overshoot.
//            double xCorrection = xError * xKP + xErrorROC * xKD + xErrorIntegral * xKI;
//            double yCorrection = yError * yKP + yErrorROC * yKD + yErrorIntegral * yKI;
//            double zCorrection = zError * zKP + zErrorROC * zKD + zErrorIntegral * zKI;
//            // If not for this code, it would move the robot relative to its heading, not the game. Standard formulas
//            double rotatedXCorrection = xCorrection * Math.cos(-zPos) - yCorrection * Math.sin(-zPos);
//            double rotatedYCorrection = xCorrection * Math.sin(-zPos) + yCorrection * Math.cos(-zPos);
//            // Mechanum Wheel Math
//            double frontRightCorrection = yCorrection - xCorrection + zCorrection;
//            double frontLeftCorrection = yCorrection + xCorrection - zCorrection;
//            double rearRightCorrection = yCorrection + xCorrection + zCorrection;
//            double rearLeftCorrection = yCorrection - xCorrection - zCorrection;
//            // Ensures that no motor is set to go above 1 (full) power. decreases all others accordingly so motion is smooth
//            double maxPower = Math.max(Math.max(Math.abs(rearLeftCorrection), Math.abs(rearRightCorrection)),
//                    Math.max(Math.abs(frontLeftCorrection), Math.abs(frontRightCorrection)));
//            if (maxPower > 1.0) {
//                rearLeftCorrection /= maxPower;
//                rearRightCorrection /= maxPower;
//                frontLeftCorrection /= maxPower;
//                frontRightCorrection /= maxPower;
//            }
//            //Updates Motor Power
//            robot.driveTrainMotors(frontLeftCorrection, frontRightCorrection, rearLeftCorrection, rearRightCorrection);
//            //saves the previous error for delta calculations (derivatives/change over time)
//            lastXError = xError;
//            lastYError = yError;
//            lastZError = zError;
//        }
//    }
//}
