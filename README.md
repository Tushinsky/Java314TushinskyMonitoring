Демонстрационный пример сервиса по передаче показаний счётчиков воды
В качестве базы данных использованы файлы в формате csv: Account - хранит информацию о номерах аккаунтов пользователей; Users - хранит информацию о логинах, паролях, правах доступа, именах пользователей; Role - хранит информацию о правах доступа; Readings - хранит информацию о переданных показаниях пользователей; ACtion - хранит информацию о типах действий, выполняемых пользователями; Enter - хранит информацию о действиях, выполняемых пользователями. Две последние таблицы пока не используются.
Главное окно приложения позволяет войти или выполнить регистрацию нового пользователя.
Панель пользователя позволяет внести показания. При этом осуществляется контроль за частотой передачи показаний - только один раз в текущем месяце по каждой категории.
Панель администратора позволяет удалить аккаунт любого пользователя, добавить новые или изменить существующие показания выбранного пользователя.
