
package io.collapp.model;

import io.collapp.model.CardLabel.LabelDomain;
import io.collapp.model.CardLabel.LabelType;
import org.junit.Assert;
import org.junit.Test;

public class CardLabelTest {

	@Test
	public void set() {

		CardLabel cl = new CardLabel(42, 42, false, LabelType.STRING, LabelDomain.USER, "label", 42);

		CardLabel cl1 = cl.color(84);
		Assert.assertEquals(84, cl1.getColor());

		CardLabel cl2 = cl1.name("label2");
		Assert.assertEquals("label2", cl2.getName());

		CardLabel cl3 = cl2.set("label3", LabelType.STRING, 0);
		Assert.assertEquals("label3", cl3.getName());
		Assert.assertEquals(0, cl3.getColor());
	}

	@Test
	public void equality() {

		CardLabel cl1 = new CardLabel(42, 42, false, LabelType.STRING, LabelDomain.USER, "label", 42);
		CardLabel cl2 = new CardLabel(42, 42, false, LabelType.STRING, LabelDomain.USER, "label", 42);
		CardLabel cl3 = new CardLabel(42, 42, false, LabelType.STRING, LabelDomain.USER, "label2", 42);

		Assert.assertFalse(cl1.equals(null));
		Assert.assertFalse(cl1.equals(new Object()));

		Assert.assertEquals(cl1, cl2);
		Assert.assertEquals(cl2, cl1);
		Assert.assertNotEquals(cl2, cl3);
		Assert.assertNotEquals(cl3, cl2);
		Assert.assertNotEquals(cl1, cl3);

		Assert.assertEquals(cl1.hashCode(), cl2.hashCode());
		Assert.assertNotEquals(cl1.hashCode(), cl3.hashCode());
	}
}
