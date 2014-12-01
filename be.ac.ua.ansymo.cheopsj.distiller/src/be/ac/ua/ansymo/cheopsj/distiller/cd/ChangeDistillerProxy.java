package be.ac.ua.ansymo.cheopsj.distiller.cd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;

public class ChangeDistillerProxy {
	private final String LEFT_FILE_NAME = "__LEFT_FILE.java";
	private final String RIGHT_FILE_NAME = "__RIGHT_FILE.java";
	
	/**
	 * The actual implementation from Fluri et. al
	 */
	private final FileDistiller changeDistiller;
	private File oldFile;
	private File newFile;

	public ChangeDistillerProxy() throws Exception {
		changeDistiller = ChangeDistiller.createFileDistiller(ChangeDistiller.Language.JAVA);
	}
	
	public ChangeDistillerProxy(String copyOldFileContents,String copyNewFileContents) throws Exception {
		changeDistiller = ChangeDistiller.createFileDistiller(ChangeDistiller.Language.JAVA);
		this.copyNewFileFrom(copyNewFileContents);
		this.copyOldFileFrom(copyOldFileContents);
	}
	
	public void copyOldFileFrom(String sourceFileContents) throws Exception {		
		oldFile = mapToFile(sourceFileContents, LEFT_FILE_NAME);
		
	}

	public void copyNewFileFrom(String sourceFileContents) throws Exception {
		newFile = mapToFile(sourceFileContents, RIGHT_FILE_NAME);
	}

	public void performDistilling() {
		try{
			changeDistiller.extractClassifiedSourceCodeChanges(oldFile, newFile);
		}catch(Exception e){
			System.err.println("Warning: error while change distilling. " + e.getMessage());
		}
	}
	
	private File mapToFile(String content, String fileName) throws Exception
	{
		File file = new File(fileName);
		if (file.exists()) {
			file.delete();
		}
		
		BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
		out.write(content);
		out.close();

		return file;
	}	
	
	public void printChanges() {
        for (SourceCodeChange scc : changeDistiller.getSourceCodeChanges()) {
            //System.out.println(scc.getChangeType());
        }
	}
	
	public List<SourceCodeChange> getSourceCodeChanges() {
		return changeDistiller.getSourceCodeChanges();
	}
}
