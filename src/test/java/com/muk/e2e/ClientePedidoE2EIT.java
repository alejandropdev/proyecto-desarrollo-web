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

/**
 * Caso 2 E2E: usuario registrado agrega al menos 2 comidas con 2 adicionales cada una,
 * verifica el carrito, confirma el pedido, y un operador gestiona el estado hasta
 * completarlo. El usuario verifica el historial con productos, adicionales y total.
 */
@SpringBootTest(classes = MukApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
class ClientePedidoE2EIT {

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
    void clienteRegistradoHacePedidoConAdicionalesYOperadorLoCompleta() {
        String base = "http://localhost:" + port;

        // =====================================================================
        // 1. USUARIO INICIA SESIÓN CON CREDENCIALES EXISTENTES
        // =====================================================================
        driver.get(base + "/login");
        wait.until(ExpectedConditions.urlContains("/login"));

        driver.findElement(By.cssSelector("input[name='email']")).sendKeys("sara@muk.com");
        driver.findElement(By.cssSelector("input[name='password']")).sendKeys("1234");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Login redirige al perfil
        wait.until(ExpectedConditions.urlContains("/clientes/perfil"));

        // =====================================================================
        // 2. PRIMERA COMIDA: BBQ Smash Burger + 2 adiciones (Pepinillos + Queso extra)
        // =====================================================================
        driver.get(base + "/menu");

        WebElement burgerCard = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href,'/comida/')][.//h3[contains(.,'BBQ Smash Burger')]]")));
        burgerCard.click();

        // Esperar a que carguen al menos 2 botones de adición
        wait.until(d -> d.findElements(By.cssSelector("[data-testid^=comida-adicion-]")).size() >= 2);

        clickAdicionByNombre("Pepinillos");
        clickAdicionByNombre("Queso extra");

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'Añadir al carrito')]"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(.,'Producto agregado al carrito')]")));

        // =====================================================================
        // 3. SEGUNDA COMIDA: Muk Chicken Crunch + 2 adiciones (Salsa ranch + Aros de cebolla)
        // =====================================================================
        driver.get(base + "/menu");

        WebElement chickenCard = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href,'/comida/')][.//h3[contains(.,'Muk Chicken Crunch')]]")));
        chickenCard.click();

        wait.until(d -> d.findElements(By.cssSelector("[data-testid^=comida-adicion-]")).size() >= 2);

        clickAdicionByNombre("Salsa ranch");
        clickAdicionByNombre("Aros de cebolla");

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'Añadir al carrito')]"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(.,'Producto agregado al carrito')]")));

        // =====================================================================
        // 4. VERIFICAR CARRITO ANTES DE CONFIRMAR EL PEDIDO
        // =====================================================================
        driver.get(base + "/carrito");

        // Esperar a que Angular cargue el carrito desde localStorage y seleccione los productos
        wait.until(d -> {
            List<WebElement> selects = d.findElements(By.cssSelector("select[name^='producto-']"));
            if (selects.size() < 2) return false;
            // Verificar que el primer select tiene un producto seleccionado (no el placeholder)
            String primerSeleccionado = new Select(selects.get(0)).getFirstSelectedOption().getText();
            return !primerSeleccionado.contains("Selecciona");
        });

        // Verificar que hay al menos 2 líneas de producto en el carrito
        List<WebElement> productSelects = driver.findElements(By.cssSelector("select[name^='producto-']"));
        assertThat(productSelects).hasSizeGreaterThanOrEqualTo(2);

        // Verificar que al menos 2 adicionales están seleccionados (uno por comida mínimo)
        wait.until(d -> d.findElements(By.cssSelector("input[type='checkbox']:checked")).size() >= 2);
        List<WebElement> checkedBoxes = driver.findElements(By.cssSelector("input[type='checkbox']:checked"));
        assertThat(checkedBoxes.size()).isGreaterThanOrEqualTo(2);

        // Leer el total del carrito ANTES de enviar (para compararlo después sin hardcodear)
        WebElement totalCarritoEl = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[normalize-space()='TOTAL:']/following-sibling::span")));
        String totalCarrito = totalCarritoEl.getText().trim();
        assertThat(totalCarrito).startsWith("$");

        // =====================================================================
        // 5. CONFIRMAR EL PEDIDO
        // =====================================================================
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button[type='submit']"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(.,'Pedido creado exitosamente')]")));

        // Redirige automáticamente a mis-pedidos
        wait.until(ExpectedConditions.urlContains("/pedidos/mis-pedidos"));

        // =====================================================================
        // 6. OBTENER EL ID DEL NUEVO PEDIDO (el de mayor ID entre los "en curso")
        // =====================================================================
        // Sara ya tiene p1 (PENDIENTE) y p11 (EN_PREPARACION) del DataLoader.
        // El nuevo pedido tendrá el ID más alto entre sus pedidos en curso.
        wait.until(d -> !d.findElements(
                By.xpath("//h2[contains(.,'Pedidos en Curso')]/../..//table//tbody//tr/td[1]")).isEmpty());

        List<WebElement> idCells = driver.findElements(
                By.xpath("//h2[contains(.,'Pedidos en Curso')]/../..//table//tbody//tr/td[1]"));

        long pedidoId = idCells.stream()
                .mapToLong(c -> Long.parseLong(c.getText().replace("#", "").trim()))
                .max()
                .orElseThrow(() -> new AssertionError("No se encontraron pedidos en curso para el usuario"));

        String userWindow = driver.getWindowHandle();

        // =====================================================================
        // 7. OPERADOR: ABRE NUEVA PESTAÑA E INICIA SESIÓN
        // =====================================================================
        driver.switchTo().newWindow(WindowType.TAB);
        String operadorWindow = driver.getWindowHandle();

        driver.get(base + "/operario/login");
        wait.until(ExpectedConditions.urlContains("/operario/login"));

        driver.findElement(By.cssSelector("input[name='usuario']")).sendKeys("operador1");
        driver.findElement(By.cssSelector("input[name='password']")).sendKeys("hash-op-001");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/operario/pedidos"));

        // =====================================================================
        // 8. OPERADOR: CAMBIA ESTADO A EN_PREPARACION
        // =====================================================================
        cambiarEstadoPedido(pedidoId, "EN_PREPARACION");
        wait.until(d -> {
            List<WebElement> alertas = d.findElements(By.cssSelector(".alerta.exito"));
            return !alertas.isEmpty() && alertas.get(0).isDisplayed();
        });

        // =====================================================================
        // 9. USUARIO: VERIFICA EN SU PESTAÑA QUE EL ESTADO CAMBIÓ
        // =====================================================================
        driver.switchTo().window(userWindow);
        driver.get(base + "/pedidos/mis-pedidos");

        // Esperar a que el estado del pedido específico sea EN_PREPARACION
        final long targetId = pedidoId;
        wait.until(d -> {
            List<WebElement> rows = d.findElements(
                    By.xpath("//h2[contains(.,'Pedidos en Curso')]/../..//table//tbody//tr"));
            return rows.stream().anyMatch(row -> {
                String txt = row.getText();
                return txt.contains("#" + targetId) && txt.contains("EN_PREPARACION");
            });
        });

        WebElement estadoEnPrep = driver.findElement(By.xpath(
                "//h2[contains(.,'Pedidos en Curso')]/../..//table//tbody//tr" +
                "[td[normalize-space()='#" + pedidoId + "']]//td[4]"));
        assertThat(estadoEnPrep.getText().trim()).contains("EN_PREPARACION");

        // =====================================================================
        // 10. OPERADOR: COMPLETA EL FLUJO → LISTO → EN_CAMINO → COMPLETADO
        // =====================================================================
        driver.switchTo().window(operadorWindow);

        // LISTO
        cambiarEstadoPedido(pedidoId, "LISTO");
        wait.until(d -> {
            List<WebElement> alertas = d.findElements(By.cssSelector(".alerta.exito"));
            return !alertas.isEmpty() && alertas.get(0).isDisplayed();
        });

        // EN_CAMINO (el servicio auto-asigna el primer domiciliario disponible)
        cambiarEstadoPedido(pedidoId, "EN_CAMINO");
        wait.until(d -> {
            List<WebElement> alertas = d.findElements(By.cssSelector(".alerta.exito"));
            return !alertas.isEmpty() && alertas.get(0).isDisplayed();
        });

        // COMPLETADO (libera al domiciliario y registra la fecha de entrega)
        cambiarEstadoPedido(pedidoId, "COMPLETADO");
        wait.until(d -> {
            List<WebElement> alertas = d.findElements(By.cssSelector(".alerta.exito"));
            return !alertas.isEmpty() && alertas.get(0).isDisplayed();
        });

        // =====================================================================
        // 11. USUARIO: VERIFICA EL HISTORIAL DE PEDIDOS COMPLETADOS
        // =====================================================================
        driver.switchTo().window(userWindow);
        driver.get(base + "/pedidos/mis-pedidos");

        // El pedido debe aparecer ahora en "Historial de Pedidos" (estado COMPLETADO)
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[contains(.,'Historial de Pedidos')]")));

        wait.until(d -> !d.findElements(By.xpath(
                "//h2[contains(.,'Historial de Pedidos')]/../..//table//tbody" +
                "//tr[td[normalize-space()='#" + pedidoId + "']]")).isEmpty());

        // Hacer clic en "Ver" para abrir el detalle del pedido completado
        WebElement btnVer = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//h2[contains(.,'Historial de Pedidos')]/../..//table//tbody" +
                         "//tr[td[normalize-space()='#" + pedidoId + "']]//button[contains(.,'Ver')]")));
        btnVer.click();

        wait.until(ExpectedConditions.urlContains("/pedidos/detalle/" + pedidoId));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//*[contains(.,'Cargando detalles del pedido')]")));

        // Esperar a que cargue el encabezado del pedido
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h1[contains(.,'Pedido #" + pedidoId + "')]")));

        // =====================================================================
        // 12. VERIFICAR PRODUCTOS Y ADICIONALES EN EL DETALLE
        // =====================================================================
        String paginaDetalle = driver.getPageSource();

        assertThat(paginaDetalle).contains("BBQ Smash Burger");
        assertThat(paginaDetalle).contains("Muk Chicken Crunch");

        assertThat(paginaDetalle).contains("Pepinillos");
        assertThat(paginaDetalle).contains("Queso extra");
        assertThat(paginaDetalle).contains("Salsa ranch");
        assertThat(paginaDetalle).contains("Aros de cebolla");

        // =====================================================================
        // 13. VERIFICAR QUE EL TOTAL COINCIDE CON EL DEL CARRITO (sin hardcodear)
        // =====================================================================
        WebElement totalDetalleEl = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[normalize-space()='TOTAL:']/following-sibling::span")));
        String totalDetalle = totalDetalleEl.getText().trim();

        assertThat(totalDetalle).isEqualTo(totalCarrito);
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    /**
     * Hace clic en el botón de adición cuyo texto contiene el nombre indicado,
     * en la página de detalle de comida (/comida/:id).
     */
    private static void clickAdicionByNombre(String nombre) {
        WebElement btn = wait.until(d -> {
            List<WebElement> btns = d.findElements(By.cssSelector("[data-testid^=comida-adicion-]"));
            return btns.stream()
                    .filter(b -> {
                        String t = b.getText();
                        if (t == null || t.isBlank()) {
                            String inner = b.getDomProperty("innerText");
                            t = (inner != null) ? inner : "";
                        }
                        return t.toLowerCase().contains(nombre.toLowerCase());
                    })
                    .findFirst()
                    .orElse(null);
        });
        btn.click();
    }

    /**
     * En el portal del operario (/operario/pedidos), encuentra la fila del pedido
     * indicado y selecciona el nuevo estado en el dropdown correspondiente.
     * Espera a que la tabla esté visible antes de buscar la fila.
     */
    private static void cambiarEstadoPedido(long pedidoId, String nuevoEstado) {
        // Esperar a que la tabla de pedidos esté presente tras una posible recarga
        wait.until(d -> !d.findElements(By.cssSelector(".tabla-pedidos")).isEmpty());

        WebElement estadoSelect = wait.until(d -> {
            List<WebElement> rows = d.findElements(By.xpath(
                    "//tr[td[normalize-space()='#" + pedidoId + "']]"));
            if (rows.isEmpty()) return null;
            List<WebElement> selects = rows.get(0).findElements(By.cssSelector("select.select-estado"));
            return selects.isEmpty() ? null : selects.get(0);
        });

        new Select(estadoSelect).selectByValue(nuevoEstado);
    }
}
