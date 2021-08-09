package com.greenfoxacademy.bankingbackofficebackendservice.apiTests;

import static com.greenfoxacademy.bankingbackofficebackendservice.apiTests.BranchAPITest.asJsonString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.greenfoxacademy.bankingbackofficebackendservice.models.Account;
import com.greenfoxacademy.bankingbackofficebackendservice.models.dtos.IbanTransferDTO;
import com.greenfoxacademy.bankingbackofficebackendservice.models.dtos.TransferDTO;
import com.greenfoxacademy.bankingbackofficebackendservice.services.TransferServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class TransferAPITest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private TransferServiceImpl transferServiceImpl;

  @Test
  @DisplayName("Test POST '/api/accounts/{id}/internal-transaction' 204")
  public void internalTransactionTest() throws Exception {

    Account dummyAccount = new Account();
    dummyAccount.setId(1);

    TransferDTO transferDTO = new TransferDTO();
    transferDTO.setTargetAccountNumber(dummyAccount.getAccountNumber());
    transferDTO.setAmount(1000.);

    mockMvc.perform(post("/api/accounts/1/internal-transaction")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(asJsonString(transferDTO)))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("Test POST '/api/accounts/{id}/internal-transaction' when DTO not valid")
  public void invalidTransferDTOTest() throws Exception{

    Account dummyAccount = new Account();
    dummyAccount.setId(1);

    TransferDTO transferDTO = new TransferDTO();
    transferDTO.setTargetAccountNumber("");
    transferDTO.setAmount(1000.);

    mockMvc.perform(post("/api/accounts/1/internal-transaction")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(asJsonString(transferDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Test POST '/api/accounts/{id}/external-transaction' 204")
  public void externalTransactionTest() throws Exception {

    Account dummyAccount = new Account();
    dummyAccount.setId(1);

    IbanTransferDTO transferDTO = new IbanTransferDTO();
    transferDTO.setTargetAccountIban("NL60ABNA6044978370");
    transferDTO.setCurrency("USD");
    transferDTO.setAmount(200.);

    mockMvc.perform(post("/api/accounts/1/external-transaction")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(asJsonString(transferDTO)))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("Test POST '/api/accounts/{id}/external-transaction' when DTO not valid")
  public void invalidIbanTransferDTOTest() throws Exception{

    Account dummyAccount = new Account();
    dummyAccount.setId(1);

    IbanTransferDTO transferDTO = new IbanTransferDTO();
    transferDTO.setTargetAccountIban(dummyAccount.getAccountNumber());
    transferDTO.setCurrency("USD");
    transferDTO.setAmount(1000.);

    mockMvc.perform(post("/api/accounts/1/external-transaction")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(asJsonString(transferDTO)))
        .andExpect(status().isBadRequest());
  }
}
