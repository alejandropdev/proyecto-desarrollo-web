package com.muk.e2e;

import com.muk.MukApplication;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MukApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
public class ClientePedidoOperadorE2EIT {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @LocalServerPort
    private int port;

    @BeforeAll
    static void openBrowser() {
        System.setProperty("java.awt.headless", "false");

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--remote-allow-origins=*",
                "--disable-search-engine-choice-screen",
                "--window-position=80,48",
                "--start-maximized");
        if (Boolean.getBoolean("e2e.headless")) {
            options.addArguments("--headless=new", "--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");
        }
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @AfterAll
    static void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testFlujoCompletoClienteOperador() throws Exception {
        String base = "http://localhost:" + port;

        // --- PESTAÑA A: CLIENTE ---
        driver.get(base + "/login");

        // 1. Cliente hace login
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[name='email']")));
        driver.findElement(By.cssSelector("input[name='email']")).sendKeys("sara@muk.com");
        driver.findElement(By.cssSelector("input[name='password']")).sendKeys("1234");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/clientes/perfil"));

        // 2. Va a menú y agrega 2 comidas con 2 adicionales
        driver.get(base + "/menu");

        // Seleccionar primer producto
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid^=menu-producto-]")));
        List<WebElement> productos = driver.findElements(By.cssSelector("[data-testid^=menu-producto-]"));
        assertThat(productos).hasSizeGreaterThanOrEqualTo(2);
        
