package utils.generator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.MockPatient;

public class MockPatientGenerator extends Generator {
    private static final int NBR_OF_CHANGES_PER_LINE = 10;
	private final static String TITLES = "Nombre,d'attributs,:,"+ MockPatient.NUMBER_OF_ATTRIBUTS + ",\n";
	private List<List<Double>> generatedValues;
	public static final int NBR_OF_LINES_TO_GENERATED = 50;
	private Random rand;

	public MockPatientGenerator(String filepath) {
		super(filepath);
		this.generatedValues  = new ArrayList<>();
		this.rand = new Random();
		int cursor = 2;
		for (int i = 0; i < NBR_OF_LINES_TO_GENERATED; i++) {
			List<Double> line = new ArrayList<>();
			int numberOfAttributs = MockPatient.NUMBER_OF_ATTRIBUTS;
			for (int j = 0; j < numberOfAttributs; j++) {
				line.add(generateAttributs());
			}
			cursor = 2;
			generatedValues.add(line);
		}
	}
	
	public void makeData(int nbrLines) throws IOException {
        int curNbrLine = 0;
		csvWriter.addLines(TITLES);
		csvWriter.createCSVWithContent();
		while (curNbrLine < nbrLines) {
			List<Double> line = generatedValues.get(rand.nextInt(NBR_OF_LINES_TO_GENERATED));
			for (int i = 0; i < NBR_OF_CHANGES_PER_LINE; i++) {
				int indice = rand.nextInt(MockPatient.NUMBER_OF_ATTRIBUTS - 1) + 1;
				line.set(indice, generateAttributs());
			}
			csvWriter.writeCell(curNbrLine);
			for (Double attribut : line) {
				csvWriter.writeCell(attribut);
			}
			csvWriter.newLine();
			curNbrLine ++;
			csvWriter.saveData();
		}
        System.out.println("CVS generated");
    }

	private double generateAttributs() {
		return rand.nextDouble()*100;
	}


}
