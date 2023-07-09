from binance.client import Client
import pandas as pd
from tabulate import tabulate
import mediaStrategyModule as md

api_key = '7CqLqaSvEKOqkTvuyUL9Ecwl8FpZ018Rnfmv710jDFAH6t5i03DTYQITMQRqSmj3'
api_secret = 'sD01P5o2J9uNHurOkFfJ7iWPEoVy6OmuEnPXqjPV2W835cVgrCnswq2OqBGMd7Rb'
client = Client(api_key, api_secret)

# Parámetros de la estrategia
fast_length     = 20
slow_length     = 60

# risk_percentage = 0.01
# stop_loss       = 1 - risk_percentage
# take_profit     = 1 + risk_percentage* 5

capitalInicial = 20
positions = []
rendimientos_finales= []
stop_loss_values= []
take_profit_values= []
leverage_value=[]


# Función para generar señal de compra
def generate_buy_signal(closes):
    maFast = closes.rolling(window=fast_length).mean()
    maSlow = closes.rolling(window=slow_length).mean()

    if maFast.iloc[-1] > maSlow.iloc[-1] and maFast.iloc[-2] <= maSlow.iloc[-2]:
        return True
    else:
        return False

def get_positions(positions):

        if len(positions) > 0:
            return True
        else:
            return False



def generate_sell_signal(current_price, stop_loss_price, take_profit_price):
    if current_price >= take_profit_price or current_price <= stop_loss_price:
        return True
    return False

def simulate_strategy(symbol, interval, start_date, leverage, take_profit, stop_loss ):

    
    historical_klines = client.futures_historical_klines(symbol, interval, start_date, 'now UTC')

    # Convertir los datos históricos en un DataFrame de pandas para facilitar su manejo
    df = pd.DataFrame(historical_klines, columns=['timestamp', 'open', 'high', 'low', 'close', 'volume', 'close_time',
                                              'quote_asset_volume', 'number_of_trades', 'taker_buy_base_asset_volume',
                                              'taker_buy_quote_asset_volume', 'ignore'])
    df['timestamp'] = pd.to_datetime(df['timestamp'], unit='ms')
    df['close'] = pd.to_numeric(df['close'])
    data = df['close']
    
    # df_final =pd.DataFrame(columns=['Symbol','Temp', 'Leverage', 'Stop Loss', 'Take Profit'
    #                                         'Capital Inicial', 'Capital Final', 'Ganancia Neta',
    #                                             'Rendimiento Final', 'Operaciones Totales',
    #                                                  'Operaciones Ganadoras', 'Ratio Positivo'])

    df_operaciones = pd.DataFrame(columns=['Tipo','Precio de entrada','stop_loss_price','take_profit_price',
                            'Tamaño $','Salida','Resultado','Capital actual'])

    capitalFinal = capitalInicial
    sell_signal = False
    #open_position = False
    operaciones_totales=0
    operaciones_ganadoras=0


    for i in range(len(data)):

        current_price = data[i]
        buy_signal = generate_buy_signal(data[:i+1])

        # if len(positions) > 0:
        #     open_position = True
        #     sell_signal = generate_sell_signal(current_price, positions[-1]['stop_loss_price'], positions[-1]['take_profit_price'])
        # else:
        #     open_position = False

        if get_positions(positions):
            sell_signal = generate_sell_signal(current_price, positions[-1]['stop_loss_price'], positions[-1]['take_profit_price'])

        if buy_signal and not get_positions(positions):
            #quantity = 1
            quantity = (capitalFinal*leverage)/current_price
            positions.append({
                'entry_price': current_price,
                'stop_loss_price': current_price * stop_loss,
                'take_profit_price': current_price * take_profit
            })

            sell_signal = generate_sell_signal(current_price, positions[-1]['stop_loss_price'], positions[-1]['take_profit_price'])
            operaciones_totales += 1
            
            aux = pd.DataFrame({'Tipo':['BUY'],'Precio de entrada':[current_price], 'stop_loss_price':[current_price * stop_loss],
                                    'take_profit_price':[current_price * take_profit],'Tamaño $':[current_price*quantity],
                                        'Salida':['-'],'Resultado':[0.0],'Capital actual':[capitalFinal]})




        elif sell_signal and get_positions(positions):
            # Realizar la venta y calcular el rendimiento
            position = positions[-1]
            exit_price = current_price
            capitalFinal += round((exit_price - position['entry_price']) * quantity,2)
            positions.pop()

            operaciones_totales += 1

            if ((exit_price - position['entry_price']) * quantity) > 0:
                operaciones_ganadoras += 1
                aux['Salida'] = 'TP'
                aux['Resultado']= round((exit_price - position['entry_price']) * quantity,2)
                aux['Capital actual']= capitalFinal
                df_operaciones = pd.concat([df_operaciones,aux],ignore_index=True)
            else:
                aux['Salida'] = 'SL'
                aux['Resultado']= round((exit_price - position['entry_price']) * quantity,2)
                aux['Capital actual']= capitalFinal
                df_operaciones = pd.concat([df_operaciones,aux],ignore_index=True)


            if capitalFinal < 0:
                print(f"Capital actual negativo: -${capitalFinal}\n")
                return


    if get_positions(positions):
            
            # Realizar la venta y calcular el rendimiento
            position = positions[-1]
            exit_price = current_price
            capitalFinal += (exit_price - position['entry_price']) * quantity
            positions.pop()

            operaciones_totales += 1

            if ((exit_price - position['entry_price']) * quantity) > 0:
                operaciones_ganadoras += 1

    df_final =pd.DataFrame({'Symbol':[symbol],
                            'Temp':[interval],
                            "Rango de tiempo":[start_date],
                            'Leverage':[leverage],
                            'Stop Loss':[stop_loss],
                            'Take Profit':[take_profit],
                            'Capital Inicial':[capitalInicial], 
                            'Capital Final':[round(capitalFinal, 2)], 
                            'Ganancia Neta':[round(capitalFinal - capitalInicial, 2)],
                            'Rendimiento Final':[((capitalFinal - capitalInicial) / capitalInicial * 100)],
                            'Operaciones Totales':[operaciones_totales],
                            'Operaciones Ganadoras':[operaciones_ganadoras],
                            'Ratio Positivo':[round(operaciones_ganadoras / operaciones_totales * 100,2)]})
    
    rendimientos_finales.append(df_final['Rendimiento Final'].values[0])

    print(f"{df_final}")
    print(f"{df_operaciones}\n")


    return df_final


