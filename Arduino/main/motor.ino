double speed_L = 203;
double speed_R = 226;
double tick_L = 0;
double tick_R = 0;
double Er_ticks = 0;
int ticks_to_move = 0;
double RPM_L = 0;                                // To Store RPM of Left Motor 
double RPM_R = 0;                                 // To Store RPM of Right Motor 
double PID_RPM_R = 0;
double PID_RPM_L = 0;
double setpoint_L = 59.9;
double setpoint_R = 60.9;
const double Kp_L = 0.12, Ki_L = 0.0, Kd_L = 0.0;
const double Kp_R = 0.05, Ki_R = 0.0, Kd_R = 0.0;
PID myPIDL(&RPM_L, &PID_RPM_L, &setpoint_L, Kp_L, Ki_L, Kd_L, DIRECT);
PID myPIDR(&RPM_R, &PID_RPM_R, &setpoint_R, Kp_R, Ki_R, Kd_R, DIRECT);


void fastForward(int grid){ // attempt for faster speed
  initMove();
  pidRPM();
  ticks_to_move = 277 * grid;
  speed_L = 276;
  speed_R = 301;
  setpoint_L = 97.3;
  setpoint_R = 100;
  motor.setSpeeds(speed_L, speed_R);
  while(tick_R < ticks_to_move || tick_L < ticks_to_move){
    getRPM();
    myPIDL.Compute();
    myPIDR.Compute();
    speed_L += PID_RPM_L;
    speed_R += PID_RPM_R;
    motor.setSpeeds(speed_L, speed_R);
  }
  setpoint_L = 59.9;
  setpoint_R = 60.9;
  initEnd();
}
void fastPForward(int grid){ // attempt for faster speed for fastest path
  initMove();
  pidRPM();
  ticks_to_move = 298 * grid;
  speed_L = 276;
  speed_R = 301;
  setpoint_L = 97.3;
  setpoint_R = 100;
  motor.setSpeeds(speed_L, speed_R);
  while(tick_R < ticks_to_move || tick_L < ticks_to_move){
    getRPM();
    myPIDL.Compute();
    myPIDR.Compute();
    speed_L += PID_RPM_L;
    speed_R += PID_RPM_R;
    motor.setSpeeds(speed_L, speed_R);
  }
  setpoint_L = 59.9;
  setpoint_R = 60.9;
  initEnd();
}
void fastP2Forward(int grid){ // attempt for faster speed for fastest path
  initMove();
  pidRPM();
  ticks_to_move = 294 * grid;
  speed_L = 276;
  speed_R = 301;
  setpoint_L = 97.3;
  setpoint_R = 100;
  motor.setSpeeds(speed_L, speed_R);
  while(tick_R < ticks_to_move || tick_L < ticks_to_move){
    getRPM();
    myPIDL.Compute();
    myPIDR.Compute();
    speed_L += PID_RPM_L;
    speed_R += PID_RPM_R;
    motor.setSpeeds(speed_L, speed_R);
  }
  setpoint_L = 59.9;
  setpoint_R = 60.9;
  initEnd();
}
void moveForward(int grid){ //move forward 1 grid fastest path
  initMove();
  pidRPM();
  ticks_to_move = 300 * grid;
  speed_L = 203;
  speed_R = 226;
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

void moveFForward(int grid){ //move forward 1 grid
  initMove();
  pidRPM();
  ticks_to_move = 286 * grid;
  speed_L = 206;
  speed_R = 222;
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
void halfForward(){ //move forward in start zone prepare for fastest path
  initMove();
  pidRPM();
  ticks_to_move = 30;
  speed_L = 206;
  speed_R = 222;
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
void turnRight(){ // turn right 90 degree
  initMove();
  pidRPM();
  speed_L = 206;
  speed_R = -222;
  ticks_to_move = 375;
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
  speed_L = -206;
  speed_R = 222;
  ticks_to_move = 373;
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
  speed_L = 206;
  speed_R = -222;
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
  speed_L = -206;
  speed_R = 222;
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

void moveBack(int grid){ // move back 1 grid, used for calibrating motor
  initMove();
  pidRPM();
  ticks_to_move = 310 * grid;
  speed_L = -335;
  speed_R = -350;
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
