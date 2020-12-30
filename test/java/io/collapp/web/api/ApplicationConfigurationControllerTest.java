
package io.collapp.web.api;

import io.collapp.model.ConfigurationKeyValue;
import io.collapp.model.Key;
import io.collapp.model.MailConfig;
import io.collapp.service.ConfigurationRepository;
import io.collapp.service.Ldap;
import io.collapp.web.api.ApplicationConfigurationController;
import io.collapp.web.api.model.Conf;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationConfigurationControllerTest {

	@Mock
	private ConfigurationRepository configurationRepository;
	@Mock
	private Ldap ldap;
	@Mock
	private Map<String, String> ldapParams;
	@Mock
	private HttpServletRequest req;

	private ApplicationConfigurationController applConfCtrl;

	@Before
	public void prepare() {
		applConfCtrl = new ApplicationConfigurationController(configurationRepository, ldap);
	}

	@Test
	public void findAll() {
		when(configurationRepository.findAll()).thenReturn(
				Arrays.asList(new ConfigurationKeyValue(Key.TEST_PLACEHOLDER, "value")));
		applConfCtrl.findAll();
		verify(configurationRepository).findAll();
	}

	@Test
	public void findByKey() {
		when(configurationRepository.hasKeyDefined(Key.SETUP_COMPLETE)).thenReturn(true);
		when(configurationRepository.getValue(Key.SETUP_COMPLETE)).thenReturn("true");
		ConfigurationKeyValue findByKey = applConfCtrl.findByKey(Key.SETUP_COMPLETE);
		Assert.assertEquals(Key.SETUP_COMPLETE, findByKey.getFirst());
		Assert.assertEquals("true", findByKey.getSecond());
		verify(configurationRepository).hasKeyDefined(Key.SETUP_COMPLETE);
		verify(configurationRepository).getValue(Key.SETUP_COMPLETE);

	}

	@Test
	public void findByKeyNotFound() {
		ConfigurationKeyValue findByKey = applConfCtrl.findByKey(Key.SETUP_COMPLETE);
		Assert.assertEquals(Key.SETUP_COMPLETE, findByKey.getFirst());
		Assert.assertNull(findByKey.getSecond());
		verify(configurationRepository).hasKeyDefined(Key.SETUP_COMPLETE);
	}

	@Test
	public void setKeyValue() {
		Conf conf = new Conf();
		conf.setToUpdateOrCreate(Arrays.asList(new ConfigurationKeyValue(Key.SETUP_COMPLETE, "true")));
		applConfCtrl.setKeyValue(conf);
		verify(configurationRepository).updateOrCreate(conf.getToUpdateOrCreate());
	}

	@Test
	public void checkLdap() {
		applConfCtrl.checkLdap(ldapParams);
		for (String s : Arrays.asList("serverUrl", "managerDn", "managerPassword", "userSearchBase",
				"userSearchFilter", "username", "password")) {
			verify(ldapParams).get(s);
		}
	}

	@Test
	public void checkEmail() {
		MailConfig mc = mock(MailConfig.class);
		applConfCtrl.checkSmtp(mc, "test@test.test");
		verify(mc).send(any(String.class), any(String.class), any(String.class));
	}
}
