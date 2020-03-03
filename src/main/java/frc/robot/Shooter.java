package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;

public class Shooter {
    private Spark intake1a;
    private Spark intake1b;
    private Spark intake2a;
    private Spark intake2b;
    private WPI_TalonSRX guideWheel;
    private boolean shooting;
    private double cycleStart;
    

    public Shooter(int port1, int port2, int port3, int port4, int guidePort) {
        intake1a = new Spark(port1);
        intake1b = new Spark(port2);
        intake2a = new Spark(port3);
        intake2b = new Spark(port4);
        guideWheel = new WPI_TalonSRX(guidePort);
        shooting=false;
        stop();
        cycleStart=0;
    }

    public void shootStage1() {
        double speed = .5;
        intake1a.set(speed);
        intake1b.set(speed);
        intake2a.set(speed);
        intake2b.set(-1 * speed);
    }

    public void shoot(boolean trigger) {
        if (trigger)
            guideWheel.set(-.3);
        else
            guideWheel.set(0);
    }

    public void stop() {
        intake1a.set(0);
        intake1b.set(0);
        intake2a.set(0);
        intake2b.set(0);
        //guideWheel.set(0);
    }

	public void toggleWheels() {
        if(!shooting){
            shootStage1();
            shooting=true;
        }
        else{
            stop();
            shooting=false;
        }

	}

	public void spinColorWheel(double spinCW) {
        spinCW*=.25;
        intake1a.set(spinCW);
        intake1b.set(spinCW);
        intake2a.set(-1 * spinCW);
        intake2b.set(spinCW);
	}

	public void autonShoot(WPI_TalonSRX ballin) {
        double curTime = Timer.getFPGATimestamp();
        if(cycleStart==0){
         cycleStart = Timer.getFPGATimestamp();
        }
        shootStage1();
        if (curTime<cycleStart+1&&curTime>cycleStart+.5){
            shoot(true);
        }
        else {
            shoot(false);
        }
        if (curTime>cycleStart+1&&curTime<cycleStart+4){
            ballin.set(ControlMode.PercentOutput, -.85);
        }
        else {
            ballin.set(ControlMode.PercentOutput, 0);
        }
        if (curTime>cycleStart+4.5){
            cycleStart = Timer.getFPGATimestamp();
        }
	}

}