//package motionEstimation;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.PrintStream;
//
//import org.opencv.core.Core;
//import org.opencv.core.CvType;
//import org.opencv.core.Mat;
//import org.opencv.core.Point;
//import org.opencv.core.Scalar;
//import org.opencv.core.Size;
//import org.opencv.imgproc.Imgproc;
//import org.opencv.highgui.*;
//
//public class LKOF
//{
//	private static final int kernelSize = 5;
//	private static final double stdDev = 1.5;
//	private static final int winSize = 9;
//
//	public static void main( String[] args ) throws FileNotFoundException
//	{
//		long startTime = System.nanoTime();
//		PrintStream outputConsole = System.out;
//		try {
//			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//			//Load the image
//			Mat image0 = Highgui.imread("/Users/adithiathreya/Desktop/SCU/COEN296-VideoProcessing/Video_Processing_Fall2014/image0.png");
//			Mat image1 = Highgui.imread("/Users/adithiathreya/Desktop/SCU/COEN296-VideoProcessing/Video_Processing_Fall2014/image1.png");
//
//			if (image0.empty() || image1.empty()) {
//				System.out.println("One for the files could not be opened");
//			}
//			//Convert to Grayscale image
//			Mat image0Gray = new Mat(image0.rows(), image0.cols(), image0.type());
//			Mat image1Gray = new Mat(image1.rows(), image1.cols(), image1.type());
//			Imgproc.cvtColor(image0, image0Gray, Imgproc.COLOR_RGB2GRAY);
//			Imgproc.cvtColor(image1, image1Gray, Imgproc.COLOR_RGB2GRAY);
//
//			//Blur the image
//			Mat image0Blur = new Mat(image0Gray.rows(), image0Gray.cols(), image0Gray.type());
//			Mat image1Blur = new Mat(image1Gray.rows(), image1Gray.cols(), image1Gray.type());
//			Size kerSize = new Size(kernelSize,kernelSize);
//			Imgproc.GaussianBlur(image0Gray, image0Blur, kerSize, stdDev);
//			Imgproc.GaussianBlur(image1Gray, image1Blur, kerSize, stdDev);
////			Highgui.imwrite("image0Blur.jpg",image0Blur);
////			Highgui.imwrite("image1Blur.jpg",image1Blur);
//
//			//Calculate derivatives	
//			float[] arrKernel = {1.0f/12, -8.0f/12, 0, 8.0f/12, -1.0f/12};
//			Mat kernelX = new Mat(1, 5, CvType.CV_64FC1);
//			Mat kernelY = new Mat(5, 1, CvType.CV_64FC1);
//			for(int i=0;i<5;i++){
//				kernelX.put(0, i, arrKernel[i]);
//				kernelY.put(i, 0, arrKernel[i]);
//			}
//			Mat fx0 = new Mat(image0Blur.rows(), image0Blur.cols(), CvType.CV_64FC1);
//			Mat fy0 = new Mat(image0Blur.rows(), image0Blur.cols(), CvType.CV_64FC1);
//			Imgproc.filter2D(image0Blur, fx0, image0Blur.depth(), kernelX);
//			Imgproc.filter2D(image0Blur, fy0, image0Blur.depth(), kernelY);
//
//			Mat fx1 = new Mat(image1Blur.rows(), image1Blur.cols(), CvType.CV_64FC1);
//			Mat fy1 = new Mat(image1Blur.rows(), image1Blur.cols(), CvType.CV_64FC1);
//			Imgproc.filter2D(image1Blur, fx1, image1Blur.depth(), kernelX);
//			Imgproc.filter2D(image1Blur, fy1, image1Blur.depth(), kernelY);		
//
//			//fx = 0.5*fx0 + 0.5*fx1;
//			//fy = 0.5*fy0 + 0.5*fy1;
//			Mat fx = new Mat(image1Blur.rows(), image1Blur.cols(), CvType.CV_64FC1);
//			Core.addWeighted(fx0, 0.5, fx1, 0.5, 0, fx);
//			Mat fy = new Mat(image1Blur.rows(), image1Blur.cols(), CvType.CV_64FC1); 
//			Core.addWeighted(fy0, 0.5, fy1, 0.5, 0, fy);
//			Mat ft = new Mat(image1Blur.rows(), image1Blur.cols(), CvType.CV_64FC1);
//			Core.addWeighted(image1Blur, -1, image0Blur, 1, 0, ft);
//
//			System.setOut(new PrintStream(new FileOutputStream(new File("fx.txt"))));
//			System.out.println("fx" + fx.dump());
//			System.setOut(new PrintStream(new FileOutputStream(new File("fy.txt"))));
//			System.out.println("fy" + fy.dump());
//			System.setOut(new PrintStream(new FileOutputStream(new File("ft.txt"))));
//			System.out.println("ft" + ft.dump());
//
//			//calculate motion vector
//			Mat A = new Mat(winSize*winSize, 2,  CvType.CV_64FC1);
//			Mat b = new Mat(winSize*winSize, 1,  CvType.CV_64FC1);
//			Mat v = new Mat(2, 1, CvType.CV_64FC1);
//			Mat V = new Mat(image1Blur.rows(), image1Blur.cols(), CvType.CV_64FC2);
//			Mat dst1 = new Mat();
//			Mat dst2 = new Mat();
//			for(int  i=4; i<image0Blur.rows()-4; i++) {
//				for(int j =4; j<image0Blur.cols()-4; j++) {
//					int temp = 0;
//					for(int k=i-4; k<i+5; ++k) {
//						for (int l=j-4; l<j+5; ++l) {
//							A.put(temp, 0, fx.get(k, l));
//							A.put(temp, 1, fy.get(k, l));
//							b.put(temp, 0, ft.get(k, l));
//							temp++;
//						}
//					}
//					Core.gemm(A.t(), A, 1, new Mat(2, 2,  CvType.CV_64FC1), 0, dst1);
//					Core.gemm(A.t(), b, 1, new Mat(1, 1,  CvType.CV_64FC1), 0, dst2);
//					Core.solve(dst1, dst2, v);
//					V.put(i, j, v.get(0, 0)[0], v.get(1, 0)[0]);
//				}
//			}
//			System.setOut(new PrintStream(new FileOutputStream(new File("V.txt"))));
//			System.out.println("V" + V.dump());
//
//			//drawing the motion vectors
//			Mat dst = image0.clone();
//			Point p = new Point(0, 0);
//			Point q = new Point(0, 0);
//			for(int  i=4; i<image0Blur.rows()-4; i+=5) {
//				for(int j =4; j<image0Blur.cols()-4; j+=5) {
//					p = new Point(i, j);
//					q = new Point(i+V.get(i, j)[0], j+V.get(i, j)[1]);
//					q.x = (int) (q.x + 3);
//					q.y = (int) (q.y + 3);
//					Core.line(dst, p, q, new Scalar(0, 0, 255), 1, 8, 0);
//					//drawing arrows
//					double angle; 
//					angle = Math.atan2( (double) p.y - q.y, (double) p.x - q.x ); 
//					double hypotenuse; 
//					hypotenuse = Math.sqrt( Math.pow((p.y - q.y), 2) + Math.pow((p.x - q.x), 2));
//					hypotenuse = hypotenuse/4;
//					p.x = (int) (q.x+hypotenuse*Math.cos(angle+Math.PI/4));
//					p.y = (int) (q.y+hypotenuse*Math.sin(angle+Math.PI/4));
//					Core.line(dst, p, q, new Scalar(0,0,255), 1,8,0);
//					p.x = (int) (q.x+hypotenuse*Math.cos(angle-Math.PI/4));
//					p.y = (int) (q.y+hypotenuse*Math.sin(angle-Math.PI/4));
//					Core.line(dst, p, q, new Scalar(0,0,255), 1,8,0);
//				}
//			}
//			Highgui.imwrite("final.jpg",dst);
//
//		} catch (Exception e) {
//			System.out.println("Error: " + e.getMessage());
//		}
//		long endTime = System.nanoTime();
//		System.setOut(outputConsole);
//		System.out.println("Took "+(endTime - startTime) + " ns");
//	}
//}
