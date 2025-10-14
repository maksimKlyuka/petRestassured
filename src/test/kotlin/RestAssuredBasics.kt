import org.junit.jupiter.api.Test
import io.restassured.RestAssured
import org.hamcrest.Matchers
import org.assertj.core.api.SoftAssertions
import org.assertj.core.api.Assertions.assertThat

class UserStoryTest {

    init {
        // Добавляем эту конфигурацию для логов
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
        RestAssured.baseURI = "https://dummyapi.io/data/v1"
    }
    private var idUser1 = "68ee868fd0b98650e860ce6b"
    private var idUser2 = ""
    private var idPost = ""
    private var idComment = ""



    @Test
    fun createUser1() {
        val requestBody = """
            {
            "title": "dr",
            "firstName": "Саша",
            "lastName": "Иванов",
            "email": "sivanov15@gmail.com",
            "dateOfBirth": "11/12/1992",
            "phone": "+79998765432"
            }
        """.trimIndent()
        val currentTime = java.time.Instant.now().toString()

        idUser1 = RestAssured.given() // Настройка запроса
            .contentType("application/json")
            .body(requestBody)
            .header("Content-Type", "application/json")
            .header("app-id", "64ba6e63c4e32672bf5e6891") // спрячу потом
            .log().all()

            .`when`() // Выполнение запроса
            .post("/user/create")

            .then() // Валидация ответа
            .statusCode(200)
            .body("id", Matchers.notNullValue())
            .body("title", Matchers.equalTo("dr"))
            .body("firstName", Matchers.equalTo("Саша"))
            .body("lastName", Matchers.equalTo("Иванов"))
            .body("email", Matchers.equalTo("sivanov15@gmail.com"))
            .body("dateOfBirth", Matchers.equalTo("1992-11-12T00:00:00.000Z"))
            .body("phone", Matchers.equalTo("+79998765432"))
            .body("location.street", Matchers.equalTo("Угловая"))
            .body("location.city", Matchers.equalTo("Улан-Уде"))
            .body("location.state", Matchers.equalTo("Республика Бурятия"))
            .body("location.country", Matchers.equalTo("Россия"))
            .body("location.timezone", Matchers.equalTo("+8:00"))
            .body("registerDate", org.hamcrest.Matchers.startsWith(currentTime.substring(0, 16)))
            .log().all()
            // Валидация заголовков
            .header("Content-Type", "application/json; charset=utf-8")

            .extract() // Извлечение поля
            .path("id")
    }

    @Test
    fun checkCreateUser1() {
        RestAssured.given()
            .contentType("application/json")
            .header("Content-Type", "application/json")
            .header("app-id", "64ba6e63c4e32672bf5e6891")
            .log().all()

            .`when`()
            .get("/user/$idUser1")

            .then()
            .statusCode(200)
            .body("id", Matchers.equalTo(idUser1))
            .body("title", Matchers.equalTo("dr"))
            .body("firstName", Matchers.equalTo("Саша"))
            .body("lastName", Matchers.equalTo("Иванов"))
            .body("email", Matchers.equalTo("sivanov15@gmail.com"))
            .body("dateOfBirth", Matchers.equalTo("1992-11-12T00:00:00.000Z"))
            .body("phone", Matchers.equalTo("+79998765432"))
            .body("location.street", Matchers.equalTo("Угловая"))
            .body("location.city", Matchers.equalTo("Улан-Уде"))
            .body("location.state", Matchers.equalTo("Республика Бурятия"))
            .body("location.country", Matchers.equalTo("Россия"))
            .body("location.timezone", Matchers.equalTo("+8:00"))
            .log().all()
    }

    @Test
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
            .log().ifError()  // залогирует при ошибке
            .statusCode(200)
            .extract()
            .response()

        SoftAssertions().apply {
            assertThat(response.path<String>("id")).isEqualTo(idUser1)
            assertThat(response.path<String>("title")).isEqualTo("mr")
            assertThat(response.path<String>("firstName")).isEqualTo("Александр")
            assertThat(response.path<String>("lastName")).isEqualTo("Иванов-Прокофьев")
            assertThat(response.path<String>("email")).isEqualTo("sivanov15@gmail.com")
            assertThat(response.path<String>("dateOfBirth")).isEqualTo("1988-05-06T00:00:00.000Z")
            assertThat(response.path<String>("phone")).isEqualTo("88005553535")
            assertThat(response.path<String>("location.street")).isEqualTo("Фишева")
            assertThat(response.path<String>("location.city")).isEqualTo("Улан-Уде")
            assertThat(response.path<String>("location.state")).isEqualTo("Республика Бурятия")
            assertThat(response.path<String>("location.country")).isEqualTo("Россия")
            assertThat(response.path<String>("location.timezone")).isEqualTo("+8:00")
        }.assertAll()
    }

    @Test
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
}