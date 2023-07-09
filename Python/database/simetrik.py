# -*- coding: utf-8 -*-
"""
Spyder Editor

This is a temporary script file.
"""
import csv
import sqlite3


def createTable(name, header, rows, cursor):
    newHeader = []
    for col in header:
        clean_col = col.replace("\ufeff", '')  # Eliminar caracteres especiales
        newHeader.append(clean_col)
    # Crear la tabla con el nombre especificado        
    create_table_query = 'CREATE TABLE {} ({})'.format(name, ', '.join(['"{}"'.format(col) for col in newHeader]))
    cursor.execute(create_table_query)

    # Insertar los datos en la tabla
    insert_data_query = 'INSERT INTO {} VALUES ({})'.format(name, ', '.join(['?' for _ in newHeader]))
    cursor.executemany(insert_data_query, rows)

def modifyTable(name, nameR, header, rows, cursor):
    newHeader = []
    for col in header:
        clean_col = col.replace("\ufeff", '')  # Eliminar caracteres especiales
        newHeader.append(clean_col)
    
    # Crear la tabla con el nombre especificado
    create_table_query = 'CREATE TABLE {} (ID INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0, {}, CRUZADO DEFAULT "-")'.format(name, ', '.join(['"{}"'.format(col) for col in newHeader]))
    cursor.execute(create_table_query)
    
    # Insertar los datos en la tabla
    insert_data_query = 'INSERT INTO {} ({},CRUZADO) VALUES ({},"{}")'.format(name, ', '.join(header), ', '.join(['?' for _ in newHeader]),'-')
    cursor.executemany(insert_data_query, rows)
    
        
    # Borrar la tabla nameR si existe
    drop_table_query = 'DROP TABLE IF EXISTS {}'.format(nameR)
    cursor.execute(drop_table_query)
    
    
    # Renombrar la tabla name a nameR
    rename_table_query = 'ALTER TABLE {} RENAME TO {}'.format(name, nameR)
    cursor.execute(rename_table_query)

def infoTable(name):
    table_name = name
    # Obtener la lista de columnas y tipos de datos
    query_columns = "PRAGMA table_info({})".format(table_name)
    cursor.execute(query_columns)
    columns_info = cursor.fetchall()
    print (columns_info)
    


clap_file_path = '/home/aletorres/OneDrive/AleOD_/WORK/Prueba Técnica-selected/CLAP.csv'
bansur_file_path = '/home/aletorres/OneDrive/AleOD_/WORK/Prueba Técnica-selected/BANSUR.csv'

with open(clap_file_path, 'r') as clapfile:
    clap_data = csv.reader(clapfile)
    clap_header = next(clap_data)  # Lee la primera línea (encabezados)
    clap_rows = list(clap_data)  # Lee las filas restantes

with open(bansur_file_path, 'r') as bansurfile:
    bansur_data = csv.reader(bansurfile)
    bansur_header = next(bansur_data)  # Lee la primera línea (encabezados)
    bansur_rows = list(bansur_data)  # Lee las filas restantes


conn = sqlite3.connect(':memory:')
cursor = conn.cursor()

createTable('clap', clap_header, clap_rows, cursor)
createTable('bansur', bansur_header, bansur_rows, cursor)


query = '''SELECT SUM(MONTO), COUNT(*) FROM clap WHERE TIPO_TRX = 'PAGADA' ORDER BY FECHA_TRANSACCION'''

cursor.execute(query)
result_clap = cursor.fetchone()

print("Monto : $",round(result_clap[0],2))
print("Cantidad: $",result_clap[1])

query = '''SELECT SUM(MONTO), COUNT(*) FROM bansur WHERE TIPO_TRX = 'PAGO' ORDER BY FECHA_TRANSACCION'''

cursor.execute(query)
result_bansur = cursor.fetchone()

print("Monto : $",round(result_bansur[0],2))
print("Cantidad: $",result_bansur[1])

query = '''SELECT SUM(MONTO), COUNT(*) FROM bansur ORDER BY FECHA_TRANSACCION'''

cursor.execute(query)
result_bansur_all = cursor.fetchone()

print("Monto total registrado en BANSUR: $", round(result_bansur_all[0],2))
print("Cantidad total de trasacciones registradas en BANSUR: $",result_bansur_all[1])

query = '''SELECT SUM(MONTO), COUNT(*) FROM clap ORDER BY FECHA_TRANSACCION'''

cursor.execute(query)
result_clap_all = cursor.fetchone()
print("\nMonto total registrado en CLAP: $", round(result_clap_all[0],2))
print("Cantidad total de trasacciones registradas en CLAP",result_clap_all[1])

print("Porcentaje de transacciones pagadas sobre las totales en CLAP : %",round(result_clap[0]/result_clap_all[0],3))
print("Porcentaje de las cantidades pagadas sobre las cantidades totales en CLAP: %",round(result_clap[1]/result_clap_all[1],3))

print("\nPorcentaje de transacciones pagadas sobre las totales en BANSUR : %",round(result_bansur[0]/result_bansur_all[0],3))
print("Porcentaje de las cantidades pagadas sobre las cantidades totales en BANSUR  : %",round(result_bansur[1]/result_bansur_all[1],3))

#####################################################################################################
    

modifyTable('clapTemp', 'clap', clap_header, clap_rows, cursor)
modifyTable('bansurTemp','bansur', bansur_header, bansur_rows, cursor)

query = '''
    UPDATE clap
    SET FECHA_TRANSACCION = strftime('%Y%m%d', FECHA_TRANSACCION)
'''

cursor.execute(query)

query = '''
    UPDATE clap
    SET CRUZADO = 'si'
    FROM bansur
    WHERE clap.ID_BANCO = bansur.ID_ADQUIRIENTE
    AND clap.TIPO_TRX = 'PAGADA'
    AND bansur.TIPO_TRX = 'PAGO'
    AND clap.INICIO6_TARJETA || FINAL4_TARJETA = bansur.TARJETA
    AND (clap.MONTO = bansur.MONTO OR ABS(clap.MONTO - bansur.MONTO) <= 0.99)
    AND clap.FECHA_TRANSACCION = bansur.FECHA_TRANSACCION
'''

cursor.execute(query)

query = '''
    SELECT *
    FROM clap
    WHERE CRUZADO = 'si'
    ORDER BY FECHA_TRANSACCION
'''

cursor.execute(query)
clap_cruzado = cursor.fetchmany(5)  # Obtener las primeras 10 líneas

for row in clap_cruzado:
    print(row)
    
query = '''
    UPDATE bansur
    SET CRUZADO = 'si'
    FROM clap
    WHERE clap.ID_BANCO = bansur.ID_ADQUIRIENTE
    AND clap.TIPO_TRX = 'PAGADA'
    AND bansur.TIPO_TRX = 'PAGO'
    AND clap.INICIO6_TARJETA || FINAL4_TARJETA = bansur.TARJETA
    AND (clap.MONTO = bansur.MONTO OR ABS(clap.MONTO - bansur.MONTO) <= 0.99)
    AND clap.FECHA_TRANSACCION = bansur.FECHA_TRANSACCION
'''

cursor.execute(query)

query = '''
    SELECT *
    FROM bansur
    WHERE CRUZADO = 'si'
    ORDER BY FECHA_TRANSACCION
'''

cursor.execute(query)
clap_cruzado = cursor.fetchmany(5)  # Obtener las primeras 10 líneas

for row in clap_cruzado:
    print(row)




