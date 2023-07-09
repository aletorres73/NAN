import pandas as pd
from binance.client import Client
import matplotlib
import matplotlib.pyplot as plt
import time

matplotlib.use('TkAgg')


def config_binance_api(symbol,leverage):

    # Configurar la conexión a la API de Binance
    api_key = '7CqLqaSvEKOqkTvuyUL9Ecwl8FpZ018Rnfmv710jDFAH6t5i03DTYQITMQRqSmj3'
    api_secret = 'sD01P5o2J9uNHurOkFfJ7iWPEoVy6OmuEnPXqjPV2W835cVgrCnswq2OqBGMd7Rb'
    client = Client(api_key, api_secret)
    client.futures_change_leverage(symbol=symbol, leverage=leverage)
    exchange_info = client.futures_exchange_info()
    positions = client.futures_position_information()

    return api_key,api_secret,client,exchange_info,positions


def get_min_info(symbol, word, exchange_info):
    #exchange_info = client.futures_exchange_info()
    symbols = exchange_info['symbols']

    if word == 'minQty':
        for symbol_info in symbols:
            if symbol_info['symbol'] == symbol:
                for filter_info in symbol_info['filters']:
                    if filter_info['filterType'] == 'LOT_SIZE':
                        return float(filter_info['minQty'])
    if word == 'notional':
        for symbol_info in symbols:
            if symbol_info['symbol'] == symbol:
                for filter_info in symbol_info['filters']:
                    if filter_info['filterType'] == 'MIN_NOTIONAL':
                        return float(filter_info['notional'])
    return None

# # Función para generar señal de compra
# def generate_buy_signal(closes): 

#     # Calcular las medias móviles
#     maFast = pd.Series(closes).rolling(window=20).mean()
#     maSlow = pd.Series(closes).rolling(window=60).mean()
#     last1_maFast = maFast.iloc[-1]
#     last2_maFast = maFast.iloc[-2]
#     last1_maSlow = maSlow.iloc[-1]
#     last2_maSlow = maSlow.iloc[-2]

#     # Generar señal de compra cuando se cumplan las condiciones
#     if last1_maFast > last1_maSlow and last2_maFast <= last2_maSlow:
#         return True
#     else:
#         return False

def generate_buy_signal(closes,current_price):
    maFast = pd.Series(closes).rolling(window=20).mean()
    maSlow = pd.Series(closes).rolling(window=60).mean()

    plt.ion()
    # Borrar la figura actual
    plt.clf()
    plt.clf()


    # Graficar las medias móviles
    plt.plot(closes, label='Closing Prices')
    plt.plot(maFast, label='Fast MA (20)')
    plt.plot(maSlow, label='Slow MA (60)')
    plt.legend()

    # Agregar el precio actual al gráfico
    plt.axhline(current_price, color='red', linestyle='--', label='Current Price')
    plt.legend()

    # Generar señal de compra cuando se cumplan las condiciones
    if maFast.iloc[-1] > maSlow.iloc[-1] and maFast.iloc[-2] <= maSlow.iloc[-2]:
        plt.scatter(len(closes) - 1, closes[-1], color='green', marker='^')
        plt.title('Buy Signal')
        plt.pause(0.1)  # Pausa breve para permitir la actualización del gráfico
        plt.show()
        return True
    else:
        plt.title('No Buy Signal')
        plt.pause(0.1)  # Pausa breve para permitir la actualización del gráfico
        plt.show()
        return False


def register_trade(symbol, side, entry_price, exit_price, exit_type, size_operation, current_capital):
    timestamp = pd.Timestamp.now()
    trade_data = {
        'Timestamp'     : [timestamp],
        'Symbol'        : [symbol],
        'Side'          : [side],
        'Entry Price'   : [entry_price],
        'Exit Price'    : [exit_price],
        'Exit Type '    : [exit_type],
        'Size Operation': [size_operation],
        'Curren Capital': [current_capital]
    }
    df = pd.DataFrame(trade_data)

    # Guardar los datos en el archivo CSV
    with open('trades.csv', 'a') as f:
        df.to_csv(f, header=f.tell() == 0, index=False)
    
def get_positions(positions,symbol):
    
    # Obtener las posiciones abiertas
    #positions = client.futures_position_information()
    for position in positions:
        if position['symbol'] == symbol and float(position['positionAmt']) != 0:
            return True
    return False
