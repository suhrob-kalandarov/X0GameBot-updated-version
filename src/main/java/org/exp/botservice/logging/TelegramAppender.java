package org.exp.botservice.logging;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;

import java.io.Serializable;
import java.util.Arrays;

@Plugin(
        name = "TelegramAppender",
        category = Core.CATEGORY_NAME,
        elementType = Appender.ELEMENT_TYPE
)
public class TelegramAppender extends AbstractAppender {

    private final TelegramBot bot;
    private final String chatId;

    protected TelegramAppender(String name, Filter filter, Layout<? extends Serializable> layout,
                               String botToken, String chatId) {
        super(name, filter, layout, false, null);
        this.bot = new TelegramBot(botToken);
        this.chatId = chatId;
    }

    @PluginFactory
    public static TelegramAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Filter") Filter filter,
            @PluginAttribute("botToken") String botToken,
            @PluginAttribute("chatId") String chatId) {

        return new TelegramAppender(name, filter, null, botToken, chatId);
    }

    @Override
    public void append(LogEvent event) {
        String logMessage = String.format(
                "[%s] %s: %s",
                event.getLevel(),
                event.getLoggerName(),
                event.getMessage().getFormattedMessage()
        );

        if (event.getThrown() != null) {
            logMessage += "\n\n" + Arrays.toString(event.getThrown().getStackTrace());
        }

        bot.execute(new SendMessage(chatId, logMessage));
    }
}