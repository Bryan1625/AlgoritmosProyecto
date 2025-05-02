import os

import requests
import time
import bibtexparser
from bibtexparser.bwriter import BibTexWriter
from bs4 import BeautifulSoup
from playwright.sync_api import sync_playwright
from cryptography.fernet import Fernet
import json

class Biblioteca:
    HEADERS = {"User-Agent": "Mozilla/5.0"}

    @staticmethod
    def buscar_sciencedirect(query, correo, contrasena, cantidad):
        with sync_playwright() as p:
            navegador = p.chromium.launch(channel="chrome", headless=False)
            pagina = navegador.new_page()

            # Acceder a través del proxy
            pagina.goto("https://login.intelproxy.com/v2/inicio?cuenta=7Ah6RNpGWF22jjyq&url=ezp.2aHR0cHM6Ly93d3cuc2NpZW5jZWRpcmVjdC5jb20-")
            pagina.wait_for_timeout(2000)

            # Iniciar sesión con Google
            pagina.click("a#btn-google")
            pagina.wait_for_selector("input[type='email']", timeout=10000)
            pagina.fill("input[type='email']", correo)
            pagina.click("button:has-text('Siguiente')")
            pagina.wait_for_timeout(3000)

            pagina.wait_for_selector("input[type='password']", timeout=10000)
            pagina.fill("input[type='password']", contrasena)
            pagina.click("button:has-text('Siguiente')")
            pagina.wait_for_timeout(5000)

            # Buscar artículos
            pagina.wait_for_selector("input#qs", timeout=10000)
            pagina.focus("input#qs")
            pagina.keyboard.type(query, delay=100)
            pagina.click("span.button-text:has-text('Search')")

            pagina.wait_for_selector("li.ResultItem", timeout=15000)

            total_descargados = 0
            pagina_number = 1

            while total_descargados < cantidad:
                resultados = pagina.locator("li.ResultItem")
                num_resultados_pagina = resultados.count()
                print(f"Resultados detectados en página {pagina_number}: {num_resultados_pagina}")

                for j in range(num_resultados_pagina):
                    index_global = total_descargados  # índice absoluto (0-based)
                    if index_global >= cantidad:
                        break
                    print(f"Procesando artículo #{index_global + 1}")
                    item_element = resultados.nth(j).element_handle()
                    Biblioteca.exportar_cita_sciencedirect(pagina, item_element, index_global + 1)
                    total_descargados += 1

                if total_descargados < cantidad:
                    print(f"Cargando página {pagina_number + 1}")
                    try:
                        pagina.click("span.anchor-text:has-text('Next')")
                        pagina.wait_for_selector("li.ResultItem", timeout=15000)
                        pagina_number += 1
                    except Exception as e:
                        print(f"No hay más páginas disponibles: {e}")
                        break

            navegador.close()


    @staticmethod
    def exportar_cita_sciencedirect(pagina, item_element, indice_articulo):
        try:
            item_element.scroll_into_view_if_needed()
            print("Artículo localizado")

            # Obtener el número de artículo (valor visible en el checkbox)
            numero_articulo_span = item_element.query_selector("span.checkbox-label-value")
            numero_articulo = numero_articulo_span.inner_text().strip()
            print(f"Número de artículo: {numero_articulo}")

            # Seleccionar el label del checkbox correspondiente
            label_selector = f"label.checkbox-label:has(span.checkbox-label-value:text-is('{numero_articulo}'))"
            label = pagina.locator(label_selector).first
            label.click()
            print(f"Checkbox del artículo #{numero_articulo} seleccionado")

            # Hacer clic en 'Export' general
            pagina.locator("span.export-all-link-text", has_text="Export").click()
            print("Clic en enlace 'Export'")

            # Esperar el botón de exportación a BibTeX aparezca y esté visible
            exportar_boton = pagina.locator("button[data-aa-button='srp-export-multi-bibtex']").first
            exportar_boton.wait_for(state="visible", timeout=10000)

            # Esperar la descarga al hacer clic
            with pagina.expect_download() as download_info:
                exportar_boton.click()
                print("Clic en 'Export citation to BibTeX'")

            download = download_info.value

            # Leer el contenido de la descarga desde su archivo temporal
            ruta_temporal = download.path()
            with open(ruta_temporal, "r", encoding="utf-8") as f:
                contenido = f.read()

            # Extraer el DOI del contenido BibTeX
            doi = None
            titulo = None
            for linea in contenido.splitlines():
                if 'doi' in linea.lower() and doi is None:
                    doi = linea.split('=')[1].strip().strip('{}").,')
                if 'title' in linea.lower() and titulo is None:
                    titulo = linea.split('=')[1].strip().strip('{}").,')

            # Revisar si el DOI ya existe
            if doi:
                if Biblioteca.doi_existente(doi):
                    print(f"El DOI {doi} ya existe, no se guarda la descarga.")
                else:
                    ruta_destino = os.path.join(
                        r"C:\Users\Bryan\Documents\btw\code\Programacion\AlgoritmosProyecto\src\main\resources",
                        f"{download.suggested_filename}"
                    )
                    download.save_as(ruta_destino)
                    print(f"Cita descargada: {ruta_destino}")
                    Biblioteca.reordenar_y_filtrar_bibtex(ruta_destino)
            else:
                if titulo and Biblioteca.titulo_existente(titulo):
                    print(f"El título '{titulo}' ya existe, no se guarda la descarga.")
                else:
                    ruta_destino = os.path.join(
                        r"C:\Users\Bryan\Documents\btw\code\Programacion\AlgoritmosProyecto\src\main\resources",
                        f"{download.suggested_filename}"
                    )
                    download.save_as(ruta_destino)
                    print(f"Cita descargada (por título): {ruta_destino}")
                    Biblioteca.reordenar_y_filtrar_bibtex(ruta_destino)

            # Desmarcar la casilla
            aria_checked = label.get_attribute("aria-checked")
            label.click()

        except Exception as e:
            print(f"Error al exportar cita del artículo: {e}")



    @staticmethod
    def buscar_sage(query):
        url = f"https://login.intelproxy.com/v2/inicio?cuenta=7Ah6RNpGWF22jjyq&url=ezp.2aHR0cHM6Ly9zay5zYWdlcHViLmNvbS8-"
        response = requests.get(url, headers=Biblioteca.HEADERS)
        soup = BeautifulSoup(response.text, "html.parser")
        articulos = []

        for item in soup.select(".art_title a"):
            titulo = item.text.strip()
            enlace = "https://journals.sagepub.com" + item["href"]
            cita = Biblioteca.extraer_cita_sage(enlace)
            articulos.append({"titulo": titulo, "enlace": enlace, "cita": cita})

        return articulos

    @staticmethod
    def extraer_cita_sage(url):
        with sync_playwright() as p:
            navegador = p.chromium.launch(channel="chrome", headless=False)
            pagina = navegador.new_page()
            pagina.goto(url)
            pagina.click("#onetrust-reject-all-handler")
            pagina.wait_for_timeout(2000)
            pagina.click(".sage-icon--cite")
            pagina.wait_for_timeout(2000)
            pagina.select_option(".sage-select__select", "BibTeX")
            pagina.wait_for_timeout(1000)
            pagina.click("text=EXPORT")
            pagina.wait_for_timeout(2000)
            cita = pagina.locator("textarea").inner_text()
            navegador.close()
            return cita if cita else "Cita no encontrada"

    @staticmethod
    def buscar_ieee(query, cantidad: int):
        with sync_playwright() as p:
            navegador = p.chromium.launch(headless=False)
            pagina = navegador.new_page()
            pagina.goto("https://ieeexplore.ieee.org/")

            pagina.fill("input.Typeahead-input", query)
            pagina.press("input.Typeahead-input", "Enter")
            pagina.wait_for_timeout(5000)

            try:
                pagina.wait_for_selector("a.fw-bold[href*='/document/']", timeout=15000)
                resultados = pagina.locator("a.fw-bold[href*='/document/']").all()
            except:
                print("No se encontraron artículos en IEEE.")
                navegador.close()


            for r in resultados[:3]:
                try:
                    r.scroll_into_view_if_needed()
                    r.click(force=True)
                    pagina.wait_for_timeout(5000)

                    titulo = pagina.title()
                    enlace = pagina.url
                    cita = Biblioteca.extraer_cita_ieee(pagina)

                    pagina.go_back()
                    pagina.wait_for_timeout(3000)
                except Exception as e:
                    print(f"Error al procesar un artículo: {e}")

            navegador.close()


    @staticmethod
    def extraer_cita_ieee(pagina, indice=None):
        try:
            boton_citar = pagina.locator("button.xpl-btn-secondary").first
            boton_citar.scroll_into_view_if_needed()
            boton_citar.wait_for(state="visible", timeout=5000)
            boton_citar.click(force=True)
            pagina.wait_for_timeout(3000)

            pagina.wait_for_selector("div.cite-this-container", timeout=5000)

            opcion_bibtex = pagina.locator("a.document-tab-link[title='BibTeX']").first
            opcion_bibtex.scroll_into_view_if_needed()
            opcion_bibtex.wait_for(state="visible", timeout=3000)
            opcion_bibtex.click(force=True)
            pagina.wait_for_timeout(2000)

            with pagina.expect_download() as descarga:
                boton_descargar = pagina.locator("a.stats-download-citations-button-download").first
                boton_descargar.scroll_into_view_if_needed()
                boton_descargar.wait_for(state="visible", timeout=3000)
                boton_descargar.click(force=True)

            archivo_descargado = descarga.value

            # Leer el contenido desde el archivo temporal
            ruta_temporal = archivo_descargado.path()
            with open(ruta_temporal, "r", encoding="utf-8") as f:
                contenido = f.read()

            # Extraer el DOI y título del contenido BibTeX
            doi = None
            titulo = None
            for linea in contenido.splitlines():
                if 'doi' in linea.lower() and doi is None:
                    doi = linea.split('=')[1].strip().strip('{}").,')
                if 'title' in linea.lower() and titulo is None:
                    titulo = linea.split('=')[1].strip().strip('{}").,')
                if doi and titulo:
                    break

            if doi:
                if Biblioteca.doi_existente(doi):
                    print(f"El DOI {doi} ya existe, no se realizará la descarga.")
                    return f"El DOI {doi} ya existe, no se realizará la descarga."
                else:
                    if indice is None:
                        indice = int(time.time())
                    ruta_guardado = f"C:\\Users\\Bryan\\Documents\\btw\\code\\Programacion\\AlgoritmosProyecto\\src\\main\\resources\\cita_ieee_{indice}.bib"
                    archivo_descargado.save_as(ruta_guardado)
                    Biblioteca.reordenar_y_filtrar_bibtex(ruta_guardado)
                    return f"Citas descargadas: {ruta_guardado}"
            else:
                if titulo and Biblioteca.titulo_existente(titulo):
                    print(f"El título '{titulo}' ya existe, no se realizará la descarga.")
                    return f"El título '{titulo}' ya existe, no se realizará la descarga."
                else:
                    if indice is None:
                        indice = int(time.time())
                    ruta_guardado = f"C:\\Users\\Bryan\\Documents\\btw\\code\\Programacion\\AlgoritmosProyecto\\src\\main\\resources\\cita_ieee_{indice}.bib"
                    archivo_descargado.save_as(ruta_guardado)
                    Biblioteca.reordenar_y_filtrar_bibtex(ruta_guardado)
                    return f"Citas descargadas (por título): {ruta_guardado}"

        except Exception as e:
            return f"Error al extraer cita: {e}"





    @staticmethod
    def buscar_todo(query, cantidad, correo, contrasena):
        Biblioteca.buscar_sciencedirect(query, correo, contrasena, cantidad)
        #articulos_sage = Biblioteca.buscar_sage(query)
        articulos_ieee = Biblioteca.buscar_ieee(query, cantidad)




    @staticmethod
    def reordenar_y_filtrar_bibtex(ruta_archivo):
        with open(ruta_archivo, 'r', encoding='utf-8') as bibfile:
            bib_database = bibtexparser.load(bibfile)

        orden_deseado = [
            'author',
            'booktitle_or_journal',
            'title',
            'year',
            'pages',
            'abstract',
            'doi',
            'keywords',
            'issn'
        ]

        nuevas_entradas = []

        for entry in bib_database.entries:
            lineas = []

            # Cabecera: tipo y clave
            tipo = entry.get('ENTRYTYPE', 'article')
            clave = entry.get('ID', 'unknown')
            lineas.append(f"@{tipo}{{{clave},")

            for campo in orden_deseado:
                if campo == 'booktitle_or_journal':
                    valor = entry.get('booktitle') or entry.get('journal') or ''
                    nombre_campo = 'booktitle' if 'booktitle' in entry else 'journal'
                    lineas.append(f"  {nombre_campo} = {{{valor}}},")
                else:
                    valor = entry.get(campo, '')
                    lineas.append(f"  {campo} = {{{valor}}},")

            # Cierra la entrada
            if lineas[-1].endswith(','):
                lineas[-1] = lineas[-1][:-1]  # quita la última coma
            lineas.append("}")

            nuevas_entradas.append('\n'.join(lineas))

        # Sobrescribe el archivo con las nuevas entradas
        with open(ruta_archivo, 'w', encoding='utf-8') as bibfile:
            bibfile.write('\n\n'.join(nuevas_entradas))



    @staticmethod
    def doi_existente(doi_buscar):
        ruta_base = r"C:\Users\Bryan\Documents\btw\code\Programacion\AlgoritmosProyecto\src\main\resources"

        for root, _, files in os.walk(ruta_base):
            for archivo in files:
                if archivo.endswith('.bib'):  # Solo buscar en archivos .bib
                    ruta_archivo = os.path.join(root, archivo)
                    try:
                        with open(ruta_archivo, 'r', encoding='utf-8') as f:
                            # Leer el contenido del archivo BibTeX
                            bib_database = bibtexparser.load(f)
                            for entry in bib_database.entries:
                                # Comprobar si el DOI está en el campo 'doi'
                                if 'doi' in entry and entry['doi'] == doi_buscar:
                                    return True
                    except Exception as e:
                        print(f"Error leyendo {ruta_archivo}: {e}")
        return False

    @staticmethod
    def titulo_existente(titulo):
        ruta_base = r"C:\Users\Bryan\Documents\btw\code\Programacion\AlgoritmosProyecto\src\main\resources"

        for root, _, files in os.walk(ruta_base):
            for archivo in files:
                if archivo.endswith('.bib'):  # Solo buscar en archivos .bib
                    ruta_archivo = os.path.join(root, archivo)
                    try:
                        with open(ruta_archivo, 'r', encoding='utf-8') as f:
                            # Leer el contenido del archivo BibTeX
                            bib_database = bibtexparser.load(f)
                            for entry in bib_database.entries:
                                # Comprobar si el DOI está en el campo 'title'
                                if 'title' in entry and entry['title'] == titulo:
                                    return True
                    except Exception as e:
                        print(f"Error leyendo {ruta_archivo}: {e}")
        return False

# Ejemplo de uso

# Leer la clave secreta
with open("clave.key", "rb") as f:
    clave = f.read()

# Leer los datos cifrados
with open("credenciales.enc", "rb") as f:
    datos_cifrados = f.read()
# Desencriptar
fernet = Fernet(clave)
datos_json = fernet.decrypt(datos_cifrados).decode()
credenciales = json.loads(datos_json)

# Usar sin imprimirlos
correo = credenciales["correo"]
contrasena = credenciales["contrasena"]

#Uso de la clase biblioteca
biblioteca = Biblioteca()
resultados = biblioteca.buscar_todo("Machine Learning", 2, correo, contrasena)
print(resultados)
