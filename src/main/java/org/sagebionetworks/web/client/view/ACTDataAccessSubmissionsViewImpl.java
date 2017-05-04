package org.sagebionetworks.web.client.view;

import java.util.Date;
import java.util.List;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.html.Div;
import org.gwtbootstrap3.client.ui.html.Span;
import org.gwtbootstrap3.extras.datetimepicker.client.ui.DateTimePicker;
import org.gwtbootstrap3.extras.datetimepicker.client.ui.base.events.ChangeDateEvent;
import org.gwtbootstrap3.extras.datetimepicker.client.ui.base.events.ChangeDateHandler;
import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.widget.footer.Footer;
import org.sagebionetworks.web.client.widget.header.Header;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class ACTDataAccessSubmissionsViewImpl implements ACTDataAccessSubmissionsView {

	public interface ACTViewImplUiBinder extends UiBinder<Widget, ACTDataAccessSubmissionsViewImpl> {}

	@UiField
	SimplePanel header;
	@UiField
	SimplePanel footer;
	
	@UiField
	DropDownMenu stateDropdownMenu;
	@UiField
	DateTimePicker minDatePicker;
	@UiField
	DateTimePicker maxDatePicker;
	
	@UiField
	Div synAlertContainer;
	@UiField
	Div accessRequirementContainer;
	@UiField
	Div showHideAccessRequirementButtonContainer;
	
	@UiField
	Div tableData;
	@UiField
	Button clearStateFilter;
	@UiField
	Button clearDateFilter;
	
	@UiField
	Span currentState;
	@UiField
	Panel accessRequirementUI;
	@UiField
	Anchor createdOnColumnHeader;
	@UiField
	CheckBox certifiedCheckbox;
	@UiField
	CheckBox validatedCheckbox;
	@UiField
	CheckBox ducCheckbox;
	@UiField
	Div ducTemplateFileContainer;
	@UiField
	CheckBox irbCheckbox;
	@UiField
	CheckBox otherAttachmentsCheckbox;
	@UiField
	CheckBox annualRenewalCheckbox;
	@UiField
	CheckBox intendedDataUsePublicCheckbox;
	@UiField
	Div subjectsContainer;
	@UiField
	Div hasRequestUI;
	@UiField
	AnchorListItem backLink;
	
	private Presenter presenter;
	private Header headerWidget;
	private Footer footerWidget;
	
	Widget widget;
	@Inject
	public ACTDataAccessSubmissionsViewImpl(ACTViewImplUiBinder binder,
			Header headerWidget, 
			Footer footerWidget
			) {
		widget = binder.createAndBindUi(this);
		this.headerWidget = headerWidget;
		this.footerWidget = footerWidget;
		headerWidget.configure(false);
		header.add(headerWidget.asWidget());
		footer.add(footerWidget.asWidget());
		backLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.onBack();
			}
		});
		clearStateFilter.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.onClearStateFilter();
			}
		});
		clearDateFilter.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.onClearDateFilter();
			}
		});
		minDatePicker.addChangeDateHandler(new ChangeDateHandler() {
			@Override
			public void onChangeDate(ChangeDateEvent evt) {
				presenter.onMinDateSelected(minDatePicker.getValue());;
			}
		});
		maxDatePicker.addChangeDateHandler(new ChangeDateHandler() {
			@Override
			public void onChangeDate(ChangeDateEvent evt) {
				presenter.onMaxDateSelected(maxDatePicker.getValue());;
			}
		});
		createdOnColumnHeader.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.onCreatedOnClick();
			}
		});
	}
	
	@Override
	public void setPresenter(final Presenter presenter) {
		this.presenter = presenter;
		header.clear();
		headerWidget.configure(false);
		header.add(headerWidget.asWidget());
		footer.clear();
		footer.add(footerWidget.asWidget());
		headerWidget.refresh();	
		Window.scrollTo(0, 0); // scroll user to top of page
	}

	@Override
	public void setAccessRequirementUIVisible(boolean visible) {
		accessRequirementUI.setVisible(visible);
	}
	@Override
	public void showErrorMessage(String message) {
		DisplayUtils.showErrorMessage(message);
	}

	@Override
	public void showLoading() {
	}

	@Override
	public void showInfo(String title, String message) {
		DisplayUtils.showInfo(title, message);
	}

	@Override
	public void clear() {		
	}
	@Override
	public void setSelectedStateText(String state) {
		currentState.setText(state);
	}
	@Override
	public void setSelectedMaxDate(Date date) {
		maxDatePicker.setValue(date);
	}
	@Override
	public void setSelectedMinDate(Date date) {
		minDatePicker.setValue(date);
	}
	
	@Override
	public Widget asWidget() {
		return widget;
	}
	
	@Override
	public void setStates(List<String> states) {
		stateDropdownMenu.clear();
		for (final String state : states) {
			AnchorListItem item = new AnchorListItem(state);
			item.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					presenter.onStateSelected(state);
				}
			});
			stateDropdownMenu.add(item);
		}
	}
	
	@Override
	public void setLoadMoreContainer(IsWidget w) {
		tableData.clear();
		tableData.add(w);
	}
	@Override
	public void setSynAlert(IsWidget w) {
		synAlertContainer.clear();
		synAlertContainer.add(w);
	}
	
	@Override
	public void setAccessRequirementWidget(IsWidget w) {
		accessRequirementContainer.clear();
		accessRequirementContainer.add(w);
	}
	@Override
	public void setShowHideButton(IsWidget button) {
		showHideAccessRequirementButtonContainer.clear();
		showHideAccessRequirementButtonContainer.add(button);
	}
	
	@Override
	public void setAreOtherAttachmentsRequired(boolean value) {
		otherAttachmentsCheckbox.setValue(value);
	}

	@Override
	public void setIsAnnualReviewRequired(boolean value) {
		annualRenewalCheckbox.setValue(value);
	}

	@Override
	public void setIsCertifiedUserRequired(boolean value) {
		certifiedCheckbox.setValue(value);
	}

	@Override
	public void setIsDUCRequired(boolean value) {
		ducCheckbox.setValue(value);
	}

	@Override
	public void setIsIDUPublic(boolean value) {
		intendedDataUsePublicCheckbox.setValue(value);
	}

	@Override
	public void setIsIRBApprovalRequired(boolean value) {
		irbCheckbox.setValue(value);
	}

	@Override
	public void setIsValidatedProfileRequired(boolean value) {
		validatedCheckbox.setValue(value);
	}
	@Override
	public void setSubjectsWidget(IsWidget w) {
		subjectsContainer.clear();
		subjectsContainer.add(w);
	}
	@Override
	public void setHasRequestUIVisible(boolean visible) {
		hasRequestUI.setVisible(visible);
	}
}
