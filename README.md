# Synchronization in Java
A synchronized method provides a strategy for preventing the thread interference and memory consistency errors. 
If an object is accessible by more than one thread, then all read or write operations to that object's fields should 
be done by a synchronized method. This article describes types of synchronization and particularly usage of a `synchronized` 
keyword and provides few examples how synchronization works on simultaneous invocation of different type of a synchronized method. 
For example, is it possible to call simultaneously a synchronized method of an instance of a class and synchronized static method of the class?

## Synchronization types in Java

### Synchronized static method

That method is synchronized on the class object (`Class`) of the class the synchronized method belongs to. 
```java
class SynchronizedStaticMethod {

	private static int instanceCount = 0;

	public static synchronized SynchronizedStaticMethod create() {
		instanceCount += 1;
		return new SynchronizedStaticMethod(); 
	}
	
}
```

### Synchronized instance method
A synchronized instance method is always synchronized on the instance of a class. It means that another thread 
is not able to call another synchronized method of that instance or synchronized block on current instance while first 
thread holds the lock.
```java
class SynchronizedInstanceMethod {

	private int counter = 0;
	
	public synchronized void perform() {
		this.counter += 1;
	}

}
```

### Synchronized block

That type of synchronization doesn't limit access to the whole method, but it limits access to a code block in curly braces. 
The code block is synchronized on a particular instance that is specified in parentheses after a keyword `synchronized`. 
That instance is called monitor. The code in curly braces is synchronized and another thread can't access this block 
before the current thread leaves the synchronized block. For monitor you can use only on a descendant of `Object` 
and you can't use primitive types like `int` or `char`. The synchronized block could be used in a static method or in an instance method. 

```java
class SynchronizedBlockOnInstance {

	private final Object lock = new Object();
	
	private int counter = 0;
	
	public void perform() {
		synchronized(this.lock) {
			this.counter += 1;
		}
	}

}
```
### General synchronization cases
That part of the article contains few examples of simultaneous call of different type of synchronized methods and blocks. 
You will see how threads interact when calling different types of synchronized methods and blocks.

### Two different static synchronized methods of the same class
First, lets try to call two different static synchronized methods of the same class. In this case it's not possible 
simultaneous invocation, a second thread will wait until first thread finishes invocation. It happens because the first 
thread has acquired synchronization lock for the whole class (type) and another threads can't invoke other synchronized 
methods of that class, because it needs to acquire the lock for the whole class.

```java
public class GeneralSynchronizationCases extends AbstractTest {
    ...
    public void testCase1() throws InterruptedException {
        Thread t1 = create(() -> GeneralSynchronization.synchronizedStaticMethod());
        Thread t2 = create(() -> GeneralSynchronization.synchronizedStaticMethod2());
    
        t1.start();
        t2.start();
    
        waitWhenAllThreadsAreDone.await();
    }

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
}
```

```text
15:23:03.730 .synchronizedStaticMethod - [Thread-0] - in
15:23:05.735 .synchronizedStaticMethod - [Thread-0] - out
15:23:05.737 .synchronizedStaticMethod2 - [Thread-1] - in
15:23:07.738 .synchronizedStaticMethod2 - [Thread-1] - out
```

### Static synchronized method of a class and a synchronized method of an instance of the class
Another quite interesting case, when call simultaneously a static synchronized method of a class and a synchronized 
method of an instance of the class. The invocation works simultaneously well, because static synchronized method holds 
synchronization lock only for the class (type) and the synchronized method of the instance of the class locks the instance of the class.

```java
public class GeneralSynchronizationCases extends AbstractTest {
    @Test
    public void testCase2() throws InterruptedException {
        Thread t1 = create(() -> subject.synchronizedInstanceMethod());
        Thread t2 = create(() -> GeneralSynchronization.synchronizedStaticMethod());
    
        t1.start();
        t2.start();
    
        waitWhenAllThreadsAreDone.await();
    }
    
    ... 
    
    public synchronized void synchronizedInstanceMethod() throws InterruptedException {
        LOG.debug("in");
    
        Thread.sleep(SLEEP_TIME);
    
        LOG.debug("out");
    }
    
    public static synchronized void synchronizedStaticMethod() throws InterruptedException {
        LOG.debug("in");
    
        Thread.sleep(SLEEP_TIME);
    
        LOG.debug("out");
    }
}
```

