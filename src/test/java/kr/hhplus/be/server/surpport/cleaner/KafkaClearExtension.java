package kr.hhplus.be.server.surpport.cleaner;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class KafkaClearExtension implements AfterEachCallback, BeforeEachCallback {

    @Override
    public void afterEach(ExtensionContext context) {
        KafkaCleaner kafkaCleaner = getKafkaCleaner(context);
        kafkaCleaner.clear();
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        KafkaCleaner kafkaCleaner = getKafkaCleaner(context);
        kafkaCleaner.clear();
    }

    private KafkaCleaner getKafkaCleaner(ExtensionContext extensionContext) {
        return SpringExtension.getApplicationContext(extensionContext)
                .getBean(KafkaCleaner.class);
    }
}