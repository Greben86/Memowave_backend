package dev.greben.memowave.rest

import dev.greben.memowave.utils.Constants.AUTH_BEARER_PREFIX
import dev.greben.memowave.utils.Constants.AUTH_HEADER_NAME
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(scripts = ["/init.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@DisplayName("Тестирование API категорий")
class CategoryControllerTest {

    // Уникальный ключ для генерации токена
    @Value("\${security.token.signing.key}")
    private val jwtSigningKey: String? = null

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Order(0)
    @Test
    fun `newCategory should create category and return response`() {
        // When & Then
        val token: String = TestUtils.generateToken("example@mail.ru", "ROLE_ADMIN", jwtSigningKey!!)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/categories/category/new")
                .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test Category\",\"description\":\"Test Description\",\"color\":\"Blue\",\"userId\":1}")
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Category"))
    }

    @Order(1)
    @Test
    fun `getAllCategories should return list of categories`() {
        // When & Then
        val token: String = TestUtils.generateToken("example@mail.ru", "ROLE_ADMIN", jwtSigningKey!!)
        mockMvc.perform(MockMvcRequestBuilders.get("/api/categories")
            .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name").value("Test Category"))
    }

    @Order(2)
    @Test
    fun `updateCategory should update category and return response`() {
        // When & Then
        val token: String = TestUtils.generateToken("example@mail.ru", "ROLE_ADMIN", jwtSigningKey!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/categories/category/1/update")
                .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated Category\",\"description\":\"Updated Description\",\"color\":\"Red\",\"userId\":0}")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Updated Category"))
    }

    @Order(3)
    @Test
    fun `copyCategoryForUser should copy category and return response`() {
        // When & Then
        val token: String = TestUtils.generateToken("example@mail.ru", "ROLE_ADMIN", jwtSigningKey!!)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/categories/category/1/copy/2")
                .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Updated Category"))
    }

    @Order(4)
    @Test
    fun `deleteCategory should delete category`() {
        // When & Then
        val token: String = TestUtils.generateToken("example@mail.ru", "ROLE_ADMIN", jwtSigningKey!!)
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/categories/category/1/delete")
                .header(AUTH_HEADER_NAME, AUTH_BEARER_PREFIX + token)
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent)
    }
}