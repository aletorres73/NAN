from binance.client import Client
import pandas as pd
# import matplotlib
# import matplotlib.pyplot as plt


# matplotlib.use('TkAgg')

api_key = '7CqLqaSvEKOqkTvuyUL9Ecwl8FpZ018Rnfmv710jDFAH6t5i03DTYQITMQRqSmj3'
api_secret = 'sD01P5o2J9uNHurOkFfJ7iWPEoVy6OmuEnPXqjPV2W835cVgrCnswq2OqBGMd7Rb'
client = Client(api_key, api_secret)

symbol = 'ETHUSDT'
interval = '15m'
#start_date = '1 day ago UTC'
#start_date = '1 week ago UTC'
start_date = '1 month ago UTC'
#start_date = '1 year ago UTC'
end_date = 'now UTC'
historical_klines = client.futures_historical_klines(symbol, interval, start_date, end_date)

# Convertir los datos históricos en un DataFrame de pandas para facilitar su manejo
df = pd.DataFrame(historical_klines, columns=['timestamp', 'open', 'high', 'low', 'close', 'volume', 'close_time',
                                              'quote_asset_volume', 'number_of_trades', 'taker_buy_base_asset_volume',
                                              'taker_buy_quote_asset_volume', 'ignore'])
df['timestamp'] = pd.to_datetime(df['timestamp'], unit='ms')
df['close'] = pd.to_numeric(df['close'])

# Parámetros de la estrategia
fast_length     = 20
slow_length     = 60
risk_percentage = 0.01
stop_loss       = 1 - risk_percentage
take_profit     = 1 + risk_percentage* 5

capitalInicial = 20
positions = []
leverage = 6


# Función para generar señal de compra
def generate_buy_signal(closes,current_price):
    maFast = closes.rolling(window=fast_length).mean()
    maSlow = closes.rolling(window=slow_length).mean()

    if maFast.iloc[-1] > maSlow.iloc[-1] and maFast.iloc[-2] <= maSlow.iloc[-2]:
        return True
    else:
        return False
    # maFast = pd.Series(closes).rolling(window=20).mean()
    # maSlow = pd.Series(closes).rolling(window=60).mean()

    # plt.ion()
    # # Borrar la figura actual
    # plt.clf()
    # plt.clf()


    # # Graficar las medias móviles
    # plt.plot(closes, label='Closing Prices')
    # plt.plot(maFast, label='Fast MA (20)')
    # plt.plot(maSlow, label='Slow MA (60)')
    # plt.legend()

    # # Agregar el precio actual al gráfico
    # plt.axhline(current_price, color='red', linestyle='--', label='Current Price')
    # plt.legend()

    # # Generar señal de compra cuando se cumplan las condiciones
    # if maFast.iloc[-1] > maSlow.iloc[-1] and maFast.iloc[-2] <= maSlow.iloc[-2]:
    #     plt.scatter(len(closes) - 1, current_price, color='green', marker='^')
    #     plt.title('Buy Signal')
    #     plt.pause(0.0001)  # Pausa breve para permitir la actualización del gráfico
    #     plt.show()
    #     return True
    # else:
    #     plt.title('No Buy Signal')
    #     plt.pause(0.0001)  # Pausa breve para permitir la actualización del gráfico
    #     plt.show()
    #     return False

def generate_sell_signal(current_price, stop_loss_price, take_profit_price):
    if current_price >= take_profit_price or current_price <= stop_loss_price:
        return True
    return False

def simulate_strategy(data):

    capitalFinal = capitalInicial
    sell_signal = False
    open_position = False
    operaciones_totales=0
    operaciones_ganadoras=0

    for i in range(len(data)):

        current_price = data[i]
        buy_signal = generate_buy_signal(data[:i+1],current_price)

        if len(positions) > 0:
            open_position = True
            sell_signal = generate_sell_signal(current_price, positions[-1]['stop_loss_price'], positions[-1]['take_profit_price'])
        else:
            open_position = False

        if buy_signal and not open_position:
            #quantity = 1
            quantity = (capitalFinal*leverage)/current_price
            positions.append({
                'entry_price': current_price,
                'stop_loss_price': current_price * stop_loss,
                'take_profit_price': current_price * take_profit
            })
            # print(f"Señal de compra detectada en el paso de tiempo {i}. Precio de entrada: {current_price}")
            # print(f"Nivel de stop loss: {positions[-1]['stop_loss_price']}")
            # print(f"Nivel de take profit: {positions[-1]['take_profit_price']}\n")
            sell_signal = generate_sell_signal(current_price, positions[-1]['stop_loss_price'], positions[-1]['take_profit_price'])
            operaciones_totales += 1


        elif sell_signal and open_position:
            # Realizar la venta y calcular el rendimiento
            position = positions[-1]
            exit_price = current_price
            capitalFinal += (exit_price - position['entry_price']) * quantity
            positions.pop()
            # print(f"Señal de venta detectada en el paso de tiempo {i}. Precio de salida: {current_price}")
            # print(f"Capital remanente: ${capitalFinal}")
            # print(f"Rendimiento de la operación: {(exit_price - position['entry_price']) * quantity}\n")
            operaciones_totales += 1

            if ((exit_price - position['entry_price']) * quantity) > 0:
                operaciones_ganadoras += 1
            
            if capitalFinal < 0:
                print(f"Capital actual negativo: -${capitalFinal}\n")
                return
    if open_position:
            
            # Realizar la venta y calcular el rendimiento
            position = positions[-1]
            exit_price = current_price
            capitalFinal += (exit_price - position['entry_price']) * quantity
            positions.pop()
            # print(f"Señal de venta detectada en el paso de tiempo {i}. Precio de salida: {current_price}")
            # print(f"Capital remanente: ${capitalFinal}")
            # print(f"Rendimiento de la operación: {(exit_price - position['entry_price']) * quantity}\n")
            operaciones_totales += 1

            if ((exit_price - position['entry_price']) * quantity) > 0:
                operaciones_ganadoras += 1


    print(f"Par operado:                    {symbol}")
    print(f"Temporalidad:                   {interval}")
    print(f"Rango de tiempo:                {start_date}\n")
    print(f"Apalancamiento:                 {leverage}")
    print(f"Stop loss:                      {stop_loss}")
    print(f"Take profit:                    {take_profit}\n")
    print(f"Capital inicial:                ${capitalInicial}")
    print(f"Capital final:                  ${round(capitalFinal,2)}")
    print(f"Ganancia neta:                  ${round(capitalFinal-capitalInicial,2)}")
    print(f"Rendimiento final:              %{round(((capitalFinal-capitalInicial)/capitalInicial)*100,2)}\n")
    print(f"Operaciones totales:            {operaciones_totales}")
    print(f"Operaciones ganadoras:          {operaciones_ganadoras}")
    print(f"Ratio de operaciones positivas: {operaciones_ganadoras/operaciones_totales}")

    return 

try:
    simulate_strategy(df['close'])
except Exception as e:
    print(f"Ocurrió un error: {str(e)}")