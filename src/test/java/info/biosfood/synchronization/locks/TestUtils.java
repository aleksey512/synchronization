package info.biosfood.synchronization.locks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestUtils {

    static Logger LOG = LoggerFactory.getLogger(TestUtils.class);

    public static Thread create(TestCaller caller) {
        return new Thread(() -> {
            try {
                caller.call();
            } catch (Throwable e) {
                LOG.error(e.getMessage(), e);
            }
        });
    }

}
