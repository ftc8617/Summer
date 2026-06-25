/* FTC Team 8617 - Version 1.0 (10/01/2022)
 weird terminal thing. copy and past the following line if its opening up the weird virtual phone:
 & "C:\Users\garre\AppData\Local\Android\Sdk\platform-tools\adb.exe" connect 192.168.43.1:5555
 NOTE: the space where "garre" is needs to be updated to your own file names.
 NOTE: Better to unstall the ADB Wi-Fi plugin by Yury Pylkov, restart your device, connect to robot, put in address (192.168.43.1) and bingo bang it auto does it
 * */
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;

/**
 * TeleOp (with test modes).
 */
@TeleOp(name="Tim's A Navigator", group="8617")
public class TimsANavigator extends LinearOpMode {
    boolean gamepad1_triangle_last,   gamepad1_triangle_now   = false;
    boolean gamepad1_circle_last,     gamepad1_circle_now     = false;
    boolean gamepad1_cross_last,      gamepad1_cross_now      = false;
    boolean gamepad1_square_last,     gamepad1_square_now     = false;
    boolean gamepad1_dpad_up_last,    gamepad1_dpad_up_now    = false;
    boolean gamepad1_dpad_down_last,  gamepad1_dpad_down_now  = false;
    boolean gamepad1_dpad_left_last,  gamepad1_dpad_left_now  = false;
    boolean gamepad1_dpad_right_last, gamepad1_dpad_right_now = false;
    boolean gamepad1_l_bumper_last,   gamepad1_l_bumper_now   = false;
    boolean gamepad1_r_bumper_last,   gamepad1_r_bumper_now   = false;

    boolean gamepad2_triangle_last,   gamepad2_triangle_now   = false;  //
    boolean gamepad2_circle_last,     gamepad2_circle_now     = false;  //
    boolean gamepad2_cross_last,      gamepad2_cross_now      = false;  //
    boolean gamepad2_square_last,     gamepad2_square_now     = false;  //
    boolean gamepad2_dpad_up_last,    gamepad2_dpad_up_now    = false;  //
    boolean gamepad2_dpad_down_last,  gamepad2_dpad_down_now  = false;  //
    boolean gamepad2_dpad_left_last,  gamepad2_dpad_left_now  = false;  //
    boolean gamepad2_dpad_right_last, gamepad2_dpad_right_now = false;  //
    boolean gamepad2_l_bumper_last,   gamepad2_l_bumper_now   = false;  //
    boolean gamepad2_r_bumper_last,   gamepad2_r_bumper_now   = false;  //
    boolean gamepad2_touchpad_last,   gamepad2_touchpad_now   = false;  //
    boolean gamepad2_share_last,      gamepad2_share_now      = false;  //

    double  yTranslation, xTranslation, rotation;                  /* Driver control inputs */
    double  rearLeft, rearRight, frontLeft, frontRight, maxPower;  /* Motor power levels */
    boolean controlMultSegLinear = true;


    long      nanoTimeCurr=0, nanoTimePrev=0;
    double    elapsedTime, elapsedHz;

    double curX, curY, curAngle;
    double minX=0.0, maxX=0.0, minY=0.0, maxY=0.0;
    double xTarget = 0, yTarget = 0, zTarget = 0;
    boolean navigating = false;
    boolean neckSeeking = false;

    //rumble settings
    Gamepad.RumbleEffect shortRumble;
    Gamepad.RumbleEffect leftRumble;

    Gamepad.RumbleEffect leftDoubleRumble;
    Gamepad.RumbleEffect rightRumble;
    Gamepad.RumbleEffect rightDoubleRumble;

    /* Declare OpMode members. */
    HardwareTimsOdomBalls robot = new HardwareTimsOdomBalls();


