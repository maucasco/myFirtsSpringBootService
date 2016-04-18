package poc.mcastro.sprinboot.restservice;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import poc.mcastro.sprinboot.restservice.model.AccountTO;
import poc.mcastro.sprinboot.restservice.model.GetMoneyTO;
import poc.mcastro.sprinboot.restservice.model.SendMoneyTO;
import poc.mcastro.sprinboot.restservice.repository.AccountsRepository;
import poc.mcastro.sprinboot.restservice.repository.TransactionRepository;

@RunWith(MockitoJUnitRunner.class)
@SpringApplicationConfiguration(classes = SpringBootRestApplication.class)
public class AccountBalanceTest {

	private MockMvc mockMvc;

	@Mock
	AccountsRepository accountRepository;
	@Mock
	TransactionRepository transactionRepository;
	@InjectMocks
	AccountBalanceService accountBalanceService;

	@Before
	public void setup() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(accountBalanceService).build();
	}

	private final AccountTO anAccount = new AccountTO("1", "1234", "Mauricio Castro", BigDecimal.valueOf(565));
	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), 
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	@Test
	public void should_return_account_if_exist() throws Exception {
		when(accountRepository.findByAccountNumber(anAccount.getAccountNumber())).thenReturn(anAccount);
		// then
		mockMvc.perform(get("/balance/getAccount").param("accountNumber", anAccount.getAccountNumber())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.customerName", is(anAccount.getCustomerName())));
	}

	@Test
	public void should_fail_if_account_not_exist() throws Exception {
		when(accountRepository.findByAccountNumber(anAccount.getAccountNumber())).thenReturn(null);
		// then
		mockMvc.perform(get("/balance/getAccount").param("accountNumber", anAccount.getAccountNumber())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	@Test
	public void should_return_balance_if_account_exist() throws Exception {
		when(accountRepository.findByAccountNumber(anAccount.getAccountNumber())).thenReturn(anAccount);
		// then
		mockMvc.perform(get("/balance/getAccountBalance").param("accountNumber", anAccount.getAccountNumber())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().string(is(anAccount.getAccountBalance().toString()))).andDo(print());
	}

	@Test
	public void should_fail_when_getting_balance_if_account_not_exist() throws Exception {
		when(accountRepository.findByAccountNumber(anAccount.getAccountNumber())).thenReturn(null);
		// then
		mockMvc.perform(get("/balance/getAccountBalance").param("accountNumber", anAccount.getAccountNumber())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	@Test
	public void should_add_new_balance_to_existing_account() throws Exception {
		// given some data to send
		SendMoneyTO sendMoneyTO = new SendMoneyTO();
		sendMoneyTO.setAccountTO(anAccount);
		final BigDecimal addedAccount = new BigDecimal(10);
		sendMoneyTO.setAmmount(addedAccount);

		// given the send data as json
		ObjectMapper mapper = new ObjectMapper();
			String accountAsJson = mapper.writeValueAsString(sendMoneyTO);

		// given an expected new balance
		final BigDecimal newBalance = anAccount.getAccountBalance().add(addedAccount);

		when(accountRepository.findByAccountNumber(anAccount.getAccountNumber())).thenReturn(anAccount);
		// then
		mockMvc.perform(put("/balance/addMoney").contentType(APPLICATION_JSON_UTF8).content(accountAsJson)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andDo(print())
				.andExpect(jsonPath("$.accountBalance", is(newBalance.intValue())));
	}

	@Test
	public void should_fail_when_adding_money_if_account_not_exist() throws Exception {
		// given some data to send
		SendMoneyTO sendMoneyTO = new SendMoneyTO();
		sendMoneyTO.setAccountTO(anAccount);
		final BigDecimal addedAccount = new BigDecimal(10);
		sendMoneyTO.setAmmount(addedAccount);

		// given the send data as json
		ObjectMapper mapper = new ObjectMapper();
		String accountAsJson = mapper.writeValueAsString(sendMoneyTO);

		when(accountRepository.findByAccountNumber(anAccount.getAccountNumber())).thenReturn(null);
		// then
		mockMvc.perform(put("/balance/addMoney").contentType(APPLICATION_JSON_UTF8).content(accountAsJson)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}
	
	@Test
	public void should_fail_getmoney_with_inssuficent_founds() throws Exception {
		// given some data to send
		GetMoneyTO getMoneyTO = new GetMoneyTO();
		getMoneyTO.setAccountTO(anAccount);
		final BigDecimal getMoney = new BigDecimal(10000);
		getMoneyTO.setAmmount(getMoney);
		getMoneyTO.setReason("Test money inssuficent");
		// given the send data as json
		ObjectMapper mapper = new ObjectMapper();
		String accountAsJson = mapper.writeValueAsString(getMoneyTO);

		when(accountRepository.findByAccountNumber(anAccount.getAccountNumber())).thenReturn(anAccount);
		// then
		mockMvc.perform(put("/balance/getMoney").contentType(MediaType.APPLICATION_JSON_UTF8).content(accountAsJson)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	@Test
	public void should_fail_getmoney_succesfully() throws Exception {
		// given some data to send
		GetMoneyTO getMoneyTO = new GetMoneyTO();
		getMoneyTO.setAccountTO(anAccount);
		final BigDecimal getMoney = new BigDecimal(100);
		getMoneyTO.setAmmount(getMoney);
		getMoneyTO.setReason("Test money inssuficent");
		
		 BigDecimal newBalance = anAccount.getAccountBalance();
		 newBalance=newBalance.subtract(getMoney);
		
		// given the send data as json
		ObjectMapper mapper = new ObjectMapper();
		String accountAsJson = mapper.writeValueAsString(getMoneyTO);
		

		when(accountRepository.findByAccountNumber(anAccount.getAccountNumber())).thenReturn(anAccount);
		// then
		mockMvc.perform(put("/balance/getMoney").contentType(MediaType.APPLICATION_JSON_UTF8).content(accountAsJson)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andDo(print())
		.andExpect(jsonPath("$.accountBalance", is(newBalance.intValue())));;
	}


}