```text
15:23:07.750 .synchronizedInstanceMethod - [Thread-2] - in
15:23:07.752 .synchronizedStaticMethod - [Thread-3] - in
15:23:09.751 .synchronizedInstanceMethod - [Thread-2] - out
15:23:09.753 .synchronizedStaticMethod - [Thread-3] - out
```

### Two different synchronized instance methods of the same instance of a class
Very straightforward case, when two different synchronized methods of the same instance of a class are called simultaneously. 
The invocation can't work simultaneously, because invocation of both thread requires to hold a synchronization lock of 
the instance and it could be only one lock at the moment.

```java
public class GeneralSynchronizationCases extends AbstractTest {
    @Test
    public void testCase3() throws InterruptedException {
        Thread t1 = create(() -> subject.synchronizedInstanceMethod());
        Thread t2 = create(() -> subject.synchronizedInstanceMethod2());
    
        t1.start();
        t2.start();
    
        waitWhenAllThreadsAreDone.await();
    }
    
    ...
    
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
}
```

```text
15:23:09.755 .synchronizedInstanceMethod - [Thread-4] - in 
15:23:11.756 .synchronizedInstanceMethod - [Thread-4] - out 
15:23:11.756 .synchronizedInstanceMethod2 - [Thread-5] - in 
15:23:13.756 .synchronizedInstanceMethod2 - [Thread-5] - out
```

### Method with internal synchronized block on the instance of a class and method with synchronized block on the class (type)
Lets call simultaneously a method with internal synchronized block on the instance of a class and method with 
synchronized block on the current class (type). That case is similar to `Case #2` and both threads can invoke the methods simultaneously.

```java
public class GeneralSynchronizationCases extends AbstractTest {
    @Test
    public void testCase4() throws InterruptedException {
        Thread t1 = create(() -> subject.synchronizedBlockOnInstance());
        Thread t2 = create(() -> subject.synchronizedBlockOnClass());
    
        t1.start();
        t2.start();
    
        waitWhenAllThreadsAreDone.await();
    }
    
    ...
    
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
}
```

```text
15:23:13.760 .synchronizedBlockOnInstance - [Thread-6] - in 
15:23:13.760 .synchronizedBlockOnInstance - [Thread-6] - acquired lock 
15:23:13.762 .synchronizedBlockOnClass - [Thread-7] - in 
15:23:13.762 .synchronizedBlockOnClass - [Thread-7] - acquired lock 
15:23:15.763 .synchronizedBlockOnClass - [Thread-7] - released lock 
15:23:15.762 .synchronizedBlockOnInstance - [Thread-6] - released lock 
15:23:15.763 .synchronizedBlockOnClass - [Thread-7] - out 
15:23:15.763 .synchronizedBlockOnInstance - [Thread-6] - out 
```

### Synchronized static method and a method with a synchronized block on the current class

That is also simple case, when a synchronized static method and a method with synchronized block on the current 
class are called simultaneously. Each of the threads try to acquire lock of current class object and they can't work 
simultaneously. The case is similar to `Case #1`.

```java
public class GeneralSynchronizationCases extends AbstractTest {
    @Test
    public void testCase5() throws InterruptedException {
        Thread t1 = create(() -> subject.synchronizedStaticMethod());
        Thread t2 = create(() -> subject.synchronizedBlockOnClass());
    
        t1.start();
        t2.start();
    
        waitWhenAllThreadsAreDone.await();
    }
    
    ...
    
    public static synchronized void synchronizedStaticMethod() throws InterruptedException {
        LOG.debug("in");
    
        Thread.sleep(SLEEP_TIME);
    
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
}
```

```text
15:23:15.783 .synchronizedStaticMethod - [Thread-8] - in 
15:23:15.783 .synchronizedBlockOnClass - [Thread-9] - in 
15:23:17.784 .synchronizedStaticMethod - [Thread-8] - out 
15:23:17.785 .synchronizedBlockOnClass - [Thread-9] - acquired lock 
15:23:19.786 .synchronizedBlockOnClass - [Thread-9] - released lock 
15:23:19.787 .synchronizedBlockOnClass - [Thread-9] - out 
```

### Method with synchronized block of a current instance of a class and synchronized static method of the class
That case is another variation of `Case #2`. The invocation works simultaneously, because first thread holds lock of 
the instance of the class and second thread holds monitor of the class (type).

