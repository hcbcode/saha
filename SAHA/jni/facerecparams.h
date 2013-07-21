#include <opencv2/core/core.hpp>

using namespace cv;

class FaceRecParams {
public:
	int minNeighbour;
	float scaleFactor;
	Size minSize;
	Size outputSize;
};
