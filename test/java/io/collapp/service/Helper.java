
package io.collapp.service;

import io.collapp.service.UserRepository;

public class Helper {

	static int createUser(UserRepository ur, String provider, String userName) {
		return ur.createUser(provider, userName, null,null, null, true);
	}
}
