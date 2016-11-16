package application;

import java.util.Arrays;

public class IIRFilters {
	int N;
	double dots[][];
	double eps;
	double w0;
	boolean lowF = true;
	
	public IIRFilters(int N, double eps, double w0, boolean lowF)
	{
		this.N = N;
		this.eps = eps;
		this.w0 = w0;
		step = w0/100;
		this.lowF = lowF;
	}

	final static int DOTS_COUNT = 200;
	double step = 0.01;
	
	private void createDots()
	{
		dots = new double[2][];
		dots[0] = new double[DOTS_COUNT];
		dots[1] = new double[DOTS_COUNT];
	}

	private double getArg(double w)
	{
		if (lowF)
			return w/w0;
		else
			return w0/w;
			
	}
	public void dotsButterworth()
	{
		createDots();
		for (int i=1;i<DOTS_COUNT;i++)
		{
			dots[0][i] = i*step;
			dots[1][i] = 1/Math.sqrt(1+Math.pow(getArg(i*step), 2*N));
		}		
	}
	public double[] filterButterworth(double x[], double fs)
	{
		int n = x.length;
		double[] y = new double[n];
		double dt = 1.0/fs;
		double tau = 1.0/(2.0*Math.PI*w0);
		double alpha = dt / (dt + 2*tau);
		y[0] = 0;
		for (int i = 1; i < n; i++)
		    y[i] = alpha*(x[i]+x[i-1]) + (1-2*alpha)*y[i-1];
		for (int i = 2; i < n; i+=2)
			y[i] = (Math.pow(dt,2)*x[i] + 2*Math.pow(dt,2)*x[i-1] + Math.pow(dt,2)*x[i-2] - (2*Math.pow(dt,2)-8*Math.pow(tau,2))*y[i-1] - (Math.pow(dt,2)-4*dt*tau+4*Math.pow(tau,2))*y[i-2]) / 
			(Math.pow(dt,2)+4*dt*tau+4*Math.pow(tau,2));
		return y;
		/*
		int n = x.length;
		double[] y = new double[n];
		double dt = 1.0/fs;
		double tau = 1.0/(2.0*Math.PI*w0);
		double alpha = dt / (dt + 2*tau);
		y[0] = 0;
		for (int i = 1; i < n; i++)
		    y[i] = alpha*(x[i]+x[i-1]) + (1-2*alpha)*y[i-1];
		for (int i = 2; i < n; i+=2)
			y[i] = (Math.pow(dt,2)*x[i] + 2*Math.pow(dt,2)*x[i-1] + Math.pow(dt,2)*x[i-2] - (2*Math.pow(dt,2)-8*Math.pow(tau,2))*y[i-1] - (Math.pow(dt,2)-4*dt*tau+4*Math.pow(tau,2))*y[i-2]) / 
			(Math.pow(dt,2)+4*dt*tau+4*Math.pow(tau,2));
		return y;
		/*double[][] newSignal = new double[2][];
		newSignal[0] = new double[signal.length];
		newSignal[1] = new double[signal.length];
		Complex[] oldSignalC = new Complex[signal.length];
		for (int k=0;k<signal.length;k++)
		{
			oldSignalC[k] = new Complex(signal[k]);
		}
		Complex[] newSignalC = new Complex[signal.length];
		Complex zero = new Complex(0);
		if (N%2 == 1)
		{
			double b0 = 1/(1-2*fc);
			double b1 = 1/(1-2*fc);
			double b2 = 0;
			double a1 = (2*fc+1)/(1-2*fc);
			double a2 = 0;
			
			for (int k=0;k<signal.length;k++)
			{
				Complex c1 = k>0? oldSignalC[k-1].multiply(b1):zero;
				Complex c2 = k>1? oldSignalC[k-2].multiply(b2):zero;
				Complex c3 = k>0? newSignalC[k-1].multiply(a2):zero;
				Complex c4 = k>1? newSignalC[k-2].multiply(a1):zero;
				newSignalC[k] = oldSignalC[k].multiply(b0).add(c1).add(c2).add(c3).add(c4);
				Math.pow((1+alpha)/2,2)*(x[i]+2*x[i-1]+x[i-2]) - 2*alpha*y[i-1] - Math.pow(alpha,2)*y[i-2]
			}
			Complex[] tmp = newSignalC;
			newSignalC = oldSignalC;
			oldSignalC = tmp;
		}
		double div = 4*fc*fc-2*Math.sin(Math.PI/4)+1;
		double b0 = 1/div;
		double b1 = 2/div;
		double b2 = 1/div;
		double a1 = (4*fc*fc+2*Math.sin(Math.PI/4)+1)/div;
		double a2 = (8*fc*fc+2)/div;
		for (int i=0;i<N;i+=2)
		{
			
			for (int k=0;k<signal.length;k++)
			{
				Complex c1 = k>0? oldSignalC[k-1].multiply(b1):zero;
				Complex c2 = k>1? oldSignalC[k-2].multiply(b2):zero;
				Complex c3 = k>0? newSignalC[k-1].multiply(a2):zero;
				Complex c4 = k>1? newSignalC[k-2].multiply(a1):zero;
				newSignalC[k] = oldSignalC[k].multiply(b0).add(c1).add(c2).add(c3).add(c4);
			}
			Complex[] tmp = newSignalC;
			newSignalC = oldSignalC;
			oldSignalC = tmp;
		}
		for (int k=0;k<signal.length;k++)
		{
			newSignal[0][k] = oldSignalC[k].real();
			newSignal[1][k] = oldSignalC[k].imag();
		}*/
	//	return newSignal;
	}

