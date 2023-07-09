from bs4 import BeautifulSoup
import requests
import re
url = 'http://www.unipython.com/'

response = requests.get(url)

bs = BeautifulSoup(response.text, 'lxml')

# buscamos el contenedor de los enlaces, en este caso
# una etiqueta div con la clase entry-content

link_container = bs.find('div', {'class':'entry-content'})

# creamos un set para no reingresar enlaces repetidos

links = set()
for ul in link_container.find_all('ul'):
    a = ul.find('a', {'href':re.compile('.*\/curso\-.*')})
    if a == None:
        break
    else:
        link = a.get('href')
        if link not in links:
            print('[*] Obteniendo enlace: {}'.format(link))
            links.add(link)