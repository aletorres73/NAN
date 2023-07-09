#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Wed Jun 21 14:00:00 2023

@author: aletorres
"""


import PyPDF2
import pandas as pd
import re

# Ruta del archivo PDF
archivo_pdf = "/home/aletorres/Descargas/Damian - parte 1 - 21.06.PDF"

# Variables auxiliares para rastrear el cliente actual
cliente_actual = None
dataframe_actual = None
dataframes_por_cliente = []
cont = 0

def getValores(linea, dataframe_actual):
    patron1 = r"\d+,\d+"
    patron2 = r"\d{1,2}/\d{1,2}/\d{4}"
    patron3 = r"\d+.\d+,\d+"
    patron4 = r"-\d+.\d+,\d+"

    valores = linea.split()

    coincidencia1 = re.search(patron1, valores[8])
    coincidencia2 = re.search(patron2, valores[8])
    coincidencia3 = re.search(patron3, valores[8])
    coincidencia4 = re.search(patron4, valores[8])

    if coincidencia4:
        valor = coincidencia4.group()
    elif coincidencia3:
        valor = coincidencia3.group()
    elif coincidencia1:
        valor = coincidencia1.group()

    fecha = valores[0]
    vence = coincidencia2.group()
    documento = valores[3]
    importe = valores[4]
    cancelado = valores[5]
    pendiente = valor
    saldo = valores[6]

    dataframe_actual.loc[len(dataframe_actual)] = [fecha, vence, documento, importe, cancelado, pendiente, saldo]

def getValoresCorriente(linea, dataframe_actual):
    valores = linea.split()
    dataframe_actual.loc[len(dataframe_actual)] = [valores[0], valores[8].replace("CUENTA", ""), valores[3], valores[4], valores[5], "", valores[6]]

def makeClientDF(linea):
    global cliente_actual, dataframe_actual
    cliente_actual = linea.split("Tercero")[-1].strip()
    dataframe_actual = pd.DataFrame(columns=["Fecha", "Vence", "Documento", "Importe", "Cancelado", "Pendiente", "Saldo"])
    dataframes_por_cliente.append({cliente_actual: dataframe_actual})

# Abrir el archivo PDF
with open(archivo_pdf, "rb") as archivo:
    lector_pdf = PyPDF2.PdfReader(archivo)

    # Extraer texto de las páginas
    lineas = []
    for pagina in lector_pdf.pages:
        texto = pagina.extract_text()
        lineas += texto.split("\n")

for linea in lineas:
    if "Tercero" in linea:
        makeClientDF(linea)
        continue

    if re.search(r"\d{4}-\d{8}", linea):
        if "CUENTA CORRIENTE" in linea:
            getValoresCorriente(linea, dataframe_actual)
            cont += 1
            continue
        elif "CONTADO" in linea:
            getValores(linea, dataframe_actual)
        else:
            getValores(linea, dataframe_actual)
    if cont == 1:
        valores = linea.split()
        pendiente = valores[2]
        dataframe_actual.loc[len(dataframe_actual)-1, "Pendiente"] = pendiente
        cont = 0
        

# Imprimir los DataFrames por cliente
for i, diccionario in enumerate(dataframes_por_cliente):
    cliente = list(diccionario.keys())[0]
    dataframe = diccionario[cliente]
    print(f"Cliente {i + 1}: {cliente}\n")
    print(dataframe)
    print("--------------------\n")
