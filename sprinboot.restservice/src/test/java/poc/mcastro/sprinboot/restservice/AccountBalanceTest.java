package poc.mcastro.sprinboot.restservice;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.matchers.Any;

import static org.mockito.Matchers.isNull;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import poc.mcastro.sprinboot.restservice.model.AccountTO;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import poc.mcastro.sprinboot.restservice.repository.AccountsRepository;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(MockitoJUnitRunner.class)
@SpringApplicationConfiguration(classes= SpringBootRestApplication.class)
public class AccountBalanceTest {
	private MockMvc mockMvc;
	@Mock
	AccountsRepository accountRepository;
	@InjectMocks
	AccountBalanceService accountBalanceService;
	 @Before
	    public void setup() throws Exception {
		 mockMvc = MockMvcBuilders.standaloneSetup(accountBalanceService).build();
	    }


	@Test
	public void should_return_account() throws Exception{
		final AccountTO accountTO = new AccountTO("1","1234",
				"Mauricio Castro", BigDecimal.valueOf(565));

		when(accountRepository.findByAccountNumber("1234")).thenReturn(accountTO);
		mockMvc.perform(post("/balance/getAccount").param("accountNumber", "1234")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(
						jsonPath("$.customerName", is(accountTO.getCustomerName())));
	}

	@Test
	public void should_return_unkhown_account() throws Exception{
		when(accountRepository.findByAccountNumber("123433")).thenReturn(null);
		mockMvc.perform(post("/balance/getAccount").param("accountNumber", "123433")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andDo(print());
	}

}
