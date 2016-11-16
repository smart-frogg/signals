package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Lab4Controller {
	// the JavaFX file chooser
	private FileChooser fileChooser = new FileChooser();
	private String filePath = "C:\\WORK\\study\\Erlang\\furieFX\\";	
	// the main stage
	private Stage stage;

	private String OYLabel = "";
	/**
	 * Load an signal from disk
	 * @throws FileNotFoundException 
	 */
	@FXML
	protected void loadSignal() throws FileNotFoundException
	{
		File file = new File(filePath);
		this.fileChooser.setInitialDirectory(file);
		// show the open dialog window
		file = this.fileChooser.showOpenDialog(this.stage);
		if (file != null)
		{
			Scanner scanner = new Scanner(file);
			ArrayList<Double> readSignal = new ArrayList<Double>();
			double stepX = 1.0/scanner.nextInt();
			int zero = scanner.nextInt();
			double stepY = 1.0/scanner.nextInt();
			scanner.nextLine();
			String OYLabel = scanner.nextLine();
			while (scanner.hasNext())
			{
				String str = scanner.next();
				if(str.isEmpty())
					break;
				Double d = new Double((Integer.parseInt(str)-zero)*stepY);
				readSignal.add(d);
			}
			clearSignal(readSignal.size());
			this.OYLabel = OYLabel;
			for(int i=0;i<readSignal.size();i++)
			{
				steps[i] = stepX*i;
				signal[i] = readSignal.get(i);
			}
				
			drawChart(true, signal, lineChart);
		}
	}
	
	private double steps[];
	private double signal[];
	private double transformation[];
	
	@FXML
	LineChart<Number,Number> lineChart;
	@FXML
	LineChart<Number,Number> approxChart;
	@FXML
	LineChart<Number,Number> amplitudeChart;
	@FXML
	LineChart<Number,Number> phaseChart;
	@FXML
	LineChart<Number,Number> leftChart;
	@FXML
	CheckBox isInsideChecker;
	
	@FXML
	TextField amplitudeField;
	@FXML
	TextField periodField;
	@FXML
	TextField tauField;
	@FXML
	TextField pointsCountField;
	@FXML
	TextField periodsCountField;
	@FXML
	TextField topBorderField;
	@FXML
	TextField bottomBorderField;
	@FXML
	TextField garmoniqueFiled;
	@FXML
	Label message;
	@FXML
	Label time;
	
	private double A = 1; // Амплитуда
	private double T = 10; // Период
	private double tau = 4; // Длина нуля
	private int N = 20; // Количество точек в периоде
	private int periods = 3; // Количество периодов

	private final double EPSILON = 0.00000001;
	@FXML
	protected void genSawSignal()
	{
		readParams();
		clearSignal(N*periods);
		double step = (T)/N;
		double s = 0;
		double dA = 2*A/T;
		int i = 0;
		for (int p=0;p<periods;p++)
		{
			double a = 0;
			double curS = 0;
			for(int t = 0; t < N;t++)
			{
				steps[i] = s;
				if (curS < T/2-step+EPSILON)
					signal[i] = dA*curS;
				else if (curS > T/2+step-EPSILON)
					signal[i] = dA*curS-2*A;
				else
					signal[i] = -A;
				s += step;
				curS += step;
				i++;
			}
		}
		drawChart(true, signal, lineChart);
	}

	protected void clearSignal(int size)
	{
		steps = new double[size];
		signal = new double[size];
		OYLabel="";
	}
	
	@FXML
	protected void genTriangularSignal()
	{
		readParams();
		clearSignal(N*periods);
		double step = (T)/N;
		double s = 0;
		double dA = 4*A/(T);
		int i = 0;
		for (int p=0;p<periods;p++)
		{
			double curS = 0;
			for(int t = 0; t < N;t++)
			{
				steps[i] = s;
				//int delta = (int)(s/T);
				if (curS < T/2+EPSILON)
					signal[i] = -dA*curS+A;
				else 
					signal[i] = dA*curS-3*A;
				s += step;
				curS += step;
				i++;
			}
		}
		drawChart(true, signal, lineChart);
	}
	
	@FXML
	protected void genRectangularSignal()
	{
		readParams();
		clearSignal(N*periods);
		double step = (T)/N;
		double s = -T/2;
		int i=0;
		for (int p=0;p<periods;p++)
		{
			double curS = 0;
			for(int t = 0; t < N;t++)
			{
				steps[i] = s;
				signal[i] = (curS < (T-tau)/2 || curS > (T+tau)/2) ? 0 : A;
				s += step;
				curS += step;
				i++;
			}
		}
		drawChart(true, signal, lineChart);
	}
	
	private void readParams()
	{
		A = Double.parseDouble(amplitudeField.getText());
		T = Double.parseDouble(periodField.getText());
		tau = Double.parseDouble(tauField.getText());
		N = Integer.parseInt(pointsCountField.getText());
		periods = Integer.parseInt(periodsCountField.getText());
	}
	
	private void drawChart(boolean clear, double signal[], XYChart lineChart)
	{
        XYChart.Series<Number,Number> series = new XYChart.Series<Number,Number>();
        series.setName("Signal");
        for (int i=0;i<signal.length;i++)
        {
        	series.getData().add(new XYChart.Data<Number,Number>(steps[i],signal[i]));
        }
        if(clear) lineChart.getData().clear();
        lineChart.getData().add(series);		
        if(!OYLabel.isEmpty())
        	lineChart.getYAxis().setLabel(OYLabel);
	}

	public void setStage(Stage primaryStage) {
		stage = primaryStage;
	}
	
	@FXML
	protected void doFilter()
	{
		int pointsCount = signal.length;
		int topBorder = Integer.parseInt(topBorderField.getText());;
		int bottomBorder = Integer.parseInt(bottomBorderField.getText());;
		transformation = RectTransform.discretUolshTransform(signal,false);
		calcAmplitudeAndPhase();
		double max = steps[pointsCount-1];
		boolean isInside = isInsideChecker.isSelected();
		double result[] = Arrays.copyOf(transformation, transformation.length);
		for (int k=0; k<pointsCount; k++)
			if(isInside)
			{
				if(k>topBorder/max && k<bottomBorder/max || k<pointsCount-topBorder/max && k>pointsCount-bottomBorder/max)
					result[k] = 0; 
			}	
			else
			{
				if(!(k>topBorder/max && k<bottomBorder/max || k<pointsCount-topBorder/max && k>pointsCount-bottomBorder/max))
					result[k] = 0; 
				
			}

		double newSignal[] =  RectTransform.discretUolshTransform(result,true);
		drawChart(true, newSignal, leftChart);		
	}


	@FXML
	protected void doUolshApproximate()
	{
		int pointsCount = signal.length;
		int garmCount = Integer.parseInt(garmoniqueFiled.getText());;
		transformation =  RectTransform.discretUolshTransform(signal,false);
		calcAmplitudeAndPhase();
		for (int k=garmCount; k<pointsCount; k++)
			transformation[k]=0;
		double newSignal[] =  RectTransform.discretUolshTransform(transformation,true);
		drawChart(true, newSignal, approxChart);		
	}
	@FXML
	protected void doAdamarApproximate()
	{
		int pointsCount = signal.length;
		int garmCount = Integer.parseInt(garmoniqueFiled.getText());;
		transformation =  RectTransform.discretAdamarTransform(signal,false);
		calcAmplitudeAndPhase();
		for (int k=garmCount; k<pointsCount; k++)
			transformation[k]=0;
		double newSignal[] =  RectTransform.discretAdamarTransform(transformation,true);
		drawChart(true, newSignal, approxChart);		
	}	
	

	protected void calcAmplitudeAndPhase()
	{
		int pointsCount = signal.length;
		amplitudeSpector = new double[pointsCount/2];
		phaseSpector = new double[pointsCount/2];
		for (int k=1; k<pointsCount/2; k++)
		{
			amplitudeSpector[k-1] = Math.sqrt(transformation[k]*transformation[k]+transformation[k+1]*transformation[k+1]);
			phaseSpector[k-1] = Math.atan2(transformation[k],transformation[k+1]);
		}
		
	}
	
	@FXML
	public void doDiscretUolshTransform()
	{
		transformation = RectTransform.discretUolshTransform(signal,false);
		calcAmplitudeAndPhase();
		showTransformation(amplitudeSpector,amplitudeChart);
		showTransformation(phaseSpector,phaseChart);
		double newSignal[] =  RectTransform.discretUolshTransform(transformation,true);
		drawChart(false, newSignal, lineChart);
	}
	
	@FXML
	public void doDiscretAdamarTransform()
	{
		transformation = RectTransform.discretAdamarTransform(signal,false);
		calcAmplitudeAndPhase();
		showTransformation(amplitudeSpector,amplitudeChart);
		showTransformation(phaseSpector,phaseChart);
		double newSignal[] =  RectTransform.discretAdamarTransform(transformation,true);
		drawChart(false, newSignal, lineChart);
	}
	
	
	private double amplitudeSpector[];
	private double phaseSpector[];
	
	private void showTransformation(double array[], LineChart<Number,Number> chart)
	{
		XYChart.Series<Number,Number> series = new XYChart.Series<Number,Number>();
        series.setName("Amplitude");
        double max = steps[array.length-1];
        for (int i=0;i<array.length/2;i++)
        {
        	series.getData().add(new XYChart.Data<Number,Number>(i/max, array[i]));
        }
        chart.getData().clear();
        chart.getData().add(series);
        chart.getStyleClass().add("thick-chart");
	}
	
}
