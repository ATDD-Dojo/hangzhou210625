package com.odde.atddv2;

import com.odde.atddv2.entity.User;
import com.odde.atddv2.repo.UserRepo;
import io.cucumber.java.After;
import io.cucumber.java.zh_cn.假如;
import io.cucumber.java.zh_cn.当;
import io.cucumber.java.zh_cn.那么;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.SneakyThrows;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.openqa.selenium.By.xpath;

@ContextConfiguration(classes = {SpringVueApplication.class}, loader = SpringBootContextLoader.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@CucumberContextConfiguration
public class ApplicationSteps {
    private final WebDriver webDriver = createWebDriver();

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ServerProperties serverProperties;

    @假如("存在用户名为{string}和密码为{string}的用户")
    public void 存在用户名为和密码为的用户(String userName, String password) {
        userRepo.deleteAll();
        userRepo.save(new User().setUserName(userName).setPassword(password));
    }

    @当("以用户名为{string}和密码为{string}登录时")
    public void 以用户名为和密码为登录时(String userName, String password) {
        webDriver.get("http://localhost:" + serverProperties.getPort() + "/");
        waitElement("//*[@id=\"app\"]/div/form/div[2]/div/div/input").sendKeys(userName);
        waitElement("//*[@id=\"app\"]/div/form/div[3]/div/div/input").sendKeys(password);
        waitElement("//*[@id=\"app\"]/div/form/button/span").click();
    }

    @那么("{string}登录成功")
    public void 登录成功(String userName) {
        await().untilAsserted(() -> assertThat(webDriver.findElements(xpath("//*[text()='" + ("Welcome " + userName) + "']"))).isNotEmpty());
    }

    @那么("登录失败的错误信息是{string}")
    public void 登录失败的错误信息是(String message) {
        await().untilAsserted(() -> assertThat(webDriver.findElements(xpath("//*[text()='" + message + "']"))).isNotEmpty());
    }

    public WebDriver createWebDriver() {
        System.setProperty("webdriver.chrome.driver", getChromeDriverBinaryPath());
        return new ChromeDriver();
    }

    @SneakyThrows
    private String getChromeDriverBinaryPath() {
        try (Stream<Path> walkStream = Files.walk(Paths.get(System.getProperty("user.home"), ".gradle", "webdriver", "chromedriver"))) {
            return walkStream
                    .filter(this::isChromeDriverBinary)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("can't find chrome driver binary"))
                    .toAbsolutePath().toString();
        }
    }

    private boolean isChromeDriverBinary(Path p) {
        File file = p.toFile();
        return file.isFile() && (file.getPath().endsWith("chromedriver") || file.getPath().endsWith("chromedriver.exe"));
    }

    private WebElement waitElement(String xpathExpression) {
        return await().until(() -> webDriver.findElement(xpath(xpathExpression)), Objects::nonNull);
    }

    @After
    public void closeBrowser() {
        webDriver.quit();
    }
}
