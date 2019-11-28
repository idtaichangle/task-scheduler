install tesseract on linux

yum install autoconf automake libtool libjpeg-devel libpng-devel libtiff-devel zlib-devel gcc-g++

cd /tmp
wget http://www.leptonica.org/source/leptonica-1.77.0.tar.gz
tar zfx leptonica-1.77.0.tar.gz
cd leptonica-1.77.0
./configure
make 
make install

ln -s /usr/local/lib/liblept.so /usr/lib/liblept.so


cd /tmp
wget  https://github.com/tesseract-ocr/tesseract/archive/4.1.0.tar.gz 
mv 4.1.0.tar.gz tesseract-4.1.0.tar.gz

tar zfx tesseract-4.1.0.tar.gz
cd tesseract-4.1.0
./autogen.sh
./configure PKG_CONFIG_PATH=/usr/local/lib/pkgconfig
make 
make install

ln -s /usr/local/lib/libtesseract.so /usr/lib/libtesseract.so



