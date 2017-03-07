package org.sagebionetworks.web.client.widget.display;

import org.sagebionetworks.repo.model.Entity;
import org.sagebionetworks.web.client.utils.Callback;
import org.sagebionetworks.web.shared.ProjectDisplayBundle;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public interface ProjectDisplayView {
	public interface Presenter {

		Widget asWidget();

		void onSave();

		void clear();

		void configure(Entity entity, String userId, Callback callback);

		void cancel();
		
	}

	Widget asWidget();

	void setSynAlertWidget(IsWidget asWidget);
	
	void setPresenter(Presenter presenter);

	void clear();

	void hide();

	void show();

	void setWiki(boolean value);
	void setFiles(boolean value);
	void setTables(boolean value);
	void setChallenge(boolean value);
	void setDiscussion(boolean value);
	void setDocker(boolean value);
	
	boolean getWiki();
	boolean getFiles();
	boolean getTables();
	boolean getChallenge();
	boolean getDiscussion();
	boolean getDocker();

	
}