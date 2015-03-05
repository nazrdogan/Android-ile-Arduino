#include <SoftwareSerial.h>
 
SoftwareSerial mySerial(10, 7); // RX, TX

int led = 13;   // LED'in bağlı oldugu pin
char chByte = 0;  // gelen seri bayt
String strInput = "";    // Gelen paket için buffer
String strCompare = "switch";


void setup() {
 // declare pin 9 to be an output:
 pinMode(led, OUTPUT);
 // initialize serial:
 mySerial.begin(9600);
}

void loop() {

 while (mySerial.available() > 0)
 {
  // Gelen baytı okuma
  chByte = mySerial.read();
  if (chByte == '\r')
  {
   //gelen mesajla karşılaştırma
   if(strInput.equals(strCompare))
   {
    //LED'i yak
    digitalWrite(led, HIGH);
    mySerial.println("LED AÇIK");
    delay(5000);
    digitalWrite(led, LOW);
    mySerial.println("LED KAPALI");
   }
   //strInput değerini resetleme
   strInput = "";
  }
  else
  {
  strInput += chByte;
  }
 }
}

