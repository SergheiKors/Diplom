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
        CardInfo card = new CardInfo(getValidDeclinedCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkCreditButton().
                fillingForm(card).
                checkDeclinedForm();
        assertEquals("DECLINED", DataHelperSQL.getCreditStatus());
    }

    // НЕГАТИВНЫЕ СЦЕНАРИИ
    @SneakyThrows
    @Test
    void shouldBuyCreditInvalidCard() { // 2. Отклонение оплаты по несуществующей карте
        CardInfo card = new CardInfo(getInvalidNumberCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkCreditButton().
                fillingForm(card).
                checkDeclinedForm();
        assertNull(DataHelperSQL.getCreditStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyCreditInvalidPatternCard() { // 1. В поле "Номер карты" ввести меньше 16 цифр.
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
        CardInfo card = new CardInfo(getZeroNumberCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkCreditButton().
                fillingForm(card).
                checkCardNumberError();
        assertNull(DataHelperSQL.getCreditStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyCreditInvalidMonthCardExpiredCardError() { // 5. В поле "Месяц" ввести невалидное значение (истекший срок действия карты или текущий месяц текущего года)
        CardInfo card = new CardInfo(getValidActiveCard(), getFirstMonth(), getCurrentYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkCreditButton().
                fillingForm(card).
                checkExpiredCardError();
        assertNull(DataHelperSQL.getCreditStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyCreditInvalidMonth() { // 6. В поле "Месяц" ввести номер месяца больше 12
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
        CardInfo card = new CardInfo(getValidActiveCard(), getZeroMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkCreditButton().
                fillingForm(card).
                checkMonthError();
        assertNull(DataHelperSQL.getCreditStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyCreditEmptyMonth() { // 8.Поле "Месяц" оставить пустым.
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
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getPreviousYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkCreditButton().
                fillingForm(card).
                checkYearError();
        assertNull(DataHelperSQL.getCreditStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyCreditEmptyYear() {// 11. Поле "Год" оставить пустым
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
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getInvalidLocaleOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkCreditButton().
                fillingForm(card).
                checkOwnerError();
        assertNull(DataHelperSQL.getCreditStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyCreditFirstNameOwner() { // 13. В поле "Владелец" ввести только имя
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getInvalidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkCreditButton().
                fillingForm(card).
                checkOwnerError();
        assertNull(DataHelperSQL.getCreditStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyCreditEmptyOwner() {// 14. Поле "Владелец" оставить пустым
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
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getZeroCVC());
        val mainPage = new StartPage();
        mainPage.checkCreditButton().
                fillingForm(card).
                checkCVCError();
        assertNull(DataHelperSQL.getCreditStatus());
    }
}
