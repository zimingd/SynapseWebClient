package org.sagebionetworks.web.client.widget.entity.file;

import org.sagebionetworks.repo.model.FileEntity;
import org.sagebionetworks.web.client.EntityTypeProvider;
import org.sagebionetworks.web.client.SynapseClientAsync;
import org.sagebionetworks.web.client.events.EntityUpdatedEvent;
import org.sagebionetworks.web.client.events.EntityUpdatedHandler;
import org.sagebionetworks.web.client.model.EntityBundle;
import org.sagebionetworks.web.client.security.AuthenticationController;
import org.sagebionetworks.web.client.widget.SynapseWidgetPresenter;
import org.sagebionetworks.web.client.widget.entity.EntityEditor;
import org.sagebionetworks.web.shared.EntityType;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class FileTitleBar implements FileTitleBarView.Presenter, SynapseWidgetPresenter {
	
	private FileTitleBarView view;
	private AuthenticationController authenticationController;
	private HandlerManager handlerManager = new HandlerManager(this);
	private EntityBundle entityBundle;
	private EntityTypeProvider entityTypeProvider;
	private SynapseClientAsync synapseClient;
	private EntityEditor entityEditor;
	
	@Inject
	public FileTitleBar(FileTitleBarView view, AuthenticationController authenticationController, EntityTypeProvider entityTypeProvider, SynapseClientAsync synapseClient, EntityEditor entityEditor) {
		this.view = view;
		this.authenticationController = authenticationController;
		this.entityTypeProvider = entityTypeProvider;
		this.synapseClient = synapseClient;
		this.entityEditor = entityEditor;
		view.setPresenter(this);
	}	
	
	public Widget asWidget(EntityBundle bundle, boolean isAdministrator, boolean canEdit, boolean readOnly) {		
		view.setPresenter(this);
		this.entityBundle = bundle; 		
		
		// Get EntityType
		EntityType entityType = entityTypeProvider.getEntityTypeForEntity(bundle.getEntity());
		
		view.createTitlebar(bundle, entityType, authenticationController, isAdministrator, canEdit, readOnly);
		return view.asWidget();
	}
	
	/**
	 * For unit testing. call asWidget with the new Entity for the view to be in sync.
	 * @param bundle
	 */
	public void setEntityBundle(EntityBundle bundle) {
		this.entityBundle = bundle;
	}
	
	public void clearState() {
		view.clear();
		// remove handlers
		handlerManager = new HandlerManager(this);		
		this.entityBundle = null;		
	}

	/**
	 * Does nothing. Use asWidget(Entity)
	 */
	@Override
	public Widget asWidget() {
		return null;
	}
    
	@Override
	public void fireEntityUpdatedEvent() {
		handlerManager.fireEvent(new EntityUpdatedEvent());
	}
	
	public void addEntityUpdatedHandler(EntityUpdatedHandler handler) {
		handlerManager.addHandler(EntityUpdatedEvent.getType(), handler);
	}

	@Override
	public boolean isUserLoggedIn() {
		return authenticationController.getLoggedInUser() != null;
	}

	@Override
	public void addNewChild(EntityType type, String parentId) {
		entityEditor.addNewEntity(type, parentId);
		
	}
	
	@Override
	public void updateNodeStorageUsage(final AsyncCallback<Long> callback) {
		synapseClient.getStorageUsage(entityBundle.getEntity().getId(), callback);
	}

	public static boolean isDataPossiblyWithin(FileEntity fileEntity) {
		String dataFileHandleId = fileEntity.getDataFileHandleId();
		return (dataFileHandleId != null && dataFileHandleId.length() > 0);
	}
	
	/*
	 * Private Methods
	 */
}
