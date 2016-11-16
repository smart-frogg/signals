package application;

public class Adamar {
	int N;
	int W[][];
	public void printMatrix()
	{
		for(int u=0;u<N;u++)
		{
			for(int v=0;v<N;v++)
				System.out.print(W[u][v]+"\t");
			System.out.println();
		}
					
	}
	public Adamar(int N, boolean isUolsh)
	{
		this.N = N;
		if (isUolsh)
			genUolsh();
		else
			genAdamar();
	}
	protected static int reverseBit(int x, int n)
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
	private static int codeGray(int u, int n)
	{
		int R[] = new int[n];
		int ut = u;
		int sr = 1 << (n-1);
		R[0] = (ut & sr) != 0? 1:0;
		for(int i=1;i<n;i++)
		{
			R[i] = (ut & sr) != 0? 1:0;
			sr>>=1;
			R[i] += (ut & sr) != 0? 1:0;
			R[i] %= 2;
		}
		int res = 0;
		for(int i=0;i<n;i++)
		{
			res<<=1;
			res+=R[i];
		}
		return res;
	}
	private static int decodeGray(int gray) 
	{
	    int bin;
	    for (bin = 0; gray > 0; gray >>= 1) {
	      bin ^= gray;
	    }
	    return bin;
	}
	
	private void genAdamar()
	{
		W = new int[N][];
		int n = (int)(Math.log(N)/Math.log(2));
		int R[] = new int[n];
		for(int u=0;u<N;u++)
		{
			W[u] = new int[N];
			int ut = decodeGray(reverseBit(u,n));
			int sr = 1 << (n-1);
			R[0] = (ut & sr) != 0? 1:0;
			System.out.print(R[0]+"\t");
			for(int i=1;i<n;i++)
			{
				R[i] = (ut & sr) != 0? 1:0;
				sr>>=1;
				R[i] += (ut & sr) != 0? 1:0;
				R[i] %= 2;
			}				
			for(int v=0;v<N;v++)
			{
				int vt = v;
				int sum = 0;
				for(int i=0;i<n;i++)
				{
					sum += R[i] * (vt&1);
					vt >>=1;
					
				}
				W[u][v] = (sum%2) == 0 ? 1 : -1;
			}
		}
	}
	
	private void genUolsh()
	{
		W = new int[N][];
		int n = (int)(Math.log(N)/Math.log(2));
		int R[] = new int[n];
		for(int u=0;u<N;u++)
		{
			W[u] = new int[N];
			int ut = u;
			int sr = 1 << (n-1);
			R[0] = (ut & sr) != 0? 1:0;
			for(int i=1;i<n;i++)
			{
				R[i] = (ut & sr) != 0? 1:0;
				sr>>=1;
				R[i] += (ut & sr) != 0? 1:0;
				R[i] %= 2;
			}				
			for(int v=0;v<N;v++)
			{
				int vt = v;
				int sum = 0;
				for(int i=0;i<n;i++)
				{
					sum += R[i] * (vt&1);
					vt >>=1;
					
				}
				W[u][v] = (sum%2) == 0 ? 1 : -1;
			}
		}
	}
	public static int getUolsh(int N, int u, int v)
	{
		int n = (int)(Math.log(N)/Math.log(2));
		int R[] = new int[n];
		int ut = u;
		int sr = 1 << (n-1);
		R[0] = (ut & sr) != 0? 1:0;
		for(int i=1;i<n;i++)
		{
			R[i] = (ut & sr) != 0? 1:0;
			sr>>=1;
			R[i] += (ut & sr) != 0? 1:0;
			R[i] %= 2;
		}				
		int vt = v;
		int sum = 0;
		for(int i=0;i<n;i++)
		{
			sum += R[i] * (vt&1);
			vt >>=1;
		}
		return (sum%2) == 0 ? 1 : -1;
	}
	public static int getAdamar(int N, int u, int v)
	{
		int n = (int)(Math.log(N)/Math.log(2));
		int ut = decodeGray(reverseBit(u,n));
		int sr = 1 << (n-1);
		int R[] = new int[n];
		R[0] = (ut & sr) != 0? 1:0;
		for(int i=1;i<n;i++)
		{
			R[i] = (ut & sr) != 0? 1:0;
			sr>>=1;
			R[i] += (ut & sr) != 0? 1:0;
			R[i] %= 2;
		}				
		int vt = v;
		int sum = 0;
		for(int i=0;i<n;i++)
		{
			sum += R[i] * (vt&1);
			vt >>=1;
			
		}
		return (sum%2) == 0 ? 1 : -1;
	}
}
