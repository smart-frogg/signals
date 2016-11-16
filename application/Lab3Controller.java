package application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Lab3Controller {

	IIRFilters filter;
	private FileChooser fileChooser = new FileChooser();
	private String filePath = "C:\\WORK\\study\\Erlang\\furieFX\\";	
	// the main stage
	private Stage stage;
	double gc = 44100;
	@FXML
	Label fileNameLabel;
	SoundStream soundStream = null;
	
	public void setStage(Stage primaryStage) {
		stage = primaryStage;
	}	
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
			drawChart(true, steps, signal[0], signalChart);
		}
	}	
	
	private double steps[];
	private double signal[][];
	private double transformation[][];	
	protected void clearSignal(int size)
	{
		steps = new double[size];
		signal = new double[2][];
		signal[0] = new double[size];
		signal[1] = new double[size];
	}
	@FXML
	LineChart<Number,Number> signalChart;	
	@FXML
	LineChart<Number,Number> dotsFilterChart;
	@FXML
	LineChart<Number,Number> absFilterChart;
	@FXML
	LineChart<Number,Number> argFilterChart;
	@FXML
	TextField nField;
	@FXML
	TextField epsField;
	@FXML
	TextField w0Field;
	@FXML
	CheckBox isHighCheckBox;
	
	protected void createFilter()
	{
		int N = Integer.parseInt(nField.getText().toString());
		double eps = Double.parseDouble(epsField.getText().toString());
		double w0 = Double.parseDouble(w0Field.getText().toString());
		boolean isHigh = isHighCheckBox.isSelected();
		filter = new IIRFilters(N,eps,w0,!isHigh);		
	}
	@FXML
	protected void showButterworth()
	{
		createFilter();
		filter.dotsButterworth();
		showDots(dotsFilterChart);
		filter.dotsAbsKButterworth();
		showDots(absFilterChart);
		filter.dotsArgKButterworth();
		showDots(argFilterChart);
		filter.dotsArgCorrectKButterworth();
		drawChart(false,filter.dots,argFilterChart);
	}

	@FXML
	protected void doFilterButterworth() throws IOException
	{
		createFilter();
		double newSignal[] = filter.filterButterworth(signal[0], 44100);
		drawChart(false, steps, newSignal, signalChart);	
		saveSignal(newSignal,"Butterworth_low.wav");
	}
	protected void saveSignal(double[] signalToSave, String fileName) throws IOException
	{
		File file = new File(filePath+fileName);
		soundStream.saveSignal(signalToSave,file);
	}	
	@FXML
	protected void showChebyshevI()
	{
		createFilter();
		filter.dotsChebyshevTypeI();
		showDots(dotsFilterChart);
		filter.dotsAbsKChebyshevTypeI();
		showDots(absFilterChart);
		filter.dotsArgKChebyshevTypeI();
		showDots(argFilterChart);
		filter.dotsArgCorrectKChebyshevTypeI();
		drawChart(false,filter.dots,argFilterChart);
	}

	@FXML
	protected void showChebyshevII()
	{
		createFilter();
		filter.dotsChebyshevTypeII();
		showDots(dotsFilterChart);
		absFilterChart.getData().clear();
		argFilterChart.getData().clear();
	}
	protected void showDots(XYChart lineChart)
	{
		drawChart(true,filter.dots,lineChart);
	}	
	protected void showFilter()
	{
		/*createFilter();
		double testSignal[] = new double[10000];
		testSignal[0] = 1000;
		for(int i=1;i<testSignal.length;i++)
			testSignal[i] = 0;
		double newSignal[][] = filter.filter(testSignal);
		filter.transform(newSignal);
		drawChart(true,filter.getAmplitude(),amplitudeFilterChart);
		drawChart(true,filter.getLogAmplitude(),logAmplitudeFilterChart);
		drawChart(true,filter.getSignal(),phaseFilterChart);*/
	}
	private void drawChart(boolean clear, double signal[][], XYChart lineChart)
	{
        XYChart.Series<Number,Number> series = new XYChart.Series<Number,Number>();
        int count = signal[0].length < 3000? signal[0].length : 3000;
        for (int i=0;i<count;i++)
        {
    		series.getData().add(new XYChart.Data<Number,Number>(signal[0][i],signal[1][i]));
    		if(Double.isNaN(signal[1][i]))
    			System.out.println(i);
        }
        if(clear) lineChart.getData().clear();
        lineChart.getData().add(series);		
	}
	private void drawChart(boolean clear, double steps[], double signal[], XYChart lineChart)
	{
        XYChart.Series<Number,Number> series = new XYChart.Series<Number,Number>();
        series.setName("Signal");
        double avg = 0;
        int points = signal.length; 
        int k = points > 10000 ? 1000:1;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        
        for (int i=0;i<points;i++)
        {
        	if (k > 1)
        	{
        		avg+=signal[i]/1000;
        		if(signal[i]>max)
        			max = signal[i];
        		if(signal[i]<min)
        			min = signal[i];
        	
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
        		series.getData().add(new XYChart.Data<Number,Number>(steps[i],signal[i]));
        }
        if(clear) lineChart.getData().clear();
        lineChart.getData().add(series);		
	}
}