try:
#start_date = '1 day ago UTC'
#start_date = '1 week ago UTC'
#start_date = '1 month ago UTC'
#start_date = '1 year ago UTC'
    print("SIMULACIÓN ESTRATEGIA: Cruce de medias móviles\n")
    print("Cargando precios... ")
    for i in range(1):
        risk_percentage = 0.01
        leverage        = i + 1
        leverage_value.append(leverage)

        for j in range(3):
            stop_loss       = 1 - risk_percentage
            take_profit     = 1 + risk_percentage*(j+1)


            stop_loss_values.append(stop_loss)
            take_profit_values.append(take_profit)

            # Ejecutar simulate_strategy() y obtener el rendimiento final
            df_final = simulate_strategy('ETHUSDT', '1h', '1 month ago UTC', leverage, take_profit, stop_loss)

            rendimientos_finales.append([stop_loss, take_profit, leverage, df_final['Rendimiento Final'].values[0]])

        max_rendimiento = max(rendimientos_finales, key=lambda x: x[3])
        indice_max_rendimiento = rendimientos_finales.index(max_rendimiento)
        mejor_configuracion = f"Leverage: {max_rendimiento[2]}, Stop Loss: {max_rendimiento[0]}, Take Profit: {max_rendimiento[1]}"
        print(f"La mejor configuración es: {mejor_configuracion}")
    
    max_rendimiento = max(rendimientos_finales)
    indice_max_rendimiento = rendimientos_finales.index(max_rendimiento)
    mejor_configuracion = f"Leverage: {indice_max_rendimiento + 1}, Stop Loss: {stop_loss_values[indice_max_rendimiento]}, Take Profit: {take_profit_values[indice_max_rendimiento]}"
    print(f"La mejor configuración es: {mejor_configuracion}")

except Exception as e:
    print(f"Ocurrió un error: {str(e)}")