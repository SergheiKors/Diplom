package ru.netology.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import ru.netology.data.CardInfo;

import java.time.Duration;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;

public class PaymentPage {
    private SelenideElement cardNumber = $(byText("Номер карты")).parent().$(".input__control");
    private SelenideElement month = $(byText("Месяц")).parent().$(".input__control");
    private SelenideElement year = $(byText("Год")).parent().$(".input__control");
    private SelenideElement owner = $(byText("Владелец")).parent().$(".input__control");
    private SelenideElement cvc = $(byText("CVC/CVV")).parent().$(".input__control");
    private SelenideElement continueButton = $(byText("Продолжить"));
    private SelenideElement cardZeroNumberError = $(byText("Неверный формат"));
    private SelenideElement cardNumberError = $(byXpath("/html/body/div/div/form/fieldset/div[1]/span/span/span[3]"));
    private SelenideElement monthError = $(byText("Неверный формат"));
    private SelenideElement yearError = $(byText("Год")).parent().$(".input__sub");
    private SelenideElement expiredCardError = $(byText("Неверно указан срок действия карты"));
    private SelenideElement ownerError = $(byXpath("//*[@id=\"root\"]/div/form/fieldset/div[3]/span/span[1]/span/span/span[3]"));
    private SelenideElement ownerOnlyNameError = $(byText("введите полное имя и фамилию"));
    private SelenideElement RussianownerError = $(byText("недопустимые символы"));
    private SelenideElement cvcError = $(byText("Неверный формат"));
    private SelenideElement approvedForm = $(".notification_status_ok");
    private SelenideElement declinedForm = $(".notification_status_error");

    public PaymentPage fillingForm(CardInfo card) {
        cardNumber.setValue(card.getCardNumber());
        month.setValue(card.getMonth());
        year.setValue(card.getYear());
        owner.setValue(card.getOwner());
        cvc.setValue(card.getCardCVC());
        continueButton.click();
        return new PaymentPage();
    }

    public void checkApprovedForm() {
        approvedForm.shouldBe(Condition.visible, Duration.ofMillis(15000));
    }

    public void checkDeclinedForm() {
        declinedForm.shouldBe(Condition.visible, Duration.ofMillis(15000));
    }

    public void checkCardNumberError() {
        cardNumberError.shouldBe(Condition.visible);
    }

    public void checkCardZeroNumberError() {
        cardZeroNumberError.shouldBe(Condition.visible);
    }
    public void checkMonthError() {
        monthError.shouldBe(Condition.visible);
    }

    public void checkExpiredCardError() {
        expiredCardError.shouldBe(Condition.visible);
    }

    public void checkYearError() {
        yearError.shouldBe(Condition.visible);
    }

    public void checkOwnerError() {
        ownerError.shouldBe(Condition.visible);
    }
    public void checkOwnerOnlyNameError() {
        ownerOnlyNameError.shouldBe(Condition.visible);
    }

    public void checkRussianOwnerError() {
        RussianownerError.shouldBe(Condition.visible);
    }

    public void checkCVCError() {
        cvcError.shouldBe(Condition.visible);
    }
}
