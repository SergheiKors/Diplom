package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.CardInfo;
import ru.netology.data.DataHelperSQL;
import ru.netology.page.StartPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.netology.data.DataHelper.*;

public class UsualPaymentTest {
    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setUp() {
        open("http://localhost:8080/");
        // DataHelperSQL.clearTables();
    }


    @SneakyThrows
    @Test
    void shouldStatusBuyPaymentValidActiveCard() { // 1. Успешная оплата по активной карте
/*
В поле "Номер карты" ввести валидное значение активной карты (см. предусловия)
В поле "Месяц", "Год" ввести валидные данные
В поле "Владелец" ввести валидное значение (латиницей имя и фамилию)
В поле "CVC/CVV" ввести валидное значение из трех цифр
Нажать кнопку "Продолжить"
ОР: Всплывающее окно с сообщением "Успешно! Операция одобрена банком." В БД есть запись APPROVED
 */
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkApprovedForm();
        assertEquals("APPROVED", DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldStatusBuyPaymentValidDeclinedCard() { // 2. Отклонение оплаты по заблокированной карте
/*
Валидный номер заблокированной карты 4444 4444 4444 4442
 */

        CardInfo card = new CardInfo(getValidDeclinedCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card);
        assertEquals("DECLINED", DataHelperSQL.getPaymentStatus());
    }

    // НЕГАТИВНЫЕ СЦЕНАРИИ
    @SneakyThrows
    @Test
    void shouldBuyPaymentInvalidCard() { // 3. Отклонение оплаты по несуществующей карте
/*
В поле "Номер карты" ввести номер карты несуществующий номер.
ОР: Всплывающее окно с сообщением "Ошибка! Банк отказал в проведении операции."
 */
        CardInfo card = new CardInfo(getInvalidNumberCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkDeclinedForm();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyPaymentInvalidPatternCard() { // 4. В поле "Номер карты" ввести менее 16 цифр.
/*
В поле "Номер карты" ввести номер карты, содержащий меньше 16 цифр.
ОР: Под полем "Номер карты" появляется валидационное сообщение "Неверный формат";
 */
        CardInfo card = new CardInfo(getInvalidPatternNumberCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkCardNumberError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyPaymentEmptyCard() { // 5. Поле "Номер карты" оставить пустым
/*
Поле "Номер карты" оставить пустым
ОР: Под полем "Номер карты" появляется сообщение "Неверный формат";
 */
        CardInfo card = new CardInfo(getEmptyNumberCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkCardNumberError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyPaymentZeroCard() { // 6. В поле "Номер карты" ввести 16 нулей
/*
В поле "Номер карты" ввести номер карты, содержащий 16 нулей.
ОР: Под полем "Номер карты" появляется валидационное сообщение "Неверный формат";
 */
        CardInfo card = new CardInfo(getZeroNumberCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkCardZeroNumberError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyPaymentInvalidMonthCardExpiredCardError() { // 7. В поле "Месяц" ввести невалидное значение (истекший срок действия карты)
/*
В поле "Месяц" ввести невалидное значение (истекший срок действия карты)
ОР: Поле "Месяц" подсвечивает сообщение: "Неверно указан срок действия карты"
 */

        CardInfo card = new CardInfo(getValidActiveCard(), getFirstMonth(), getCurrentYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkExpiredCardError();
        assertEquals("DECLINED", DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyPaymentInvalidMonth() { // 8. В поле "Месяц" ввести номер месяца больше 12
/*
В поле "Месяц" ввести номер месяца больше 12 (цифра 13)
ОР: Под полем "Месяц" появляется валидационное сообщение "Неверно указан срок действия карты";
 */
        CardInfo card = new CardInfo(getValidActiveCard(), getInvalidMonth(), getCurrentYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkMonthError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyPaymentZeroMonth() { // 9. В поле "Месяц" ввести 00
/*
В поле "Месяц" ввести 00.
ОР: Поле "Месяц" подсвечивает сообщение: "Неверно указан срок действия карты"
 */
        CardInfo card = new CardInfo(getValidActiveCard(), getZeroMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkMonthError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyPaymentEmptyMonth() { // 10.Поле "Месяц" оставить пустым.
/*
Поле "Месяц" оставить пустым.
ОР: Под полем "Месяц" появляется валидационное сообщение "Поле обязательно для заполнения";
 */
        CardInfo card = new CardInfo(getValidActiveCard(), getEmptyMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkMonthError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyPaymentInvalidYearCard() { // 11. В поле "Год" ввести прошедший год
/*
В поле "Год" ввести прошедший год
ОР: Под полем "Год" появляется валидационное сообщение "Истёк срок действия карты";
 */
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getPreviousYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkYearError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyPaymentEmptyYear() { // 12. Поле "Год" оставить пустым
/*
Поле "Год" оставить пустым
ОР: Под полем "Год" появляется валидационное сообщение "Поле обязательно для заполнения";
 */
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getEmptyYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkYearError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyPaymentZeroYear() { // 13. В поле "Год" ввести нулевой год "00"
/*
В поле "Год" ввести нулевой год "00"
ОР: Под полем "Год" появляется валидационное сообщение "Истёк срок действия карты";
 */
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getZeroYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkYearError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyPaymentRussianOwner() { // 14. В поле "Владелец" ввести имя, фамилию на русской раскладке
/*
В поле "Владелец" ввести имя, фамилию на русской раскладке клавиатуры.
ОР: Поле "Владелец" подсвечивает сообщение: "недопустимые символы"
 */

        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getInvalidLocaleOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkRussianOwnerError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyPaymentFirstNameOwner() { // 15. В поле "Владелец" ввести только имя
/*
В поле "Владелец" ввести только имя
ОР: Поле "Владелец" подсвечивает сообщение: "введите полное имя и фамилию"
 */
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getInvalidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkOwnerOnlyNameError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyPaymentEmptyOwner() { // 16. Поле "Владелец" оставить пустым
/*
Поле "Владелец" оставить пустым
ОР: Под полем "Владелец" появляется валидационное сообщение "Поле обязательно для заполнения";
 */

        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getEmptyOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkOwnerError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyPaymentInvalidCVC() { // 17. В поле "CVC/CVV" ввести только 2 цифры
/*
В поле "CVC/CVV" ввести только 2 цифры (невалидное значение)
ОР: Поле "CVC/CVV" подсвечивает сообщение: "Значение поля должно состоять из трех цифр"
 */
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getInvalidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkCVCError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyPaymentEmptyCVC() { // 18. Поле "CVC/CVV" оставить пустым
/*
Поле "CVC/CVV" оставить пустым
ОР: Под полем "CVC/CVV" появляется валидационное сообщение "Поле обязательно для заполнения";
 */
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getEmptyCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkCVCError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyPaymentZeroCVC() { // 19. В поле "CVC/CVV" ввести "нули"
/*
В поле "CVC/CVV" ввести "нули"
ОР: Под полем "CVC/CVV" появляется валидационное сообщение "Неверный формат";
 */

        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getZeroCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkCVCError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }

}