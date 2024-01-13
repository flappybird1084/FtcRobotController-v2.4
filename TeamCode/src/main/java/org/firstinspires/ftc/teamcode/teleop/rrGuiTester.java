/* Copyright (c) 2019 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.teleop;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.RobotHardware;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

import java.util.ArrayList;

/**
 * This 2023-2024 OpMode illustrates the basics of TensorFlow Object Detection,
 * including Java Builder structures for specifying Vision parameters.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list.
 */
@TeleOp(name = "test roadrunner gui", group = "Auto Concept")

public class rrGuiTester extends LinearOpMode {

    ArrayList<TrajectorySequence> trajectorySequenceArrayList = new ArrayList<>();
    ArrayList<String> nameList = new ArrayList<>();
    int selectedProgram = 0;
    double selectedProgramCounter = 0;
    DcMotor ViperSlide;
    DcMotor ViperSlide2;
    private ElapsedTime runtime = new ElapsedTime();

    boolean isTriggered = false;

    boolean isOverride = false;




    RobotHardware robot = new RobotHardware();

    double viperSlideTarget = 0;
    double rotationsNeeded = 0;
    double encoderDrivingTarget = 0;
    double encoderDrivingTarget2 = 0;
    private void runViperSlide(int moveBy) {
        viperSlideTarget += moveBy;
        rotationsNeeded = viperSlideTarget/RobotHardware.VS_CIRCUMFERENCE;

        encoderDrivingTarget = rotationsNeeded*RobotHardware.TICK_COUNT;

        encoderDrivingTarget2 = -encoderDrivingTarget;

        robot.ViperSlide.setPower(0.5);
        robot.ViperSlide2.setPower(0.5);

        robot.ViperSlide.setTargetPosition((int) encoderDrivingTarget);
        robot.ViperSlide2.setTargetPosition((int) encoderDrivingTarget2);

        robot.ViperSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.ViperSlide2.setMode(DcMotor.RunMode.RUN_TO_POSITION);

    }

