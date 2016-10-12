package application;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Lab2Controller {

	// the JavaFX file chooser
	private FileChooser fileChooser = new FileChooser();
	private String filePath = "C:\\WORK\\study\\Erlang\\furieFX\\";	
	// the main stage
	private Stage stage;

	private FurieModel model = FurieModel.getInstance();
	
	double gc = 44100;

	SoundStream soundStream = null;
	
	@FXML
	protected void loadSignal() throws IOException
	{
		File file = new File(filePath);
		this.fileChooser.setInitialDirectory(file);
		// show the open dialog window
		file = this.fileChooser.showOpenDialog(this.stage);
		if (file != null)
		{
			fileNameLabel.setText(file.getName());
			soundStream = new SoundStream();
			
			ArrayList<Double> readSignal = soundStream.loadSignal(file);
			clearSignal(readSignal.size());
			for(int i=0;i<readSignal.size();i++)
			{
				steps[i] = i/gc;
				signal[0][i] = readSignal.get(i);
				signal[1][i] = 0;
			}
			drawChart(true, signal, signalChart);
		}
	}
	
	protected void saveSignal(double[][] signalToSave, String fileName) throws IOException
	{
		File file = new File(filePath+fileName);
		soundStream.saveSignal(signalToSave[0],file);
	}

	private double steps[];
	private double signal[][];
	private double transformation[][];
	
	@FXML
	LineChart<Number,Number> signalChart;
	@FXML
	LineChart<Number,Number> phaseFourierChart;
	@FXML
	LineChart<Number,Number> amplitudeFourierChart;
	@FXML
	LineChart<Number,Number> logAmplitudeFourierChart;
	@FXML
	LineChart<Number,Number> phaseFilterChart;
	@FXML
	LineChart<Number,Number> amplitudeFilterChart;
	@FXML
	LineChart<Number,Number> logAmplitudeFilterChart;
	@FXML
	LineChart<Number,Number> idealChart;
	@FXML
	Label fileNameLabel;

	@FXML
	TextField nField;
	@FXML
	TextField fField;
	@FXML
	TextField cropField;
	@FXML
	ComboBox<String> windowChoiceBox;
	@FXML
	CheckBox highCheckBox;
	
	private double A = 1000; // Амплитуда
	private double T = 0.01; // Период
	private int N = 131072/(10); // Количество точек в периоде
	private int periods = 10; // Количество периодов

	private final double EPSILON = 0.00000001;

	@FXML
	protected void genSawSignal()
	{
		clearSignal(N*periods);
		double step = (T)/N;
		double s = 0;
		double dA = 2*A/T;
		int i = 0;
		for (int p=0;p<periods;p++)
		{
			double curS = 0;
			for(int t = 0; t < N;t++)
			{
				steps[i] = s;
				if (curS < T/2-step+EPSILON)
					signal[0][i] = dA*curS;
				else if (curS > T/2+step-EPSILON)
					signal[0][i] = dA*curS-2*A;
				else
					signal[0][i] = -A;
				signal[1][i] = 0;
				s += step;
				curS += step;
				i++;
			}
		}
		drawChart(true, signal, signalChart);
		fileNameLabel.setText("Saw signal");
	}
	
	Filter filter = null;
	protected void createFilter()
	{
		int N = Integer.parseInt(nField.getText().toString());
		double fc = Double.parseDouble(fField.getText().toString());
		String window = (String) windowChoiceBox.getValue();
		filter = new Filter(N, 2*fc/gc, highCheckBox.isSelected());
		if (window == null)
			window = "Rectangular";
		switch (window)
		{
			case "Bartlett": filter.genWBartlett(); break;
			case "Hanning": filter.genWHanning(); break;
			case "Hamming": filter.genWHamming(); break;
			case "Blackman": filter.genWBlackman(); break;
			case "Rectangular": 
			default: filter.genWRectangular(); break;
		}		
	}
	@FXML
	protected void showFilter()
	{
		createFilter();
		double testSignal[] = new double[10000];
		testSignal[0] = 1000;
		for(int i=1;i<testSignal.length;i++)
			testSignal[i] = 0;
		double newSignal[][] = filter.filter(testSignal);
		filter.transform(newSignal);
		drawChart(true,filter.getAmplitude(),amplitudeFilterChart);
		drawChart(true,filter.getLogAmplitude(),logAmplitudeFilterChart);
		drawChart(true,filter.getSignal(),phaseFilterChart);
	}
	
	@FXML
	protected void applyToSound() throws IOException
	{
		createFilter();
		double newSignal[][] = filter.filter(signal[0]);
		drawChart(false,newSignal,signalChart);
		String window = (String) windowChoiceBox.getValue();
		if (window == null)
			window = "Rectangular";
		if (!highCheckBox.isSelected())
			saveSignal(newSignal, "high_"+window+".wav");
		else
			saveSignal(newSignal, "low_"+window+".wav");
	}
	
	protected void clearSignal(int size)
	{
		steps = new double[size];
		signal = new double[2][];
		signal[0] = new double[size];
		signal[1] = new double[size];
	}

	private void drawChart(boolean clear, double signal[], XYChart lineChart)
	{
        XYChart.Series<Number,Number> series = new XYChart.Series<Number,Number>();
        series.setName("Signal");
        int count = signal.length < 3000? signal.length : 3000;
        for (int i=0;i<count;i++)
        {
    		series.getData().add(new XYChart.Data<Number,Number>(i,signal[i]));
        }
        if(clear) lineChart.getData().clear();
        lineChart.getData().add(series);		
	}
	
	private void drawChart(boolean clear, double signal[][], XYChart lineChart)
	{
        XYChart.Series<Number,Number> series = new XYChart.Series<Number,Number>();
        series.setName("Signal");
        double avg = 0;
        int points = signal[0].length; 
        int k = points > 10000 ? 1000:1;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        
        for (int i=0;i<points;i++)
        {
        	if (k > 1)
        	{
        		avg+=signal[0][i]/1000;
        		if(signal[0][i]>max)
        			max = signal[0][i];
        		if(signal[0][i]<min)
        			min = signal[0][i];
        	
        		if ((i-1)%k==0)
        		{
        			series.getData().add(new XYChart.Data<Number,Number>(steps[i],max));
        			series.getData().add(new XYChart.Data<Number,Number>(steps[i],min));
        			avg = 0;
        			max = Double.MIN_VALUE;
        			min = Double.MAX_VALUE;
        		}
        	}
        	else
        		series.getData().add(new XYChart.Data<Number,Number>(steps[i],signal[0][i]));
        }
        if(clear) lineChart.getData().clear();
        lineChart.getData().add(series);		
	}

	public void setStage(Stage primaryStage) {
		stage = primaryStage;
	}
	
	@FXML
	protected void crop()
	{
		int size = 1<<Integer.parseInt(cropField.getText());
		signal[0] = Arrays.copyOf(signal[0], size);
		signal[1] = Arrays.copyOf(signal[1], size);
		drawChart(true, signal, signalChart);		
	}
	
	@FXML
	protected void doFilterLow() throws IOException
	{
		transformation = model.fastFurieNTransform(signal, false);
		calcAmplitudeAndPhase(transformation);
		drawChart(true, amplitudeSpector, idealChart);			
		int topBorder = 0;
		int bottomBorder = 1000;
		int pointsCount = signal[0].length;
		double max = steps[pointsCount-1];
		double newSignal[][] = model.filter(signal, topBorder*pointsCount/gc, bottomBorder*pointsCount/gc, false);
		drawChart(false, newSignal, signalChart);	
		saveSignal(newSignal,"fourier_low.wav");
		calcAmplitudeAndPhase(model.result);
		drawChart(false, amplitudeSpector, idealChart);			
	}
	
	@FXML
	protected void doFilterHigh() throws IOException
	{
		transformation = model.fastFurieNTransform(signal, false);
		calcAmplitudeAndPhase(transformation);
		drawChart(true, amplitudeSpector, idealChart);			
		int topBorder = 0;
		int bottomBorder = 20000;
		int pointsCount = signal[0].length;
		double max = steps[pointsCount-1];
		double newSignal[][] = model.filter(signal, topBorder*pointsCount/gc, bottomBorder*pointsCount/gc, true);
		drawChart(false, newSignal, signalChart);		
		saveSignal(newSignal,"fourier_high.wav");
		calcAmplitudeAndPhase(model.result);
		drawChart(false, amplitudeSpector, idealChart);			
	}
	
	@FXML
	protected void restoreSignal()
	{
		drawChart(true, signal, signalChart);		
	}
	
	protected void calcAmplitudeAndPhase(double transformation[][])
	{
		int pointsCount = signal[0].length;
		amplitudeSpector = new double[pointsCount];
		phaseSpector = new double[pointsCount];
		for (int k=0; k<pointsCount; k++)
		{
			amplitudeSpector[k] = Math.sqrt(transformation[0][k]*transformation[0][k]+transformation[1][k]*transformation[1][k]);
			phaseSpector[k] = Math.atan(transformation[1][k]/transformation[0][k]);
		}
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

