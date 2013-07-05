saha
====

Smart Android Household Array

about
=====

I just wanted to add some text

setup
====
Android Studio does not yet autogenerate a local.properties with your SDK location.
This means you need to take a couple of steps before you open the project.

1. Install the Android SDK if you haven't already
2. Make sure you have Android 17 and the latest build tools installed
3. Go to the saha directory
4. Do a "android list targets" and note the id of Android 17 (4.2.2)
5. Run "android update project -p SAHA/src/main -t <id>
6. Check that you have a local.properties in the saha folder (if not, may have to copy it from SAHA/src/main)
7. Now you can import the project into Android studio!
