package org.sagebionetworks.web.unitclient.widget.user;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.web.client.PortalGinInjector;
import org.sagebionetworks.web.client.view.DivView;
import org.sagebionetworks.web.client.widget.asynch.UserGroupHeaderFromAliasAsyncHandler;
import org.sagebionetworks.web.client.widget.team.TeamBadge;
import org.sagebionetworks.web.client.widget.team.UserTeamBadge;
import org.sagebionetworks.web.client.widget.user.UserBadge;
import org.sagebionetworks.web.shared.WidgetConstants;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.Widget;

/**
 * Unit test for the Team Badge widget.
 *
 */
public class UserTeamBadgeTest {
	@Mock
	PortalGinInjector mockGinInjector;
	@Mock
	UserBadge mockUserBadge;
	@Mock
	TeamBadge mockTeamBadge;
	UserTeamBadge badge;
	String principalId = "id1";
	Map<String, String> widgetDescriptor;
	@Mock
	UserGroupHeaderFromAliasAsyncHandler mockUserGroupHeaderAsyncHandler;
	@Mock
	DivView mockDiv;
	@Before
	public void before() throws JSONObjectAdapterException{
		MockitoAnnotations.initMocks(this);
		GWTMockUtilities.disarm();
		Widget mockView = mock(Widget.class);
		when(mockGinInjector.getUserBadgeWidget()).thenReturn(mockUserBadge);
		when(mockGinInjector.getTeamBadgeWidget()).thenReturn(mockTeamBadge);
		when(mockUserBadge.asWidget()).thenReturn(mockView);
		when(mockTeamBadge.asWidget()).thenReturn(mockView);
		
		badge = new UserTeamBadge(mockGinInjector, mockUserGroupHeaderAsyncHandler, mockDiv);
		widgetDescriptor = new HashMap<String, String>();
		widgetDescriptor.put(WidgetConstants.USER_TEAM_BADGE_WIDGET_IS_INDIVIDUAL_KEY, "true");
		widgetDescriptor.put(WidgetConstants.USER_TEAM_BADGE_WIDGET_ID_KEY, principalId);
	}

	@After
	public void tearDown(){
		// Be nice to the next test
		GWTMockUtilities.restore();
	}

	@Test
	public void testConfigure(){
		badge.configure(null, widgetDescriptor, null, null);
		verify(mockGinInjector).getUserBadgeWidget();
		verify(mockUserBadge).configure(eq(principalId));
	}
	
	@Test
	public void testConfigureWithUsername(){
		String username = "Potter";
		widgetDescriptor.remove(WidgetConstants.USER_TEAM_BADGE_WIDGET_ID_KEY);
		widgetDescriptor.put(WidgetConstants.USER_TEAM_BADGE_WIDGET_USERNAME_KEY, username);
		badge.configure(null, widgetDescriptor, null, null);
		verify(mockGinInjector).getUserBadgeWidget();
		verify(mockUserBadge).configureWithUsername(username);
	}
	
	@Test
	public void testConfigureTeam(){
		widgetDescriptor.put(WidgetConstants.USER_TEAM_BADGE_WIDGET_IS_INDIVIDUAL_KEY, "false");
		badge.configure(null, widgetDescriptor, null, null);
		verify(mockGinInjector).getTeamBadgeWidget();
		verify(mockTeamBadge).configure(eq(principalId));
	}
	
	@Test
	public void testConfigureNullIsIndividual(){
		widgetDescriptor.remove(WidgetConstants.USER_TEAM_BADGE_WIDGET_IS_INDIVIDUAL_KEY);
		badge.configure(null, widgetDescriptor, null, null);
		verify(mockGinInjector).getTeamBadgeWidget();
		verify(mockTeamBadge).configure(eq(principalId));
	}
	
	@Test
	public void testConfigureNullPrincipalId() {
		//userbadge or teambadge widget deals with null principal id, this should pass along
		widgetDescriptor.remove(WidgetConstants.USER_TEAM_BADGE_WIDGET_ID_KEY);
		badge.configure(null, widgetDescriptor, null, null);
		verify(mockUserBadge).configure(eq((String)null));
	}


}
