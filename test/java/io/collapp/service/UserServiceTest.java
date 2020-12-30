
package io.collapp.service;

import io.collapp.config.PersistenceAndServiceConfig;
import io.collapp.model.*;
import io.collapp.service.PermissionService;
import io.collapp.service.ProjectService;
import io.collapp.service.UserRepository;
import io.collapp.service.UserService;
import io.collapp.service.config.TestServiceConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestServiceConfig.class, PersistenceAndServiceConfig.class })
@Transactional
public class UserServiceTest {

	@Autowired
	private ProjectService projectService;

	@Autowired
	private PermissionService permissionService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Test
	public void createUserWithoutRole() {
		Assert.assertFalse(userRepository.userExistsAndEnabled("demo", "test"));
		UserToCreate userToCreate = new UserToCreate();
		userToCreate.setUsername("test");
		userToCreate.setProvider("demo");
		userToCreate.setEnabled(true);
		userService.createUser(userToCreate);
		Assert.assertTrue(userRepository.userExistsAndEnabled("demo", "test"));
		Assert.assertTrue(permissionService.findBaseRoleAndPermissionByUserId(
				userRepository.findUserByName("demo", "test").getId()).isEmpty());
	}

	@Test
	public void createUserWithRole() {
		Assert.assertFalse(userRepository.userExistsAndEnabled("demo", "test"));
		permissionService.createRole(new Role("A"));
		permissionService.createRole(new Role("B"));
		UserToCreate userToCreate = new UserToCreate();
		userToCreate.setUsername("test");
		userToCreate.setProvider("demo");
		userToCreate.setEnabled(true);
		userToCreate.setRoles(Arrays.asList("A", "B"));
		userService.createUser(userToCreate);
		Assert.assertTrue(userRepository.userExistsAndEnabled("demo", "test"));
		int userId = userRepository.findUserByName("demo", "test").getId();
		Map<String, ?> res = permissionService.findBaseRoleAndPermissionByUserId(userId);
		Assert.assertEquals(new HashSet<>(Arrays.asList("A", "B")), res.keySet());
	}

	@Test
	public void createUserWithPermissions() {
		projectService.create("test", "TEST", "desc");
		Project project = projectService.findByShortName("TEST");

		Set<Permission> permissions = EnumSet.of(Permission.READ, Permission.CREATE_FILE);
		permissionService.createRole(new Role("A"));
		permissionService.updatePermissionsToRole(new Role("A"), permissions);

		UserToCreate userToCreate = new UserToCreate();
		userToCreate.setUsername("test");
		userToCreate.setProvider("demo");
		userToCreate.setEnabled(true);
		userToCreate.setRoles(Arrays.asList("A"));
		userService.createUser(userToCreate);
		int userId = userRepository.findUserByName("demo", "test").getId();

		permissionService.createRoleInProjectId(new Role("ROLE_PROJ"), project.getId());
		permissionService.assignRoleToUsersInProjectId(new Role("ROLE_PROJ"), Collections.singleton(userId),
				project.getId());
		permissionService.updatePermissionsToRoleInProjectId(new Role("ROLE_PROJ"), permissions, project.getId());

		UserWithPermission uwp = userService.findUserWithPermission(userId);
		Assert.assertEquals(uwp.getBasePermissions().keySet(), permissions);
		Assert.assertEquals(uwp.getPermissionsForProject().get("TEST").keySet(), permissions);
	}

	@Test
	public void createUsers() {
		UserToCreate userToCreate1 = new UserToCreate();
		userToCreate1.setUsername("test1");
		userToCreate1.setProvider("demo");
		userToCreate1.setEnabled(true);
		UserToCreate userToCreate2 = new UserToCreate();
		userToCreate2.setUsername("test2");
		userToCreate2.setProvider("demo");
		userToCreate2.setEnabled(true);
		userService.createUsers(Arrays.asList(userToCreate1, userToCreate2));
		Assert.assertTrue(userRepository.userExistsAndEnabled("demo", "test1"));
		Assert.assertTrue(userRepository.userExistsAndEnabled("demo", "test2"));
	}
}
