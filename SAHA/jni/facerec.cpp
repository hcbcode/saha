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
#include "facerecwrapper.h"

#define LOG_TAG "facerec.cpp"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))

using namespace std;
using namespace cv;

extern "C" {

JNIEXPORT jlong JNICALL Java_com_hcb_saha_jni_NativeFaceRecognizer_loadPersistedModel(
		JNIEnv* env, jobject thiz, jstring modelFilePath) {

	FaceRecWrapper* wrapper = new FaceRecWrapper();
	Ptr<FaceRecognizer> model = createEigenFaceRecognizer();
	// Load model from persisted model
	const char* modelFile = env->GetStringUTFChars(modelFilePath, NULL);
	LOGD("Reading model from file: %s", modelFile);
	FileStorage fs(modelFile, FileStorage::READ);
	if (fs.isOpened()) {
		model->load(fs);
		fs.release();
	}

	wrapper->setModel(model);
	return (jlong) wrapper;

}

JNIEXPORT void JNICALL Java_com_hcb_saha_jni_NativeFaceRecognizer_deleteWrapper(
		JNIEnv* env, jobject thiz, jlong wrapperRef) {
	FaceRecWrapper* wrapper = (FaceRecWrapper*) wrapperRef;
	delete wrapper;
}

/*
 * Train the recognizer with a set of images
 */
JNIEXPORT jboolean JNICALL Java_com_hcb_saha_jni_NativeFaceRecognizer_nativeTrainRecognizer(
		JNIEnv* env, jobject thiz, jintArray userIds,
		jobjectArray usersImagePathArray, jstring modelFilePath,
		jlong wrapperRef) {

	// Vectors to hold image data and corresponding label (user id)
	vector<Mat> images;
	vector<int> labels;

	// usersImagePathArray is a String[userId][imagePaths] array.
	int userCount = env->GetArrayLength(userIds);
	jint *body = env->GetIntArrayElements(userIds, 0);

	// Loop over all users
	for (int userIndex = 0; userIndex < userCount; userIndex++) {
		int userId = body[userIndex];
		jobjectArray imagePathsArray =
				(jobjectArray) env->GetObjectArrayElement(usersImagePathArray,
						userIndex);
		int imageCount = env->GetArrayLength(imagePathsArray);

		CascadeClassifier haar_cascade;
		// FIXME Move path to Java and pass it down
		if (haar_cascade.load(
				"/sdcard/SAHA/users/haarcascade_frontalface_default.xml")) {
			LOGD("Haar successfully loaded");
		}

		// Loop over all image paths for that user
		for (int imageIndex = 0; imageIndex < imageCount; imageIndex++) {
			jstring imagePath = (jstring) env->GetObjectArrayElement(
					imagePathsArray, imageIndex);
			const char* imagePathC = env->GetStringUTFChars(imagePath, NULL);
			LOGD("User: %d, Image: %s", userId, imagePathC);

			Mat original = imread(imagePathC, 1);
			Mat gray;
			cvtColor(original, gray, CV_BGR2GRAY);
			vector<Rect_<int> > faces;
			// FIXME We need to figure out the size here based on camera res
			haar_cascade.detectMultiScale(gray, faces, 1.1, 3, 0,
					Size(40, 80));
			LOGD("FACES : %d ", faces.size());
			if (faces.size() > 0) {
				Rect face_i = faces[0];
				Mat faceMat = gray(face_i);
				resize(faceMat, faceMat, Size(200, 200));
				//imwrite("/sdcard/face/pig.jpg", faceMat);
				labels.push_back(userId);
				images.push_back(faceMat);
			}

		}
	}

	FaceRecWrapper* wrapper = (FaceRecWrapper*) wrapperRef;
	// Create the recognizer
	Ptr<FaceRecognizer> model = wrapper->getModel();
	// Train the recognizer
	model->train(images, labels);

	// Save the model to file
	const char* file = env->GetStringUTFChars(modelFilePath, NULL);
	LOGD("Writing model to file: %s", file);
	FileStorage fs(file, FileStorage::WRITE);
	if (fs.isOpened()) {
		model->save(fs);
		fs.release();
	}

	return true;

}

/*
 * Predict user id based on input image
 */
JNIEXPORT jint JNICALL Java_com_hcb_saha_jni_NativeFaceRecognizer_nativePredictUserId(
		JNIEnv* env, jobject thiz, jstring imagePath, jstring modelFilePath,
		jlong wrapperRef) {

	FaceRecWrapper* wrapper = (FaceRecWrapper*) wrapperRef;
	// Create the recognizer
	Ptr<FaceRecognizer> model = wrapper->getModel();

	const char* image = env->GetStringUTFChars(imagePath, NULL);

	CascadeClassifier haar_cascade;
	// FIXME Move path to Java and pass it down
	if (haar_cascade.load(
			"/sdcard/SAHA/users/haarcascade_frontalface_default.xml")) {
		LOGD("Haar successfully loaded");
	}

	LOGD("Predicting from image file: %s", image);
	Mat original = imread(image, 1);
	Mat gray; //= original.clone();
	cvtColor(original, gray, CV_BGR2GRAY);
	vector<Rect_<int> > faces;
	haar_cascade.detectMultiScale(gray, faces, 1.1, 3, 0, Size(20, 60));
	LOGD("FACES : %d ", faces.size());
	int predictUserId = -1;
	if (faces.size() > 0) {
		Rect face_i = faces[0];
		Mat faceMat = gray(face_i);
		resize(faceMat, faceMat, Size(200, 200));
		//imwrite("/sdcard/face/pig.jpg", original_face);
		predictUserId = model->predict(faceMat);

		LOGD("Predicting user id: %d", predictUserId);
	}

	return predictUserId;

}

} // extern "C"
