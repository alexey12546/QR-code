# QR Code Bot

Это бот для генерации QR-кодов. 
Бота также можно развернуть в контейнере Docker с использованием Jib для сборки и деплоя.

## Требования

- Java 17 или выше
- Созданный бот в телеграме
- Docker
- Maven

## Клонирование репозитория

Сначала клонируйте репозиторий:

```bash
git clone <URL вашего репозитория>
cd QR-code/product
```
## Создание образа

```bash
sudo mvn compile jib:dockerBuild -Dbot.name=BestQRService_bot -Dbot.token=YOUR_BOT_TOKEN
```

## Запуск контейнера

После того как образ будет собран, вы можете запустить контейнер с помощью Docker:

```bash
sudo docker run -d --name qrcodebot -p 8080:8080 qrcodebot
```
Это создаст контейнер с именем qrcodebot и откроет порт 8080.

## Остановка контейнера

Чтобы остановить контейнер, используйте команду:

```bash
sudo docker stop qrcodebot
```
