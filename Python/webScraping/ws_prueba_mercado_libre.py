# -*- coding: utf-8 -*-
"""
Created on Tue Jan 23 10:28:19 2024

@author: aleln
"""


from bs4 import BeautifulSoup
from selenium import webdriver
from selenium.webdriver import FirefoxOptions
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import pandas as pd
import time

options = FirefoxOptions()
# options.add_argument('--headless')
options.binary_location = r"C:\Program Files\Mozilla Firefox\firefox.exe"
driver = webdriver.Firefox(options = options)

link    = "https://listado.mercadolibre.com.ar/"
search  = "moto g13".replace(" ", "+") 

driver.get(link+search+'#D')

page = BeautifulSoup(driver.page_source, 'html.parser')

product_title   = []
product_price   = []
product_link    = []
product_status  = []
product_reviews = []

try:
    cookie_banner = WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.CLASS_NAME, 'cookie-consent-banner-opt-out__container'))
    )
    # Puedes cerrar el banner de cookies de diversas maneras, aquí usaremos un clic en un botón de "Aceptar"
    accept_button = cookie_banner.find_element(By.XPATH, '//button[contains(text(), "Aceptar")]')
    accept_button.click()
except:
    # Manejar excepción si el banner de cookies no está presente o no se puede cerrar
    pass

page_amount = 1

print('Iniciando búsqueda...\n')

for i in range(0, page_amount):

    for block in page.findAll('li',attrs={'class':'ui-search-layout__item'}):
        group = block.find('a')
        if group:
            product_title.append(group['title'])
            product_link.append(group['href'])
        else:
            product_title.append('')
            product_link.append('')
            
        price = block.find('div',attrs={'class':'ui-search-price__second-line'})
        if price:
            product_price.append(price.text)
        else: 
            product_price.append('')
        
        reviews = block.find('span',attrs={'class':'ui-search-reviews__rating-number'})
        if reviews : 
            product_reviews.append(reviews.text)
        else       :
            product_reviews.append('')
        
        status = block.find('div',attrs={'class':'ui-search-item__group ui-search-item__group--variations-text'})
        if status :
            product_status.append(status.text)
        else      : 
            product_status.append('')
        
    next_buttom = driver.find_element(By.CLASS_NAME,'andes-pagination__button--next')
    next_buttom.click() 
    time.sleep(1)

print('Búsqueda finalizada.\n')
        
driver.close()
driver.quit()

product_list = pd.DataFrame({'TITLE'    : product_title,
                             'PRICE'    : product_price,
                             'STATUS'   : product_status,
                             'REVIEWS'  : product_reviews,
                             'LINK'     : product_link    })

print(product_list)

product_list = product_list.sort_values(by='PRICE',ascending=True)
product_list.to_csv(r'C:\Users\git\Projects\Python\webScraping\lista_prueba.csv',index=None, header=True, encoding='utf-8-sig')
    
    
