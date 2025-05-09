package kr.hhplus.be.server.surpport.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
public class RedisCacheCleaner {

    @Autowired
    private CacheManager cacheManager;

    public void clear() {
        cacheManager.getCacheNames().forEach(name -> cacheManager.getCache(name).clear());
    }
}
