package org.sagebionetworks.web.client.widget.entity.editor;

import java.util.List;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.html.Div;
import org.gwtbootstrap3.client.ui.html.Text;
import org.sagebionetworks.web.client.plotly.BarMode;
import org.sagebionetworks.web.client.plotly.GraphType;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class PlotlyConfigViewImpl implements PlotlyConfigView {
	private Presenter presenter;
	@UiField
	ListBox typeDropdownMenu;
	@UiField
	ListBox barModeDropdownMenu;
	@UiField
	TextBox titleField;
	@UiField
	TextBox xAxisLabel;
	@UiField
	TextBox yAxisLabel;
	@UiField
	TextBox tableViewSynId;
	@UiField
	Div yAxisColumnsContainer;
	@UiField
	Button entityPickerButton;
	@UiField
	TextBox whereClause;
	@UiField
	TextBox groupByClause;
	@UiField
	Div showHideAdvancedButtonContainer;
	@UiField
	Div advancedUI;
	@UiField
	FormGroup barChartModeUI;
	@UiField
	Div synAlertContainer;
	@UiField
	Div extraWidgets;
	@UiField
	ListBox xColumnNamesMenu;
	@UiField
	ListBox yColumnNamesMenu;
	public interface PlotlyConfigViewImplUiBinder extends UiBinder<Widget, PlotlyConfigViewImpl> {}
	Widget widget;
	
	@Inject
	public PlotlyConfigViewImpl(PlotlyConfigViewImplUiBinder binder) {
		widget = binder.createAndBindUi(this);
		entityPickerButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.onFindTable();
			}
		});
		
		for (GraphType type : GraphType.values()) {
			typeDropdownMenu.addItem(type.getDisplayName(), type.name());
		}
		
		for (BarMode mode : BarMode.values()) {
			barModeDropdownMenu.addItem(mode.getDisplayName(), mode.name());
		}
		
		typeDropdownMenu.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				updateBarModeVisibility();
			}
		});
		yColumnNamesMenu.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				presenter.onAddYColumn(yColumnNamesMenu.getSelectedValue());
			}
		});
		xColumnNamesMenu.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				presenter.onXColumnChanged();
			}
		});
	}
	private void updateBarModeVisibility() {
		setBarModeVisible(GraphType.BAR.equals(getGraphType()));
	}
	
	@Override
	public Widget asWidget() {
		return widget;
	}	

	@Override 
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}


	@Override
	public String getTitle() {
		return titleField.getValue();
	}

	@Override
	public void setTitle(String title) {
		titleField.setValue(title);
	}

	@Override
	public BarMode getBarMode() {
		return BarMode.valueOf(barModeDropdownMenu.getSelectedValue());
	}
	
	@Override
	public void setBarMode(BarMode barMode) {
		for (int i = 0; i < barModeDropdownMenu.getItemCount(); i++) {
			if (barMode.name().equals(barModeDropdownMenu.getValue(i))) {
				//found
				barModeDropdownMenu.setSelectedIndex(i);
				break;
			}
		}
	}
	
	@Override
	public GraphType getGraphType() {
		return GraphType.valueOf(typeDropdownMenu.getSelectedValue());
	}
	
	@Override
	public void setGraphType(GraphType graphType) {
		for (int i = 0; i < typeDropdownMenu.getItemCount(); i++) {
			if (graphType.name().equals(typeDropdownMenu.getValue(i))) {
				//found
				typeDropdownMenu.setSelectedIndex(i);
				break;
			}
		}
		updateBarModeVisibility();
	}
	
	@Override
	public String getXAxisLabel() {
		return xAxisLabel.getValue();
	}


	@Override
	public void setXAxisLabel(String label) {
		xAxisLabel.setValue(label);
	}


	@Override
	public String getYAxisLabel() {
		return yAxisLabel.getValue();
	}


	@Override
	public void setYAxisLabel(String label) {
		yAxisLabel.setValue(label);
	}


	@Override
	public String getWhereClause() {
		return whereClause.getValue();
	}


	@Override
	public void setWhereClause(String v) {
		whereClause.setValue(v);
	}


	@Override
	public String getGroupByClause() {
		return groupByClause.getValue();
	}


	@Override
	public void setGroupByClause(String v) {
		groupByClause.setValue(v);
	}

	@Override
	public void clearYAxisColumns() {
		yAxisColumnsContainer.clear();
	}


	@Override
	public void addYAxisColumn(final String yColumnName) {
		final Div yColumnContainer = new Div();
		yColumnContainer.addStyleName("margin-bottom-5");
		yColumnContainer.add(new Text(yColumnName));
		Icon deleteButton = new Icon(IconType.TIMES);
		deleteButton.addStyleName("imageButton text-danger margin-left-5");
		yColumnContainer.add(deleteButton);
		deleteButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.onRemoveYColumn(yColumnName);
				yAxisColumnsContainer.remove(yColumnContainer);
			}
		});
		yAxisColumnsContainer.add(yColumnContainer);
	}
	
	@Override
	public void setTableSynId(String value) {
		tableViewSynId.setValue(value);
	}

	@Override
	public String getTableSynId() {
		return tableViewSynId.getValue();
	}
	
	@Override
	public void setAvailableColumns(List<String> names) {
		xColumnNamesMenu.clear();
		yColumnNamesMenu.clear();
		yColumnNamesMenu.addItem(" ");
		for (String name : names) {
			xColumnNamesMenu.addItem(name);
			yColumnNamesMenu.addItem(name);
		}
	}
	
	@Override
	public void resetSelectedYColumn() {
		yColumnNamesMenu.setSelectedIndex(0);
	}
	
	@Override
	public String getXAxisColumnName() {
		return xColumnNamesMenu.getSelectedValue();
	}
	@Override
	public void setXAxisColumnName(String value) {
		boolean found = false;
		for (int i = 0; i < xColumnNamesMenu.getItemCount(); i++) {
			if (xColumnNamesMenu.getItemText(i).equals(value)) {
				found = true;
				xColumnNamesMenu.setSelectedIndex(i);
				break;
			}
		}
		if (!found) {
			xColumnNamesMenu.addItem(value);
			xColumnNamesMenu.setSelectedIndex(xColumnNamesMenu.getItemCount() - 1);
		}
	}

	@Override
	public void setAdvancedUIVisible(boolean visible) {
		advancedUI.setVisible(visible);
	}
	@Override
	public void setBarModeVisible(boolean visible) {
		barChartModeUI.setVisible(visible);
	}
	
	@Override
	public void add(IsWidget w) {
		extraWidgets.add(w);
	}
	@Override
	public void setSynAlert(IsWidget w) {
		synAlertContainer.clear();
		synAlertContainer.add(w);
	}
}