	public void dotsAbsKButterworth()
	{
		createDots();
		for (int i=1;i<DOTS_COUNT;i++)
		{
			dots[0][i] = i*step;
			dots[1][i] = Complex.abs(kButterworth(getArg(i*step)));
		}		
	}	
	public void dotsArgKButterworth()
	{
		createDots();
		for (int i=1;i<DOTS_COUNT;i++)
		{
			dots[0][i] = i*step;
			dots[1][i] = Complex.arg(kButterworth(getArg(i*step)));
		}		
	}	
	public void dotsArgCorrectKButterworth()
	{
		createDots();
		for (int i=1;i<DOTS_COUNT;i++)
		{
			dots[0][i] = i*step;
			dots[1][i] = Complex.arg(kButterworth(getArg(i*step)));
			while(i>0 && dots[1][i] > dots[1][i-1])
				dots[1][i]-=2*Math.PI;
		}		
	}
	public Complex kButterworth(double w)
	{
		Complex p = new Complex(1);
		Complex s = new Complex(0,w);
		Complex one = new Complex(1);
		Complex two = new Complex(2);
		int G = N/2;
		if (N%2 == 1)
		{
			Complex s1 = s.add(one);
			p = p.multiply(s1);
		}
		Complex s2 = s.multiply(s);
		for (int i=1;i<=G;i++)
		{
			double arg = Math.PI*(2*i-1)/(2*N);
			Complex sin = new Complex(Math.sin(arg));
			p = p.multiply(s2.add(two.multiply(s).multiply(sin)).add(one));
		}	
		return one.divide(p);
	}	
	
	public void dotsChebyshevTypeI()
	{
		createDots();
		for (int i=1;i<DOTS_COUNT;i++)
		{
			dots[0][i] = i*step;
			double t = T(getArg(i*step));
			dots[1][i] = 1/Math.sqrt(1+eps*eps*t*t);
		}		
	}
	public void dotsAbsKChebyshevTypeI()
	{
		createDots();
		for (int i=1;i<DOTS_COUNT;i++)
		{
			dots[0][i] = i*step;
			dots[1][i] = Complex.abs(kChebyshevTypeI(getArg(i*step)));
		}		
	}	
	public void dotsArgKChebyshevTypeI()
	{
		createDots();
		for (int i=1;i<DOTS_COUNT;i++)
		{
			dots[0][i] = i*step;
			dots[1][i] = Complex.arg(kChebyshevTypeI(getArg(i*step)));
		}		
	}	
	public void dotsArgCorrectKChebyshevTypeI()
	{
		createDots();
		for (int i=1;i<DOTS_COUNT;i++)
		{
			dots[0][i] = i*step;
			dots[1][i] = Complex.arg(kChebyshevTypeI(getArg(i*step)));
			while(i>0 && dots[1][i] > dots[1][i-1])
				dots[1][i]-=2*Math.PI;
		}		
	}
	static double asinh(double x)
	{
		return Math.log(x + Math.sqrt(x*x + 1.0));
	} 
	public Complex kChebyshevTypeI(double w)
	{
		Complex p = new Complex(1);
		Complex s = new Complex(0,w);
		Complex s2 = s.multiply(s);
		
		Complex one = new Complex(1);
		Complex two = new Complex(2);
		double beta = asinh(1/eps)/N; 
		Complex teta0 = new Complex(-Math.sinh(beta));
		int G = N/2;
		if (N%2 == 1)
		{
			Complex s1 = s.subtract(teta0);
			p = p.multiply(teta0.neg().divide(s1));
		}
		for (int i=1;i<=G;i++)
		{
			double arg = Math.PI*(2*i-1)/(2*N);
			Complex tetak = new Complex(-Math.sin(arg)*Math.sinh(beta));
			Complex wk = new Complex(Math.cos(arg)*Math.cosh(beta));
			Complex tetak2 = tetak.multiply(tetak);
			Complex wk2 = wk.multiply(wk);
			Complex wk2tetak2 = wk2.add(tetak2); 
			p = p.multiply(wk2tetak2.divide(s2.add(wk2tetak2).subtract(two.multiply(s).multiply(tetak))));
		}	
		return p;
	}		
	
	public void dotsChebyshevTypeII()
	{
		createDots();
		for (int i=1;i<DOTS_COUNT;i++)
		{
			dots[0][i] = i*step;
			double t = T(1/getArg(i*step));
			dots[1][i] = 1/Math.sqrt(1+1/(eps*eps*t*t));
		}		
	}	
	double T(double w)
	{
		double T0=1;
		double T1=w;
		for(int i=2;i<=N;i++)
		{
			double t=2*w*T1-T0;
			T0 = T1;
			T1 = t;
		}	
		return T1;
	}

}
