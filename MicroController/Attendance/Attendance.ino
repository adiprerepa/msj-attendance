
#include <Adafruit_Fingerprint.h>

// 2 -> IN
// 3 -> OUT
// weird thing: The fingerprint sensor
// does not work unless it is placed on a surface.
SoftwareSerial mySerial(2, 3);

Adafruit_Fingerprint finger = Adafruit_Fingerprint(&mySerial);

void setup()  
{
  Serial.begin(9600);
  while (!Serial);  // For Yun/Leo/Micro/Zero/...
  delay(100);
  Serial.println("\n\nAdafruit finger detect test");
  
  // for sending and recieving from sensor
  finger.begin(57600);
  delay(5);
  if (finger.verifyPassword()) {
    Serial.println("Found fingerprint sensor!");
  } else {
    // place on surface
    Serial.println("Did not find fingerprint sensor :(");
    while (1) { delay(1); }
  }

  finger.getTemplateCount();
  Serial.print("Sensor contains ");
  Serial.print(finger.templateCount);
  Serial.print(" templates. Waiting for a finger...\n");
}

void loop()                   
{
  getFingerprintID();
  delay(50);            // slap some delay on this baby
}


// returns -1 if failed, otherwise returns ID #
int getFingerprintID() {
  uint8_t p = finger.getImage();
  if (p != FINGERPRINT_OK)  return -1;

  p = finger.image2Tz();
  if (p != FINGERPRINT_OK)  return -1;

  p = finger.fingerFastSearch();
  if (p != FINGERPRINT_OK)  return -1;
  
  // found a match!
  Serial.print("ID: ");
  Serial.print(finger.fingerID);
  Serial.print(" | Confidence: ");
  Serial.println(finger.confidence);
  return finger.fingerID; 
}
