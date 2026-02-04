/* FTC Team 8617 - Version 1.0 (03/17/2025)
*/
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import java.util.Locale;

@Autonomous(name="Small Triangle Auto", group="8617", preselectTeleOp = "TeleDecode")
//@Disabled
public class AutonomousTinyTriangle extends AutonomousBase {

    public void turnToHeading(double targetAngle, double maxPower) {
        double kP = 0.01;

        double error;
        double turnPower;

        while (opModeIsActive()) {
            double currentAngle = robot.headingIMU();
            error = robot.angleWrap(targetAngle - currentAngle);

            if(Math.abs(error) < 1.0) {
                break;
            }

            turnPower = error * kP;
            turnPower = Math.max(-maxPower, Math.min(turnPower, maxPower));

            robot.frontLeftMotor.setPower(-turnPower);
            robot.rearLeftMotor.setPower(-turnPower);
            robot.frontRightMotor.setPower(turnPower);
            robot.rearRightMotor.setPower(turnPower);
        }

        robot.stopMotion();
    }
    boolean debugMode = false;

    int startDelay = 0;

    boolean blue = true; // true is blue, false is red
    double tempOffset = 0;
    double pos_y=0, pos_x=0, pos_angle=0.0;  // Allows us to specify movement INCREMENTALLY, not ABSOLUTE

    @Override
    public void runOpMode() throws InterruptedException {

        telemetry.addData("State", "Initializing (please wait)");
        telemetry.update();

        // Initialize robot hardware (autonomous mode)
        robot.init(hardwareMap,true);


        telemetry.addData("State", "B Dawg ??");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY).  While waiting, poll for options
        while (!isStarted()) {
            performEveryLoop();
            captureGamepad1Buttons();

            initTableTuning();
            if(gamepad1_dpad_right_now && !gamepad1_dpad_right_last){
                robot.turntableSlot += 1;
            } else if (gamepad1_dpad_left_now && !gamepad1_dpad_left_last){
                robot.turntableSlot -= 1;
            }
            robot.turntableUpdate(robot.turntableSlot);

            if(gamepad1_dpad_up_now && !gamepad1_dpad_up_last){
                startDelay += 1000;
            } else if (gamepad1_dpad_down_now && !gamepad1_dpad_down_last){
                startDelay -= 1000;
            }

            if(gamepad1_circle_last && !gamepad1_circle_now){
                blue = !blue;
            }

            // Check for operator input that changes Autonomous options


             //  gamepad1_r_bumper
            telemetry.addLine("Initialized & Looping rn gang");
            String posStr = String.format(Locale.US, "{X,Y: %.1f, %.1f in  H: %.1f deg}", robotGlobalXCoordinatePosition, robotGlobalYCoordinatePosition,Math.toDegrees(robotOrientationRadians));
            telemetry.addData("Position", posStr);
            telemetry.addData("Offset", "%.3f counts", robot.turntableOffset);
            telemetry.addData("Start Delay", "%d ms", startDelay);
            telemetry.addData("Blue Side", "%b", blue);
            telemetry.update();
            //pause brieefly before looping
            idle();

        } // !isStarted

        mainAutonomous();

        telemetry.addData("Program", "Complete");
        telemetry.update();

    } /* runOpMode() */

    /*--------------------------------------------------------------------------------------------*/
    /* Autonomous Right:                                                                          */
    /*   1 Starting Point                                                                         */
    /*   2 Hang pre-load specimen at submersible                                                  */
    /*   3 Herd samples from spike marks (left/center/wall)                                       */
    /*   4 Grab clipped specimen from observation zone wall                                       */
    /*   5 Hang specimen on high rung (repeat steps 4 & 5)                                        */
    /*   6 Park in observation zone                                                              */
    /*--------------------------------------------------------------------------------------------*/
    private void mainAutonomous() {
        sleep(startDelay);
        strafeAndShoot();

        
        // ensure motors are turned off even if we run out of time
        robot.driveTrainMotorsZero();
        sleep(999999999);
    } // mainAutonomous

//write movement functions

    private void strafeAndShoot () {
        processPigChucker(2,0);
        processTurntable(1);
        if (blue){
            strafe(1,2);
        } else {
            strafe(1,1);
        }
        sleep(420);
        driveStraight(0);
        sleep(5000);
        shootThree();
        sleep(1000);
        driveStraight(0);
        //sleep(500);
    }

    private void driveStraight(double power) {
        robot.frontLeftMotor.setPower(power);
        robot.frontRightMotor.setPower(power);
        robot.rearLeftMotor.setPower(power);
        robot.rearRightMotor.setPower(power);
    }

    private void strafe(double power, int direction){
        if (direction == 1) { //strafe left
            robot.frontLeftMotor.setPower(-power);
            robot.frontRightMotor.setPower(power);
            robot.rearLeftMotor.setPower(power);
            robot.rearRightMotor.setPower(-power);
        } else if (direction == 2){ //strafe right
            robot.frontLeftMotor.setPower(power);
            robot.frontRightMotor.setPower(-power);
            robot.rearLeftMotor.setPower(-power);
            robot.rearRightMotor.setPower(power);
        }
    }

    private void shootThree() {
        processKicker(false);
        processTurntable(1);
        sleep(500);
        processKicker(true);
        sleep(500);
        processKicker(false);
        sleep(100);
        processTurntable(2);
        sleep(500);
        processKicker(true);
        sleep(500);
        processKicker(false);
        sleep(200);
        processTurntable(3);
        sleep(500);
        processKicker(true);
        sleep(300);
        processKicker(false);

        /*
        processTurntable(1);
        sleep(1400);
        processKicker(true);
        sleep(1400);
        processKicker(false);
        sleep(2000);

        processTurntable(2);
        sleep(1400);

        processKicker(true);
        sleep(1400);
        processKicker(false);
        sleep(2000);

        processTurntable(3);
        sleep(1400);
        processKicker(true);
        sleep(1000);
        processKicker(false);
        */
    }

    /*--------------------------------------------------------------------------------------------*/

    //awesome and cool manipulator functions ------_______----_____ :> peter the programming python approves
   private void processTurntable(int slot){
       robot.turntableSlot = slot;

       robot.turntableUpdate(slot);
   }

   private void processPigChucker(int preset, double power) {
       if (preset == 0){ //manual speed setting
           robot.pigChucker.setPower(power);
       } else if (preset == 1){
           robot.pigChucker.setPower(.63);
       } else if (preset == 2){
           robot.pigChucker.setPower(.72);
       }
   }

   private void processKicker(boolean up){
       robot.flipperUp = up;
       if (robot.flipperUp){
           robot.flipperPos = .3;
       } else {
           robot.flipperPos = .76;
       }

       robot.flipperServo.setPosition(robot.flipperPos);
   }

   private void processIntake(boolean intake){
       if(intake){
           robot.intakeMotor.setPower(-1);
       } else {
           robot.intakeMotor.setPower(0);
       }
   }

   private void initTableTuning(){
       tempOffset = 0;
       if(gamepad1_r_bumper_now && !gamepad1_r_bumper_last){
           tempOffset += 0.002;
       } else if (gamepad1_l_bumper_now && !gamepad1_l_bumper_last){
           tempOffset -= 0.002;
       }
       robot.turntableOffset += tempOffset;
       robot.turntableUpdate(1);

   }

} /* AutonomousGargantuanTriangle */
