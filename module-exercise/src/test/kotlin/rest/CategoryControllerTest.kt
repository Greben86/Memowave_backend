package dev.greben.memowave.rest

import dev.greben.memowave.dto.CategoryRequest
import dev.greben.memowave.dto.CategoryResponse
import dev.greben.memowave.repository.CategoryRepository
import dev.greben.memowave.repository.WordRepository
import dev.greben.memowave.service.CategoryService
import dev.greben.memowave.service.JwtService
import dev.greben.memowave.service.PackService
import dev.greben.memowave.utils.Constants.AUTH_BEARER_PREFIX
import dev.greben.memowave.utils.Constants.AUTH_HEADER_NAME
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(CategoryController::class)
class CategoryControllerTest {

    // Уникальный ключ для генерации токена
    @Value("\${security.token.signing.key}")
    private val jwtSigningKey: String? = null

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var categoryService: CategoryService

    @MockBean
    private lateinit var packService: PackService

    @MockBean
    private lateinit var categoryRepository: CategoryRepository

    @MockBean
    private lateinit var wordRepository: WordRepository

    @MockBean
    private lateinit var jwtService: JwtService

    private lateinit var categoryRequest: CategoryRequest
    private lateinit var categoryResponse: CategoryResponse

    @BeforeEach
    fun setUp() {
        categoryRequest = CategoryRequest(
            name = "Test Category",
            description = "Test Description",
            color = "Blue",
            userId = 1L
        )

        categoryResponse = CategoryResponse(
            id = 1L,
            name = "Test Category",
            description = "Test Description",
            color = "Blue",
            userId = 1L
        )
    }

    @Test
    fun `getAllCategories should return list of categories`() {
        // Given
        Mockito.`when`(categoryService.getAllCategories())
            .thenReturn(listOf(categoryResponse))

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

    @Test
    fun `newCategory should create category and return response`() {
        // Given
        Mockito.`when`(categoryService.saveCategory(Mockito.any(CategoryRequest::class.java)))
            .thenReturn(categoryResponse)

        // When & Then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/categories/category/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test Category\",\"description\":\"Test Description\",\"color\":\"Blue\",\"userId\":1}")
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Category"))
    }

    @Test
    fun `updateCategory should update category and return response`() {
        // Given
        Mockito.`when`(categoryService.updateCategory(Mockito.eq(1L), Mockito.any(CategoryRequest::class.java)))
            .thenReturn(categoryResponse)

        // When & Then
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/categories/category/1/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated Category\",\"description\":\"Updated Description\",\"color\":\"Red\",\"userId\":1}")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Category"))
    }

    @Test
    fun `deleteCategory should delete category`() {
        // Given
        Mockito.doNothing().`when`(categoryService).deleteCategory(1L)

        // When & Then
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/categories/category/1/delete")
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent)
    }

    @Test
    fun `copyCategoryForUser should copy category and return response`() {
        // Given
        Mockito.`when`(categoryService.copyCategoryForUser(1L, 2L))
            .thenReturn(categoryResponse)

        // When & Then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/categories/category/1/copy/2")
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Category"))
    }
}