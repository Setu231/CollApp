
package io.collapp.web.api;

import io.collapp.model.Permission;
import io.collapp.model.User;
import io.collapp.model.UserMetadata;
import io.collapp.model.UserWithPermission;
import io.collapp.service.*;
import io.collapp.web.api.UserController;
import io.collapp.web.api.model.DisplayNameEmail;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collection;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventEmitter eventEmitter;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserWithPermission user;

    @Mock
    private ProjectService projectService;

    private UserController userController;

    @Before
    public void prepare() {
        userController = new UserController(userRepository, userService, eventEmitter, eventRepository,
            projectService);
    }

    @Test
    public void findAllUsers() {
        userController.findAllUsers();
        verify(userRepository).findAll();
    }

    @Test
    public void keepAlive() {
        Assert.assertTrue(userController.keepAlive());
    }

    @Test
    public void updateUserProfile() {
        DisplayNameEmail d = new DisplayNameEmail();
        d.setDisplayName("displayName");
        d.setEmail("email");
        d.setEmailNotification(true);
        d.setSkipOwnNotifications(true);

        userController.updateUserProfile(user, d);

        verify(userRepository).updateProfile(user, "email", "displayName", true, true);
    }

    @Test
    public void updateMetadata() {
        UserMetadata metadata = new UserMetadata(true, false);

        userController.updateMetadata(user, metadata);

        verify(userRepository).updateMetadata(user.getId(), metadata);
    }

    @Test
    public void testGetUserById() {
        when(userRepository.findById(user.getId())).thenReturn(user);

        User u = userController.getUser(user.getId());
        Assert.assertEquals(user, u);
    }

    @Test
    public void testGetUserByName() {
        when(userRepository.findUserByName(user.getProvider(), user.getUsername())).thenReturn(user);

        User u = userController.getUser(user.getProvider(), user.getUsername());
        Assert.assertEquals(user, u);
    }

    @Test
    public void userProfile() {
        Assert.assertEquals(user, userController.userProfile(user));
    }

    @Test
    public void testGetUserProfileWithoutGlobalRead() {
        User testUser = mock(User.class);
        when(userRepository.findUserByName("test", "test")).thenReturn(testUser);

        //
        userController.getUserProfile("test", "test", user, 1);
        //

        // check that we are going in the correct branch
        verify(user).projectsIdWithPermission(Permission.READ);
        verify(eventRepository).getUserActivityForProjects(eq(testUser.getId()), Mockito.<Date>any(),
            Mockito.<Collection<Integer>>any());
        verify(projectService).findProjectsActivityByUserInProjects(eq(testUser.getId()),
            Mockito.<Collection<Integer>>any());
        verify(eventRepository, Mockito.never()).getLatestActivityByProjects(eq(testUser.getId()), Mockito.<Date>any(),
            Mockito.<Collection<Integer>>any());
    }

    @Test
    public void testGetUserProfileWithGlobalRead() {
        User testUser = mock(User.class);
        when(userRepository.findUserByName("test", "test")).thenReturn(testUser);

        Map<Permission, Permission> permission = new EnumMap<>(Permission.class);
        permission.put(Permission.READ, Permission.READ);
        when(user.getBasePermissions()).thenReturn(permission);

        //
        userController.getUserProfile("test", "test", user, 1);
        //

        // check that we are going in the correct branch
        verify(user, Mockito.never()).projectsWithPermission(Permission.READ);
        verify(eventRepository).getUserActivity(eq(testUser.getId()), Mockito.<Date>any());
        verify(projectService).findProjectsActivityByUser(eq(testUser.getId()));
        verify(eventRepository, Mockito.never()).getLatestActivity(eq(testUser.getId()), Mockito.<Date>any());
    }

    @Test
    public void testClearAllTokens() {

        userController.clearAllTokens(user);

        verify(userRepository).clearAllTokens(eq(user));
    }

    @Test
    public void testGetUserActivity() {
        User testUser = mock(User.class);
        when(userRepository.findUserByName("test", "test")).thenReturn(testUser);

        userController.getUserActivity("test", "test", user);

        verify(eventRepository).getLatestActivityByProjects(eq(testUser.getId()), Mockito.<Date>any(),
            Mockito.<Collection<Integer>>any());
    }

    @Test
    public void testGetUserActivityWithGlobalRead() {
        User testUser = mock(User.class);
        when(userRepository.findUserByName("test", "test")).thenReturn(testUser);

        Map<Permission, Permission> permission = new EnumMap<>(Permission.class);
        permission.put(Permission.READ, Permission.READ);
        when(user.getBasePermissions()).thenReturn(permission);

        userController.getUserActivity("test", "test", user);

        verify(eventRepository).getLatestActivity(eq(testUser.getId()), Mockito.<Date>any());
    }
}
