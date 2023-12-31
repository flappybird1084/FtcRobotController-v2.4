package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.control.PIDCoefficients;
import com.acmerobotics.roadrunner.control.PIDFController;
//import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.robot.Robot;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.RobotHardware;

import org.firstinspires.ftc.teamcode.RobotHardware;

@Disabled
@TeleOp(name = "PID Tester 2", group= "Concept")
public class PIDLogic  extends LinearOpMode {
    RobotHardware robot = new RobotHardware();
    private ElapsedTime runtime = new ElapsedTime();
    double leftYawCoolDown = runtime.seconds();
    double rightYawCoolDown = runtime.seconds();
    double additionalYaw = 0;
    double viperSlideTarget = 0;
    private CRServo spinny;
    double totalGamepad2TriggerInput = 0;


    double kP, kI, kD = 0;
    double integralSum = 0;
    private double lastError = 0;
    public static double f=0;

    public static int target1 = 0;
    public static int target2 = 0;
    private final double ticks_in_degree = RobotHardware.TICK_COUNT/360;
    private DcMotorEx slideMotor1;
    private DcMotorEx slideMotor2;

    double pid;
    double pid2;
    double slidePower1;
    double slidePower2;
    double ff1;
    double ff2;


    @Override
    public void runOpMode() throws InterruptedException {


        while (opModeIsActive()) {

            ff1 = Math.cos(Math.toRadians(target1/ticks_in_degree))*f;
            ff2 = Math.sin(Math.toRadians(target2/ticks_in_degree))*f;

            slidePower1 = PIDControl(target1, ff1);
            slidePower2 = PIDControl(target2, ff2);

            slideMotor1.setPower(slidePower1);
            slideMotor2.setPower(slidePower2);



            double max; // get top wheel speed

            double regularSpeed = 0.5;
            double superSpeed = 1;
            //superspeed is used only when there's a stretch
            // of ground to be covered, normally not
            // to be used during matches

            double axial = -gamepad1.left_stick_y * regularSpeed;  // Note: pushing stick forward gives negative value
            double lateral = gamepad1.left_stick_x * regularSpeed;
            double yaw = gamepad1.right_stick_x * regularSpeed;

            if (gamepad1.left_bumper) {
                axial *= superSpeed / regularSpeed; //recalculating all values, least year's method wasQ less elegant
                lateral *= superSpeed / regularSpeed;
                yaw *= superSpeed / regularSpeed;
            }

            double leftFrontPower = (axial + lateral + yaw);
            double rightFrontPower = (axial - lateral - yaw);
            double leftBackPower = (axial - lateral + yaw);
            double rightBackPower = (axial + lateral - yaw);
            // movement algorithm

            double right_trig = gamepad1.right_trigger;
            double left_trig = gamepad1.left_trigger;
            // for strafing

            double avgMotorPower = (leftBackPower + leftFrontPower + rightBackPower + rightFrontPower) / 4;
            // additionalyaw resource. we need to turn proportional to average speed of bot, and while this isn't perfect it works


            max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
            max = Math.max(max, Math.abs(leftBackPower));
            max = Math.max(max, Math.abs(rightBackPower));
            // calculate top wheel speed

            if (gamepad1.dpad_left && (runtime.seconds() - leftYawCoolDown) > 1) {
                additionalYaw -= 0.01;
                leftYawCoolDown = runtime.seconds();
            }
            // need a cooldown for additionalyaw or it'll keep adding

            if (gamepad1.dpad_right && (runtime.seconds() - rightYawCoolDown) > 1) {
                additionalYaw += 0.01;
                rightYawCoolDown = runtime.seconds();
            }
/*
            if(gamepad2.dpad_up){
                robot.singleMotorEncoderMovements(telemetry,2,0.05,robot.EncoderTest)\
            }

 */

            viperSlideTarget += gamepad2.left_stick_y;

//            robot.ViperSlide.setPower(gamepad2.left_stick_y);
//            robot.ViperSlide2.setPower(gamepad2.left_stick_y);
            robot.linearActuator.setPower(gamepad2.right_stick_y);


            // intake code
            if (gamepad2.a) {
                spinny.setPower(-0.6);
            } else if (gamepad2.b) {
                spinny.setPower(0.6);
            } else {
                spinny.setPower(0);
            }

            // plane code
            if (gamepad2.dpad_up) {
                robot.servo2.setPosition(0.25);
            }

            if (gamepad2.dpad_down) {
                robot.servo2.setPosition(0);
            }

            // intake lifter code


            totalGamepad2TriggerInput = -gamepad2.left_trigger + gamepad2.right_trigger;

            robot.servo3.setPower(totalGamepad2TriggerInput);


            if (gamepad1.right_trigger > 0) {
                leftFrontPower = right_trig;
                leftBackPower = -right_trig;
                rightFrontPower = -right_trig;
                rightBackPower = right_trig;
            }

            if (gamepad1.left_trigger > 0) {
                leftFrontPower = -left_trig;
                leftBackPower = left_trig;
                rightFrontPower = left_trig;
                rightBackPower = -left_trig;
            }

            telemetry.addData("vector to degree test: ", robot.vectorToDegrees(axial, lateral));
            // i believe this is calculating angle of the robot relative to starting point

            if (max > 1) {
                leftFrontPower /= max;
                rightFrontPower /= max;
                leftBackPower /= max;
                rightBackPower /= max;
            }

            //leftBackPower += additionalYaw * avgMotorPower;
            //leftFrontPower += additionalYaw * avgMotorPower;
            //rightBackPower -= additionalYaw * avgMotorPower;
            //rightFrontPower -= additionalYaw * avgMotorPower;

            //robot.leftFront.setPower(leftFrontPower * superSpeed);
            //robot.rightFront.setPower(rightFrontPower * superSpeed);
            //robot.leftBack.setPower(leftBackPower * superSpeed);
            //robot.rightBack.setPower(rightBackPower * superSpeed);

            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Front left/Right", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", leftBackPower, rightBackPower);
            telemetry.addData("Left Trigger:", gamepad1.left_trigger);
            telemetry.addData("Right Trigger", gamepad1.right_trigger);
            telemetry.addData("Left Stick Y", gamepad1.left_stick_y);
            telemetry.addData("Left Stick X", gamepad1.left_stick_x);
            telemetry.addData("Right Stick Y", gamepad1.right_stick_y);
            telemetry.addData("Right Stick X", gamepad1.right_stick_x);
            telemetry.addData("Additional Yaw: ", additionalYaw);
            telemetry.addData("Average Motor Power: ", avgMotorPower);
            telemetry.addData("Axial: ", axial);
            telemetry.addData("Lateral: ", lateral);
            telemetry.addData("Yaw: ", yaw);
            telemetry.addData("Servo Pos: ", robot.servo1.getController().getServoPosition(robot.servo1.getPortNumber()));
            telemetry.addData("Servo Reported Power: ", robot.servo1.getPower());
            telemetry.update();
        }
    }

    public double PIDControl(double reference, double state) {
        double error = reference - state;
        integralSum += error*runtime.seconds();

        double derivative = (error-lastError) / runtime.seconds();

        runtime.reset();


        double output = (error*kP) + (derivative*kD) + (integralSum*kI);
        return output;
    }

}