package frc.robot;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This is a sample program demonstrating how to use an ultrasonic sensor and
 * proportional control to maintain a set distance from an object.
 */

public class MB1013 {
  // factor to convert sensor values to a distance in inches
  private static final double K_VALUE_TO_INCHES = 0.00977;

  private AnalogInput m_ultrasonic;
  private DigitalOutput dioFlag;
  private double senseTime;
//private double distance;
  private String label;
  private ArrayList<Double> dataSet; 
  public MB1013(int port, String string) {
    senseTime = Timer.getFPGATimestamp();
    m_ultrasonic = new AnalogInput(port);
    dioFlag = new DigitalOutput(port);
  //  distance = 0;
    dioFlag.set(false);
    label=string;
    dataSet=new ArrayList<Double>();
  }

  public double getDistance() {
    double curTime = Timer.getFPGATimestamp();
    double dist=0;
    if (curTime > senseTime + .1) {
      dioFlag.set(true);
      Timer.delay(0.0005);
      dist = m_ultrasonic.getVoltage() / K_VALUE_TO_INCHES;
      dioFlag.set(false);
      dataSet.add(0,dist);
      senseTime=curTime;

    }
    
    return getAverage();
  }

  private double getAverage() {
    double sum=0;
    if(dataSet.size()>10)
      dataSet.remove(10);
    for(Double data:dataSet){
      sum+=data.doubleValue();
    }
    return sum/dataSet.size();
  }

  public void updateDashboard() {
    SmartDashboard.putNumber(label+" "+m_ultrasonic.getChannel(), getDistance());
  }
}