        // Entrar a Producto 1
        String producto1Url = productos.get(0).getAttribute("href");
        driver.get(producto1Url);
        
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='comida-agregar-carrito']")));
        List<WebElement> adicionesProd1 = driver.findElements(By.cssSelector("[data-testid^=comida-adicion-]"));
        if (adicionesProd1.size() >= 2) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", adicionesProd1.get(0));
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", adicionesProd1.get(1));
        }
        WebElement btnAdd1 = driver.findElement(By.cssSelector("[data-testid='comida-agregar-carrito']"));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", btnAdd1);
        wait.until(ExpectedConditions.elementToBeClickable(btnAdd1));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btnAdd1);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(), 'Producto agregado')]")));

        // Entrar a Producto 2
        driver.get(base + "/menu");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid^=menu-producto-]")));
        productos = driver.findElements(By.cssSelector("[data-testid^=menu-producto-]"));
        String producto2Url = productos.get(1).getAttribute("href");
        driver.get(producto2Url);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='comida-agregar-carrito']")));
        List<WebElement> adicionesProd2 = driver.findElements(By.cssSelector("[data-testid^=comida-adicion-]"));
        if (adicionesProd2.size() >= 2) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", adicionesProd2.get(0));
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", adicionesProd2.get(1));
        }
        WebElement btnAdd2 = driver.findElement(By.cssSelector("[data-testid='comida-agregar-carrito']"));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", btnAdd2);
        wait.until(ExpectedConditions.elementToBeClickable(btnAdd2));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btnAdd2);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(), 'Producto agregado')]")));

        // 3. Revisa el carrito y confirma
        driver.get(base + "/carrito");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='carrito-producto-nombre']")));
        
        List<WebElement> nombresCarrito = driver.findElements(By.cssSelector("[data-testid='carrito-producto-nombre']"));
        assertThat(nombresCarrito).hasSize(2);

        WebElement btnConfirm = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[data-testid='carrito-btn-confirmar']")));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", btnConfirm);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btnConfirm);

        // Espera redireccion a Mis Pedidos
        wait.until(ExpectedConditions.urlContains("/pedidos/mis-pedidos"));
        
        // Espera que la fila del pedido cargue
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid^=mis-pedido-row-]")));

        // Obtener el ID del pedido recien creado (el primero en la lista de pendientes)
        WebElement primerPedidoRow = driver.findElements(By.cssSelector("[data-testid^=mis-pedido-row-]")).get(0);
        String rowTestId = primerPedidoRow.getAttribute("data-testid");
        String pedidoIdStr = rowTestId.replace("mis-pedido-row-", "");
        
        // Verifica que este PENDIENTE
        WebElement estadoElement = driver.findElement(By.cssSelector("[data-testid='estado-pedido-" + pedidoIdStr + "']"));
        assertThat(estadoElement.getText().trim()).isEqualTo("PENDIENTE");

        String clienteWindow = driver.getWindowHandle();

        // --- PESTAÑA B: OPERADOR ---
        driver.switchTo().newWindow(WindowType.TAB);
        driver.get(base + "/operario/login");

        // 4. Operador hace login
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("usuario")));
        driver.findElement(By.name("usuario")).sendKeys("operador1");
        driver.findElement(By.name("password")).sendKeys("hash-op-001");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/operario/pedidos"));
        
        // 5. Cambiar el estado del nuevo pedido
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='operario-tabla-pedidos']")));
        WebElement operarioSelect = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='operario-estado-" + pedidoIdStr + "']")));
        Select selectEstado = new Select(operarioSelect);
        selectEstado.selectByValue("EN_PREPARACION");
        Thread.sleep(1000); // Esperar que la API procese
        
        // --- VUELVE A PESTAÑA A (CLIENTE) ---
        driver.switchTo().window(clienteWindow);
        driver.navigate().refresh();
        
        // 6. Verifica que en el cliente se ve EN_PREPARACION
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='estado-pedido-" + pedidoIdStr + "']")));
        estadoElement = driver.findElement(By.cssSelector("[data-testid='estado-pedido-" + pedidoIdStr + "']"));
        assertThat(estadoElement.getText().trim()).isEqualTo("EN_PREPARACION");

        // --- VUELVE A PESTAÑA B (OPERADOR) ---
        driver.switchTo().window(driver.getWindowHandles().toArray()[1].toString());

        // 7. Sigue cambiando estado y asigna domiciliario
        operarioSelect = driver.findElement(By.cssSelector("[data-testid='operario-estado-" + pedidoIdStr + "']"));
        selectEstado = new Select(operarioSelect);
        selectEstado.selectByValue("EN_CAMINO");
        Thread.sleep(1000);

        // Terminar pedido
        operarioSelect = driver.findElement(By.cssSelector("[data-testid='operario-estado-" + pedidoIdStr + "']"));
        selectEstado = new Select(operarioSelect);
        selectEstado.selectByValue("COMPLETADO");
        Thread.sleep(1500); // Esperar que se elimine del listado de pendientes
        
        // --- VUELVE A PESTAÑA A (CLIENTE) ---
        driver.switchTo().window(clienteWindow);
        driver.navigate().refresh();
        
        // 8. Cliente ingresa al detalle finalizado
        WebElement btnDetalle = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[data-testid='btn-ver-detalle-" + pedidoIdStr + "']")));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", btnDetalle);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btnDetalle);
        
        // Verifica detalle y montos (dinámicamente)
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='detalle-item-nombre']")));
        } catch (Exception e) {
            System.err.println("===== PAGE SOURCE =====");
            System.err.println(driver.getPageSource());
            System.err.println("=======================");
            throw e;
        }
        
        // Extraer todos los subtotales de items
        List<WebElement> subtotalElements = driver.findElements(By.xpath("//p[contains(text(), 'Subtotal')]/following-sibling::p"));
        long sumSubtotales = 0;
        for(WebElement el : subtotalElements) {
            String val = el.getText().replace("$", "").replace(".", "").trim();
            sumSubtotales += Long.parseLong(val);
        }
        
        // Extraer todos los subtotales de adiciones
        List<WebElement> adicionTotalElements = driver.findElements(By.xpath("//p[@data-testid='detalle-adicion-nombre']/span"));
        long sumAdiciones = 0;
        for(WebElement el : adicionTotalElements) {
            String val = el.getText().replace("$", "").replace(".", "").trim();
            sumAdiciones += Long.parseLong(val);
        }
        
        // Extraer gran total de la vista
        WebElement totalGeneralElement = driver.findElement(By.cssSelector("[data-testid='detalle-total']"));
        String totalView = totalGeneralElement.getText().replace("$", "").replace(".", "").trim();
        long totalEsperado = Long.parseLong(totalView);
        
        // ASERCIÓN PRINCIPAL: Verificar suma a pagar sea acorde a los productos
        assertThat(sumSubtotales + sumAdiciones).isEqualTo(totalEsperado);
        assertThat(totalEsperado).isGreaterThan(0); // Asegurar que cobramos algo
    }
}
