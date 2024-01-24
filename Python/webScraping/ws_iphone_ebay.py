# -*- coding: utf-8 -*-
"""
Created on Mon Jan 22 14:19:40 2024

@author: aleln
"""

from bs4 import BeautifulSoup
from selenium import webdriver
from selenium.webdriver import FirefoxOptions
from selenium.webdriver.common.by import By
import pandas as pd
import time

options = FirefoxOptions()
# options.add_argument('--headless')
options.binary_location = r"C:\Program Files\Mozilla Firefox\firefox.exe"
driver = webdriver.Firefox(options = options)

link = "https://www.ebay.com"
search= "iphone x".replace(" ", "+") 

driver.get(link+"/sch/i.html?_from=R40&_trksid=p4432023.m570.l1313&_nkw="+search+"&_sacat=0")

phone_title = []
phone_link = []
phone_status = []
phone_score = []
phone_reviews = []
phone_price = []
phone_location = []

page = BeautifulSoup(driver.page_source, 'html.parser')
pg_amount = 1

for i in range(0,pg_amount):

    for phone in page.findAll('li',attrs={'class':'s-item','data-view':True}):
        
        title = phone.find('div', attrs={'class':'s-item__title'})
        if title:
            phone_title.append(title.text)
        else: phone_title.append('')
        
        link = phone.find('a', attrs={'class':'s-item__link'})
        if link:
            phone_link.append(link['href'])
        else: phone_link.append('')
        
        status= phone.find('div',attrs={'class':'s-item__subtitle'})
        if status:
            phone_status.append(status.text)
        else: phone_status.append('')
        
        score = phone.find('div',attrs={'class':['b-starrating','x-star-rating']})
        if score:
            score.find('span',attrs={'class':'clipped'})
            if score:
                phone_score.append(score.text[0:3])
            else: phone_score.append('')
        else: phone_score.append('')
        
        reviews = phone.find('span',attrs={'classs':'s-item__reviews-count'})
        if reviews:
            phone_reviews.append(reviews.span.text[0:reviews.span.text.find('valor')-1])
        else: phone_reviews.append('')
        
        price = phone.find('span',attrs={'class':'s-item__price'})
        if price:
            phone_price.append(float(''.join((price.text[3:(price.text+' a').find(' a')]).split())))
        else: phone_price.append('')
        
        location = phone.find('span',attrs={'class':'s-item__location'})
        if location:
            phone_location.append(location.text[3:])
        else: phone_location.append('')
        
    next_buttom = driver.find_element(By.CLASS_NAME,'pagination__next')
    next_buttom.click()
    time.sleep(2)
    
phone_list = pd.DataFrame({
                            'TITLE':phone_title,
                            'STATUS':phone_score,
                            'SCORE':phone_status,
                            'REVIEWS':phone_reviews,
                            'PRICE':phone_price,
                            'LOCATION':phone_location,
                            'LINK':phone_link       
                            })

phone_list = phone_list.sort_values(by=['PRICE','SCORE','REVIEWS'],ascending=[True,False,False])

print(phone_list)
phone_list.to_csv(r'C:\Users\git\Projects\Python\webScraping\lista_prueba_iphone.csv',index=None, header=True, encoding='utf-8-sig')
    
    
    
    