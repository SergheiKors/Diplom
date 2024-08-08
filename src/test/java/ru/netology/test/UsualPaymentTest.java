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
        DataHelperSQL.clearTables();
    }


    @SneakyThrows
    @Test
    void shouldStatusBuyPaymentValidActiveCard() { // 1. Успешная оплата по активной карте
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
        CardInfo card = new CardInfo(getValidDeclinedCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkDeclinedForm();
        assertEquals("DECLINED", DataHelperSQL.getPaymentStatus());
    }

    // НЕГАТИВНЫЕ СЦЕНАРИИ
    @SneakyThrows
    @Test
    void shouldBuyPaymentInvalidCard() { // 3. Отклонение оплаты по несуществующей карте
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
        CardInfo card = new CardInfo(getZeroNumberCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkCardNumberError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyPaymentInvalidMonthCardExpiredCardError() { // 7. В поле "Месяц" ввести невалидное значение (истекший срок действия карты)
        CardInfo card = new CardInfo(getValidActiveCard(), getFirstMonth(), getCurrentYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkExpiredCardError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyPaymentInvalidMonth() { // 8. В поле "Месяц" ввести номер месяца больше 12
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
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getInvalidLocaleOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkOwnerError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyPaymentFirstNameOwner() { // 15. В поле "Владелец" ввести только имя
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getInvalidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkOwnerError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyPaymentEmptyOwner() { // 16. Поле "Владелец" оставить пустым
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
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getZeroCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkCVCError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }
}