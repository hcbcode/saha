#include <opencv2/core/core.hpp>
#include <opencv2/contrib/contrib.hpp>

using namespace cv;

class FaceRecWrapper {
private:
	Ptr<FaceRecognizer> model;
public:
	void setModel(Ptr<FaceRecognizer> model);
	Ptr<FaceRecognizer> getModel();
};
