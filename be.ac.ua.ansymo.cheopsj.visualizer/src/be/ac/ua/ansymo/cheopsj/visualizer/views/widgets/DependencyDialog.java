/***************************************************
 * Copyright (c) 2014 Nicolas Demarbaix
 * 
 * Contributors: 
 * 		Nicolas Demarbaix - Initial Implementation
 ***************************************************/
package be.ac.ua.ansymo.cheopsj.visualizer.views.widgets;

import java.util.Date;
import java.util.Vector;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;

/**
 * Dialog to display the dependencies of a change
 * @author nicolasdemarbaix
 *
 */
public class DependencyDialog extends TitleAreaDialog {

	private String entID = "";
	private String cDate = "";
	private String changeType = "";
	private String famixType = "";
	private String user = "";
	private String commitMessage = "";
	
	Vector<IChange> dependencies = null;
	Vector<IChange> dependents = null;
	
	/**
	 * Public constructor
	 * @param parentShell (Shell) parent component
	 */
	public DependencyDialog(Shell parentShell) {
		super(parentShell);
		dependencies = new Vector<IChange>();
		dependents = new Vector<IChange>();		
	}
	
	/**
	 * Set the data for the focus entity
	 * @param ID (String) unique name of entity
	 * @param date (String) string representation of the change date
	 * @param cType (String) change type
	 * @param fType (String) famix type
	 * @param user (String) user that change the entity
	 * @param commit (String) commit message
	 */
	public void setEntityData(String ID, String date, String cType, String fType, String user, String commit) {
		this.entID = ID;
		this.cDate = date;
		this.changeType = cType;
		this.famixType = fType;
		this.user = user;
		this.commitMessage = commit;
	}
	
	/**
	 * Set the relation data
	 * @param dept_vec (Vector<IChange>) list of dependents for the focus change
	 * @param depe_vec (Vector<IChange>) list of dependencies for the focus change
	 */
	public void setRelationData(Vector<IChange> dept_vec, Vector<IChange> depe_vec) {
		this.dependencies = depe_vec;
		this.dependents = dept_vec;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite body = (Composite)super.createDialogArea(parent);
		this.setTitle("Dependency relations for entity '" + entID + "'");
		body.setLayout(setupLayout());
		
		setupBody(body);
		
		return body;
	}
	
	/**
	 * Setup the layout of the dialog
	 * @return (GridLayout) dialog layout
	 */
	private GridLayout setupLayout() {
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginBottom = 5;
		layout.marginHeight = 5;
		layout.marginLeft = 5;
		layout.marginRight = 5;	
		layout.verticalSpacing = 5;
		return layout;
	}
	
	/**
	 * Setup the dialog itself
	 * @param parent (Composite) parent component
	 */
	private void setupBody(Composite parent) {
		Label info = new Label(parent, SWT.NONE);
		info.setText("Source change information: \n" 
					 + "   Date:           " + cDate + "\n"
					 + "   Change type:    " + changeType + "\n"
					 + "   Famix type:     " + famixType + "\n"
					 + "   User:           " + user + "\n"
					 + "   Commit message: " + commitMessage);
		GridData lData = new GridData();
		lData.verticalSpan = 1;
		lData.horizontalSpan = 1;
		info.setLayoutData(lData);
		
		Label deptLabel = new Label(parent, SWT.BORDER);
		deptLabel.setText("Dependents:");
		
		TableViewer viewer = new TableViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData vData = new GridData(SWT.FILL, SWT.FILL, true,true);
		vData.heightHint = 100;
		viewer.getTable().setLayoutData(vData);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		createTable(viewer);
		viewer.add(this.dependents.toArray());
		viewer.getTable().setSize(viewer.getTable().getSize().x, 100);

		Label depeLabel = new Label(parent, SWT.BORDER);
		depeLabel.setText("Dependencies: ");
		
		TableViewer viewer2 = new TableViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData v2Data = new GridData(SWT.FILL, SWT.FILL, true,true);
		v2Data.heightHint = 100;
		viewer2.getTable().setLayoutData(v2Data);
		viewer2.getTable().setHeaderVisible(true);
		viewer2.getTable().setLinesVisible(true);
		createTable(viewer2);
		viewer2.add(this.dependencies.toArray());
		viewer2.getTable().setSize(viewer.getTable().getSize().x, 100);
	}
	
	/**
	 * Create a table for the viewer
	 * @param viewer (TableViewer) the given viewer
	 */
	private void createTable(TableViewer viewer) {
		TableViewerColumn changeCol = new TableViewerColumn(viewer, SWT.NONE);
		changeCol.getColumn().setText("Change type");
		changeCol.getColumn().setWidth(100);
		changeCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return "";
			}
			public Image getImage(Object element) {
				IChange change = (IChange)element;
				return ((AtomicChange) change).getIcon();
			};
		});
		
		TableViewerColumn nameCol = new TableViewerColumn(viewer, SWT.NONE);
		nameCol.getColumn().setText("Entity");
		nameCol.getColumn().setWidth(100);
		nameCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				IChange change = (IChange)element;
				return ((AtomicChange)change).getName();
			}
			@Override
			public Image getImage(Object element) {
				return null;
			}
		});
		
		TableViewerColumn famixCol = new TableViewerColumn(viewer, SWT.NONE);
		famixCol.getColumn().setText("Famix type");
		famixCol.getColumn().setWidth(100);
		famixCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				IChange change = (IChange)element;
				return change.getFamixType();
			}
		});
		
		TableViewerColumn dateCol = new TableViewerColumn(viewer, SWT.NONE);
		dateCol.getColumn().setText("Change date");
		dateCol.getColumn().setWidth(100);
		dateCol.setLabelProvider(new ColumnLabelProvider() {
			@SuppressWarnings("deprecation")
			@Override
			public String getText(Object element) {
				IChange change = (IChange)element;
				Date d = change.getTimeStamp();
				return d.getDate() + "-" + (d.getMonth()+1) + "-" + (d.getYear()+1900);
			}
		});
		
		TableViewerColumn userCol = new TableViewerColumn(viewer, SWT.NONE);
		userCol.getColumn().setText("User");
		userCol.getColumn().setWidth(100);
		userCol.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				IChange change = (IChange)element;
				return change.getUser();
			};
		});
		
		TableViewerColumn commitCol = new TableViewerColumn(viewer, SWT.NONE);
		commitCol.getColumn().setText("Message");
		commitCol.getColumn().setWidth(100);
		commitCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				IChange change = (IChange)element;
				return change.getIntent();
			}
		});
	}


}
