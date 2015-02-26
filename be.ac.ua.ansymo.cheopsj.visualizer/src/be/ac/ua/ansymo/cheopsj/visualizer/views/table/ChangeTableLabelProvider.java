package be.ac.ua.ansymo.cheopsj.visualizer.views.table;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixEntity;

public class ChangeTableLabelProvider extends LabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		switch (columnIndex) {
		case 0:
			if (element instanceof AtomicChange) return ((AtomicChange) element).getIcon();
		case 1:
			return null;
		case 2:
			if (element instanceof AtomicChange) {
				if (((AtomicChange)element).getChangeSubject() instanceof FamixEntity) {
					return ((FamixEntity) ((AtomicChange) element).getChangeSubject()).getIcon();
				}
			}
		default:
			return null;
		}
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return null;
		case 1:
			if (element instanceof IChange) return ((IChange) element).getName();
		case 2:
			return null;
		case 3:
			if (element instanceof IChange) return ((IChange) element).getTimeStamp().toString();
		default:
			return null;
		}
	}

}
