package poc.mcastro.sprinboot.restservice.money;

import static com.jayway.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

import static org.hamcrest.Matchers.greaterThan;
import poc.mcastro.sprinboot.restservice.money.money.account.AccountTO;
import poc.mcastro.sprinboot.restservice.money.money.account.SendMoneyTO;
import poc.mcastro.sprinboot.restservice.money.money.transaction.TransactionStatus;
import poc.mcastro.sprinboot.restservice.money.money.transaction.TransactionTO;
import poc.mcastro.sprinboot.restservice.money.money.transaction.TransactionType;

@WebIntegrationTest(randomPort = true )
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringBootRestApplication.class)
public class AccountBalanceIntegrationTest {
    private final AccountTO anAccount = new AccountTO("1", "1234", "Mauricio Castro", BigDecimal.valueOf(565));
	 @Value("${local.server.port}")
	    int port;

	    @Before()
	    public void setUp() {
	        RestAssured.port = port;
	    }
	
    @Test
    public void should_return_account_if_exist() throws Exception {
    	String name= "Mauricio Castro";
    	
    	String value=  given().
    						param("accountNumber", "1234").
    				   when().
    				   		get("/balance/getAccount").
    				   then().assertThat().
    				   		statusCode(OK.value()).extract().path("customerName");
    						assertThat(value).as(name);
    	
    }
    
    @Test
    public void should_return_account_balance() throws Exception {
    	String name= "Mauricio Castro";
    	
    	 given().
    						param("accountNumber", "1234").
    				   when().
    				   		get("/balance/getAccountBalance").
    				   then().assertThat().
    				   		statusCode(OK.value()).body(greaterThan("0"));
    						
    	
    }
    
    
    @Test
    public void should_add_money_to_existing_account() throws Exception {
        // given some data to send
        final BigDecimal amountToAdd = new BigDecimal(10);
        SendMoneyTO sendMoneyTO = new SendMoneyTO();
        sendMoneyTO.setAccountTO(anAccount);
        sendMoneyTO.setAmmount(amountToAdd);

        // given the sent data as json
        String accountAsJson = anObjectMapper().writeValueAsString(sendMoneyTO);

        // given an expected new balance
        final BigDecimal newBalance = anAccount.getAccountBalance().add(amountToAdd);

        Response balance=given().body(accountAsJson).with().contentType(ContentType.JSON).
        when().post("/balance/addMoney").
        then().contentType(ContentType.JSON).assertThat().statusCode(OK.value()).extract().response();
        final TransactionTO insertedTransaction =
                anObjectMapper().readValue(balance.asString(), TransactionTO.class);
        
        assertThat(insertedTransaction.getTransactionStatus()).isEqualTo(TransactionStatus.APPROVED);
        assertThat(insertedTransaction.getType()).isEqualTo(TransactionType.CREDIT);
    }

    
    private ObjectMapper anObjectMapper() {
        ObjectMapper anObjectMapper = new ObjectMapper();
        anObjectMapper.registerModule(new JavaTimeModule());
        return anObjectMapper;
    }
}
