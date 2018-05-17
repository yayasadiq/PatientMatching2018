package utils.generator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import utils.IOhelpers.CSVWriter;
import utils.timer.TimeMeasurer;

public class SimilarityGenerator extends Generator{
	private static final int NBR_OF_GENERATED_COLUMNS = 100;
	private static final String TITLES = "Trial / Control";

	public SimilarityGenerator(String filepath) {
		super(filepath);
	}

	@Override
	public void makeData(int nbrLines) throws IOException {
		makeRectangularMatrix(nbrLines, nbrLines);
		
	}

	public void makeRectangularMatrix(int nbrLines, int nbrColumns) throws IOException{
		int curNbrOfColumns = 100;
		TimeMeasurer measurer = new TimeMeasurer();
		List<Double> list = new LinkedList<>();
		measurer.startTimer("Generating");
		Random rand = new Random();
		double functionResult = Math.exp(-1/Math.log(100));
		List<StringBuilder> rows = new ArrayList<>();
		List<List<Double>> base = new ArrayList<>();
		
		rows.add(new StringBuilder());
		StringBuilder header = rows.get(0);
		header.append(TITLES).append(',');
		for (int i = 2; i <= nbrLines; i++) {
			rows.add(new StringBuilder(String.valueOf(i) + ","));
		}
		
		for (int j = 0; j < NBR_OF_GENERATED_COLUMNS ; j++) {
			header.append(String.valueOf(j)).append(',');
			base.add(new ArrayList<>());
			for (int i = 1 ; i < nbrLines; i++) {
				double generatedSim = generateSim(rand, functionResult);
				rows.get(i).append(String.valueOf(generatedSim)).append(',');
				base.get(j).add(generatedSim);
			}
		}
		while (curNbrOfColumns < nbrColumns) {
			List<Double> column = base.get(rand.nextInt(NBR_OF_GENERATED_COLUMNS-1));		
			for (int i = 1 ; i < nbrLines-1; i++) {
				Double sim = column.get(i);
				if (rand.nextBoolean()) {
					rows.get(i).append(sim - (rand.nextDouble() * sim)/10).append(',');					
				} else {
					rows.get(i).append(sim + (rand.nextDouble() * (1-sim))/10).append(',');										
				}
			}
			curNbrOfColumns++;
		}
		
		for (StringBuilder sb : rows) {
			sb.append('\n');
			csvWriter.addLines(sb.toString());
		}
		csvWriter.createCSVWithContent();
		csvWriter.saveData();
		measurer.stopTimer();
		System.out.println("Generating data duration : " + measurer.getLastTime() + "s");
	}
	
	private double generateSim(Random rand, double functionResult) {
		double minBound = Math.pow(functionResult, rand.nextInt(100) + 1);
		return minBound + ((1-minBound)/ 5) * rand.nextDouble();
	}

}
