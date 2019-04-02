


// The purpose of the project is to make a people counter that detects the number of people who enter the room using lasers and LDRs and control it with an android app.
//
// Connect the one leg of an LDR to the GND with 1K ohm resistor.
//
// Make sure the lasers are toward to the LDRs directly.
//
// If you want that the results of the people counter is accurate, please set the optimum level of LDRs correctly.
//
//
// Connections:
//
// Arduino Uno
//                                Laser_Module_1
// Pin 2  ------------------------
//                                Laser_Module_2
// Pin 3  ------------------------
//                                Laser_Module_3
// Pin 4  ------------------------
//                                Buzzer
// Pin 5  ------------------------
//                                HC-06 Bluetooth Module
// Pin 6  ------------------------TX
// Pin 7  ------------------------RX
//                                Control_Led_1
// Pin 8  ------------------------
//                                Control_Led_2
// Pin 12 ------------------------
//                                Control_Led_3
// Pin 13 ------------------------
//                                RGB_Module
// Pin 9  ------------------------R
// Pin 10 ------------------------G
// Pin 11 ------------------------B
//                                LDR_1
//     AO ------------------------
//                                LDR_2
//     A1 ------------------------
//                                LDR_3
//     A2 ------------------------


                               
#include <SoftwareSerial.h>

int LaserPin_1 = 2; // Define the laser sensors' pins.
int LaserPin_2 = 3;


int BuzzerPin = 5; // Buzzer pin.

int Control_RX = 6; // RX and TX pin for the SoftWareSerial library.
int Control_TX = 7;

int RedPin = 9; // PWM pins for RGB LED sensor.
int GreenPin = 10;

int Control_Led_1 = 8; // Set the each of led as a mark for the status of the each of laser modules.
int Control_Led_2 = 12;

int LDR_1 = A0; // Analog pins for LDRs.
int LDR_2 = A1;

int LDR_1_Read ; // Define the value of LDRs as global variables.
int LDR_2_Read ;

int LDR1 = 1,LDR2 = 1;

int Counter = 0; // Set the default value of the counter as zero.

volatile boolean Alarm_is_Activated = false; // Choose whether the alarm is on or not.
volatile boolean Alarm_Initial = false;

volatile boolean Counter_Detect = false; // It is a variable to give delay time to Arduino.

SoftwareSerial Control(Control_RX, Control_TX); // Define the Rx and the Tx pins to communicate with Bluetooth Module.

String Name = "Control"; // Name your module and set the password for it.
int Password = 1111;
String Uart = "9600,0,0";




void setup() {
  
  Serial.begin(9600);
  Control.begin(9600); // Begin HC-05 Bluetooth module to communicate.
  
  // Change_BluetoothModule_Defaults(); // You can activate it if you want to change the defaults of the Bluetooth module.
  
  pinMode(LaserPin_1,OUTPUT);
  pinMode(LaserPin_2,OUTPUT);
 

  pinMode(Control_Led_1,OUTPUT);
  pinMode(Control_Led_2,OUTPUT);

digitalWrite(LaserPin_1,HIGH);
      digitalWrite(Control_Led_1,HIGH);
digitalWrite(LaserPin_2,HIGH);
      digitalWrite(Control_Led_2,HIGH);

  
}

void loop() {

    get_Data_From_LDR(); // Get the data from LDR sensors.

      char c ;
    if(Control.available()){ // If HC-06 Bluetooth module is available, Commands() has proceeded.

    c = Control.read();
     Serial.write(c);
    Commands(c);
    //String c =  Control.read();
    //Serial.println(c);
    }

  
    
    Set_Alarm(); // Initial the alarm function.

    Set_Counter(); // Begin the people counter.
 
}
void Commands(char i){ // Choose which events happen when the specific character is sent from the app to Arduino.
  
    switch(i){
      case '1' :
      Control.print(Counter);
      Serial.println(Counter);
      break;
      case '2' :
      Alarm_is_Activated = true;
      break;
      case '3' :
      Alarm_is_Activated = false;
      break;
      case '4' :
      digitalWrite(LaserPin_1,HIGH);
      digitalWrite(Control_Led_1,HIGH);
      LDR1 = 1;
      break;
      case '5' :
      digitalWrite(LaserPin_1,LOW);
      digitalWrite(Control_Led_1,LOW);
      LDR1 = 0;
      break;
      case '6' :
      digitalWrite(LaserPin_2,HIGH);
      digitalWrite(Control_Led_2,HIGH);
      LDR2 = 1;
      break;
      case '7' :
      digitalWrite(LaserPin_2,LOW);
      digitalWrite(Control_Led_2,LOW);
      LDR2 = 0;
      break;
      case 'r' :
      Counter = 0;
      break;          
    }
  }

void get_Data_From_LDR(){ // Get the data of LDR sensors.
  LDR_1_Read = analogRead(LDR_1);
  LDR_2_Read = analogRead(LDR_2);

if (LDR1 == 1){
  Serial.println("X");
  Serial.println(LDR_1_Read);
}

  
}

void Set_Counter(){ // Set a people counter.


Counter_Detect = false;


if (LDR2 == 1)
    if ( LDR_2_Read < 37 )
        Counter_Detect = true;


        if(LDR1 == 1)
   if(LDR_1_Read < 47)
Counter_Detect = true;
        
 

  
     if(Counter_Detect == true){
       Counter = Counter + 1;
       Control.print(Counter);
       Control.print("!");
       
       delay(500); // Give some time to get the number of people who enter the room accurately.
      }
      if(Counter_Detect == false){
        Counter = Counter;
      }
}

void Set_Alarm(){ // Set an adjustable alarm system.

  if(Alarm_is_Activated == true){

Alarm_Initial = false;

    if (LDR2 == 1)
    if ( LDR_2_Read < 37 )
    Alarm_Initial = true;

    if(LDR1 == 1)
   if(LDR_1_Read < 47)
Alarm_Initial = true;
    
   
    
    
    
 
   if(Alarm_Initial == true){
    tone(BuzzerPin,500);
     Color_Change(255, 0); 
     delay(50);
     Color_Change(0, 255); 
     delay(50);
     Color_Change(255, 255); 
     delay(50);
     Color_Change(80, 0);
     delay(50); 
     Color_Change(0, 255); 
     delay(50);
     Color_Change(255, 255);
     delay(50);
   }
  }
  if(Alarm_is_Activated == false || Alarm_Initial == false ){
    noTone(BuzzerPin);
    Color_Change(0, 0);
  }
}

void Color_Change(int red, int green){ // Change the RGB Module variables easily.
 red = 255 - red;
 green = 255 - green;
 //blue = 255 - blue;
 analogWrite(RedPin, red);
analogWrite(GreenPin, green);
}

void Change_BluetoothModule_Defaults(){ // Change the default values of the Bluetooth module whatever values you choose.
 
  Control.print("AT+NAME"); // Change the name.
  Control.println(Name); 
  Serial.print("Name is changed: ");
  Serial.println(Name);
  delay(2000);
  Control.print("AT+PSWD"); // Change the password.
  Control.println(Password);
  Serial.print("Password is changed: ");
  Serial.println(Password);
  delay(2000);
  Control.print("AT+UART"); // Change the baud rate. If the Bluetooth module is a HC-05, the default value of the baud rate is 38400.
  Control.println(Uart);
  Serial.print("Baud rate is set: ");
  Serial.println(Uart);
  delay(2000);
  Serial.println("Task is completed."); // You can see whether the task is completed correctly or not using the terminal.
}
