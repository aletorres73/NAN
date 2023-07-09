
#from binance.client import Client
import time
import numpy as np
import mediaStrategyModule as st

# Establecer el apalancamiento x5
symbol       = 'ETHUSDT'
leverage     = 5
timeframe    = '1m'  # Cambiar a marco temporal de 1 hora
limit        = 100

api_key, api_secret, client, exchange_info, positions = st.config_binance_api(symbol,leverage)

# Definir variables
entry_price         = 0
stop_loss_price     = 0
take_profit_price   = 0
risk_percentage     = 0.01
capital_inicial     = 20
capital_actual      = capital_inicial 

# Función para ejecutar la estrategia
def run_strategy():
    
    klines      = client.futures_klines(symbol=symbol, interval=timeframe, limit=limit)
    closes      = [float(candle[4]) for candle in klines]
    closes_arr  = np.array(closes)

    # Obtener el último precio de cierre
    last_close  = closes[-1]

    # Calcular los niveles de stop loss y take profit
    stop_loss_price     = last_close * (1 - risk_percentage)
    take_profit_price   = last_close * (1 + risk_percentage)

    # Verificar si hay una operación abierta
    open_position = st.get_positions(positions,symbol)

    # Verificar la señal de compra
    if st.generate_buy_signal(closes_arr,last_close) == True and open_position == False:
        
        # Obtener el tamaño del contrato
        contract_size = st.get_min_info(symbol, 'minQty',exchange_info)

        # Calcular la cantidad de contratos a comprar
        quantity = 1 * contract_size

        # Verificar el tamaño mínimo de la orden
        min_notional = float( st.get_min_info(symbol, 'notional',exchange_info))
        if quantity * contract_size < min_notional/last_close:
            quantity = round(min_notional / last_close, 3)

        # # Ejecutar la orden de compra
        # order = client.futures_create_order(
        #     symbol=symbol,
        #     side=st.Client.SIDE_BUY,
        #     type=st.Client.ORDER_TYPE_MARKET,
        #     quantity=quantity
        # )
        print(f"Señal de compra detectada. Precio de entrada: ${last_close}")
        print(f"Nivel de stop loss:     ${stop_loss_price}")
        print(f"Nivel de take profit:   ${take_profit_price}")
        print(f"Tamaño de operación:    ${quantity * last_close}")
        print(f"Capital actual:         ${capital_actual - (quantity*last_close)}")

        # Registrar la entrada en el archivo de trades
        # st.register_trade(symbol, 'BUY', last_close, 0, 'Entry', size_operation=quantity*last_close, 
        #     current_capital=capital_actual - (quantity*last_close))
        # entry_price = last_close
    
    while open_position == True:

        # Esperar un tiempo antes de verificar si se debe cerrar la posición por stop loss o take profit
        time.sleep(1)

        current_price = float(client.futures_symbol_ticker(symbol=symbol)['price'])


        # Verificar si se debe cerrar la posición por stop loss
        if current_price <= stop_loss_price:

            # # Crear la orden de stop loss
            # stop_loss_order = client.futures_create_order(
            #     symbol=symbol,
            #     side=st.Client.SIDE_SELL,
            #     type=st.Client.ORDER_TYPE_STOP_MARKET,
            #     quantity=quantity,
            #     stopPrice=stop_loss_price,
            #     activationPrice=stop_loss_price
            # )
            # Obtener el precio actual
            current_price = float(client.futures_symbol_ticker(symbol=symbol)['price'])   
            open_position = False
            print(f"Salida por stopp loss al precio : ${stop_loss_price}")
            print(f"Resutado de operación:           -${quantity*stop_loss_price}")
            print(f"Capital actual:                   ${capital_actual-(quantity*stop_loss_price)}")
            print(f"Rendimiento de la operación:      %{round((stop_loss_price-entry_price)*quantity),2}")
            print(f"Rendimiento de la operación:      %{round(((stop_loss_price-entry_price)/entry_price)*100),2}")     

            # Registrar la salida por stop loss en el archivo de trades
            # st.register_trade(symbol, 'SELL', entry_price, stop_loss_price, 'Stop Loss',
            #     size_operation=quantity*stop_loss_price, current_capital=capital_actual-(quantity*stop_loss_price))        

        # Verificar si se debe cerrar la posición por take profit
        if current_price >= take_profit_price:

            # # Crear la orden de take profit
            # take_profit_order = client.futures_create_order(
            #     symbol=symbol,
            #     side=st.Client.SIDE_SELL,
            #     type=st.Client.ORDER_TYPE_TAKE_PROFIT,
            #     quantity=quantity,
            #     stopPrice=take_profit_price,
            #     activationPrice=take_profit_price
            # )
            # Obtener el precio actual
            current_price = float(client.futures_symbol_ticker(symbol=symbol)['price'])
            open_position = False
           
            print(f"Salida por take profit al precio: ${take_profit_price}")
            print(f"Resutado de operación:            ${quantity*take_profit_price}")
            print(f"Capital actual:                   ${capital_actual+(quantity*take_profit_price)}")
            print(f"Rendimiento de la operación:      ${round((take_profit_price-entry_price)*quantity),2}")     
            print(f"Rendimiento de la operación:      %{round(((take_profit_price-entry_price)/entry_price)*100),2}")     
            # Registrar la salida por take profit en el archivo de trades
            # st.register_trade(symbol, 'SELL', entry_price, take_profit_price, 'Take Profit',
            #     size_operation=quantity*stop_loss_price, current_capital=capital_actual-(quantity*stop_loss_price))
    

# Bucle principal para ejecutar la estrategia en tiempo real
while True:
    try:
        run_strategy()
        
    except Exception as e:
        print(f"Ocurrió un error: {str(e)}")

    # Esperar un tiempo antes de volver a ejecutar la estrategia
    #time.sleep(2)  # Esperar 2 segundos
