/* FTC Team 8617 - Version 1.0 (03/17/2025)
*/
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.Range;

import java.util.Locale;

@Autonomous(name="TimDoesTricks", group="8617", preselectTeleOp = "TimsANavigator")
//@Disabled
public class TimDoesTricks extends TimsAutoBase {

    boolean debugMode = false;

    int startDelay = 0;
    double tempOffset = 0;
    double pos_y = 0, pos_x = 0, pos_angle = 0.0;  // Allows us to specify movement INCREMENTALLY, not ABSOLUTE
    double yTarget = 0, xTarget = 0, zTarget = 0;
    double odomResetCounter = 251;
    double odomResets = 0;
    double zLeeway = 1;
    double driveToLeeway = 5;
    double driveThroughLeeway = 300;

    @Override
    public void runOpMode() throws InterruptedException {

        telemetry.addData("State", "Initializing (please wait)");
        telemetry.update();

        // Initialize robot hardware (autonomous mode)
        robot.init(hardwareMap, true);


        telemetry.addData("State", "B Dawg ??");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY).  While waiting, poll for options
        while (!isStarted()) {
            performEveryLoop();
            captureGamepad1Buttons();


            // Check for operator input that changes Autonomous options


            //  gamepad1_r_bumper
            telemetry.addLine("Initialized & Looping rn gang");
            telemetry.addData("Start Delay", "%d ms", startDelay);
            telemetry.addData("# of Odom Resets", "%.3f", odomResets);
            telemetry.update();

            if (odomResetCounter > 250){
                robot.odom.resetPosAndIMU();
                odomResetCounter = 0;
                odomResets++;
            }
            else{
                odomResetCounter++;
            }
            robot.neckServo.setPosition(.5);

            //pause brieefly before looping
            idle();

        } // !isStarted

        mainAutonomous();

        telemetry.addData("Program", "Complete");
        telemetry.update();

    } /* runOpMode() */
    private void mainAutonomous() {
   //     sleep(startDelay);

        topLeft(driveThroughLeeway);
        //sleep(1000);
        //lookRight(driveToLeeway);
        //sleep(1000);
        topRight(driveToLeeway);
        lookCenter(driveToLeeway);
        driveCenter(driveToLeeway);
        topLeft(driveToLeeway);
        driveCenter(driveToLeeway);
        topRight(driveThroughLeeway);
        topLeft(driveThroughLeeway);
        driveCenter(driveThroughLeeway);
        topRight(driveThroughLeeway);
        topLeft(driveThroughLeeway);
        driveCenter(driveToLeeway);

        //lookForward(driveToLeeway);


        // ensure motors are turned off even if we run out of time
        robot.driveTrainMotorsZero();
        sleep(999999999);
    } // mainAutonomous

