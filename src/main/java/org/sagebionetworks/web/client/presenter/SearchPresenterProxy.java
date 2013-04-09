package org.sagebionetworks.web.client.presenter;

import org.sagebionetworks.web.client.place.Search;


import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
/**
 * Code cut point for SearchPresenter.
 * @author John
 *
 */
public class SearchPresenterProxy extends AbstractActivity implements PresenterProxy<Search>{

	AsyncProvider<SearchPresenter> provider;
	
	@Inject
	public SearchPresenterProxy(AsyncProvider<SearchPresenter> provider){
		this.provider = provider;
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		this.provider.get(new AsyncCallback<SearchPresenter>() {
			
			@Override
			public void onSuccess(SearchPresenter result) {
				result.start(panel, eventBus);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// not sure what to do here.
				
			}
		});
		
	}

	@Override
	public void setPlace(final Search place) {
		this.provider.get(new AsyncCallback<SearchPresenter>() {
			
			@Override
			public void onSuccess(SearchPresenter result) {
				result.setPlace(place);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// not sure what to do here.
			}
		});
	}

}

