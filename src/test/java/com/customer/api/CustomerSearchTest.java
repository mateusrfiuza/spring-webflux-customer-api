package com.customer.api;


import com.customer.api.domain.Customer;
import com.customer.api.domain.repository.CustomerRepository;
import com.customer.api.dataprovider.repository.exception.RegisterNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CustomerApplication.class})
@AutoConfigureWebTestClient
public class CustomerSearchTest {

    @MockBean
    CustomerRepository repository;

    @Autowired
    private WebTestClient client;

    @Test
    @WithMockUser
    public void customer_not_registered() {

        //Given
        Mockito
                .when(repository.findById(any()))
                .thenReturn(Mono.error(RegisterNotFoundException::new));

        // When
        var response = client.get()
                .uri("/customers/0fea3a30-d699-440e-9309-0e513193db5e")
                .exchange();

        // Then
        response
                .expectStatus()
                .isNotFound();
    }

    @Test
    @WithMockUser
    public void customer_registered() {

        //Given
        var customer = new Customer(UUID.fromString("0fea3a30-d699-440e-9309-0e513193db5e"),"mateus@mail.com", "Mateus");
        var monoResult = Mono.just(customer);
        Mockito
                .when(repository.findById(any()))
                .thenReturn(monoResult);

        // When
        var response = client.get()
                .uri("/customers/0fea3a30-d699-440e-9309-0e513193db5e")
                .exchange();

        // Then
        response
            .expectStatus()
            .is2xxSuccessful()
            .expectBody()
            .jsonPath("$.id").isEqualTo(customer.getId().toString())
            .jsonPath("$.email").isEqualTo(customer.getEmail())
            .jsonPath("$.name").isEqualTo(customer.getName());
    }

}
