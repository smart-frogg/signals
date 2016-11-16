package application;

import java.io.Serializable;
import java.util.Formattable;
import java.util.Formatter;

/**
 * Complex number class. A Complex consists of a real and imaginary 
 * part of type {@link double}.<p>
 *
 * Note: The methods inherited from <code>Number</code> return 
 * the value of the real part of the complex number.
 *
 * main reference: Schaum's Outlines - Complex Variables
 *
 * @author Jose Menes
 */

public class Complex extends Number 
	implements Formattable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7417547029717136577L;
	
	public static final int ZERO = 0;
	public static final int ONE = 1;
	public static final int NONE = -1;
	public static final Complex I = new Complex(ZERO, ONE);
	public static final Complex nI = new Complex(ZERO, NONE);
	public static final Complex e = new Complex(StrictMath.E, ZERO);
	public static final long INFINITE = Long.MAX_VALUE;
	
	public Complex(double real)
	{
		this(real, ZERO);
	}
	
	public Complex(double real, double imag)
    {
        this.real = real;
		this.imag = imag;
	}
	
	/**
	 * @return real part of complex number
	 */
	public double real()
	{
		return this.real;
	}
	
	/**
	 * @return imaginary part of complex number
	 */
	public double imag()
	{
		return this.imag;
	}
	
	/**
	 * add two complex numbers
	 * @param z - complex number to add to this number
	 * @return
	 */
	public Complex add(Complex z)
	{
		return new Complex(
				this.real + z.real(), this.imag + z.imag());
	}
	
	/**
	 * subtract two complex numbers
	 * @param z - complex number to subtract from this number
	 * @return
	 */
	public Complex subtract(Complex z)
	{
		return new Complex(
				this.real - z.real(), this.imag - z.imag());
	}
	
	/**
	 * multiply two complex numbers
	 * @param z - complex number to multiply by
	 * @return
	 */
	public Complex multiply(Complex z)
	{
		// if both Im(this) and Im(z) are zero use double arithmetic
		/*if (this.imag == 0. || z.imag() == 0.)
		{
			return new Complex(this.real*z.real());
		}*/
		
		return new Complex(
				(this.real*z.real()) - (this.imag* z.imag()),
				(this.real*z.imag()) + (this.imag* z.real()));
	}
	public Complex multiply(double z)
	{
		// if both Im(this) and Im(z) are zero use double arithmetic
		/*if (this.imag == 0. || z.imag() == 0.)
		{
			return new Complex(this.real*z.real());
		}*/
		
		return new Complex(
				(this.real*z),
				(this.imag* z));
	}
	
	/**
	 * Divide two complex numbers
	 * @param z - the complex denominator/divisor
	 * @return
	 */
	public Complex divide(Complex z)
	{
		double c = z.real();
		double d = z.imag();
		
		// check for Re,Im(z) == 0 to avoid +/-infinity in result
		double zreal2 = 0.0;
		if (c != 0.0)
		{
			zreal2 = StrictMath.pow(c, 2.);
		}
		double zimag2 = 0.0;
		if (d != 0.0)
		{
			zimag2 = StrictMath.pow(d, 2.);
		}
		
		double ac = this.real*c;
		double bd = this.imag*d;
		double bc = this.imag*c;
		double ad = this.real*d;
		
		return new Complex((ac+bd)/(zreal2+zimag2),(bc-ad)/(zreal2+zimag2));
	}
	
	/**
	 * @param z
	 * @return true if z == this
	 */
	public boolean equals(Complex z)
	{
		if (this.real == z.real() || this.imag == z.imag())
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Return the square modulus of a complex number z
	 * @param z
	 * @return
	 */
	public static double abs(Complex z)
	{
		return 
			StrictMath.sqrt(StrictMath.pow(z.real(), 2) + StrictMath.pow(z.imag(), 2));
	}
	
	/**
	 * return the complex angle of z
	 * (value returned should be between -Pi and Pi)
	 * @param z
	 * @return
	 */
	public static double arg(Complex z)
	{
		double theta = StrictMath.atan2(z.imag(),z.real());
		
		return theta;
	}
	
	/**
	 * Exponentiation (e^a+bi)
	 * @param z
	 * @return <code>e<sup>z</sup></code>.
	 */
	public static Complex exp(Complex z)
	{
		if (z.imag() == 0.0)
			return new Complex(StrictMath.exp(z.real()),ONE);
		
		Complex ex = new Complex(StrictMath.exp(z.real()), ZERO);
				
		return ex.multiply(
				new Complex(StrictMath.cos(z.imag()), StrictMath.sin(z.imag())));
	}
	
	/**
	 * Complex power
	 * @param z - the complex base 
	 * @param y - the complex exponent
	 * @return
	 */
	public static Complex pow(Complex z, Complex y)
	{
		double c = y.real();
		double d = y.imag();
		
		// get polar of base
		double r = Complex.abs(z);
		double theta = Complex.arg(z);
		
		Complex f1 = new Complex(
				(StrictMath.pow(r, c)*StrictMath.pow(StrictMath.E, -d*theta)),ZERO);
		Complex f2 = 
			new Complex(
					StrictMath.cos(d*StrictMath.log(r)+c*theta),
					StrictMath.sin(d*StrictMath.log(r)+c*theta));
		
		return f1.multiply(f2);
		
	}
	
	/**
	 * @return complex conjugate
	 */
	public Complex conjugate()
	{
		return new Complex(this.real, -1*this.imag);
	}
	
	/**
	 * @return negative of complex
	 */
	public Complex neg()
	{
		if (this.imag != 0)
			return new Complex(-1*this.real, -1*this.imag);
		else
			return new Complex(-1*this.real);
	}
	
	/**
	 * Return the given complex number multiplied by i
	 * @param complex number z
	 * @return
	 */
	public static Complex Iz(Complex z)
	{
		Complex result;
		if (z.imag() != 0.0)
			result = new Complex(-1.*z.imag(), z.real());
		else
			result = new Complex(z.imag(), z.real());
		
		return result;
	}
	
	/**
	 * Return the given complex number multiplied by -i
	 * @param complex number z
	 * @return
	 */
	public static Complex nIz(Complex z)
	{
		Complex result;
		if (z.real() != 0.0)
			result = new Complex(z.imag(), -1*z.real());
		else
			result = new Complex(z.imag(), z.real());
		
		return result;
	}
	
	@Override
	public double doubleValue() {
		// TODO Auto-generated method stub
		return Double.valueOf(this.real);
	}

	@Override
	public float floatValue() {
		// TODO Auto-generated method stub
		return Float.parseFloat(Double.toString(this.real));
	}

	@Override
	public int intValue() {
		// TODO Auto-generated method stub
		return Integer.parseInt(Double.toString(this.real));
	}

	@Override
	public long longValue() {
		// TODO Auto-generated method stub
		return Long.parseLong(Double.toString(this.real));
	}

	public void formatTo(Formatter arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		throw new NoSuchMethodError();

	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.valueOf(real) + " " + String.valueOf(imag) + "i";
	}
	
	private double real;
	private double imag;

}