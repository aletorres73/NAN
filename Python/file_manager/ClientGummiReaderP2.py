#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Wed Jun 21 22:09:58 2023

@author: aletorres
"""

import PyPDF2
import pandas as pd
import re

# Ruta del archivo PDF
archivo_pdf = "/home/aletorres/Descargas/Damian - parte 2 - 21.06.PDF"

# Variables auxiliares para rastrear el cliente actual
cliente_actual = None
dataframe_actual = None
dataframes_por_cliente = []
cont = 0

def getValores(linea, dataframe_actual):

    valores = linea.split()

    fecha       = valores[0]
    vence       = valores[8]
    documento   = valores[3]
    importe     = valores[4]
    cancelado   = valores[5]
    pendiente   = valores[9]
    saldo       = valores[6]

    dataframe_actual.loc[len(dataframe_actual)] = [fecha, vence, documento, importe, cancelado, pendiente, saldo]


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
        getValores(linea, dataframe_actual)


# Imprimir los DataFrames por cliente
for i, diccionario in enumerate(dataframes_por_cliente):
    cliente = list(diccionario.keys())[0]
    dataframe = diccionario[cliente]
    print(f"Cliente {i + 1}: {cliente}\n")
    print(dataframe)
    print("--------------------\n")
