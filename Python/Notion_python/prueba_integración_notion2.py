#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Fri Apr 14 13:39:04 2023

@author: atorres
"""
from notion_client import Client
from pprint import pprint

# Aquí debes colocar el token de integración de Notion
notion = Client(auth="secret_ckY5BWvhJiJexE0iCrSLP9s87V8DLReOhEtlOjeiUp5")

# Aquí debes colocar el ID de la base de datos que deseas consultar
database_id = "b86a3d19fde143e083325aa4f9e44893"

# Consultar la base de datos
results = notion.databases.query(
    **{
        "database_id": database_id,
    }
).get("results")

# Imprimir los resultados
pprint(results)
