package org.sagebionetworks.web.client.widget.entity.renderer;

import java.util.Map;

import org.sagebionetworks.web.client.PortalGinInjector;
import org.sagebionetworks.web.client.security.AuthenticationController;
import org.sagebionetworks.web.client.utils.Callback;
import org.sagebionetworks.web.client.widget.WidgetRendererPresenter;
import org.sagebionetworks.web.client.widget.entity.RegisterTeamDialog;
import org.sagebionetworks.web.shared.WidgetConstants;
import org.sagebionetworks.web.shared.WikiPageKey;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class RegisterChallengeTeamWidget implements SingleButtonView.Presenter, WidgetRendererPresenter {
	
	private SingleButtonView view;
	private Map<String,String> descriptor;
	private PortalGinInjector ginInjector;
	private AuthenticationController authController;
	public static final String DEFAULT_BUTTON_TEXT = "Register a Team";
	String challengeId;
	Callback widgetRefreshRequired;
	
	@Inject
	public RegisterChallengeTeamWidget(SingleButtonView view, PortalGinInjector ginInjector, AuthenticationController authController) {
		this.view = view;
		this.ginInjector = ginInjector;
		this.authController = authController;
		view.setPresenter(this);
	}
	
	@Override
	public void configure(final WikiPageKey wikiKey, final Map<String, String> widgetDescriptor, Callback widgetRefreshRequired, Long wikiVersionInView) {
		this.descriptor = widgetDescriptor;
		this.widgetRefreshRequired = widgetRefreshRequired;
		challengeId = descriptor.get(WidgetConstants.CHALLENGE_ID_KEY);
		String buttonText = descriptor.get(WidgetConstants.BUTTON_TEXT_KEY);
		if (buttonText == null)
			buttonText = DEFAULT_BUTTON_TEXT;
		
		view.setButtonText(buttonText);
		descriptor = widgetDescriptor;
		view.setButtonVisible(authController.isLoggedIn());
	}
	
	@Override
	public void onClick() {
		RegisterTeamDialog dialog = ginInjector.getRegisterTeamDialog();
		dialog.configure(challengeId, widgetRefreshRequired);
		dialog.showModal();
	}
	
	@Override
	public Widget asWidget() {
		return view.asWidget();
	}
}
