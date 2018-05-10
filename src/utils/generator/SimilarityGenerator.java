package utils.generator;

import java.io.FileNotFoundException;
import java.util.Random;

public class SimilarityGenerator extends Generator{

	private static final double MAX_VALUE = 0.5;
	private static final String TITLES = "Trial / Control";
	private static final int[] COEFFICIENT = {1,10,20, 20, 30, 30, 20, 20, 10, 1};
	private static final double[] MIN_BOUND = {0, 0.1, 0.2, 0.3, 0.4, 0.5,0.6,0.7,0.8 ,0.9};

	public SimilarityGenerator(String filepath) {
		super(filepath);
	}

	@Override
	public void makeData(int nbrLines) throws FileNotFoundException {
		makeSquareMatrix(nbrLines, nbrLines);
		
	}

	public void makeSquareMatrix(int nbrLines, int nbrColumns) throws FileNotFoundException {
		Random rand = new Random();
        int cursor = 2;
        csvWriter.writeCell(TITLES);
        for (int i = 0; i < nbrColumns; i++) {
			csvWriter.writeCell(i);
		}
        csvWriter.newLine();
        for (int i = 0; i < nbrLines ; i++) {
        	csvWriter.writeCell(i);
        	for (int j = 0; j < nbrColumns; j++) {
    			csvWriter.writeCell(rand.nextDouble() * MAX_VALUE);
    		}
        	csvWriter.newLine();
		}

        csvWriter.createCSVWithContent();
        System.out.println("CVS generated");
	}

	private double chooseRandomWeightNumber(Random rand) {
		int nbrOfCoefficient = COEFFICIENT.length;
		int maxBound = 0;
		for (int i = 0; i < nbrOfCoefficient; i++) {
			maxBound += COEFFICIENT[i];
		}
		int choosenNumber = rand.nextInt(maxBound);
		int counter = 0;
		while(choosenNumber >= 0) {
			choosenNumber -= COEFFICIENT[counter];
			counter++;
		}
		double rangeMin = MIN_BOUND[counter - 1];
		double rangeMax = rangeMin + 0.1;
		return rangeMin + (rangeMax - rangeMin) * rand.nextDouble();
	}

}
