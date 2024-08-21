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

public class CreditPaymentTest {
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
        DataHelperSQL.clearTables();
    }


    @SneakyThrows
    @Test
    void shouldStatusBuyCreditValidActiveCard() { // 1. Успешная оплата по активной карте
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
        mainPage.checkCreditButton().
                fillingForm(card).
                checkApprovedForm();
        assertEquals("APPROVED", DataHelperSQL.getCreditStatus());
    }


    @SneakyThrows
    @Test
    void shouldStatusBuyCreditValidDeclinedCard() {// 2. Отклонение оплаты по заблокированной карте
/*
В поле "Номер карты" ввести валидное значение заблокированной карты (см. предусловия)
В поле "Месяц", "Год" ввести валидные данные
В поле "Владелец" ввести валидное значение (латиницей имя и фамилию)
В поле "CVC/CVV" ввести валидное значение из трех цифр
Нажать кнопку "Продолжить"
ОР: Всплывающее окно с сообщением "Ошибка! Банк отказал в проведении операции." В БД есть запись DECLINED
 */
        CardInfo card = new CardInfo(getValidDeclinedCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkCreditButton().
                fillingForm(card);
        // checkDeclinedForm();
        assertEquals("DECLINED", DataHelperSQL.getCreditStatus());
    }



    // НЕГАТИВНЫЕ СЦЕНАРИИ
    @SneakyThrows
    @Test
    void shouldBuyCreditInvalidCard() { // 2. Отклонение оплаты по несуществующей карте
/*
В поле "Номер карты" ввести номер карты несуществующий номер.
ОР: Всплывающее окно с сообщением "Ошибка! Банк отказал в проведении операции."
 */
        CardInfo card = new CardInfo(getInvalidNumberCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkCreditButton().
                fillingForm(card).
                checkDeclinedForm();
        assertEquals("DECLINED", DataHelperSQL.getCreditStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyCreditInvalidPatternCard() { // 1. В поле "Номер карты" ввести меньше 16 цифр.
/*
В поле "Номер карты" ввести номер карты, содержащий меньше 16 цифр.
ОР: Под полем "Номер карты" появляется валидационное сообщение "Неверный формат";
 */
        CardInfo card = new CardInfo(getInvalidPatternNumberCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkCreditButton().
                fillingForm(card).
                checkCardNumberError();
        assertNull(DataHelperSQL.getCreditStatus());
    }

    @SneakyThrows
    @Test
    void shouldBuyCreditEmptyCard() { // 4. Поле "Номер карты" пустое
/*
Поле "Номер карты" оставить пустым
ОР: Под полем "Номер карты" появляется сообщение "Неверный формат";
 */
        CardInfo card = new CardInfo(getEmptyNumberCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkCreditButton().
                fillingForm(card).
                checkCardNumberError();
        assertNull(DataHelperSQL.getCreditStatus());
    }

    @SneakyThrows
    @Test
    void shouldBuyCreditZeroCard() { // 3. В поле "Номер карты" ввести 16 нулей
/*
В поле "Номер карты" ввести номер карты, содержащий 16 нулей.
ОР: Под полем "Номер карты" появляется валидационное сообщение "Неверный формат";
 */
        CardInfo card = new CardInfo(getZeroNumberCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkCreditButton().
                fillingForm(card).
                checkCardZeroNumberError();
        assertNull(DataHelperSQL.getCreditStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyCreditInvalidMonthCardExpiredCardError() {
        // 5. В поле "Месяц" ввести невалидное значение (истекший срок действия карты или текущий месяц текущего года)
/*
В поле "Месяц" ввести невалидное значение (истекший срок действия карты)
ОР: Поле "Месяц" подсвечивает сообщение: "Неверно указан срок действия карты"
 */
        CardInfo card = new CardInfo(getValidActiveCard(), getFirstMonth(), getCurrentYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkCreditButton().
                fillingForm(card);
        assertNull(DataHelperSQL.getCreditStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyCreditInvalidMonth() { // 6. В поле "Месяц" ввести номер месяца больше 12
/*
В поле "Месяц" ввести номер месяца больше 12 (цифра 13)
ОР: Под полем "Месяц" появляется валидационное сообщение "Неверно указан срок действия карты";
 */
        CardInfo card = new CardInfo(getValidActiveCard(), getInvalidMonth(), getCurrentYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkCreditButton().
                fillingForm(card).
                checkMonthError();
        assertNull(DataHelperSQL.getCreditStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyCreditZeroMonth() { // 7. В поле "Месяц" ввести 00.
/*
В поле "Месяц" ввести 00.
ОР: Поле "Месяц" подсвечивает сообщение: "Неверно указан срок действия карты"
 */
        CardInfo card = new CardInfo(getValidActiveCard(), getZeroMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkCreditButton().
                fillingForm(card);
        assertNull(DataHelperSQL.getCreditStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyCreditEmptyMonth() { // 8.Поле "Месяц" оставить пустым.
/*
Поле "Месяц" оставить пустым.
ОР: Под полем "Месяц" появляется валидационное сообщение "Поле обязательно для заполнения";
 */
        CardInfo card = new CardInfo(getValidActiveCard(), getEmptyMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkCreditButton().
                fillingForm(card).
                checkMonthError();
        assertNull(DataHelperSQL.getCreditStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyCreditInvalidYearCard() { // 9. В поле "Год" ввести прошедший год
/*
В поле "Год" ввести прошедший год
ОР: Под полем "Год" появляется валидационное сообщение "Истёк срок действия карты";
 */
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getPreviousYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkCreditButton().
                fillingForm(card);
        assertNull(DataHelperSQL.getCreditStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyCreditEmptyYear() {// 11. Поле "Год" оставить пустым
/*
Поле "Год" оставить пустым
ОР: Под полем "Год" появляется валидационное сообщение "Поле обязательно для заполнения";
 */
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getEmptyYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkCreditButton().
                fillingForm(card).
                checkYearError();
        assertNull(DataHelperSQL.getCreditStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyCreditZeroYear() { // 10. В поле "Год" ввести "00"
/*
В поле "Год" ввести нулевой год "00"
ОР: Под полем "Год" появляется валидационное сообщение "Истёк срок действия карты";
 */
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getZeroYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkCreditButton().
                fillingForm(card).
                checkYearError();
        assertNull(DataHelperSQL.getCreditStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyCreditRussianOwner() { // 12. В поле "Владелец" ввести имя, фамилию на русской раскладке
/*
В поле "Владелец" ввести имя, фамилию на русской раскладке клавиатуры.
ОР: Поле "Владелец" подсвечивает сообщение: "недопустимые символы"
 */
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getInvalidLocaleOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkCreditButton().
                fillingForm(card).
                checkRussianOwnerError();
        assertNull(DataHelperSQL.getCreditStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyCreditFirstNameOwner() { // 13. В поле "Владелец" ввести только имя
/*
В поле "Владелец" ввести только имя
ОР: Поле "Владелец" подсвечивает сообщение: "введите полное имя и фамилию".
 */
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getInvalidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkCreditButton().
                fillingForm(card);
        assertNull(DataHelperSQL.getCreditStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyCreditEmptyOwner() {// 14. Поле "Владелец" оставить пустым
/*
Поле "Владелец" оставить пустым
ОР: Под полем "Владелец" появляется валидационное сообщение "Поле обязательно для заполнения";
 */
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getEmptyOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkCreditButton().
                fillingForm(card).
                checkOwnerError();
        assertNull(DataHelperSQL.getCreditStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyCreditInvalidCVC() { // 16. В поле "CVC/CVV" ввести 2 цифры
/*
В поле "CVC/CVV" ввести только 2 цифры (невалидное значение)
ОР: Поле "CVC/CVV" подсвечивает сообщение: "Значение поля должно состоять из трех цифр"
 */
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getInvalidCVC());
        val mainPage = new StartPage();
        mainPage.checkCreditButton().
                fillingForm(card).
                checkCVCError();
        assertNull(DataHelperSQL.getCreditStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyCreditEmptyCVC() { // 17. Поле "CVC/CVV" оставить пустым
/*
Поле "CVC/CVV" оставить пустым
ОР: Под полем "CVC/CVV" появляется валидационное сообщение "Поле обязательно для заполнения";
 */
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getEmptyCVC());
        val mainPage = new StartPage();
        mainPage.checkCreditButton().
                fillingForm(card).
                checkCVCError();
        assertNull(DataHelperSQL.getCreditStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyCreditZeroCVC() { // 15. В поле "CVC/CVV" ввести "нули"
/*
В поле "CVC/CVV" ввести "нули"
ОР: Под полем "CVC/CVV" появляется валидационное сообщение "Неверный формат";
 */
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getZeroCVC());
        val mainPage = new StartPage();
        mainPage.checkCreditButton().
                fillingForm(card);
        assertNull(DataHelperSQL.getCreditStatus());
    }


}
