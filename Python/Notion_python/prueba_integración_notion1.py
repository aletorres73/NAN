#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Fri Apr 14 11:15:33 2023

@author: atorres
"""

from notion_client import Client

notion = Client(auth="secret_ckY5BWvhJiJexE0iCrSLP9s87V8DLReOhEtlOjeiUp5")


# Obtener la base de datos
database_id = "b86a3d19fde143e083325aa4f9e44893"

# Leer los datos de la base de datos
results = notion.databases.query(database_id=database_id)

# Imprimir los datos de cada página en la base de datos
for page in results["results"]:
    print(page["properties"])
    
new_page={'Precio de envío($)':{'id': 'Exy%5E', 'type': 'number', 'number': 100},
          'Plataforma':{'id':'KZiC', 'type': 'select', 'select': None},
          'Precio de costo ($)':{'id': 'TDAI', 'type': 'number', 'number': 100},
          'Precio de venta ($)':{'id': 'k%3DCN', 'type': 'formula',
                                 'formula': {'type': 'number', 'number': 120}},
          'Publicación':{'id': 'title', 'type': 'title', 
                         'title':[{'type': 'text',
                                   'text':{'content': 'Auriculares', 'link': None},
                                   'annotations':{'bold': False, 'italic': False, 'strikethrough': False, 'underline': False, 'code': False, 'color': 'default'}, 
                                   'plain_text':'Auriculares', 'href': None}]}}

created_page = notion.pages.create(parent={"database_id": database_id},
                                                   properties=new_page)


    
    
    
    
    
    
    
    
    