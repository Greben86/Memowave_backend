package dev.greben.memowave.rest

import dev.greben.memowave.dto.WordRequest
import dev.greben.memowave.dto.WordResponse
import dev.greben.memowave.service.WordService
import dev.greben.memowave.utils.Constants.AUTH_CLAIMS_LOGIN
import dev.greben.memowave.utils.Constants.AUTH_CLAIMS_ROLE
import dev.greben.memowave.utils.Constants.AUTH_CLAIMS_TYPE
import dev.greben.memowave.utils.Constants.AUTH_CLAIMS_USER_ID
import dev.greben.memowave.utils.TokenType
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.LocalDateTime
import java.util.*
import javax.crypto.SecretKey

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(scripts = ["/init.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@DisplayName("Тестирование API слов")
class WordControllerTest {

    companion object {
        val log = KotlinLogging.logger {}
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var wordService: WordService

    private lateinit var jwtToken: String
    // Уникальный ключ для генерации токена
    @Value("\${security.token.signing.key}")
    private val jwtSigningKey: String? = null

    @BeforeEach
    fun setUp() {
        jwtToken = generateToken("test@example.com", "USER", jwtSigningKey!!)
    }

    private fun generateToken(email: String, role: String, jwtSigningKey: String): String {
        val currentTime = Date(System.currentTimeMillis())
        val keyBytes = Decoders.BASE64.decode(jwtSigningKey)
        return Jwts.builder()
            .claims()
            .add(mapOf(AUTH_CLAIMS_TYPE to TokenType.ACCESS.name, AUTH_CLAIMS_USER_ID to 1L, AUTH_CLAIMS_LOGIN to email, AUTH_CLAIMS_ROLE to role))
            .and()
            .subject(email)
            .issuedAt(currentTime)
            .expiration(DateUtils.addMinutes(currentTime, 1))
            .signWith(Keys.hmacShaKeyFor(keyBytes), Jwts.SIG.HS256)
            .compact()
    }

    @Test
    fun `getAllWords should return list of word responses`() {
        // Given
        val wordResponse1 = WordResponse(
            id = 1L,
            categoryId = 1L,
            text = "word1",
            translate = "перевод1",
            example = "example1",
            imageUrl = "http://image1.jpg",
            repetitionCount = 0,
            nextRepetitionDate = LocalDateTime.now().plusDays(1),
            quality = 0,
            prevEaseFactor = 2.5,
            prevInterval = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val wordResponse2 = WordResponse(
            id = 2L,
            categoryId = 2L,
            text = "word2",
            translate = "перевод2",
            example = "example2",
            imageUrl = null,
            repetitionCount = 1,
            nextRepetitionDate = LocalDateTime.now().plusDays(2),
            quality = 0,
            prevEaseFactor = 2.5,
            prevInterval = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val expectedWords = listOf(wordResponse1, wordResponse2)

        Mockito.`when`(wordService.getAllWords()).thenReturn(expectedWords)

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/words")
            .header("Authorization", "Bearer $jwtToken")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].text").value("word1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].translate").value("перевод1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].text").value("word2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].translate").value("перевод2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].repetitionCount").value(0))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].repetitionCount").value(1))
    }

    @Test
    fun `getWordsByCategory should return words for given category id`() {
        // Given
        val categoryId = 1L
        val wordResponse = WordResponse(
            id = 1L,
            categoryId = 1L,
            text = "word1",
            translate = "перевод1",
            example = "example1",
            imageUrl = "http://image1.jpg",
            repetitionCount = 0,
            nextRepetitionDate = LocalDateTime.now().plusDays(1),
            quality = 0,
            prevEaseFactor = 2.5,
            prevInterval = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val expectedWords = listOf(wordResponse)

        Mockito.`when`(wordService.getWordsByCategory(categoryId)).thenReturn(expectedWords)

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/words?category=$categoryId")
            .header("Authorization", "Bearer $jwtToken")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].text").value("word1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].categoryId").value("1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].repetitionCount").value(0))
    }

    @Test
    fun `addWord should return created word response`() {
        // Given
        val wordResponse = WordResponse(
            id = 1L,
            categoryId = 1L,
            text = "new word",
            translate = "новое слово",
            example = "example",
            imageUrl = "http://image.jpg",
            repetitionCount = 0,
            nextRepetitionDate = LocalDateTime.now().plusDays(1),
            quality = 0,
            prevEaseFactor = 2.5,
            prevInterval = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        Mockito.`when`(wordService.saveWord(Mockito.any(WordRequest::class.java))).thenReturn(wordResponse)

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/words")
            .header("Authorization", "Bearer $jwtToken")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"categoryId\":1,\"text\":\"new word\",\"translate\":\"новое слово\",\"example\":\"example\",\"imageUrl\":\"http://image.jpg\",\"repetitionCount\":0,\"nextRepetitionDate\":\"${LocalDateTime.now().plusDays(1)}\"}")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.text").value("new word"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.translate").value("новое слово"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.repetitionCount").value(0))
    }

    @Test
    fun `addWords should return list of created word responses`() {
        // Given
        val wordResponse1 = WordResponse(
            id = 1L,
            categoryId = 1L,
            text = "word1",
            translate = "перевод1",
            example = "example1",
            imageUrl = "http://image1.jpg",
            repetitionCount = 0,
            nextRepetitionDate = LocalDateTime.now().plusDays(1),
            quality = 0,
            prevEaseFactor = 2.5,
            prevInterval = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val wordResponse2 = WordResponse(
            id = 2L,
            categoryId = 1,
            text = "word2",
            translate = "перевод2",
            example = "example2",
            imageUrl = null,
            repetitionCount = 0,
            nextRepetitionDate = LocalDateTime.now().plusDays(2),
            quality = 0,
            prevEaseFactor = 2.5,
            prevInterval = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val expectedResponses = listOf(wordResponse1, wordResponse2)

        Mockito.`when`(wordService.saveWords(Mockito.anyList())).thenReturn(expectedResponses)

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/words/batch")
            .header("Authorization", "Bearer $jwtToken")
            .contentType(MediaType.APPLICATION_JSON)
            .content("[{\"categoryId\":1,\"text\":\"word1\",\"translate\":\"перевод1\",\"example\":\"example1\",\"imageUrl\":\"http://image1.jpg\",\"repetitionCount\":0,\"nextRepetitionDate\":\"${LocalDateTime.now().plusDays(1)}\"},{\"categoryId\":1,\"text\":\"word2\",\"translate\":\"перевод2\",\"example\":\"example2\",\"imageUrl\":null,\"repetitionCount\":0,\"nextRepetitionDate\":\"${LocalDateTime.now().plusDays(2)}\"}]")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].text").value("word1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].text").value("word2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].repetitionCount").value(0))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].repetitionCount").value(0))
            .andDo { log.info { it.request.contentAsString } }
            .andDo { log.info { it.response.contentAsString } }
    }

    @Test
    fun `updateWord should return updated word response`() {
        // Given
        val wordId = 1L
        val wordResponse = WordResponse(
            id = 1L,
            categoryId = 1L,
            text = "updated word",
            translate = "обновленное слово",
            example = "updated example",
            imageUrl = "http://updated-image.jpg",
            repetitionCount = 1,
            nextRepetitionDate = LocalDateTime.now().plusDays(3),
            quality = 0,
            prevEaseFactor = 2.5,
            prevInterval = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        Mockito.`when`(wordService.updateWord(Mockito.eq(wordId), Mockito.any(WordRequest::class.java))).thenReturn(wordResponse)

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.put("/api/words/$wordId")
            .header("Authorization", "Bearer $jwtToken")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"categoryId\":1,\"text\":\"updated word\",\"translate\":\"обновленное слово\",\"example\":\"updated example\",\"imageUrl\":\"http://updated-image.jpg\",\"repetitionCount\":1,\"nextRepetitionDate\":\"${LocalDateTime.now().plusDays(3)}\"}")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.text").value("updated word"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.translate").value("обновленное слово"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.repetitionCount").value(1))
    }

    @Test
    fun `deleteWord should return no content`() {
        // Given
        val wordId = 1L

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/words/$wordId")
            .header("Authorization", "Bearer $jwtToken")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNoContent)
    }
}