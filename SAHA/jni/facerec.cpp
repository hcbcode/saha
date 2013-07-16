/**
 * Face recognizer using OpenCV
 * TODO: We need to create an actual C++ instance that holds the model
 * instead of recreating it each time from the saved data which is SLOW
 *
 * @author Andreas Borglin
 */
#include <string.h>
#include <jni.h>
#include <iostream>
#include <fstream>
#include <sstream>

#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <opencv2/contrib/contrib.hpp>
#include <opencv2/highgui/highgui.hpp>

#include <android/log.h>

#define LOG_TAG "SAHA"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))

using namespace std;
using namespace cv;

extern "C" {
/*
 * Train the recognizer with a set of images
 */
JNIEXPORT jboolean JNICALL Java_com_hcb_saha_jni_NativeFaceRecognizer_nativeTrainRecognizer(
		JNIEnv* env, jobject thiz, jobjectArray usersImagePathArray,
		jstring modelFilePath) {

	// Vectors to hold image data and corresponding label (user id)
	vector<Mat> images;
	vector<int> labels;

	// usersImagePathArray is a String[userId][imagePaths] array.
	int userCount = env->GetArrayLength(usersImagePathArray);

	// Loop over all users
	for (int userId = 0; userId < userCount; userId++) {
		jobjectArray imagePathsArray = (jobjectArray) env->GetObjectArrayElement(usersImagePathArray, userId);
		int imageCount = env->GetArrayLength(imagePathsArray);

		// Loop over all image paths for that user
		for (int imageIndex = 0; imageIndex < imageCount; imageIndex++) {
			jstring imagePath = (jstring) env->GetObjectArrayElement(imagePathsArray, imageIndex);
			const char* imagePathC = env->GetStringUTFChars(imagePath, NULL);
			LOGD("User: %d, Image: %s", userId, imagePathC);

			// Add the user id and image path to the vectors (same pos)
			labels.push_back(userId);
			images.push_back(imread(imagePathC, 0));
		}
	}

	// Create the recognizer
	Ptr<FaceRecognizer> model = createEigenFaceRecognizer();
	// Train the recognizer
	model->train(images, labels);

	// Save the model to file
	const char* file = env->GetStringUTFChars(modelFilePath, NULL);
	LOGD("Writing model to file: %s", file);
	FileStorage fs(file, FileStorage::WRITE);
	model->save(fs);

	return true;

}

/*
 * Predict user id based on input image
 */
JNIEXPORT jint JNICALL Java_com_hcb_saha_jni_NativeFaceRecognizer_nativePredictUserId(
		JNIEnv* env, jobject thiz, jstring imagePath, jstring modelFilePath) {

	Ptr<FaceRecognizer> model = createEigenFaceRecognizer();

	const char* image = env->GetStringUTFChars(imagePath, NULL);

	// Load model from persisted model
	const char* modelFile = env->GetStringUTFChars(modelFilePath, NULL);
	LOGD("Reading model from file: %s", modelFile);
	FileStorage fs(modelFile, FileStorage::READ);
	model->load(fs);

	// Read input image
	Mat inputImage = imread(image, 0);

	LOGD("Predicting from image file: %s", image);
	int predictUserId = model->predict(inputImage);

	LOGD("Predicting user id: %d", predictUserId);
	return predictUserId;

}
} // extern "C"
