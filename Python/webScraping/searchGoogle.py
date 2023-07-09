import urllib
import requests_html


def get_source(url):
    session = requests_html.HTMLSession()
    response = session.get(url)
    return response

def parse_results(response):
    CSS_IDENTIFICADOR_RESULTADO = ".tF2Cxc "
    CSS_IDENTIFICADOR_TITULO = "h3.LC20lb.MBeuO.DKV0Md"
    CSS_IDENTIFICADOR_ENLACE = ".yuRUbf a"
    CSS_IDENTIFICADOR_TEXTO = ".VwiC3b.yXK7lf.MUxGbd.yDYNvb.lyLwlc.lEBKkf"

    results = response.html.find(CSS_IDENTIFICADOR_RESULTADO)
    output = []
    
    for result in results:
        texto= result.find(CSS_IDENTIFICADOR_TEXTO, first=True)
        titulo= result.find(CSS_IDENTIFICADOR_TITULO, first=True)
        enlace= result.find(CSS_IDENTIFICADOR_ENLACE, first=True).attrs['href']

        if(hasattr(texto,'full_text')):
            item = {
                'titulo':titulo.text,
                'enlace':enlace,
                'texto': texto.full_text
            }
            output.append(item)
        if(hasattr(texto, 'text')):
            item = {
                'titulo':result.find(CSS_IDENTIFICADOR_TITULO, first=True).text,
                'enlace':result.find(CSS_IDENTIFICADOR_ENLACE, first=True).attrs['href'],
                'texto':result.find(CSS_IDENTIFICADOR_TEXTO, first=True).text
             }
            output.append(item)
            
    return output

def google_search(query):
    query = urllib.parse.quote_plus(query)
    response = get_source("https://www.google.com.ar/search?q=" + query + "&num=100")
    return parse_results(response)

texto_a_buscar = input("Que desea buscar? ").lower()
resultado_final = google_search(texto_a_buscar)
contador = 0

for resultado in resultado_final:
    contador += 1
    print("---Resultado # ", contador)
    print(resultado.get('titulo'))
    print(resultado.get('enlace'))
    print(resultado.get('texto'))
    print("------------------------------------")