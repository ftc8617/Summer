/* FTC Team 8617 - Version 1.0 (03/17/2025)
*/
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

import java.util.Locale;

@Autonomous(name="Big Triangle Auto", group="8617", preselectTeleOp = "TeleDecode")
//@Disabled
public class AutonomousGargantuanTriangle extends AutonomousBase {
    boolean debugMode = false;

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

            // Check for operator input that changes Autonomous options


             //  gamepad1_r_bumper
            telemetry.addLine("Initialized & Looping rn gang");
            String posStr = String.format(Locale.US, "{X,Y: %.1f, %.1f in  H: %.1f deg}", robotGlobalXCoordinatePosition, robotGlobalYCoordinatePosition,Math.toDegrees(robotOrientationRadians));
            telemetry.addData("Position", posStr);
            telemetry.addData("Offset", "%.3f counts", robot.turntableOffset);
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
        startBackpedaling();
        
        // ensure motors are turned off even if we run out of time
        robot.driveTrainMotorsZero();
        sleep(999999999);
    } // mainAutonomous

//write movement functions

    private void startBackpedaling () {
        processPigChucker(1,0);
        processIntake(false);
        sleep(500);
        processTurntable(1);
        sleep(1000);
        processIntake(true);
        //driveStraight(-.4);
        //driveToPosition(0,5,0,DRIVE_SPEED_20,TURN_SPEED_20,DRIVE_TO);

        sleep(2100);
        //driveStraight(0);
        sleep(3000);
        shootThree();
        sleep(500);
        robot.frontLeftMotor.setPower(-0.4);
        robot.frontRightMotor.setPower(0.4);
        robot.rearLeftMotor.setPower(0.4);
        robot.rearRightMotor.setPower(0.4);
        sleep(400);
        driveStraight(0);
    }

    private void driveStraight(double power) {
        robot.frontLeftMotor.setPower(power);
        robot.frontRightMotor.setPower(power);
        robot.rearLeftMotor.setPower(power);
        robot.rearRightMotor.setPower(power);
    }

    private void shootThree() {
        processTurntable(1);
        sleep(1400);;
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
           robot.pigChucker.setPower(.75);
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
       tempOffset=0;
       if(gamepad1_r_bumper_now && !gamepad1_r_bumper_last){
           tempOffset += 0.002;
       } else if (gamepad1_l_bumper_now && !gamepad1_l_bumper_last){
           tempOffset -= 0.002;
       }
       robot.turntableOffset += tempOffset;
       robot.turntableUpdate(1);

   }

} /* AutonomousGargantuanTriangle */
