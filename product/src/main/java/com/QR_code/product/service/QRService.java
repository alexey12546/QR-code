package com.QR_code.product.service;

import com.QR_code.product.config.BotConfig;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class QRService extends TelegramLongPollingBot {

    @Autowired
    BotConfig botConfig;

    @Override
    public String getBotUsername() {
        return botConfig.getUsername();
    }

    public String getBotToken() {
        return botConfig.getToken();
    }

    public QRService(BotConfig botConfig) {
        this.botConfig = botConfig;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Отправка ссылки✅️"));
        listOfCommands.add(new BotCommand("/help", "Помощь в пользовании ботом\uD83D\uDCDD"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error message", e);
        }
    }

    public void onUpdateReceived(Update update) {
        Long chatId = update.getMessage().getChatId();
        if (update.hasMessage() && update.getMessage().hasText()) {
            verificationText(chatId, update);
        } else {
            sendMessage(chatId, "Данный тип не обрабатывается !");
        }
    }

    public void verificationText(Long chatId, Update update) {
        String text = update.getMessage().getText();
        if (text.startsWith("http")) {
            try {
                BufferedImage qrImage = generateQRCodeImage(text);
                File file = new File("qr.png");
                ImageIO.write(qrImage, "PNG", file);
                InputFile inputFile = new InputFile(file);
                SendPhoto photo = new SendPhoto();
                photo.setChatId(update.getMessage().getChatId().toString());
                photo.setPhoto(inputFile);
                execute(photo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (text.equals("/start")) {
            sendMessage(chatId, "Привет!\uD83D\uDC4B\n" +
                    "Этот бот тебе поможет быстро преобразовать твою ссылку в QR-code.\n" +
                    "Просто отправь нужную ссылку и получи свой QR.\uD83E\uDD17");
        } else if (text.equals("/help")) {
            sendMessage(chatId, "Какую ссылку данный бот сможет корректно преобразовать в QR-code?\uD83E\uDD14\n" +
                    "Любая ссылка, которая начинается с 'http' - будет обработана.\n" +
                    "Просто отправь нужную ссылку и получи свой QR.\uD83E\uDD17");
        } else {
            sendMessage(chatId, "Отправь ссылку!");
        }
    }

    private BufferedImage generateQRCodeImage(String text) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        var bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 300, 300);
        BufferedImage image = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < 300; x++) {
            for (int y = 0; y < 300; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return image;
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try {
            execute(message);
        } catch (TelegramApiException ignored) {
            log.error("Error message", ignored);
            throw new RuntimeException(ignored);
        }
    }

}
