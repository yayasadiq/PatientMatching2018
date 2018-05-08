package utils.generator;

import java.io.FileNotFoundException;
import java.util.Random;

public class SimilarityGenerator extends Generator{

	private static final String TITLES = "Trial / Control";

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
    			csvWriter.writeCell(rand.nextDouble());
    		}
        	csvWriter.newLine();
		}

        csvWriter.createCSVWithContent();
        System.out.println("CVS generated");
	}

}
