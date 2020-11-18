#include <EnableInterrupt.h>
#include <PID_v1.h>
#include "DualVNH5019MotorShield.h"
//Motor Left
#define encoder1A 3
#define encoder1B 5

//Motor Right
#define encoder2A 11
#define encoder2B 13
DualVNH5019MotorShield motor;
char arguments;
int check;
void setup()
{
  //initialize the variables we're linked to
  Serial.begin(9600);
  setupSensor();
  setupPID();
  setupEncoder();
  initMove();
}

void loop()
{

  arguments = Serial.read(); // read from rpi character
  switch (arguments) {
    case 'F':{
//        moveFForward(1);
        fastForward(1);
        cali();
        Serial.println("PC,AR,A");
        break;
    }
    case 't':{
        check = check_speed();
        if(check == 1){
          fastForward(1);
        }
        else{
          moveFForward(1);
        }
        cali();
        Serial.println("PC,AR,A");
        break;
      }
    case 'L': {
        turnLeft();
        cali();
        Serial.println("PC,AR,A");
        break;
      }
    case 'R': {
        turnRight();
        cali();
        Serial.println("PC,AR,A");
        break;
      }
    case '1': { //fastest path
//        moveFForward(1);
        fastForward(1);
        cali_FP();
        break;
      }
    case '2': {
//        moveFForward(2);
        fastP2Forward(2);
        cali_FP();
        break;
      }
    case '3': {
//        moveFForward(3);
        fastP2Forward(3);
        cali_FP();
        break;
      }
    case '4': {
//        moveForward(4);
        fastP2Forward(4);
        cali_FP();
        break;
      }
    case '5': {
//        moveForward(5);
        fastP2Forward(5); 
        cali_FP();
        break;
      }
    case '6': {
//        moveForward(6);
        fastPForward(6);
        cali_FP();
        break;
      }
    case '7': {
//        moveForward(7);
        fastPForward(7);
        cali_FP();
        break;
      }
    case '8': {
//        moveForward(8);
        fastPForward(8);
        cali_FP();
        break;
      }
    case '9': {
//        moveForward(9);
        fastPForward(9);
        cali_FP();
        break;
      }
    case 'G': {
      turnRight();
      cali_s();
      Serial.println("PC,AR,G");
      break;
      }
    case 'H': {
      halfForward();
      cali();
      break;
      }
    case 'C': { // calibrate front and side
        cali();
        break;
      }
    case 'S': {
        getReading();
        getReading2();
        break;
      }
    case 'Z': { // start zone calibration
        cali_s();
        getReading();
        break;
      }
    case 'X':{//for rpi to chaeck connection
      Serial.println("RP,AR,X");
      break;
    }
  }
}

// methods after this is in beta for new movement

void front_d() { //move till see wall
  double dist1 = SF1();
  double dist2 = SF2();
  double dist3 = SF3();
    dist1 = SF1();
    dist3 = SF3();
    dist2 = SF2();
    while (dist1 > 10.8 && dist3 > 10.8 && dist2 > 10.8) {
      slowForward();
      dist1 = SF1();
      dist3 = SF3();
      dist2 = SF2();
    }
    while (dist1 <= 10.8 && dist3 <= 10.8 && dist2 >= 10.8) {
      slowBackward();
      dist1 = SF1();
      dist3 = SF3();
      dist2 = SF2();
    }
  initEnd();
}
void front_d1() { //calibate front robot till 6cm away from wall
  double dist1 = SF1();
  double dist2 = SF2();
  double dist3 = SF3();
  if (dist1 <= 17 && dist2 <= 16 && dist3 <= 17) {
    dist1 = SF1();
    dist3 = SF3();
    dist2 = SF2();
    while (dist1 > 10.5 && dist3 > 10.5 && dist2 > 10.5) {
      slowForward();
      dist1 = SF1();
      dist3 = SF3();
      dist2 = SF2();
    }
    while (dist1 <= 10.5 && dist3 <= 10.5 && dist2 >= 10.5) {
      slowBackward();
      dist1 = SF1();
      dist3 = SF3();
      dist2 = SF2();
    }
  }
  initEnd();
}
void front_p1() { //calibrate robot to make sure robot is aligned with the wall
  double dist1 = SF1();
  double dist3 = SF3();
  double error = abs(dist1 - dist3);
  while (error >= 0.3 && (dist1 <= 18 && dist3 <= 18)) {
    if (dist3 > dist1) {
      slowRight();
    }
    else if (dist1 > dist3) {
      slowLeft();
    }
    else {
      error = 0;
      break;
    }
    dist1 = SF1();
    dist3 = SF3();
    error = abs(dist1 - dist3);
    delay(10);
  }
  initEnd();
}
void side_p1() { //calibrate robot to make sure robot side is aligned with the wall
  initMove();
  double dist4 = SR4(); //(SR4() + 0.4);
  double dist5 = SR5();
  double error = abs(dist4 - dist5);
  while ((error >= 0.3) && (dist4 < 13 && dist5 < 13)) { //11
    if (dist4 > dist5) {
      slowRight();
    }
    else if (dist5 > dist4) {
      slowLeft();
    }
    else {
      error = 0;
    }
    dist4 = SR4();//(SR4() + 0.4);
    dist5 = SR5();
    error = abs(dist4 - dist5);
    delay(10);
  }
  initEnd();
}
void cali_s() { // calibration at start zone
  initMove();
  front_d();
  front_p1();
  side_p1();
  turnLeft();
  front_d();
  front_p1();
  side_p1();
  cali();
  initEnd();
}
void cali() { //calibate for exploration
  side_p1();
  front_d1();
  front_p1();
  front_d1();
  front_p1();
  side_p1();
}
void cali_FP() { //calibate fastest path
  side_p1();
  front_p1();
  front_p1();
  side_p1();
}
int check_speed(){
  double R1 = SR4_IR();
  double R2 = SR5_IR();
  if(R1 == 1 && R2 == 1){
    return 1;
  }
  return 2;
}
