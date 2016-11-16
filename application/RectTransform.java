package application;

public class RectTransform {
	static public double[] discretUolshTransform(double signal[], boolean inverse)
	{
		int pointsCount = signal.length;
		double transform[] = new double[pointsCount];
		int sign = inverse ? 1:-1;
		for (int k=0; k<pointsCount; k++)
		{
			double sum = 0;
			for (int i = 0; i < pointsCount; i++)
			{
				sum += signal[i]*Adamar.getUolsh(pointsCount,k, i);
			}
			if(!inverse)
			{
				sum /= pointsCount;
			}
			
			transform[k] = sum;
		}
		return transform;
	}
	static public double[] discretAdamarTransform(double signal[], boolean inverse)
	{
		int pointsCount = signal.length;
		double transform[] = new double[pointsCount];
		int sign = inverse ? 1:-1;
		for (int k=0; k<pointsCount; k++)
		{
			double sum = 0;
			for (int i = 0; i < pointsCount; i++)
			{
				sum += signal[i]*Adamar.getAdamar(pointsCount,k, i);
			}
			if(!inverse)
			{
				sum /= pointsCount;
			}
			
			transform[k] = sum;
		}
		return transform;
	}	
}
