package org.sagebionetworks.web.client.presenter;

import org.sagebionetworks.web.client.utils.CallbackP;
import org.sagebionetworks.web.client.widget.entity.RejectReasonView;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;


public class RejectReasonWidget implements RejectReasonView.Presenter, IsWidget {

    // Template rejection format
    public static String TEMPLATE_HEADER_HELLO = "Hi ";
    public static String TEMPLATE_HEADER_THANKS = ",\n\nThank you for submitting your Synapse" +
            " profile for validation. Before I can accept your request, you need to:\n";
    public static String TEMPLATE_HEADER_SIGNATURE = 
    		"\nPlease contact us at act@sagebionetworks.org if you have any questions.\n" + 
    		"\n" + 
    		"Regards,\n" + 
    		"Access and Compliance Team (ACT)\n" + 
    		"act@sagebionetworks.org";

    // If no options are shown for rejected reason
    public static String ERROR_MESSAGE = "Error: Please select at least one checkbox and generate a response or manually enter a response";

    // Common reasons for user rejection
    
    private String displayName;
    private RejectReasonView view;
    CallbackP<String> callback;

    @Inject
    public RejectReasonWidget (RejectReasonView view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    public void show(String displayName, CallbackP<String> callback) {
    	this.displayName = displayName;
        this.view.clear();
        this.callback = callback;
        view.show();
    }

    @Override
    public void updateResponse() {
        String output = view.getSelectedCheckboxText();
        view.setValue(TEMPLATE_HEADER_HELLO + displayName + TEMPLATE_HEADER_THANKS + output + TEMPLATE_HEADER_SIGNATURE);
    }

    @Override
    public void onSave() {
        if (view.getValue().equals("")) {
            view.showError(ERROR_MESSAGE);
        } else {
            callback.invoke(view.getValue());
            view.hide();
        }
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    };
}
