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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MukApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
class AdminProductoMenuE2EIT {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @LocalServerPort
    private int port;

    @BeforeAll
    static void openBrowser() {
        // En Windows + Maven/Failsafe el fork a veces arranca con headless AWT y Chrome no muestra ventana.
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
    void adminCreaProductoConDosAdicionesYApareceEnMenu() {
        String base = "http://localhost:" + port;
        String nombreUnico = "E2E-Burger-" + System.currentTimeMillis();

        driver.get(base + "/");

        WebElement linkAdmin = wait.until(ExpectedConditions.elementToBeClickable(By.id("link-admin-login")));
        linkAdmin.click();

        wait.until(ExpectedConditions.urlContains("/admin/login"));

        driver.findElement(By.cssSelector("[data-testid=admin-login-usuario]")).sendKeys("admin");
        driver.findElement(By.cssSelector("[data-testid=admin-login-password]")).sendKeys("wrong-password");
        driver.findElement(By.cssSelector("[data-testid=admin-login-submit]")).click();

        WebElement errorBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid=admin-login-error]")));
        assertThat(errorBox.getText().trim()).isNotBlank();

        driver.findElement(By.cssSelector("[data-testid=admin-login-usuario]")).clear();
        driver.findElement(By.cssSelector("[data-testid=admin-login-usuario]")).sendKeys("admin");
        driver.findElement(By.cssSelector("[data-testid=admin-login-password]")).clear();
        driver.findElement(By.cssSelector("[data-testid=admin-login-password]")).sendKeys("1234");
        driver.findElement(By.cssSelector("[data-testid=admin-login-submit]")).click();

        wait.until(ExpectedConditions.urlContains("/admin/platos"));

        driver.get(base + "/admin/productos");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid=producto-nombre]")));

        driver.findElement(By.cssSelector("[data-testid=producto-nombre]")).sendKeys(nombreUnico);
        driver.findElement(By.cssSelector("[data-testid=producto-precio]")).clear();
        driver.findElement(By.cssSelector("[data-testid=producto-precio]")).sendKeys("25000");
        driver.findElement(By.cssSelector("[data-testid=producto-imagen]"))
                .sendKeys("https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=800&q=80");
        driver.findElement(By.cssSelector("[data-testid=producto-descripcion]")).sendKeys("Hamburguesa de prueba E2E");

        Select categoria = new Select(driver.findElement(By.cssSelector("[data-testid=producto-categoria]")));
        categoria.selectByVisibleText("BURGERS");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid=producto-adiciones-panel]")));

        WebElement pep = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//label[contains(.,'Pepinillos')]//input[@type='checkbox']")));
        WebElement queso = driver.findElement(
                By.xpath("//label[contains(.,'Queso extra')]//input[@type='checkbox']"));
        if (!pep.isSelected()) {
            pep.click();
        }
        if (!queso.isSelected()) {
            queso.click();
        }

        driver.findElement(By.cssSelector("[data-testid=producto-submit]")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//td[contains(normalize-space(.),'" + nombreUnico + "')]")));

        String adminWindow = driver.getWindowHandle();
        driver.switchTo().newWindow(WindowType.TAB);
        driver.get(base + "/menu");

        WebElement tarjeta = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href,'/comida/')][.//h3[contains(.,'" + nombreUnico + "')]]")));
        tarjeta.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid^=comida-adicion-]")));
        wait.until(d -> {
            List<WebElement> btns = d.findElements(By.cssSelector("[data-testid^=comida-adicion-]"));
            if (btns.size() < 2) {
                return false;
            }
            String joined = btns.stream()
                    .map(b -> {
                        String t = b.getText();
                        if (t == null || t.isBlank()) {
                            String inner = b.getDomProperty("innerText");
                            return inner != null ? inner : "";
                        }
                        return t;
                    })
                    .collect(Collectors.joining(" "))
                    .toLowerCase();
            return joined.contains("pepinillos") && joined.contains("queso extra");
        });

        driver.close();
        driver.switchTo().window(adminWindow);
    }
}