    @Override
    public void runOpMode() throws InterruptedException {

        telemetry.addData("State", "Initializing (please wait)");
        telemetry.update();

        //creating rumble effects
        shortRumble = new Gamepad.RumbleEffect.Builder()
                .addStep(1,1,200) //rumble both sides for 200ms
                .build();

        leftRumble = new Gamepad.RumbleEffect.Builder()
                .addStep(1,0,200)
                .build();
        leftDoubleRumble = new Gamepad.RumbleEffect.Builder()
                .addStep(1,0,200)
                .addStep(0,0,100)
                .addStep(1,0,200)
                .build();

        rightRumble = new Gamepad.RumbleEffect.Builder()
                .addStep(0,1,200)
                .build();
        rightDoubleRumble = new Gamepad.RumbleEffect.Builder()
                .addStep(0,1,200)
                .addStep(0,0,100)
                .addStep(0,1,200)
                .build();

        // Initialize robot hardware (not autonomous mode)
        robot.init(hardwareMap,false);


        // Preload and initialize

//        robot.pigChucker.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        robot.intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("State", "austen james richardson");
        telemetry.addLine("Press X (cross) to reset encoders");
        telemetry.addLine("(to run Teleop without Auto first)");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        while (!isStarted()) {
            // Check for operator input that changes Autonomous options
            captureGamepad1Buttons();
            // Normally autonomous resets encoders.  Do we need to for teleop??
            if( gamepad1_cross_now && !gamepad1_cross_last) {
                //               robot.resetEncoders();
                continue;
            }
            // Pause briefly before looping
            idle();
        } // !isStarted

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive())
        {
            // Refresh gamepad button status
            captureGamepad1Buttons();
            captureGamepad2Buttons();

            // Bulk-refresh the Control/Expansion Hub device status (motor status, digital I/O) -- FASTER!
            robot.readBulkData();



            if( processDpadDriveMode() == false ) {
                // Control based on joystick; report the sensed values
                telemetry.addData("Joystick", "x=%.3f, y=%.3f spin=%.3f",
                        gamepad1.left_stick_x, -gamepad1.left_stick_y, gamepad1.right_stick_x );
                //processStandardDriveMode();
            } // processDpadDriveMode

            encoderReset();

            //add gameplay functions
            processNeck();
            updateTargetPos();
            goTo(yTarget,xTarget,zTarget,robot.odom.getPosY(),robot.odom.getPosX(),robot.odom.getHeading(),elapsedTime);
            //TODO: x and y are prolly swithced. change the code dummy
            resetOdom();

            // Compute current cycle time
            nanoTimePrev = nanoTimeCurr;
            nanoTimeCurr = System.nanoTime();
            elapsedTime  = (nanoTimeCurr - nanoTimePrev)/ 1000000.0;   // msec
            elapsedHz    =  1000.0 / elapsedTime;

            // Update telemetry data
            telemetry.addData("Front", "%.2f (%.0f cts/sec) %.2f (%.0f cts/sec)",
                    frontLeft, robot.frontLeftMotorVel, frontRight, robot.frontRightMotorVel );
            telemetry.addData("Rear ", "%.2f (%.0f cts/sec) %.2f (%.0f cts/sec)",
                    rearLeft,  robot.rearLeftMotorVel,  rearRight,  robot.rearRightMotorVel );
            telemetry.addData("Front", "%d %d counts", robot.frontLeftMotorPos, robot.frontRightMotorPos );
            telemetry.addData("Back ", "%d %d counts", robot.rearLeftMotorPos,  robot.rearRightMotorPos );
            /* old telemetry, replace
//          telemetry.addData("Slide ", "%d %d counts", robot.slideLMotorPos,  robot.slideRMotorPos );
            telemetry.addData("Slide ", "%d %d cts (%.4f %.4f)", robot.slideLMotorPos,  robot.slideRMotorPos, robot.slideLMotor.getPower(), robot.slideRMotor.getPower() );
//          telemetry.addData("Chain ", "%d counts", robot.chainMotorPos );
            telemetry.addData("Chain ", "%d counts (%.1f)", robot.chainMotorPos, robot.chainMotor.getPower() );
            telemetry.addData("Claw ", "%.3f counts", robot.clawPos );
            telemetry.addData("Diffy ", "%.3f %.3f counts", robot.diffyLPos, robot.diffyRPos );
            */
//            telemetry.addData("Shooter ", "%.3f speed", robot.pigChucker.getPower());
//
//            telemetry.addData("Kicker ", "%.3f counts", robot.flipperPos);
//            telemetry.addData("Turntable ", "%.3f counts", robot.turntablePos);

            telemetry.addData("CycleTime", "%.1f msec (%.1f Hz)", elapsedTime, elapsedHz );
            telemetry.addData("version","103");

            telemetry.addData("CommandedNeckPos", "%.3f counts" , robot.neckServo.getPosition());
            robot.odom.update();
            telemetry.addData("xPos", "%.3f", robot.odom.getPosX());
            telemetry.addData("yPos", "%.3f", robot.odom.getPosY());
            telemetry.addData("zPos", "%.3f", robot.angleWrap(robot.odom.getHeading()*180/3.14159265));

            telemetry.addData("xTarget", "%.3f", xTarget);
            telemetry.addData("yTarget", "%.3f", yTarget);
            telemetry.addData("zTarget", "%.3f", zTarget);


            telemetry.update();

            // Pause for metronome tick.  40 mS each cycle = update 25 times a second.
//          robot.waitForTick(40);
        } // opModeIsActive

    } // runOpMode

    /*---------------------------------------------------------------------------------*/
    void captureGamepad1Buttons() {
        gamepad1_triangle_last   = gamepad1_triangle_now;    gamepad1_triangle_now   = gamepad1.triangle;
        gamepad1_circle_last     = gamepad1_circle_now;      gamepad1_circle_now     = gamepad1.circle;
        gamepad1_cross_last      = gamepad1_cross_now;       gamepad1_cross_now      = gamepad1.cross;
        gamepad1_square_last     = gamepad1_square_now;      gamepad1_square_now     = gamepad1.square;
        gamepad1_dpad_up_last    = gamepad1_dpad_up_now;     gamepad1_dpad_up_now    = gamepad1.dpad_up;
        gamepad1_dpad_down_last  = gamepad1_dpad_down_now;   gamepad1_dpad_down_now  = gamepad1.dpad_down;
        gamepad1_dpad_left_last  = gamepad1_dpad_left_now;   gamepad1_dpad_left_now  = gamepad1.dpad_left;
        gamepad1_dpad_right_last = gamepad1_dpad_right_now;  gamepad1_dpad_right_now = gamepad1.dpad_right;
        gamepad1_l_bumper_last   = gamepad1_l_bumper_now;    gamepad1_l_bumper_now   = gamepad1.left_bumper;
        gamepad1_r_bumper_last   = gamepad1_r_bumper_now;    gamepad1_r_bumper_now   = gamepad1.right_bumper;
    } // captureGamepad1Buttons

    /*---------------------------------------------------------------------------------*/
    void captureGamepad2Buttons() {
        gamepad2_triangle_last   = gamepad2_triangle_now;    gamepad2_triangle_now   = gamepad2.triangle;
        gamepad2_circle_last     = gamepad2_circle_now;      gamepad2_circle_now     = gamepad2.circle;
        gamepad2_cross_last      = gamepad2_cross_now;       gamepad2_cross_now      = gamepad2.cross;
        gamepad2_square_last     = gamepad2_square_now;      gamepad2_square_now     = gamepad2.square;
        gamepad2_dpad_up_last    = gamepad2_dpad_up_now;     gamepad2_dpad_up_now    = gamepad2.dpad_up;
        gamepad2_dpad_down_last  = gamepad2_dpad_down_now;   gamepad2_dpad_down_now  = gamepad2.dpad_down;
        gamepad2_dpad_left_last  = gamepad2_dpad_left_now;   gamepad2_dpad_left_now  = gamepad2.dpad_left;
        gamepad2_dpad_right_last = gamepad2_dpad_right_now;  gamepad2_dpad_right_now = gamepad2.dpad_right;
        gamepad2_l_bumper_last   = gamepad2_l_bumper_now;    gamepad2_l_bumper_now   = gamepad2.left_bumper;
        gamepad2_r_bumper_last   = gamepad2_r_bumper_now;    gamepad2_r_bumper_now   = gamepad2.right_bumper;
//      gamepad2_touchpad_last   = gamepad2_touchpad_now;    gamepad2_touchpad_now   = gamepad2.touchpad;
//      gamepad2_share_last      = gamepad2_share_now;       gamepad2_share_now      = gamepad2.share;
    } // captureGamepad2Buttons

    /*---------------------------------------------------------------------------------*/
    /*  TELE-OP: Mecanum-wheel drive control using Dpad (slow/fine-adjustment mode)    */
    /*---------------------------------------------------------------------------------*/
    boolean processDpadDriveMode() {
        double fineControlSpeed = 0.20;
        boolean dPadMode = false;
        // Only process 1 Dpad button at a time

        return dPadMode;
    } // processDpadDriveMode

    private double minThreshold( double valueIn ) {
        double valueOut;

        //========= NO/MINIMAL JOYSTICK INPUT =========
        if( Math.abs( valueIn) < 0.02 ) {
            valueOut = 0.0;
        }
        else {
            valueOut = valueIn;
        }
        return valueOut;
    } // minThreshold

    private double multSegLinearRot( double valueIn ) {
        double valueOut;

        //========= NO JOYSTICK INPUT =========
        if( Math.abs( valueIn) < 0.05 ) {
            valueOut = 0.0;
        }
        //========= POSITIVE JOYSTICK INPUTS =========
        else if( valueIn > 0.0 ) {
            if( valueIn < 0.33 ) {                      // NOTE: approx 0.06 required to **initiate** rotation
                valueOut = (0.25 * valueIn) + 0.0650;   // 0.02=0.070  0.33=0.1475
            }
            else if( valueIn < 0.60 ) {
                valueOut = (0.50 * valueIn) - 0.0175;   // 0.33=0.1475  0.60=0.2825
            }
            else if( valueIn < 0.90 ) {
                valueOut = (0.75 * valueIn) - 0.1675;   // 0.60=0.2825  0.90=0.5075
            }
            else
                valueOut = (6.00 * valueIn) - 4.8925;   // 0.90=0.5075  1.00=1.1075 (clipped!)
        }
        //========= NEGATIVE JOYSTICK INPUTS =========
        else { // valueIn < 0.0
            if( valueIn > -0.33 ) {
                valueOut = (0.25 * valueIn) - 0.0650;
            }
            else if( valueIn > -0.60 ) {
                valueOut = (0.50 * valueIn) + 0.0175;
            }
            else if( valueIn > -0.90 ) {
                valueOut = (0.75 * valueIn) + 0.1675;
            }
            else
                valueOut = (6.00 * valueIn) + 4.8925;
        }

        return valueOut/1.0; //half speed
    } // multSegLinearRot

    private double multSegLinearXY( double valueIn ) {
        double valueOut;

        //========= NO JOYSTICK INPUT =========
        if( Math.abs( valueIn) < 0.05 ) {
            valueOut = 0.0;
        }
        //========= POSITIVE JOYSTICK INPUTS =========
        else if( valueIn > 0.0 ) {
            if( valueIn < 0.50 ) {                       // NOTE: approx 0.06 required to **initiate** rotation
                valueOut = (0.25 * valueIn) + 0.040;     // 0.01=0.0425   0.50=0.1650
            }
            else if( valueIn < 0.90 ) {
                valueOut = (0.75 * valueIn) - 0.210;     // 0.50=0.1650   0.90=0.4650
            }
            else
                valueOut = (8.0 * valueIn) - 6.735;      // 0.90=0.4650   1.00=1.265 (clipped)
        }
        //========= NEGATIVE JOYSTICK INPUTS =========
        else { // valueIn < 0.0
            if( valueIn > -0.50 ) {
                valueOut = (0.25 * valueIn) - 0.040;
            }
            else if( valueIn > -0.90 ) {
                valueOut = (0.75 * valueIn) + 0.210;
            }
            else
                valueOut = (8.0 * valueIn) + 6.735;
        }

        return valueOut;
    } // multSegLinearXY

    /*---------------------------------------------------------------------------------*/
    /*  TELE-OP: Standard Mecanum-wheel drive control (no dependence on gyro!)         */
    /*---------------------------------------------------------------------------------*/
    void processStandardDriveMode() {
        // new awesone roarbots code

        // Retrieve X/Y and ROTATION joystick input
        if( controlMultSegLinear ) {
            yTranslation = multSegLinearXY( .9 * -gamepad1.left_stick_y );
            xTranslation = multSegLinearXY(  .9 * gamepad1.left_stick_x );
            rotation     = multSegLinearRot( .9  * -gamepad1.right_stick_x );

        }
        else {
            yTranslation = -gamepad1.left_stick_y * .50;
            xTranslation =  gamepad1.left_stick_x * .50;
            rotation     = -gamepad1.right_stick_x * 0.65;
        }

        // Normal teleop drive control:
        // - left joystick is TRANSLATE fwd/back/left/right
        // - right joystick is ROTATE clockwise/counterclockwise
        // NOTE: assumes the right motors are defined FORWARD and the
        // left motors are defined REVERSE so positive power is FORWARD.
        frontRight = yTranslation - xTranslation + rotation;
        frontLeft  = yTranslation + xTranslation - rotation;
        rearRight  = yTranslation + xTranslation + rotation;
        rearLeft   = yTranslation - xTranslation - rotation;
        // Normalize the values so none exceed +/- 1.0
        maxPower = Math.max( Math.max( Math.abs(rearLeft),  Math.abs(rearRight)  ),
                Math.max( Math.abs(frontLeft), Math.abs(frontRight) ) );
        if (maxPower > 1.0)
        {
            rearLeft   /= maxPower;
            rearRight  /= maxPower;
            frontLeft  /= maxPower;
            frontRight /= maxPower;
        }
        //auto break
        if ((gamepad1.left_stick_y < .15 && gamepad1.left_stick_y > -.15) && (gamepad1.left_stick_x < .15 && gamepad1.left_stick_x > -.15) && (gamepad1.right_stick_y < .15 && gamepad1.right_stick_y > -.15) && (gamepad1.right_stick_x < .15 && gamepad1.right_stick_x > -.15)) {
            frontLeft = 0;
            frontRight = 0;
            rearLeft = 0;
            rearRight = 0;
        }
        // Update motor power settings:
        robot.driveTrainMotors( frontLeft, frontRight, rearLeft, rearRight );


        /*
        //now for the legacy drive !
        //Finds the hypotenuse of the triangle created by the two joystick values. Used to find the absoulte direction to go in.
        double r = Math.hypot(gamepad1.left_stick_x, gamepad1.left_stick_y);
        //Finds the robot's angle from the raw values of the joystick
        double robotAngle = Math.atan2(gamepad1.left_stick_y, gamepad1.left_stick_x) - Math.PI / 4;
        double rightX = gamepad1.right_stick_x;
        final double v1 = r * Math.cos(robotAngle) + rightX;
        final double v2 = r * Math.sin(robotAngle) - rightX;
        final double v3 = r * Math.sin(robotAngle) + rightX;
        final double v4 = r * Math.cos(robotAngle) - rightX;

        if(gamepad1.left_stick_y > 0.15 || gamepad1.left_stick_y < -0.15 || gamepad1.left_stick_x > 0.15 || gamepad1.left_stick_x < -0.15) {
            //reversed bc idek the old code just works this way
            frontLeft = (-0.75*v2);
            frontRight = (-0.75*v1);
            rearLeft = (-0.75*v4);
            rearRight = (-0.75*v3);
        }

        else if(gamepad1.left_bumper) {
            frontLeft = (-0.5);
            rearLeft = (-0.5);
            frontRight = (0.5);
            rearRight = (0.5);
        }

        else if(gamepad1.right_bumper) {
            frontLeft = (0.5);
            rearLeft = (0.5);
            frontRight = (-0.5);
            rearRight = (-0.5);
        }
        else if(gamepad1.right_trigger > 0.15) {
            double power = 0.25*gamepad1.right_trigger;
            frontLeft = (power);
            rearLeft = (power);
            frontRight = (-power);
            rearRight = (-power);
        }
        else if(gamepad1.left_trigger > 0.15) {
            double power = 0.25*gamepad1.left_trigger;
            frontLeft = (-power);
            rearLeft = (-power);
            frontRight = (power);
            rearRight = (power);
        }
        else {
            frontLeft = (0);
            rearLeft = (0);
            frontRight = (0);
            rearRight = (0);
        }
        old code
        robot.driveTrainMotors( frontLeft, frontRight, rearLeft, rearRight );
        */

    } // processStandardDriveMode
    void encoderReset (){
        if (gamepad2_square_now && !gamepad2_square_last) { // reset tele-op positions
//            robot.pigChucker.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//            robot.intakeMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//
//            robot.pigChucker.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//            robot.intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            sleep(50);
            gamepad1.runRumbleEffect(shortRumble);
            gamepad2.runRumbleEffect(shortRumble);
        }
    } //encoderReset
    void processNeck(){
//

        if (gamepad1_square_now && !gamepad1_square_last){
            if (neckSeeking){
                robot.neckPos = 0.5;
            }
            neckSeeking = !neckSeeking;
        }
        if (neckSeeking){
            robot.neckSwivel = 0;
            double headingToDegrees = robot.angleWrap(robot.odom.getHeading() * 180 / 3.14159265);
            if (Math.abs(headingToDegrees) > (330 / 2)){ //TODO: this may be 330 / 2 ... basically the max range of servo / 2
                robot.neckPos = 0.5;
            }
            else {
                robot.neckPos = -headingToDegrees / 330 + 0.5; //TODO: also check this out
            }
        }
        else{
            robot.neckSwivel = ((gamepad1.left_trigger) - (gamepad1.right_trigger)) / 4.0;
            if(gamepad1_r_bumper_now && !gamepad1_r_bumper_last){
                robot.neckPos -= 0.25;
            } else if (gamepad1_l_bumper_now && !gamepad1_l_bumper_last){
                robot.neckPos += 0.25;
            }
            if (robot.neckPos > 1.0){
                robot.neckPos = 1.0;
            } else if (robot.neckPos < 0.0) {
                robot.neckPos = 0.0;
            }
        }
        double neckTotal = Range.clip(robot.neckPos + robot.neckSwivel, 0.0, 1.0);
        robot.neckServo.setPosition(neckTotal);
    }
    void resetOdom(){
        if (gamepad1_triangle_now){
            robot.odom.resetPosAndIMU();
        }
    }
    void updateTargetPos(){
        if (Math.max(Math.abs(gamepad1.left_stick_x), Math.max(Math.abs(gamepad1.left_stick_y), Math.abs(gamepad1.right_stick_x))) > 0.15) {
            navigating = false;
        }
        else if (gamepad1_circle_now){
            navigating = true;
            xTarget = 0; yTarget = 0; zTarget = 0;
        }
        else if (gamepad1_dpad_up_now){
            navigating = true;
            xTarget = 900; yTarget = 0; zTarget = 0; //TODO: this is gonna be an issue cuz 180 and -180 technicaly don't exist and are the same position. what will it do here? may need to abs smth. idek
        }
        else if (gamepad1_dpad_right_now && !gamepad1_dpad_right_last){
            navigating = true;
            xTarget = 0; yTarget = -900; zTarget = 90;
        }
        else if( gamepad1_dpad_down_now){
            navigating = true;
            xTarget = -900; yTarget = 0; zTarget = 0;
        }
        else if (gamepad1_dpad_left_now && !gamepad1_dpad_left_last) {
            navigating = true;
            xTarget = 0; yTarget = 900; zTarget = -90;
        }
    }


    double xKD = -0.00;
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
    void goTo(double xTarget, double yTarget, double zTarget, double xPos, double yPos, double zPos, double dt) { //TODO: must swithc the xpos and ypos inputs becuase of weird odom swithc thingy. sucks
        if (!navigating) {
            xErrorIntegral = 0;
            lastXError = 0;
            yErrorIntegral = 0;
            lastYError = 0;
            zErrorIntegral = 0;
            lastZError = 0;

            processStandardDriveMode();
        }

        else {

            double xError = xTarget - xPos;
            double yError = yTarget - yPos;
            double zError = zTarget - robot.angleWrap(zPos*180/3.14159265);

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

            // If the magnitude of the power is too small to move the robot, kill it to 0
//            if (Math.abs(frontRightCorrection) < minJuice) {
//                frontRightCorrection = 0.0;
//            }
//            if (Math.abs(frontLeftCorrection) < minJuice) {
//                frontLeftCorrection = 0.0;
//            }
//            if (Math.abs(rearRightCorrection) < minJuice) {
//                rearRightCorrection = 0.0;
//            }
//            if (Math.abs(rearLeftCorrection) < minJuice) {
//                rearLeftCorrection = 0.0;
//            }

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
    }

} // Teleop
