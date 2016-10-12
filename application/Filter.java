package application;

import java.util.Arrays;

import application.FurieModel;

public class Filter {
	int N;
	double fc;
	boolean isHigh;
	private double h[][];
	private double w[];
	
	private double signal[][];
	private double transformation[][];
	private double amplitudeSpector[];
	private double logAmplitudeSpector[];
	int Fd;
	
	public Filter(int N, double fc, boolean isHigh)
	{
		this.N = N;
		this.fc = fc;
		this.isHigh = isHigh;
		genH();
		genWRectangular();
	}
	
	protected void genH()
	{
		this.Fd = Fd;
		int M = N-1;
		h = new double[2][];
		h[0] = new double[N];
		h[1] = new double[N];
		double sum = 0;
		for(int i=0; i<N; i++)
		{
			if (i==M/2)
			{
				if (isHigh)
					h[0][i] = 1 - 2*fc;
				else
					h[0][i] = 2*fc;
			}
			else
			{
				h[0][i] = (isHigh?-1:1) *Math.sin(2*Math.PI * fc * (i - M/2)) / (Math.PI * (i - M/2));
			}
			sum += h[0][i];
			h[1][i] = 0;
		}
	}

	public void genWRectangular()
	{
		w = new double[N];
		for (int i=0;i<N;i++)
		{
			w[i] = 1;
		}		
	}
	public void genWBartlett()
	{
		w = new double[N];
		for (int i=0;i<N;i++)
		{
			w[i] = 1 - 2 * Math.abs((i-(N-1)/2.))/(N-1);
		}		
	}
	public void genWHanning()
	{
		w = new double[N];
		for (int i=0;i<N;i++)
		{
			w[i] = 0.5 - 0.5 * Math.cos((2*Math.PI*i) /(N-1)) ;
		}		
	}	
	public void genWHamming()
	{
		w = new double[N];
		for (int i=0;i<N;i++)
		{
			w[i] = 0.54 - 0.46 * Math.cos((2*Math.PI*i) /(N-1)) ;
		}		
	}	
	public void genWBlackman()
	{
		w = new double[N];
		for (int i=0;i<N;i++)
		{
			w[i] = 0.42 - 0.5 * Math.cos((2*Math.PI*i) /(N-1)) + 0.08 * Math.cos((4*Math.PI*i) /( N-1));
		}		
	}
	
	protected void calcAmplitude()
	{
		amplitudeSpector = new double[transformation[0].length];
		logAmplitudeSpector = new double[transformation[0].length];
		for (int k=0; k<transformation[0].length; k++)
		{
			amplitudeSpector[k] = Math.sqrt(transformation[0][k]*transformation[0][k]+transformation[1][k]*transformation[1][k]);
			logAmplitudeSpector[k] =20*Math.log10(amplitudeSpector[k]);
		}
	}
	
	public void transform(double[][] newSignal)
	{
		transformation = FurieModel.getInstance().fastFurieNTransform(newSignal, false);
		calcAmplitude();
	}
	public double[] getSignal()
	{
		return h[0];
	}	
	public double[] getAmplitude()
	{
		double[] res = Arrays.copyOf(amplitudeSpector, amplitudeSpector.length/2);
		return res;
	}
	public double[] getLogAmplitude()
	{
		double[] res = Arrays.copyOf(logAmplitudeSpector, logAmplitudeSpector.length/2);
		return res;
	}
	
	public double[][] filter(double signal[])
	{
		double[][] newSignal = new double[2][];
		newSignal[0] = new double[signal.length];
		newSignal[1] = new double[signal.length];
		for (int k=0;k<signal.length;k++)
		{
			newSignal[1][k] = 0;
			newSignal[0][k] = 0; 
			for(int i=0; i<(N>k+1?k+1:N); i++)
			{
				newSignal[0][k] += w[i]*h[0][i]*signal[k-i];
			}
			newSignal[1][k] = 0;
		}
		return newSignal;
	}


}
