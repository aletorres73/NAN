#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Wed Apr 19 09:03:25 2023

@author: atorres
"""
from binance.client import Client
import pandas as pd
import mplfinance as mpf

# Configurar la conexión a la API de Binance
api_key = 'Bf8oaYKwaPSwlv6AmARm6TVNN4asSkX60VNQonlAbEueQzvuUGMYZMGU5OdND9i3'
api_secret = 'nbocErHKQgxPNd2Agg3i0zRVAGEJmXOwmNZ8eUJRsNv70a2Y1pRFO7tYZO2CU9SN'
client = Client(api_key, api_secret)

# Obtener los datos históricos del mercado de Binance
symbol = 'BTCUSDT'
interval = '1h'
start_date = '1 year ago UTC'
end_date = 'now UTC'
historical_klines = client.futures_historical_klines(symbol, interval, start_date, end_date)

# Convertir los datos a un dataframe de pandas
df = pd.DataFrame(historical_klines, columns=['timestamp', 'open', 'high', 'low', 'close', 'volume', 'close_time', 'quote_asset_volume', 'number_of_trades', 'taker_buy_base_asset_volume', 'taker_buy_quote_asset_volume', 'ignore'])

# Convertir los timestamps a formato legible
df['timestamp'] = pd.to_datetime(df['timestamp'], unit='ms')

df = df.astype({'open': float, 'high': float, 'low': float, 'close':float, 'volume':float})

# Establecer el índice del dataframe como los timestamps
df = df.set_index('timestamp')

# Crear el gráfico de velas
#mpf.plot(df, type='candle', mav=(20, 50), volume=False, show_nontrading=True)

# Calcular las medias móviles de 20 y 50 períodos
sma_20 = df['close'].rolling(window=8).mean()
sma_50 = df['close'].rolling(window=100).mean()

# Crear una columna en el dataframe para almacenar las señales de compra y venta
df['Signal'] = 0

# Identificar las señales de compra (20 cruza hacia arriba a 50) y señales de venta (20 cruza hacia abajo a 50)
for i in range(1, len(df)):
    if sma_20.iloc[i] > sma_50.iloc[i] and sma_20.iloc[i-1] <= sma_50.iloc[i-1]:
        df.at[df.index[i], 'Signal'] = 1
    elif sma_20.iloc[i] < sma_50.iloc[i] and sma_20.iloc[i-1] >= sma_50.iloc[i-1]:
        df.at[df.index[i], 'Signal'] = -1

# Inicializar el saldo en USD y la posición en criptomoneda
porcentaje_capital=0.02
saldo_inicial=1000
saldo_usd = 1000
posicion_crypto = 0
precio_crypto = 0

# Recorrer el dataframe y simular compras y ventas basadas en las señales de compra y venta
for i in range(len(df)):
    precio_crypto = df['close'].iloc[i]
    # Si la señal es de compra y no tenemos una posición en criptomoneda
    if df['Signal'].iloc[i] == 1 and posicion_crypto == 0:
        # Comprar criptomoneda con el monto seleccionado
        posicion_crypto = (saldo_usd*porcentaje_capital) / precio_crypto
        saldo_usd -= posicion_crypto*precio_crypto
    # Si la señal es de venta y tenemos una posición en criptomoneda
    elif df['Signal'].iloc[i] == -1 and posicion_crypto > 0:
        # Vender la posición
        saldo_usd += posicion_crypto * precio_crypto
        posicion_crypto = 0

# Calcular la ganancia o pérdida final
saldo_final= saldo_usd + (posicion_crypto * df['close'].iloc[-1])
rentabilidad = ((saldo_final-saldo_inicial)/saldo_inicial)*100

# Imprimir la ganancia o pérdida final
print("Saldo final: ${:.2f}".format(saldo_final))
print("Rentabilidad final: %{:.2f}".format(rentabilidad))
