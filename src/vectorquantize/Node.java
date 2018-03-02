
package vectorquantize;


import java.util.ArrayList;

class block {
	float [][]value;
	int num;
	public block(int vectorHeight, int vectorWidth) {
		value=new float[vectorHeight][vectorWidth];
                num=0;
	}
}

public class Node {
	Node left;
	Node right;
	float avg[][];
	float value[][];
	int [][]block;
	ArrayList<int[][]> arr;
	Node (int vectorHeight , int vectorWidth){
		left =null;
		right=null;
		avg =new float [vectorHeight][vectorWidth];
		value=new float[vectorHeight][vectorWidth];
		block=new int[vectorHeight][vectorWidth];
		arr=new ArrayList<int [][]>();
		for(int i=0;i<vectorHeight;i++) {
			for(int k=0;k<vectorWidth;k++) {
				avg[i][k]=0;
			}
		}
		}
	}

