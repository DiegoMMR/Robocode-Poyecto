/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myfirstbot;
import java.awt.Color;
import robocode.*;



/**
 *
 * @author User
 */
public class MyfirstBot extends AdvancedRobot
{
    final double pi = Math.PI;
    long inCorner;
    Enemy target;
    int hit = 0;
    //Entero que nos indica la direccion con la que debemos movernos 
    int direccion = 1;
    double potencia;
    
    public void run()
    {
    setColors(Color.BLUE,Color.GRAY,Color.BLACK);
        inCorner=getTime();
        target = new Enemy();
        target.distance = 900000000;
        
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        turnRadarRightRadians(2*pi);  
        
        while(true) {
        
        calculoMovimiento(); 
        //Calcula la potencia de 
        calculoPotencia(); 
        //Buscamos enemigos
        escanear();            
        //apuntamos al enemigo
        apuntar();
        //Calculado ya todo, disparamos             
        fire(potencia); 
        //Executa todas los recxultados de los metodos anteriores.
        execute();        
                
        }
        
    }
        //Calculamos movimiento a realizar
  void calculoMovimiento() 
  { //Si estamos mas lejos de 300, nos acercamos con un angulo de 45 grados. 
            if(target.distance <300){ 
//Si ha pasado un tiempo determinado des el ultimo movimiento; 
if (getTime()%20 == 0)  {  
    //Establecemos la direccion del movimiento
    if(hit<4){ //Cambiamos de direccion;
        direccion *= -1;
        } else{
        if (getTime()%60 == 0)  {
            hit = 0;
        }
    }
        
    setAhead(direccion*(350+(int)((int)Math.random()*350)));
        }
        setTurnRightRadians(target.bearing + (pi/2));
    }
            else{
                setAhead(300);
            }
            setTurnRightRadians(target.bearing + (pi/4));
        }
        
  public void onHitByBullet(HitRobotEvent e)
  {
      hit = hit +1;
      
      if (e.getBearing() > -10 && e.getBearing() < 10) {
			fire(3);
		}
		if (e.isMyFault()) {
			turnRight(10);
		}
      
  }
  


  public void onHitWall(HitWallEvent event) { 
      long temp = getTime();
      if ((temp - inCorner) < 100){
          setBack(100);
          setTurnRightRadians(target.bearing);
          execute();
          setAhead(300);
          execute();
      }
      inCorner=temp;
  }
  void escanear() {
      double radarOffset;
      if (getTime() - target.ctime > 5) {
          radarOffset = 360;
      }
      else {
          radarOffset = getRadarHeadingRadians()-absbearing(getX(),getY(),target.x,target.y);
      
      if (radarOffset < 0)
          radarOffset -= pi/7;
          else
          radarOffset += pi/7;
  
  }
   setTurnRadarLeftRadians(NormaliseBearing(radarOffset));
  }
  
  void apuntar() {
      long time = getTime() + (int)(target.distance/(20- (3*(400/target.distance))));
      
      double gunOffset = getGunHeadingRadians() - absbearing(getX(),getY(),target.guessX(time),target.guessY(time));
      setTurnGunLeftRadians(NormaliseBearing(gunOffset));
  }
  
  double NormaliseBearing(double ang) {
      if (ang > pi)
          ang -= 2*pi;
      if (ang < -pi) 
          ang += 2*pi; 
      return ang;
  }
  
  double NormaliseHeading(double ang) {
      if (ang > 2*pi)
          ang -= 2*pi;
      if (ang < 0)
          ang += 2*pi;
      return ang; 
  }
  
  public double distancia( double x1,double y1, double x2,double y2 )
  { 
      return Math.sqrt( (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1) );
  }
  
  public double absbearing( double x1,double y1, double x2,double y2 )
  { double xo = x2-x1; double yo = y2-y1;
  double h = distancia( x1,y1, x2,y2 ); 
  if( xo > 0 && yo > 0 )  
  { return Math.asin( xo / h ); 
  } if( xo > 0 && yo < 0 ) 
  { return Math.PI - Math.asin( xo / h ); 
  } if( xo < 0 && yo < 0 )     
  { return Math.PI + Math.asin( -xo / h );
  } if( xo < 0 && yo > 0 )  
  { return 2.0*Math.PI - Math.asin( -xo / h );   
  } return 0;   
  }
  
  public void onScannedRobot(ScannedRobotEvent e) {
      System.out.println("Escaneo a :" + e.getName()); if ((e.getDistance() < target.distance)||(target.name == e.getName()))
  {
      System.out.println("He entrado");
      target.name = e.getName();
      target.bearing = e.getBearingRadians(); 
      target.head = e.getHeadingRadians();
      target.ctime = getTime();      
      target.speed = e.getVelocity();
      target.distance = e.getDistance();
      double absbearing_rad = (getHeadingRadians()+e.getBearingRadians())%(2*pi);
      target.x = getX()+Math.sin(absbearing_rad)*e.getDistance();
      target.y = getY()+Math.cos(absbearing_rad)*e.getDistance();
      
  }
       }
  
  
  
  void calculoPotencia() {
      potencia = 500/target.distance;
  }
  public void onRobotDeath(RobotDeathEvent e) {
      if (e.getName() == target.name) 
          target.distance = 9000000;
  }
  
  public void onHitRobot(HitRobotEvent event)
  { if (event.getName() != target.name)
      target.distance = 9000000;
  }
  
  public void onWin(WinEvent event) {
      while(true){
          execute();
          setTurnGunLeftRadians(pi/2);
  }
  }

  class Enemy {
      String name;
      //Almacena la orientacion
      public double bearing;
      //Almacena el apuntamiento
      public double head;
      //Tiempo en que ha sido detectado
      public long ctime;
      //Velocidad del enemigo
      public double speed;
      //Posicion del enemigo
      public double x,y;
      //Distancia del enemigo publicdouble distance;
      public double distance;
      
      public String getname(){ return name;          } 
      public double getbearing(){ return bearing;            } 
      public double gethead(){ return head;          } 
      public long getctime(){ return ctime;         }
      public double getspeed(){ return speed;         }
      
      public double getx(){ return x;             } 
      public double gety(){ return y;             } 
      public double getdistance(){ return distance;   }
      
      //Metodos para intentar adivinar la posicion futura del tanque.
      public double guessX(long when)       { long diff = when - ctime; return x+Math.sin(head)*speed*diff;       } 
      public double guessY(long when)       { long diff = when - ctime; return y+Math.cos(head)*speed*diff;       }    }
      
      
      
  }
