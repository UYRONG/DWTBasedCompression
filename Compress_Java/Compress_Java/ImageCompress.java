
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import java.util.Arrays;


public class ImageCompress {

	JFrame frame;
	JLabel lbIm1;
	BufferedImage imgOne;
	int width = 512; // default image width and height
	int height = 512;

	/** Read Image RGB
	 *  Reads the image of given width and height at the given imgPath into the provided BufferedImage.
	 */
	private void readImageRGB(int width, int height, String imgPath, BufferedImage img, int Level)
	{
		try
		{
			int frameLength = width*height*3;

			File file = new File(imgPath);
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			raf.seek(0);

			long len = frameLength;
			byte[] bytes = new byte[(int) len];

			raf.read(bytes);
			byte [][] Rchannel = new byte [height][width];
			byte [][] Gchannel = new byte [height][width];
			byte [][] Bchannel = new byte [height][width];	

			// read image and stored value in rgb channel seperatly
			int ind = 0;
			for(int y = 0; y < height; y++)
			{
				for(int x = 0; x < width; x++)
				{
					byte a = 0;
					byte r = bytes[ind];
					byte g = bytes[ind+height*width];
					byte b = bytes[ind+height*width*2]; 
					Rchannel[y][x] = r;
					Gchannel[y][x] = g;
					Bchannel[y][x] = b;
					ind++;
				}
			}
		

			//if(Level > -1){ // non-progressive
				// Encoding each Channel
				double [][] enRchannel = encode(Rchannel, Level);
				double [][] enGchannel = encode(Gchannel, Level);
				double [][] enBchannel = encode(Bchannel, Level);

			// Decoding each Channel and use the coeff based on parameters
				
				Rchannel = decode(enRchannel, Level, false);
				Gchannel = decode(enGchannel, Level, false);
				Bchannel = decode(enBchannel, Level, false);

				// display image
				int ind1 = 0;
				for(int y = 0; y < height; y++)
				{
					for(int x = 0; x < width; x++)
					{
						byte a = 0;
						byte r = Rchannel[y][x];
						byte g = Gchannel[y][x];
						byte b = Bchannel[y][x]; 

						int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
						//int pix = ((a << 24) + (r << 16) + (g << 8) + b);
						img.setRGB(x,y,pix);
						ind1++;
					}
				}
			
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public double[][] matrixTranspose(double[][] matrix){
		double[][] tempMatrix = new double[height][width];
		for(int j = 0; j < height; j++){
			for(int i = 0; i < width; i++){
				tempMatrix[j][i] = matrix[i][j];
			}
		}
		return tempMatrix;

	}
	
	// Implementing encoding, encoding is the same for all modes so just modified the bytes 
	public double[][] encode(byte [][] channel, int level){
		double [][] newChannel = new double [height][width];
		// convert byte to double
		for(int j = 0; j < height; j++){
			for(int i = 0; i < width; i++){
				newChannel[j][i] = Byte.toUnsignedInt(channel[j][i]);
			}
		}
		// NonstandardDecomposition 
		int index = height;
		int counter = 9-level;
		while (counter > 0){
			//System.out.println("!!! Get in counter");
			// row
			for (int i = 0; i < index; i++){
				double[] subline = new double[index];
				System.arraycopy(newChannel[i],0,subline,0,index);
				subline = decomposition(subline);
				System.arraycopy(subline,0,newChannel[i],0,index);
			}
			// column 
			for (int i = 0; i < index; i++){
				double[] temp = new double[index];
				for (int j = 0; j < index; j++){
					temp[j] = newChannel[j][i];// get column
				}
				temp = decomposition(temp);
				for (int j = 0; j < index; j++){
					newChannel[j][i] = temp[j];// write column back
				}
			}
			index = index/2;
			counter--;
		}
		return newChannel;

	}
	
	// Return low and high pass decomposition
	public double[] decomposition(double [] line){
		double[] result = Arrays.copyOf(line, line.length);
		for(int i = 0; i < line.length/2; i++){
			// low pass, the average value of two pairs from 0 to n/2 -1
			result[i] = (line[i*2] + line[i*2+1]) / 2;
			// high pass, the diff between two pairs from n/2 to n-1
			result[line.length/2 + i] = (line[i*2] - line[i*2+1]) / 2;
		}
		return result;
	}
	
	public byte[][] decode(double [][] enChannel, int level, boolean prog){
		byte [][] decoChannel = new byte [height][width];
		if(level != 9 & !prog){
			// zero off all the not required coefficient
			enChannel = zeroCoef(enChannel, level);
		}

		int index = (int) Math.pow(2,level+1);
		int counter = 9-level;
		while (counter > 0){
			//System.out.println("!!! Get out");
			// when converting back, convert column first and then row 
			// column 
			for (int i = 0; i < index; i++){
				double[] temp = new double[index];
				for (int j = 0; j < index; j++){
					temp[j] = enChannel[j][i];// get column
				}
				temp = composition(temp);
				for (int j = 0; j < index; j++){
					enChannel[j][i] = temp[j];// write column back
				}
			}
			// row
			for (int i = 0; i < index; i++){
				double[] subline = new double[index];
				System.arraycopy(enChannel[i],0,subline,0,index);
				subline = composition(subline);
				System.arraycopy(subline,0,enChannel[i],0,index);
			}
		
			index = index*2;
			counter--;
		}

		for(int j = 0; j < height; j++){
			for(int i = 0; i < width; i++){
				decoChannel[j][i] = (byte)(int) Math.round(enChannel[j][i]);
			}
		}
		return decoChannel;
	}

	public double[][] zeroCoef(double[][] channel, int level){
		int numCoeff = (int) Math.pow(2, level);
		for(int j = 0; j < height; j++){
			for(int i = 0; i < width; i++){
				if (i >= numCoeff || j >= numCoeff){
					channel[j][i] = 0;
				}
			}
		}
		return channel;
	}
	
	// convert back coefficient
	public double[] composition(double [] line){
		double[] result = Arrays.copyOf(line, line.length);
		for(int i = 0; i < line.length/2; i++){	
			result[i*2] = (line[i] + line[line.length/2 + i]);
			result[i*2+1] = (line[i] - line[line.length/2 + i]);
		}
		return result;
	}

	public void showIms(String[] args){

		// Read a parameter from command line
		String param1 = args[1];
		System.out.println("The second parameter was: " + param1);

		// Read in the specified image
		boolean progressive = Integer.parseInt(args[1]) == -1;
		String path = args[0];
		frame = new JFrame();
		lbIm1 = new JLabel();
		GridBagLayout gLayout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		frame.getContentPane().setLayout(gLayout);	
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		if(!progressive){
			imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			readImageRGB(width, height, path, imgOne, Integer.parseInt(args[1]));
			System.out.println("Current Level is " + Integer.parseInt(args[1]));
		
			// Use label to display the image
			lbIm1.setIcon(new ImageIcon(imgOne));			
			frame.getContentPane().add(lbIm1, c);
			frame.pack();
			frame.setVisible(true);
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);	
		}
		else{
			progressiveDisplay(c,path);
		}
	}
	public void progressiveDisplay(GridBagConstraints c, String path){
		for (int i = 0; i < 10; i++){		
			System.out.println("Current Level is " + i);
			imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			readImageRGB(width, height, path, imgOne, i);
			lbIm1.setIcon(new ImageIcon(imgOne));
			frame.getContentPane().add(lbIm1, c);
			frame.pack();
			frame.setVisible(true);	
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			try{
				// in order to make sure it's 24 fps
				//Thread.sleep(24);
				Thread.sleep(200);
			}
			catch(InterruptedException e){
				System.out.println("Oh No!");
			}
		
		}
	}

	public static void main(String[] args) {
		if(args.length<2){
			System.out.println("Missing arguments");
		}
		else{
			ImageCompress ren = new ImageCompress();
			ren.showIms(args);
		}
	}

}
