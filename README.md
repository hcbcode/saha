saha
====

Smart Android Household Array

setup
====
You'll have to use Eclipse for now due to NDK dependencies.

1. Run "gradle eclipse" to generate eclipse files to import to Eclipse
2. You still need a local.properties that points to your SDK path
3. You must have the Android NDK installed and on your path
4. Do a "ndk-build" from the saha/SAHA folder
5. Refresh project in Eclipse
6. You can now run it

Note that there are currently dependencies on hardcoded images on my device :)
Also note that I have stripped all dependencies besides armeabi-v7a from OpenCV
Cropping still doesn't work...
