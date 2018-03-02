
package vectorquantize;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class VectorQuantize {
	static ArrayList<Node> leaves=new ArrayList<Node> ();
	static ArrayList<block> block=new ArrayList<block>();
        static int width=0, hight=0, vectorHeight=2, vectorWidth=2, bits=4;
	public static void main(String[] args) throws IOException {
		//compress();
                //decompress();
                GUI x=new GUI();
                x.show();
	}

public static void compress(String h,String w,String b,String path) throws IOException {
	vectorHeight=Integer.parseInt(h);
        vectorWidth=Integer.parseInt(w);
        bits=Integer.parseInt(b);
	Node root=new Node(vectorHeight, vectorWidth);
	int[][] original=VectorQuantize.readImage(path);
	int restHeight=original.length%vectorHeight; 
	int restWidth=original[0].length%vectorWidth;
	if(restHeight!=0)
        {
            restHeight= vectorHeight-restHeight;
	}
	if(restWidth!=0)
        {
            restWidth=vectorWidth-restWidth;
	}
	
	hight=original.length+restHeight;
	width=original[0].length+restWidth;
	int [][]modified = new int [hight][width];
	modifyArray(modified,original);
	splitToBlocks(root, modified);
	traverse(root);
	getLeaves(root); 
	for(int i=0;i<leaves.size();i++) 
        {
            calculateAvg(leaves.get(i));
            block temp=new block(vectorHeight,vectorWidth); 
            for(int j=0;j<vectorHeight;j++) 
            {
                for(int k=0;k<vectorWidth;k++)
                {
                    temp.value[j][k]= Math.round(leaves.get(i).avg[j][k]);
                }	
            }
            temp.num=i;
            block.add(temp);
	}
	BufferedWriter buffer = new BufferedWriter (new FileWriter ("tags.txt"));
	for(int i=0;i<root.arr.size();i++)
        {
		buffer.write(get_q(root.arr.get(i))+".");		
	}
	buffer.close();
	
	
}
	
public static void decompress() throws IOException {
	BufferedReader buf= new BufferedReader (new FileReader ("tags.txt"));
	String bff= buf.readLine();
	String[] arr = bff.split("\\.");
	int [][] pixels=new int [hight][width];
	float[][] Block=new float [vectorHeight][vectorWidth];
	int temp=0,index=0;
	for(int i=0;i<pixels.length;i+=vectorHeight)
        {
		for(int j=0;j<pixels[i].length;j+=vectorWidth)
                {
			temp=Integer.parseInt(arr[index]);
			Block=block.get(temp).value;
			for(int k=0;k<vectorHeight;k++)
                        {
				for(int q=0;q<vectorWidth;q++)
                                {
					pixels[i+q][j+k]=(int) Block[k][q];
				}
			}
			index++;
		}
	}
	VectorQuantize.writeImage(pixels, "output.jpg");
}

public static int get_q(int [][] Block){
	float min=0,temp=0;
	int index=0;
	for(int j=0;j<vectorHeight;j++)
        {
		for(int k=0;k<vectorWidth;k++)
                {
			min+= Math.abs(block.get(0).value[j][k] - Block[j][k] );
		}
	}
	for(int i=1;i<block.size();i++) 
        {
		for(int j=0;j<vectorHeight;j++)
                {
			for(int k=0;k<vectorWidth;k++)
                        {
				temp+=Math.abs(block.get(i).value[j][k] - Block[j][k] );
			}
		}
		if(temp<min)
                {
			min=temp;
			index=i;
		}
                temp=0;       
	}	
	return block.get(index).num;
	
}
	
public static void build(Node root) {
	calculateAvg(root);
	root.left=new Node(vectorHeight,vectorWidth);
	root.right=new Node(vectorHeight,vectorWidth);
	int rightDiff=0,leftDiff=0; 
	for(int i=0;i<vectorHeight;i++)
        {
		for(int j=0;j<vectorWidth;j++)
                {
			root.left.value[i][j]=root.avg[i][j]-1;
			root.right.value[i][j]=root.avg[i][j]+1;
		}
	}
	for(int i=0;i<root.arr.size();i++)
        {
		for(int j=0;j<vectorHeight;j++)
                {
			for(int k=0;k<vectorWidth;k++)
                        {
				rightDiff+=Math.abs(root.arr.get(i)[j][k] - root.right.value[j][k]);
				leftDiff+=Math.abs(root.arr.get(i)[j][k] - root.left.value[j][k]);
			}
		}
		if(leftDiff<rightDiff)
                {
			root.left.arr.add(root.arr.get(i));
		}
		else
                {
			root.right.arr.add(root.arr.get(i));
		}
		rightDiff=0;
		leftDiff=0; 
	}
}

public static void traverse(Node root)
{
	bits--;
	if(bits<0) {
		return;
	}
	build(root);	
	traverse(root.left);
	traverse(root.right);
}

public static void getLeaves(Node root) {
	if(root==null)
        {
		return;
	}
	if(root.right==null&&root.right==null) 
        {
		leaves.add(root);	
	}
	else 
        {
		getLeaves(root.left);
		getLeaves(root.right);
	}
}
	
public static void splitToBlocks(Node root,int [][] im)
{
                for(int i=0;i<im.length;i+= vectorHeight)
                {
			for(int j=0;j<im[i].length;j+=vectorWidth)
                        {
				for(int k=0;k<vectorHeight;k++)
                                {
					for(int q=0;q<vectorWidth;q++)
                                        {
						root.block[k][q]=im[i+k][j+q];
					}
					
				}
				root.arr.add(root.block);
				root.block=new int[vectorHeight][vectorWidth];
			}
		}
		
}
		
public static void calculateAvg(Node root) 
{
	for(int i=0;i<root.arr.size();i++)
        {
		for(int j=0;j<root.arr.get(i).length;j++)
                {
			for(int k=0;k<root.arr.get(i)[j].length;k++)
                        {
				
				root.avg[j][k] += (float)root.arr.get(i)[j][k]/root.arr.size();
			}
		}
	}
	
}

public static void modifyArray(int [][]modified , int [][]original) {
	for(int i=0;i<modified.length;i++) {
		for(int j=0;j<modified[i].length;j++) {
			modified[i][j]=0;
		}
	}
	for(int i=0;i<original.length;i++) {
		for(int j=0;j<original[i].length;j++) {
			modified[i][j]=original[i][j];
		}
	}
}

	
public static int[][] readImage(String path){
	BufferedImage img;
	try {
		img = ImageIO.read(new File(path));
	int hieght=img.getHeight();
	int width=img.getWidth();
	int[][] imagePixels=new int[hieght][width];
	for(int x=0;x<width;x++){
		for(int y=0;y<hieght;y++){
			int pixel=img.getRGB(x, y);
			int red=(pixel  & 0x00ff0000) >> 16;
			int grean=(pixel  & 0x0000ff00) >> 8;
			int blue=pixel  & 0x000000ff;
			int alpha=(pixel & 0xff000000) >> 24;
			imagePixels[y][x]=red;
		}
	}
	return imagePixels;
	} catch (IOException e) {
		// TODO Auto-generated catch block
		return null;
	}
}

public static void writeImage(int[][] imagePixels,String outPath){
	BufferedImage image = new BufferedImage(imagePixels[0].length, imagePixels.length, BufferedImage.TYPE_INT_RGB);
  
	for (int y= 0; y < imagePixels.length; y++) {
        for (int x = 0; x < imagePixels[y].length; x++) {
             int value =-1 << 24;
             value= 0xff000000 | (imagePixels[y][x]<<16) | (imagePixels[y][x]<<8) | (imagePixels[y][x]);
             image.setRGB(x, y, value); 
        }
    }
    File ImageFile = new File(outPath);
    try {
        ImageIO.write(image, "jpg", ImageFile);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
}
