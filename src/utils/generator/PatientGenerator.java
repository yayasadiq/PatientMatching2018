package utils.generator;

import java.io.FileNotFoundException;
import java.util.Random;

public class PatientGenerator extends Generator {
    private final static String TITLES = "ID,age_yrs,bts_1,bts_2,bts_3,bts_4,bts_5,bts_6,bts_7,bts_8,bts_9,bts_10,bts_11,bts_12,control1,control2,control3,control4,control5,control6,control7,control8,control9,control10,control11,control12,drug01_1,drug01_2,drug01_3,drug01_4,drug01_5,drug01_6,drug01_7,drug01_8,drug01_9,drug01_10,drug01_11,drug01_12,drug02_1,drug02_2,drug02_3,drug02_4,drug02_5,drug02_6,drug02_7,drug02_8,drug02_9,drug02_10,drug02_11,drug02_12,drug03_1,drug03_2,drug03_3,drug03_4,drug03_5,drug03_6,drug03_7,drug03_8,drug03_9,drug03_10,drug03_11,drug03_12,drug04_1,drug04_2,drug04_3,drug04_4,drug04_5,drug04_6,drug04_7,drug04_8,drug04_9,drug04_10,drug04_11,drug04_12,drug05_1,drug05_2,drug05_3,drug05_4,drug05_5,drug05_6,drug05_7,drug05_8,drug05_9,drug05_10,drug05_11,drug05_12,drug06_1,drug06_2,drug06_3,drug06_4,drug06_5,drug06_6,drug06_7,drug06_8,drug06_9,drug06_10,drug06_11,drug06_12,drug07_1,drug07_2,drug07_3,drug07_4,drug07_5,drug07_6,drug07_7,drug07_8,drug07_9,drug07_10,drug07_11,drug07_12,drug08_1,drug08_2,drug08_3,drug08_4,drug08_5,drug08_6,drug08_7,drug08_8,drug08_9,drug08_10,drug08_11,drug08_12,drug09_1,drug09_2,drug09_3,drug09_4,drug09_5,drug09_6,drug09_7,drug09_8,drug09_9,drug09_10,drug09_11,drug09_12,drug10_1,drug10_2,drug10_3,drug10_4,drug10_5,drug10_6,drug10_7,drug10_8,drug10_9,drug10_10,drug10_11,drug10_12,drug11_1,drug11_2,drug11_3,drug11_4,drug11_5,drug11_6,drug11_7,drug11_8,drug11_9,drug11_10,drug11_11,drug11_12,asthma_exacerbation,admiss_exacerbation,smr01_exacerbation,ae_exacerbation,poor_saba,ocs_emergency,saba_daily_doses\n";

	public PatientGenerator(String filepath) {
		super(filepath);
	}
	
	public void makeData(int nbrLines) throws FileNotFoundException {
        Random rand = new Random();
        int cursor = 2;
        csvWriter.addLines(TITLES);
        for (int i = 0; i < nbrLines; i++) {
			csvWriter.writeCell(i);
			csvWriter.writeCell(rand.nextInt(50));
			while(cursor < 15) {
				csvWriter.writeCell(rand.nextInt(5) + 1);
				cursor = cursor + 1;
			}
			while(cursor < 27) {
				csvWriter.writeCell(rand.nextInt(3) + 1);
				cursor = cursor + 1;
			}
			while(cursor < 164) {
				csvWriter.writeCell(rand.nextInt(2));
				cursor = cursor + 1;
			}
			csvWriter.writeCell(rand.nextInt(1));
			csvWriter.newLine();
			cursor = 2;
		}
        csvWriter.createCSVWithContent();
        System.out.println("CVS generated");
    }

	
}
