package application;

import java.util.Arrays;

public class FurieModel {

	private static FurieModel model = null;
	private FurieModel(){}
	public static FurieModel getInstance()
	{
		if(model == null)
			model = new FurieModel();
		return model;
	}
	protected double[][] getCopy(double[][] from)
	{
		double result[][] = new double[2][];
		result[0] = Arrays.copyOf(from[0], from[0].length);
		result[1] = Arrays.copyOf(from[1], from[1].length);
		return result;
	}	
	protected int reverseBit(int x, int n)
	{
		int b = 0;
		int i=0;
		while (x!=0){
			  b <<= 1;
			  b |= (x & 1);
			  x>>=1;
			  i++;
			}
		if (i<n)
			b <<= n-i;
		return b;
	}
	
	protected double mulR(double r1, double i1,double r2, double i2)
	{
		return r1*r2-i1*i2;
	}
	protected double mulI(double r1, double i1,double r2, double i2)
	{
		return r1*i2+r2*i1;
	}
	
	public double[][] fastFurieNTransform(double signal[][], boolean inverse)
	{
		int pointsCount = signal[0].length;
		int M = pointsCount;
		int L=0;
		for (; (M&1) == 0; M>>=1, L++);
		L = 1<<L;
		if(L>1)
		{
			resultFFT = new double[2][];
			resultFFT[0] = new double [pointsCount];
			resultFFT[1] = new double [pointsCount];
			for (int start=0; start<M; start++)
			{
				fastFurieTransform(signal, inverse, true, M, start);
			}
		}
		else
		{
			resultFFT = getCopy(signal);
		}
			
		
		int sign = inverse ? 1:-1;

		double resultFull [][] = new double[2][];
		resultFull[0] = new double [pointsCount];
		resultFull[1] = new double [pointsCount];		
		for(int s=0; s<M; s++)
		{
		  for(int r=0; r<L; r++)
		  {
			  resultFull[0][r+s*L] = 0;
			  resultFull[1][r+s*L] = 0;
			  for(int m=0; m<M; m++)
			  {
				  double sin = Math.sin(sign*2*Math.PI*(m)*(r+s*L)/pointsCount);
				  double cos = Math.cos(sign*2*Math.PI*(m)*(r+s*L)/pointsCount);
					
				  resultFull[0][r+s*L] += mulR(resultFFT[0][m+r*M],resultFFT[1][m+r*M],cos,sin);
				  resultFull[1][r+s*L] += mulI(resultFFT[0][m+r*M],resultFFT[1][m+r*M],cos,sin);		  
			  }
			  
		  }
		}
		if(!inverse)
		for (int i=0; i<pointsCount; i++)
		{
			resultFull[0][i] /= pointsCount;
			resultFull[1][i] /= pointsCount;
		}		
		return resultFull;
	}

	public double[][] fastFurieTransform(double signal[][], boolean inverse)
	{
		int pointsCount = signal[0].length;
		resultFFT = new double[2][];
		resultFFT[0] = new double [pointsCount];
		resultFFT[1] = new double [pointsCount];
		fastFurieTransform(signal, inverse, inverse, 1, 0);
		return resultFFT;
	}
	double resultFFT [][];
	protected void fastFurieTransform(double signal[][], boolean inverse, boolean withoutDivision, int step, int start)
	{
		int pointsCount = signal[0].length/step;
		int n = (int)(Math.log(pointsCount)/Math.log(2));
		
		int sign = inverse ? 1:-1;
		
		for (int i=0; i<pointsCount; i++)
		{
			int j = reverseBit(i,n);
			resultFFT[0][j*step+start] = signal[0][i*step+start];
			resultFFT[1][j*step+start] = signal[1][i*step+start];
		}
		
		for (int s=2; s<=pointsCount; s<<=1)
		{
			double wm0 = Math.cos(sign*2*Math.PI/s); 
			double wm1 = Math.sin(sign*2*Math.PI/s); 
			for (int k=0; k<=pointsCount-1; k+=s)
			{
				double w0 = 1;
				double w1 = 0;
				for (int j = 0; j < s/2; j++)
				{
					int idx1 = (k+j)*step+start;
					int idx2 = idx1 + s*step/2;
					double u0 = resultFFT[0][idx1]; 
					double u1 = resultFFT[1][idx1]; 
					double t0 = mulR(w0,w1,resultFFT[0][idx2],resultFFT[1][idx2]); 
					double t1 = mulI(w0,w1,resultFFT[0][idx2],resultFFT[1][idx2]);
					resultFFT[0][idx1] = u0 + t0;
					resultFFT[1][idx1] = u1 + t1;
					resultFFT[0][idx2] = u0 - t0;
					resultFFT[1][idx2] = u1 - t1;
					double tmp = mulR(wm0,wm1,w0,w1); 
					w1 = mulI(wm0,wm1,w0,w1);
					w0 = tmp;
				}
			}
		}

		if(!withoutDivision)
		for (int i=0; i<pointsCount; i++)
		{
			resultFFT[0][i*step+start] /= pointsCount;
			resultFFT[1][i*step+start] /= pointsCount;
		}		
	}	
	
	public double[][] discretFurieTransform(double signal[][], boolean inverse)
	{
		int pointsCount = signal[0].length;
		double transform[][] = new double[2][];
		transform[0] = new double[pointsCount];
		transform[1] = new double[pointsCount];
		//int sign = inverse ? -1:1;
		int sign = inverse ? 1:-1;
		for (int k=0; k<pointsCount; k++)
		{
			double a = 0;
			double b = 0;			
			for (int i = 0; i < pointsCount; i++)
			{
				double sin = Math.sin(2*Math.PI*k*sign*i/pointsCount);
				double cos = Math.cos(2*Math.PI*k*sign*i/pointsCount);
				a += mulR(cos,sin,signal[0][i],signal[1][i]);
				b += mulI(cos,sin,signal[0][i],signal[1][i]);
				/*double sin = Math.sin(2*Math.PI*k*sign*i/pointsCount);
				double cos = Math.cos(2*Math.PI*k*sign*i/pointsCount);
				
				a += cos*signal[0][i]+sin*signal[1][i];
				b += -sin*signal[0][i]+cos*signal[1][i];*/			
			}
			if(!inverse)
			{
				a /= pointsCount;
				b /= pointsCount;
			}
			
			transform[0][k] = a;
			transform[1][k] = b;
		}
		return transform;
	}
	
	public double result[][];
	public double[][] filter(double signal[][], double topBorder, double bottomBorder, boolean isInside)
	{
		int pointsCount = signal[0].length;
		double[][] transformation = model.fastFurieNTransform(signal,false);
		result = model.getCopy(transformation);
		for (int k=0; k<pointsCount; k++)
			if(!isInside)
			{
				if(k>topBorder && k<bottomBorder || k<pointsCount-topBorder && k>pointsCount-bottomBorder)
					result[0][k] = result[1][k] = 0; 
			}	
			else
			{
				if(!(k>topBorder && k<bottomBorder || k<pointsCount-topBorder && k>pointsCount-bottomBorder))
					result[0][k] = result[1][k] = 0; 
				
			}

		return model.fastFurieNTransform(result,true);		
	}
}
