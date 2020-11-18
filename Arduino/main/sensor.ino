#include <ZSharpIR.h>
#include <RunningMedian.h>
#define NumOfRuns 20
// define 5 short range sensors
#define SENSOR_1 A0
#define SENSOR_2 A1
#define SENSOR_3 A2
#define SENSOR_4 A3
#define SENSOR_5 A4
// define 1 long range sensors
#define SENSOR_6 A5
ZSharpIR sensor1(SENSOR_1, 1);
ZSharpIR sensor2(SENSOR_2, 2);
ZSharpIR sensor3(SENSOR_3, 3);
ZSharpIR sensor4(SENSOR_4, 4);
ZSharpIR sensor5(SENSOR_5, 5);
//long range sensors
ZSharpIR sensor6(SENSOR_6, 6);//20150
//RunningMedian class to calculate the median values from the sensor
RunningMedian sensor1Values(NumOfRuns);
RunningMedian sensor2Values(NumOfRuns);
RunningMedian sensor3Values(NumOfRuns);
RunningMedian sensor4Values(NumOfRuns);
RunningMedian sensor5Values(NumOfRuns);
RunningMedian sensor6Values(NumOfRuns);

void setupSensor() {
  //initialize the variables we're linked to
  Serial.begin(9600);
  pinMode(SENSOR_1, INPUT);
  pinMode(SENSOR_2, INPUT);
  pinMode(SENSOR_3, INPUT);
  pinMode(SENSOR_4, INPUT);
  pinMode(SENSOR_5, INPUT);
  pinMode(SENSOR_6, INPUT);
}
void getReading(){ // reading for algo
  Serial.print("PC,AR,");
  Serial.print(SF1_IR());
  Serial.print(SF2_IR());
  Serial.print(SF3_IR());
  Serial.print(SR4_IR());
  Serial.print(SR5_IR());
  Serial.println(SL_IR());
  Serial.flush(); 
}
void getReading2(){ // reading for user 
  Serial.println(" ");
  Serial.print(sensor1.distance());
  Serial.print(": ");
  Serial.print(sensor2.distance());
  Serial.print(": ");
  Serial.print(sensor3.distance());
  Serial.print(": ");
  Serial.print(sensor4.distance());
  Serial.print(": ");
  Serial.print(sensor5.distance());
  Serial.print(": ");
  Serial.println(sensor6.distance());  
}
double SF1(){ // sensor 1 front left
  for(int i=0; i<NumOfRuns; i++){
    sensor1Values.add(sensor1.distance());
  }
  double dist = sensor1Values.getMedian();
  return (dist);
}
double SF2(){ // sensor 2 front mid
  for(int i=0; i<NumOfRuns; i++){
    sensor2Values.add(sensor2.distance());
  }
  double dist = sensor2Values.getMedian();
  return dist;
}
double SF3(){ // sensor 3 front right
  for(int i=0; i<NumOfRuns; i++){
    sensor3Values.add(sensor3.distance());
  }
  double dist = sensor3Values.getMedian();
  return dist;
}

double SR4(){ // sensor 4 left front
  for(int i=0; i<NumOfRuns; i++){
    sensor4Values.add(sensor4.distance());
  }
  double dist = sensor4Values.getMedian();
  return (dist+0.2);
}

double SR5(){ // sensor 5 left back
  for(int i=0; i<NumOfRuns; i++){
    sensor5Values.add(sensor5.distance());
  }
  double dist = sensor5Values.getMedian();
  return dist;
}
double SL6(){ // long sensor 6 right
  for(int i=0; i<NumOfRuns; i++){
    sensor6Values.add(sensor6.distance());
  }
  double dist = sensor6Values.getMedian();
  return dist;
}
int SF1_IR(){
  double dist = (SF1());
  if( dist <= 15){
    return 1;
  }
  else if (dist > 15 && dist < 25){
    return 2;
  }
  else{
    return 0;
  }
}
int SF2_IR(){
  double dist = (SF2());
  if(dist <= 14.7){
    return 1;
  }
  else if (dist > 14.7 && dist < 26){
    return 2;
  }
  else{
    return 0;
  }
}
int SF3_IR(){
  double dist = (SF3());
  if(dist <= 15){
    return 1;
  }
  else if (dist > 15 && dist < 25){
    return 2;
  }
  else{
    return 0;
  }
}
int SR4_IR(){
  double dist = (SR4());
  if(dist <= 14.2){
    return 1;
  }
  else if (dist > 14.2 && dist < 24.7){
    return 2;
  }
  else{
    return 0;
  }
}
int SR5_IR(){
  double dist = (SR5());
  if(dist <= 14.4){
    return 1;
  }
  else if (dist > 14.4 && dist < 24.7){
    return 2;
  }
  else{
    return 0;
  }
}
int SL_IR(){
  double dist = (SL6());
  if(dist <= 18){
    return 1;
  }
  else if (dist > 18 && dist <= 28){
    return 2;
  }
  else if (dist > 28 && dist <= 38){
    return 3;
  }
  else if(dist > 38 && dist <= 48){
    return 4;
  }
  else if(dist > 48 && dist <= 58){
    return 5;
  }
  else{
    return 0;
  }
}

void getMedianReading(){
  for (int i=0; i<NumOfRuns; i++){
    sensor1Values.add(sensor1.distance());
    sensor2Values.add(sensor2.distance());
    sensor3Values.add(sensor3.distance());
    sensor4Values.add(sensor4.distance());
    sensor5Values.add(sensor5.distance());
    sensor6Values.add(sensor6.distance());
  }

  Serial.print("Front Right sensor 1 median reading: ");
  Serial.println(sensor1Values.getMedian()); 
  Serial.print("Front Mid sensor 2 median reading: "); 
  Serial.println(sensor2Values.getMedian());  
  Serial.print("Front Left sensor 3 median reading: ");
  Serial.println(sensor3Values.getMedian()); 
  Serial.print("Left Front sensor 4 median reading: ");
  Serial.println(sensor4Values.getMedian()); 
  Serial.print("Left Back sensor 5 median reading: ");
  Serial.println(sensor5Values.getMedian()); 
  Serial.print("Right Long sensor 6 median reading: ");
  Serial.println(sensor6Values.getMedian()); 
}