```java
public class GeneralSynchronizationCases extends AbstractTest {
    @Test
    public void testCase6() throws InterruptedException {
        Thread t1 = create(() -> subject.synchronizedStaticMethod());
        Thread t2 = create(() -> subject.synchronizedBlockOnInstance());
    
        t1.start();
        t2.start();
    
        waitWhenAllThreadsAreDone.await();
    }
    
    ...
    
    public static synchronized void synchronizedStaticMethod() throws InterruptedException {
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
}
```

```text
15:23:19.796 .synchronizedStaticMethod - [Thread-10] - in
15:23:19.796 .synchronizedBlockOnInstance - [Thread-11] - in
15:23:19.797 .synchronizedBlockOnInstance - [Thread-11] - acquired lock
15:23:21.797 .synchronizedStaticMethod - [Thread-10] - out
15:23:21.798 .synchronizedBlockOnInstance - [Thread-11] - released lock
15:23:21.798 .synchronizedBlockOnInstance - [Thread-11] - out
```

### Synchronized method and unsynchronized method of a current instance of a class
Simultaneous invocation of a synchronized method and unsynchronized method of a current instance of a class. 
Both methods don't contain any internal synchronized blocks. The invocation works simultaneously, because first thread 
holds lock of the instance of the class and second thread doesn't hold any locks.

```java
public class GeneralSynchronizationCases extends AbstractTest {
    @Test
    public void testCase7() throws InterruptedException {
        Thread t1 = create(() -> subject.justMethod());
        Thread t2 = create(() -> subject.synchronizedBlockOnInstance());
    
        t1.start();
        t2.start();
    
        waitWhenAllThreadsAreDone.await();
    }
    
    ...
    
    public void justMethod() throws InterruptedException {
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
}
```

```text
15:23:21.802 .synchronizedBlockOnInstance - [Thread-13] - in 
15:23:21.802 .synchronizedBlockOnInstance - [Thread-13] - acquired lock 
15:23:21.804 .justMethod - [Thread-12] - in 
15:23:23.802 .synchronizedBlockOnInstance - [Thread-13] - released lock 
15:23:23.805 .synchronizedBlockOnInstance - [Thread-13] - out 
15:23:23.805 .justMethod - [Thread-12] - out 
```

### Synchronized method and unsynchronized method access simultaneously the same field
The method `unsynchronizedAccessToClassField()` doesn't synchronize access to a field `Integer counter = new Integer(0);` 
which is used as a monitor in a synchronized block in method `synchronizedBlockOnClassField()`. 
The thread #2 can access `counter` even when monitor is held by thread #1. That is actually one of 
the worst situations when the same field is semi-synchronized.

```java
public class GeneralSynchronizationCases extends AbstractTest {
    @Test
    public void testCase8() throws InterruptedException {
        Thread t1 = create(() -> subject.synchronizedBlockOnClassField());
        Thread t2 = create(() -> subject.unsynchronizedAccessToClassField());
    
        t1.start();
        t2.start();
    
        waitWhenAllThreadsAreDone.await();
    }
    
    ...
    
    public void synchronizedBlockOnClassField() throws InterruptedException {
        LOG.debug("in");
    
        synchronized (this.counter) {
            LOG.debug("acquired lock");
    
            counter++;
    
            LOG.debug("counter: " + counter);
    
            Thread.sleep(SLEEP_TIME);
    
            LOG.debug("released lock");
        }
    
        LOG.debug("out");
    }
    
    public void unsynchronizedAccessToClassField() throws InterruptedException {
        LOG.debug("in");
    
        Thread.sleep(1000);
    
        counter++;
    
        LOG.debug("counter: " + counter);
    
        LOG.debug("out");
    }
}
```

It's obvious that `Thread-1` that calls noLocks method doesn't wait until `Thread-0` wakes up from sleeping.
```text
16:14:51.501 .synchronizedBlockOnClassField - [Thread-0] - in 
16:14:51.502 .unsynchronizedAccessToClassField - [Thread-1] - in 
16:14:51.505 .synchronizedBlockOnClassField - [Thread-0] - acquired lock 
16:14:51.505 .synchronizedBlockOnClassField - [Thread-0] - counter: 1 
16:14:52.505 .unsynchronizedAccessToClassField - [Thread-1] - counter: 2 
16:14:52.506 .unsynchronizedAccessToClassField - [Thread-1] - out 
16:14:53.506 .synchronizedBlockOnClassField - [Thread-0] - released lock 
16:14:53.506 .synchronizedBlockOnClassField - [Thread-0] - out
```
