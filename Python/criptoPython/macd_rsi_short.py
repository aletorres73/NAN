#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Wed Apr 19 18:29:08 2023

@author: atorres
"""
import pandas as pd
from binance.client import Client
#from binance.enums import *
import talib

# Autenticación con la API de Binance
api_key = 'Bf8oaYKwaPSwlv6AmARm6TVNN4asSkX60VNQonlAbEueQzvuUGMYZMGU5OdND9i3'
api_secret = 'nbocErHKQgxPNd2Agg3i0zRVAGEJmXOwmNZ8eUJRsNv70a2Y1pRFO7tYZO2CU9SN'

client = Client(api_key, api_secret)

# Par de trading y configuración de la estrategia
pair = 'BTCUSDT'
interval = Client.KLINE_INTERVAL_1HOUR
macd_fast = 12
macd_slow = 26
macd_signal = 9
rsi_period = 14
rsi_buy_threshold = 30

# Obtener datos históricos de precios
klines = client.futures_klines(symbol=pair, interval=interval, limit=200)
data = pd.DataFrame(klines, columns=['timestamp', 'open', 'high', 'low', 'close', 'volume', 'close_time', 'quote_asset_volume', 'number_of_trades', 'taker_buy_base_asset_volume', 'taker_buy_quote_asset_volume', 'ignore'])
data = data.drop(columns=['timestamp', 'close_time', 'quote_asset_volume', 'number_of_trades', 'taker_buy_base_asset_volume', 'taker_buy_quote_asset_volume', 'ignore'])
data = data.astype('float64')

# Calcular el MACD
macd, macdsignal, macdhist = talib.MACD(data['close'], fastperiod=macd_fast, slowperiod=macd_slow, signalperiod=macd_signal)

# Calcular el RSI
rsi = talib.RSI(data['close'], timeperiod=rsi_period)

# Calcular las señales de trading
data['macd'] = macd
data['macdsignal'] = macdsignal
data['rsi'] = rsi

data['macd_buy_signal'] = ((macd > macdsignal) & (macd.shift(1) < macdsignal.shift(1)))
data['rsi_buy_signal'] = (rsi < rsi_buy_threshold)

data['buy_signal'] = data['macd_buy_signal'] & data['rsi_buy_signal']

# Iniciar la operación de trading
position = 'out'
for index, row in data.iterrows():
    if row['buy_signal'] and position == 'out':
        # Abrir una posición corta
        order = client.futures_create_order(symbol=pair, side='SELL', type='MARKET', quantity=100, reduceOnly=True, timeInForce='GTC', workingType='CONTRACT_PRICE', closePosition=True)
        print('Posición abierta')
        print(order)
        position = 'in'
    elif position == 'in':
        # Cerrar la posición corta
        order = client.futures_create_order(symbol=pair, side='BUY', type='MARKET', quantity=100, reduceOnly=True, timeInForce='GTC', workingType='CONTRACT_PRICE')
        print('Posición cerrada')
        print(order)
        position = 'out'
