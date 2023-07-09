#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Fri Apr 14 13:51:36 2023

@author: atorres
"""
from notion_client import Client
from notion_client.errors import APIResponseError

notion = Client(auth="secret_ckY5BWvhJiJexE0iCrSLP9s87V8DLReOhEtlOjeiUp5")

# Aquí debes colocar el ID de la base de datos a la que deseas escribir
database_id = "11341ac5d5524448aaedf772f0a45b49"

# Aquí debes colocar el ID de la página padre en la que deseas crear la nueva página
parent_page_id = "ef4cbabba759438f8317a784468d7d1b"

# Crear una nueva página en la base de datos especificando la página padre
try:
    new_page = notion.pages.create(
        parent={"page_id": parent_page_id},
        properties={
            "Propiedad 1": {"title": [{"text": {"content": "Valor 1"}}]},
            "Propiedad 2": {"number": 123},
            "Propiedad 3": {"checkbox": True},
        },
    )
    print("Nueva página creada con éxito:", new_page)
except APIResponseError as e:
    print(e)
    new_page = None  # Definir la variable `new_page` como `None` en caso de error

# Utilizar la variable `new_page` después de su definición en el bloque `try`
if new_page is not None:
    # Hacer algo con `new_page` aquí
    pass
else:
    print("No se pudo crear la nueva página en Notion.")