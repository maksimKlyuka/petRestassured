import org.junit.jupiter.api.Test
import io.restassured.RestAssured
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestMethodOrder
import org.hamcrest.Matchers

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserStoryTest {

    init {
        // Добавляем эту конфигурацию для логов
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
        RestAssured.baseURI = "https://dummyapi.io/data/v1"
    }

    private var idUser1 = "68f1275efc305325fda8eb12"
    private var idUser2 = ""
    private var idPost = ""
    private var idComment = ""
    private var email = "sivanov28@gmail.com"
    private var emailU2 = "mivanova28@gmail.com"
    private var timeUserCreate = "" // общая
    private var timeUserUpdate = "" // общая
    private var timePostCreate = ""
    private var timeCommentCreate = ""

    @Test
    @Order(1)
    fun createUser1() {
        val requestBody = """
            {
            "title": "dr",
            "firstName": "Саша",
            "lastName": "Иванов",
            "email": "$email",
            "dateOfBirth": "11/12/1992",
            "phone": "+79998765432"
            }
        """.trimIndent()
        timeUserCreate = java.time.Instant.now().toString().substring(0, 16)

        val response = RestAssured.given()
            .contentType("application/json")
            .body(requestBody)
            .header("Content-Type", "application/json")
            .header("app-id", "64ba6e63c4e32672bf5e6891") // спрячу потом
            .log().all()

            .`when`() // Выполнение запроса
            .post("/user/create")

            .then()
            .log().all()
            .statusCode(200)
            .extract()
            .response()

            try { // можно без try тут обойтись, написал для практики
                SoftAssertions().apply {
                    assertThat(response.header("Content-Type")).isEqualTo("application/json; charset=utf-8")

                    assertThat(response.path<String>("id")).isNotNull()
                    assertThat(response.path<String>("title")).isEqualTo("dr")
                    assertThat(response.path<String>("firstName")).isEqualTo("Саша")
                    assertThat(response.path<String>("lastName")).isEqualTo("Иванов")
                    assertThat(response.path<String>("email")).isEqualTo(email)
                    assertThat(response.path<String>("dateOfBirth")).isEqualTo("1992-11-12T00:00:00.000Z")
                    assertThat(response.path<String>("phone")).isEqualTo("+79998765432")
                    assertThat(response.path<String>("registerDate")).startsWith(timeUserCreate)
                    assertThat(response.path<String>("updatedDate")).startsWith(timeUserCreate)
                }.assertAll()
            } finally{
            idUser1 = response.path<String>("id")
        }
    }

    @Test
    @Order(2)
    fun checkCreateUser1() {
        val response = RestAssured.given()
            .contentType("application/json")
            .header("Content-Type", "application/json")
            .header("app-id", "64ba6e63c4e32672bf5e6891") // спрячу потом
            .log().all()

            .`when`()
            .get("/user/$idUser1")

            .then()
            .statusCode(200)
            .log().all()
            .extract()
            .response()

        SoftAssertions().apply {
            assertThat(response.header("Content-Type")).isEqualTo("application/json; charset=utf-8")

            assertThat(response.path<String>("id")).isEqualTo(idUser1)
            assertThat(response.path<String>("title")).isEqualTo("dr")
            assertThat(response.path<String>("firstName")).isEqualTo("Саша")
            assertThat(response.path<String>("lastName")).isEqualTo("Иванов")
            assertThat(response.path<String>("email")).isEqualTo(email)
            assertThat(response.path<String>("dateOfBirth")).isEqualTo("1992-11-12T00:00:00.000Z")
            assertThat(response.path<String>("phone")).isEqualTo("+79998765432")
            assertThat(response.path<String>("registerDate")).startsWith(timeUserCreate)
            assertThat(response.path<String>("updatedDate")).startsWith(timeUserCreate)
        }.assertAll()
    }

    @Test
    @Order(3)
    fun updateUser1() {
        val requestBody = """
            {
            "title": "mr",
            "firstName": "Александр",
            "lastName": "Иванов-Прокофьев",
            "dateOfBirth": "05/06/1988",
            "phone": "88005553535",
            "location": {
                "street": "Фишева",
                "city": "Улан-Уде",
                "state": "Республика Бурятия",
                "country": "Россия",
                "timezone": "+8:00"
                }
            }
        """.trimIndent()
        val response = RestAssured.given()
            .contentType("application/json")
            .body(requestBody)
            .header("Content-Type", "application/json")
            .header("app-id", "64ba6e63c4e32672bf5e6891") // спрячу потом
            .log().all()

            .`when`()
            .put("/user/$idUser1")

            .then()
            .log().all()
            .statusCode(200)
            .extract()
            .response()
        timeUserUpdate = java.time.Instant.now().toString().substring(0, 16)
        SoftAssertions().apply {
            assertThat(response.header("Content-Type")).isEqualTo("application/json; charset=utf-8")

            assertThat(response.path<String>("id")).isEqualTo(idUser1)
            assertThat(response.path<String>("title")).isEqualTo("mr")
            assertThat(response.path<String>("firstName")).isEqualTo("Александр")
            assertThat(response.path<String>("lastName")).isEqualTo("Иванов-Прокофьев")
            assertThat(response.path<String>("email")).isEqualTo(email)
            assertThat(response.path<String>("dateOfBirth")).isEqualTo("1988-05-06T00:00:00.000Z")
            assertThat(response.path<String>("phone")).isEqualTo("88005553535")
            assertThat(response.path<String>("location.street")).isEqualTo("Фишева")
            assertThat(response.path<String>("location.city")).isEqualTo("Улан-Уде")
            assertThat(response.path<String>("location.state")).isEqualTo("Республика Бурятия")
            assertThat(response.path<String>("location.country")).isEqualTo("Россия")
            assertThat(response.path<String>("location.timezone")).isEqualTo("+8:00")
            assertThat(response.path<String>("registerDate")).startsWith(timeUserCreate)
            assertThat(response.path<String>("updatedDate")).startsWith(timeUserUpdate)
        }.assertAll()
    }

    @Test
    @Order(4)
    fun checkUpdateUser1() {
        val response = RestAssured.given()
            .contentType("application/json")
            .header("Content-Type", "application/json")
            .header("app-id", "64ba6e63c4e32672bf5e6891") // спрячу потом
            .log().all()

            .`when`()
            .get("/user/$idUser1")

            .then()
            .statusCode(200)
            .log().all()
            .extract()
            .response()

        SoftAssertions().apply {
            assertThat(response.header("Content-Type")).isEqualTo("application/json; charset=utf-8")

            assertThat(response.path<String>("id")).isEqualTo(idUser1)
            assertThat(response.path<String>("title")).isEqualTo("mr")
            assertThat(response.path<String>("firstName")).isEqualTo("Александр")
            assertThat(response.path<String>("lastName")).isEqualTo("Иванов-Прокофьев")
            assertThat(response.path<String>("email")).isEqualTo(email)
            assertThat(response.path<String>("dateOfBirth")).isEqualTo("1988-05-06T00:00:00.000Z")
            assertThat(response.path<String>("phone")).isEqualTo("88005553535")
            assertThat(response.path<String>("location.street")).isEqualTo("Фишева")
            assertThat(response.path<String>("location.city")).isEqualTo("Улан-Уде")
            assertThat(response.path<String>("location.state")).isEqualTo("Республика Бурятия")
            assertThat(response.path<String>("location.country")).isEqualTo("Россия")
            assertThat(response.path<String>("registerDate")).startsWith(timeUserCreate)
            assertThat(response.path<String>("updatedDate")).startsWith(timeUserUpdate)
        }.assertAll()
    }

    @Test
    @Order(5)
    fun createPostUser1() {
        val requestBody = """
            {
            "text": "Я сегодня бегал",
            "likes": 0,
            "tags": ["#AXE", "#Реклама"],
            "owner": "$idUser1"
            }
        """.trimIndent()

        val response = RestAssured.given()
            .contentType("application/json")
            .body(requestBody)
            .header("Content-Type", "application/json")
            .header("app-id", "64ba6e63c4e32672bf5e6891") // спрячу потом
            .log().all()

            .`when`()
            .post("/post/create")

            .then()
            .log().all()
            .statusCode(200)
            .extract()
            .response()

        idPost = response.path("id")
        timePostCreate = java.time.Instant.now().toString().substring(0, 16)

        SoftAssertions().apply {
            assertThat(response.header("Content-Type")).isEqualTo("application/json; charset=utf-8")

            assertThat(response.path<String>("id")).isNotNull()
            assertThat(response.path<Int>("likes")).isEqualTo(0)
            assertThat(response.path<List<String>>("tags")).containsExactly("#AXE", "#Реклама")
            assertThat(response.path<String>("text")).isEqualTo("Я сегодня бегал")
            assertThat(response.path<String>("publishDate")).startsWith(timePostCreate)
            assertThat(response.path<String>("updatedDate")).startsWith(timePostCreate)
            assertThat(response.path<String>("owner.id")).isEqualTo(idUser1)
            assertThat(response.path<String>("owner.title")).isEqualTo("mr")
            assertThat(response.path<String>("owner.firstName")).isEqualTo("Александр")
            assertThat(response.path<String>("owner.lastName")).isEqualTo("Иванов-Прокофьев")
        }.assertAll()
    }

    @Test
    @Order(6)
    fun checkCreatePostUser1() {
        val response = RestAssured.given()
            .contentType("application/json")
            .header("Content-Type", "application/json")
            .header("app-id", "64ba6e63c4e32672bf5e6891") // спрячу потом
            .log().all()

            .`when`()
            .get("/post/$idPost")

            .then()
            .log().all()
            .statusCode(200)
            .extract()
            .response()

        SoftAssertions().apply {
            assertThat(response.header("Content-Type")).isEqualTo("application/json; charset=utf-8")

            assertThat(response.path<String>("id")).isNotNull()
            assertThat(response.path<Int>("likes")).isEqualTo(0)
            assertThat(response.path<List<String>>("tags")).containsExactly("#AXE", "#Реклама")
            assertThat(response.path<String>("text")).isEqualTo("Я сегодня бегал")
            assertThat(response.path<String>("publishDate")).startsWith(timePostCreate)
            assertThat(response.path<String>("updatedDate")).startsWith(timePostCreate)
            assertThat(response.path<String>("owner.id")).isEqualTo(idUser1)
            assertThat(response.path<String>("owner.title")).isEqualTo("mr")
            assertThat(response.path<String>("owner.firstName")).isEqualTo("Александр")
            assertThat(response.path<String>("owner.lastName")).isEqualTo("Иванов-Прокофьев")
        }.assertAll()
    }

    @Test
    @Order(7)
    fun createUser2() {
        val requestBody = """
            {
            "title": "ms",
            "firstName": "Маша",
            "lastName": "Иванова",
            "email": "$emailU2",
            "dateOfBirth": "05/06/1994",
            "phone": "+79998765566"
            }
        """.trimIndent()
        timeUserCreate = java.time.Instant.now().toString().substring(0, 16)

        val response = RestAssured.given()
            .contentType("application/json")
            .body(requestBody)
            .header("Content-Type", "application/json")
            .header("app-id", "64ba6e63c4e32672bf5e6891") // спрячу потом
            .log().all()

            .`when`() // Выполнение запроса
            .post("/user/create")

            .then()
            .log().all()
            .statusCode(200)
            .extract()
            .response()

        idUser2 = response.path<String>("id")

            SoftAssertions().apply {
                assertThat(response.header("Content-Type")).isEqualTo("application/json; charset=utf-8")

                assertThat(response.path<String>("id")).isNotNull()
                assertThat(response.path<String>("title")).isEqualTo("ms")
                assertThat(response.path<String>("firstName")).isEqualTo("Маша")
                assertThat(response.path<String>("lastName")).isEqualTo("Иванова")
                assertThat(response.path<String>("email")).isEqualTo(emailU2)
                assertThat(response.path<String>("dateOfBirth")).isEqualTo("1994-05-06T00:00:00.000Z")
                assertThat(response.path<String>("phone")).isEqualTo("+79998765566")
                assertThat(response.path<String>("registerDate")).startsWith(timeUserCreate)
                assertThat(response.path<String>("updatedDate")).startsWith(timeUserCreate)
            }.assertAll()
    }

    @Test
    @Order(8)
    fun createComment() {
        val requestBody = """
            {
            "message": "Но футболка сухая и совсем не пахнет",
            "owner":"$idUser2", 
            "post": "$idPost"
            }
        """.trimIndent()
        val response = RestAssured.given()
            .contentType("application/json")
            .body(requestBody)
            .header("Content-Type", "application/json")
            .header("app-id", "64ba6e63c4e32672bf5e6891") // спрячу потом
            .log().all()

            .`when`()
            .post("/comment/create")

            .then()
            .log().all()
            .statusCode(200)
            .extract()
            .response()

        timeCommentCreate = java.time.Instant.now().toString().substring(0, 16)
        idComment = response.path("id")

        SoftAssertions().apply {
            assertThat(response.header("Content-Type")).isEqualTo("application/json; charset=utf-8")

            assertThat(response.path<String>("message")).isEqualTo("Но футболка сухая и совсем не пахнет")
            assertThat(response.path<String>("owner.id")).isEqualTo(idUser2)
            assertThat(response.path<String>("owner.firstName")).isEqualTo("Маша")
            assertThat(response.path<String>("owner.lastName")).isEqualTo("Иванова")
            assertThat(response.path<String>("post")).isEqualTo(idPost)
            assertThat(response.path<String>("publishDate")).startsWith(timeCommentCreate)
        }
    }

    @Test
    @Order(9)
    fun deleteUser1() {
        RestAssured.given()
            .contentType("application/json")
            .header("Content-Type", "application/json")
            .header("app-id", "64ba6e63c4e32672bf5e6891")
            .log().all()

            .`when`()
            .delete("/user/$idUser1")

            .then() // Валидация ответа
            .statusCode(200)
            .body("id", Matchers.equalTo(idUser1))
            .log().all()
    }

    @Test
    @Order(10)
    fun deleteUser2() {
        RestAssured.given()
            .contentType("application/json")
            .header("Content-Type", "application/json")
            .header("app-id", "64ba6e63c4e32672bf5e6891")
            .log().all()

            .`when`()
            .delete("/user/$idUser2")

            .then() // Валидация ответа
            .statusCode(200)
            .body("id", Matchers.equalTo(idUser2))
            .log().all()
    }
}