import requests
import time
from bs4 import BeautifulSoup
from playwright.sync_api import sync_playwright

class Biblioteca:
    HEADERS = {"User-Agent": "Mozilla/5.0"}

    @staticmethod
    def buscar_sciencedirect(query):
        url = f"https://www.sciencedirect.com/search?qs={query}"
        response = requests.get(url, headers=Biblioteca.HEADERS)
        soup = BeautifulSoup(response.text, "html.parser")
        articulos = []

        for item in soup.select(".result-item-content h2 a"):
            titulo = item.text.strip()
            enlace = "https://www.sciencedirect.com" + item["href"]
            cita = Biblioteca.extraer_cita_sciencedirect(enlace)
            articulos.append({"titulo": titulo, "enlace": enlace, "cita": cita})

        return articulos

    @staticmethod
    def extraer_cita_sciencedirect(url):
        with sync_playwright() as p:
            navegador = p.chromium.launch(channel="chrome", headless=False)
            pagina = navegador.new_page()
            pagina.goto(url)
            pagina.click("text=Cite")
            pagina.wait_for_timeout(2000)
            pagina.click("text=Export citation to BibTeX")
            pagina.wait_for_timeout(2000)
            cita = pagina.locator("textarea").inner_text()
            navegador.close()
            return cita if cita else "Cita no encontrada"

    @staticmethod
    def buscar_sage(query):
        url = f"https://journals.sagepub.com/action/doSearch?AllField={query}"
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
    def buscar_todo(query, cantidad: int):
        articulos_sciencedirect = Biblioteca.buscar_sciencedirect(query)
        articulos_sage = Biblioteca.buscar_sage(query)
        articulos_ieee = Biblioteca.buscar_ieee(query, cantidad)

        return {
            "ScienceDirect": articulos_sciencedirect,
            "SAGE": articulos_sage,
            "IEEE": articulos_ieee
        }

# Ejemplo de uso
biblioteca = Biblioteca()
resultados = biblioteca.buscar_todo("Machine Learning")
print(resultados)
