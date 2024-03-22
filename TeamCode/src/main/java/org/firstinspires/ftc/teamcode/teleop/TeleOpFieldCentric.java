package org.firstinspires.ftc.teamcode.teleop;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.RobotHardware;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

/**
 * This teleop program aims to create a separate teleop drive that is field-centric.
 * The drive difference from the normal teleop, but the attachement controls are the same
 * [To be implemented™]
 */
@TeleOp(group = "advanced")
public class TeleOpFieldCentric extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        // Initialize SampleMecanumDrive
        final SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

        // Grab motors
        final DcMotor ViperSlide = hardwareMap.get(DcMotor.class, "ViperSlide");
        final DcMotor ViperSlide2 = hardwareMap.get(DcMotor.class, "ViperSlide2");
        final DcMotor spaghettiIntake = hardwareMap.get(DcMotor.class, "spaghettiIntake");

        // Grab distance sensor
        // This is technically also the color sensor, it is the same hardware device
        // That's why it is called "color" in the hardware map
        // Note for wiring: This plug into the I2C port! NOT the digital port
        final DistanceSensor dist = hardwareMap.get(DistanceSensor.class, "color");

        // Grab LED driver
        // The LED Driver class is really just a wrapper for the Servo class, because the different patterns of the LED Driver just correspond to Servo positions (pulse width)
        // You can find a layout diagram on Page 11 of the REV Documentation for the LED Driver:
        // https://www.revrobotics.com/content/docs/REV-11-1105-UM.pdf
        // Note for wiring: Plug into servo port
        final RevBlinkinLedDriver ledDriver = hardwareMap.get(RevBlinkinLedDriver.class, "ledDriver");

        // Superspeed use
        double speedMultiplier = 0.5;

        // Grab runtime
        final ElapsedTime runtime = new ElapsedTime();

        // Grab servos
        final CRServo spinny = hardwareMap.get(CRServo.class, "servo1");
        final Servo servo2 = hardwareMap.get(Servo.class, "servo2");

        // Init motors
        ViperSlide.setDirection(DcMotor.Direction.FORWARD);
        ViperSlide2.setDirection(DcMotor.Direction.FORWARD);
        spaghettiIntake.setDirection(DcMotor.Direction.FORWARD);

        //triggerOffset for trigger movements
        double triggerOffset = gamepad1.left_stick_x * speedMultiplier;

        // We want to turn off velocity control for teleop
        // Velocity control per wheel is not necessary outside of motion profiled auto
        drive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Set a starting pose
        drive.setPoseEstimate(new Pose2d(0,0,0));

        // Set the brake mode for all motors
        drive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        // MET in seconds for the thread to NOT change the leds
        // Essentially, when an indicator pattern needs to be shown, it will be show for x seconds, where x is this variable
        // After x seconds, the LED go will back to the normal (pixel status) mode
        // Is a final array because it needs to be accessed in inner class (threads)
        final double[] doNotChangeLedsUtil = {0};

        // Note for Parinaz: The Distance sensor is mounted on a hole in the back of the box (the intake pixel box)
        // The distance is the distance from the back of the box to the pixel
        // When no pixel is in the box, the distance sensor will be able to see out opening in the front
        // When a pixel is located in the box, the distance sensor will see that something is blocking it
        // See the if block in the while loop for how it works
        double distance; // For use in led, literally the distance in CM

        // Viper slide control mode for telemetry (we can't see the viper slide mode on the dashboard, that's why this was made)
        // Is a final array because it needs to be accessed in inner class (threads)
        final boolean[] viperSlideAlternativeControl = {false};

        // Viper slide controller thread
        Thread vsController = new Thread() {
            double encoderDrivingTarget = 0;

            double encoderDrivingTarget2 = 0;
            public void run() {
                // Init vars
                double viperSlideTarget = 0;
                double rotationsNeeded = 0;
                // Init viperslides
                ViperSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                ViperSlide2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

                ViperSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                ViperSlide2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

                ViperSlide.setTargetPosition(0);
                ViperSlide2.setTargetPosition(0);


                ViperSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                ViperSlide2.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                ViperSlide.setPower(0.5);
                ViperSlide2.setPower(0.5);

                while (opModeIsActive()) {

                    // Gamepad 2 x to activate alt control
                    if (!viperSlideAlternativeControl[0] && gamepad2.x) {
                        ViperSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                        ViperSlide2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

                        ViperSlide.setPower(0);
                        ViperSlide2.setPower(0);
                        ViperSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                        ViperSlide2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                        viperSlideAlternativeControl[0] = true;

                        // Strobe red when switching into alt mode, for one second
                        doNotChangeLedsUtil[0] = runtime.seconds() + 1;
                        ledDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.STROBE_RED);
                    }

                    // Reset to pid mode
                    if (viperSlideAlternativeControl[0] && gamepad2.y) {
                        // This ensures the slide is in 0 position
                        ViperSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                        ViperSlide2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                        ViperSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                        ViperSlide2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

                        ViperSlide.setTargetPosition(0);
                        ViperSlide2.setTargetPosition(0);


                        ViperSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                        ViperSlide2.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                        ViperSlide.setPower(0.5);
                        ViperSlide2.setPower(0.5);
                        viperSlideTarget = 0;
                        rotationsNeeded = 0;
                        this.encoderDrivingTarget = 0;
                        this.encoderDrivingTarget2 = 0;
                        viperSlideAlternativeControl[0] = false;

                        // Strobe white for 1 second when going back into normal (PID) mode
                        doNotChangeLedsUtil[0] = runtime.seconds() + 1;
                        ledDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.STROBE_WHITE);

                    }

                    // Pid Mode
                    if (!viperSlideAlternativeControl[0]) {
                        ViperSlide.setPower(0.5);
                        ViperSlide2.setPower(0.5);

                        // If joystick is released, reset target position to current position
                        if (gamepad2.left_stick_y < 0.1 && gamepad2.left_stick_y > -0.1) {
                            ViperSlide.setTargetPosition((int) ViperSlide.getCurrentPosition());
                            ViperSlide2.setTargetPosition((int) ViperSlide2.getCurrentPosition());

                            // Reverse calculate rotations
                            this.encoderDrivingTarget = ViperSlide.getTargetPosition();
                            double rotationsNeed = encoderDrivingTarget / RobotHardware.TICK_COUNT;
                            viperSlideTarget = rotationsNeed * RobotHardware.VS_CIRCUMFERENCE;

                        } else {

                            // Normal PID Operation
                            viperSlideTarget += gamepad2.left_stick_y * 0.5;
                            rotationsNeeded = viperSlideTarget / RobotHardware.VS_CIRCUMFERENCE;

                            encoderDrivingTarget = rotationsNeeded * RobotHardware.TICK_COUNT;

                            // Check bounds
                            if (encoderDrivingTarget > 1) {
                                encoderDrivingTarget = 0;
                            }

                            if (encoderDrivingTarget < -3501) {
                                encoderDrivingTarget = -3500;
                            }

                            encoderDrivingTarget2 = -encoderDrivingTarget;



                            ViperSlide.setTargetPosition((int) encoderDrivingTarget);
                            ViperSlide2.setTargetPosition((int) encoderDrivingTarget2);
                        }

                        ViperSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                        ViperSlide2.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                        //viperslide1 is positive when extended
                        //viperslide2 is negative when extended

                    } else {
                        // Alternative control mode
                        ViperSlide.setPower(gamepad2.left_stick_y*0.5);
                        ViperSlide2.setPower(gamepad2.left_stick_y*-0.5);
                    }
                }
            }
        };

        // This is still in INIT mode, we set a "ready to start" pattern for the LED
        ledDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.CP1_BREATH_FAST);

        waitForStart(); // Start has been called

        if (isStopRequested()) return;

        vsController.start(); // Activate vs Controller


        doNotChangeLedsUtil[0] = runtime.seconds() + 1; // This to show the below pattern for one second
        ledDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.STROBE_GOLD); // "Go" pattern



        while (opModeIsActive() && !isStopRequested()) {
            // Read pose
            Pose2d poseEstimate = drive.getPoseEstimate();


            //checks if user pressed trigger or moved stick, move robot is corresponding way
            if (gamepad1.left_trigger > 0.05 || gamepad1.right_trigger > 0.05){
                triggerOffset = gamepad1.right_trigger - gamepad1.left_trigger;
            }else{
                triggerOffset = gamepad1.left_stick_x * speedMultiplier;
            }

            // Create a vector from the gamepad x/y inputs
            // Then, rotate that vector by the inverse of that heading
            Vector2d input = new Vector2d(
                    -gamepad1.left_stick_y * speedMultiplier,
                     -triggerOffset
            ).rotated(-poseEstimate.getHeading());


            // Pass in the rotated input + right stick value for rotation
            // Rotation is not part of the rotated input thus must be passed in separately
            drive.setWeightedDrivePower(
                    new Pose2d(
                            input.getX(),
                            input.getY(),
                            -gamepad1.right_stick_x*0.5 // Rotation go less
                    )
            );

            // Spinny (Intake wheel) control
            // A - pixel in
            if(gamepad2.a){
                spinny.setPower(-0.6);
            }

            //B - pixel out
            else if (gamepad2.b) {
                spinny.setPower(0.6);
            }
            else{
                spinny.setPower(0);
            }

            // Rest the rotation/heading of the robot to 0 degrees in case of drift
            if (gamepad1.y) {
                drive.setPoseEstimate(new Pose2d(0,0,0));
            }

            // plane launcher code
            if(gamepad2.dpad_down){ // Reset
                servo2.setPosition(0.25);
            }

            if(gamepad2.dpad_up){ // Launch
                servo2.setPosition(0);
            }

            // superspeed
            if (gamepad1.left_bumper) {
                speedMultiplier = 1;
            } else {
                speedMultiplier = 0.5;
            }

            // intake lifter code
            if(gamepad2.dpad_left){
                spaghettiIntake.setPower(0.75); //shoot out
            }else if(gamepad2.dpad_right){
                spaghettiIntake.setPower(-0.75); //suck in
            }else{
                spaghettiIntake.setPower(0);
            }

            // LED stuff for seeing if any pixels are in the box
            if (doNotChangeLedsUtil[0] < runtime.seconds()) { // Only show if there is no set pattern (such as the start and go patterns)
                distance = dist.getDistance(DistanceUnit.CM);

                if (distance > 5.55) { // No pixels
                    ledDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.CP1_2_END_TO_END_BLEND_1_TO_2);
                } else if (distance > 1.5) { // One pixel only
                    ledDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.COLOR_WAVES_FOREST_PALETTE);
                } else { // Both pixels
                    ledDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.COLOR_WAVES_LAVA_PALETTE);
                }
            }

            // Reset LED
            // Sometimes, if you press start too quickly after init, the LEDs will get suspended in the "idle" state
            // Use this to "wake up" the LEDs, will display the "go" pattern
            if (gamepad1.x) {
                doNotChangeLedsUtil[0] = runtime.seconds() + 1;
                ledDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.STROBE_GOLD);
            }

            // Update everything. Odometry. Etc.
            drive.update();

            // Print pose to telemetry
            telemetry.addData("x", poseEstimate.getX());
            telemetry.addData("y", poseEstimate.getY());
            telemetry.addData("heading", poseEstimate.getHeading());
            telemetry.addData("distance", dist.getDistance(DistanceUnit.CM));

            // VS telemetry has been added! Copied from the old Teleop
            if (viperSlideAlternativeControl[0]) {
                telemetry.addData("Viper Slide Control Mode","🆘 ALT EXTEND MODE");
            } else {
                telemetry.addData("Viper Slide Control Mode","✳️ Normal Target Mode");
            }
            telemetry.update();
        }
    }
}