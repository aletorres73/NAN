#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Wed Apr 19 17:01:42 2023

@author: atorres
"""
import pandas as pd

# DataFrame original
df = pd.DataFrame({'A': [1, 2], 'B': [3, 4]})

# Fila que se agregará
new_row = pd.DataFrame({'A': [5], 'B': [6]})

# Concatenar DataFrame original y fila
df = pd.concat([df, new_row], ignore_index=True)

# Inicializar un DataFrame para almacenar las operaciones

operaciones = pd.DataFrame({'Tipo':[], 'Cantidad':[], 'Precio':[]})

for i in range(10):
    operaciones = operacion.loc([operaciones,operaciones2])
