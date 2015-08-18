Device Files
============

Device Files is a small Java and C library for extracting vendor info,
serial number and size for whole disk drives, such as that named /dev/sda on
Linux or C: on Windows.  Build is via Maven.  The native C parts (which
do most of the work) are organized around the 'Java native loader'
framework available at
(https://github.com/uw-dims/java-native-loader/tree/master).

This repository currently contains native builds for the included C
code under [Linux] (./src/main/native/Linux/Makefile) only (both
32-bit and 64-bit builds are done).  Still to be done are Mac OS and
Windows native builds.


## Installation

```
$ cd /path/to/device-files

$ mvn test

$ mvn install

$ mvn javadoc:javadoc
```

The javadoc step should leave the api docs in target/site/apidocs/.

## API

The main class is 
[DeviceFile](./src/main/java/edu/uw/apl/commons/devicefiles/DeviceFile.java).
There are also simple unit tests under the [usual Maven location]
(./src/test/java).


## Example Code
Use of the DeviceFile class enables access to vendor, product and
serial number information about disk drives:

```
java.io.File f = new java.io.File( "/dev/sda" );
DeviceFile df = new DeviceFile( f );
String serialNumber = df.serialNumber();
```

The getID() call returns a string much like that in /dev/disk/by-id/ on Linux:
```
String id = df.getID();
```

## Java Native Loader

The Device Files Maven project also highlights the use of the
[Java Native Loader]
(https://github.com/uw-dims/java-native-loader/tree/master) framework.
Aspects of this framework include

* The local [pom] (./pom.xml) shows how we use profiles to
  canonicalize the results of Maven's <arch> property (which itself
  delegates to ${os.arch}) to match the scheme used in the
  [Native Loader]
  (https://github.com/uw-dims/java-native-loader/blob/master/main/src/main/java/edu/uw/apl/nativelibloader/OSInfo.java).
  For example, amd64 maps to x86_64.

* The pom also shows how use of a 'native' profile ensures that only a
  power user need build the native C code on any given platform.  A
  regular Maven user builds and tests just the Java classes.
  
* The pom also shows how we define PREFIX and LIBNAME values and pass
  those down to the platform-dependent build as environment
  variables. For Linux, the platform-dependent build is a [Makefile]
  (src/main/native/Linux/Makefile).

* The result of the native build is a platform (+ bitness) specific
  library file which becomes a Maven resource, so is bundled into the
  jar, from where it can be loaded by the native loader.  An example
  is at
  (src/main/resources/edu/uw/apl/commons/devicefiles/native/Linux/x86_64/libdevice-files.so)

* The values for PREFIX and LIBNAME in the pom then match those in the
  NativeLoader API call, which can be seen in the static initializer
  in the [DeviceFile]
  (src/main/java/edu/uw/apl/commons/devicefiles/DeviceFile.java).
