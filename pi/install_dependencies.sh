echo "Installing Fingerprint Software"
wget -O - http://apt.pm-codeworks.de/pm-codeworks.de.gpg | apt-key add -
wget http://apt.pm-codeworks.de/pm-codeworks.list -P /etc/apt/sources.list.d/
sudo apt-get update
sudo apt-get install python-fingerprint --yes
echo "Installing LCD I2C software"
sudo apt-get install i2c-tools
sudo apt-get install python-smbus
echo "Installing gRPC & protobuf software"
python -m pip install grpcio
python -m pip install grpcio-tools
echo "Job Done."
