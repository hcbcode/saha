#include "facerecwrapper.h"
#include <opencv2/core/core.hpp>
#include <opencv2/contrib/contrib.hpp>

using namespace cv;

void FaceRecWrapper::setModel(Ptr<FaceRecognizer> m) {
	model = m;
}

Ptr<FaceRecognizer> FaceRecWrapper::getModel() {
	return model;
}
