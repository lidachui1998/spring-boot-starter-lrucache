import com.lidachui.lrucache.util.LRUCache;
import javax.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
/**
 * CacheUseTest
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2024/1/14 23:04
 */
@SpringBootTest
public class CacheUseTest {



        @Resource
        private LRUCache lruCache;

        @Test
        public void testSomeServiceMethod() {
            for (int i = 0; i <= 101; i++) {
                String key = "key" + i;
                String value = "value" + i;
                lruCache.put(key,value);
            }

            // 添加数据到缓存
            // 从缓存中获取数据
            System.out.println("从缓存获取: key1 => " + lruCache.get("key1")); // 应未命中
            System.out.println("从缓存获取: key4 => " + lruCache.get("key4")); // 应命中

            // 显示缓存统计信息
            System.out.println("缓存统计: " + lruCache.getCacheStats());

            // 清空缓存
            lruCache.clear();
            System.out.println("缓存清空后大小: " + lruCache.size());
        }

}
