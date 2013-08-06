package org.sagebionetworks.web.client.widget.entity;

import java.util.ArrayList;
import java.util.List;

import org.sagebionetworks.repo.model.BatchResults;
import org.sagebionetworks.repo.model.EntityHeader;
import org.sagebionetworks.repo.model.Reference;
import org.sagebionetworks.repo.model.message.ObjectType;
import org.sagebionetworks.repo.model.request.ReferenceList;
import org.sagebionetworks.repo.model.wiki.WikiPage;
import org.sagebionetworks.schema.adapter.AdapterFactory;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.web.client.DisplayConstants;
import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.GlobalApplicationState;
import org.sagebionetworks.web.client.SynapseClientAsync;
import org.sagebionetworks.web.client.place.Synapse;
import org.sagebionetworks.web.client.security.AuthenticationController;
import org.sagebionetworks.web.client.transform.NodeModelCreator;
import org.sagebionetworks.web.client.widget.SynapseWidgetPresenter;
import org.sagebionetworks.web.shared.WikiPageKey;
import org.sagebionetworks.web.shared.exceptions.ForbiddenException;
import org.sagebionetworks.web.shared.exceptions.NotFoundException;
import org.sagebionetworks.web.shared.exceptions.UnknownErrorException;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * Lightweight widget used to show a wiki page (has a markdown widget and pagebrowser)
 * 
 * @author Jay
 *
 */
