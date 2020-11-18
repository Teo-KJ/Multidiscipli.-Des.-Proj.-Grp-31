double speed_L = 280;
double speed_R = 301;
double tick_L = 0;
double tick_R = 0;
double Er_ticks = 0;
int ticks_to_move = 0;
double RPM_L = 0;                                // To Store RPM of Left Motor 
double RPM_R = 0;                                 // To Store RPM of Right Motor 
double PID_RPM_R = 0;
double PID_RPM_L = 0;
double setpoint_R = 100;
double setpoint_L = 98; //96
const double Kp_L = 0.12, Ki_L = 0.0, Kd_L = 0.0; //0.15
const double Kp_R = 0.05, Ki_R = 0.0, Kd_R = 0.0; //0.05
PID myPIDL(&RPM_L, &PID_RPM_L, &setpoint_L, Kp_L, Ki_L, Kd_L, DIRECT);
PID myPIDR(&RPM_R, &PID_RPM_R, &setpoint_R, Kp_R, Ki_R, Kd_R, DIRECT);

void setupPID(){
  myPIDL.SetMode(AUTOMATIC);
  myPIDL.SetOutputLimits(-350,350);
  myPIDL.SetSampleTime(10);
  myPIDR.SetMode(AUTOMATIC);
  myPIDR.SetOutputLimits(-350,350);
  myPIDR.SetSampleTime(10);
}

void setupMotor(){
    motor.setSpeeds(speed_R,speed_L);
}

void moveForward(int grid){ //move forward 1 grid
  initMove();
  pidRPM();
  ticks_to_move = 270 * grid;
  speed_L = 280; //275
  speed_R = 301;
  motor.setSpeeds(speed_L, speed_R);
  while(tick_R < ticks_to_move || tick_L < ticks_to_move){
    getRPM();
    myPIDL.Compute();
    myPIDR.Compute();
    speed_L += PID_RPM_L;
    speed_R += PID_RPM_R;
    motor.setSpeeds(speed_L, speed_R);
  }
  delay(40);
  initEnd();
}

void moveForward1(){ //move forward 1 tick for calibration
  initMove();
  pidRPM();
  ticks_to_move = 1;
  speed_L = 100; //275
  speed_R = 100;
  setpoint_R = 21;
  setpoint_L = 20;
  motor.setSpeeds(speed_L, speed_R);
  while(tick_R < ticks_to_move || tick_L < ticks_to_move){
    getRPM();
    myPIDL.Compute();
    myPIDR.Compute();
    speed_L += PID_RPM_L;
    speed_R += PID_RPM_R;
    motor.setSpeeds(speed_L, speed_R);
  }
  initEnd();
  setpoint_R = 100;
  setpoint_L = 98;
}

void moveBack(int grid){ // move back 1 grid
  initMove();
  pidRPM();
  ticks_to_move = 270 * grid;
  speed_L = -270;
  speed_R = -300;
  motor.setSpeeds(speed_L, speed_R);
  while(tick_R < ticks_to_move || tick_L < ticks_to_move){
    getRPM();
    myPIDL.Compute();
    myPIDR.Compute();
    speed_L -= PID_RPM_L;
    speed_R -= PID_RPM_R;
    motor.setSpeeds(speed_L, speed_R);
  }
  initEnd();
}

void moveBack1(){ // move back 1 tick for calibration
  initMove();
  pidRPM();
  ticks_to_move = 1;
  speed_L = -100;
  speed_R = -100;
  motor.setSpeeds(speed_L, speed_R);
  while(tick_R < ticks_to_move || tick_L < ticks_to_move){
    getRPM();
    myPIDL.Compute();
    myPIDR.Compute();
    speed_L -= PID_RPM_L;
    speed_R -= PID_RPM_R;
    motor.setSpeeds(speed_L, speed_R);
  }
  initEnd();
}
void turnLeft1(){ // turn left 1 tick for calibration
  initMove();
  pidRPM();
  speed_L = -278;
  speed_R = 300;
  ticks_to_move = 1;
  motor.setSpeeds(speed_L, speed_R);
  while(tick_L < ticks_to_move || tick_R < ticks_to_move ){
   getRPM();
   myPIDL.Compute();
   myPIDR.Compute();
   speed_L -= PID_RPM_L;
   speed_R += PID_RPM_R;
   motor.setSpeeds(speed_L, speed_R);
  }
  initEnd();
}
void turnRight1(){ // turn right 1 tick for calibration
  initMove();
  pidRPM();
  speed_L = 278;
  speed_R = -300;
  ticks_to_move = 1;
  motor.setSpeeds(speed_L, speed_R);
  while(tick_L < ticks_to_move || tick_R < ticks_to_move ){
   getRPM();
   myPIDL.Compute();
   myPIDR.Compute();
   speed_L += PID_RPM_L;
   speed_R -= PID_RPM_R;
   motor.setSpeeds(speed_L, speed_R);
  }
  initEnd();
}

