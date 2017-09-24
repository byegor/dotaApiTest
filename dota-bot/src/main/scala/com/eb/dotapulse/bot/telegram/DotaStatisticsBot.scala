package com.eb.dotapulse.bot.telegram

import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.Uri.Query
import com.typesafe.config.ConfigFactory
import info.mukel.telegrambot4s.Implicits._
import info.mukel.telegrambot4s.api.declarative.{Commands, InlineQueries}
import info.mukel.telegrambot4s.api.{Polling, TelegramBot}
import info.mukel.telegrambot4s.methods.ParseMode
import info.mukel.telegrambot4s.models.{InlineKeyboardButton, InlineKeyboardMarkup, InlineQueryResultArticle, InputTextMessageContent}

/**
  * Created by Egor on 23.09.2017.
  */
object DotaStatisticsBot  extends TelegramBot with Polling
  with InlineQueries
  with Commands {

  lazy val token = ConfigFactory.load().getString("bot.token")

  def lmgtfyBtn(query: String): InlineKeyboardMarkup = InlineKeyboardMarkup.singleButton(
    InlineKeyboardButton.url("\uD83C\uDDECoogle it now!", lmgtfyUrl(query)))

  onCommand('start, 'help) { implicit msg =>
    reply(
      s"""Generates ${"Let me \uD83C\uDDECoogle that for you!".italic} links.
         |
         |/start | /help - list commands
         |
         |/lmgtfy args - generate link
         |
         |/lmgtfy2 | /btn args - clickable button
         |
         |@Bot args - Inline mode
      """.stripMargin,
      parseMode = ParseMode.Markdown)
  }

  onCommand('lmgtfy) { implicit msg =>
    withArgs { args =>
      val query = args.mkString(" ")

      reply(
        query.altWithUrl(lmgtfyUrl(query)),
        disableWebPagePreview = true,
        parseMode = ParseMode.Markdown
      )
    }
  }

  def lmgtfyUrl(query: String): String =
    Uri("http://lmgtfy.com")
      .withQuery(Query("q" -> query))
      .toString()

  onCommand('pepe, 'lmgtfy2) { implicit msg =>
    withArgs { args =>
      val query = args.mkString(" ")
      reply(query, replyMarkup = lmgtfyBtn(query))
    }
  }

  onInlineQuery { implicit iq =>
    val query = iq.query

    if (query.isEmpty)
      answerInlineQuery(Seq())
    else {

      val textMessage = InputTextMessageContent(
        query.altWithUrl(lmgtfyUrl(query)),
        disableWebPagePreview = true,
        parseMode = ParseMode.Markdown)

      val results = List(
        InlineQueryResultArticle(
          "btn:" + query,
          inputMessageContent = textMessage,
          title = iq.query,
          description = "Clickable button + link",
          replyMarkup = lmgtfyBtn(query)
        ),
        InlineQueryResultArticle(
          query,
          inputMessageContent = textMessage,
          description = "Clickable link",
          title = iq.query
        )
      )

      answerInlineQuery(results, cacheTime = 1)
    }
  }
}