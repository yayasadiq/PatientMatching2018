package optimisation.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

public class CSVWriter {
	private String filepath;
    private File file;
    private StringBuilder sb;
    
    public CSVWriter(String filepath) {
        this.filepath = filepath;
        this.file = new File(filepath);
        this.sb = new StringBuilder();
    }
    
    
    public void createCSVWithContent(StringBuilder sb) throws FileNotFoundException {
    	PrintWriter pw = new PrintWriter(file);
    	pw.write(sb.toString());
        pw.close();
    }
    
    public void addLignesToFile(StringBuilder sb) throws IOException {
    	if (file.exists()) {
			FileWriter fw = new FileWriter(this.filepath,true);
	    	fw.write(sb.toString());
	    	fw.close();
		} else {
			this.createCSVWithContent(sb);
		}
    	
    }
    
    public void writeHeaderWithControls(ArrayList<String> controls) {
    	StringBuilder sb = new StringBuilder();
        sb.append("id trial patient / id control patient").append(',');
        for (String string : controls) {
        	sb.append(string).append(',');			
		}
        sb.append('\n');
        try {
			this.createCSVWithContent(sb);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        System.out.println("Header created");
    }
    
    
	public void setFilepath(String filepath) {
		this.filepath = filepath;
		this.file = new File(filepath); 
	}
	
	public void writeCell(String cell) {
		this.sb.append(cell).append(',');
	}
	
	public void newLine() {
		this.sb.append('\n');
	}
	
	public void endFile() {
		try {
			this.createCSVWithContent(sb);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