void turnRight(){ // turn right 90 degree
  initMove();
  pidRPM();
  speed_L = 278;
  speed_R = -300;//386
  ticks_to_move = 370;
  motor.setSpeeds(speed_L, speed_R);
  Serial.println("Turn Right");
  while(tick_L < ticks_to_move || tick_R < ticks_to_move ){
   getRPM();
   myPIDL.Compute();
   myPIDR.Compute();
   speed_L += PID_RPM_L;
   speed_R -= PID_RPM_R;
   motor.setSpeeds(speed_L, speed_R);
  }
  delay(20);
  initEnd();
}
void turnLeft(){ // turn left 90 degree
  initMove();
  pidRPM();
  speed_L = -278;
  speed_R = 300;
  ticks_to_move = 362;//380
  motor.setSpeeds(speed_L, speed_R);
  Serial.println("Turn Left");
  while(tick_L < ticks_to_move || tick_R < ticks_to_move ){
   getRPM();
   myPIDL.Compute();
   myPIDR.Compute();
   speed_L -= PID_RPM_L;
   speed_R += PID_RPM_R;
   motor.setSpeeds(speed_L, speed_R);
  }
  delay(20);
  initEnd();
}

void turnRight_D(float degree){ // turn right 1 = 10 degree, 9 = 90 degreee
  initMove();
  pidRPM();
  speed_L = 278;
  speed_R = -300;
  ticks_to_move = (44* degree);
  motor.setSpeeds(speed_L, speed_R);
  Serial.println("Turn Right");
  while(tick_L < ticks_to_move || tick_R < ticks_to_move ){
   getRPM();
   myPIDL.Compute();
   myPIDR.Compute();
   speed_L += PID_RPM_L;
   speed_R -= PID_RPM_R;
   motor.setSpeeds(speed_L, speed_R);
  }
  initEnd();
}
void turnLeft_D(float degree){ // turn left 1 = 10 degree, 9 = 90 degreee
  initMove();
  pidRPM();
  speed_L = -278;
  speed_R = 300;
  ticks_to_move = (44* degree);
  motor.setSpeeds(speed_L, speed_R);
  Serial.println("Turn Left");
  while(tick_L < ticks_to_move || tick_R < ticks_to_move ){
   getRPM();
   myPIDL.Compute();
   myPIDR.Compute();
   speed_L -= PID_RPM_L;
   speed_R += PID_RPM_R;
   motor.setSpeeds(speed_L, speed_R);
  }
  initEnd();
}

// methods after this is for initialise or get value
void pidRPM(){//clear pid offset
  PID_RPM_R=0;
  PID_RPM_L=0;
}

void getRPM(){                  
   float duration_L = pulseIn(3, HIGH); // pulseIn returns length of pulse in microseconds
   float duration_R = pulseIn(11, HIGH);
   duration_L = duration_L * 2 ;            
   duration_L = duration_L/1000000; //converts length of pulse to seconds                   
   duration_R = duration_R * 2 ;            
   duration_R = duration_R/1000000;       
                
   RPM_L = ((1/duration_L)/(562/60));  //converts length of pulse to mins
   RPM_R = ((1/duration_R)/(562/60)); // formula for rpm = (counts/min) / (counts/rev)
} 

void initStart() {                  
  motor.setSpeeds(0, 0);
  motor.setBrakes(0, 0);
}

void initEnd() {                     
  motor.setSpeeds(0, 0);
  motor.setBrakes(400, 400);
  delay(20);
}
void initMove() {                             
  tick_R = 0;                                    
  tick_L = 0;  
  //Er_ticks = 0;
}
void E1_ticks_increment()
{
  tick_L ++;
}

void E2_ticks_increment()
{
  tick_R ++;
}

void setupEncoder(){
  motor.init();
  enableInterrupt(encoder1A, E1_ticks_increment, RISING);
  enableInterrupt(encoder2A, E2_ticks_increment, RISING);
}
// methods after this is in beta for movement
void slowForward(){
  motor.setSpeeds(65, 65);
}
void slowBackward(){
  motor.setSpeeds(-65, -65);
}
void slowRight(){
  motor.setSpeeds(65, -65);
}
void slowLeft(){
  motor.setSpeeds(-65, 65);
}

void moveForward100(){
  initMove();
  pidRPM();
  ticks_to_move = 300;
  speed_L = 100; 
  speed_R = 100;
  setpoint_R = 21;
  setpoint_L = 20;
  motor.setSpeeds(speed_L, speed_R);
  while(tick_R < ticks_to_move || tick_L < ticks_to_move){
    getRPM();
    myPIDL.Compute();
    myPIDR.Compute();
    speed_L += PID_RPM_L;
    speed_R += PID_RPM_R;
    motor.setSpeeds(speed_L, speed_R);
  }
  initEnd();
}
