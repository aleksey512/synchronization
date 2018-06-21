package info.biosfood.synchronization.locks;

import org.junit.Before;

import java.util.concurrent.CountDownLatch;

abstract public class AbstractTest {

    CountDownLatch waitWhenAllThreadsAreDone;

    Thread create(TestCaller caller) {
        return TestUtils.create(() -> {
            caller.call();
            waitWhenAllThreadsAreDone.countDown();
        });
    }

    @Before
    public void setup() {
        waitWhenAllThreadsAreDone = new CountDownLatch(2);
    }

}
