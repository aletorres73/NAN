import logging
from telegram.ext import Updater, CommandHandler, MessageHandler, Filters
import openai

TELEGRAM_TOKEN = "5848760683:AAEDjGbH11t9cwVepl9h3rzw_kg28szD6zw"
OPENAI_API_KEY = "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"

openai.api_key = OPENAI_API_KEY

def respond(update, context):
    # Obtener el mensaje del usuario
    message = update.message.text

    # Utilizar la API de OpenAI GPT para generar una respuesta
    response = openai.Completion.create(
        engine="text-davinci-002",
        prompt=message,
        max_tokens=1024,
        n=1,
        stop=None,
        temperature=0.7,
    )

    # Enviar la respuesta al usuario
    context.bot.send_message(chat_id=update.effective_chat.id, text=response.choices[0].text)

def start(update, context):
    # Código para manejar el comando "/start"
    context.bot.send_message(chat_id=update.effective_chat.id, text="¡Hola! Soy un bot de ejemplo. ¿En qué puedo ayudarte?")

def main():
    # Crear un objeto de actualización y configurar el bot
    updater = Updater(TELEGRAM_TOKEN, use_context=True)
    dp = updater.dispatcher

    # Agregar el manejador de comandos y el manejador de mensajes
    dp.add_handler(CommandHandler("start", start))
    dp.add_handler(MessageHandler(Filters.text & ~Filters.command, respond))

    # Iniciar el bot
    updater.start_polling()
    updater.idle()

if __name__ == '__main__':
    main()

