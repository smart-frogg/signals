package application;

import java.util.Arrays;

import application.FurieModel;

public class Filter {
	private double h[][];
	private double signal[][];
	private double transformation[][];
	private double amplitudeSpector[];
	private double logAmplitudeSpector[];
	private double phaseSpector[];
	int Fd;
	public void genSignal(int N, double fc, int Fd)
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
				h[0][i] = 2*fc;
			}
			else
			{
				h[0][i] = Math.sin(2*Math.PI * fc * (i - M/2)) / (Math.PI * (i - M/2));
			}
			sum += h[0][i];
			h[1][i] = 0;
		}
		
		signal = new double[2][];
		signal[0] = new double[Fd+1];
		signal[1] = new double[Fd+1];
		for(int f=0; f<=Fd; f++)
		{
			signal[0][f] = 0;
			signal[1][f] = 0;
			for(int i=0; i<N; i++)
			{
				double cos = Math.cos(-2*f*i/(Fd));
				double sin = Math.sin(-2*f*i/(Fd));
				signal[0][f] += FurieModel.getInstance().mulR(h[0][i], h[1][i], cos, sin);
				signal[1][f] += FurieModel.getInstance().mulI(h[0][i], h[1][i], cos, sin);
			}
			//signal[0][f] /= f;
			//signal[1][f] /= f;
		}
	}
	protected void calcAmplitudeAndPhase()
	{
		int pointsCount = signal[0].length;
		amplitudeSpector = new double[pointsCount];
		logAmplitudeSpector = new double[pointsCount];
		phaseSpector = new double[pointsCount];
		for (int k=0; k<pointsCount; k++)
		{
			amplitudeSpector[k] = Math.sqrt(transformation[0][k]*transformation[0][k]+transformation[1][k]*transformation[1][k]);
			phaseSpector[k] = Math.atan(transformation[1][k]/transformation[0][k]);
			logAmplitudeSpector[k] =20*Math.log10(amplitudeSpector[k]);
		}
	}
	
	public void transform()
	{
		transformation = signal;//FurieModel.getInstance().fastFurieNTransform(signal, false);
		calcAmplitudeAndPhase();
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
	public double[] getPhase()
	{
		return phaseSpector;
	}


}
