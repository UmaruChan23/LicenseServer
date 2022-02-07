# LicenseServer
https://license-diplom.herokuapp.com/
- POST /register - регистрация
    String login
    String password
- POST /auth - авторизация
    String login
    String password
    String deviceId
    Header Authorization Bearer + token
- POST /code/trial - получить код активации временной лицензии
    Header Authorization
- POST /code/activate - активировать код
    Header Authorization
    String activation code
- POST /ticket - обновить тикет для клиента
    Header Authorization
    JSON ticket
