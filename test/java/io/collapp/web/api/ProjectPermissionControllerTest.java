
package io.collapp.web.api;

import io.collapp.model.Permission;
import io.collapp.model.Role;
import io.collapp.model.RoleAndMetadata;
import io.collapp.service.EventEmitter;
import io.collapp.service.PermissionService;
import io.collapp.service.ProjectService;
import io.collapp.web.api.ProjectPermissionController;
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
public class ProjectPermissionControllerTest {

	@Mock
	private PermissionService permissionService;
	@Mock
	private EventEmitter eventEmitter;
	@Mock
	private ProjectService projectService;

	private ProjectPermissionController projectPermissionController;

	private final String projectShortName = "TEST";
	private final int projectId = 0;
	private String roleName = "ROLENAME";

	@Before
	public void prepare() {
		projectPermissionController = new ProjectPermissionController(permissionService, eventEmitter, projectService);
	}

	@Test
	public void assignUsersToRole() {
		Users usersToAdd = new Users();
		usersToAdd.setUserIds(Collections.singleton(1));
		projectPermissionController.assignUsersToRole(projectShortName, roleName, usersToAdd);

		verify(permissionService).assignRoleToUsersInProjectId(new Role(roleName), usersToAdd.getUserIds(), projectId);
		verify(eventEmitter).emitAssignRoleToUsers(roleName, projectShortName);
	}

	@Test
	public void createRole() {
		CreateRole newRole = new CreateRole();
		newRole.setName("name");
		projectPermissionController.createRole(projectShortName, newRole);

		verify(permissionService).createRoleInProjectId(new Role("name"), projectId);
		verify(eventEmitter).emitCreateRole(projectShortName);
	}

	@Test
	public void deleteRole() {
		when(permissionService.findRoleInProjectByName(projectId, roleName)).thenReturn(
				new RoleAndMetadata(roleName, true, false, true));
		projectPermissionController.deleteRole(projectShortName, roleName);
		verify(permissionService).deleteRoleInProjectId(new Role(roleName), projectId);
		verify(eventEmitter).emitDeleteRole(projectShortName);
	}

	@Test(expected = IllegalArgumentException.class)
	public void deleteUnremovableRole() {
		when(permissionService.findRoleInProjectByName(projectId, roleName)).thenReturn(
				new RoleAndMetadata(roleName, false, false, true));
		projectPermissionController.deleteRole(projectShortName, roleName);
	}

	@Test
	public void existingPermissions() {
		projectPermissionController.existingPermissions(projectShortName);
	}

	@Test
	public void findAllRolesAndRelatedPermissions() {
		projectPermissionController.findAllRolesAndRelatedPermissions(projectShortName);
		verify(permissionService).findAllRolesAndRelatedPermissionInProjectId(projectId);
	}

	@Test
	public void findUserByRole() {
		projectPermissionController.findUserByRole(projectShortName, roleName);
		verify(permissionService).findUserByRoleAndProjectId(new Role(roleName), projectId);
	}

	@Test
	public void removeRoleToUsers() {
		Users usersToRemove = new Users();
		usersToRemove.setUserIds(Collections.singleton(1));
		projectPermissionController.removeRoleToUsers(projectShortName, roleName, usersToRemove);
		verify(permissionService).removeRoleToUsersInProjectId(new Role(roleName), usersToRemove.getUserIds(),
				projectId);
		verify(eventEmitter).emitRemoveRoleToUsers(roleName, projectShortName);
	}

	@Test
	public void updateRole() {
		UpdateRole updateRole = new UpdateRole();
		updateRole.setPermissions(Collections.singleton(Permission.ADMINISTRATION));
		when(permissionService.findRoleInProjectByName(projectId, roleName)).thenReturn(
				new RoleAndMetadata(roleName, true, false, false));

		projectPermissionController.updateRole(projectShortName, roleName, updateRole);
		verify(permissionService).updatePermissionsToRoleInProjectId(new Role(roleName), updateRole.getPermissions(),
				projectId);
		verify(eventEmitter).emitUpdatePermissionsToRole(projectShortName);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateReadOnlyRole() {
		UpdateRole updateRole = new UpdateRole();
		updateRole.setPermissions(Collections.singleton(Permission.ADMINISTRATION));
		when(permissionService.findRoleInProjectByName(projectId, roleName)).thenReturn(
				new RoleAndMetadata(roleName, true, false, true));

		projectPermissionController.updateRole(projectShortName, roleName, updateRole);
	}
}
