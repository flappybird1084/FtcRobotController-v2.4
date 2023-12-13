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

package org.firstinspires.ftc.teamcode.autonomous;

import android.util.Size;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.teamcode.RobotHardware;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.tfod.TfodProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This 2023-2024 OpMode illustrates the basics of TensorFlow Object Detection,
 * including Java Builder structures for specifying Vision parameters.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list.
 */
@TeleOp(name = "test roadrunner gui", group = "Auto Concept")

public class nonCanvasRedToCanvasTestThingUsingTheRoadRunnerGUIForGeneratingPathsIntoTrajectoriesForTheRobot extends LinearOpMode {

    ArrayList<TrajectorySequence> trajectorySequenceArrayList = new ArrayList<>();
    ArrayList<String> nameList = new ArrayList<>();
    int selectedProgram = 0;
    double selectedProgramCounter = 0;


    @Override
    public void runOpMode() {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

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

            TrajectorySequence untitled0 = drive.trajectorySequenceBuilder(new Pose2d(-36.74, -65.23, Math.toRadians(90.00)))
                    .splineTo(new Vector2d(-34.48, -20.10), Math.toRadians(90.00))
                    .splineTo(new Vector2d(9.24, -5.99), Math.toRadians(21.58))
                    .splineTo(new Vector2d(47.32, -39.42), Math.toRadians(0.00))
                    .build();
            drive.setPoseEstimate(untitled0.start());

            trajectorySequenceArrayList.add(untitled0);
            nameList.add("rian test under truss 2");

        }

        waitForStart();

        while(opModeIsActive()){
            telemetry.addData("Selected Program: ", selectedProgram);
            telemetry.addData("Selected Program Counter", selectedProgramCounter);
            if(selectedProgram < nameList.size()){
                telemetry.addData("Selected Program Name: ", nameList.get(selectedProgram));
            }
            else{
                telemetry.addData("Selected Program Name: ", "none + namelist len: ", +nameList.size());
            }
            selectedProgramCounter += ((-gamepad1.left_trigger+gamepad1.right_trigger)/8000);
            selectedProgram = (int) Math.round(selectedProgramCounter);
            if(gamepad1.a){
                drive.followTrajectorySequence(trajectorySequenceArrayList.get(selectedProgram));
            }
            telemetry.update();
        }

    }   // end runOpMode()

}   // end class
