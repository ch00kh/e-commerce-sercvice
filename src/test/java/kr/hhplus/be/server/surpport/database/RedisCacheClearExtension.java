package kr.hhplus.be.server.surpport.database;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class RedisCacheClearExtension implements AfterEachCallback, BeforeEachCallback {

    @Override
    public void afterEach(ExtensionContext context) {
        RedisCacheCleaner redisCacheCleaner = getDataCleaner(context);
        redisCacheCleaner.clear();
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        RedisCacheCleaner redisCacheCleaner = getDataCleaner(context);
        redisCacheCleaner.clear();
    }

    private RedisCacheCleaner getDataCleaner(ExtensionContext extensionContext) {
        return SpringExtension.getApplicationContext(extensionContext)
                .getBean(RedisCacheCleaner.class);
    }
}
