# Дипломный проект по итогам курса "Тестировщик ПО"

## Документация

* [План выполнения работ](https://github.com/SergheiKors/Diplom/blob/main/docs/Plan.md)
* [Отчет о проведенном тестировании](https://github.com/SergheiKors/Diplom/blob/main/docs/Report.md)
* [Отчет о проведенной автоматизации](https://github.com/SergheiKors/Diplom/blob/main/docs/Summary.md)



## О проекте
Приложение представляет из себя веб-сервис.

![image](https://user-images.githubusercontent.com/97331580/192088410-3df39e3e-a875-4114-80bb-9048515ee215.png)

Приложение предлагает купить тур по определённой цене с помощью двух способов:

* Обычная оплата по дебетовой карте
* Уникальная технология: выдача кредита по данным банковской карты

Само приложение не обрабатывает данные по картам, а пересылает их банковским сервисам:

* сервису платежей (далее - Payment Gate)
* кредитному сервису (далее - Credit Gate)

Сервис может взаимодействоватьс СУБД  MySql и PostgreSql

База данных хранит информацию о заказах, платежах, статусах карт, способах оплаты.

## Цели проекта

В рамках проекта необходимо автоматизировать тестирование комплексного сервиса покупки тура, взаимодействующего с СУБД и API Банка.

### Для запуска приложения:

1. С помощью Git cклонировать репозиторий командой git clone https://github.com/SergheiKors/Diplom.git;
2. Запустить Docker;
3. Открыть проект в IntelliJ IDEA;
4. В терминале IntelliJ IDEA запустить необходимые базы данных и нужные контейнеры командой `docker-compose up`;
6. В новой вкладке терминала ввести следующую команду:  
   `java "-Dspring.datasource.url=jdbc:mysql://localhost:3306/app" -jar aqa-shop.jar` для БД MySQL;  
   `java "-Dspring.datasource.url=jdbc:postgresql://localhost:5432/app" -jar aqa-shop.jar` для БД PostgreSQL;
7. Проверить доступность приложения в браузере по адресу:  
   `http://localhost:8080/`

### Для запуска автотестов:

- В новой вкладке терминала ввести следующую команду:  
  `./gradlew clean test "-D db.url=jdbc:mysql://localhost:3306/app"` для БД MySQL;  
  `./gradlew clean test "-D db.url=jdbc:postgresql://localhost:5432/app"` для БД PostgreSQL;

### Для просмотра отчетов по результатам тестирования:
1. Сгенерировать отчет Allure, выполнив команду в терминале IDEA: ```./gradlew allureServe```
* Если отчет не открывается автоматически в браузере, то выполнить команду: ```./gradlew allureReport``` и открыть отчет вручную (файл index.html) по адресу: ```.\build\reports\allure-report\allureReport```
2. При необходимости изменить подключение к другой БД, необходимо остановить подключения в терминалах черех Ctrl + C в окне терминала
