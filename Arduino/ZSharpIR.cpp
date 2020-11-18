/*
	ZSharpIR

	Arduino library for retrieving distance (in mm) from the analog GP2Y0A21Y and GP2Y0A02YK,...

    Original comment from Dr. Marcal Casas-Cartagena :
	inspired from :
	- https://github.com/qub1750ul/Arduino_SharpIR.git
    - https://github.com/jeroendoggen/Arduino-GP2Y0A21YK-library.git
    - https://github.com/guillaume-rico/SharpIR.git
    - https://github.com/nikv96/MDP-Arduino.git
    - https://github.com/jeroendoggen/Arduino-GP2Y0A21YK-library.git

  

*/


#include "Arduino.h"
#include "Math.h"
#include "ZSharpIR.h"

// Initialisation function
//  + irPin : is obviously the pin where the IR sensor is attached
//  + sensorModel is a int to differentiate the two sensor models this library currently supports:
//     1080 is the int for the GP2Y0A21Y and 
//     20150 is the int for GP2Y0A02YK and 
//     100500 is the long for GP2Y0A710K0F
//    The numbers reflect the distance range they are designed for (in cm)
ZSharpIR::ZSharpIR(int irPin, const uint32_t  sensorModel) {
  
    _irPin=irPin;
    _model=sensorModel;
    
    // Define pin as Input
    pinMode (_irPin, INPUT);
    _Adcres=10;
	_refVoltage=5000;
}

// Sort an array
void ZSharpIR::sort(int a[], int size) {
    for(int i=0; i<(size-1); i++) {
        bool flag = true;
        for(int o=0; o<(size-(i+1)); o++) {
            if(a[o] > a[o+1]) {
                int t = a[o];
                a[o] = a[o+1];
                a[o+1] = t;
                flag = false;
            }
        }
        if (flag) break;
    }
}
double ZSharpIR::voltage(){
    int raw = analogRead(_irPin);
    int voltFromRaw= map(raw ,0,1023,0,5000);
    float v = voltFromRaw/1000.0;
    return v;
}
// Read distance and compute it
double ZSharpIR::distance() {

    int ir_val[NB_SAMPLE];
    double distanceMM;
    double current;

    for (int i=0; i<NB_SAMPLE; i++){
        // Read analog value
        ir_val[i] = analogRead(_irPin);
    }
    
    // Sort it 
    sort(ir_val,NB_SAMPLE);

    if (_model==999){
        distanceMM=(int)(138773.464825 * pow(ir_val[0],-1.0233470));
   }

    else if (_model==1080)//GP2Y0A21YK0F
		{
        
        // Different expressions required as the Photon has 12 bit ADCs vs 10 bit for Arduinos
          distanceMM =( 277.28 * pow(map(ir_val[NB_SAMPLE / 2], 0, (1<<_Adcres)-1, 0, _refVoltage)/1000.0, -1.2045));


    }   
    else if (_model==20150)//GP2Y0A02YK0F
	{

        // Previous formula used by  Dr. Marcal Casas-Cartagena
        // puntualDistance=61.573*pow(voltFromRaw/1000, -1.1068);
        
        // Different expressions required as the Photon has 12 bit ADCs vs 10 bit for Arduinos
          distanceMM =( 603.74 * pow(map(ir_val[NB_SAMPLE / 2], 0, (1<<_Adcres)-1, 0, _refVoltage)/1000.0, -1.16));


    } 
    else if (_model==1)//sensor 1
    {

        distanceMM = (5135.5* pow(ir_val[NB_SAMPLE/2],-0.987)); //8162 -1.083
    }
    else if (_model==2)//sensor 2
    {
        distanceMM = (2548.7 * pow(ir_val[NB_SAMPLE/2],-0.857));//4672.5 -0.974

    }
    else if (_model==3)//sensor 3
    {
        distanceMM = (5417.5 * pow(ir_val[NB_SAMPLE/2],-0.999)); //4413.4  -0.979 

    }
    else if (_model==4)//sensor 4
    {
        distanceMM = (11573* pow(ir_val[NB_SAMPLE/2],-1.153));
    }
        else if (_model==5)//sensor 5
    {
        distanceMM = (9049 * pow(ir_val[NB_SAMPLE/2],-1.105));
    }
    else if (_model==6){
        current = -0.1052 * ir_val[NB_SAMPLE/2] + 71.646;
        if (current <=35)
        {
            distanceMM = current;
        }
        else{
            current = (23995 * pow(ir_val[NB_SAMPLE/2],-1.119));
            if(current <=60){
                distanceMM = current;
            }
            else{
                distanceMM = (22414 * pow(ir_val[NB_SAMPLE/2],-1.106));
            }
        }
    }
    else if (_model==7){
        distanceMM = map(ir_val[NB_SAMPLE / 2], 0, (1<<_Adcres)-1, 0, _refVoltage);
    }
    return distanceMM;
}




/// <summary>
/// setARefVoltage:set the ADC reference voltage: (default value: 5000mV, set to 3300mV, typically 3.3 on Arduino boards)
/// </summary>
void ZSharpIR::setARefVoltage(int refV)
{

	_refVoltage=refV;
}
/// <summary>
/// SetAnalogReadResolution:set the ADC resolution : (default value: 10, set to 12, typically 10 on Arduino boards)
/// </summary>
void ZSharpIR::SetAnalogReadResolution(int res)
{

	_Adcres=res;
	//analogReadResolution( res);
}