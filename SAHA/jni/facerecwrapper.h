#include <opencv2/core/core.hpp>
#include <opencv2/contrib/contrib.hpp>
#include "facerecparams.h"

//class FaceRecParams;

using namespace cv;

class FaceRecWrapper {
private:
	Ptr<FaceRecognizer> model;
	FaceRecParams* params;
public:
	void setModel(Ptr<FaceRecognizer> model);
	Ptr<FaceRecognizer> getModel();
	void setParams(FaceRecParams* params);
	FaceRecParams* getParams();
};
