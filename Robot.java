// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

/*
NOTES: GENERAL THINGS TO REMEMBER
- When making IDs, don't start counting w/ 0, start at 1 (or a num larger than 0)
  ^ This is bc when connecting unknown devices it initializes as 0
- Motor controls are numbered

How to connect to the robot (aka the radio):
- Change wifi connection to FRC-10366
- Password: FRC10366
- Search "Driver Station" from Windows searchbar and open the app
- Press the settings (gear) button on the left 
- Make sure 10366 is inputted as the team number

How to test if the code runs (must be connected to robot):
- Press the WPILib Command Palette (Red and blue logo next to the play button on the top right)
- Type "Deploy Robot Code" and run it

 */

package frc.robot;

import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.SparkMax;


import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;




/**
 * This is a demo program showing the use of the Differentiq1``alDrive class. Runs the motors with
 * arcade steering.
 */
public class Robot extends TimedRobot {
  /*
  NOTE: TERMINOLOGY 
  There are LEADS, and there are followers. The followers simply copy what the lead does
  Motor controllers are able to copy behaviors

  Might need to change connected Ids to properly work
   */
  private final SparkMax m_leftMotor = new SparkMax(1, MotorType.kBrushed);
  private final SparkMax m_leftMotor_follower = new SparkMax(2, MotorType.kBrushed);
  private final SparkMax m_rightMotor = new SparkMax(3, MotorType.kBrushed);
  private final SparkMax m_rightMotor_follower = new SparkMax(5, MotorType.kBrushed);

  /*make an object for the elevator motor and the arm motor*/

  private final SparkMax elevatorMotor = new SparkMax(10, MotorType.kBrushless);
  private final SparkMax armMotor = new SparkMax(6, MotorType.kBrushless);//not brushed

  private final DifferentialDrive m_robotDrive =
      new DifferentialDrive(m_leftMotor::set, m_rightMotor::set);
  //ElevatorSubsystem m_ElevatorSubsystem = new ElevatorSubsystem(10,5); //work on this

  // port might not be 0 later, change if needed
  private final Joystick m_stick = new Joystick(0);

  /** Called once at the beginning of the robot program. */
  //private double timeStart;
  private Timer timer = new Timer();

  private double armSpeed = 0.25f;

  public Robot() {
    // timeStart = Timer.getFPGATimestamp(); 
    SendableRegistry.addChild(m_robotDrive, m_leftMotor);
    SendableRegistry.addChild(m_robotDrive, m_rightMotor);
    //SendableRegistry.addChild(m_robotDrive, elevatorMotor);
   // SendableRegistry.addChild(m_robotDrive, armMotor);
    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    m_rightMotor.configure(new SparkMaxConfig().inverted(false), ResetMode.kNoResetSafeParameters, PersistMode.kNoPersistParameters);
    m_rightMotor_follower.configure(new SparkMaxConfig().follow(m_rightMotor, false), ResetMode.kNoResetSafeParameters, PersistMode.kNoPersistParameters);
    
    m_leftMotor.configure(new SparkMaxConfig().inverted(true), ResetMode.kNoResetSafeParameters, PersistMode.kNoPersistParameters);
    m_leftMotor_follower.configure(new SparkMaxConfig().follow(m_leftMotor, false), ResetMode.kNoResetSafeParameters, PersistMode.kNoPersistParameters);

    elevatorMotor.configure(new SparkMaxConfig().inverted(false), ResetMode.kNoResetSafeParameters, PersistMode.kNoPersistParameters); // invert based on how the robot elevator is set up
    armMotor.configure(new SparkMaxConfig().inverted(false), ResetMode.kNoResetSafeParameters, PersistMode.kNoPersistParameters); // invert based on how the robot elevator is set up
  }

  @Override
  public void autonomousInit() {
    // this only runs ONCE, so this will get the time the autonomous mode is on, then keep that one timeframe
    //double startTime = Timer.getFPGATimestamp();

    timer.start();
  }

  public void robotInit() {
    SmartDashboard.putNumber("Coral Output Speed", armSpeed);
  }

  public void robotPeriodic() {
    armSpeed = SmartDashboard.getNumber("Coral Output Speed", 0.25);
  }


  @Override
  public void autonomousPeriodic() {
    // timer starts counting as soon as the robot turns on
    // add a manual switch 
    //final double timeStart = Timer.getFPGATimestamp();
    double time = timer.get(); // returns a double of the time in seconds
    
    //System.out.println(time);
    
    if (time < 3) {
      m_robotDrive.tankDrive(0.4, .4);
    } 
    else if (time< 6) {
      m_robotDrive.tankDrive(.4, .4);
    }
    else if (time < 9) {
      m_robotDrive.tankDrive(.6,.6);

    } 
    else if (time < 12) {
      m_robotDrive.tankDrive(.4, .4);

    }
    
    else if (time < 15) {
      m_robotDrive.tankDrive(-.4, .4);
    
    }
    else {
      m_robotDrive.tankDrive(.2, -.2);
      
    }

  }

  @SuppressWarnings("deprecation")
  @Override
  public void teleopPeriodic() {
    boolean precisionMode = m_stick.getRawButton(6);
    if (precisionMode) {
      m_robotDrive.arcadeDrive(-m_stick.getY() * 0.50, -m_stick.getX() * 0.50);
    } else {
        // Drive with arcade drive.`  
      // That means that the Y axis drives forward
      // and backward, and the X turns left and right.
      m_robotDrive.arcadeDrive(-m_stick.getY() * 0.75, -m_stick.getX() * 0.75);
      // elevatorDrive.tankDrive(-0.5, -0.5);
    }
    

    boolean elevatorSafety = m_stick.getRawButton(5);
    double speedElevator = m_stick.getRawAxis(5);
    // elevator code for when we control it
     if (elevatorSafety == true) {
      if (precisionMode) {
        elevatorMotor.set(-speedElevator * 0.5);
      }                                                                                                                                                                                                                                                                                                        
      else {  
      elevatorMotor.set(-speedElevator);
      }
    }
    else {;
      elevatorMotor.set(0);
      elevatorMotor.setInverted(false);
    }
    if (m_stick.getRawButton(2)) {
      armMotor.set(armSpeed);
    }
    else if (m_stick.getRawButton(3)) {
      armMotor.set(0.0);
    }
    


    SmartDashboard.putBoolean("Elevator Safety Switch", elevatorSafety);
    // SmartDashboard.putBoolean("ElevatorUp", elevatorUp);
    // SmartDashboard.putBoolean("ElevatorDown", elevatorDown);
    

    // arm

    


  }
}
