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

// Java parameter names
#define CASCADE_MIN_NEIGHBOURS "CASCADE_MIN_NEIGHBOURS"
#define CASCADE_SCALE_FACTOR "CASCADE_SCALE_FACTOR"
#define CASCADE_MIN_FACE_WIDTH "CASCADE_MIN_FACE_WIDTH"
#define CASCADE_MIN_FACE_HEIGHT "CASCADE_MIN_FACE_HEIGHT"
#define CASCADE_OUTPUT_FACE_WIDTH "CASCADE_OUTPUT_FACE_WIDTH"
#define CASCADE_OUTPUT_FACE_HEIGHT "CASCADE_OUTPUT_FACE_HEIGHT"

using namespace std;
using namespace cv;

extern "C" {

/*
 * Static method that detects a face in an image, crops and resizes it and
 * sets it to the output Mat. If dumpToFacePath is specified, it will
 * write the output image to that path.
 */
static bool detectAndCropFace(CascadeClassifier* haar_cascade,
		const char* imagePath, FaceRecParams* params, Mat* output,
		const char* dumpFaceToPath) {
	// Read image as RGB
	Mat original = imread(imagePath, 1);
	Mat gray;
	// Convert to grayscale
	cvtColor(original, gray, CV_BGR2GRAY);
	vector<Rect_<int> > faces;
	haar_cascade->detectMultiScale(gray, faces, params->scaleFactor,
			params->minNeighbour, 0, params->minSize);
	LOGD("Faces detected in image: %d ", faces.size());

	// We only care about the first case
	if (faces.size() > 0) {
		Rect face_i = faces[0];
		// Crop the face to faceMat
		Mat faceMat = gray(face_i);
		// Resize image to outputSize
		resize(faceMat, faceMat, params->outputSize);
		// If we have a dump path, dump the cropped face to this path
		if (dumpFaceToPath != NULL) {
			imwrite(dumpFaceToPath, faceMat);
		}
		// Copy the face to the output mat
		faceMat.copyTo(*output);
		return true;
	}

	return false;
}

/*
 * Read OpenCV face parameters from Java config
 */
static void readFaceParameters(FaceRecParams* faceRecParams, JNIEnv* env,
		jclass params) {

	// Set min neighbours (int)
	jfieldID fieldId = env->GetStaticFieldID(params, CASCADE_MIN_NEIGHBOURS,
			"I");
	if (fieldId != NULL) {
		jint minN = env->GetStaticIntField(params, fieldId);
		faceRecParams->minNeighbour = (int) minN;
	}

	// Set scale factor (float)
	fieldId = env->GetStaticFieldID(params, CASCADE_SCALE_FACTOR, "F");
	if (fieldId != NULL) {
		jfloat scaleFactor = env->GetStaticFloatField(params, fieldId);
		faceRecParams->scaleFactor = (float) scaleFactor;
	}

	// Set min size
	int width, height;
	fieldId = env->GetStaticFieldID(params, CASCADE_MIN_FACE_WIDTH, "I");
	if (fieldId != NULL) {
		width = (int) env->GetStaticIntField(params, fieldId);
	}
	fieldId = env->GetStaticFieldID(params, CASCADE_MIN_FACE_HEIGHT, "I");
	if (fieldId != NULL) {
		height = (int) env->GetStaticIntField(params, fieldId);
	}
	faceRecParams->minSize = Size(width, height);

	// Set output size
	fieldId = env->GetStaticFieldID(params, CASCADE_OUTPUT_FACE_WIDTH, "I");
	if (fieldId != NULL) {
		width = (int) env->GetStaticIntField(params, fieldId);
	}
	fieldId = env->GetStaticFieldID(params, CASCADE_OUTPUT_FACE_HEIGHT, "I");
	if (fieldId != NULL) {
		height = (int) env->GetStaticIntField(params, fieldId);
	}
	faceRecParams->outputSize = Size(width, height);

	LOGD(
			"Cascade minNeighbour: %d, scaleFactor: %f, minSize: %d:%d, outputSize: %d:%d",
			faceRecParams->minNeighbour, faceRecParams->scaleFactor,
			faceRecParams->minSize.width, faceRecParams->minSize.height,
			faceRecParams->outputSize.width, faceRecParams->outputSize.height);
}

/*
 * Load the persisted model file for the face recognizer
 */
JNIEXPORT jlong JNICALL Java_com_hcb_saha_jni_NativeFaceRecognizer_loadPersistedModel(
		JNIEnv* env, jobject thiz, jstring modelFilePath, jclass params) {

	// Create a new wrapper instance so that we can keep it alive across JNI calls
	FaceRecWrapper* wrapper = new FaceRecWrapper();

	FaceRecParams* faceRecParams = new FaceRecParams();
	readFaceParameters(faceRecParams, env, params);
	wrapper->setParams(faceRecParams);

	Ptr<FaceRecognizer> model = createEigenFaceRecognizer();

	const char* modelFile = env->GetStringUTFChars(modelFilePath, NULL);
	LOGD("Reading model from file: %s", modelFile);

	// Load model from persisted model
	try {
		FileStorage fs(modelFile, FileStorage::READ);
		if (fs.isOpened()) {
			model->load(fs);
			fs.release();
		}
	} catch (Exception e) {
		// This likely means there are no data yet, which is fine
	}
	env->ReleaseStringUTFChars(modelFilePath, modelFile);

	// Save the model to the wrapper
	wrapper->setModel(model);
	return (jlong) wrapper;
}

/*
 * Delete the wrapper which will deallocate the face recognizer as well
 */
JNIEXPORT void JNICALL Java_com_hcb_saha_jni_NativeFaceRecognizer_deleteWrapper(
		JNIEnv* env, jobject thiz, jlong wrapperRef) {
	FaceRecWrapper* wrapper = (FaceRecWrapper*) wrapperRef;
	FaceRecParams* params = wrapper->getParams();
	delete params;
	delete wrapper;
}

/*
 * Train the recognizer with a set of images
 */
JNIEXPORT jboolean JNICALL Java_com_hcb_saha_jni_NativeFaceRecognizer_nativeTrainRecognizer(
		JNIEnv* env, jobject thiz, jintArray userIds,
		jobjectArray usersImagePathArray, jstring modelFilePath,
		jstring classifierPath, jlong wrapperRef) {

	// Vectors to hold image data and corresponding label (user id)
	vector<Mat> images;
	vector<int> labels;

	// usersImagePathArray is a String[userId][imagePaths] array.
	int userCount = env->GetArrayLength(userIds);
	// Array of user ids
	jint* userIdsArray = env->GetIntArrayElements(userIds, 0);

	// Load face classifier
	const char* classifier = env->GetStringUTFChars(classifierPath, NULL);
	CascadeClassifier haar_cascade;
	if (!haar_cascade.load(classifier)) {
		LOGD("Failed to load classifier!");
		return false;
	}
	env->ReleaseStringUTFChars(classifierPath, classifier);

	FaceRecWrapper* wrapper = (FaceRecWrapper*) wrapperRef;

	// Loop over all users
	for (int userIndex = 0; userIndex < userCount; userIndex++) {
		int userId = userIdsArray[userIndex];
		jobjectArray imagePathsArray =
				(jobjectArray) env->GetObjectArrayElement(usersImagePathArray,
						userIndex);
		int imageCount = env->GetArrayLength(imagePathsArray);

		// Loop over all image paths for that user
		for (int imageIndex = 0; imageIndex < imageCount; imageIndex++) {

			jstring imagePath = (jstring) env->GetObjectArrayElement(
					imagePathsArray, imageIndex);
			const char* imagePathC = env->GetStringUTFChars(imagePath, NULL);
			LOGD("User: %d, Image: %s", userId, imagePathC);

			Mat croppedFace;
			bool ret = detectAndCropFace(&haar_cascade, imagePathC,
					wrapper->getParams(), &croppedFace, NULL);

			if (ret) {
				labels.push_back(userId);
				images.push_back(croppedFace);
			} else {
				LOGD("Ignoring due to no face detected: %s", imagePathC);
			}

			env->ReleaseStringUTFChars(imagePath, imagePathC);
		}
	}

	env->ReleaseIntArrayElements(userIds, userIdsArray, 0);

	// Get the recognizer from the wrapper
	Ptr<FaceRecognizer> model = wrapper->getModel();
	const char* file = env->GetStringUTFChars(modelFilePath, NULL);

	try {
		// Train the recognizer
		model->train(images, labels);

		// Save the model to file
		LOGD("Writing model to file: %s", file);
		FileStorage fs(file, FileStorage::WRITE);
		if (fs.isOpened()) {
			model->save(fs);
			fs.release();
		}
	} catch (Exception e) {
		// OpenCV prints the error
		return false;
	}

	env->ReleaseStringUTFChars(modelFilePath, file);

	return true;
}

/*
 * Predict user id based on input image
 */
JNIEXPORT jint JNICALL Java_com_hcb_saha_jni_NativeFaceRecognizer_nativePredictUserId(
		JNIEnv* env, jobject thiz, jstring imagePath, jstring modelFilePath,
		jstring classifierPath, jlong wrapperRef) {

	FaceRecWrapper* wrapper = (FaceRecWrapper*) wrapperRef;
	// Get the recognizer from wrapper
	Ptr<FaceRecognizer> model = wrapper->getModel();

	// Load the classifier
	const char* classifier = env->GetStringUTFChars(classifierPath, NULL);
	CascadeClassifier haar_cascade;
	if (!haar_cascade.load(classifier)) {
		LOGD("Failed to load classifier!");
		return -1;
	}

	env->ReleaseStringUTFChars(classifierPath, classifier);

	// Read the image to predict from
	const char* image = env->GetStringUTFChars(imagePath, NULL);
	LOGD("Predicting from image file: %s", image);

	// Get the cropped/resized face
	Mat croppedFace;
	bool ret = detectAndCropFace(&haar_cascade, image, wrapper->getParams(),
			&croppedFace, NULL);
	env->ReleaseStringUTFChars(imagePath, image);

	int predictUserId = -1;
	if (ret) {
		try {
			predictUserId = model->predict(croppedFace);
		} catch (Exception e) {
			// Ignore - OpenCV prints the error anyways
		}
	} else {
		LOGD("Failed to detect/crop face!");
	}

	return predictUserId;

}

JNIEXPORT void JNICALL Java_com_hcb_saha_jni_NativeFaceRecognizer_dumpCroppedImages(
		JNIEnv* env, jobject thiz, jintArray userIds,
		jobjectArray usersImagePathArray, jobjectArray outputPaths, jstring modelFilePath,
		jstring classifierPath, jlong wrapperRef) {

	// TODO - dump all cropped faces to dir

}

}
 // extern "C"
