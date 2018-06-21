package info.biosfood.synchronization.locks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GeneralSynchronization {

    static Logger LOG = LoggerFactory.getLogger(GeneralSynchronization.class);

    static final long SLEEP_TIME = 2000L;

    Counter counter = new Counter();

    public static synchronized void synchronizedStaticMethod() throws InterruptedException {
        LOG.debug("in");

        Thread.sleep(SLEEP_TIME);

        LOG.debug("out");
    }

    public static synchronized void synchronizedStaticMethod2() throws InterruptedException {
        LOG.debug("in");

        Thread.sleep(SLEEP_TIME);

        LOG.debug("out");
    }

    public synchronized void synchronizedInstanceMethod() throws InterruptedException {
        LOG.debug("in");

        Thread.sleep(SLEEP_TIME);

        LOG.debug("out");
    }

    public synchronized void synchronizedInstanceMethod2() throws InterruptedException {
        LOG.debug("in");

        Thread.sleep(SLEEP_TIME);

        LOG.debug("out");
    }

    public void synchronizedBlockOnInstance() throws InterruptedException {
        LOG.debug("in");

        synchronized (this) {
            LOG.debug("acquired lock");

            Thread.sleep(SLEEP_TIME);

            LOG.debug("released lock");
        }

        LOG.debug("out");
    }

    public void synchronizedBlockOnClass() throws InterruptedException {
        LOG.debug("in");

        synchronized (GeneralSynchronization.class) {
            LOG.debug("acquired lock");

            Thread.sleep(SLEEP_TIME);

            LOG.debug("released lock");
        }

        LOG.debug("out");
    }

    public void justMethod() throws InterruptedException {
        LOG.debug("in");

        Thread.sleep(SLEEP_TIME);

        LOG.debug("out");
    }

    public void synchronizedBlockOnClassField() throws InterruptedException {
        LOG.debug("in");

        synchronized (this.counter) {
            LOG.debug("acquired lock");

            counter.increment();

            LOG.debug("counter: " + counter.count());

            Thread.sleep(SLEEP_TIME);

            LOG.debug("released lock");
        }

        LOG.debug("out");
    }

    public void unsynchronizedAccessToClassField() throws InterruptedException {
        LOG.debug("in");

        Thread.sleep(1000);

        counter.increment();

        LOG.debug("counter: " + counter.count());

        LOG.debug("out");
    }

}
