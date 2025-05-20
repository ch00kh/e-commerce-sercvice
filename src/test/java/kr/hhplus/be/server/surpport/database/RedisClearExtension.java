package kr.hhplus.be.server.surpport.database;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class RedisClearExtension implements AfterEachCallback, BeforeEachCallback {

    @Override
    public void afterEach(ExtensionContext context) {
        RedisCleaner redisCleaner = getDataCleaner(context);
        redisCleaner.clear();
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        RedisCleaner redisCleaner = getDataCleaner(context);
        redisCleaner.clear();
    }

    private RedisCleaner getDataCleaner(ExtensionContext extensionContext) {
        return SpringExtension.getApplicationContext(extensionContext)
                .getBean(RedisCleaner.class);
    }
}
