# import requests
from bs4 import BeautifulSoup
import pandas as pd
from selenium import webdriver
# from selenium.webdriver.firefox.options import Options
from selenium.webdriver import FirefoxOptions
from selenium.webdriver.common.by import By
# from selenium.webdriver.firefox.firefox_binary import FirefoxBinary




def buscar_vendedores_calzado(zona):

    # options = Options()
    # options.headless = True  # Para ejecut|ar Firefox en modo headless, sin abrir una ventana visual

    # binary = FirefoxBinary('/usr/bin/firefox')
    # binary = FirefoxBinary("C:\Program Files\Mozilla Firefox")
    # driver = webdriver.Firefox(options=options, service=binary)
    
    options = FirefoxOptions()
    options.add_argument('--headless')
    # options.set_preference("browser.download.manager.showWhenStarting", False)
    options.binary_location = r"C:\Program Files\Mozilla Firefox\firefox.exe"
    driver = webdriver.Firefox(options = options)

    url = f"https://www.google.com/search?q=vendedores+de+calzado+{zona}"

    driver.get(url)

    element = driver.find_elements(By.CSS_SELECTOR,'div.yuRUbf h3.LC20lb')
    
    html = '\n'.join([h_element.get_attribute("innerHTML") for h_element in element])

    driver.quit()

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


    # Crear DataFrame con los resultados
    df = pd.DataFrame(vendedores)

    # Imprimir el DataFrame en pantalla
    print(df)

# Ejemplo de búsqueda en la zona sur de Capital Federal, Argentina
buscar_vendedores_calzado("zona sur Capital Federal Argentina")
