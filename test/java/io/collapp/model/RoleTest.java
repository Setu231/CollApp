
package io.collapp.model;

import org.junit.Assert;
import org.junit.Test;

public class RoleTest {

	@Test
	public void testEquality() {
		Role r = new Role("test");
		Role r2 = new Role("TEST");
		Role r3 = new Role("NOT_TEST");

		Assert.assertFalse(r.equals(null));
		Assert.assertFalse(r.equals(new Object()));

		Assert.assertEquals(r, r2);
		Assert.assertNotEquals(r, r3);

		Assert.assertEquals(r.hashCode(), r2.hashCode());
		Assert.assertNotEquals(r.hashCode(), r3.hashCode());
	}
}
