
package io.collapp.web.api;

import com.google.gson.JsonParseException;
import io.collapp.common.Json;
import io.collapp.model.User;
import io.collapp.model.UserMetadata;
import io.collapp.model.UserToCreate;
import io.collapp.service.EventEmitter;
import io.collapp.service.UserRepository;
import io.collapp.service.UserService;
import io.collapp.web.api.UsersAdministrationController;
import io.collapp.web.api.UsersAdministrationController.Update;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UsersAdministrationControllerTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserService userService;

	@Mock
	private EventEmitter eventEmitter;

	private UsersAdministrationController usersAdministrationController;

	@Before
	public void prepare() {
		usersAdministrationController = new UsersAdministrationController(userRepository, userService, eventEmitter);
	}

	@Test
	public void toggleUser() {
		User u = new User(42, "demo", "a", null, null, true, true, new Date(), false, Json.GSON.toJson(new UserMetadata(false, false)));
		Update up = new Update();
		up.setEnabled(false);
		usersAdministrationController.toggle(0, u, up);
		verify(userRepository).toggle(0, false);
	}

	@Test
	public void createUser() {
		UserToCreate utc = new UserToCreate();
		utc.setProvider("demo");
		utc.setUsername("username");
		usersAdministrationController.createUser(utc);
		verify(userService).createUser(utc);
	}

	@Test
	public void createUsers() throws JsonParseException, IOException {
		MultipartFile mpf = mock(MultipartFile.class);
		when(mpf.getInputStream()).thenReturn(
				new ByteArrayInputStream("[{\"provider\" : \"demo\", \"username\" : \"username\"}]".getBytes("UTF-8")));
		usersAdministrationController.createUsers(mpf);
		verify(userService).createUsers(Mockito.<List<UserToCreate>> any());
	}

	@Test(expected = IllegalArgumentException.class)
	public void sameUser() {
		User u = new User(0, "demo", "a", null, null, true, true, new Date(), false, Json.GSON.toJson(new UserMetadata(false, false)));
		Update up = new Update();
		up.setEnabled(false);
		usersAdministrationController.toggle(0, u, up);
		verify(userRepository, never()).toggle(anyInt(), anyBoolean());
	}
}
