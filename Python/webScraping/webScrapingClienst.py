import requests
from bs4 import BeautifulSoup
import pandas as pd
from selenium import webdriver
from selenium.webdriver.firefox.options import Options
from selenium.webdriver.firefox.firefox_binary import FirefoxBinary




def buscar_vendedores_calzado(zona):

    options = Options()
    options.headless = True  # Para ejecutar Firefox en modo headless, sin abrir una ventana visual

    binary = FirefoxBinary('/usr/bin/firefox')
    driver = webdriver.Firefox(firefox_binary=binary, options=options)

    url = f"https://www.google.com/search?q=vendedores+de+calzado+{zona}"

    driver.get(url)

    element = driver.find_elements('css selector','yuRUbf')

    html = element.get_attribute("outerHTML")

    driver.quit()


    headers = {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"
    }
    
    soup = BeautifulSoup(html, "html.parser")

    # resultados = soup.select(".tF2Cxc")  # Selector para los resultados de búsqueda
    # resultados = soup.find_all(class_="tF2Cxc")
    resultados = soup.find_all('div', {'class':'s6JM6d'})


    vendedores = []
    for resultado in resultados:
        titulo = resultado.select_one(".DKV0Md").text
        enlace = resultado.select_one(".yuRUbf a")["href"]
        vendedor = {"Nombre": titulo, "Zona": zona, "Teléfono": "", "Enlace": enlace}
        vendedores.append(vendedor)

    # # Iterar por los vendedores y obtener información adicional
    # for vendedor in vendedores:
    #     response = requests.get(vendedor["Enlace"], headers=headers)
    #     soup = BeautifulSoup(response.text, "html.parser")

    #     # Agregar código para obtener el número de teléfono o información adicional del vendedor
    #     # Utiliza técnicas de web scraping similares para extraer la información deseada

    #     # Actualiza el diccionario del vendedor con la información adicional
    #     vendedor["Teléfono"] = "123456789"  # Ejemplo: Asigna un número de teléfono ficticio

    # Crear DataFrame con los resultados
    df = pd.DataFrame(vendedores)

    # Imprimir el DataFrame en pantalla
    print(df)

# Ejemplo de búsqueda en la zona sur de Capital Federal, Argentina
buscar_vendedores_calzado("zona sur Capital Federal Argentina")
