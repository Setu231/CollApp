
package io.collapp.web.api;

import io.collapp.model.Permission;
import io.collapp.model.Role;
import io.collapp.model.RoleAndMetadata;
import io.collapp.service.EventEmitter;
import io.collapp.service.PermissionService;
import io.collapp.web.api.PermissionController;
import io.collapp.web.api.model.CreateRole;
import io.collapp.web.api.model.UpdateRole;
import io.collapp.web.api.model.Users;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PermissionControllerTest {

	@Mock
	private PermissionService permissionService;
	@Mock
	private EventEmitter eventEmitter;

	private PermissionController permissionController;

	private String roleName = "ROLENAME";

	@Before
	public void prepare() {
		permissionController = new PermissionController(permissionService, eventEmitter);
	}

	@Test
	public void assignUsersToRole() {
		Users usersToAdd = new Users();
		usersToAdd.setUserIds(Collections.singleton(1));
		permissionController.assignUsersToRole(roleName, usersToAdd);

		verify(permissionService).assignRoleToUsers(new Role(roleName), usersToAdd.getUserIds());
		verify(eventEmitter).emitAssignRoleToUsers(roleName);
	}

	@Test
	public void createRole() {
		CreateRole newRole = new CreateRole();
		newRole.setName("name");
		permissionController.createRole(newRole);

		verify(permissionService).createRole(new Role("name"));
		verify(eventEmitter).emitCreateRole();
	}

	@Test
	public void deleteRole() {
		when(permissionService.findRoleByName(roleName)).thenReturn(
				new RoleAndMetadata(roleName, true, true, true));
		permissionController.deleteRole(roleName);
		verify(permissionService).deleteRole(new Role(roleName));
		verify(eventEmitter).emitDeleteRole();
	}

	@Test(expected = IllegalArgumentException.class)
	public void deleteUnremovableRole() {
		when(permissionService.findRoleByName(roleName)).thenReturn(
				new RoleAndMetadata(roleName, false, true, true));
		permissionController.deleteRole(roleName);
	}

	@Test
	public void existingPermissions() {
		permissionController.existingPermissions();
	}

	@Test
	public void findAllRolesAndRelatedPermissions() {
		permissionController.findAllRolesAndRelatedPermissions();
		verify(permissionService).findAllRolesAndRelatedPermission();
	}

	@Test
	public void findUserByRole() {
		permissionController.findUserByRole(roleName);
		verify(permissionService).findUserByRole(new Role(roleName));
	}

	@Test
	public void removeRoleToUsers() {
		Users usersToRemove = new Users();
		usersToRemove.setUserIds(Collections.singleton(1));
		permissionController.removeRoleToUsers(roleName, usersToRemove);
		verify(permissionService).removeRoleToUsers(new Role(roleName), usersToRemove.getUserIds());
		verify(eventEmitter).emitRemoveRoleToUsers(roleName);
	}

	@Test
	public void updateRole() {

		when(permissionService.findRoleByName(roleName)).thenReturn(
				new RoleAndMetadata(roleName, false, false, false));

		UpdateRole updateRole = new UpdateRole();
		updateRole.setPermissions(Collections.singleton(Permission.ADMINISTRATION));
		permissionController.updateRole(roleName, updateRole);
		verify(permissionService).updatePermissionsToRole(new Role(roleName), updateRole.getPermissions());
		verify(eventEmitter).emitUpdatePermissionsToRole();
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateReadOnlyRole() {

		when(permissionService.findRoleByName(roleName)).thenReturn(
				new RoleAndMetadata(roleName, false, true, true));

		UpdateRole updateRole = new UpdateRole();
		updateRole.setPermissions(Collections.singleton(Permission.ADMINISTRATION));
		permissionController.updateRole(roleName, updateRole);
	}
}
