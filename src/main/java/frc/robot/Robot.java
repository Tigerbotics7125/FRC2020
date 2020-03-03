/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  private Shooter shooter;
  private Spark hatch;

  private WPI_TalonSRX ballin;
  private WPI_TalonSRX drawBridge;

  private Joystick gamePad;
  private MB1013 leftEye;
  private MB1013 rightEye;

  private WPI_VictorSPX leftMotor;
  private WPI_VictorSPX rightMotor;

  private DifferentialDrive chassis;

  // private UsbCamera cam1;
  // private UsbCamera cam2;
  private boolean dbDown;
  private boolean autonForward;
  private double autonTime;
  private DigitalInput limitSwitch;
  // Cameras cameras;

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   * 
   * @param server
   */
  @Override
  public void robotInit() {
    limitSwitch = new DigitalInput(2);
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    leftMotor = new WPI_VictorSPX(2);

    rightMotor = new WPI_VictorSPX(1);
    ballin = new WPI_TalonSRX(0);
    drawBridge = new WPI_TalonSRX(4);

    shooter = new Shooter(0, 1, 2, 3, 3);
    // hatch = new Spark(1);

    gamePad = new Joystick(0);
    rightEye = new MB1013(1, "Right Eye");
    leftEye = new MB1013(0, "Left Eye");
    chassis = new DifferentialDrive(leftMotor, rightMotor);

    // cam1 = CameraServer.getInstance().startAutomaticCapture(0);
    // cam2 = CameraServer.getInstance().startAutomaticCapture(1);

    dbDown = false;
    autonForward = false;
    // server= CameraServer.getInstance();
    // server.setQuality(50);
    CameraServer.getInstance().startAutomaticCapture(0);
    CameraServer.getInstance().startAutomaticCapture(1);

  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like diagnostics that you want ran during disabled, autonomous,
   * teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable chooser
   * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
   * remove all of the chooser code and uncomment the getString line to get the
   * auto name from the text box below the Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure below with additional strings. If using the SendableChooser
   * make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
    autonTime = Timer.getFPGATimestamp();
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
    case kCustomAuto:
      // Put custom auto code here
      break;
    case kDefaultAuto:
    default:
      if (limitSwitch.get())
      drawBridge.set(ControlMode.PercentOutput, -.5);
      else 
      drawBridge.set(ControlMode.PercentOutput, 0);
      double curTime = Timer.getFPGATimestamp();
      if (curTime < autonTime + 2)
      chassis.arcadeDrive(.5, 0);
    else {
      chassis.arcadeDrive(0, 0);
      shooter.autonShoot(ballin);

    }
    /*if (!dbDown) {
        do {
          drawBridge.set(ControlMode.PercentOutput, -.5);
        } while (limitSwitch.get());
        // Put default auto code here
        drawBridge.set(ControlMode.PercentOutput, 0);
        dbDown = true;
        autonTime = Timer.getFPGATimestamp();
      }
      double curTime = Timer.getFPGATimestamp();
      if (curTime < autonTime + 2)
        chassis.arcadeDrive(.5, 0);
      else
        chassis.arcadeDrive(0, 0);
*/
      break;
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    double ballButton = gamePad.getRawAxis(5) * .75;
    double forw = gamePad.getRawAxis(1) * .85;
    if(forw<0){
      forw *= forw;
      forw *= -1;
    }
    else{
      forw *= forw;
    }
 
    /* positive is forward */
   
    boolean drawUp = gamePad.getRawButton(1);

    boolean drawDown = gamePad.getRawButton(3);
    double turn = gamePad.getRawAxis(0) * .85; /* positive is right */
    /* deadband gamepad 10% */
    ballin.set(ControlMode.PercentOutput, ballButton);
    if (drawDown) {
      drawBridge.set(ControlMode.PercentOutput, .75);
    } else if (drawUp) {
      drawBridge.set(ControlMode.PercentOutput, -.5);
    } else {
      drawBridge.set(ControlMode.PercentOutput, 0);
    }
    if (Math.abs(forw) < 0.10) {

      forw = 0;

    }
    if (Math.abs(turn) < 0.10) {

      turn = 0;

    }
    boolean straightenUp = gamePad.getRawButton(2);
    boolean shootHer = gamePad.getRawButton(5);
    boolean startSpin = gamePad.getRawButton(6);
    double spinCW = gamePad.getRawAxis(3);
    shooter.shoot(shootHer);
    if (startSpin)
      shooter.shootStage1();
    else if (spinCW > 0)
      shooter.spinColorWheel(spinCW);
    else
      shooter.stop();

    if (straightenUp)
      getStraight();
    else
      chassis.arcadeDrive(forw, turn);
    /*
     * double speed=gamePad.getRawAxis(1)*0.5; intake.set(speed);
     * hatch.set(-1*speed); pickUp.set(ControlMode.PercentOutput,speed);
     */
    leftEye.updateDashboard();
    rightEye.updateDashboard();
  }

  private void getStraight() {
    double diff = rightEye.getDistance() - leftEye.getDistance();
    double turnSpeed = 0;
    if (diff > 10)
      turnSpeed = .5;
    else if (diff < -10)
      turnSpeed = -.5;
    else
      turnSpeed = diff / 10;
    // if (leftEye.getDistance() > rightEye.getDistance() + 0.1)
    chassis.arcadeDrive(0, turnSpeed);
    // else if (leftEye.getDistance() < rightEye.getDistance() - 0.1)
    // chassis.arcadeDrive(0, -0.5);

  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