public class WikiPageWidget implements WikiPageWidgetView.Presenter,
SynapseWidgetPresenter {
	
	private SynapseClientAsync synapseClient;
	private NodeModelCreator nodeModelCreator;
	private GlobalApplicationState globalApplicationState;
	private Callback callback;
	private JSONObjectAdapter jsonObjectAdapter;
	private WikiPageKey wikiKey;
	private Boolean canEdit, showAddPage;
	private WikiPage currentPage;
	private boolean isEmbeddedInOwnerPage;
	private AdapterFactory adapterFactory;
	private String ownerObjectName; //used for linking back to the owner object
	private int spanWidth;
	private WikiPageWidgetView view; 
	AuthenticationController authenticationController;
	
	public interface Callback{
		public void pageUpdated();
		public void noWikiFound();
	}
	
	public interface OwnerObjectNameCallback{
		public void ownerObjectNameInitialized();
	}
	
	@Inject
	public WikiPageWidget(WikiPageWidgetView view,
			SynapseClientAsync synapseClient,
			NodeModelCreator nodeModelCreator,
			JSONObjectAdapter jsonObjectAdapter, AdapterFactory adapterFactory,
			GlobalApplicationState globalApplicationState,
			AuthenticationController authenticationController) {
		super();
		this.view = view;
		this.synapseClient = synapseClient;
		this.nodeModelCreator = nodeModelCreator;
		this.jsonObjectAdapter = jsonObjectAdapter;
		this.adapterFactory = adapterFactory;
		this.globalApplicationState = globalApplicationState;
		this.authenticationController = authenticationController;
		view.setPresenter(this);
	}
	
	@Override
	public Widget asWidget() {
		return view.asWidget();
	}
	
	public void configure(final WikiPageKey inWikiKey, final Boolean canEdit, final Callback callback, final boolean isEmbeddedInOwnerPage, final int spanWidth, final boolean showAddPage) {
		this.canEdit = canEdit;
		this.wikiKey = inWikiKey;
		this.isEmbeddedInOwnerPage = isEmbeddedInOwnerPage;
		this.spanWidth = spanWidth;
		this.showAddPage = showAddPage;
		//set up callback
		if (callback != null)
			this.callback = callback;
		else 
			this.callback = new Callback() {
				@Override
				public void pageUpdated() {
				}
				@Override
				public void noWikiFound() {
				}
			};
		
		setOwnerObjectName(new OwnerObjectNameCallback() {
			@Override
			public void ownerObjectNameInitialized() {
				//get the wiki page
				synapseClient.getWikiPage(wikiKey, new AsyncCallback<String>() {
					@Override
					public void onSuccess(String result) {
						try {
							currentPage = nodeModelCreator.createJSONEntity(result, WikiPage.class);
							wikiKey.setWikiPageId(currentPage.getId());
							view.configure(currentPage, wikiKey, ownerObjectName, canEdit, isEmbeddedInOwnerPage, spanWidth, showAddPage);
						} catch (JSONObjectAdapterException e) {
							onFailure(e);
						}
					}
					@Override
					public void onFailure(Throwable caught) {
						//if it is because of a missing root (and we have edit permission), then the pages browser should have a Create Wiki button
						if (caught instanceof NotFoundException) {
							//show insert wiki button if user can edit and it's embedded in another entity page
							if (canEdit && isEmbeddedInOwnerPage)
								view.showNoWikiAvailableUI();
							else if (!isEmbeddedInOwnerPage) //otherwise, if it's not embedded in the owner page, show a 404
								view.show404();
							
							callback.noWikiFound();
						}
						else if (caught instanceof ForbiddenException) {
							if (!isEmbeddedInOwnerPage) //if it's not embedded in the owner page, show a 403
								view.show403();
						}
						else {
							if(!DisplayUtils.handleServiceException(caught, globalApplicationState.getPlaceChanger(), authenticationController.isLoggedIn(), view))
								view.showErrorMessage(DisplayConstants.ERROR_LOADING_WIKI_FAILED+caught.getMessage());
						}
					}
				});				
			}
		});
	}
	
	@Override
	public void refreshWikiAttachments(final String updatedTitle, final String updatedMarkdown, final Callback pageUpdatedCallback) {
		//get the wiki page
		synapseClient.getWikiPage(wikiKey, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				try {
					currentPage = nodeModelCreator.createJSONEntity(result, WikiPage.class);
					//update with the most current markdown and title
					currentPage.setMarkdown(updatedMarkdown);
					if (updatedTitle != null && updatedTitle.length() > 0)
						currentPage.setTitle(updatedTitle);
					view.updateWikiPage(currentPage);
					if (pageUpdatedCallback != null)
						pageUpdatedCallback.pageUpdated();
				} catch (JSONObjectAdapterException e) {
					onFailure(e);
				}
			}
			@Override
			public void onFailure(Throwable caught) {
				if(!DisplayUtils.handleServiceException(caught, globalApplicationState.getPlaceChanger(), authenticationController.isLoggedIn(), view))
					view.showErrorMessage(DisplayConstants.ERROR_LOADING_WIKI_FAILED+caught.getMessage());
			}
		});				
	}
	
	public void setOwnerObjectName(final OwnerObjectNameCallback callback) {
		if (wikiKey.getOwnerObjectType().equalsIgnoreCase(ObjectType.ENTITY.toString())) {
			//lookup the entity name based on the id
			Reference ref = new Reference();
			ref.setTargetId(wikiKey.getOwnerObjectId());
			List<Reference> allRefs = new ArrayList<Reference>();
			allRefs.add(ref);
			ReferenceList list = new ReferenceList();
			list.setReferences(allRefs);		
			try {
				synapseClient.getEntityHeaderBatch(list.writeToJSONObject(adapterFactory.createNew()).toJSONString(), new AsyncCallback<String>() {
					@Override
					public void onSuccess(String result) {					
						BatchResults<EntityHeader> headers;
						try {
							headers = nodeModelCreator.createBatchResults(result, EntityHeader.class);
							if (headers.getTotalNumberOfResults() == 1) {
								EntityHeader theHeader = headers.getResults().get(0);
								ownerObjectName = theHeader.getName();
								callback.ownerObjectNameInitialized();
							} else {
								view.show404();
							}
						} catch (JSONObjectAdapterException e) {
							onFailure(new UnknownErrorException(DisplayConstants.ERROR_INCOMPATIBLE_CLIENT_VERSION));
						}
					}
					
					@Override
					public void onFailure(Throwable caught) {					
						if(!DisplayUtils.handleServiceException(caught, globalApplicationState.getPlaceChanger(), authenticationController.isLoggedIn(), view))
							view.showErrorMessage(caught.getMessage());
					}
				});
			} catch (JSONObjectAdapterException e) {
				view.showErrorMessage(DisplayConstants.ERROR_INCOMPATIBLE_CLIENT_VERSION);
			}
		}
		else if (wikiKey.getOwnerObjectType().equalsIgnoreCase(ObjectType.EVALUATION.toString())) {
			ownerObjectName = "";
			callback.ownerObjectNameInitialized();
		}
	}
	
	@Override
	public void saveClicked(String title, String md) 
	{
		//before saving, we need to update the page first (widgets may have added/removed file handles from the list, like ImageConfigEditor)
		refreshWikiAttachments(title, md, new Callback() {
			@Override
			public void pageUpdated() {
				//after page attachments have been refreshed, send the update
				JSONObjectAdapter json = jsonObjectAdapter.createNew();
				try {
					currentPage.writeToJSONObject(json);
					synapseClient.updateWikiPage(wikiKey.getOwnerObjectId(), wikiKey.getOwnerObjectType(), json.toJSONString(), new AsyncCallback<String>() {
						@Override
						public void onSuccess(String result) {
							//showDefaultViewWithWiki();
							refresh();
						}
						@Override
						public void onFailure(Throwable caught) {
							if(!DisplayUtils.handleServiceException(caught, globalApplicationState.getPlaceChanger(), authenticationController.isLoggedIn(), view))
								view.showErrorMessage(caught.getMessage());
						}
					});
				} catch (JSONObjectAdapterException e) {
					view.showErrorMessage(DisplayConstants.ERROR_INCOMPATIBLE_CLIENT_VERSION);
				}
			}
			@Override
			public void noWikiFound() {
			}
		});
	}
	
	@Override
	public void deleteButtonClicked() {
		synapseClient.deleteWikiPage(wikiKey, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				//clear the now invalid page id from the wiki key
				wikiKey.setWikiPageId(null);
				if (isEmbeddedInOwnerPage)
					refresh();
				else
					globalApplicationState.getPlaceChanger().goTo(new Synapse(wikiKey.getOwnerObjectId()));
			}
			
			@Override
			public void onFailure(Throwable caught) {
				if(!DisplayUtils.handleServiceException(caught, globalApplicationState.getPlaceChanger(), authenticationController.isLoggedIn(), view))
					view.showErrorMessage(caught.getMessage());
			}
		});	
	}
	
	@Override
	public void cancelClicked() {
		refresh();
	}

	@Override
	public void createPage(final String name) {
		WikiPage page = new WikiPage();
		//if this is creating the root wiki, then refresh the full page
		final boolean isCreatingWiki = wikiKey.getWikiPageId() ==null;
		page.setParentWikiId(wikiKey.getWikiPageId());
		page.setTitle(name);
		String wikiPageJson;
		try {
			wikiPageJson = page.writeToJSONObject(adapterFactory.createNew()).toJSONString();
			synapseClient.createWikiPage(wikiKey.getOwnerObjectId(),  wikiKey.getOwnerObjectType(), wikiPageJson, new AsyncCallback<String>() {
				@Override
				public void onSuccess(String result) {
					if (isCreatingWiki)
						view.showInfo("Wiki Created", "");
					else
						view.showInfo("Page '" + name + "' Added", "");
					
					refresh();
				}
				
				@Override
				public void onFailure(Throwable caught) {
					if(!DisplayUtils.handleServiceException(caught, globalApplicationState.getPlaceChanger(), authenticationController.isLoggedIn(), view))
						view.showErrorMessage(DisplayConstants.ERROR_PAGE_CREATION_FAILED);
				}
			});
		} catch (JSONObjectAdapterException e) {			
			view.showErrorMessage(DisplayConstants.ERROR_INCOMPATIBLE_CLIENT_VERSION);		
		}
	}
	
	public void clear(){
		view.clear();
	}
	private void refresh() {
		configure(wikiKey, canEdit, callback, isEmbeddedInOwnerPage, spanWidth, showAddPage);
	}
}
