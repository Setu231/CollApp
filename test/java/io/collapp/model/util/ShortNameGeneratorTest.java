
package io.collapp.model.util;

import org.junit.Assert;
import org.junit.Test;

import io.collapp.model.util.ShortNameGenerator;

public class ShortNameGeneratorTest {

	@Test
	public void testShortNameGenerator() {

		Assert.assertEquals(null, ShortNameGenerator.generateShortNameFrom(null));
		Assert.assertEquals("", ShortNameGenerator.generateShortNameFrom(""));
		Assert.assertEquals("   ", ShortNameGenerator.generateShortNameFrom("   "));

		Assert.assertEquals("TEST", ShortNameGenerator.generateShortNameFrom("test"));

		Assert.assertEquals("TESTTEST", ShortNameGenerator.generateShortNameFrom("test test"));

		Assert.assertEquals("TESTTE", ShortNameGenerator.generateShortNameFrom("testtest"));

		Assert.assertEquals("TESTTE", ShortNameGenerator.generateShortNameFrom("testtesttesttesttesttesttesttesttesttesttesttest"));

		//
		Assert.assertEquals("TESTDHD", ShortNameGenerator.generateShortNameFrom("test DerpoHurpoDurr"));

		Assert.assertEquals("TESTTEST", ShortNameGenerator.generateShortNameFrom("test test test test test"));

		// token has length <= 4
		Assert.assertEquals("TESTTEST", ShortNameGenerator.generateShortNameFrom("test TeSt Test test tEst"));

		Assert.assertEquals("TESTSTES", ShortNameGenerator.generateShortNameFrom("testa TeSta Testa testa tEsta"));
	}

}
