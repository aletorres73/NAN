
from binance.client import Client
import talib as ta
import time
import numpy as np
import pandas as pd

# Configurar la conexión a la API de Binance
api_key = '7CqLqaSvEKOqkTvuyUL9Ecwl8FpZ018Rnfmv710jDFAH6t5i03DTYQITMQRqSmj3'
api_secret = 'sD01P5o2J9uNHurOkFfJ7iWPEoVy6OmuEnPXqjPV2W835cVgrCnswq2OqBGMd7Rb'
client = Client(api_key, api_secret)

# Establecer el apalancamiento x5
symbol = 'ETHUSDT'
leverage = 5
client.futures_change_leverage(symbol=symbol, leverage=leverage)

# Definir variables
entry_price = 0
stop_loss_price = 0
take_profit_price = 0
risk_percentage = 0.01


# Función para generar señal de compra
def generate_buy_signal(closes):

    # Calcular las medias móviles
    ma20 = pd.Series(closes).rolling(window=20).mean()
    ma60 = pd.Series(closes).rolling(window=60).mean()

    # Generar señal de compra cuando se cumplan las condiciones
    if ma20.iloc[-1] > ma60.iloc[-1] and ma20.iloc[-2] <= ma60.iloc[-2]:
        return True
    else:
        return False

def register_trade(symbol, side, entry_price, exit_price, exit_type):
    timestamp = pd.Timestamp.now()
    trade_data = {
        'Timestamp': [timestamp],
        'Symbol': [symbol],
        'Side': [side],
        'Entry Price': [entry_price],
        'Exit Price': [exit_price],
        'Exit Type': [exit_type]
    }
    df = pd.DataFrame(trade_data)

    # Guardar los datos en el archivo CSV
    with open('trades.csv', 'a') as f:
        df.to_csv(f, header=f.tell() == 0, index=False)

# Función para ejecutar la estrategia
def run_strategy():
    # Obtener los precios de cierre del par de futuros deseado (por ejemplo, BTC/USDT)
    symbol = 'ETHUSDT'
    timeframe = '5m'  # Cambiar a marco temporal de 1 hora
    limit = 100
    klines = client.futures_klines(symbol=symbol, interval=timeframe, limit=limit)
    closes = [float(candle[4]) for candle in klines]
    closes_arr = np.array(closes)

    # Obtener el último precio de cierre
    last_close = closes[-1]


    # Verificar si hay una operación abierta
    open_position = False
    
    # Obtener las posiciones abiertas
    positions = client.futures_position_information()
    for position in positions:
        if position['symbol'] == symbol and float(position['positionAmt']) != 0:
            open_position = True
            break

    # Verificar la señal de compra
    if generate_buy_signal(closes_arr)== True:

        # Ejecutar la orden de compra
        #if not open_position:


        # Calcular los niveles de stop loss y take profit
        stop_loss_price = last_close * (1 - risk_percentage)
        take_profit_price = last_close * (1 + risk_percentage)

        # # Calcular la cantidad de contratos a comprar
        # Obtener el tamaño del contrato
        contract_size = float(client.futures_exchange_info()['symbols'][0]['contractSize'])

        # Calcular la cantidad de contratos a comprar
        quantity = round(1 / (leverage * contract_size), 3)

        # equity = float(client.futures_account_balance()[0]['balance'])
        # leverage = client.futures_get_leverage(symbol=symbol)['leverage']
        # contract_size = float(client.futures_exchange_info()['symbols'][0]['contractSize'])
        # quantity = round((equity * risk_percentage) / (last_close * leverage * contract_size), 3)
        
        # Verificar el tamaño mínimo de la orden
        min_notional = float(client.futures_exchange_info()['symbols'][0]['filters'][3]['minNotional'])
        if quantity * last_close < min_notional:
            quantity = round(min_notional / last_close, 3)
        
        # Ejecutar la orden de compra
        quantity = 0.001  # Cantidad de contratos a comprar
        order = client.futures_create_order(
            symbol=symbol,
            side=Client.SIDE_BUY,
            type=Client.ORDER_TYPE_MARKET,
            quantity=quantity
        )
        print(f"Señal de compra detectada. Precio de entrada: {last_close}")
        print(f"Nivel de stop loss: {stop_loss_price}")
        print(f"Nivel de take profit: {take_profit_price}")
        # Registrar la entrada en el archivo de trades
        register_trade(symbol, 'BUY', last_close, 0, 'Entry')
        entry_price = last_close

    if (generate_buy_signal(closes_arr) == False) and open_position==True:

        # Esperar un tiempo antes de verificar si se debe cerrar la posición por stop loss o take profit
        time.sleep(5)

        # Obtener el precio actual
        current_price = float(client.futures_symbol_ticker(symbol=symbol)['price'])

        # Verificar si se debe cerrar la posición por stop loss
        if current_price <= stop_loss_price:

            # Ejecutar la orden de venta por stop loss
            print(f"Se alcanzó el nivel de stop loss. Precio de salida: {stop_loss_price}")
            order = client.futures_create_order(
                symbol=symbol,
                side=Client.SIDE_SELL,
                type=Client.ORDER_TYPE_MARKET,
                quantity=quantity
            )

            # Registrar la salida por stop loss en el archivo de trades
            register_trade(symbol, 'SELL', entry_price, stop_loss_price, 'Stop Loss')
            return

        # Verificar si se debe cerrar la posición por take profit
        if current_price >= take_profit_price:

            # Ejecutar la orden de venta por take profit
            print(f"Se alcanzó el nivel de take profit. Precio de salida: {take_profit_price}")
            order = client.futures_create_order(
                symbol=symbol,
                side=Client.SIDE_SELL,
                type=Client.ORDER_TYPE_MARKET,
                quantity=quantity
            )

            # Registrar la salida por take profit en el archivo de trades
            register_trade(symbol, 'SELL', entry_price, take_profit_price, 'Take Profit')
            return


# Bu    cle principal para ejecutar la estrategia en tiempo real
while True:
    try:
        run_strategy()
    except Exception as e:
        print(f"Ocurrió un error: {str(e)}")

    # Esperar un tiempo antes de volver a ejecutar la estrategia
    time.sleep(2)  # Esperar 30 segundos