//write movement functions
    public void processNeck(){
        double headingToDegrees = robot.angleWrap(robot.odom.getHeading() * 180 / 3.14159265);
        if (Math.abs(headingToDegrees) - .25 > (330 / 2)){ //TODO: this may be 330 / 2 ... basically the max range of servo / 2
            robot.neckPos = 0.5;
        }
        else {
            robot.neckPos = -headingToDegrees / 330 + 0.5 - 0.25; //TODO: also check this out
        }
        double neckTotal = Range.clip(robot.neckPos, 0.0, 1.0);
        robot.neckServo.setPosition(neckTotal);
    }
    public void topLeft(double leeway) {
        robot.odom.update();
        yTarget = 336;
        xTarget = 1860;
        zTarget = 0;
        while (Math.abs(yTarget - robot.odom.getPosY()) > leeway || Math.abs(xTarget - robot.odom.getPosX()) > leeway || Math.abs(zTarget - robot.angleWrap(robot.odom.getHeading()*180/3.14159265)) > zLeeway){
            robot.odom.update();
            processNeck();
            goTo(yTarget,xTarget,zTarget,robot.odom.getPosY(),robot.odom.getPosX(),robot.odom.getHeading(), 10); // TODO: dt cannot be this if the derivatvie term is every used. don't rlly need it tho
        }
        if (leeway == driveToLeeway){
            robot.driveTrainMotorsZero();
        }
    }

    public void lookRight(double leeway) {
        robot.odom.update();
        yTarget = robot.odom.getPosY();
        xTarget = robot.odom.getPosX();
        zTarget = -90;
        while (Math.abs(yTarget - robot.odom.getPosY()) > leeway || Math.abs(xTarget - robot.odom.getPosX()) > leeway || Math.abs(zTarget - robot.angleWrap(robot.odom.getHeading()*180/3.14159265)) > zLeeway){
            robot.odom.update();
            processNeck();
            goTo(yTarget,xTarget,zTarget,robot.odom.getPosY(),robot.odom.getPosX(),robot.odom.getHeading(), 10); // TODO: dt cannot be this if the derivatvie term is every used. don't rlly need it tho
        }
        if (leeway == driveToLeeway){
            robot.driveTrainMotorsZero();
        }
    }

    public void topRight(double leeway) {
        robot.odom.update();
        yTarget = -1168;
        xTarget = 1851;
        zTarget = -90;
        while (Math.abs(yTarget - robot.odom.getPosY()) > leeway || Math.abs(xTarget - robot.odom.getPosX()) > leeway || Math.abs(zTarget - robot.angleWrap(robot.odom.getHeading()*180/3.14159265)) > zLeeway){
            robot.odom.update();
            processNeck();
            goTo(yTarget,xTarget,zTarget,robot.odom.getPosY(),robot.odom.getPosX(),robot.odom.getHeading(), 10); // TODO: dt cannot be this if the derivatvie term is every used. don't rlly need it tho
        }
        if (leeway == driveToLeeway){
            robot.driveTrainMotorsZero();
        }
    }

    public void lookCenter(double leeway) {
        robot.odom.update();
        yTarget = robot.odom.getPosY();
        xTarget = robot.odom.getPosX();
        zTarget = 0;
        while (Math.abs(yTarget - robot.odom.getPosY()) > leeway || Math.abs(xTarget - robot.odom.getPosX()) > leeway || Math.abs(zTarget - robot.angleWrap(robot.odom.getHeading()*180/3.14159265)) > zLeeway){
            robot.odom.update();
            processNeck();
            goTo(yTarget,xTarget,zTarget,robot.odom.getPosY(),robot.odom.getPosX(),robot.odom.getHeading(), 10); // TODO: dt cannot be this if the derivatvie term is every used. don't rlly need it tho
        }
        if (leeway == driveToLeeway){
            robot.driveTrainMotorsZero();
        }
    }

    public void driveCenter(double leeway) {
        robot.odom.update();
        yTarget = 0;
        xTarget = 0;
        zTarget = 0;
        while (Math.abs(yTarget - robot.odom.getPosY()) > leeway || Math.abs(xTarget - robot.odom.getPosX()) > leeway || Math.abs(zTarget - robot.angleWrap(robot.odom.getHeading()*180/3.14159265)) > zLeeway){
            robot.odom.update();
            processNeck();
            goTo(yTarget,xTarget,zTarget,robot.odom.getPosY(),robot.odom.getPosX(),robot.odom.getHeading(), 10); // TODO: dt cannot be this if the derivatvie term is every used. don't rlly need it tho
        }
        if (leeway == driveToLeeway){
            robot.driveTrainMotorsZero();
        }
    }

    public void lookForward(double leeway) {
        robot.odom.update();
        yTarget = robot.odom.getPosY();
        xTarget = robot.odom.getPosX();
        zTarget = 0;
        while (Math.abs(yTarget - robot.odom.getPosY()) > leeway || Math.abs(xTarget - robot.odom.getPosX()) > leeway || Math.abs(zTarget - robot.angleWrap(robot.odom.getHeading()*180/3.14159265)) > zLeeway){
            robot.odom.update();
            processNeck();
            goTo(yTarget,xTarget,zTarget,robot.odom.getPosY(),robot.odom.getPosX(),robot.odom.getHeading(), 10); // TODO: dt cannot be this if the derivatvie term is every used. don't rlly need it tho
        }
        if (leeway == driveToLeeway){
            robot.driveTrainMotorsZero();
        }
    }







}