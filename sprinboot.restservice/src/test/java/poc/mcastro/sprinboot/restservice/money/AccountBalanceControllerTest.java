package poc.mcastro.sprinboot.restservice.money;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import poc.mcastro.sprinboot.restservice.money.money.account.AccountTO;
import poc.mcastro.sprinboot.restservice.money.money.account.AccountsRepository;
import poc.mcastro.sprinboot.restservice.money.money.account.GetMoneyTO;
import poc.mcastro.sprinboot.restservice.money.money.account.SendMoneyTO;
import poc.mcastro.sprinboot.restservice.money.money.rest.AccountBalanceController;
import poc.mcastro.sprinboot.restservice.money.money.transaction.TransactionRepository;
import poc.mcastro.sprinboot.restservice.money.money.transaction.TransactionTO;
import poc.mcastro.sprinboot.restservice.money.money.transaction.TransactionType;

import java.math.BigDecimal;
import java.nio.charset.Charset;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@SpringApplicationConfiguration(classes = SpringBootRestApplication.class)
public class AccountBalanceControllerTest {

    private MockMvc mockMvc;

    @Mock
    AccountsRepository accountRepository;
    @Mock
    TransactionRepository transactionRepository;
    @InjectMocks
    AccountBalanceController accountBalanceController;

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(accountBalanceController).build();
    }

    private final AccountTO anAccount = new AccountTO("1", "1234", "Mauricio Castro", BigDecimal.valueOf(565));

    private ObjectMapper anObjectMapper = new ObjectMapper();

    private static final MediaType APPLICATION_JSON_UTF8 = new MediaType(
                    MediaType.APPLICATION_JSON.getType(),
                    MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8")
    );

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
    public void should_add_money_to_existing_account() throws Exception {
        // given some data to send
        final BigDecimal amountToAdd = new BigDecimal(10);
        SendMoneyTO sendMoneyTO = new SendMoneyTO();
        sendMoneyTO.setAccountTO(anAccount);
        sendMoneyTO.setAmmount(amountToAdd);

        // given the sent data as json
        String accountAsJson = anObjectMapper.writeValueAsString(sendMoneyTO);

        // given an expected new balance
        final BigDecimal newBalance = anAccount.getAccountBalance().add(amountToAdd);

        when(accountRepository.findByAccountNumber(anAccount.getAccountNumber())).thenReturn(anAccount);
        // then
        mockMvc.perform(post("/balance/addMoney").contentType(APPLICATION_JSON_UTF8).content(accountAsJson)
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

        // given the sent data as json
        String accountAsJson = anObjectMapper.writeValueAsString(sendMoneyTO);

        when(accountRepository.findByAccountNumber(anAccount.getAccountNumber())).thenReturn(null);
        // then
        mockMvc.perform(post("/balance/addMoney").contentType(APPLICATION_JSON_UTF8).content(accountAsJson)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void should_fail_to_add_money_because_insufficient_founds() throws Exception {
        // given some data to send
        final BigDecimal anAmount = new BigDecimal(10000);
        GetMoneyTO getMoneyTO = new GetMoneyTO.GetMoneyBuilder()
                .withAccountTO(anAccount)
                .withAmount(anAmount)
                .withReason("Test money insufficient")
                .build();

        // given a dummy transaction
        final TransactionTO aDummyTransaction = new TransactionTO.TransactionBuilder().build();

        // given the sent data as json
        ObjectMapper mapper = new ObjectMapper();
        String accountAsJson = mapper.writeValueAsString(getMoneyTO);

        when(accountRepository.findByAccountNumber(anAccount.getAccountNumber())).thenReturn(anAccount);

        // then
        mockMvc.perform(post("/balance/getMoney").contentType(MediaType.APPLICATION_JSON_UTF8).content(accountAsJson)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());

        // no calls to transactionRepository::save ever done
        verify(transactionRepository, times(0)).save(aDummyTransaction);
    }

    @Test
    public void should_add_money_to_account() throws Exception {
        // given some data to send
        final BigDecimal anAmount = new BigDecimal(100);
        GetMoneyTO getMoneyTO = new GetMoneyTO.GetMoneyBuilder()
                .withAccountTO(anAccount)
                .withAmount(anAmount)
                .withReason("Test money")
                .build();

        // given a new expected balance
        BigDecimal expectedBalance = anAccount.getAccountBalance().subtract(anAmount);

        // given the send data as json
        String accountAsJson = anObjectMapper.writeValueAsString(getMoneyTO);

        when(accountRepository.findByAccountNumber(anAccount.getAccountNumber())).thenReturn(anAccount);

        // then
        mockMvc.perform(post("/balance/getMoney").contentType(MediaType.APPLICATION_JSON_UTF8).content(accountAsJson)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andDo(print())
                .andExpect(jsonPath("$.accountBalance", is(expectedBalance.intValue())));

    }


}
