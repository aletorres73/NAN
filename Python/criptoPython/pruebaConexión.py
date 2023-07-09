import ccxt
import ta
import time

# Configurar la conexión a la API de Binance
exchange = ccxt.binance({
    'apiKey': 'TU_API_KEY',
    'secret': 'TU_API_SECRET',
    'enableRateLimit': True,
})

# Establecer el apalancamiento x5
symbol = 'BTC/USDT'
leverage = 5
exchange.fapiPrivate_post_leverage({
    'symbol': symbol,
    'leverage': leverage,
})

# Definir variables
entry_price = None
stop_loss_price = None
take_profit_price = None
risk_percentage = 0.01

# Función para generar señal de compra
def generate_buy_signal(closes):
    # Calcular las medias móviles
    ma20 = ta.trend.sma_indicator(closes, window=20)
    ma60 = ta.trend.sma_indicator(closes, window=60)

    # Generar señal de compra cuando se cumplan las condiciones
    if ta.utils.cross.cross_above(ma20, ma60):
        return True
    else:
        return False

# Función para ejecutar la estrategia
def run_strategy():
    # Obtener los precios de cierre del par de futuros deseado (por ejemplo, BTC/USDT)
    symbol = 'BTC/USDT'
    timeframe = '1h'  # Cambiar a marco temporal de 1 hora
    limit = 100
    ohlcv = exchange.fetch_ohlcv(symbol, timeframe, limit=limit)
    closes = [candle[4] for candle in ohlcv]

    # Verificar la señal de compra
    if generate_buy_signal(closes):
        # Obtener el último precio de cierre
        last_close = closes[-1]

        # Calcular los niveles de stop loss y take profit
        stop_loss_price = last_close * (1 - risk_percentage)
        take_profit_price = last_close * (1 + risk_percentage)

        # Ejecutar la orden de compra
        print(f"Señal de compra detectada. Precio de entrada: {last_close}")
        print(f"Nivel de stop loss: {stop_loss_price}")
        print(f"Nivel de take profit: {take_profit_price}")
        # Aquí puedes implementar la lógica para ejecutar la orden de compra en Binance

# Bucle principal para ejecutar la estrategia en tiempo real
while True:
    try:
        run_strategy()
    except Exception as e:
        print(f"Ocurrió un error: {str(e)}")

    # Esperar un tiempo antes de volver a ejecutar la estrategia
    time.sleep(30)  # Esperar para volver a conectar