    @Override
    public void runOpMode() {





        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        robot.init(hardwareMap);

        ViperSlide = hardwareMap.get(DcMotor.class, "ViperSlide");
        ViperSlide2 = hardwareMap.get(DcMotor.class, "ViperSlide2");
        runtime.reset();

        ViperSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        ViperSlide2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        ViperSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        ViperSlide2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        ViperSlide.setTargetPosition(0);
        ViperSlide2.setTargetPosition(0);

        ViperSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        ViperSlide2.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        Thread viperslidewatcher = new Thread() {
            @Override
            public void run() {
                boolean canPress = true;
                while (!isInterrupted()) {
                    if (opModeIsActive() && !isOverride) {
                        isTriggered = false;
                        // Check for the most stupid going below 0 thing
                        if (ViperSlide.getTargetPosition() > 1) {
                            ViperSlide.setTargetPosition(0);
                            ViperSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                            isTriggered = true;
                        }
                        if (ViperSlide2.getTargetPosition() > 1) {
                            ViperSlide2.setTargetPosition(0);
                            ViperSlide2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                            isTriggered = true;
                        }
                        if (ViperSlide.getTargetPosition() < -3501) {
                            ViperSlide.setTargetPosition(-3500);
                            ViperSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                            isTriggered = true;
                        }
                        if (ViperSlide2.getTargetPosition() < -3501) {
                            ViperSlide2.setTargetPosition(-3500);
                            ViperSlide2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                            isTriggered = true;
                        }
                        if (Math.abs(Math.abs(ViperSlide.getCurrentPosition()) - Math.abs(ViperSlide2.getCurrentPosition())) > 30) {
                            ViperSlide.setPower(0);
                            ViperSlide2.setPower(0);
                            isTriggered = true;
                        }
                    } else if (!opModeIsActive()){
                        break;
                    }
                    if (gamepad1.b && canPress) {
                        if (isOverride) {
                            isOverride = false;
                        } else {
                            isOverride = true;
                        }
                        isTriggered = false;
                        canPress = false;
                    } else if (!gamepad1.b) {
                        canPress = true;
                    };
                    telemetry.addData("Selected Program: ", selectedProgram);
                    telemetry.addData("Selected Program Counter", selectedProgramCounter);
                    if(selectedProgram < nameList.size()){
                        telemetry.addData("Selected Program Name: ", nameList.get(selectedProgram));
                    }
                    else{
                        telemetry.addData("Selected Program Name: ", "none + namelist len: ", +nameList.size());
                    }

                    if (isTriggered) telemetry.addData("VS Killswitch","[⚠️\uD83D\uDED1] TRIGGERED");
                    else if (isOverride) telemetry.addData("VS Killswitch", "[⭕️] OVERRIDE");
                    else telemetry.addData("VS Killswitch", "[✅] OK");
                    telemetry.update();
                }
            }
        };

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                viperslidewatcher.interrupt();
                try {
                    viperslidewatcher.join();
                } catch (InterruptedException e) {

                }
            }
        });

        while(!opModeIsActive()) {




            TrajectorySequence testdrive = drive.trajectorySequenceBuilder(new Pose2d(-36.83, -57.37, Math.toRadians(88.34)))
                    .splineTo(new Vector2d(-36.30, -25.33), Math.toRadians(89.33))
                    .splineTo(new Vector2d(-9.14, -0.61), Math.toRadians(-6.34))
                    .splineTo(new Vector2d(55.98, -35.09), Math.toRadians(0.00))
                    .build();
            drive.setPoseEstimate(testdrive.start());

            trajectorySequenceArrayList.add(testdrive);
            nameList.add("rufan test under bridge 1");

            TrajectorySequence testdrive2 = drive.trajectorySequenceBuilder(new Pose2d(-36.04, -60.86, Math.toRadians(1.50)))
                    .splineToSplineHeading(new Pose2d(10.51, -61.99, Math.toRadians(-1.39)), Math.toRadians(-1.39))
                    .splineTo(new Vector2d(36.88, -31.52), Math.toRadians(-1.25))
                    .build();
            drive.setPoseEstimate(testdrive2.start());

            trajectorySequenceArrayList.add(testdrive2);
            nameList.add("rian test under truss");

            TrajectorySequence testdrive3 = drive.trajectorySequenceBuilder(new Pose2d(-36.04, -59.59, Math.toRadians(90.00)))
                    .splineTo(new Vector2d(-11.21, -0.63), Math.toRadians(0.00))
                    .splineTo(new Vector2d(24.19, -17.98), Math.toRadians(270.00))
                    .splineTo(new Vector2d(50.70, -36.32), Math.toRadians(0.00))
                    .build();
            drive.setPoseEstimate(testdrive3.start());

            trajectorySequenceArrayList.add(testdrive3);
            nameList.add("rufan test under door 1");

            TrajectorySequence untitled0 = drive.trajectorySequenceBuilder(new Pose2d(-36.32, -62.41, Math.toRadians(90.00)))
                    .splineToSplineHeading(new Pose2d(-23.62, -11.49, Math.toRadians(-0.95)), Math.toRadians(-0.95))
                    .splineToSplineHeading(new Pose2d(21.23, -15.02, Math.toRadians(-19.16)), Math.toRadians(-19.16))
                    .splineToSplineHeading(new Pose2d(44.00, -17.84, Math.toRadians(22.00)), Math.toRadians(31.39))
                    .build();

            drive.setPoseEstimate(untitled0.start());

            trajectorySequenceArrayList.add(untitled0);
            nameList.add("rian test under truss 2");

            TrajectorySequence untitled1 = drive.trajectorySequenceBuilder(new Pose2d(-36.35, -62.17, Math.toRadians(90.00)))
                    .splineToSplineHeading(new Pose2d(-36.88, -11.06, Math.toRadians(90.00)), Math.toRadians(90.00))
                    .splineToSplineHeading(new Pose2d(30.73, 15.98, Math.toRadians(43.69)), Math.toRadians(43.69))
                    .splineToSplineHeading(new Pose2d(51.45, 34.95, Math.toRadians(0.00)), Math.toRadians(0.00))
                    .addTemporalMarker(() -> {
                        // Run your action in here!
                        robot.viperSlideEncoderMovements(telemetry,20,0.5,false,robot.ViperSlide);
                        robot.viperSlideEncoderMovements(telemetry,20,0.5,true,robot.ViperSlide2);
                    })
                    .waitSeconds(4)
                    .addTemporalMarker(() -> {

                        // Run your action in here!
                        robot.viperSlideEncoderMovements(telemetry,20,0.5,true,robot.ViperSlide);
                        robot.viperSlideEncoderMovements(telemetry,20,0.5,false,robot.ViperSlide2);
                    })  
                    .build();

            drive.setPoseEstimate(untitled1.start());

            trajectorySequenceArrayList.add(untitled1);
            nameList.add("iyer markers te77st");

            TrajectorySequence viperSliding = drive.trajectorySequenceBuilder(new Pose2d(-36.35, -62.17, Math.toRadians(90.00)))
                    .addTemporalMarker(() -> {
                        // TODO: not working make it run both motors
                        runViperSlide(2000);
                    })
                    .waitSeconds(4)
                    .addTemporalMarker(() -> {

                        // empty
                    })
                    .build();
            trajectorySequenceArrayList.add(viperSliding);
            nameList.add("viper slide only test");


            untitled0 = drive.trajectorySequenceBuilder(new Pose2d(10.85, -66.61, Math.toRadians(89.51)))
                    .splineToSplineHeading(new Pose2d(5.04, -40.08, Math.toRadians(118.93)), Math.toRadians(118.93))
                    .lineToSplineHeading(new Pose2d(44.90, -28.87, Math.toRadians(0.00)))
                    .splineToSplineHeading(new Pose2d(58.38, -61.93, Math.toRadians(1.85)), Math.toRadians(1.85))
                    .build();
            drive.setPoseEstimate(untitled0.start());
            trajectorySequenceArrayList.add(untitled0);
            nameList.add("red canvas left");


            untitled0 = drive.trajectorySequenceBuilder(new Pose2d(10.85, -66.61, Math.toRadians(89.51)))
                    .splineToSplineHeading(new Pose2d(11.70, -27.59, Math.toRadians(90.00)), Math.toRadians(90.00))
                    .lineToSplineHeading(new Pose2d(45.19, -35.82, Math.toRadians(0.00)))
                    .splineToSplineHeading(new Pose2d(58.38, -61.93, Math.toRadians(1.85)), Math.toRadians(1.85))
                    .build();
            drive.setPoseEstimate(untitled0.start());
            trajectorySequenceArrayList.add(untitled0);
            nameList.add("red canvas center");


            untitled0 = drive.trajectorySequenceBuilder(new Pose2d(10.85, -66.61, Math.toRadians(89.51)))
                    .splineToSplineHeading(new Pose2d(11.70, -27.59, Math.toRadians(90.00)), Math.toRadians(90.00))
                    .lineToSplineHeading(new Pose2d(45.19, -35.82, Math.toRadians(0.00)))
                    .splineToSplineHeading(new Pose2d(58.38, -61.93, Math.toRadians(1.85)), Math.toRadians(1.85))
                    .build();
            drive.setPoseEstimate(untitled0.start());
            trajectorySequenceArrayList.add(untitled0);
            nameList.add("red canvas right");


            untitled0 = drive.trajectorySequenceBuilder(new Pose2d(10.85, 66.61, Math.toRadians(270.49)))
                    .splineToSplineHeading(new Pose2d(20.07, 37.67, Math.toRadians(300.00)), Math.toRadians(300.00))
                    .lineToSplineHeading(new Pose2d(50.44, 43.06, Math.toRadians(0.00)))
                    .splineToSplineHeading(new Pose2d(58.38, 61.93, Math.toRadians(358.15)), Math.toRadians(358.15))
                    .build();
            drive.setPoseEstimate(untitled0.start());
            trajectorySequenceArrayList.add(untitled0);
            nameList.add("blue canvas left");

            untitled0 = drive.trajectorySequenceBuilder(new Pose2d(10.85, 66.61, Math.toRadians(270.49)))
                    .splineToSplineHeading(new Pose2d(12.70, 29.44, Math.toRadians(270.00)), Math.toRadians(270.00))
                    .lineToSplineHeading(new Pose2d(50.44, 43.06, Math.toRadians(0.00)))
                    .splineToSplineHeading(new Pose2d(58.38, 61.93, Math.toRadians(358.15)), Math.toRadians(358.15))
                    .build();
            drive.setPoseEstimate(untitled0.start());
            trajectorySequenceArrayList.add(untitled0);
            nameList.add("blue canvas center");

            untitled0 = drive.trajectorySequenceBuilder(new Pose2d(11.78, 64.24, Math.toRadians(-88.96)))
                    .splineToSplineHeading(new Pose2d(4.16, 41.82, Math.toRadians(253.20)), Math.toRadians(253.20))
                    .lineToSplineHeading(new Pose2d(51.27, 29.69, Math.toRadians(3.69)))
                    .splineToSplineHeading(new Pose2d(57.05, 61.56, Math.toRadians(0.00)), Math.toRadians(0.00))
                    .build();
            trajectorySequenceArrayList.add(untitled0);
            nameList.add("blue canvas right");

//            untitled0 = drive.trajectorySequenceBuilder(new Pose2d(-36.32, 67.20, Math.toRadians(270.00)))
//                    .splineTo(new Vector2d(-44.50, 37.59), Math.toRadians(240.00))
//                    .build();
//            drive.setPoseEstimate(untitled0.start());
//            trajectorySequenceArrayList.add(untitled0);
//            nameList.add("blue noncanvas nontruss right");
//
//            untitled0 = drive.trajectorySequenceBuilder(new Pose2d(-36.88, 65.65, Math.toRadians(266.55)))
//                    .splineTo(new Vector2d(-36.60, 26.87), Math.toRadians(270.00))
//                    .build();
//            drive.setPoseEstimate(untitled0.start());
//            trajectorySequenceArrayList.add(untitled0);
//            nameList.add("blue noncanvas nontruss center");
//
//            untitled0 = drive.trajectorySequenceBuilder(new Pose2d(-36.88, 65.65, Math.toRadians(266.55)))
//                    .splineTo(new Vector2d(-27.86, 38.71), Math.toRadians(310.00))
//                    .build();
//            drive.setPoseEstimate(untitled0.start());
//            trajectorySequenceArrayList.add(untitled0);
//            nameList.add("blue noncanvas nontruss left");
//
//            untitled0 = drive.trajectorySequenceBuilder(new Pose2d(-36.88, 65.65, Math.toRadians(266.55)))
//                    .splineToSplineHeading(new Pose2d(-27.86, 38.71, Math.toRadians(310.00)), Math.toRadians(310.00))
//                    .splineToSplineHeading(new Pose2d(-20.94, 17.28, Math.toRadians(-1.13)), Math.toRadians(-1.13))
//                    .splineToSplineHeading(new Pose2d(54.79, 29.41, Math.toRadians(0.00)), Math.toRadians(29.74))
//                    .build();
//            drive.setPoseEstimate(untitled0.start());
//            trajectorySequenceArrayList.add(untitled0);
//            nameList.add("blue noncanvas truss left");
//
//            untitled0 = drive.trajectorySequenceBuilder(new Pose2d(-36.88, 65.65, Math.toRadians(266.55)))
//                    .splineToSplineHeading(new Pose2d(-34.62, 27.01, Math.toRadians(270.00)), Math.toRadians(270.00))
//                    .splineToSplineHeading(new Pose2d(-20.94, 17.28, Math.toRadians(-1.13)), Math.toRadians(-1.13))
//                    .splineToSplineHeading(new Pose2d(54.79, 29.41, Math.toRadians(0.00)), Math.toRadians(29.74))
//                    .build();
//            drive.setPoseEstimate(untitled0.start());
//            trajectorySequenceArrayList.add(untitled0);
//            nameList.add("blue noncanvas truss center");
//
//            untitled0 = drive.trajectorySequenceBuilder(new Pose2d(-36.88, 65.65, Math.toRadians(266.55)))
//                    .splineToSplineHeading(new Pose2d(-43.93, 38.86, Math.toRadians(240.00)), Math.toRadians(240.00))
//                    .lineToSplineHeading(new Pose2d(-20.94, 17.28, Math.toRadians(-1.13)))
//                    .splineToSplineHeading(new Pose2d(54.79, 29.41, Math.toRadians(0.00)), Math.toRadians(29.74))
//                    .build();
//            drive.setPoseEstimate(untitled0.start());
//            trajectorySequenceArrayList.add(untitled0);
//            nameList.add("blue noncanvas truss right");

            untitled0 = drive.trajectorySequenceBuilder(new Pose2d(-36.46, 64.67, Math.toRadians(268.42)))
                    .splineToSplineHeading(new Pose2d(-28.98, 39.14, Math.toRadians(-41.91)), Math.toRadians(-41.91))
                    .lineToSplineHeading(new Pose2d(-30.96, 17.28, Math.toRadians(-1.46)))
                    .splineToSplineHeading(new Pose2d(50.28, 24.61, Math.toRadians(15.00)), Math.toRadians(15.00))
                    .splineToSplineHeading(new Pose2d(51.83, 14.32, Math.toRadians(-35.96)), Math.toRadians(-35.96))
                    .splineToSplineHeading(new Pose2d(64.95, 14.32, Math.toRadians(0.00)), Math.toRadians(0.00))
                    .build();
            drive.setPoseEstimate(untitled0.start());
            trajectorySequenceArrayList.add(untitled0);
            nameList.add("blue noncanvas left");

            untitled0 = drive.trajectorySequenceBuilder(new Pose2d(-36.46, 64.67, Math.toRadians(268.42)))
                    .splineToSplineHeading(new Pose2d(-35.89, 28.84, Math.toRadians(270.00)), Math.toRadians(270.00))
                    .lineToSplineHeading(new Pose2d(-56.77, 19.67, Math.toRadians(-1.46)))
                    .splineToSplineHeading(new Pose2d(50.28, 24.61, Math.toRadians(15.00)), Math.toRadians(15.00))
                    .splineToSplineHeading(new Pose2d(51.83, 14.32, Math.toRadians(-35.96)), Math.toRadians(-35.96))
                    .splineToSplineHeading(new Pose2d(64.95, 14.32, Math.toRadians(0.00)), Math.toRadians(0.00))
                    .build();
            drive.setPoseEstimate(untitled0.start());
            trajectorySequenceArrayList.add(untitled0);
            nameList.add("blue noncanvas center");

            untitled0 = drive.trajectorySequenceBuilder(new Pose2d(-36.46, 64.67, Math.toRadians(268.42)))
                    .splineToSplineHeading(new Pose2d(-47.04, 41.54, Math.toRadians(270.00)), Math.toRadians(270.00))
                    .lineToSplineHeading(new Pose2d(-59.59, 39.56, Math.toRadians(241.80)))
                    .lineToSplineHeading(new Pose2d(-56.77, 19.67, Math.toRadians(-1.46)))
                    .splineToSplineHeading(new Pose2d(50.28, 24.61, Math.toRadians(15.00)), Math.toRadians(15.00))
                    .splineToSplineHeading(new Pose2d(51.83, 14.32, Math.toRadians(-35.96)), Math.toRadians(-35.96))
                    .splineToSplineHeading(new Pose2d(64.95, 14.32, Math.toRadians(0.00)), Math.toRadians(0.00))
                    .build();
            drive.setPoseEstimate(untitled0.start());
            trajectorySequenceArrayList.add(untitled0);
            nameList.add("blue noncanvas right");

            untitled0 = drive.trajectorySequenceBuilder(new Pose2d(-36.46, -64.67, Math.toRadians(91.58)))
                    .splineToSplineHeading(new Pose2d(-47.32, -38.57, Math.toRadians(90.00)), Math.toRadians(90.00))
                    .lineToSplineHeading(new Pose2d(-26.87, -18.97, Math.toRadians(361.46)))
                    .splineToSplineHeading(new Pose2d(50.28, -24.61, Math.toRadians(345.00)), Math.toRadians(345.00))
                    .splineToSplineHeading(new Pose2d(51.83, -14.32, Math.toRadians(395.96)), Math.toRadians(395.96))
                    .splineToSplineHeading(new Pose2d(64.95, -14.32, Math.toRadians(360.00)), Math.toRadians(360.00))
                    .build();
            drive.setPoseEstimate(untitled0.start());
            trajectorySequenceArrayList.add(untitled0);
            nameList.add("red noncanvas left");

            untitled0 = drive.trajectorySequenceBuilder(new Pose2d(-36.60, -65.23, Math.toRadians(90.00)))
                    .splineToSplineHeading(new Pose2d(-35.75, -31.66, Math.toRadians(90.00)), Math.toRadians(90.00))
                    .lineToSplineHeading(new Pose2d(-53.52, -25.46, Math.toRadians(6.51)))
                    .splineToSplineHeading(new Pose2d(-7.97, -17.70, Math.toRadians(-9.26)), Math.toRadians(-9.26))
                    .splineToSplineHeading(new Pose2d(50.99, -25.74, Math.toRadians(-24.30)), Math.toRadians(-24.30))
                    .splineToSplineHeading(new Pose2d(57.76, -16.43, Math.toRadians(0.43)), Math.toRadians(0.43))
                    .build();
            drive.setPoseEstimate(untitled0.start());
            trajectorySequenceArrayList.add(untitled0);
            nameList.add("red noncanvas center");

            untitled0 = drive.trajectorySequenceBuilder(new Pose2d(-36.60, -65.23, Math.toRadians(90.00)))
                    .splineToSplineHeading(new Pose2d(-25.74, -37.59, Math.toRadians(90.00)), Math.toRadians(90.00))
                    .lineToSplineHeading(new Pose2d(-33.50, -23.91, Math.toRadians(6.51)))
                    .splineToSplineHeading(new Pose2d(-7.97, -17.70, Math.toRadians(-9.26)), Math.toRadians(-9.26))
                    .splineToSplineHeading(new Pose2d(50.99, -25.74, Math.toRadians(-24.30)), Math.toRadians(-24.30))
                    .splineToSplineHeading(new Pose2d(57.76, -16.43, Math.toRadians(0.43)), Math.toRadians(0.43))
                    .build();
            drive.setPoseEstimate(untitled0.start());
            trajectorySequenceArrayList.add(untitled0);
            nameList.add("red noncanvas right");


            //TODO: add blue side with full range of motion for parking and other drop spots. also do red.



        }

        waitForStart();
        viperslidewatcher.start(); // Start the watchdog thread
        while(opModeIsActive()){

            selectedProgramCounter += ((-gamepad1.left_trigger+gamepad1.right_trigger)/8000);
            selectedProgram = (int) Math.round(selectedProgramCounter);
            if(gamepad1.a){
                drive.followTrajectorySequence(trajectorySequenceArrayList.get(selectedProgram));

            }

            if (isStopRequested()) {
                viperslidewatcher.interrupt();
                try {
                    viperslidewatcher.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }

    }   // end runOpMode()

}   // end class
