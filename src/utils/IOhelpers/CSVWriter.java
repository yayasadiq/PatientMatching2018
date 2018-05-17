package utils.IOhelpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
    
    
    public void createCSVWithContent() throws IOException {
    	PrintWriter pw = new PrintWriter(file);
    	pw.write(sb.toString());
        pw.close();
        sb = new StringBuilder(); 
    }
       
    
	public void setFilepath(String filepath) {
		this.filepath = filepath;
		this.file = new File(filepath); 
	}
	
	public <T> void writeCell(T cell) {
		this.sb.append(String.valueOf(cell)).append(',');
	}
	
	public void newLine() {
		this.sb.append('\n');
	}
	
	public void saveData() throws IOException {
		if (file.exists()) {
	    	FileWriter fw = new FileWriter(file, true);
			fw.write(sb.toString());
	    	fw.close();
		} else {
			this.createCSVWithContent();
		}
		sb = new StringBuilder();
	}
	
	public void addLines(String string) {
		sb.append(string);
	}
	
	@Override
	public String toString() {
		return sb.toString();
	}

}
