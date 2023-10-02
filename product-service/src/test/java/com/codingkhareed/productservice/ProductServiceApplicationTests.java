package com.codingkhareed.productservice;

import com.codingkhareed.productservice.dto.ProductRequest;
import com.codingkhareed.productservice.dto.ProductResponse;
import com.codingkhareed.productservice.model.Product;
import com.codingkhareed.productservice.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

	@Container
	static MongoDBContainer mongoDBContainer =
			new MongoDBContainer(
					DockerImageName.parse("mongodb/mongodb-community-server:latest")
							.asCompatibleSubstituteFor("mongo")
			);

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ProductRepository productRepository;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
		dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@BeforeEach
	void clearData() {
		productRepository.deleteAll();
	}

	@Test
	void shouldCreateProduct() throws Exception {
		ProductRequest productRequest = getProductRequest();
		String productRequestString = objectMapper.writeValueAsString(productRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(productRequestString))
				.andExpect(status().isCreated());
		Assertions.assertEquals(1, productRepository.findAll().size());
	}

	@Test
	void shouldGetAllProducts() throws Exception {
		Product product = buildProduct();
		productRepository.save(product);

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/product"))
				.andExpect(status().isOk())
				.andReturn();

		String response = result.getResponse().getContentAsString();
		ProductResponse[] myObjects = objectMapper.readValue(response, ProductResponse[].class);

		Assertions.assertEquals(1, myObjects.length);
		Assertions.assertEquals(product.getId(), myObjects[0].getId());
		Assertions.assertEquals(product.getName(), myObjects[0].getName());
		Assertions.assertEquals(product.getDescription(), myObjects[0].getDescription());
		Assertions.assertEquals(product.getPrice(), myObjects[0].getPrice());
	}

	private Product buildProduct() {
		Product product = new Product();
		product.setName("Samsung Galaxy");
		product.setDescription("A decent phone");
		product.setPrice(BigDecimal.valueOf(932.47));

		return product;
	}

	private ProductRequest getProductRequest() {
		return ProductRequest.builder()
				.name("iPhone 13")
				.description("a very nice phone")
				.price(BigDecimal.valueOf(1200.32))
				.build();
	}
}
