package be.ac.ua.ansymo.cheopsj.model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;
import be.ac.ua.ansymo.cheopsj.model.changes.Subject;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixAttribute;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixBehaviouralEntity;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixEntity;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixInvocation;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixLocalVariable;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixMethod;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixPackage;

public class PrintGraphForGroove {
	
	private ModelManager model;
	
	//The PrintGraphForGroove is a Singleton entity, hence the constructor is private.
	//You should always call the static method getInstance() to get the PrintGraphForGroove instance.
	private static PrintGraphForGroove INSTANCE = null;
	
	private PrintGraphForGroove() {
		model = ModelManager.getInstance();
	}
	
	/**
	 * The PrintGraphForGroove is a Singleton entity. Therefore the constructor is private.
	 * This method returns an instance of the PrintGraphForGroove. If no instance existed 
	 * before it will call the private constructor to create a new instance. Else
	 * It will return the existing instance. 
	 *  
	 * @return the Singleton PrintGraphForGroove instance
	 */
	public static PrintGraphForGroove getInstance() {
		if (INSTANCE == null)
			INSTANCE = new PrintGraphForGroove();
		return INSTANCE;
	}
	
	public void printGraphForGroove(){
		try{
			FileWriter fstream = new FileWriter("start.gst");
			BufferedWriter out = new BufferedWriter(fstream);
			printStartOfGrooveGraph(out);

			for(IChange change : model.getModelManagerChange().getChanges()){
				printEdgesInGrooveChange(out, change);
			}

			for(Subject famix : model.getFamixElements()){
				printEdgesInGrooveFamix(famix, out);
			}

			for(IChange change : model.getModelManagerChange().getChanges()){
				for(Change dep : ((AtomicChange)change).getStructuralDependencies()){
					printEdgeInGrooveGraph(out, change.getID(), dep.getID(), "depends");
				}

				String subjectID = ((AtomicChange)change).getChangeSubject().getID();
				printEdgeInGrooveGraph(out, change.getID(), subjectID, "subject");
			}

			printEndOfGrooveGraph(out);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void printEdgesInGrooveChange(BufferedWriter out, IChange change) {
		try {
			printNodeInGrooveGraph(out, change.getID());
			if(change instanceof Add){
				printEdgeInGrooveGraph(out, change.getID(), change.getID(), "type:Add");	
			}else if(change instanceof Remove){
				printEdgeInGrooveGraph(out, change.getID(), change.getID(), "type:Rem");
			}
	
			String timestampID = change.getID() + "a0";
			printNodeInGrooveGraph(out, timestampID);
			printEdgeInGrooveGraph(out, timestampID, timestampID, "int:"+change.getTimeStamp().getTime());
			printEdgeInGrooveGraph(out, change.getID(), timestampID, "timestamp");
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void printEdgesInGrooveFamix(Subject famix, BufferedWriter out) {
		try {
			printNodeInGrooveGraph(out, famix.getID());
	
			printEdgeInGrooveGraph(out, famix.getID(), famix.getID(), "type:"+famix.getFamixType());
	
			//name ?
			String nameID = famix.getID() + "a0";
			printNodeInGrooveGraph(out, nameID);
			if(famix instanceof FamixEntity){
				printEdgeInGrooveGraph(out, nameID, nameID, "string:&quot;"+((FamixEntity)famix).getName()+"&quot;");
			}else if(famix instanceof FamixInvocation){
				//printEdgeInGrooveGraph(out, nameID, nameID, "string:&quot;"+((FamixInvocation)famix).getStringRepresentation()+":&quot;");
			}
			printEdgeInGrooveGraph(out, famix.getID(), nameID, "name");
	
			if (famix instanceof FamixPackage) {
				FamixPackage belongsTo = ((FamixPackage) famix).getBelongsToPackage();
				if(belongsTo != null)				
					printEdgeInGrooveGraph(out, famix.getID(), belongsTo.getID(), "belongsTo");
			} else if (famix instanceof FamixClass) {
				FamixPackage belongsTo = ((FamixClass) famix).getBelongsToPackage();
				if(belongsTo != null)				
					printEdgeInGrooveGraph(out, famix.getID(), belongsTo.getID(), "belongsTo");
				FamixClass belongsToClass = ((FamixClass) famix).getBelongsToClass();
				if(belongsToClass != null)
					printEdgeInGrooveGraph(out, famix.getID(), belongsToClass.getID(), "belongsTo");
			} else if (famix instanceof FamixMethod) {
				FamixClass belongsToClass = ((FamixMethod) famix).getBelongsToClass();
				if(belongsToClass != null)
					printEdgeInGrooveGraph(out, famix.getID(), belongsToClass.getID(), "belongsTo");
				FamixClass returnClass = ((FamixMethod) famix).getDeclaredReturnClass();
				if(returnClass != null)
					printEdgeInGrooveGraph(out, famix.getID(), returnClass.getID(), "returnClass");
			} else if (famix instanceof FamixAttribute) {
				FamixClass belongsToClass = ((FamixAttribute) famix).getBelongsToClass();
				if(belongsToClass != null)
					printEdgeInGrooveGraph(out, famix.getID(), belongsToClass.getID(), "belongsTo");
				FamixClass declaredClass = ((FamixAttribute) famix).getDeclaredClass();
				if(declaredClass != null)
					printEdgeInGrooveGraph(out, famix.getID(), declaredClass.getID(), "declaredClass");
			} else if (famix instanceof FamixInvocation) {
				FamixBehaviouralEntity candidate = ((FamixInvocation) famix).getCandidate();
				if(candidate != null)
					printEdgeInGrooveGraph(out, famix.getID(), candidate.getID(), "candidate");
				FamixMethod invokedby = (FamixMethod) ((FamixInvocation) famix).getInvokedBy();
				printEdgeInGrooveGraph(out, famix.getID(), invokedby.getID(), "invokedBy");
			} else if (famix instanceof FamixLocalVariable) {
				FamixMethod belongsToMethod = (FamixMethod) ((FamixLocalVariable) famix).getBelongsToBehaviour();
				if(belongsToMethod != null)
					printEdgeInGrooveGraph(out, famix.getID(), belongsToMethod.getID(), "belongsTo");
				FamixClass declaredClass = ((FamixLocalVariable) famix).getDeclaredClass();
				if(declaredClass != null)
					printEdgeInGrooveGraph(out, famix.getID(), declaredClass.getID(), "declaredClass");
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void printNodeInGrooveGraph(BufferedWriter out, String id) throws IOException{
		out.write("\t\t<node id=\""+id+"\">\n");
		out.write("\t\t</node>\n");
	}
	
	private void printEdgeInGrooveGraph(BufferedWriter out, String from, String to, String label) throws IOException{
		out.write("\t\t<edge from=\""+from+"\" to=\""+to+"\">\n");
		out.write("\t\t\t<attr name=\"label\">\n");
		out.write("\t\t\t\t<string>"+label+"</string>\n");
		out.write("\t\t\t</attr>\n");
		out.write("\t\t</edge>\n");
	}
	
	private void printStartOfGrooveGraph(BufferedWriter out ) throws IOException{
		out.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
		out.write("<gxl xmlns=\"http://www.gupro.de/GXL/gxl-1.0.dtd\">\n");
		out.write("\t<graph role=\"graph\" edgeids=\"false\" edgemode=\"directed\" id=\"start\">\n");
		out.write("\t\t<attr name=\"$version\">\n");
		out.write("\t\t\t<string>curly</string>\n");
		out.write("\t\t</attr>\n");
		out.write("\t\t<attr name=\"$version\">\n");
		out.write("\t\t\t<string>curly</string>\n");
		out.write("\t\t</attr>\n");
	}
	
	private void printEndOfGrooveGraph(BufferedWriter out ) throws IOException{
		out.write("\t</graph>\n");
		out.write("</gxl>");
	}
}
