import os

import requests
import time
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
            resultados = pagina.locator("li.ResultItem")
            num_resultados = resultados.count()
            print(f"Resultados encontrados: {num_resultados}")

            for i in range(cantidad):
                item_element = resultados.nth(i).element_handle()
                print(f"Procesando artículo #{i + 1}")
                Biblioteca.exportar_cita_sciencedirect(pagina, item_element, i)

                # Cambiar de página si es necesario
                if (i + 1) % 25 == 0 and (i + 1) < num_resultados:
                    print(f"Cargando página {((i + 1) // 25) + 1}")
                    pagina.click("span.anchor-text:has-text('Next')")
                    pagina.wait_for_selector("li.ResultItem", timeout=15000)

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

            # Ruta de destino para guardar la cita
            ruta_destino = os.path.join(
                r"C:\Users\Bryan\Documents\btw\code\Programacion\AlgoritmosProyecto\src\main\resources",
                f"{indice_articulo + 1}_{download.suggested_filename}"
            )
            download.save_as(ruta_destino)
            print(f"Cita descargada: {ruta_destino}")

            # Desmarcar la casilla verificando aria-checked
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
                return []

            articulos = []
            for r in resultados[:3]:
                try:
                    r.scroll_into_view_if_needed()
                    r.click(force=True)
                    pagina.wait_for_timeout(5000)

                    titulo = pagina.title()
                    enlace = pagina.url
                    cita = Biblioteca.extraer_cita_ieee(pagina)

                    articulos.append({"titulo": titulo, "enlace": enlace, "cita": cita})

                    pagina.go_back()
                    pagina.wait_for_timeout(3000)
                except Exception as e:
                    print(f"Error al procesar un artículo: {e}")

            navegador.close()
            return articulos

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

            # Si no se proporciona un índice, usar timestamp para evitar sobrescritura
            if indice is None:
                indice = int(time.time())  # Timestamp para garantizar unicidad

            ruta_guardado = f"C:\\Users\\Bryan\\Documents\\btw\\code\\Programacion\\AlgoritmosProyecto\\src\\main\\resources\\cita_ieee_{indice}.bib"
            archivo_descargado.save_as(ruta_guardado)

            return f"Citas descargadas: {ruta_guardado}"

        except Exception as e:
            return f"Error al extraer cita: {e}"


    @staticmethod
    def buscar_todo(query, cantidad, correo, contrasena):
        articulos_sciencedirect = Biblioteca.buscar_sciencedirect(query, correo, contrasena, cantidad)
        #articulos_sage = Biblioteca.buscar_sage(query)
        #articulos_ieee = Biblioteca.buscar_ieee(query, cantidad)

        return {
            "ScienceDirect": articulos_sciencedirect,
            #"SAGE": articulos_sage,
            #"IEEE": articulos_ieee
        }

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
resultados = biblioteca.buscar_todo("Machine Learning", 3, correo, contrasena)
print(resultados)
