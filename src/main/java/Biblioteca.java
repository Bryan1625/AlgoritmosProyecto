import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

public class Biblioteca {
    private static WebDriver driver;
    private static WebDriverWait wait;
    private static final String CHROME_PATH = "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe";
    private static final String USER_DATA_DIR = "C:\\Users\\TU_USUARIO\\AppData\\Local\\Google\\Chrome\\User Data";
    private static final String PROFILE_NAME = "Bryan";

    private static final String[] BIBLIOTECAS = {
            "https://www.sciencedirect.com",
            "https://search-sagepub-com.crai.referencistas.com/",
            "https://ieeexplore.ieee.org/"
    };

    private static final List<Map<String, String>> articulos = new ArrayList<>();
    private static final Set<String> articulosUnicos = new HashSet<>();
    private static final List<Map<String, String>> articulosDuplicados = new ArrayList<>();

    public static void inicializar() {
        ChromeOptions options = new ChromeOptions();
        options.setBinary(CHROME_PATH);
        options.addArguments("user-data-dir=" + USER_DATA_DIR);
        options.addArguments("profile-directory=" + PROFILE_NAME);
        options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public static void buscarYExportarBibTeX(String titulo) {
        buscarEnScienceDirect(titulo);
        buscarEnIEEE(titulo);
        buscarEnSage(titulo);
        guardarResultados("articulos_unicos.bib", articulos);
        guardarResultados("articulos_duplicados.bib", articulosDuplicados);
    }

    private static void buscarEnScienceDirect(String titulo) {
        driver.get("https://www.sciencedirect.com");
        WebElement searchBox = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("qs")));
        searchBox.sendKeys(titulo);
        searchBox.submit();
        esperarCarga(".result-item-title a");
        extraerInformacion(".result-item-title a");
    }

    private static void buscarEnIEEE(String titulo) {
        driver.get("https://ieeexplore.ieee.org/");
        WebElement searchBox = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("global-search")));
        searchBox.sendKeys(titulo);
        searchBox.submit();
        esperarCarga(".List-results-items h2 a");
        extraerInformacion(".List-results-items h2 a");
    }

    private static void buscarEnSage(String titulo) {
        driver.get("https://sk.sagepub.com/");
        WebElement searchBox = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("query")));
        searchBox.sendKeys(titulo);
        searchBox.submit();
        esperarCarga(".teaser__title a");
        extraerInformacion(".teaser__title a");
    }

    private static void extraerInformacion(String selector) {
        List<WebElement> resultados = driver.findElements(By.cssSelector(selector));
        for (WebElement resultado : resultados) {
            String titulo = resultado.getText();
            String enlace = resultado.getAttribute("href");
            Map<String, String> articulo = new HashMap<>();
            articulo.put("titulo", titulo);
            articulo.put("enlace", enlace);
            if (articulosUnicos.contains(titulo)) {
                articulosDuplicados.add(articulo);
            } else {
                articulosUnicos.add(titulo);
                articulos.add(articulo);
            }
        }
    }

    private static void esperarCarga(String cssSelector) {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(cssSelector)));
        } catch (Exception e) {
            System.out.println("Tiempo de espera agotado al cargar el elemento: " + cssSelector);
        }
    }

    private static void guardarResultados(String archivo, List<Map<String, String>> datos) {
        try (FileWriter writer = new FileWriter(archivo)) {
            for (Map<String, String> articulo : datos) {
                writer.write("@article{\n");
                writer.write("  title={" + articulo.get("titulo") + "},\n");
                writer.write("  url={" + articulo.get("enlace") + "}\n");
                writer.write("}\n\n");
            }
            System.out.println("Archivo generado: " + archivo);
        } catch (IOException e) {
            System.out.println("Error al escribir el archivo " + archivo);
        }
    }

    public static void cerrar() {
        if (driver != null) {
            driver.quit();
        }
    }

    public static void main(String[] args) {
        inicializar();
        buscarYExportarBibTeX("Machine Learning");
        cerrar();
    }
}
