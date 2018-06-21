package info.biosfood.synchronization.locks;

import org.junit.Test;

public class GeneralSynchronizationCases extends AbstractTest {

    GeneralSynchronization subject = new GeneralSynchronization();

    @Test
    public void testCase1() throws InterruptedException {
        Thread t1 = create(() -> GeneralSynchronization.synchronizedStaticMethod());
        Thread t2 = create(() -> GeneralSynchronization.synchronizedStaticMethod2());

        t1.start();
        t2.start();

        waitWhenAllThreadsAreDone.await();
    }

    @Test
    public void testCase2() throws InterruptedException {
        Thread t1 = create(() -> subject.synchronizedInstanceMethod());
        Thread t2 = create(() -> GeneralSynchronization.synchronizedStaticMethod());

        t1.start();
        t2.start();

        waitWhenAllThreadsAreDone.await();
    }

    @Test
    public void testCase3() throws InterruptedException {
        Thread t1 = create(() -> subject.synchronizedInstanceMethod());
        Thread t2 = create(() -> subject.synchronizedInstanceMethod2());

        t1.start();
        t2.start();

        waitWhenAllThreadsAreDone.await();
    }

    @Test
    public void testCase4() throws InterruptedException {
        Thread t1 = create(() -> subject.synchronizedBlockOnInstance());
        Thread t2 = create(() -> subject.synchronizedBlockOnClass());

        t1.start();
        t2.start();

        waitWhenAllThreadsAreDone.await();
    }

    @Test
    public void testCase5() throws InterruptedException {
        Thread t1 = create(() -> subject.synchronizedStaticMethod());
        Thread t2 = create(() -> subject.synchronizedBlockOnClass());

        t1.start();
        t2.start();

        waitWhenAllThreadsAreDone.await();
    }

    @Test
    public void testCase6() throws InterruptedException {
        Thread t1 = create(() -> subject.synchronizedStaticMethod());
        Thread t2 = create(() -> subject.synchronizedBlockOnInstance());

        t1.start();
        t2.start();

        waitWhenAllThreadsAreDone.await();
    }

    @Test
    public void testCase7() throws InterruptedException {
        Thread t1 = create(() -> subject.justMethod());
        Thread t2 = create(() -> subject.synchronizedBlockOnInstance());

        t1.start();
        t2.start();

        waitWhenAllThreadsAreDone.await();
    }

    @Test
    public void testCase8() throws InterruptedException {
        Thread t1 = create(() -> subject.synchronizedBlockOnClassField());
        Thread t2 = create(() -> subject.unsynchronizedAccessToClassField());

        t1.start();
        t2.start();

        waitWhenAllThreadsAreDone.await();
    }

}
