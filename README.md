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

To test the face stuff,

1. Press the add user icon in the action bar
2. Enter your first name (lowercase, no spaces)
3. Press Register
4. Hold the front-facing camera straight in front of your face (~50 cm away)
5. The app will take snapshots, persist and add it to the training model
6. The face recognizer will re-train now (takes a few secs, but all requests are synchronous so no thread worries)
7. Press Recognize in the options menu and do the same as in step #4
8. It should pop a Toast with your name

To list users

1. Click the List users menu option
2. To save more face images for a user, press the button for that user

This has been tested with 3 people in the system and with ~75% accuracy. Need more input data for higher accuracy.
